package com.bok.iso.mngr.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;
import org.springframework.stereotype.Repository;

@Repository
public class BokManagerBoardDaoImpl implements BokManagerBoardDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());	
    
    /**
     * Initializes the BOK_MNGR_BOARD table if it does not already exist.
     */
    @Override
    public void initTable() {
        // 초기 테이블을 drop 하는 코드
        //logger.info("--- Dropping existing BOK_MNGR_BOARDS table if it exists");
        //jdbcTemplate.execute("DROP TABLE IF EXISTS BOK_MNGR_BOARDS");

        StringBuffer sql = new StringBuffer("/* 게시판 DB 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_BOARDS ");
        sql.append("\n\t(CATEGORY_INDEX INTEGER, CONTENTS)");
        logger.info("--- {}", sql.toString());
        int result = jdbcTemplate.update(sql.toString());
        logger.info("--- result=[{}]", result);

    }
    /**
     * Inserts a new record into the BOK_MNGR_BOARD table.
     * @param input The data to insert.
     * @return The number of rows affected.
     */
    @Override
    public int insertItem(BokManagerBoardDto input) {
        String sql = "INSERT INTO BOK_MNGR_BOARDS (CATEGORY_INDEX, CONTENTS) VALUES (?, ?)";
        logger.info("--- SQL: {}", sql);
        return jdbcTemplate.update(sql, input.getCategoryIndex(), input.getContents());
    }

    /**
     * Updates an existing record in the BOK_MNGR_BOARD table.
     * @param input The data to update.
     * @return The number of rows affected.
     */
    @Override
    public int updateItem(BokManagerBoardDto input) {
        String sql = "UPDATE BOK_MNGR_BOARDS SET CONTENTS = ? WHERE CATEGORY_INDEX = ?";
        logger.info("--- SQL: {}", sql);
        return jdbcTemplate.update(sql, input.getContents(), input.getCategoryIndex());
    }

    /**
     * Retrieves a single record from the BOK_MNGR_BOARD table by its sequence ID.
     * @param seq The sequence ID of the record to retrieve.
     * @return A BokManagerBoardDto object representing the record.
     */
    @Override
    public BokManagerBoardDto selectItem(int seq) {
        String sql = "SELECT CATEGORY_INDEX, CONTENTS FROM BOK_MNGR_BOARDS WHERE CATEGORY_INDEX = ?";
        logger.info("--- SQL: {}", sql);
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                BokManagerBoardDto dto = new BokManagerBoardDto();
                dto.setCategoryIndex(rs.getInt("CATEGORY_INDEX"));
                dto.setContents(rs.getString("CONTENTS"));
                return dto;
            }, seq);
        } catch (Exception e) {
            logger.error("Error fetching item with seq {}: {}", seq, e.getMessage());
            return null;
        }        
    }
}
