package com.bok.iso.mngr.svc;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;

public interface BokManagerCalendarSvc {

    public void initTable();
    public List<String> selectAll();
    public Map<String, String> selectItems(int year, int month, String name);
    public int insertItem(BokManagerCalendarHolidayDto dto) ;

    public int [][] getCalendarTable(Calendar calendar, int year, int month) ;

    public Map<String, String> loadMap(String filePath) ;

    public int saveMap(String filePath, String key, String value);

    /** year, month 파라미터를 정수로 변환하고 13월/0월 보정을 적용한다. 반환값: {yearInt, monthInt, dayInt} */
    public int[] resolveYearMonthDay(Calendar calendar, String year, String month);

    /** 필터/검색어가 없고 이번달인 경우, 오늘이 속한 주의 시작일로 startDay를 보정한다. */
    public int resolveStartDay(int[][] dayTable, int dayInt, int monthInt, int startDay, String filterKey);

    /** 다음달/다음년을 계산한다. 반환값: {nextYear, nextMonth} */
    public int[] resolveNextYearMonth(int yearInt, int monthInt);

    /** filterKey로 캘린더 본문을 정리하고, 일치 라인을 하이라이트한다. */
    public Map<String, String> applyFilterHighlight(Map<String, String> data, String filterKey);

    /** searchKey가 포함된 항목만 추려 하이라이트한다. */
    public Map<String, String> applySearchHighlight(Map<String, String> data, String searchKey);

}
