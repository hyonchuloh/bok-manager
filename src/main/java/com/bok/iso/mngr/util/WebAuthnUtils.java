package com.bok.iso.mngr.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.ECParameterSpec;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

public class WebAuthnUtils {

    private static final Base64.Encoder BASE64URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64URL_DECODER = Base64.getUrlDecoder();
    private static final ObjectMapper CBOR_MAPPER = new ObjectMapper(new CBORFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static String encodeBase64Url(byte[] value) {
        return BASE64URL_ENCODER.encodeToString(value);
    }

    public static byte[] decodeBase64Url(String value) {
        return BASE64URL_DECODER.decode(value);
    }

    public static ClientData parseClientDataJSON(byte[] clientDataJSON) throws IOException {
        Map<String, Object> clientData = JSON_MAPPER.readValue(clientDataJSON, new TypeReference<Map<String, Object>>() {});
        String type = (String) clientData.get("type");
        String origin = (String) clientData.get("origin");
        String challenge = (String) clientData.get("challenge");
        return new ClientData(type, origin, challenge);
    }

    public static Map<Object, Object> parseCbor(byte[] cborBytes) throws IOException {
        return CBOR_MAPPER.readValue(cborBytes, new TypeReference<Map<Object, Object>>() {});
    }

    public static AttestedCredentialData parseAuthenticatorData(byte[] authDataBytes) throws IOException {
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

    public static PublicKey coseToPublicKey(byte[] cosePublicKey) throws Exception {
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

    public static boolean verifyAssertion(PublicKey publicKey, byte[] clientDataJSON, byte[] authenticatorData, byte[] signature) throws Exception {
        byte[] clientDataHash = MessageDigest.getInstance("SHA-256").digest(clientDataJSON);
        byte[] signedData = new byte[authenticatorData.length + clientDataHash.length];
        System.arraycopy(authenticatorData, 0, signedData, 0, authenticatorData.length);
        System.arraycopy(clientDataHash, 0, signedData, authenticatorData.length, clientDataHash.length);

        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(signedData);
        return sig.verify(signature);
    }

    public static boolean isUserVerified(byte flags) {
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
        if (value instanceof java.util.List<?>) {
            java.util.List<?> list = (java.util.List<?>) value;
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

    public static class ClientData {
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

    public static class AttestedCredentialData {
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
