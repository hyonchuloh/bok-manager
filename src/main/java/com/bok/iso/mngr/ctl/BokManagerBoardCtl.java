package com.bok.iso.mngr.ctl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bok.iso.mngr.svc.BokManagerBoardSvc;
import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/manager")
public class BokManagerBoardCtl {

    @Autowired
	private BokManagerBoardSvc svc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

@GetMapping("/board")
    public String getBoard(
                @RequestParam(name="resultMsg", required=false) String resultMsg,
                Model model) {
        
        BokManagerBoardDto board1 = svc.selectItem(1);
        BokManagerBoardDto board2 = svc.selectItem(2);

        // Handle null cases
        String board1Contents = (board1 != null && board1.getContents() != null) ? board1.getContents() : "";
        String board2Contents = (board2 != null && board2.getContents() != null) ? board2.getContents() : "";

        logger.info("Fetched board1 size: {}", board1Contents.length());
        logger.info("Fetched board2 size: {}", board2Contents.length());

        model.addAttribute("board1", board1Contents);
        model.addAttribute("board2", board2Contents);
        model.addAttribute("resultMsg", resultMsg);
        /* 현재 날짜 주입 */
        Calendar cal = Calendar.getInstance();
        model.addAttribute("yearInt", cal.get(Calendar.YEAR));
		model.addAttribute("monthInt", cal.get(Calendar.MONTH)+1);
		model.addAttribute("dayInt", cal.get(Calendar.DAY_OF_MONTH));
        return "board/board";
    }

    @PostMapping("/board-save")
    public String updateItem(
            @RequestParam("categoryIndex") int categoryIndex,
            @RequestParam("contents") String contents) {
        logger.info("Updating item: categoryIndex={}, contents={}", categoryIndex, contents);

        BokManagerBoardDto entity = new BokManagerBoardDto();
        entity.setCategoryIndex(categoryIndex);
        entity.setContents(contents);

        int updateResult = svc.updateItem(entity);
        if (updateResult == 0) {
            svc.insertItem(entity);
            logger.info("Inserted new item as it did not exist: {}", entity);
            return "redirect:/manager/board?resultMsg=" + java.net.URLEncoder.encode("저장되었습니다.", java.nio.charset.StandardCharsets.UTF_8);;
        } else {
            logger.info("Updated existing item: {}", entity);
            return "redirect:/manager/board?resultMsg=" + java.net.URLEncoder.encode("저장되지 않았습니다. 다시 시도해주세요.", java.nio.charset.StandardCharsets.UTF_8);
        }
    }

}
