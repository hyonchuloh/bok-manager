package com.bok.iso.mngr;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.bok.iso.mngr.svc.BokManagerBoardSvc;
import com.bok.iso.mngr.svc.BokManagerCalendarSvc;
import com.bok.iso.mngr.svc.BokManagerCallbookSvc;
import com.bok.iso.mngr.svc.BokManagerUserSvc;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BokManagerApplication {

    private final BokManagerCallbookSvc callbookSvc;
    private final BokManagerUserSvc loginSvc;
    private final BokManagerCalendarSvc calendarSvc;
    private final BokManagerBoardSvc boardSvc;

    BokManagerApplication(BokManagerUserSvc loginSvc, BokManagerCallbookSvc callbookSvc, BokManagerCalendarSvc calendarSvc, BokManagerBoardSvc boardSvc) {
        this.loginSvc = loginSvc;
        this.callbookSvc = callbookSvc;
        this.calendarSvc = calendarSvc;
        this.boardSvc = boardSvc;
    }

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BokManagerApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
	}
 
	@Bean
    @Profile("server")
    public ServletWebServerFactory servletContainer() {
        // Enable SSL Trafic for non-local environments
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        return tomcat;
    }

    @PostConstruct
    public void initDatabase() {
        callbookSvc.initTable();
        loginSvc.initTable();
        loginSvc.initPasskeyTable();
        calendarSvc.initTable();
        boardSvc.initTable();
        /*
        List<String> list = calendarSvc.selectAll();
        for ( String temp : list ) {
            System.out.println("\t" + temp);
        } */
    }

}
