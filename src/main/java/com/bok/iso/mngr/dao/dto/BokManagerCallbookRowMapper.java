package com.bok.iso.mngr.dao.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class BokManagerCallbookRowMapper implements RowMapper<BokManagerCallbookDto> {

    @Override
    public BokManagerCallbookDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        BokManagerCallbookDto retValue = new BokManagerCallbookDto
            (rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
        return retValue;
    }


}
