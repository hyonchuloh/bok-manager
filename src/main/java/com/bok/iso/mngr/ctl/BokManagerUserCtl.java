package com.bok.iso.mngr.ctl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        model.addAttribute("userId", "ID");
        return "login/login";
    }

    @GetMapping("/login")
    public String login(
            HttpSession session, Model model) {
        /* 세션 검증 */
        if (  userSvc.isAuthentication(session) ) 
            return "redirect:/home";
        logger.info("--- [login] ---");
        return "login/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("userId") String userId,
            @RequestParam("userPw") String userPw,
            HttpSession session,
            Model model) {
        
        logger.info("--- [login] input id=[{}]", userId);
        BokManagerUserDto loginUser = userSvc.selectId(userId);

        if ( loginUser != null && loginUser.getUserPw().equals(userPw) ) {
            logger.info("--- [login] login succes (userId : "+ userId +")");
            /* set session info */
            userSvc.setSessionForUserId(session, userId);
            return "redirect:/manager/calendar/" + userId;
        } else {
            logger.info("--- [login] login failure (userId : "+ userId +")");
            model.addAttribute("userId", userId);
            model.addAttribute("message", "["+userId+"] 계정이 없거나 패스워드가 불일치 합니다.");
            return "login/login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        userSvc.removeSessionForUserId(session);
        return "redirect:/login";
    }

    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        /* 세션 검증 */
        if (  !userSvc.isAuthentication(session) ) 
            return "redirect:/login";
        model.addAttribute("userId", userSvc.getUserId(session));
        return "home";
    }

}
