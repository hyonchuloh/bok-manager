package com.bok.iso.mngr.dao;

import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

public interface BokManagerUserDao {

    public void initTable();

    public BokManagerUserDto selectId(String userId);
    public java.util.List<BokManagerUserDto> selectAll();
    public int insertId(String userId, String userPw, String userEmail);
    public int deleteId(String userId);
    public int updateId(String userId, String userPw, String userEmail);


}
