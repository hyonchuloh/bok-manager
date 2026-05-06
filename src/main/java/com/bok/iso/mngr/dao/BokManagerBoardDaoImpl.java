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
        // 초기 테이블을 drop 하는 코드
        //logger.info("--- Dropping existing BOK_MNGR_BOARDS table if it exists ...");
        //jdbcTemplate.execute("DROP TABLE BOK_MNGR_BOARDS");
        
        StringBuffer sql = new StringBuffer("\n\n\t/* 게시판 DB 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_BOARDS ");
        sql.append("\n\t(SEQ INTEGER PRIMARY KEY AUTOINCREMENT, TITLE VARCHAR(255), CONTENTS TEXT, CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        logger.info("--- {}\n", sql.toString());
        jdbcTemplate.execute(sql.toString());
        logger.info("--- 게시판 DB 초기화 완료.");

        /* 기본 게시물 삽입 */
        if ( this.getListItems().isEmpty() ) {
            BokManagerBoardDto defaultBoard = new BokManagerBoardDto();
            defaultBoard.setTitle("Welcome to BOK Manager!");
            defaultBoard.setContents("기본 게시물이 등록되었습니다. 게시판을 자유롭게 사용해보세요.");
            this.insertItem(defaultBoard);
        }
        
    }
    /**
     * Inserts a new record into the BOK_MNGR_BOARD table.
     * @param input The data to insert.
     * @return The number of rows affected.
     */
    @Override
    public int insertItem(BokManagerBoardDto input) {
        String sql = "\n\n\tINSERT INTO BOK_MNGR_BOARDS (TITLE, CONTENTS) VALUES (?, ?)";
        logger.info("--- SQL: {}\n", sql);
        int result = jdbcTemplate.update(sql, input.getTitle(), input.getContents());
        logger.info("--- 게시판 데이터 삽입 완료. result=[{}]", result);
        return result;
    }

    /**
     * Updates an existing record in the BOK_MNGR_BOARD table.
     * @param input The data to update.
     * @return The number of rows affected.
     */
    @Override
    public int updateItem(BokManagerBoardDto input) {
        String sql = "\n\n\tUPDATE BOK_MNGR_BOARDS SET TITLE = ?, CONTENTS = ? WHERE SEQ = ?";
        logger.info("--- SQL: {}\n", sql);
        int result = jdbcTemplate.update(sql, input.getTitle(), input.getContents(), input.getSeq());
        logger.info("--- 게시판 데이터 수정 완료. result=[{}]", result);
        return result;
    }

    /**
     * Retrieves a single record from the BOK_MNGR_BOARD table by its sequence ID.
     * @param seq The sequence ID of the record to retrieve.
     * @return A BokManagerBoardDto object representing the record.
     */
    @Override
    public BokManagerBoardDto selectItem(int seq) {
        String sql = "\n\n\tSELECT SEQ, CONTENTS, TITLE, CREATED_AT FROM BOK_MNGR_BOARDS WHERE SEQ = ?";
        logger.info("--- SQL: {}\n", sql);
        logger.info("--- 게시판 데이터 조회. seq=[{}]", seq);
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                BokManagerBoardDto dto = new BokManagerBoardDto();
                dto.setSeq(rs.getInt("SEQ"));
                dto.setContents(rs.getString("CONTENTS"));
                dto.setTitle(rs.getString("TITLE"));
                dto.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
                return dto;
            }, seq);
        } catch (Exception e) {
            logger.error("Error fetching item with seq {}: {}", seq, e.getMessage());
            return null;
        }        
    }

    @Override
    public List<BokManagerBoardDto> getListItems() {
        String sql = "\n\n\tSELECT SEQ, TITLE, CONTENTS, CREATED_AT FROM BOK_MNGR_BOARDS ORDER BY CREATED_AT DESC";
        logger.info("--- SQL: {}\n", sql);
        List<BokManagerBoardDto> items = jdbcTemplate.query(sql, (rs, rowNum) -> {
            BokManagerBoardDto dto = new BokManagerBoardDto();
            dto.setSeq(rs.getInt("SEQ"));
            dto.setTitle(rs.getString("TITLE"));
            dto.setContents(rs.getString("CONTENTS"));
            dto.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
            return dto;
        });
        logger.info("--- 게시판 데이터 목록 조회 건수=[{}]", items.size());
        return items;
    }

    @Override
    public BokManagerBoardDto getLatestItem() { 
        String sql = "\n\n\tSELECT SEQ, TITLE, CONTENTS, CREATED_AT FROM BOK_MNGR_BOARDS ORDER BY CREATED_AT DESC LIMIT 1";
        logger.info("--- SQL: {}\n", sql);
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                BokManagerBoardDto dto = new BokManagerBoardDto();
                dto.setSeq(rs.getInt("SEQ"));
                dto.setTitle(rs.getString("TITLE"));
                dto.setContents(rs.getString("CONTENTS"));
                dto.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
                return dto;
            });
        } catch (Exception e) {
            logger.error("Error fetching latest item: {}", e.getMessage());
            return null;
        }
    }
}
