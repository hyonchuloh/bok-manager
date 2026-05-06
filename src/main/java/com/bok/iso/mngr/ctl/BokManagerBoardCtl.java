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
import com.bok.iso.mngr.svc.BokManagerUserSvc;

import jakarta.servlet.http.HttpSession;

import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/manager")
public class BokManagerBoardCtl {

    @Autowired
	private BokManagerBoardSvc svc;
    @Autowired
	private BokManagerUserSvc loginSvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    /* 좌측에는 게시글 목록을 보여주고, 우측에는 가장 최신 게시글 본문을 보여준다. */
    @GetMapping("/board")
    public String getBoard(
                @RequestParam(name="resultMsg", required=false) String resultMsg,
                @RequestParam(name="seq", required=false) Integer seq,
                Model model, HttpSession session) {

        logger.info("--- Accessing board with resultMsg: {}", resultMsg);
        /* 세션 검증 */
		//if (  !loginSvc.isAuthentication(session) ) 
        //    return "redirect:/login";

        /* (좌측데이터) 게시글 목록을 뿌려준다 */
        List<BokManagerBoardDto> boardList = svc.getListItems();
        logger.info("--- Fetched board list with {} items", boardList.size());
        model.addAttribute("boardList", boardList);
        
        /* (우측데이터) 가장 최신 게시글 본문을 뿌려준다 */
        BokManagerBoardDto latestBoard = null;
        if (seq != null && seq > 0 ) {      // seq가 명시적으로 전달된 경우 해당 게시글을 보여준다.
            latestBoard = svc.selectItem(seq);
            model.addAttribute("latestBoard", latestBoard);
        } else if ( seq != null && seq == 0 ) {            // seq가 0으로 전달된 경우 신규 작성 화면으로 간주하여 빈 데이터를 보여준다.
            model.addAttribute("latestBoard", null);
        } else {                            // seq가 전달되지 않은 경우 가장 최신 게시글을 보여준다.
            latestBoard = svc.getLatestItem();
            model.addAttribute("latestBoard", latestBoard);
        }
        
        /* 현재 날짜 주입 */
        Calendar cal = Calendar.getInstance();
        model.addAttribute("yearInt", cal.get(Calendar.YEAR));
		model.addAttribute("monthInt", cal.get(Calendar.MONTH)+1);
		model.addAttribute("dayInt", cal.get(Calendar.DAY_OF_MONTH));

        /* 결과 메시지 주입 */
        model.addAttribute("resultMsg", resultMsg);
        return "board/board";
    }

    /* 게시글 저장(신규/수정) */
    @PostMapping("/board-save")
    public String updateItem(
            @RequestParam("seq") String seq,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents) {
        logger.info("Updating item: seq={}, title={}, contents={}", seq, title, contents);

        BokManagerBoardDto entity = new BokManagerBoardDto();
        if (seq != null && !seq.isEmpty()) {
            try {
                entity.setSeq(Integer.parseInt(seq));
            } catch (NumberFormatException e) {
                logger.error("Invalid seq value: {}", seq);
                return "redirect:/manager/board?resultMsg=" + java.net.URLEncoder.encode("잘못된 게시글 번호입니다.", java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        entity.setTitle(title);
        entity.setContents(contents);
        int updateResult = svc.updateItem(entity);
        String resultMsg = (updateResult > 0) ? "저장되었습니다." : "저장에 실패했습니다.";
        logger.info("Update result: {}, message: {}", updateResult, resultMsg);
        return "redirect:/manager/board?resultMsg=" + java.net.URLEncoder.encode(resultMsg, java.nio.charset.StandardCharsets.UTF_8);
    }

}
