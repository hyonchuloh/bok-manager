package com.bok.iso.mngr.ctl;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;
import com.bok.iso.mngr.svc.BokManagerCallbookSvc;
import com.bok.iso.mngr.svc.BokManagerUserSvc;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BokManagerCallbookCtl {

    @Autowired
    private BokManagerCallbookSvc callbookSvc;
    @Autowired
	private BokManagerUserSvc loginSvc;
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());	


    @GetMapping("/manager/callbook/{name}")
    public String callbook(
        @RequestParam(value="searchKey", required=false) String searchKey,
        @PathVariable(value="name") String name,
        Model model, HttpSession session) {

        /* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
            return "redirect:/login";
        if ( searchKey != null ) {
            model.addAttribute("list", callbookSvc.selectItems(searchKey));
        } else {
            model.addAttribute("list", callbookSvc.selectItems());
        }
        
        model.addAttribute("userId", loginSvc.getUserId(session));
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("name", name);
        return "callbook/callbook";
    }

    @PostMapping("/manager/callbook")
    public String callbook(
        @RequestParam("seq") int seq,
        @RequestParam("extName") String extName,
        @RequestParam("depName") String depName,
        @RequestParam("bizName") String bizName,
        @RequestParam("name") String name,
        @RequestParam("call") String call,
        @RequestParam("email") String email,
        @RequestParam("ext") String ext,
        @RequestParam(name="searchKey", required=false) String searchKey,
        Model model, HttpSession session) {

        /* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
        return "redirect:/login";
        
        int result = 0;
        String resultMsg = "정상처리되었습니다.";
        if ( seq > 0 ) { // update
            result = callbookSvc.updateItem(new BokManagerCallbookDto(seq, extName, depName, bizName, name, call, email, ext));
        } else { // insert 
            result = callbookSvc.insertItem(new BokManagerCallbookDto(seq, extName, depName, bizName, name, call, email, ext));
        }
        if ( result == 0 ) {
            resultMsg = "실패하였습니다.";
        }

        model.addAttribute("resultMsg", resultMsg);
        if ( searchKey != null ) {
            model.addAttribute("list", callbookSvc.selectItems(searchKey));
        } else {
            model.addAttribute("list", callbookSvc.selectItems());
        }
        model.addAttribute("userId", loginSvc.getUserId(session));
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("name", name);
        return "callbook/callbook";
    }

    @PostMapping("/manager/callbook-delete")
    public String callbookDelete(@RequestParam("seq") String seq, Model model) {
        logger.info("--- RequestParam(seq)=" + seq);
        callbookSvc.deleteItem(Integer.parseInt(seq));
        return "redirect:/manager/callbook";
    }

}
