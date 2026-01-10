package com.bok.iso.mngr.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

@Repository
public class BokManagerUserDaoImpl implements BokManagerUserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    @Override
    public void initTable() {
        StringBuffer sql = new StringBuffer("\n\n\t/* 사용자 테이블 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_USERS (USER_ID PRIMARY KEY, USER_PW, USER_EMAIL)");
        logger.info("--- {\n", sql.toString());
        logger.info("--- 사용자 테이블 초기화(이미 존재하는 경우 무시) RESULT =[{}]", jdbcTemplate.update(sql.toString())); 

        sql = new StringBuffer("\n\n\t/* 관리자 정보 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tINSERT OR IGNORE INTO BOK_MNGR_USERS (USER_ID, USER_PW, USER_EMAIL) VALUES (?,?,?)");
        logger.info("--- {}\n", sql.toString());
        logger.info("--- 관리자 정보 초기화(이미 존재하는 경우 무시) RESULT(ohhyonchul)=[{}]", jdbcTemplate.update(sql.toString(), "ohhyonchul", "1", "hyonchul.oh@bok.or.kr"));
        //logger.info("--- result=[{}]", jdbcTemplate.update(sql.toString(), "2310449", "bok1234!!", "hyonchul.oh@bok.or.kr"));
    }

    @Override
    public int insertId(String userId, String userPw, String userName, String userEmail) {
        logger.info("--- insertId()");
        return 0;
    }

    @Override
    public BokManagerUserDto selectId(String userId) {
        StringBuffer sql = new StringBuffer("\n\n\t/* 단건 조회 쿼리 */");
        sql.append("\n\tSELECT * FROM BOK_MNGR_USERS WHERE USER_ID=? LIMIT 1");
        logger.info("--- {}\n", sql.toString());
        logger.info("--- 사용자 단건 조회 USER_ID : " + userId);
        BokManagerUserDto retValue = null;
        try {
            retValue = jdbcTemplate.queryForObject(sql.toString(), (rs, rowNum) -> {
                BokManagerUserDto result = new BokManagerUserDto();
                result.setUserId(rs.getString("USER_ID"));
                result.setUserPw(rs.getString("USER_PW"));
                result.setEmail(rs.getString("USER_EMAIL"));
                return result;
            }, userId);
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }

    @Override
    public int updateId(String seq, String UserId, String userPw, String userName, String userEmail) {
        logger.info("--- initTable()");
        return 0;
    }

    @Override
    public int deleteId(String seq) {
        logger.info("--- initTable()");
        return 0;
    }


}
    
