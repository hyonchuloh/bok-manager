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
    public int insertId(String userId, String userPw, String userEmail) {
        StringBuffer sql = new StringBuffer("\n\n\t/* 단건 등록 쿼리 */");
        sql.append("\n\tINSERT INTO BOK_MNGR_USERS (USER_ID, USER_PW, USER_EMAIL) VALUES (?,?,?)");
        logger.info("--- {}\n", sql.toString());
        logger.info("--- 사용자 단건 등록 USER_ID : " + userId);
        int retValue = 0;
        try {
            retValue = jdbcTemplate.update(sql.toString(), userId, userPw, userEmail);
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }

    @Override
    public BokManagerUserDto selectId(String userId) {
        StringBuffer sql = new StringBuffer("\n\n\t/* 단건 조회 쿼리 */");
        sql.append("\n\tSELECT * FROM BOK_MNGR_USERS WHERE USER_ID='"+userId+"' LIMIT 1");
        logger.info("--- {}\n", sql.toString());
        logger.info("--- 사용자 단건 조회 USER_ID : " + userId);
        BokManagerUserDto retValue = null;
        try {
            retValue = jdbcTemplate.queryForObject(
                sql.toString(),
                (rs, rowNum) -> {
                    BokManagerUserDto result = new BokManagerUserDto();
                    result.setUserId(rs.getString("USER_ID"));
                    result.setUserPw(rs.getString("USER_PW"));
                    result.setEmail(rs.getString("USER_EMAIL"));
                    return result;
                }
            );
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }

    @Override
    public int updateId(String userId, String userPw, String userEmail) {
        StringBuffer sql = new StringBuffer("\n\n\t/* 단건 수정 쿼리 */");
        sql.append("\n\tUPDATE BOK_MNGR_USERS SET USER_PW=?, USER_EMAIL=? WHERE USER_ID=?");
        logger.info("--- {}\n", sql.toString());
        logger.info("--- 사용자 단건 수정 USER_ID : " + userId);
        int retValue = 0;
        try {
            retValue = jdbcTemplate.update(sql.toString(), userPw, userEmail, userId);
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }

    @Override
    public int deleteId(String seq) {
        StringBuffer sql = new StringBuffer("\n\n\t/* 단건 삭제 쿼리 */");
        sql.append("\n\tDELETE FROM BOK_MNGR_USERS WHERE USER_ID=?");
        logger.info("--- {}\n", sql.toString());
        logger.info("--- 사용자 단건 삭제 USER_ID : " + seq);
        int retValue = 0;
        try {
            retValue = jdbcTemplate.update(sql.toString(), seq);
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }

    @Override
    public java.util.List<BokManagerUserDto> selectAll() {
        StringBuffer sql = new StringBuffer("\n\n\t/* 전체 조회 쿼리 */");
        sql.append("\n\tSELECT * FROM BOK_MNGR_USERS");
        logger.info("--- {}\n", sql.toString());
        java.util.List<BokManagerUserDto> retValue = null;
        try {
            retValue = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
                BokManagerUserDto result = new BokManagerUserDto();
                result.setUserId(rs.getString("USER_ID"));
                result.setUserPw(rs.getString("USER_PW"));
                result.setEmail(rs.getString("USER_EMAIL"));
                return result;
            });
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public String selectBokConfigValue(String key) {
        StringBuffer sql = new StringBuffer("\n\n\t/* 컨피그 값 조회 쿼리 */");
        sql.append("\n\tSELECT USER_PW FROM BOK_MNGR_USERS WHERE USER_ID=?");
        logger.info("--- {}\n", sql.toString());
        String retValue = null;
        try {
            retValue = jdbcTemplate.queryForObject(sql.toString(), new Object[] { key }, String.class);
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }
    
    /**
     * BOK_MNGR_USERS 테이블의 USER_ID 컬럼을 키로, USER_PW 컬럼을 값으로 활용하여 구성된 
     * 간단한 키-값 저장소에서 특정 키에 대한 값을 업데이트하는 메서드입니다.
     * @param key 업데이트할 키 (USER_ID)
     * @param value 업데이트할 값 (USER_PW)
     * @return 업데이트 결과 (성공 시 1, 실패 시 0)
     */
    @Override
    public int updateBokConfigValue(String key, String value) {
        // USER_ID에 해당 key 값이 없다면 INSERT를, 존재한다면 UPDATE를 수행하는 쿼리입니다.
        StringBuffer sql = new StringBuffer("\n\n\t/* BOK_MNGR_USERS 테이블의 USER_ID를 키로, USER_PW를 값으로 활용하는 간단한 키-값 저장소에서 특정 키에 대한 값을 업데이트하는 쿼리 */");
        sql.append("\n\tINSERT INTO BOK_MNGR_USERS (USER_ID, USER_PW) VALUES (?, ?) ON CONFLICT(USER_ID) DO UPDATE SET USER_PW=excluded.USER_PW");
        logger.info("--- {}\n", sql.toString());
        logger.info("--- BOK_MNGR_USERS 테이블의 USER_ID를 키로, USER_PW를 값으로 활용하는 간단한 키-값 저장소에서 특정 키에 대한 값을 업데이트하는 쿼리 - key: {}, value: {}", key, value);
        int retValue = 0;
        try {
            retValue = jdbcTemplate.update(sql.toString(), key, value);
        } catch ( Exception e ) {
            logger.error("--- {}", e.getMessage());
        }
        return retValue;
    }
}
    
