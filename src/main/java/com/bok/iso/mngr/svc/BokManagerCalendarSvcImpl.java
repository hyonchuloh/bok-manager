package com.bok.iso.mngr.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerCalendarDao;
import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;

@Service
public class BokManagerCalendarSvcImpl implements BokManagerCalendarSvc {

    @Autowired
    public BokManagerCalendarDao dao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    @Override
    public void initTable() {
        dao.initTable();
    }

    @Override
    public List<String> selectAll() {
        return dao.selectAll();
    }
    
    @Override
    public int insertItem(BokManagerCalendarHolidayDto dto) {
        return dao.insertItem(dto);
    }

    @Override
    public Map<String, String> selectItems(int year, int month, String name) {
        List<BokManagerCalendarHolidayDto> result = dao.selectItems(year, month, name);
        Map<String, String> retValue = null;
        if ( result.size() > 0 ) {
            retValue = new HashMap<String, String>(); //CAL.${yearInt}.${monthInt}.${col}
            String finalTagStr = "";
            for ( BokManagerCalendarHolidayDto dto : result ) {

                if ( dto.getCalData().startsWith("1") ) {
                    finalTagStr = "<span style='background-color: #ffdde5; font-weight: 700;'>";
                    finalTagStr += dto.getCalData().substring(1) + "</span>";
                } else if ( dto.getCalData().startsWith("2") ) {
                    finalTagStr = "<span style='background-color: #ddffdd; font-weight: 700;'>";
                    finalTagStr += dto.getCalData().substring(1) + "</span>";
                } else {
                    finalTagStr = "<span style='background-color: #ffdde5; font-weight: 700;'>";
                    finalTagStr += dto.getCalData() + "</span>";
                }

                if ( finalTagStr.contains(";1") ) {
                    finalTagStr = finalTagStr.replaceAll(";1","</span> <span style='background-color: #ffdde5; font-weight: 700;'>");
                }
                if ( finalTagStr.contains(";2") ) {
                    finalTagStr = finalTagStr.replaceAll(";2","</span> <span style='background-color: #ddffdd; font-weight: 700;'>");
                }
                retValue.put("CAL." + dto.getCalYear() + "." + dto.getCalMonth() + "." + dto.getCalDay(), finalTagStr);
            }
        }
        return retValue;
    }

}
