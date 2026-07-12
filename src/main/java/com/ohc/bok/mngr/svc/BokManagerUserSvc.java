package com.ohc.bok.mngr.svc;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerUserDto;

public interface BokManagerUserSvc {

    /**
     * 핵심 기능: 세션에 로그인된 사용자가 있는지 검증.
     * 호출 URI: 로그인이 필요한 거의 모든 화면 진입 시 공통 호출
     * (GET /, /login, /home, /admin/users, POST /admin/users-passkey-delete,
     *  GET/POST /manager/board*, /manager/calendar*, /manager/callbook* 등)
     */
    public boolean isAuthentication(HttpSession session);

    /**
     * 핵심 기능: 로그인 성공 시 세션에 사용자 ID를 저장.
     * 호출 URI: POST /login, POST /login/passkey/assert
     */
    public void setSessionForUserId(HttpSession session, String userId);

    /**
     * 핵심 기능: 로그아웃 시 세션에서 사용자 ID를 제거.
     * 호출 URI: GET /logout
     */
    public void removeSessionForUserId(HttpSession session);

    /**
     * 핵심 기능: 세션에 저장된 로그인 사용자 ID를 조회.
     * 호출 URI: GET /home, GET /manager/calendar, GET /manager/calendar-week, GET /manager/calfind, GET /manager/callbook
     */
    public String getUserId(HttpSession session);

    /**
     * 핵심 기능: 전체 사용자 계정 목록 조회.
     * 호출 URI: GET /admin/users
     */
    public java.util.List<BokManagerUserDto> selectAll();

    /**
     * 핵심 기능: 사용자 테이블 초기화(앱 기동 시 1회).
     * 호출 URI: 없음 (BokManagerApplication 기동 시 @PostConstruct에서 호출)
     */
    public void initTable();

    /**
     * 핵심 기능: 사용자 ID로 계정 단건 조회.
     * 호출 URI: GET /, GET /login, POST /login, POST /login/passkey/options
     */
    public BokManagerUserDto selectId(String userId);

    /**
     * 핵심 기능: 사용자 계정 신규 등록.
     * 호출 URI: POST /admin/users-insert
     */
    public int insertId(String userId, String userPw, String userEmail);

    /**
     * 핵심 기능: 사용자 계정 삭제.
     * 호출 URI: POST /admin/users-delete
     */
    public int deleteId(String userId);

    /**
     * 핵심 기능: 사용자 계정 정보 수정.
     * 호출 URI: POST /admin/users-edit
     */
    public int updateId(String userId, String userPw, String userEmail);

    /* Font Management */

    /**
     * 핵심 기능: 선택 가능한 전체 폰트 목록 조회.
     * 호출 URI: GET /manager/board
     */
    public List<String> getFontListAll();

    /**
     * 핵심 기능: 화면(category)별 사용 폰트 저장.
     * 호출 URI: POST /manager/board-save
     */
    public int setCurrentFont(String category, String fontName);

    /**
     * 핵심 기능: 화면(category)별 현재 폰트 조회.
     * 호출 URI: GET /manager/board, GET /manager/calendar, GET /manager/calendar-week
     */
    public String getCurrentFont(String category);

    /**
     * 핵심 기능: 화면(category)별 폰트 크기 저장.
     * 호출 URI: POST /manager/board-save
     */
    public int setCurrentSize(String category, String fontSize);

    /**
     * 핵심 기능: 화면(category)별 현재 폰트 크기 조회.
     * 호출 URI: GET /manager/board, GET /manager/calendar, GET /manager/calendar-week
     */
    public String getCurrentSize(String category);

    /**
     * 핵심 기능: 화면(category)별 줄 간격 저장.
     * 호출 URI: POST /manager/board-save
     */
    public int setCurrentLineHeight(String category, String lineHeight);

    /**
     * 핵심 기능: 화면(category)별 현재 줄 간격 조회.
     * 호출 URI: GET /manager/board, GET /manager/calendar, GET /manager/calendar-week
     */
    public String getCurrentLineHeight(String category);

    /**
     * 핵심 기능: 화면(category)별 자간 저장.
     * 호출 URI: POST /manager/board-save
     */
    public int setCurrentLetterSpacing(String category, String letterSpacing);

    /**
     * 핵심 기능: 화면(category)별 현재 자간 조회.
     * 호출 URI: GET /manager/board, GET /manager/calendar, GET /manager/calendar-week
     */
    public String getCurrentLetterSpacing(String category);

}
