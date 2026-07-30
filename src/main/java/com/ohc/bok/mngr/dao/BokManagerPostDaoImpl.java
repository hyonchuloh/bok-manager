package com.ohc.bok.mngr.dao;

import java.sql.Types;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ohc.bok.mngr.dao.dto.BokManagerPostDto;

@Repository
public class BokManagerPostDaoImpl implements BokManagerPostDao {

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerPostDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 짧은 게시물(트윗) 및 대댓글을 저장하는 BOK_MNGR_POSTS 테이블을 초기화한다.
     * PARENT_SEQ가 NULL이면 최상위 게시물, 값이 있으면 해당 SEQ에 대한 (대)댓글이다.
     */
    @Override
    public void initTable() {
        StringBuffer sql = new StringBuffer("\n\n\t/* 짧은 게시물/대댓글 DB 초기화(이미 존재하는 경우 무시) */");
        sql.append("\n\tCREATE TABLE IF NOT EXISTS BOK_MNGR_POSTS ");
        sql.append("\n\t(SEQ INTEGER PRIMARY KEY AUTOINCREMENT, PARENT_SEQ INTEGER, USER_ID VARCHAR(50), CONTENTS TEXT, CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        logger.info("--- {}\n", sql.toString());
        jdbcTemplate.execute(sql.toString());
        logger.info("--- 짧은 게시물 DB 초기화 완료.");

        /* 기존 테이블에 PASSWORD/LIKE_COUNT 컬럼이 없으면 추가(이미 존재하면 무시) */
        try {
            jdbcTemplate.execute("ALTER TABLE BOK_MNGR_POSTS ADD COLUMN PASSWORD VARCHAR(64)");
            logger.info("--- PASSWORD 컬럼 추가 완료.");
        } catch (Exception e) {
            logger.info("--- PASSWORD 컬럼이 이미 존재하여 추가를 건너뜁니다.");
        }
        try {
            jdbcTemplate.execute("ALTER TABLE BOK_MNGR_POSTS ADD COLUMN LIKE_COUNT INTEGER DEFAULT 0");
            logger.info("--- LIKE_COUNT 컬럼 추가 완료.");
        } catch (Exception e) {
            logger.info("--- LIKE_COUNT 컬럼이 이미 존재하여 추가를 건너뜁니다.");
        }
    }

    @Override
    public int insertItem(BokManagerPostDto input) {
        String sql = "\n\n\tINSERT INTO BOK_MNGR_POSTS (PARENT_SEQ, USER_ID, CONTENTS, PASSWORD) VALUES (?, ?, ?, ?)";
        logger.info("--- SQL: {}\n", sql);
        int result = jdbcTemplate.update(conn -> {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            if (input.getParentSeq() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, input.getParentSeq());
            }
            ps.setString(2, input.getUserId());
            ps.setString(3, input.getContents());
            if (input.getPassword() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, input.getPassword());
            }
            return ps;
        });
        logger.info("--- 게시물 삽입 완료. result=[{}]", result);
        return result;
    }

    @Override
    public int updateItem(BokManagerPostDto input) {
        String sql = "\n\n\tUPDATE BOK_MNGR_POSTS SET CONTENTS = ? WHERE SEQ = ?";
        logger.info("--- SQL: {}\n", sql);
        int result = jdbcTemplate.update(sql, input.getContents(), input.getSeq());
        logger.info("--- 게시물 수정 완료. seq=[{}], result=[{}]", input.getSeq(), result);
        return result;
    }

    @Override
    public int likeItem(int seq) {
        String sql = "\n\n\tUPDATE BOK_MNGR_POSTS SET LIKE_COUNT = LIKE_COUNT + 1 WHERE SEQ = ?";
        logger.info("--- SQL: {}\n", sql);
        int result = jdbcTemplate.update(sql, seq);
        logger.info("--- 좋아요 반영 완료. seq=[{}], result=[{}]", seq, result);
        return result;
    }

    @Override
    public BokManagerPostDto selectItem(int seq) {
        String sql = "\n\n\tSELECT SEQ, PARENT_SEQ, USER_ID, CONTENTS, CREATED_AT, PASSWORD, LIKE_COUNT FROM BOK_MNGR_POSTS WHERE SEQ = ?";
        logger.info("--- SQL: {}\n", sql);
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), seq);
        } catch (Exception e) {
            logger.error("Error fetching post with seq {}: {}", seq, e.getMessage());
            return null;
        }
    }

    @Override
    public List<BokManagerPostDto> getFeedItems() {
        /* 최상위 게시물 목록과 각 게시물의 (대)댓글 수를 함께 조회한다 */
        String sql = "\n\n\tSELECT p.SEQ, p.PARENT_SEQ, p.USER_ID, p.CONTENTS, p.CREATED_AT, p.PASSWORD, p.LIKE_COUNT, "
                + "\n\t\t(SELECT COUNT(*) FROM BOK_MNGR_POSTS r WHERE r.PARENT_SEQ = p.SEQ) AS REPLY_COUNT "
                + "\n\tFROM BOK_MNGR_POSTS p WHERE p.PARENT_SEQ IS NULL ORDER BY p.CREATED_AT DESC";
        logger.info("--- SQL: {}\n", sql);
        List<BokManagerPostDto> items = jdbcTemplate.query(sql, (rs, rowNum) -> {
            BokManagerPostDto dto = mapRow(rs);
            dto.setReplyCount(rs.getInt("REPLY_COUNT"));
            return dto;
        });
        logger.info("--- 게시물 목록 조회 건수=[{}]", items.size());
        return items;
    }

    @Override
    public List<BokManagerPostDto> getThreadItems(int rootSeq) {
        /* 재귀 CTE로 루트 게시물과 그에 딸린 모든 (대)댓글을 함께 조회한다 */
        String sql = "\n\n\tWITH RECURSIVE THREAD(SEQ, PARENT_SEQ, USER_ID, CONTENTS, CREATED_AT, PASSWORD, LIKE_COUNT) AS ("
                + "\n\t\tSELECT SEQ, PARENT_SEQ, USER_ID, CONTENTS, CREATED_AT, PASSWORD, LIKE_COUNT FROM BOK_MNGR_POSTS WHERE SEQ = ?"
                + "\n\t\tUNION ALL"
                + "\n\t\tSELECT p.SEQ, p.PARENT_SEQ, p.USER_ID, p.CONTENTS, p.CREATED_AT, p.PASSWORD, p.LIKE_COUNT"
                + "\n\t\tFROM BOK_MNGR_POSTS p INNER JOIN THREAD t ON p.PARENT_SEQ = t.SEQ"
                + "\n\t)"
                + "\n\tSELECT * FROM THREAD ORDER BY CREATED_AT ASC";
        logger.info("--- SQL: {}\n", sql);
        List<BokManagerPostDto> items = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), rootSeq);
        logger.info("--- 게시물 쓰레드 조회 건수=[{}], rootSeq=[{}]", items.size(), rootSeq);
        return items;
    }

    @Override
    public int deleteItem(int seq) {
        String sql = "\n\n\tDELETE FROM BOK_MNGR_POSTS WHERE SEQ = ?";
        logger.info("--- SQL: {}\n", sql);
        int result = jdbcTemplate.update(sql, seq);
        logger.info("--- 게시물 삭제 완료. seq=[{}], result=[{}]", seq, result);
        return result;
    }

    @Override
    public int deleteThreadItems(int rootSeq) {
        /* 루트 게시물과 그에 딸린 모든 (대)댓글을 함께 삭제한다 */
        List<BokManagerPostDto> thread = getThreadItems(rootSeq);
        int result = 0;
        for (BokManagerPostDto item : thread) {
            result += deleteItem(item.getSeq());
        }
        logger.info("--- 게시물 쓰레드 삭제 완료. rootSeq=[{}], deletedCount=[{}]", rootSeq, result);
        return result;
    }

    private BokManagerPostDto mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        BokManagerPostDto dto = new BokManagerPostDto();
        dto.setSeq(rs.getInt("SEQ"));
        int parentSeq = rs.getInt("PARENT_SEQ");
        dto.setParentSeq(rs.wasNull() ? null : parentSeq);
        dto.setUserId(rs.getString("USER_ID"));
        dto.setContents(rs.getString("CONTENTS"));
        dto.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
        dto.setPassword(rs.getString("PASSWORD"));
        dto.setLikeCount(rs.getInt("LIKE_COUNT"));
        return dto;
    }
}
