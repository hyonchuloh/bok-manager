package com.bok.iso.mngr.ctl;

import java.util.List;

import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bok.iso.mngr.svc.BokManagerBoardSvc;
import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/manager/board")
public class BokManagerBoardCtl {

    @Autowired
	private BokManagerBoardSvc svc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    @GetMapping("/list/{name}")
    public String getList(
            @PathVariable(value="name") String name,
            Model model) {
        List<BokManagerBoardDto> result = svc.selectList();
        logger.info("Fetched list of items: {}", result);
        logger.info("PathVariable(name): {}", name);
        model.addAttribute("boardList", result);
        model.addAttribute("name", name);
        return "board/list";
    }

    @GetMapping("/select/{seq}")
    public String getItem(@RequestParam int seq) {
        logger.info("Fetching item with seq: {}", seq);
        return svc.selectItem(seq).toString();
    }

    @PostMapping("/insert")
    public String putItem(@RequestBody BokManagerBoardDto entity) {
        logger.info("Inserting item: {}", entity);
        svc.insertItem(entity);
        return "Item inserted successfully";
    }

    @PostMapping("/update")
    public String updateItem(@RequestBody BokManagerBoardDto entity) { 
        logger.info("Updating item: {}", entity);
        svc.updateItem(entity);
        return "Item updated successfully";
    }

    @GetMapping("/delete/{seq}")
    public String deleteItem(@RequestParam int seq) { 
        logger.info("Deleting item with seq: {}", seq);
        svc.deleteItem(seq);
        return "Item deleted successfully";
    }

}
