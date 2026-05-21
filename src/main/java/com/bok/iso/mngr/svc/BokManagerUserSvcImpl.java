package com.bok.iso.mngr.svc;

import java.util.Arrays;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerUserDao;
import com.bok.iso.mngr.dao.dto.BokManagerUserDto;

@Service
public class BokManagerUserSvcImpl implements BokManagerUserSvc {

    @Autowired
    private BokManagerUserDao dao;

    @Autowired
    private Environment env;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());	

    /**
     * 세션에 userId가 존재하는지 검증한다.
     * 로컬 환경에서는 userId가 없으면 "ohhyonchul"로 세션을 설정하여 인증된 상태로 간주한다.
     * 이는 개발 편의를 위한 것으로, 실제 운영 환경에서는 반드시 인증 절차를 거쳐야 한다.
     */
    @Override
    public boolean isAuthentication(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if ( userId != null && userId.trim().length() > 0 ) {
            return true;
        }
        /* 로컬 환경 검증 */
        boolean isLocal = Arrays.stream(env.getActiveProfiles()).anyMatch("local"::equals);
        if ( isLocal ) {
            session.setAttribute("userId", "ohhyonchul");
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
