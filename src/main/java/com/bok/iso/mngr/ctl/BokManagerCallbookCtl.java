package com.bok.iso.mngr.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;
import com.bok.iso.mngr.svc.BokManagerCallbookSvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class BokManagerCallbookCtl {

    @Autowired
    private BokManagerCallbookSvc callbookSvc;

    @GetMapping("/manager/callbook")
    public String callbook(Model model) {

        model.addAttribute("list", callbookSvc.selectItems());
        return "callbook/callbook";
    }

    @PostMapping("/manager/callbook")
    public String callbook(
        @RequestParam("seq") String seq,
        @RequestParam("extName") String extName,
        @RequestParam("depName") String depName,
        @RequestParam("bizName") String bizName,
        @RequestParam("name") String name,
        @RequestParam("call") String call,
        @RequestParam("email") String email,
        @RequestParam("ext") String ext,
        Model model) {
        
        if ( seq != null && seq.trim().length() > 0 ) { // update
            callbookSvc.updateItem(new BokManagerCallbookDto(seq, extName, depName, bizName, name, call, email, ext));
        } else { // insert 
            callbookSvc.insertItem(new BokManagerCallbookDto(seq, extName, depName, bizName, name, call, email, ext));
        }
        return "callbook/callbook";
    }

}
