### **[Step 1] 환경 구축 및 인프라 (기반 잡기)**

- [x] **Spring Boot 프로젝트 생성:** Java 17+, Spring Boot 3.1+ 버전으로 프로젝트 생성.
    
- [x] **의존성(Dependencies) 추가:** `Web`, `Data JPA`, `PostgreSQL`, `Docker Compose Support`, `Mustache`, `Lombok`, `Kafka` 설정.
    
- [x] **보안 환경 설정:** 프로젝트 루트에 `.env` 파일을 생성하고 DB 접속 정보(`USER`, `PASSWORD`, `DB_NAME`) 정의.
    
- [x] **Docker Compose 구성:** `docker-compose.yml` 작성 및 PostgreSQL/Kafka 컨테이너 설정.
    
- [x] **연동 테스트:** 애플리케이션 실행 시 도커 컨테이너가 자동으로 뜨고 DB에 연결되는지 확인.
    

### **[Step 2] 로깅 및 데이터 도메인 설계 (운영 준비)**

- [ ] **UsageLog 엔티티 개발:** `basePackage`, `projectName`, `selectedOptions`(JSONB) 등을 담는 엔티티 작성.
    
- [ ] **Kafka Producer 구현:** 프로젝트 생성 요청 시 이벤트를 발행하는 서비스 로직 작성.
    
- [ ] **Kafka Consumer & Repository:** 이벤트를 구독하여 DB에 이용 기록을 저장하는 기능 구현.
    

### **[Step 3] 코드 템플릿 설계 (설계도 작성)**

- [ ] **Base 엔티티 템플릿:** `Member.java.mustache` 작성 (필드 및 JPA 어노테이션 포함).
    
- [ ] **Security 템플릿:** `SecurityConfig.java.mustache` 작성 (OAuth2 설정 포함).
    
- [ ] **설정 파일 템플릿:** `application.yml.mustache`, `build.gradle.mustache` 등 프로젝트 기본 설정 파일 작성.
    

### **[Step 4] Generator 핵심 엔진 개발 (핵심 로직)**

- [ ] **Mustache Render 서비스:** 사용자 입력값(DTO)을 템플릿에 주입하여 자바 코드로 변환하는 엔진 구현.
    
- [ ] **File Archiver 서비스:** 생성된 코드 파일들을 메모리 내에서 `.zip` 파일로 압축하는 로직 구현.
    
- [ ] **Exception Handling:** 잘못된 패키지명이나 옵션 선택 시에 대한 예외 처리 로직 추가.
    

### **[Step 5] API 및 인터페이스 구현 (접점 만들기)**

- [ ] **Request DTO 정의:** `ProjectRequest` (프로젝트명, 패키지명, 소셜 로그인 종류 등) 클래스 작성.
    
- [ ] **ProjectController 구현:** 생성 요청을 받아 파일 스트림(Zip)을 반환하는 REST 엔드포인트 개발.
    
- [ ] **통합 테스트:** 실제 API 호출 시 `Member` 엔티티가 포함된 완전한 프로젝트 압축 파일이 다운로드되는지 최종 확인.