package com.bok.iso.mngr.svc;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerPasskeyDao;
import com.bok.iso.mngr.dao.BokManagerUserDao;
import com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;
import com.bok.iso.mngr.util.WebAuthnUtils;

@Service
public class BokManagerUserSvcImpl implements BokManagerUserSvc {

    private final BokManagerUserDao dao;
    private final BokManagerPasskeyDao passkeyDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerUserSvcImpl(BokManagerUserDao dao, BokManagerPasskeyDao passkeyDao) {
        this.dao = dao;
        this.passkeyDao = passkeyDao;
    }
    
    /* 세션에 userId가 존재하는지 검증한다.
     * 로컬 환경에서는 userId가 없으면 "ohhyonchul"로 세션을 설정하여 인증된 상태로 간주한다.
     * 이는 개발 편의를 위한 것으로, 실제 운영 환경에서는 반드시 인증 절차를 거쳐야 한다.
     */
    @Override
    public boolean isAuthentication(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if ( userId != null && userId.trim().length() > 0 ) {
            return true;
        }
        /* 로컬 환경 검증 
        boolean isLocal = Arrays.stream(env.getActiveProfiles()).anyMatch("local"::equals);
        if ( isLocal ) {
            session.setAttribute("userId", "ohhyonchul");
            return true;
        }*/
        return false;
    }

    @Override
    public void removeSessionForUserId(HttpSession session) {
        session.removeAttribute("userId");
    }

    @Override
    public void setSessionForUserId(HttpSession session, String userId) {
        session.setAttribute("userId", userId);
    }

    @Override
    public String getUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }

    @Override
    public int deleteId(String seq) {
        int result = dao.deleteId(seq);
        logger.info("--- [deleteId] result=[{}]", result);
        return result;
    }

    @Override
    public int insertId(String userId, String userPw, String userEmail) {
        int result = dao.insertId(userId, userPw, userEmail);
        logger.info("--- [insertId] result=[{}]", result);  
        return result;
    }

    @Override
    public BokManagerUserDto selectId(String userId) {
        BokManagerUserDto result = dao.selectId(userId);
        logger.info("--- [selectId] result=[{}]", result); 
        return result;
    }

    @Override
    public int updateId(String userId, String userPw, String userEmail) {
        int result = dao.updateId(userId, userPw, userEmail);
        logger.info("--- [updateId] result=[{}]", result);
        return result;
    }

    @Override
    public void initTable() {
        dao.initTable();
    }

    @Override
    public void initPasskeyTable() {
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
    public java.util.List<BokManagerPasskeyDto> selectPasskeysByUserId(String userId) {
        return passkeyDao.selectByUserId(userId);
    }

    @Override
    public int deletePasskeyByCredentialId(String credentialId) {
        int result = passkeyDao.deleteByCredentialId(credentialId);
        logger.info("--- [deletePasskeyByCredentialId] credentialId=[{}] result=[{}]", credentialId, result);
        return result;
    }

    @Override
    public java.util.List<BokManagerPasskeyDto> selectAllPasskeys() {
        return passkeyDao.selectAllPasskeys();
    }

    @Override
    public java.util.List<BokManagerUserDto> selectAll() {
        java.util.List<BokManagerUserDto> list = dao.selectAll();
        logger.info("--- [selectAll] user count=[{}]", list.size());
        return list;
    }

    @Override
    public List<String> getFontListAll() {    
        String retValue = null;
        try {
            retValue = dao.selectBokConfigValue("bok.fonts");
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok.fonts", retValue);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok.fonts", e);
        }
        if ( retValue != null ) {
            return Arrays.asList(retValue.split(";"));
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public int setCurrentFont(String category, String fontName) {
        String retValue = null;
        try {
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentFont", retValue);
            return dao.updateBokConfigValue("bok." + category + ".currentFont", fontName);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentFont", e);
        }
        return 0;
    }

    @Override
    public String getCurrentFont(String category) {
        String retValue = null;
        try {
            retValue = dao.selectBokConfigValue("bok." + category + ".currentFont");
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentFont", retValue);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentFont", e);
        }
        return retValue;
    }

    @Override
    public int setCurrentSize(String category, String fontSize) {
        String retValue = null;
        try {
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentSize", retValue);
            return dao.updateBokConfigValue("bok." + category + ".currentSize", fontSize);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentSize", e);
        }
        return 0;
    }

    @Override
    public String getCurrentSize(String category) {
        String retValue = null;
        try {
            retValue = dao.selectBokConfigValue("bok." + category + ".currentSize");
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentSize", retValue);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentSize", e);
        }
        return retValue;
    }

    @Override
    public int setCurrentLineHeight(String category, String lineHeight) {
        String retValue = null;
        try {
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentLineHeight", retValue);
            return dao.updateBokConfigValue("bok." + category + ".currentLineHeight", lineHeight);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentLineHeight", e);
        }
        return 0;
    }

    @Override
    public String getCurrentLineHeight(String category) {
        String retValue = null;
        try {
            retValue = dao.selectBokConfigValue("bok." + category + ".currentLineHeight");
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentLineHeight", retValue);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentLineHeight", e);
        }
        return retValue;
    }

    @Override
    public int setCurrentLetterSpacing(String category, String letterSpacing) {
        String retValue = null;
        try {
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentLetterSpacing", retValue);
            return dao.updateBokConfigValue("bok." + category + ".currentLetterSpacing", letterSpacing);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentLetterSpacing", e);
        }
        return 0;
    }

    @Override
    public String getCurrentLetterSpacing(String category) {
        String retValue = null;
        try {
            retValue = dao.selectBokConfigValue("bok." + category + ".currentLetterSpacing");
            logger.info("--- [selectBokConfigValue] key=[{}], value=[{}]", "bok." + category + ".currentLetterSpacing", retValue);
        } catch (Exception e) {
            logger.error("--- [selectBokConfigValue] Error occurred while fetching config value for key=[{}]", "bok." + category + ".currentLetterSpacing", e);
        }
        return retValue;
    }

    @Override
    public String createPasskeyChallenge(HttpSession session, String userId) throws java.security.NoSuchAlgorithmException {
        byte[] challengeBytes = java.security.SecureRandom.getInstanceStrong().generateSeed(32);
        String challenge = WebAuthnUtils.encodeBase64Url(challengeBytes);
        session.setAttribute("passkeyChallenge", challenge);
        session.setAttribute("passkeyUserId", userId);
        return challenge;
    }

    @Override
    public java.util.Map<String, Object> buildPasskeyRegistrationOptions(String challenge, String userId, BokManagerUserDto loginUser) {
        java.util.Map<String, Object> user = java.util.Map.of(
                "id", WebAuthnUtils.encodeBase64Url(userId.getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                "name", userId,
                "displayName", loginUser.getEmail() != null ? loginUser.getEmail() : userId);

        return java.util.Map.of(
                "challenge", challenge,
                "rp", java.util.Map.of("name", "bok-manager"),
                "user", user,
                "pubKeyCredParams", java.util.List.of(java.util.Map.of("type", "public-key", "alg", -7)),
                "authenticatorSelection", java.util.Map.of("authenticatorAttachment", "platform", "userVerification", "required"),
                "attestation", "none");
    }

    @Override
    public java.util.Map<String, Object> buildPasskeyAssertionOptions(String challenge, String userId) {
        java.util.List<BokManagerPasskeyDto> passkeys = selectPasskeysByUserId(userId);
        java.util.List<java.util.Map<String, Object>> allowCredentials = buildAllowCredentials(passkeys);

        return java.util.Map.of(
                "challenge", challenge,
                "allowCredentials", allowCredentials,
                "userVerification", "required");
    }

    private java.util.List<java.util.Map<String, Object>> buildAllowCredentials(java.util.List<BokManagerPasskeyDto> passkeys) {
        if (passkeys == null || passkeys.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<java.util.Map<String, Object>> credentialList = new java.util.ArrayList<>();
        for (BokManagerPasskeyDto passkey : passkeys) {
            credentialList.add(java.util.Map.of(
                    "type", "public-key",
                    "id", passkey.getCredentialId()));
        }
        return credentialList;
    }

    @Override
    public void verifyAndRegisterPasskey(String userId, String challenge, java.util.Map<?, ?> response) throws Exception {
        String clientDataJSON = (String) response.get("clientDataJSON");
        String attestationObject = (String) response.get("attestationObject");

        WebAuthnUtils.ClientData clientData = WebAuthnUtils.parseClientDataJSON(WebAuthnUtils.decodeBase64Url(clientDataJSON));
        if (!"webauthn.create".equals(clientData.getType()) || !challenge.equals(clientData.getChallenge())) {
            throw new IllegalArgumentException("Invalid create request");
        }

        byte[] attObj = WebAuthnUtils.decodeBase64Url(attestationObject);
        java.util.Map<Object, Object> attestationMap = WebAuthnUtils.parseCbor(attObj);
        byte[] authData = (byte[]) attestationMap.get("authData");
        WebAuthnUtils.AttestedCredentialData credentialData = WebAuthnUtils.parseAuthenticatorData(authData);
        String credentialId = WebAuthnUtils.encodeBase64Url(credentialData.getCredentialId());
        String publicKey = WebAuthnUtils.encodeBase64Url(credentialData.getCredentialPublicKey());

        BokManagerPasskeyDto passkey = new BokManagerPasskeyDto();
        passkey.setUserId(userId);
        passkey.setCredentialId(credentialId);
        passkey.setPublicKey(publicKey);
        passkey.setSignCount(credentialData.getSignCount());
        insertPasskey(passkey);
    }

    @Override
    public String verifyPasskeyAssertion(String challenge, String credentialId, java.util.Map<?, ?> response) throws Exception {
        String clientDataJSON = (String) response.get("clientDataJSON");
        String authenticatorData = (String) response.get("authenticatorData");
        String signature = (String) response.get("signature");

        WebAuthnUtils.ClientData clientData = WebAuthnUtils.parseClientDataJSON(WebAuthnUtils.decodeBase64Url(clientDataJSON));
        if (!"webauthn.get".equals(clientData.getType()) || !challenge.equals(clientData.getChallenge())) {
            throw new IllegalArgumentException("Invalid assertion request");
        }

        BokManagerPasskeyDto passkey = selectPasskeyByCredentialId(credentialId);
        if (passkey == null) {
            throw new IllegalArgumentException("Unknown passkey");
        }

        java.security.PublicKey publicKey = WebAuthnUtils.coseToPublicKey(WebAuthnUtils.decodeBase64Url(passkey.getPublicKey()));
        boolean verified = WebAuthnUtils.verifyAssertion(publicKey,
                WebAuthnUtils.decodeBase64Url(clientDataJSON),
                WebAuthnUtils.decodeBase64Url(authenticatorData),
                WebAuthnUtils.decodeBase64Url(signature));
        if (!verified) {
            throw new IllegalArgumentException("Assertion verification failed");
        }

        return passkey.getUserId();
    }

}
