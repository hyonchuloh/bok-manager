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

}
