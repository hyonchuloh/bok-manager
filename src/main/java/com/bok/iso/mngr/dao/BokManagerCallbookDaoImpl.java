package com.bok.iso.mngr.dao;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
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
        jdbcTemplate.query(createDb.toString(), Integer.class);
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

        List<BokManagerCallbookDto> 

        StringBuffer sql = new StringBuffer("SELECT SEQ, EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT FROM BOK_MNGR_CALLBOOK ORDER BY EXT_NAME");
        return jdbcTemplate.query(sql.toString(), new BokManagerCallbookRowMapper());;
    }

}
