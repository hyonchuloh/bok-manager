package com.bok.iso.mngr.svc;

import jakarta.servlet.http.HttpSession;

import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

public interface BokManagerUserSvc {

    public boolean isAuthentication(HttpSession session);

    public void setSessionForUserId(HttpSession session, String userId);
    public void removeSessionForUserId(HttpSession session);
    public String getUserId(HttpSession session);
    public java.util.List<BokManagerUserDto> selectAll();

    public void initTable();
    public BokManagerUserDto selectId(String userId);
    public int insertId(String userId, String userPw, String userEmail);
    public int deleteId(String userId);
    public int updateId(String userId, String userPw, String userEmail);

}
