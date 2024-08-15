package com.bok.iso.mngr.svc;

import java.util.List;
import java.util.Map;

import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;

public interface BokManagerCalendarSvc {

    public void initTable();
    public List<String> selectAll();
    public Map<String, String> selectItems(int year, int month, String name);
    public int insertItem(BokManagerCalendarHolidayDto dto) ;

}
