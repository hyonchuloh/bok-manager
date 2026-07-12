package com.ohc.bok.mngr.svc;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerBoardDto;

public interface BokManagerBoardSvc {

    /**
     * 핵심 기능: 게시판 테이블 초기화(앱 기동 시 1회).
     * 호출 URI: 없음 (BokManagerApplication 기동 시 @PostConstruct에서 호출)
     */
    void initTable();

    /**
     * 핵심 기능: 게시글 신규 등록/수정.
     * 호출 URI: POST /manager/board-save
     */
    int updateItem(BokManagerBoardDto input);

    /**
     * 핵심 기능: 게시글 단건 조회.
     * 호출 URI: GET /manager/board-popup/{seq}, GET /manager/board (seq 지정 시)
     */
    BokManagerBoardDto selectItem(int seq);

    /**
     * 핵심 기능: 게시글 목록 조회.
     * 호출 URI: GET /manager/board
     */
    List<BokManagerBoardDto> getListItems();

    /**
     * 핵심 기능: 가장 최근 게시글 조회.
     * 호출 URI: GET /manager/board (seq 미지정 시)
     */
    BokManagerBoardDto getLatestItem();

    /**
     * 핵심 기능: 게시글 삭제.
     * 호출 URI: POST /manager/board-delete
     */
    int deleteItem(int seq);
}
