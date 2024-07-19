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

import com.bok.iso.mngr.svc.BokManagerLoginSvc;

@Controller
public class BokManagerLoginCtl {

    @Autowired
    public BokManagerLoginSvc svc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/")
    public String index(
            HttpServletRequest request, Model model) {
        logger.info("--- [login] ---");
        return "login/login";
    }

    @GetMapping("/login")
    public String login(
            HttpSession session, Model model) {
        /* 세션 검증 */
        if (  svc.isAuthentication(session) ) 
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

                if ( "ohhyonchul".equals(userId) && !"".equals(userPw) ) {
                    logger.info("--- [login] login succes (userId : "+ userId +")");
                    /* set session info */
                    svc.setSessionForUserId(session, userId);
                    return "home";
                } else {
                    logger.info("--- [login] login failure (userId : "+ userId +")");
                    model.addAttribute("userId", userId);
                    model.addAttribute("message", "Invalid Login. Please retry");
                    return "login/login";
                }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        svc.removeSessionForUserId(session);
        return "redirect:/login";
    }

    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {

        /* 세션 검증 */
        if (  !svc.isAuthentication(session) ) 
            return "redirect:/login";
        model.addAttribute("userId", svc.getUserId(session));

        return "home";
    }

}
