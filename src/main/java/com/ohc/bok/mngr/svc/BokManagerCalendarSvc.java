package com.ohc.bok.mngr.svc;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.ohc.bok.mngr.dao.dto.BokManagerCalendarHolidayDto;

public interface BokManagerCalendarSvc {

    /**
     * 핵심 기능: 공휴일/기념일 테이블 초기화(앱 기동 시 1회).
     * 호출 URI: 없음 (BokManagerApplication 기동 시 @PostConstruct에서 호출)
     */
    public void initTable();

    /**
     * 핵심 기능: 등록된 공휴일/기념일 이름 전체 목록 조회.
     * 호출 URI: 없음 (현재 컨트롤러에서 호출되지 않음)
     */
    public List<String> selectAll();

    /**
     * 핵심 기능: 특정 연/월의 공휴일/기념일 목록을 날짜별 맵으로 조회.
     * 호출 URI: GET /manager/calendar, GET /manager/calendar-week
     */
    public Map<String, String> selectItems(int year, int month, String name);

    /**
     * 핵심 기능: 공휴일/기념일 등록(동일 날짜 데이터가 있으면 병합).
     * 호출 URI: POST /manager/calendar/holiday, POST /manager/calendar-week/holiday
     */
    public int insertItem(BokManagerCalendarHolidayDto dto) ;

    /**
     * 핵심 기능: 특정 연/월의 주차별 날짜 표(7x7)를 생성.
     * 호출 URI: GET /manager/calendar, GET /manager/calendar-week
     */
    public int [][] getCalendarTable(Calendar calendar, int year, int month) ;

    /**
     * 핵심 기능: 사용자별 캘린더 본문 파일(.dat)을 로드.
     * 호출 URI: GET /manager/calendar, GET /manager/calfind, GET /manager/calendar-week
     */
    public Map<String, String> loadMap(String filePath) ;

    /**
     * 핵심 기능: 캘린더 본문 파일(.dat)에 특정 날짜 메모를 저장.
     * 호출 URI: POST /manager/calendar, POST /manager/calendar-week
     */
    public int saveMap(String filePath, String key, String value);

    /**
     * 핵심 기능: year, month 파라미터를 정수로 변환하고 13월/0월 보정을 적용한다.
     * 반환값: {yearInt, monthInt, dayInt}
     * 호출 URI: GET /manager/calendar, GET /manager/calendar-week
     */
    public int[] resolveYearMonthDay(Calendar calendar, String year, String month);

    /**
     * 핵심 기능: 필터/검색어가 없고 이번달인 경우, 오늘이 속한 주의 시작일로 startDay를 보정한다.
     * 호출 URI: GET /manager/calendar, GET /manager/calendar-week
     */
    public int resolveStartDay(int[][] dayTable, int dayInt, int monthInt, int startDay, String filterKey);

    /**
     * 핵심 기능: 다음달/다음년을 계산한다. 반환값: {nextYear, nextMonth}
     * 호출 URI: GET /manager/calendar, GET /manager/calendar-week
     */
    public int[] resolveNextYearMonth(int yearInt, int monthInt);

    /**
     * 핵심 기능: filterKey로 캘린더 본문을 정리하고, 일치 라인을 하이라이트한다.
     * 호출 URI: GET /manager/calendar, GET /manager/calendar-week
     */
    public Map<String, String> applyFilterHighlight(Map<String, String> data, String filterKey);

    /**
     * 핵심 기능: searchKey가 포함된 항목만 추려 하이라이트한다.
     * 호출 URI: GET /manager/calfind
     */
    public Map<String, String> applySearchHighlight(Map<String, String> data, String searchKey);

}
