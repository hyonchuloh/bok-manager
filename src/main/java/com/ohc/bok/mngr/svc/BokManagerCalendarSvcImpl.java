package com.ohc.bok.mngr.svc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ohc.bok.mngr.dao.BokManagerCalendarDao;
import com.ohc.bok.mngr.dao.dto.BokManagerCalendarHolidayDto;

@Service
public class BokManagerCalendarSvcImpl implements BokManagerCalendarSvc {

    public final BokManagerCalendarDao dao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerCalendarSvcImpl(BokManagerCalendarDao dao) {
        this.dao = dao;
    }	

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

    @Override
    public int[][] getCalendarTable(Calendar calendar, int year, int month) {
        calendar.set(year, month-1, 1);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = calendar.get(Calendar.DAY_OF_WEEK);
        int retValue[][] = new int[7][7];
        int daycount = 1;
        for ( int i=0; i<7; i++) {
            for ( int j=0; j<7; j++) {
                if ( firstDay -1 > 0 ) {
                    retValue[i][j] = 0;
                    firstDay--;
                    continue;
                } else if ( daycount > lastDay ) {
                    retValue[i][j] = -1;
                } else {
                    retValue[i][j] = daycount;
                }
                daycount++;
            }
        }
        return retValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> loadMap(String filePath) {
        Map<String, String> map = new HashMap<String, String>();
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(filePath));
            map = (Map<String, String>) ois.readObject();
        } catch ( Exception e ) {
            logger.error("--- ERROR 발생 : {}", e.getMessage());
        } finally {
            try {
                if ( ois != null ) ois.close();
            } catch ( Exception e ) {
                //
            }
        }
        return map;
    }

    @Override
    public int saveMap(String filePath, String key, String value) {
        Map<String, String> map = loadMap(filePath);
        logger.info("--- CURRENT MAP ["+key+"]=["+map.get(key)+"]");
        if ( value != null && value.trim().length() > 0 ) {
            map.put(key, value.trim());
            logger.info("--- SAVE MAP ["+key+"]=["+value.trim()+"]");
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(map);
            logger.info("--- SAVE SUCCESS!");
        } catch ( Exception e ) {
            logger.error("--- SAVE FAILURE!", e);
            return 0;
        } finally {
            try {
                if ( oos !=null ) oos.close();
            } catch ( Exception e ) {
                logger.error("--- SAVE FAILURE!", e);
                return 0;
            }
        }
        return 1;
    }

    @Override
    public int[] resolveYearMonthDay(Calendar calendar, String year, String month) {
        int yearInt = calendar.get(Calendar.YEAR);
        int monthInt = calendar.get(Calendar.MONTH) + 1;
        int dayInt = calendar.get(Calendar.DAY_OF_MONTH);
        if ( year != null && month != null )  {
            yearInt = Integer.parseInt(year);
            monthInt = Integer.parseInt(month);
            if ( monthInt == 13 )  {
                yearInt += 1;
                monthInt = 1;
            } else if ( monthInt == 0 )  {
                yearInt -= 1;
                monthInt = 12;
            }
        }
        return new int[] { yearInt, monthInt, dayInt };
    }

    @Override
    public int resolveStartDay(int[][] dayTable, int dayInt, int monthInt, int startDay, String filterKey) {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int secondWeekofMonth = dayTable[2][0];
        int thirdWeekofMonth = dayTable[3][0];
        int fourthWeekofMonth = dayTable[4][0];
        if ( startDay == 0 && filterKey == null && monthInt == currentMonth ) {
            if (dayInt >= fourthWeekofMonth) {
                startDay = fourthWeekofMonth;
            } else if (dayInt >= thirdWeekofMonth) {
                startDay = thirdWeekofMonth;
            } else if (dayInt >= secondWeekofMonth) {
                startDay = secondWeekofMonth;
            }
        }
        return startDay;
    }

    @Override
    public int[] resolveNextYearMonth(int yearInt, int monthInt) {
        int nextMonth = monthInt + 1;
        int nextYear = yearInt;
        if ( nextMonth == 13 ) {
            nextYear += 1;
            nextMonth = 1;
        }
        return new int[] { nextYear, nextMonth };
    }

    @Override
    public Map<String, String> applyFilterHighlight(Map<String, String> data, String filterKey) {
        if ( filterKey == null || filterKey.trim().length() == 0 ) {
            return data;
        }
        String temp = "";
        String [] lines = null;
        StringBuffer tempResult = null;
        for ( String key : data.keySet() ) {
            temp = data.get(key);

            temp = temp.replaceAll("<br>", "\n");
            temp = temp.replaceAll("<br/>", "\n");
            temp = temp.replaceAll("<br />", "\n");
            temp = temp.replaceAll("\t", "");
            temp = temp.replaceAll("<div", "\n<div");
            temp = temp.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            temp = temp.replaceAll("\n\n", "\n");

            lines = temp.split("\n");
            tempResult = new StringBuffer();
            for ( String line : lines ) {
                if ( line.trim().length() == 0 ) continue;
                if ( line.contains(filterKey) ) {
                    tempResult.append("<span style='background-color: #ffeedd;'>" + line + "</span><br/>");
                } else {
                    tempResult.append("<span style='color: #CCCCCC;'>" + line + "</span><br/>");
                }
            }
            data.put(key, tempResult.toString());
        }
        return data;
    }

    @Override
    public Map<String, String> applySearchHighlight(Map<String, String> data, String searchKey) {
        java.util.TreeMap<String, String> result = new java.util.TreeMap<String, String>();
        for ( String key : data.keySet() ) {
            if ( data.get(key).contains(searchKey) ) {
                logger.info("--- RESULT : ["+key+"]=["+data.get(key)+"]");
                result.put(key, data.get(key).replaceAll(searchKey, "<span style='background-color: yellow;'>" + searchKey + "</span>"));
            }
        }
        return result;
    }

}
