package com.bok.iso.mngr;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.bok.iso.mngr.svc.BokManagerBoardSvc;
import com.bok.iso.mngr.svc.BokManagerCalendarSvc;
import com.bok.iso.mngr.svc.BokManagerCallbookSvc;
import com.bok.iso.mngr.svc.BokManagerPasskeySvc;
import com.bok.iso.mngr.svc.BokManagerUserSvc;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BokManagerApplication {

    private final BokManagerCallbookSvc callbookSvc;
    private final BokManagerUserSvc loginSvc;
    private final BokManagerPasskeySvc passkeySvc;
    private final BokManagerCalendarSvc calendarSvc;
    private final BokManagerBoardSvc boardSvc;

    BokManagerApplication(BokManagerUserSvc loginSvc, BokManagerPasskeySvc passkeySvc, BokManagerCallbookSvc callbookSvc, BokManagerCalendarSvc calendarSvc, BokManagerBoardSvc boardSvc) {
        this.loginSvc = loginSvc;
        this.passkeySvc = passkeySvc;
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

    /**
     * TLD 스캔 최적화.
     * 내장 Tomcat은 기동 시 모든 JAR를 열어 TLD(JSP 커스텀 태그 정의 파일)를 찾는다.
     * 이 프로젝트에서 TLD가 실제로 들어있는 JAR은 JSTL 구현체와 spring-webmvc 둘뿐이고,
     * 나머지(POI/SQLite/H2/Jackson 등 수십 개)에는 TLD가 없어 스캔이 낭비다.
     * 기본을 "스캔 안 함"으로 두고 위 두 JAR만 스캔해 기동/JSP 컴파일 속도를 높인다.
     * (WebServerFactoryCustomizer는 사용 중인 TomcatServletWebServerFactory에
     *  자동 적용되므로 local·server 프로파일 모두에 반영된다.)
     *
     * 주의: 앞으로 TLD를 가진 의존성을 추가하면 아래 패턴 목록에도 그 JAR를 넣어야 한다.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tldScanOptimizer() {
        return factory -> factory.addContextCustomizers(context -> {
            StandardJarScanFilter filter = new StandardJarScanFilter();
            filter.setDefaultTldScan(false);
            filter.setTldScan("jakarta.servlet.jsp.jstl-*.jar,spring-webmvc-*.jar");
            context.getJarScanner().setJarScanFilter(filter);
        });
    }

    @PostConstruct
    public void initDatabase() {
        callbookSvc.initTable();
        loginSvc.initTable();
        passkeySvc.initTable();
        calendarSvc.initTable();
        boardSvc.initTable();
    }

}
