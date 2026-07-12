package com.ohc.bok.mngr.dao;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerCalendarHolidayDto;

public interface BokManagerCalendarDao {

    public void initTable();
    public List<String> selectAll();
    public List<BokManagerCalendarHolidayDto> selectItems(int year, int month, String name);
    public int insertItem(BokManagerCalendarHolidayDto dto) ;
    /* 단건조회 */
    public BokManagerCalendarHolidayDto selectItem(int year, int month, int day, String name);

}
