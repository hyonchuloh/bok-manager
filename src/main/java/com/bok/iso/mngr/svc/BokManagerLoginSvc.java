package com.bok.iso.mngr.svc;
import javax.servlet.http.HttpSession;

public interface BokManagerLoginSvc {

    public boolean isAuthentication(HttpSession session);

    public void setSessionForUserId(HttpSession session, String userId);
    public void removeSessionForUserId(HttpSession session);
    public String getUserId(HttpSession session);

}
