# Bok Manager

Bok Manager는 Spring Boot 기반의 웹 애플리케이션으로 오현철 과장이 개인적으로 사용하는 업무관리 프로그램입니다.
게시판, 캘린더, 전화번호부, 사용자 관리 등의 기능을 제공합니다.

## 기능

- **게시판 관리**: 게시글 작성/조회/수정/삭제, 마크다운 렌더링, 글꼴·크기·줄간격·자간 설정, 비밀글(로그인 비밀번호로 열람할 때마다 확인)
- **캘린더**: 월간/평일(주간)/연간 뷰, 휴일·휴가 표시, 날짜별 메모, 검색, 엑셀 다운로드
- **전화번호부**: 연락처 관리 및 엑셀 파일 업로드
- **사용자 관리**: 계정 등록/수정/삭제, 패스키(WebAuthn) 등록 현황 조회 및 삭제
- **로그인**: 아이디/비밀번호 로그인 및 패스키(WebAuthn) 로그인

## 기술 스택

- **Backend**: Spring Boot 3.2.12, Java 17
- **Database**: SQLite
- **Frontend**: JSP, JSTL
- **Build Tool**: Maven
- **Server**: Tomcat (Embedded)
- **기타**: Apache POI (엑셀 처리)

### 사전 요구사항

- Java 17 이상
- Maven 3.6 이상 (또는 저장소에 포함된 `./mvnw` 사용)

### 실행 방법

```bash
./mvnw spring-boot:run
```

기본 활성 프로필은 `local`이며, SSL 없이 `http://localhost:10443`에서 구동됩니다.

### 프로필

- `local`: 로컬 개발용 프로필 (기본, SSL 비활성화, 포트 10443)
- `server`: 프로덕션용 프로필 (SSL 활성화, 포트 10443, PFX 키스토어 필요)

`server` 프로필로 실행 시:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=server
```

이 경우 저장소에는 포함되지 않는 키스토어 파일(`ohhyonchul_com.pfx`)이 필요하며, 브라우저에서 `https://localhost:10443`으로 접속합니다.

## 빌드

WAR 파일로 빌드:

```bash
./mvnw clean package
```

빌드된 WAR 파일은 `target/` 디렉토리에 생성됩니다.

## 데이터베이스

- SQLite 파일(`bok-manager.db`)을 사용하며, 애플리케이션 최초 기동 시 필요한 테이블이 자동으로 생성됩니다.
- 데이터베이스 파일은 개인 데이터를 포함하므로 `.gitignore`에 등록되어 저장소에 커밋되지 않습니다.

## 설정

- 서버 포트: 10443
- SSL 설정: `server` 프로필에서만 PFX 키스토어 사용 (`local` 프로필은 SSL 비활성화)
- 세션 타임아웃: 360분

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/ohc/bok/mngr/
│   │   ├── BokManagerApplication.java
│   │   ├── ctl/          # 컨트롤러
│   │   ├── svc/          # 서비스
│   │   └── dao/          # 데이터 액세스 객체 및 dto/ 데이터 전송 객체
│   ├── resources/
│   │   ├── application.properties (+ -local, -server 프로필별 설정)
│   │   └── static/       # 정적 리소스 (CSS, JS, 이미지 등)
│   └── webapp/WEB-INF/jsp/  # JSP 뷰
└── test/
    └── java/com/ohc/bok/mngr/
        └── BokManagerApplicationTests.java
```

## 참고한 사이트

https://favicon.io/favicon-converter/
