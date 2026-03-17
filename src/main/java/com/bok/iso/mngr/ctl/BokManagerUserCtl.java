package com.bok.iso.mngr.ctl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bok.iso.mngr.dao.dto.BokManagerUserDto;
import com.bok.iso.mngr.svc.BokManagerUserSvc;


@Controller
public class BokManagerUserCtl {

    @Autowired
    public BokManagerUserSvc userSvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/")
    public String index(
            HttpServletRequest request, Model model) {
        logger.info("--- [login] ---");
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
            logger.info("--- [login] login succes (userId : "+ userId +")");
            /* set session info */
            userSvc.setSessionForUserId(session, userId);

            // User-Agent로 모바일/데스크톱 구분
            String userAgent = request.getHeader("User-Agent");
            boolean isMobile = userAgent != null && userAgent.matches(".*(Mobile|Android|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini).*");
            return isMobile ? "redirect:/manager/calendar-week" : "redirect:/manager/calendar";

        } else {
            logger.info("--- [login] login failure (userId : "+ userId +")");
            model.addAttribute("userId", userId);
            model.addAttribute("message", "["+userId+"] 계정이 없거나 패스워드가 불일치 합니다.");
            return "login/login";
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
        //if (  !userSvc.isAuthentication(session) ) 
        //    return "redirect:/login";
        model.addAttribute("list", userSvc.selectAll());
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
