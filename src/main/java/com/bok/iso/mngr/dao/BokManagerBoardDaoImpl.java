package com.bok.iso.mngr.dao;

import java.util.List;

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
        //logger.info("--- {}", "DROP TABLE BOK_MNGR_CAL_HOLIDAY");
        //jdbcTemplate.update("DROP TABLE BOK_MNGR_CAL_HOLIDAY");

        StringBuffer sql = new StringBuffer("/* 게시판 DB 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_BOARD ");
        sql.append("\n\t(SEQ INTEGER PRIMARY KEY AUTOINCREMENT, TITLE, CATEGORY_INDEX INTEGER, CONTENTS, CREATE_USER_ID, ATTACH_INDEX INTEGER, UPDATE_TIME LONG, CREATE_TIME LONG, DELETE_YN BOOLEAN)");
        logger.info("--- {}", sql.toString());
        logger.info("--- result=[{}]", jdbcTemplate.update(sql.toString())); 
    }

    /**
     * Inserts a new record into the BOK_MNGR_BOARD table.
     * @param input The data to insert.
     * @return The number of rows affected.
     */
    @Override
    public int insertItem(BokManagerBoardDto input) {
        String sql = "INSERT INTO BOK_MNGR_BOARD (TITLE, CATEGORY_INDEX, CONTENTS, CREATE_USER_ID, ATTACH_INDEX, UPDATE_TIME, CREATE_TIME, DELETE_YN) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        logger.info("--- SQL: {}", sql);
        return jdbcTemplate.update(sql, input.getTitle(), input.getCategoryIndex(), input.getContents(), input.getCreateUserId(), input.getAttachIndex(), input.getUpdateTime(), input.getCreateTime(), input.isDeleteYn());
    }

    /**
     * Deletes a record from the BOK_MNGR_BOARD table by its sequence ID.
     * @param seq The sequence ID of the record to delete.
     * @return The number of rows affected.
     */
    @Override
    public int deleteItem(int seq) {
        String sql = "DELETE FROM BOK_MNGR_BOARD WHERE SEQ = ?";
        logger.info("--- SQL: {}", sql);
        return jdbcTemplate.update(sql, seq);
    }

    /**
     * Updates an existing record in the BOK_MNGR_BOARD table.
     * @param input The data to update.
     * @return The number of rows affected.
     */
    @Override
    public int updateItem(BokManagerBoardDto input) {
        String sql = "UPDATE BOK_MNGR_BOARD SET TITLE = ?, CATEGORY_INDEX = ?, CONTENTS = ?, CREATE_USER_ID = ?, ATTACH_INDEX = ?, UPDATE_TIME = ?, CREATE_TIME = ?, DELETE_YN = ? WHERE SEQ = ?";
        logger.info("--- SQL: {}", sql);
        return jdbcTemplate.update(sql, input.getTitle(), input.getCategoryIndex(), input.getContents(), input.getCreateUserId(), input.getAttachIndex(), input.getUpdateTime(), input.getCreateTime(), input.isDeleteYn(), input.getSeq());
    }

    /**
     * Retrieves all records from the BOK_MNGR_BOARD table.
     * @return A list of BokManagerBoardDto objects.
     */
    @Override
    public List<BokManagerBoardDto> selectList() {
        String sql = "SELECT SEQ, TITLE, CATEGORY_INDEX, CONTENTS, CREATE_USER_ID, ATTACH_INDEX, UPDATE_TIME, CREATE_TIME, DELETE_YN FROM BOK_MNGR_BOARD";
        logger.info("--- SQL: {}", sql);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BokManagerBoardDto dto = new BokManagerBoardDto();
            dto.setSeq(rs.getInt("SEQ"));
            dto.setTitle(rs.getString("TITLE"));
            dto.setCategoryIndex(rs.getInt("CATEGORY_INDEX"));
            dto.setContents(rs.getString("CONTENTS"));
            dto.setCreateUserId(rs.getString("CREATE_USER_ID"));
            dto.setAttachIndex(rs.getInt("ATTACH_INDEX"));
            dto.setUpdateTime(rs.getLong("UPDATE_TIME"));
            dto.setCreateTime(rs.getLong("CREATE_TIME"));
            dto.setDeleteYn(rs.getBoolean("DELETE_YN"));
            return dto;
        });
    }

    /**
     * Retrieves a single record from the BOK_MNGR_BOARD table by its sequence ID.
     * @param seq The sequence ID of the record to retrieve.
     * @return A BokManagerBoardDto object representing the record.
     */
    @Override
    public BokManagerBoardDto selectItem(int seq) {
        String sql = "SELECT SEQ, TITLE, CATEGORY_INDEX, CONTENTS, CREATE_USER_ID, ATTACH_INDEX, UPDATE_TIME, CREATE_TIME, DELETE_YN FROM BOK_MNGR_BOARD WHERE SEQ = ?";
        logger.info("--- SQL: {}", sql);
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            BokManagerBoardDto dto = new BokManagerBoardDto();
            dto.setSeq(rs.getInt("SEQ"));
            dto.setTitle(rs.getString("TITLE"));
            dto.setCategoryIndex(rs.getInt("CATEGORY_INDEX"));
            dto.setContents(rs.getString("CONTENTS"));
            dto.setCreateUserId(rs.getString("CREATE_USER_ID"));
            dto.setAttachIndex(rs.getInt("ATTACH_INDEX"));
            dto.setUpdateTime(rs.getLong("UPDATE_TIME"));
            dto.setCreateTime(rs.getLong("CREATE_TIME"));
            dto.setDeleteYn(rs.getBoolean("DELETE_YN"));
            return dto;
        }, seq);
    }

}
