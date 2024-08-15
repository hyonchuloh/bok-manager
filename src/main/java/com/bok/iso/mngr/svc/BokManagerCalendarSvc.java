package com.bok.iso.mngr.svc;

import java.util.Map;

import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;

public interface BokManagerCalendarSvc {

    public void initTable();
    public Map<String, String> selectItems(int year, int month);
    public int insertItem(BokManagerCalendarHolidayDto dto) ;

}
