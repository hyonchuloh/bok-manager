package com.bok.iso.mngr.svc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BokManagerMainSvcImpl implements BokManagerMainSvc {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
