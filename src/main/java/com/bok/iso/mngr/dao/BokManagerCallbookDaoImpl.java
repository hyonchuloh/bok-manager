package com.bok.iso.mngr.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;
import com.bok.iso.mngr.dao.dto.BokManagerCallbookRowMapper;

@Repository
public class BokManagerCallbookDaoImpl implements BokManagerCallbookDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        return jdbcTemplate.query(sql.toString(), new BokManagerCallbookRowMapper());
    }

    @Override
    public BokManagerCallbookDto selectItem(int seq) {
        StringBuffer sql = new StringBuffer("/* 단건 조회 쿼리 */");
        sql.append("\n\tSELECT * FROM BOK_MNGR_CALLBOOK WHERE SEQ=" + seq);
        return jdbcTemplate.queryForObject(sql.toString(), BokManagerCallbookDto.class);
    }

    @Override
    public int updateItem(BokManagerCallbookDto dto) {
        StringBuffer sql = new StringBuffer("/* 업데이트 쿼리 */");
        sql.append("\n\tUPDATE BOK_MNGR_CALLBOOK SET EXT_NAME=?, DEP_NAME=?, BIZ_NAME=?, NAME=?, CALL=?, EMAIL=?, EXT=?");
        return jdbcTemplate.update(sql.toString(), dto.getExtName(), dto.getDepName(), dto.getBizName(), dto.getName(), dto.getCall(), dto.getEmail(), dto.getExt());
    }

    @Override
    public int insertItem(BokManagerCallbookDto dto) {
        StringBuffer sql = new StringBuffer("/* 단건 추가 쿼리 */");
        sql.append("\n\tINSERT INTO BOK_MNGR_CALLBOOK (EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT) VALUES (?,?,?,?,?,?,?)");
        return jdbcTemplate.update(sql.toString(), dto.getExtName(), dto.getDepName(), dto.getBizName(), dto.getName(), dto.getCall(), dto.getEmail(), dto.getExt());
    }

    @Override
    public int deleteItem(int seq) {
        // TODO Auto-generated method stub
        return 0;
    }

}
