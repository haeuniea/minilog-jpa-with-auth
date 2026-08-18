# Minilog JPA

Spring Boot와 JPA를 활용하여 구현한 마이크로블로그 REST API 프로젝트입니다.

MySQL에 사용자, 게시글, 팔로우 데이터를 저장하며  
Controller → Service → Repository 계층 구조와 JPA 연관관계, 트랜잭션, 피드 조회 기능을 학습하기 위해 구현했습니다.

## 개발 환경

- Java 21
- Spring Boot 4.1.0
- Gradle
- Spring Web MVC
- Spring Data JPA
- MySQL 8
- Lombok
- Springdoc OpenAPI (Swagger)
- JUnit 5
- Mockito
- AssertJ
- Testcontainers

## 프로젝트 구조

```text
com.asdf.minilog
├── config
│   └── ApiDocumentationConfig
├── controller
│   ├── UserController
│   ├── ArticleController
│   ├── FollowController
│   └── FeedController
├── dto
│   ├── UserRequestDto
│   ├── UserResponseDto
│   ├── ArticleRequestDto
│   ├── ArticleResponseDto
│   ├── FollowRequestDto
│   └── FollowResponseDto
├── entity
│   ├── User
│   ├── Article
│   └── Follow
├── exception
│   ├── UserNotFoundException
│   ├── ArticleNotFoundException
│   └── GlobalExceptionHandler
├── repository
│   ├── UserRepository
│   ├── ArticleRepository
│   └── FollowRepository
├── service
│   ├── UserService
│   ├── ArticleService
│   └── FollowService
├── util
│   └── EntityDtoMapper
└── MinilogApplication
```

## 계층 구조

```text
Client
  ↓ HTTP Request
Controller
  ↓
Service
  ↓
Repository
  ↓
JPA / Hibernate
  ↓
MySQL
```

조회 결과는 필요에 따라 DTO로 변환하여 반환합니다.

```text
Entity
  ↓
EntityDtoMapper
  ↓
ResponseDto
  ↓
Controller
  ↓
Client
```

### 각 계층의 역할

- **Controller**: HTTP 요청 및 응답 처리
- **Service**: 비즈니스 로직 처리
- **Repository**: JPA를 이용한 데이터베이스 접근
- **Entity**: 데이터베이스 테이블 및 연관관계 정의
- **DTO**: API 요청 및 응답에 필요한 데이터 전달
- **Mapper**: Entity와 DTO 간 변환

## 주요 기능

### 사용자

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| GET | `/api/v1/user` | 전체 사용자 조회 |
| GET | `/api/v1/user/{userId}` | 특정 사용자 조회 |
| POST | `/api/v1/user` | 사용자 생성 |
| PUT | `/api/v1/user/{userId}` | 사용자 수정 |
| DELETE | `/api/v1/user/{userId}` | 사용자 삭제 |

### 게시글

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| POST | `/api/v1/article` | 게시글 생성 |
| GET | `/api/v1/article/{articleId}` | 게시글 조회 |
| PUT | `/api/v1/article/{articleId}` | 게시글 수정 |
| DELETE | `/api/v1/article/{articleId}` | 게시글 삭제 |
| GET | `/api/v1/article?authorId={authorId}` | 특정 사용자의 게시글 조회 |

### 팔로우

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| POST | `/api/v1/follow` | 사용자 팔로우 |
| DELETE | `/api/v1/follow/{followerId}/{followeeId}` | 언팔로우 |
| GET | `/api/v1/follow/{followerId}` | 팔로잉 목록 조회 |

### 피드

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| GET | `/api/v1/feed?followerId={followerId}` | 팔로우한 사용자의 게시글 피드 조회 |


## 주요 구현 내용

- Spring Data JPA를 이용한 CRUD 구현
- JPA Entity 연관관계 설정
- 파생 쿼리를 이용한 사용자 및 팔로우 관계 조회
- JPQL을 이용한 팔로우 기반 게시글 피드 조회
- Request DTO / Response DTO 분리
- EntityDtoMapper를 이용한 Entity → DTO 변환
- `@Transactional`을 이용한 트랜잭션 관리
- JPA Auditing을 이용한 생성일 및 수정일 관리
- `@ControllerAdvice`를 이용한 전역 예외 처리
- Swagger/OpenAPI를 이용한 API 문서화
- MockMvc, Mockito, Testcontainers를 이용한 테스트

## 실행 방법

### 1. MySQL 실행

Docker를 이용하여 MySQL 컨테이너를 실행합니다.

```bash
docker start mysql-minilog
```

> 최초 실행 시에는 프로젝트에서 사용하는 MySQL 컨테이너를 먼저 생성해야 합니다.  
> Minilog용 Docker MySQL은 호스트의 `3308` 포트를 사용합니다.

### 2. 프로젝트 빌드

WSL2 터미널에서 프로젝트 디렉토리로 이동하여 빌드합니다.

```bash
cd ~/eog-springboot4/minilog-jpa
gradle build
```

### 3. 애플리케이션 실행

```bash
gradle bootRun
```

### 4. Swagger UI 접속

애플리케이션 실행 후 브라우저에서 Swagger UI에 접속하여 API를 확인하고 테스트할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

## 테스트

```bash
gradle test
```

- MockMvc / Mockito를 이용한 Controller 테스트
- Testcontainers를 이용한 Service 및 DB 연동 테스트
- CRUD, 피드 조회, 예외 처리 검증

## 학습 내용

이 프로젝트를 통해 다음 내용을 학습했습니다.

- Spring Data JPA와 `JpaRepository`
- JPA Entity 연관관계
- `@OneToMany`, `@ManyToOne`
- 연관관계의 주인과 외래 키
- Cascade 및 `orphanRemoval`
- LAZY / EAGER 로딩
- Spring Data JPA 파생 쿼리
- JPQL
- Entity와 DTO 분리
- Entity-DTO Mapper
- 트랜잭션 관리
- JPA Auditing
- 전역 예외 처리
- Swagger/OpenAPI
- MockMvc / Mockito
- Testcontainers를 이용한 데이터베이스 테스트

## 참고

이 프로젝트는 『스프링 부트 개발자 온보딩 가이드』의 예제를 참고하여 학습 목적으로 구현했습니다.

교재의 예제를 기반으로 하되, 현재 학습 환경에 맞게 다음 사항을 변경하여 진행했습니다.

- Spring Boot 3 → Spring Boot 4.1.0
- Spring Boot 버전 변경에 따른 일부 의존성 및 코드 수정