package com.bok.iso.mngr;

import javax.annotation.PostConstruct;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import com.bok.iso.mngr.svc.BokManagerCallbookSvc;
import com.bok.iso.mngr.svc.BokManagerUserSvc;

@SpringBootApplication
public class BokManagerApplication {

    @Autowired
    private BokManagerCallbookSvc callbookSvc;
    @Autowired
    private BokManagerUserSvc loginSvc;

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BokManagerApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
	}

	@Bean
    public ServletWebServerFactory servletContainer() {
        // Enable SSL Trafic
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
    }



}
