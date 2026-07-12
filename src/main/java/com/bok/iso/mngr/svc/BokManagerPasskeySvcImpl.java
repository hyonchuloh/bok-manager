package com.bok.iso.mngr.svc;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerPasskeyDao;
import com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

@Service
public class BokManagerPasskeySvcImpl implements BokManagerPasskeySvc {

    private final BokManagerPasskeyDao passkeyDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Base64.Encoder BASE64URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64URL_DECODER = Base64.getUrlDecoder();
    private static final ObjectMapper CBOR_MAPPER = new ObjectMapper(new CBORFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    BokManagerPasskeySvcImpl(BokManagerPasskeyDao passkeyDao) {
        this.passkeyDao = passkeyDao;
    }

    @Override
    public void initTable() {
        passkeyDao.initTable();
    }

    @Override
    public int insertPasskey(BokManagerPasskeyDto passkey) {
        int result = passkeyDao.insertPasskey(passkey);
        logger.info("--- [insertPasskey] credentialId=[{}] userId=[{}] result=[{}]", passkey.getCredentialId(), passkey.getUserId(), result);
        return result;
    }

    @Override
    public BokManagerPasskeyDto selectPasskeyByCredentialId(String credentialId) {
        return passkeyDao.selectByCredentialId(credentialId);
    }

    @Override
    public List<BokManagerPasskeyDto> selectPasskeysByUserId(String userId) {
        return passkeyDao.selectByUserId(userId);
    }

    @Override
    public int deletePasskeyByCredentialId(String credentialId) {
        int result = passkeyDao.deleteByCredentialId(credentialId);
        logger.info("--- [deletePasskeyByCredentialId] credentialId=[{}] result=[{}]", credentialId, result);
        return result;
    }

    @Override
    public List<BokManagerPasskeyDto> selectAllPasskeys() {
        return passkeyDao.selectAllPasskeys();
    }

    @Override
    public String createPasskeyChallenge(HttpSession session, String userId) throws NoSuchAlgorithmException {
        byte[] challengeBytes = SecureRandom.getInstanceStrong().generateSeed(32);
        String challenge = encodeBase64Url(challengeBytes);
        session.setAttribute("passkeyChallenge", challenge);
        session.setAttribute("passkeyUserId", userId);
        return challenge;
    }

    @Override
    public Map<String, Object> buildPasskeyRegistrationOptions(String challenge, String userId, BokManagerUserDto loginUser) {
        Map<String, Object> user = Map.of(
                "id", encodeBase64Url(userId.getBytes(StandardCharsets.UTF_8)),
                "name", userId,
                "displayName", loginUser.getEmail() != null ? loginUser.getEmail() : userId);

        return Map.of(
                "challenge", challenge,
                "rp", Map.of("name", "bok-manager"),
                "user", user,
                "pubKeyCredParams", List.of(Map.of("type", "public-key", "alg", -7)),
                "authenticatorSelection", Map.of("authenticatorAttachment", "platform", "userVerification", "required"),
                "attestation", "none");
    }

    @Override
    public Map<String, Object> buildPasskeyAssertionOptions(String challenge, String userId) {
        List<BokManagerPasskeyDto> passkeys = selectPasskeysByUserId(userId);
        List<Map<String, Object>> allowCredentials = buildAllowCredentials(passkeys);

        return Map.of(
                "challenge", challenge,
                "allowCredentials", allowCredentials,
                "userVerification", "required");
    }

    private List<Map<String, Object>> buildAllowCredentials(List<BokManagerPasskeyDto> passkeys) {
        if (passkeys == null || passkeys.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> credentialList = new ArrayList<>();
        for (BokManagerPasskeyDto passkey : passkeys) {
            credentialList.add(Map.of(
                    "type", "public-key",
                    "id", passkey.getCredentialId()));
        }
        return credentialList;
    }

    @Override
    public void verifyAndRegisterPasskey(String userId, String challenge, Map<?, ?> response) throws Exception {
        String clientDataJSON = (String) response.get("clientDataJSON");
        String attestationObject = (String) response.get("attestationObject");

        ClientData clientData = parseClientDataJSON(decodeBase64Url(clientDataJSON));
        if (!"webauthn.create".equals(clientData.getType()) || !challenge.equals(clientData.getChallenge())) {
            throw new IllegalArgumentException("Invalid create request");
        }

        byte[] attObj = decodeBase64Url(attestationObject);
        Map<Object, Object> attestationMap = parseCbor(attObj);
        byte[] authData = (byte[]) attestationMap.get("authData");
        AttestedCredentialData credentialData = parseAuthenticatorData(authData);
        String credentialId = encodeBase64Url(credentialData.getCredentialId());
        String publicKey = encodeBase64Url(credentialData.getCredentialPublicKey());

        BokManagerPasskeyDto passkey = new BokManagerPasskeyDto();
        passkey.setUserId(userId);
        passkey.setCredentialId(credentialId);
        passkey.setPublicKey(publicKey);
        passkey.setSignCount(credentialData.getSignCount());
        insertPasskey(passkey);
    }

    @Override
    public String verifyPasskeyAssertion(String challenge, String credentialId, Map<?, ?> response) throws Exception {
        String clientDataJSON = (String) response.get("clientDataJSON");
        String authenticatorData = (String) response.get("authenticatorData");
        String signature = (String) response.get("signature");

        ClientData clientData = parseClientDataJSON(decodeBase64Url(clientDataJSON));
        if (!"webauthn.get".equals(clientData.getType()) || !challenge.equals(clientData.getChallenge())) {
            throw new IllegalArgumentException("Invalid assertion request");
        }

        BokManagerPasskeyDto passkey = selectPasskeyByCredentialId(credentialId);
        if (passkey == null) {
            throw new IllegalArgumentException("Unknown passkey");
        }

        PublicKey publicKey = coseToPublicKey(decodeBase64Url(passkey.getPublicKey()));
        boolean verified = verifyAssertion(publicKey,
                decodeBase64Url(clientDataJSON),
                decodeBase64Url(authenticatorData),
                decodeBase64Url(signature));
        if (!verified) {
            throw new IllegalArgumentException("Assertion verification failed");
        }

        return passkey.getUserId();
    }

    /* ----- WebAuthn 저수준 프로토콜 처리 (인코딩/디코딩, CBOR 파싱, 서명 검증) ----- */

    private static String encodeBase64Url(byte[] value) {
        return BASE64URL_ENCODER.encodeToString(value);
    }

    private static byte[] decodeBase64Url(String value) {
        return BASE64URL_DECODER.decode(value);
    }

    private static ClientData parseClientDataJSON(byte[] clientDataJSON) throws IOException {
        Map<String, Object> clientData = JSON_MAPPER.readValue(clientDataJSON, new TypeReference<Map<String, Object>>() {});
        String type = (String) clientData.get("type");
        String origin = (String) clientData.get("origin");
        String challenge = (String) clientData.get("challenge");
        return new ClientData(type, origin, challenge);
    }

    private static Map<Object, Object> parseCbor(byte[] cborBytes) throws IOException {
        return CBOR_MAPPER.readValue(cborBytes, new TypeReference<Map<Object, Object>>() {});
    }

    private static AttestedCredentialData parseAuthenticatorData(byte[] authDataBytes) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(authDataBytes);
        byte[] rpIdHash = new byte[32];
        buffer.get(rpIdHash);
        byte flags = buffer.get();
        int signCount = buffer.getInt();

        boolean hasAttestedCredentialData = (flags & 0x40) != 0;
        if (!hasAttestedCredentialData) {
            return new AttestedCredentialData(flags, signCount, null, null);
        }

        byte[] aaguid = new byte[16];
        buffer.get(aaguid);
        int credentialIdLength = Short.toUnsignedInt(buffer.getShort());
        byte[] credentialId = new byte[credentialIdLength];
        buffer.get(credentialId);
        byte[] publicKeyCose = new byte[buffer.remaining()];
        buffer.get(publicKeyCose);

        return new AttestedCredentialData(flags, signCount, credentialId, publicKeyCose);
    }

    private static PublicKey coseToPublicKey(byte[] cosePublicKey) throws Exception {
        Map<Object, Object> coseKey = parseCbor(cosePublicKey);
        Number ktyNumber = getNumberValue(coseKey, 1);
        Number algNumber = getNumberValue(coseKey, 3);

        int kty = ktyNumber.intValue();
        int alg = algNumber.intValue();

        if (kty != 2 || alg != -7) {
            throw new IllegalArgumentException("Unsupported COSE key type or algorithm");
        }

        byte[] x = getByteArrayValue(coseKey, -2);
        byte[] y = getByteArrayValue(coseKey, -3);
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
        parameters.init(new ECGenParameterSpec("secp256r1"));
        ECParameterSpec ecSpec = parameters.getParameterSpec(ECParameterSpec.class);
        ECPoint ecPoint = new ECPoint(new BigInteger(1, x), new BigInteger(1, y));
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(ecPoint, ecSpec);
        return KeyFactory.getInstance("EC").generatePublic(pubSpec);
    }

    private static boolean verifyAssertion(PublicKey publicKey, byte[] clientDataJSON, byte[] authenticatorData, byte[] signature) throws Exception {
        byte[] clientDataHash = MessageDigest.getInstance("SHA-256").digest(clientDataJSON);
        byte[] signedData = new byte[authenticatorData.length + clientDataHash.length];
        System.arraycopy(authenticatorData, 0, signedData, 0, authenticatorData.length);
        System.arraycopy(clientDataHash, 0, signedData, authenticatorData.length, clientDataHash.length);

        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(signedData);
        return sig.verify(signature);
    }

    private static boolean isUserVerified(byte flags) {
        return (flags & 0x04) != 0;
    }

    private static Number getNumberValue(Map<Object, Object> map, int key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get((long) key);
        }
        if (value == null) {
            value = map.get(String.valueOf(key));
        }
        if (value instanceof Number) {
            return (Number) value;
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        throw new IllegalArgumentException("Expected numeric COSE value for key=" + key + ", got=" + (value != null ? value.getClass().getName() : "null"));
    }

    private static byte[] getByteArrayValue(Map<Object, Object> map, int key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get((long) key);
        }
        if (value == null) {
            value = map.get(String.valueOf(key));
        }
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (!(item instanceof Number)) {
                    throw new IllegalArgumentException("Expected numeric list for COSE byte array, found " + item.getClass().getName());
                }
                bytes[i] = ((Number) item).byteValue();
            }
            return bytes;
        }
        throw new IllegalArgumentException("Expected byte[] or byte list for COSE value key=" + key + ", got=" + (value != null ? value.getClass().getName() : "null"));
    }

    private static class ClientData {
        private final String type;
        private final String origin;
        private final String challenge;

        public ClientData(String type, String origin, String challenge) {
            this.type = type;
            this.origin = origin;
            this.challenge = challenge;
        }

        public String getType() {
            return type;
        }

        public String getOrigin() {
            return origin;
        }

        public String getChallenge() {
            return challenge;
        }
    }

    private static class AttestedCredentialData {
        private final byte flags;
        private final int signCount;
        private final byte[] credentialId;
        private final byte[] credentialPublicKey;

        public AttestedCredentialData(byte flags, int signCount, byte[] credentialId, byte[] credentialPublicKey) {
            this.flags = flags;
            this.signCount = signCount;
            this.credentialId = credentialId;
            this.credentialPublicKey = credentialPublicKey;
        }

        public byte getFlags() {
            return flags;
        }

        public int getSignCount() {
            return signCount;
        }

        public byte[] getCredentialId() {
            return credentialId;
        }

        public byte[] getCredentialPublicKey() {
            return credentialPublicKey;
        }
    }

}
