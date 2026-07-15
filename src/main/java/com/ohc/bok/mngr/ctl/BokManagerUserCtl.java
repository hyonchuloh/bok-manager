package com.ohc.bok.mngr.ctl;

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

import com.ohc.bok.mngr.dao.dto.BokManagerUserDto;
import com.ohc.bok.mngr.svc.BokManagerPasskeySvc;
import com.ohc.bok.mngr.svc.BokManagerUserSvc;


@Controller
public class BokManagerUserCtl {

    public final BokManagerUserSvc userSvc;
    public final BokManagerPasskeySvc passkeySvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerUserCtl(BokManagerUserSvc userSvc, BokManagerPasskeySvc passkeySvc) {
        this.userSvc = userSvc;
        this.passkeySvc = passkeySvc;
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

            return "redirect:/manager/calendar";

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

        String challenge = passkeySvc.createPasskeyChallenge(session, userId);

        if ("register".equals(mode)) {
            return ResponseEntity.ok(passkeySvc.buildPasskeyRegistrationOptions(challenge, userId, loginUser));
        }

        return ResponseEntity.ok(passkeySvc.buildPasskeyAssertionOptions(challenge, userId));
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
            passkeySvc.verifyAndRegisterPasskey(userId, challenge, response);
            return ResponseEntity.ok(java.util.Map.of("success", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
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
            String credentialId = (String) body.get("id");
            String verifiedUserId = passkeySvc.verifyPasskeyAssertion(challenge, credentialId, response);
            userSvc.setSessionForUserId(session, verifiedUserId);
            return ResponseEntity.ok(java.util.Map.of("success", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
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
        model.addAttribute("passkeyList", passkeySvc.selectAllPasskeys());
        model.addAttribute("message", message);
        return "admin/users";
    }

    @PostMapping("/admin/users-passkey-delete")
    public String deleteUserPasskey(@RequestParam("credentialId") String credentialId,
                                    @RequestParam(value = "message", required = false, defaultValue = "") String message,
                                    HttpSession session) {
        if (  !userSvc.isAuthentication(session) ) 
            return "redirect:/login";
        int result = passkeySvc.deletePasskeyByCredentialId(credentialId);
        if (result > 0) {
            message = "패스키가 성공적으로 삭제되었습니다.";
        } else {
            message = "패스키 삭제에 실패했습니다.";
        }
        return "redirect:/admin/users?message=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
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
