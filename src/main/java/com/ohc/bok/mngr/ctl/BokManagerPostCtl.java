package com.ohc.bok.mngr.ctl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ohc.bok.mngr.dao.dto.BokManagerPostDto;
import com.ohc.bok.mngr.svc.BokManagerPostSvc;
import com.ohc.bok.mngr.svc.BokManagerUserSvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 짧은 게시물(타임라인) 컨트롤러. index 화면에 노출되는 공개 게시판이므로
 * 로그인 여부와 무관하게 누구나 조회/작성할 수 있다(비로그인 작성자는 게시물별 비밀번호로 신원을 확인).
 */
@Controller
public class BokManagerPostCtl {

    /* index에서 "더 보기" 클릭 시 AJAX로 추가 로드할 게시물 묶음 크기 */
    private static final int FEED_PAGE_SIZE = 5;

    private final BokManagerPostSvc svc;
    private final BokManagerUserSvc userSvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerPostCtl(BokManagerPostSvc svc, BokManagerUserSvc userSvc) {
        this.svc = svc;
        this.userSvc = userSvc;
    }

    /** index의 "더 보기"가 AJAX로 요청하는 다음 게시물 묶음 */
    @GetMapping("/post-feed-more")
    public String getFeedMore(
                @RequestParam(name="offset") int offset,
                Model model, HttpSession session) {

        List<BokManagerPostDto> feed = svc.getFeed();
        int from = Math.max(0, Math.min(offset, feed.size()));
        int to = Math.min(from + FEED_PAGE_SIZE, feed.size());
        model.addAttribute("items", feed.subList(from, to));
        model.addAttribute("hasMore", to < feed.size());
        model.addAttribute("nextOffset", to);
        addCommonAttributes(model, session);
        return "post/feedMoreFragment";
    }

    /**
     * 인라인 댓글 조각(index 화면에서 게시물로 이동하지 않고 AJAX로 불러오는 댓글 목록).
     * 대댓글 작성/수정/삭제/좋아요도 AJAX 요청 시 이 조각을 다시 렌더링해 반환한다.
     */
    @GetMapping("/post-replies/{seq}")
    public String getReplies(
                @PathVariable(value="seq", required=true) Integer seq,
                Model model, HttpSession session) {
        return renderRepliesFragment(seq, null, model, session);
    }

    /* 루트 게시물(seq)에 달린 대댓글만 골라 조각 뷰를 렌더링한다 */
    private String renderRepliesFragment(int rootSeq, String resultMsg, Model model, HttpSession session) {
        List<BokManagerPostDto> thread = svc.getThread(rootSeq);
        List<BokManagerPostDto> replies = new java.util.ArrayList<>();
        for (BokManagerPostDto item : thread) {
            if (item.getDepth() > 0) {
                replies.add(item);
            }
        }
        model.addAttribute("replies", replies);
        model.addAttribute("rootSeq", rootSeq);
        model.addAttribute("resultMsg", resultMsg);
        addCommonAttributes(model, session);
        return "post/repliesFragment";
    }

    /* 현재 페이지에서 벗어나지 않고 인라인으로 댓글을 펼치거나 작성할 때 보내는 AJAX 요청인지 여부 */
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /* index 화면이 공통으로 사용하는 부가 속성 주입 */
    private void addCommonAttributes(Model model, HttpSession session) {
        boolean authenticated = userSvc.isAuthentication(session);
        String sessionUserId = authenticated ? userSvc.getUserId(session) : null;
        boolean isAdmin = authenticated && userSvc.isAdminUser(sessionUserId);

        Calendar cal = Calendar.getInstance();
        model.addAttribute("yearInt", cal.get(Calendar.YEAR));
        model.addAttribute("monthInt", cal.get(Calendar.MONTH)+1);
        model.addAttribute("dayInt", cal.get(Calendar.DAY_OF_MONTH));
        model.addAttribute("authenticated", authenticated);
        model.addAttribute("sessionUserId", sessionUserId);
        model.addAttribute("isAdmin", isAdmin);
    }

    /* 세션 인증 여부에 따라 작성자 신원을 결정한다. 비로그인이면 사용자가 입력한 닉네임/비밀번호를 사용한다. */
    private String[] resolveIdentity(HttpSession session, String userIdInput, String userPwInput) {
        if (userSvc.isAuthentication(session)) {
            return new String[] { userSvc.getUserId(session), null };
        }
        return new String[] { userIdInput, userPwInput };
    }

    /* 최상위 게시물(트윗) 작성 */
    @PostMapping("/post-save")
    public String createPost(
            @RequestParam("contents") String contents,
            @RequestParam(value="userId", required=false) String userIdInput,
            @RequestParam(value="userPw", required=false) String userPwInput,
            HttpSession session) {

        String[] identity = resolveIdentity(session, userIdInput, userPwInput);
        String resultMsg;
        if (contents == null || contents.trim().isEmpty()) {
            resultMsg = "내용을 입력해주세요.";
        } else if (identity[0] == null || identity[0].trim().isEmpty()
                || (!userSvc.isAuthentication(session) && (identity[1] == null || identity[1].trim().isEmpty()))) {
            resultMsg = "닉네임과 비밀번호를 입력해주세요.";
        } else {
            int result = svc.createPost(identity[0], identity[1], contents);
            resultMsg = (result > 0) ? "게시되었습니다." : "게시에 실패했습니다.";
            logger.info("Create post result: {}, message: {}", result, resultMsg);
        }
        return "redirect:/?resultMsg=" + java.net.URLEncoder.encode(resultMsg, java.nio.charset.StandardCharsets.UTF_8);
    }

    /* 게시물/댓글에 대한 (대)댓글 작성 */
    @PostMapping("/post-reply")
    public String createReply(
            @RequestParam("parentSeq") Integer parentSeq,
            @RequestParam("rootSeq") Integer rootSeq,
            @RequestParam("contents") String contents,
            @RequestParam(value="userId", required=false) String userIdInput,
            @RequestParam(value="userPw", required=false) String userPwInput,
            HttpServletRequest request, Model model, HttpSession session) {

        String[] identity = resolveIdentity(session, userIdInput, userPwInput);
        String resultMsg;
        if (contents == null || contents.trim().isEmpty()) {
            resultMsg = "내용을 입력해주세요.";
        } else if (identity[0] == null || identity[0].trim().isEmpty()
                || (!userSvc.isAuthentication(session) && (identity[1] == null || identity[1].trim().isEmpty()))) {
            resultMsg = "닉네임과 비밀번호를 입력해주세요.";
        } else {
            int result = svc.createReply(identity[0], identity[1], parentSeq, contents);
            resultMsg = (result > 0) ? "댓글이 등록되었습니다." : "댓글 등록에 실패했습니다.";
            logger.info("Create reply result: {}, message: {}", result, resultMsg);
        }
        if (isAjax(request)) {
            return renderRepliesFragment(rootSeq, resultMsg, model, session);
        }
        return "redirect:/?resultMsg=" + java.net.URLEncoder.encode(resultMsg, java.nio.charset.StandardCharsets.UTF_8);
    }

    /* 게시물 내용 수정. 관리자는 비밀번호 없이, 그 외에는 작성 시 등록한 비밀번호로만 가능 */
    @PostMapping("/post-edit")
    public String editItem(
            @RequestParam("seq") Integer seq,
            @RequestParam("contents") String contents,
            @RequestParam(value="userPw", required=false) String userPwInput,
            @RequestParam(value="redirectSeq", required=false) Integer redirectSeq,
            HttpServletRequest request, Model model, HttpSession session) {

        boolean authenticated = userSvc.isAuthentication(session);
        String sessionUserId = authenticated ? userSvc.getUserId(session) : null;
        boolean isAdmin = authenticated && userSvc.isAdminUser(sessionUserId);

        int result = svc.updateItem(seq, contents, sessionUserId, isAdmin, userPwInput);
        String resultMsg;
        if (result < 0) {
            resultMsg = "비밀번호가 일치하지 않아 수정할 수 없습니다.";
        } else {
            resultMsg = (result > 0) ? "수정되었습니다." : "수정에 실패했습니다.";
        }
        logger.info("Edit post result: {}, message: {}", result, resultMsg);

        if (redirectSeq != null && isAjax(request)) {
            return renderRepliesFragment(redirectSeq, resultMsg, model, session);
        }
        String encodedMsg = java.net.URLEncoder.encode(resultMsg, java.nio.charset.StandardCharsets.UTF_8);
        return "redirect:/?resultMsg=" + encodedMsg;
    }

    /* 게시물(및 하위 대댓글 전체) 삭제. 관리자는 비밀번호 없이, 그 외에는 작성 시 등록한 비밀번호로만 가능 */
    @PostMapping("/post-delete")
    public String deleteItem(
            @RequestParam("seq") Integer seq,
            @RequestParam(value="userPw", required=false) String userPwInput,
            @RequestParam(value="redirectSeq", required=false) Integer redirectSeq,
            HttpServletRequest request, Model model, HttpSession session) {

        boolean authenticated = userSvc.isAuthentication(session);
        String sessionUserId = authenticated ? userSvc.getUserId(session) : null;
        boolean isAdmin = authenticated && userSvc.isAdminUser(sessionUserId);

        int result = svc.deleteItem(seq, sessionUserId, isAdmin, userPwInput);
        String resultMsg;
        if (result < 0) {
            resultMsg = "비밀번호가 일치하지 않아 삭제할 수 없습니다.";
        } else {
            resultMsg = (result > 0) ? "삭제되었습니다." : "삭제에 실패했습니다.";
        }
        logger.info("Delete post result: {}, message: {}", result, resultMsg);

        if (redirectSeq != null && isAjax(request)) {
            return renderRepliesFragment(redirectSeq, resultMsg, model, session);
        }
        String encodedMsg = java.net.URLEncoder.encode(resultMsg, java.nio.charset.StandardCharsets.UTF_8);
        return "redirect:/?resultMsg=" + encodedMsg;
    }

    /* 게시물 좋아요. 로그인 여부와 무관하게 누구나 누를 수 있다 */
    @PostMapping("/post-like")
    public String likeItem(
            @RequestParam("seq") Integer seq,
            @RequestParam(value="redirectSeq", required=false) Integer redirectSeq,
            HttpServletRequest request, Model model, HttpSession session) {

        int result = svc.likeItem(seq);
        logger.info("Like post seq={}, result={}", seq, result);

        if (redirectSeq != null && isAjax(request)) {
            return renderRepliesFragment(redirectSeq, null, model, session);
        }
        return "redirect:/";
    }
}
