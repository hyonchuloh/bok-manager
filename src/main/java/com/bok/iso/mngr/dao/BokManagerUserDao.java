package com.bok.iso.mngr.dao;

import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

public interface BokManagerUserDao {

    public void initTable();

    public BokManagerUserDto selectId(String userId);
    public int insertId(String userId, String userPw, String userName, String userEmail);
    public int deleteId(String seq);
    public int updateId(String seq, String UserId, String userPw, String userName, String userEmail);


}
