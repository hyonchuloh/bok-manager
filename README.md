# Bok Manager

Bok Manager는 Spring Boot 기반의 웹 애플리케이션으로 오현철 과장이 개인적으로 사용하는 프로그램입니다.
게시판, 캘린더, 전화번호부, 사용자 관리 등의 기능을 제공합니다.

## 기능

- **게시판 관리**: 게시글 작성, 조회, 수정, 삭제
- **캘린더**: 월간 및 주간 뷰로 일정 관리, 휴일 표시
- **전화번호부**: 연락처 관리 및 엑셀 파일 업로드 기능
- **사용자 관리**: 사용자 정보 관리
- **로그인**: 사용자 인증

## 기술 스택

- **Backend**: Spring Boot 3.2.1, Java 17
- **Database**: SQLite (H2 지원)
- **Frontend**: JSP, JSTL
- **Build Tool**: Maven
- **Server**: Tomcat (Embedded)
- **기타**: Apache POI (엑셀 처리)

## 설치 및 실행

### 사전 요구사항

- Java 17 이상
- Maven 3.6 이상

### 실행 방법

1. 프로젝트 클론 또는 다운로드
2. 프로젝트 루트 디렉토리로 이동
3. 의존성 설치 및 실행

   ```bash
   ./mvnw spring-boot:run
   ```

   또는 Windows의 경우:

   ```bash
   mvnw.cmd spring-boot:run
   ```

4. 브라우저에서 `https://localhost:10443` 접속 (SSL 활성화)

### 프로필

- `server`: 프로덕션용 프로필 (기본)
- `local`: 로컬 개발용 프로필 (디버그 로깅)

프로필 변경 시:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## 빌드

WAR 파일로 빌드:

```bash
./mvnw clean package
```

빌드된 WAR 파일은 `target/` 디렉토리에 생성됩니다.

## 데이터베이스

- 기본 데이터베이스: SQLite (`bok-manager.db`)
- H2 데이터베이스도 지원

## 설정

`src/main/resources/application.yml` 파일에서 설정을 변경할 수 있습니다.

- 서버 포트: 10443
- SSL 설정: PFX 키스토어 사용
- 세션 타임아웃: 360분

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/bok/iso/mngr/
│   │   ├── BokManagerApplication.java
│   │   ├── ctl/          # 컨트롤러
│   │   ├── svc/          # 서비스
│   │   ├── dao/          # 데이터 액세스 객체
│   │   └── dto/          # 데이터 전송 객체
│   ├── resources/
│   │   ├── application.yml
│   │   └── static/       # 정적 리소스 (CSS, 이미지 등)
│   └── webapp/WEB-INF/jsp/  # JSP 뷰
└── test/
    └── java/com/bok/iso/mngr/
        └── BokManagerApplicationTests.java
```

## 기여

기여를 원하시면 이슈를 생성하거나 풀 리퀘스트를 보내주세요.

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
