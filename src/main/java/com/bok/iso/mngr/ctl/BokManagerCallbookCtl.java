package com.bok.iso.mngr.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bok.iso.mngr.svc.BokManagerCallbookSvc;

@Controller
public class BokManagerCallbookCtl {

    @Autowired
    private BokManagerCallbookSvc callbookSvc;

    @GetMapping("/callbook")
    public String callbook(Model model) {

        model.addAttribute("list", callbookSvc.selectItems());
        return "callbook/callbook";
    }

}
