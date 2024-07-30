package com.bok.iso.mngr.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;
import com.bok.iso.mngr.dao.dto.BokManagerCallbookRowMapper;

@Repository
public class BokManagerCallbookDaoImpl implements BokManagerCallbookDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    @Override
    public void initTable() {
        StringBuffer createDb = new StringBuffer("CREATE TABLE IF NOT EXISTS BOK_MNGR_CALLBOOK ");
        createDb.append("(SEQ INTEGER PRIMARY KEY AUTOINCREMENT, EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT);");
        jdbcTemplate.queryForObject(createDb.toString(), Integer.class);
    }

    /**
     *      <th>seq</th>
            <th>기관명</th>
            <th>부서명</th>
            <th>담당업무</th>
            <th>이름</th>
            <th>연락처</th>
            <th>이메일</th>
            <th>기타</th>
     */
    @Override
    public List<BokManagerCallbookDto> selectItems() {
        StringBuffer sql = new StringBuffer("/* 전체 리스트 조회 쿼리*/");
        sql.append("\n\tSELECT SEQ, EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT FROM BOK_MNGR_CALLBOOK ORDER BY EXT_NAME");
        List<BokManagerCallbookDto> retValue = jdbcTemplate.query(sql.toString(), new BokManagerCallbookRowMapper());
        logger.info("--- " + sql.toString());
        logger.info("--- RESULT CNT : " + retValue.size());
        return retValue;
    }

    @Override
    public BokManagerCallbookDto selectItem(int seq) {
        StringBuffer sql = new StringBuffer("/* 단건 조회 쿼리 */");
        sql.append("\n\tSELECT * FROM BOK_MNGR_CALLBOOK WHERE SEQ=" + seq);
        logger.info("--- " + sql.toString());
        return jdbcTemplate.queryForObject(sql.toString(), BokManagerCallbookDto.class);
    }

    @Override
    public int updateItem(BokManagerCallbookDto dto) {
        StringBuffer sql = new StringBuffer("/* 업데이트 쿼리 */");
        sql.append("\n\tUPDATE BOK_MNGR_CALLBOOK SET EXT_NAME=?, DEP_NAME=?, BIZ_NAME=?, NAME=?, CALL=?, EMAIL=?, EXT=?");
        logger.info("--- " + sql.toString());
        logger.info("--- PARAM : " + dto.toString());
        return jdbcTemplate.update(sql.toString(), dto.getExtName(), dto.getDepName(), dto.getBizName(), dto.getName(), dto.getCall(), dto.getEmail(), dto.getExt());
    }

    @Override
    public int insertItem(BokManagerCallbookDto dto) {
        StringBuffer sql = new StringBuffer("/* 단건 추가 쿼리 */");
        sql.append("\n\tINSERT INTO BOK_MNGR_CALLBOOK (EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT) VALUES (?,?,?,?,?,?,?)");
        logger.info("--- " + sql.toString());
        logger.info("--- PARAM : " + dto.toString());
        return jdbcTemplate.update(sql.toString(), dto.getExtName(), dto.getDepName(), dto.getBizName(), dto.getName(), dto.getCall(), dto.getEmail(), dto.getExt());
    }

    @Override
    public int deleteItem(int seq) {
        // TODO Auto-generated method stub
        return 0;
    }

}
