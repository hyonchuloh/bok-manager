package com.bok.iso.mngr.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerCalendarDao;
import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;

@Service
public class BokManagerCalendarSvcImpl implements BokManagerCalendarSvc {

    @Autowired
    public BokManagerCalendarDao dao;

    @Override
    public void initTable() {
        dao.initTable();
    }
    @Override
    public int insertItem(BokManagerCalendarHolidayDto dto) {
        return dao.insertItem(dto);
    }

    @Override
    public Map<String, String> selectItems(int year, int month) {
        List<BokManagerCalendarHolidayDto> result = dao.selectItems(year, month);
        Map<String, String> retValue = null;
        if ( result.size() > 0 ) {
            retValue = new HashMap<String, String>(); //CAL.${yearInt}.${monthInt}.${col}
            String spanTag = "";
            for ( BokManagerCalendarHolidayDto dao : result ) {
                switch (dao.getCalClcd() ) {
                case 1:
                    spanTag = "<span style='background-color: #ffdde5; font-weight: 700;'>";
                    break;
                case 2:
                    spanTag = "<span style='background-color: #ddffdd; font-weight: 700;'>";
                    break;
                }
                retValue.put("CAL." + dao.getCalYear() + "." + dao.getCalMonth() + "." + dao.getCalDay(), spanTag + dao.getCalData() + "</span>");
            }
        }
        return retValue;
    }

}
