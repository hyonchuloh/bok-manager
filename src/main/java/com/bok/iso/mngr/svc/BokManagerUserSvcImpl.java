package com.bok.iso.mngr.svc;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerUserDao;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

@Service
public class BokManagerUserSvcImpl implements BokManagerUserSvc {

    @Autowired
    private BokManagerUserDao dao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

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
        int result = dao.deleteId(seq);
        logger.info("--- [deleteId] result=[{}]", result);
        return result;
    }

    @Override
    public int insertId(String userId, String userPw, String userEmail) {
        int result = dao.insertId(userId, userPw, userEmail);
        logger.info("--- [insertId] result=[{}]", result);  
        return result;
    }

    @Override
    public BokManagerUserDto selectId(String userId) {
        BokManagerUserDto result = dao.selectId(userId);
        logger.info("--- [selectId] result=[{}]", result); 
        return result;
    }

    @Override
    public int updateId(String userId, String userPw, String userEmail) {
        int result = dao.updateId(userId, userPw, userEmail);
        logger.info("--- [updateId] result=[{}]", result);
        return result;
    }

    @Override
    public void initTable() {
        dao.initTable();
    }

    @Override
    public java.util.List<BokManagerUserDto> selectAll() {
        java.util.List<BokManagerUserDto> list = dao.selectAll();
        logger.info("--- [selectAll] user count=[{}]", list.size());
        return list;
    }

}
