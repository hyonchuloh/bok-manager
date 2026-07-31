package com.ohc.bok.mngr.svc;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerPostDto;

public interface BokManagerPostSvc {

    /**
     * 핵심 기능: 게시물 테이블 초기화(앱 기동 시 1회).
     * 호출 URI: 없음 (BokManagerApplication 기동 시 @PostConstruct에서 호출)
     */
    void initTable();

    /**
     * 핵심 기능: 최상위 짧은 게시물(트윗) 신규 작성. 로그인 세션이 있으면 rawPassword는 null로 전달한다.
     * 호출 URI: POST /post-save
     */
    int createPost(String userId, String rawPassword, String contents);

    /**
     * 핵심 기능: 특정 게시물/댓글에 대한 (대)댓글 작성. 로그인 세션이 있으면 rawPassword는 null로 전달한다.
     * 호출 URI: POST /post-reply
     */
    int createReply(String userId, String rawPassword, int parentSeq, String contents);

    /**
     * 핵심 기능: 타임라인(최상위 게시물 목록, 댓글 수 포함) 조회.
     * 호출 URI: GET /, GET /post-feed-more
     */
    List<BokManagerPostDto> getFeed();

    /**
     * 핵심 기능: 루트 게시물과 그에 달린 모든 대댓글을 depth를 부여한 순서로 조회.
     * 호출 URI: GET /post-replies/{seq}
     */
    List<BokManagerPostDto> getThread(int rootSeq);

    /**
     * 핵심 기능: 게시물 내용 수정. 관리자는 비밀번호 없이, 그 외에는 작성 시 사용한 비밀번호(또는 로그인 세션 본인 확인)로만 가능.
     * 호출 URI: POST /post-edit
     * @return 1 이상: 성공, 0: 대상 없음, -1: 권한 없음
     */
    int updateItem(int seq, String contents, String requestUserId, boolean isAdminSession, String suppliedRawPassword);

    /**
     * 핵심 기능: 게시물(및 그 하위 대댓글 전체) 삭제. 관리자는 비밀번호 없이, 그 외에는 작성 시 사용한 비밀번호(또는 로그인 세션 본인 확인)로만 가능.
     * 호출 URI: POST /post-delete
     * @return 1 이상: 삭제된 행 수, 0: 대상 없음, -1: 권한 없음
     */
    int deleteItem(int seq, String requestUserId, boolean isAdminSession, String suppliedRawPassword);

    /**
     * 핵심 기능: 게시물 좋아요 수 누적.
     * 호출 URI: POST /post-like
     */
    int likeItem(int seq);
}
