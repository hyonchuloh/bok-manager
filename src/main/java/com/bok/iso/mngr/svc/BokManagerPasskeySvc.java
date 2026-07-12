package com.bok.iso.mngr.svc;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

public interface BokManagerPasskeySvc {

    public void initTable();
    public int insertPasskey(BokManagerPasskeyDto passkey);
    public BokManagerPasskeyDto selectPasskeyByCredentialId(String credentialId);
    public List<BokManagerPasskeyDto> selectPasskeysByUserId(String userId);
    public int deletePasskeyByCredentialId(String credentialId);
    public List<BokManagerPasskeyDto> selectAllPasskeys();

    /* WebAuthn / Passkey 등록·인증 프로토콜 처리 */
    public String createPasskeyChallenge(HttpSession session, String userId) throws java.security.NoSuchAlgorithmException;
    public Map<String, Object> buildPasskeyRegistrationOptions(String challenge, String userId, BokManagerUserDto loginUser);
    public Map<String, Object> buildPasskeyAssertionOptions(String challenge, String userId);
    public void verifyAndRegisterPasskey(String userId, String challenge, Map<?, ?> response) throws Exception;
    public String verifyPasskeyAssertion(String challenge, String credentialId, Map<?, ?> response) throws Exception;

}
