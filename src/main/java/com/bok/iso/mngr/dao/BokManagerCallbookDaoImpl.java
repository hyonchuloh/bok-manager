package com.bok.iso.mngr.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;

@Repository
public class BokManagerCallbookDaoImpl implements BokManagerCallbookDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    @Override
    public void initTable() {
        StringBuffer sql = new StringBuffer("/* 연락처 DB 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_CALLBOOK ");
        sql.append("\n\t(SEQ INTEGER PRIMARY KEY AUTOINCREMENT, EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT)");
        logger.info("--- {}", sql.toString());
        logger.info("--- result=[{}]", jdbcTemplate.update(sql.toString())); 
    }

    @Override
    public List<BokManagerCallbookDto> selectItems() {
        StringBuffer sql = new StringBuffer("/* 전체 리스트 조회 쿼리*/");
        sql.append("\n\tSELECT SEQ, EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT FROM BOK_MNGR_CALLBOOK ORDER BY SEQ DESC");
        List<BokManagerCallbookDto> retValue = jdbcTemplate.query(sql.toString(), (rs, rowNum) ->{
            BokManagerCallbookDto result = new BokManagerCallbookDto
            (rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
        return result;
        });
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
        sql.append("\n\tUPDATE BOK_MNGR_CALLBOOK SET EXT_NAME=?, DEP_NAME=?, BIZ_NAME=?, NAME=?, CALL=?, EMAIL=?, EXT=? WHERE SEQ=?");
        logger.info("--- " + sql.toString());
        logger.info("--- PARAM : " + dto.toString());
        return jdbcTemplate.update(sql.toString(), dto.getExtName(), dto.getDepName(), dto.getBizName(), dto.getName(), dto.getCall(), dto.getEmail(), dto.getExt(), dto.getSeq());
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
        StringBuffer sql = new StringBuffer("/* 단건 삭제 쿼리 */");
        sql.append("\n\tDELETE FROM BOK_MNGR_CALLBOOK WHERE SEQ=?");
        logger.info("--- " + sql.toString());
        logger.info("--- PARAM : " + seq);
        return jdbcTemplate.update(sql.toString(), seq);
    }

}
