package com.bok.iso.mngr.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;

@Repository
public class BokManagerCalendarDaoImpl implements BokManagerCalendarDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    @Override
    public void initTable() {
        /* 테이블 드랍코드 1번만 실행하고 바로 주석처리할 것 */
        logger.info("--- {}", "DROP TABLE BOK_MNGR_CAL_HOLIDAY");
        jdbcTemplate.update("DROP TABLE BOK_MNGR_CAL_HOLIDAY");

        StringBuffer sql = new StringBuffer("/* 기념일 DB 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_CAL_HOLIDAY ");
        sql.append("\n\t(CAL_YEAR INTEGER, CAL_MONTH INTERGER, CAL_DAY INTEGER, CAL_NAME, CAL_DATA, CONSTRAINT GROUP_PK PRIMARY KEY(CAL_YEAR, CAL_MONTH, CAL_DAY, CAL_NAME))");
        logger.info("--- {}", sql.toString());
        logger.info("--- result=[{}]", jdbcTemplate.update(sql.toString())); 
    }

    @Override
    public List<String> selectAll() {
        StringBuffer sql = new StringBuffer("/* 기념일 DB 전체조회 쿼리 */");
        sql.append("\n\tSELECT * FROM BOK_MNGR_CAL_HOLIDAY");
        List<String> retValue = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            return "INSERT INFO BOK_MNGR_CAL_HOLIDAY VALUES (" + rs.getInt(1) + "," + rs.getInt(2) + "," + rs.getInt(3) + "," + rs.getInt(4) + ",'" + rs.getString(5) + "')";
        });
        return retValue;
    }

    @Override
    public int insertItem(BokManagerCalendarHolidayDto dto) {
        StringBuffer sql = new StringBuffer("/* 기념일 DB 단건 추가(또는 대체) 쿼리 */");
        sql.append("\n\tINSERT OR REPLACE INTO BOK_MNGR_CAL_HOLIDAY (CAL_YEAR, CAL_MONTH, CAL_DAY, CAL_NAME, CAL_DATA) VALUES (?,?,?,?,?)");
        logger.info("--- " + sql.toString());
        logger.info("--- PARAM : [{}]", dto.toString());
        return jdbcTemplate.update(sql.toString(), dto.getCalYear(), dto.getCalMonth(), dto.getCalDay(), dto.getCalName(), dto.getCalData());
    }

    @Override
    public List<BokManagerCalendarHolidayDto> selectItems(int year, int month, String name) {
        StringBuffer sql = new StringBuffer("/* 기념일 DB 리스트 조회 쿼리*/");
        sql.append("\n\tSELECT * FROM BOK_MNGR_CAL_HOLIDAY WHERE CAL_YEAR="+year+" AND CAL_MONTH="+month+" AND CAL_NAME='"+name+"' ORDER BY CAL_DAY");
        List<BokManagerCalendarHolidayDto> retValue = jdbcTemplate.query(sql.toString(), (rs, rowNum) ->{
                    BokManagerCalendarHolidayDto result = new BokManagerCalendarHolidayDto
                    (rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5));
            return result;
        });
        logger.info("--- " + sql.toString());
        logger.info("--- RESULT CNT : " + retValue.size());
        return retValue;
    }

}
