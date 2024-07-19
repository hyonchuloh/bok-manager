package com.bok.iso.mngr.dao;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;

@Repository
public class BokManagerCallbookDaoImpl implements BokManagerCallbookDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *   <th>no</th>
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

        StringBuffer createDb = new StringBuffer("CREATE TABLE IF NOT EXISTS BOK_MNGR_CALLBOOK ");
        createDb.append("(SEQ INTEGER PRIMARY KEY AUTOINCREMENT, EXT_NAME, DEP_NAME, BIZ_NAME, NAME, CALL, EMAIL, EXT);");
        jdbcTemplate.execute(createDb.toString());

        return null;
    }

}
