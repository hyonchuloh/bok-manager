package com.bok.iso.mngr.dao;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;

public interface BokManagerCalendarDao {

    public void initTable();
    public List<String> selectAll();
    public List<BokManagerCalendarHolidayDto> selectItems(int year, int month);
    public int insertItem(BokManagerCalendarHolidayDto dto) ;

}
