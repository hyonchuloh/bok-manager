package com.bok.iso.mngr.ctl;

import jakarta.servlet.http.HttpSession;
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

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BokManagerCallbookCtl {

    @Autowired
    private BokManagerCallbookSvc callbookSvc;
    @Autowired
	private BokManagerUserSvc loginSvc;
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());	


    @GetMapping("/manager/callbook")
    public String callbook(
        @RequestParam(value="searchKey", required=false) String searchKey,
        @RequestParam(value="resultMsg", required=false) String resultMsg,
        Model model, HttpSession session) {
        logger.info("-------------------------------------------------------");
        logger.info("--- Callbook Controller");
        logger.info("--- RequestParam(searchKey)=" + searchKey);
        logger.info("--- RequestParam(resultMsg)=" + resultMsg);
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
        model.addAttribute("resultMsg", resultMsg);
        logger.info("-------------------------------------------------------");
        return "callbook/callbook";
    }

    @PostMapping("/manager/callbook-save")
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
        logger.info("-------------------------------------------------------");
        logger.info("--- Callbook Controller");
        logger.info("--- RequestParam(seq)=" + seq);
        logger.info("--- RequestParam(extName)=" + extName);
        logger.info("--- RequestParam(depName)=" + depName);
        logger.info("--- RequestParam(bizName)=" + bizName);
        logger.info("--- RequestParam(name)=" + name);
        logger.info("--- RequestParam(call)=" + call);
        logger.info("--- RequestParam(email)=" + email);
        logger.info("--- RequestParam(ext)=" + ext);
        logger.info("--- RequestParam(searchKey)=" + searchKey);


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
        logger.info("-------------------------------------------------------");
        return "redirect:/manager/callbook?searchKey=" + searchKey + "&resultMsg" + java.net.URLEncoder.encode(resultMsg, java.nio.charset.StandardCharsets.UTF_8);
    }

    @PostMapping("/manager/callbook-delete")
    public String callbookDelete(@RequestParam("seq") String seq, Model model) {
        logger.info("-------------------------------------------------------");
        logger.info("--- Callbook Controller");
        logger.info("--- RequestParam(seq)=" + seq);
        callbookSvc.deleteItem(Integer.parseInt(seq));
        logger.info("-------------------------------------------------------");
        return "redirect:/manager/callbook";
    }

    /**
     * 업로드 팝업을 띄우기 위한 GET 핸들러
     * same path as POST (/manager/callbook/upload) but different HTTP method
     */
    @GetMapping("/manager/callbook/upload")
    public String uploadCallbookPopup(
            Model model, HttpSession session) {

        /* 세션 검증 */
        if (!loginSvc.isAuthentication(session))
            return "redirect:/login";

        return "callbook/callbook-upload";
    }

    /**
     * 엑셀 파일 업로드 받아서 DB에 벌크 저장
     * form field name: file
     */
    @PostMapping("/manager/callbook/upload")
    public String uploadCallbookExcel(
            @RequestParam("file") MultipartFile file,
            Model model, HttpSession session) {

        logger.info("--- Callbook Excel upload start, filename={}", file.getOriginalFilename());

        /* 세션 검증 */
        if (!loginSvc.isAuthentication(session))
            return "redirect:/login";

        String resultMsg;
        try {
            int inserted = callbookSvc.bulkInsertFromExcel(file);
            resultMsg = inserted + " 건 업로드되었습니다.";
        } catch (Exception e) {
            logger.error("Excel upload failed", e);
            resultMsg = "업로드 실패: " + e.getMessage();
        }

        return "redirect:/manager/callbook?resultMsg=" + java.net.URLEncoder.encode(resultMsg, java.nio.charset.StandardCharsets.UTF_8);
    }

}
