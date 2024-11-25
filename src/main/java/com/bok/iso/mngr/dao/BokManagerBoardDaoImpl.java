package com.bok.iso.mngr.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;

public class BokManagerBoardDaoImpl implements BokManagerBoardDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());	
    
    @Override
    public void initTable() {
        //logger.info("--- {}", "DROP TABLE BOK_MNGR_CAL_HOLIDAY");
        //jdbcTemplate.update("DROP TABLE BOK_MNGR_CAL_HOLIDAY");

        StringBuffer sql = new StringBuffer("/* 게시판 DB 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_BOARD ");
        sql.append("\n\t(SEQ INTEGER PRIMARY KEY AUTOINCREMENT, TITLE, CATEGORY_INDEX INTEGER, CONTENTS, CREATE_USER_ID, ATTACH_INDEX INTEGER, UPDATE_TIME LONG, CREATE_TIME LONG, DELETE_YN BOOLEAN)");
        logger.info("--- {}", sql.toString());
        logger.info("--- result=[{}]", jdbcTemplate.update(sql.toString())); 
    }

    
    

    @Override
    public int insertItem(BokManagerBoardDto input) {
        return 0;
    }

    @Override
    public int deleteItem(int seq) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteItem'");
    }

    @Override
    public int updateItem(BokManagerBoardDto input) {
        throw new UnsupportedOperationException("Unimplemented method 'updateItem'");
    }

    @Override
    public List<BokManagerBoardDto> selectList() {
        throw new UnsupportedOperationException("Unimplemented method 'selectList'");
    }

    @Override
    public BokManagerBoardDto selectItem(int seq) {
        throw new UnsupportedOperationException("Unimplemented method 'selectItem'");
    }

}
