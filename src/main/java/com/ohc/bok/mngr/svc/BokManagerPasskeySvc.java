package com.ohc.bok.mngr.svc;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import com.ohc.bok.mngr.dao.dto.BokManagerPasskeyDto;
import com.ohc.bok.mngr.dao.dto.BokManagerUserDto;

public interface BokManagerPasskeySvc {

    /**
     * 핵심 기능: passkey 테이블 초기화(앱 기동 시 1회).
     * 호출 URI: 없음 (BokManagerApplication 기동 시 @PostConstruct에서 호출)
     */
    public void initTable();

    /**
     * 핵심 기능: passkey 신규 등록.
     * 호출 URI: POST /login/passkey/register-verify (verifyAndRegisterPasskey 내부에서 간접 호출)
     */
    public int insertPasskey(BokManagerPasskeyDto passkey);

    /**
     * 핵심 기능: credentialId로 passkey 단건 조회.
     * 호출 URI: POST /login/passkey/assert (verifyPasskeyAssertion 내부에서 간접 호출)
     */
    public BokManagerPasskeyDto selectPasskeyByCredentialId(String credentialId);

    /**
     * 핵심 기능: 특정 사용자에게 등록된 passkey 목록 조회.
     * 호출 URI: POST /login/passkey/options (buildPasskeyAssertionOptions 내부에서 간접 호출)
     */
    public List<BokManagerPasskeyDto> selectPasskeysByUserId(String userId);

    /**
     * 핵심 기능: credentialId로 passkey 삭제.
     * 호출 URI: POST /admin/users-passkey-delete
     */
    public int deletePasskeyByCredentialId(String credentialId);

    /**
     * 핵심 기능: 등록된 전체 passkey 목록 조회.
     * 호출 URI: GET /admin/users
     */
    public List<BokManagerPasskeyDto> selectAllPasskeys();

    /**
     * 핵심 기능: WebAuthn challenge 값을 생성해 세션에 저장.
     * 호출 URI: POST /login/passkey/options
     */
    public String createPasskeyChallenge(HttpSession session, String userId) throws java.security.NoSuchAlgorithmException;

    /**
     * 핵심 기능: passkey 등록(navigator.credentials.create)용 옵션 객체 조립.
     * 호출 URI: POST /login/passkey/options (mode=register)
     */
    public Map<String, Object> buildPasskeyRegistrationOptions(String challenge, String userId, BokManagerUserDto loginUser);

    /**
     * 핵심 기능: passkey 인증(navigator.credentials.get)용 옵션 객체 조립.
     * 호출 URI: POST /login/passkey/options (mode=login)
     */
    public Map<String, Object> buildPasskeyAssertionOptions(String challenge, String userId);

    /**
     * 핵심 기능: 등록 응답(attestation)을 검증하고 passkey를 저장.
     * 호출 URI: POST /login/passkey/register-verify
     */
    public void verifyAndRegisterPasskey(String userId, String challenge, Map<?, ?> response) throws Exception;

    /**
     * 핵심 기능: 인증 응답(assertion) 서명을 검증하고 로그인 대상 사용자 ID를 반환.
     * 호출 URI: POST /login/passkey/assert
     */
    public String verifyPasskeyAssertion(String challenge, String credentialId, Map<?, ?> response) throws Exception;

}
