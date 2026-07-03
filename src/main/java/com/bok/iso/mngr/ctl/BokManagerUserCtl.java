package com.bok.iso.mngr.ctl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bok.iso.mngr.dao.dto.BokManagerUserDto;
import com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto;
import com.bok.iso.mngr.svc.BokManagerUserSvc;
import com.bok.iso.mngr.util.WebAuthnUtils;


@Controller
public class BokManagerUserCtl {

    public final BokManagerUserSvc userSvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerUserCtl(BokManagerUserSvc userSvc) {
        this.userSvc = userSvc;
    }

    @GetMapping("/")
    public String index(
            HttpSession session, Model model) {
        logger.info("--- [login] ---");
        /* 세션 검증 */
        if (  userSvc.isAuthentication(session) ) 
            return "redirect:/home";
        BokManagerUserDto adminUser = userSvc.selectId("ohhyonchul");
        String message = adminUser != null ? adminUser.getEmail() : "로그인 하세요!";
        model.addAttribute("message", message);
        return "login/login";
    }

    @GetMapping("/login")
    public String login(
            HttpSession session, Model model) {
        /* 세션 검증 */
        if (  userSvc.isAuthentication(session) ) 
            return "redirect:/home";
        logger.info("--- [login] ---");
        BokManagerUserDto adminUser = userSvc.selectId("ohhyonchul");
        String message = adminUser != null ? adminUser.getEmail() : "로그인 하세요!";
        model.addAttribute("message", message);
        return "login/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("userId") String userId,
            @RequestParam("userPw") String userPw,
            HttpSession session,
            Model model,
            HttpServletRequest request) {
        
        logger.info("--- [login] input id=[{}]", userId);
        BokManagerUserDto loginUser = userSvc.selectId(userId);

        if ( loginUser != null && loginUser.getUserPw().equals(userPw) ) {
            logger.info("--- [login] login success (userId : "+ userId +")");
            /* set session info */
            userSvc.setSessionForUserId(session, userId);

            return "redirect:/manager/calendar-week";

        } else {
            logger.info("--- [login] login failure (userId : "+ userId +")");
            model.addAttribute("userId", userId);
            model.addAttribute("message", "["+userId+"] 계정이 없거나 패스워드가 불일치 합니다.<br/>(패스워드 초기화 조건 score=10,000점 이상)");
            return "login/login";
        }
    }

    @PostMapping("/login/passkey/options")
    public ResponseEntity<java.util.Map<String, Object>> passkeyOptions(
            @RequestParam("userId") String userId,
            @RequestParam(value = "mode", defaultValue = "login") String mode,
            HttpSession session) throws java.security.NoSuchAlgorithmException {

        BokManagerUserDto loginUser = userSvc.selectId(userId);
        if (loginUser == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Unknown userId"));
        }

        String challenge = createPasskeyChallenge(session, userId);

        if ("register".equals(mode)) {
            return ResponseEntity.ok(createRegistrationOptions(challenge, userId, loginUser));
        }

        return ResponseEntity.ok(createAssertionOptions(challenge, userId));
    }

    private String createPasskeyChallenge(HttpSession session, String userId) throws java.security.NoSuchAlgorithmException {
        byte[] challengeBytes = java.security.SecureRandom.getInstanceStrong().generateSeed(32);
        String challenge = WebAuthnUtils.encodeBase64Url(challengeBytes);
        session.setAttribute("passkeyChallenge", challenge);
        session.setAttribute("passkeyUserId", userId);
        return challenge;
    }

    private java.util.Map<String, Object> createRegistrationOptions(String challenge, String userId, BokManagerUserDto loginUser) {
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

    private java.util.Map<String, Object> createAssertionOptions(String challenge, String userId) {
        java.util.List<BokManagerPasskeyDto> passkeys = userSvc.selectPasskeysByUserId(userId);
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

    @PostMapping("/login/passkey/register-verify")
    public ResponseEntity<java.util.Map<String, Object>> registerPasskey(@RequestBody java.util.Map<String, Object> body,
            HttpSession session) {
        String challenge = (String) session.getAttribute("passkeyChallenge");
        String userId = (String) session.getAttribute("passkeyUserId");
        if (challenge == null || userId == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Missing passkey session state"));
        }

        try {
            java.util.Map<?, ?> response = (java.util.Map<?, ?>) body.get("response");
            String clientDataJSON = (String) response.get("clientDataJSON");
            String attestationObject = (String) response.get("attestationObject");

            WebAuthnUtils.ClientData clientData = WebAuthnUtils.parseClientDataJSON(WebAuthnUtils.decodeBase64Url(clientDataJSON));
            if (!"webauthn.create".equals(clientData.getType()) || !challenge.equals(clientData.getChallenge())) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid create request"));
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
            userSvc.insertPasskey(passkey);

            return ResponseEntity.ok(java.util.Map.of("success", true));
        } catch (Exception e) {
            logger.error("--- [registerPasskey] error", e);
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Passkey registration failed"));
        }
    }

    @PostMapping("/login/passkey/assert")
    public ResponseEntity<java.util.Map<String, Object>> assertPasskey(@RequestBody java.util.Map<String, Object> body,
            HttpSession session) {
        String challenge = (String) session.getAttribute("passkeyChallenge");
        if (challenge == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Missing passkey session state"));
        }

        try {
            java.util.Map<?, ?> response = (java.util.Map<?, ?>) body.get("response");
            String clientDataJSON = (String) response.get("clientDataJSON");
            String authenticatorData = (String) response.get("authenticatorData");
            String signature = (String) response.get("signature");
            String credentialId = (String) body.get("id");

            WebAuthnUtils.ClientData clientData = WebAuthnUtils.parseClientDataJSON(WebAuthnUtils.decodeBase64Url(clientDataJSON));
            if (!"webauthn.get".equals(clientData.getType()) || !challenge.equals(clientData.getChallenge())) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid assertion request"));
            }

            BokManagerPasskeyDto passkey = userSvc.selectPasskeyByCredentialId(credentialId);
            if (passkey == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Unknown passkey"));
            }

            java.security.PublicKey publicKey = WebAuthnUtils.coseToPublicKey(WebAuthnUtils.decodeBase64Url(passkey.getPublicKey()));
            boolean verified = WebAuthnUtils.verifyAssertion(publicKey,
                    WebAuthnUtils.decodeBase64Url(clientDataJSON),
                    WebAuthnUtils.decodeBase64Url(authenticatorData),
                    WebAuthnUtils.decodeBase64Url(signature));
            if (!verified) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Assertion verification failed"));
            }

            userSvc.setSessionForUserId(session, passkey.getUserId());
            return ResponseEntity.ok(java.util.Map.of("success", true));
        } catch (Exception e) {
            logger.error("--- [assertPasskey] error", e);
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Passkey login failed"));
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        userSvc.removeSessionForUserId(session);
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        /* 세션 검증 */
        if (  !userSvc.isAuthentication(session) ) 
            return "redirect:/login";
        model.addAttribute("userId", userSvc.getUserId(session));
        return "home";
    }

    /* 관리자 사용자 목록 조회 */
    @GetMapping("/admin/users")
    public String getUserList(@RequestParam(value = "message", required = false, defaultValue = "사용자 관리") String message, 
                        HttpSession session, Model model) {
        /* 세션 검증 */
        if (  !userSvc.isAuthentication(session) ) 
            return "redirect:/login";
        model.addAttribute("list", userSvc.selectAll());
        model.addAttribute("passkeyList", userSvc.selectAllPasskeys());
        model.addAttribute("message", message);
        return "admin/users";
    }

    @PostMapping("/admin/users-edit")
    public String editUser(@RequestParam("userId") String userId,
                           @RequestParam("userPw") String userPw,
                           @RequestParam("email") String email,
                           Model model) {
        int result = userSvc.updateId(userId, userPw, email);
         String message = "사용자 정보가 성공적으로 수정되었습니다.";
        if (result <= 0) {
            message = "사용자 정보 수정에 실패했습니다.";
        }
        return "redirect:/admin/users?message=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
    }

    @PostMapping("/admin/users-insert")
    public String insertUser(@RequestParam("userId") String userId,
                            @RequestParam("userPw") String userPw,
                            @RequestParam("email") String email,
                            Model model) {
        int result = userSvc.insertId(userId, userPw, email);
        String message = "사용자가 성공적으로 등록되었습니다.";
        if (result <= 0) {
            message = "사용자 등록에 실패했습니다.";
        }
        return "redirect:/admin/users?message=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
    }

    @PostMapping("/admin/users-delete")
    public String deleteUser(@RequestParam("userId") String userId, Model model) {
        int result = userSvc.deleteId(userId);
        String message = "사용자가 성공적으로 삭제되었습니다.";
        if (result <= 0) {
            message = "사용자 삭제에 실패했습니다.";
        }
        return "redirect:/admin/users?message=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
    }

}
