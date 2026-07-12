package com.bok.iso.mngr.svc;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

public interface BokManagerUserSvc {

    public boolean isAuthentication(HttpSession session);

    public void setSessionForUserId(HttpSession session, String userId);
    public void removeSessionForUserId(HttpSession session);
    public String getUserId(HttpSession session);
    public java.util.List<BokManagerUserDto> selectAll();

    public void initTable();
    public BokManagerUserDto selectId(String userId);
    public int insertId(String userId, String userPw, String userEmail);
    public int deleteId(String userId);
    public int updateId(String userId, String userPw, String userEmail);

    public void initPasskeyTable();
    public int insertPasskey(com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto passkey);
    public BokManagerPasskeyDto selectPasskeyByCredentialId(String credentialId);
    public java.util.List<BokManagerPasskeyDto> selectPasskeysByUserId(String userId);
    public int deletePasskeyByCredentialId(String credentialId);
    public java.util.List<BokManagerPasskeyDto> selectAllPasskeys();

    /* WebAuthn / Passkey 등록·인증 프로토콜 처리 */
    public String createPasskeyChallenge(HttpSession session, String userId) throws java.security.NoSuchAlgorithmException;
    public java.util.Map<String, Object> buildPasskeyRegistrationOptions(String challenge, String userId, BokManagerUserDto loginUser);
    public java.util.Map<String, Object> buildPasskeyAssertionOptions(String challenge, String userId);
    public void verifyAndRegisterPasskey(String userId, String challenge, java.util.Map<?, ?> response) throws Exception;
    public String verifyPasskeyAssertion(String challenge, String credentialId, java.util.Map<?, ?> response) throws Exception;

    /* Font Management */
    public List<String> getFontListAll();
    public int setCurrentFont(String category, String fontName);
    public String getCurrentFont(String category);
    public int setCurrentSize(String category, String fontSize);
    public String getCurrentSize(String category);
    public int setCurrentLineHeight(String category, String lineHeight);
    public String getCurrentLineHeight(String category);
    public int setCurrentLetterSpacing(String category, String letterSpacing);
    public String getCurrentLetterSpacing(String category);


}
