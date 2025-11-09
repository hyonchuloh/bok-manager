package com.bok.iso.mngr.svc;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerUserDao;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

@Service
public class BokManagerUserSvcImpl implements BokManagerUserSvc {

    @Autowired
    private BokManagerUserDao dao;

    @Override
    public boolean isAuthentication(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if ( userId != null && userId.trim().length() > 0 ) {
            return true;
        }
        return false;
    }

    @Override
    public void removeSessionForUserId(HttpSession session) {
        session.removeAttribute("userId");
    }

    @Override
    public void setSessionForUserId(HttpSession session, String userId) {
        session.setAttribute("userId", userId);
    }

    @Override
    public String getUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }

    @Override
    public int deleteId(String seq) {
        return dao.deleteId(seq);
    }

    @Override
    public int insertId(String userId, String userPw, String userName, String userEmail) {
        return dao.insertId(userId, userPw, userName, userEmail);
    }

    @Override
    public BokManagerUserDto selectId(String userId) {
        return dao.selectId(userId);
    }

    @Override
    public int updateId(String seq, String UserId, String userPw, String userName, String userEmail) {
        return dao.updateId(seq, UserId, userPw, userName, userEmail);
    }

    @Override
    public void initTable() {
        dao.initTable();
    }

}
