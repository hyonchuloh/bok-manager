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
        logger.info("-- initTable");
        dao.initTable();
    }

    @Override
    public List<String> selectAll() {
        List<String> retValue = dao.selectAll();
        logger.info("-- selectAll result : " + retValue.size());
        return retValue;
    }
    
    @Override
    public int insertItem(BokManagerCalendarHolidayDto dto) {
        // Insert 하기 전에 동일한 년, 월, 일 데이터가 있는지 조회해서 있으면 업데이트, 없으면 인서트 하는 방식으로 구현할 것
        BokManagerCalendarHolidayDto existingDto = dao.selectItem(dto.getCalYear(), dto.getCalMonth(), dto.getCalDay(), dto.getCalName());
        if ( existingDto != null && existingDto.getCalData().trim().length() > 0 ) {
            logger.info("-- 기존 데이터 존재 : " + existingDto.toString());
            if ( dto.getCalData().startsWith("*") ) {
               dto.setCalData(dto.getCalData().substring(1));
            } else {
               dto.setCalData(existingDto.getCalData() + "," + dto.getCalData()); 
            }
        }
        int result = dao.insertItem(dto);
        logger.info("-- insertItem result : " + result);
        return result;
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
