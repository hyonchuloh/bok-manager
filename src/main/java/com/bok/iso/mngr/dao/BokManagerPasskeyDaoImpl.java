package com.bok.iso.mngr.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto;

@Repository
public class BokManagerPasskeyDaoImpl implements BokManagerPasskeyDao {

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerPasskeyDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS BOK_MNGR_PASSKEYS (USER_ID, CREDENTIAL_ID PRIMARY KEY, PUBLIC_KEY, SIGN_COUNT INTEGER)";
        logger.info("--- passkey table init SQL=[{}]", sql);
        jdbcTemplate.update(sql);
    }

    @Override
    public int insertPasskey(BokManagerPasskeyDto passkey) {
        String sql = "INSERT OR REPLACE INTO BOK_MNGR_PASSKEYS (USER_ID, CREDENTIAL_ID, PUBLIC_KEY, SIGN_COUNT) VALUES (?, ?, ?, ?)";
        logger.info("--- insertPasskey credential=[{}] userId=[{}] signCount=[{}]", passkey.getCredentialId(), passkey.getUserId(), passkey.getSignCount());
        return jdbcTemplate.update(sql, passkey.getUserId(), passkey.getCredentialId(), passkey.getPublicKey(), passkey.getSignCount());
    }

    @Override
    public BokManagerPasskeyDto selectByCredentialId(String credentialId) {
        String sql = "SELECT USER_ID, CREDENTIAL_ID, PUBLIC_KEY, SIGN_COUNT FROM BOK_MNGR_PASSKEYS WHERE CREDENTIAL_ID=?";
        logger.info("--- selectByCredentialId credentialId=[{}]", credentialId);
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                BokManagerPasskeyDto result = new BokManagerPasskeyDto();
                result.setUserId(rs.getString("USER_ID"));
                result.setCredentialId(rs.getString("CREDENTIAL_ID"));
                result.setPublicKey(rs.getString("PUBLIC_KEY"));
                result.setSignCount(rs.getLong("SIGN_COUNT"));
                return result;
            }, credentialId);
        } catch (Exception e) {
            logger.info("--- selectByCredentialId not found [{}]", credentialId);
            return null;
        }
    }

    @Override
    public List<BokManagerPasskeyDto> selectByUserId(String userId) {
        String sql = "SELECT USER_ID, CREDENTIAL_ID, PUBLIC_KEY, SIGN_COUNT FROM BOK_MNGR_PASSKEYS WHERE USER_ID=?";
        logger.info("--- selectByUserId userId=[{}]", userId);
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                BokManagerPasskeyDto result = new BokManagerPasskeyDto();
                result.setUserId(rs.getString("USER_ID"));
                result.setCredentialId(rs.getString("CREDENTIAL_ID"));
                result.setPublicKey(rs.getString("PUBLIC_KEY"));
                result.setSignCount(rs.getLong("SIGN_COUNT"));
                return result;
            }, userId);
        } catch (Exception e) {
            logger.error("--- selectByUserId error", e);
            return java.util.Collections.emptyList();
        }
    }
}
