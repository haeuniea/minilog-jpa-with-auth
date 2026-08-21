# Minilog JPA with Auth

Spring Boot와 JPA를 활용하여 구현한 마이크로블로그 REST API 프로젝트입니다.

기존 Minilog JPA 프로젝트의 사용자, 게시글, 팔로우, 피드 기능에  
Spring Security와 JWT를 적용하여 사용자 인증 및 권한 기반 인가 기능을 추가했습니다.

## 개발 환경

- Java 21
- Spring Boot 4.1.0
- Gradle
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
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
│   ├── ApiDocumentationConfig
│   └── SecurityConfig
├── controller
│   ├── AuthenticationController
│   ├── UserController
│   ├── ArticleController
│   ├── FollowController
│   └── FeedController
├── dto
│   ├── AuthenticationRequestDto
│   ├── AuthenticationResponseDto
│   ├── UserRequestDto
│   ├── UserResponseDto
│   ├── ArticleRequestDto
│   ├── ArticleResponseDto
│   ├── FollowRequestDto
│   └── FollowResponseDto
├── entity
│   ├── User
│   ├── Article
│   ├── Follow
│   └── Role
├── exception
│   ├── UserNotFoundException
│   ├── ArticleNotFoundException
│   ├── NotAuthorizedException
│   └── GlobalExceptionHandler
├── repository
│   ├── UserRepository
│   ├── ArticleRepository
│   └── FollowRepository
├── security
│   ├── JwtAuthenticationEntryPoint
│   ├── JwtRequestFilter
│   ├── JwtUtil
│   ├── MinilogGrantedAuthority
│   ├── MinilogUserDetails
│   └── MinilogUserDetailsService
├── service
│   ├── UserService
│   ├── ArticleService
│   └── FollowService
├── util
│   └── EntityDtoMapper
└── MinilogApplication
```

## 계층 구조

기존 Minilog JPA 프로젝트의 Controller → Service → Repository 구조에  
Spring Security 기반의 인증 및 인가 과정이 추가되었습니다.

```text
Client
  ↓ HTTP Request + JWT
Spring Security
  ↓
JWT 인증 / 인가
  ↓
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

JWT 인증에 성공하면 사용자의 인증 정보를 `SecurityContext`에 저장하고,  
Controller에서는 `@AuthenticationPrincipal`을 이용하여 현재 로그인한 사용자 정보를 사용할 수 있습니다.

### 각 계층의 역할

- **Security**: JWT 생성·검증 및 사용자 인증 처리
- **Controller**: HTTP 요청 및 응답 처리, 인증된 사용자 정보 활용
- **Service**: 비즈니스 로직 및 데이터 소유권 기반 인가 처리
- **Repository**: JPA를 이용한 데이터베이스 접근
- **Entity**: 데이터베이스 테이블 및 연관관계 정의
- **DTO**: API 요청 및 응답에 필요한 데이터 전달
- **Mapper**: Entity와 DTO 간 변환

## 주요 기능

### 인증

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| POST | `/api/v2/auth/login` | 로그인 및 JWT 발급 |

### 사용자

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| GET | `/api/v2/user` | 전체 사용자 조회 |
| GET | `/api/v2/user/{userId}` | 특정 사용자 조회 |
| POST | `/api/v2/user` | 사용자 생성 |
| PUT | `/api/v2/user/{userId}` | 사용자 수정 |
| DELETE | `/api/v2/user/{userId}` | 사용자 삭제 |

### 게시글

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| POST | `/api/v2/article` | 게시글 생성 |
| GET | `/api/v2/article/{articleId}` | 게시글 조회 |
| PUT | `/api/v2/article/{articleId}` | 게시글 수정 |
| DELETE | `/api/v2/article/{articleId}` | 게시글 삭제 |
| GET | `/api/v2/article?authorId={authorId}` | 특정 사용자의 게시글 조회 |

### 팔로우

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| POST | `/api/v2/follow` | 사용자 팔로우 |
| DELETE | `/api/v2/follow/{followeeId}` | 언팔로우 |
| GET | `/api/v2/follow/{followerId}` | 팔로잉 목록 조회 |

팔로우 및 언팔로우 시 `followerId`를 클라이언트가 직접 전달하지 않고,  
`@AuthenticationPrincipal`을 통해 현재 로그인한 사용자의 ID를 사용합니다.

### 피드

| HTTP Method | Endpoint | 기능 |
| --- | --- | --- |
| GET | `/api/v2/feed?followerId={followerId}` | 팔로우한 사용자의 게시글 피드 조회 |

## 주요 구현 내용

- Spring Security를 이용한 인증 및 인가 구현
- JWT 생성 및 검증
- 로그인 성공 시 JWT 발급
- `JwtRequestFilter`를 이용한 JWT 인증 처리
- `SecurityContext`를 이용한 인증 정보 관리
- `UserDetails`, `UserDetailsService`, `GrantedAuthority` 구현
- BCrypt를 이용한 비밀번호 해시 저장 및 검증
- `SecurityFilterChain`을 이용한 엔드포인트 접근 제어
- `@AuthenticationPrincipal`을 이용한 현재 로그인 사용자 조회
- `@PreAuthorize`를 이용한 권한 기반 접근 제어
- `ROLE_AUTHOR`, `ROLE_ADMIN` 사용자 권한 관리
- 게시글 수정 및 삭제 시 작성자 권한 검증
- 인증 실패 시 `401 Unauthorized` 처리
- Swagger UI에서 JWT 인증 지원
- MockMvc, Mockito, Testcontainers를 이용한 테스트

## JWT 인증 흐름

### 회원가입

사용자의 비밀번호는 평문으로 저장하지 않고 BCrypt를 이용하여 해시 형태로 저장합니다.

```text
Client
  ↓
사용자 생성
  ↓
BCrypt 비밀번호 해시
  ↓
MySQL 저장
```

### 로그인

사용자가 username과 password로 로그인하면 Spring Security가 사용자 정보를 조회하고 비밀번호를 검증합니다.

```text
Client
  ↓ username / password
AuthenticationController
  ↓
AuthenticationManager
  ↓
UserDetailsService
  ↓
DB 사용자 조회
  ↓
PasswordEncoder 비밀번호 검증
  ↓
인증 성공
  ↓
JwtUtil
  ↓
JWT 발급
```

### 인증 이후 요청

로그인 이후 클라이언트는 발급받은 JWT를 `Authorization` 헤더에 포함하여 요청합니다.

```text
Client
  ↓ Authorization: Bearer JWT
JwtRequestFilter
  ↓
JWT 검증
  ↓
UserDetailsService로 사용자 조회
  ↓
Authentication 생성
  ↓
SecurityContext 저장
  ↓
인증 / 인가
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
MySQL
```

게시글 생성, 수정, 삭제와 같이 현재 사용자의 정보가 필요한 기능에서는  
`@AuthenticationPrincipal`을 통해 인증된 사용자의 ID를 사용합니다.

## 인증 및 인가

### 인증 (Authentication)

현재 요청을 보낸 사용자가 누구인지 확인합니다.

```text
username / password
→ 로그인 인증
→ JWT 발급

JWT
→ JWT 검증
→ Authentication
→ SecurityContext
```

### 인가 (Authorization)

인증된 사용자가 해당 작업을 수행할 권한이 있는지 확인합니다.

- `SecurityFilterChain`: URL 수준의 접근 권한 관리
- `@PreAuthorize`: 메서드 수준의 권한 검사
- Service: 게시글 작성자 등 실제 데이터의 소유권 검사

사용자 권한은 다음과 같이 구분합니다.

- `ROLE_AUTHOR`
- `ROLE_ADMIN`

예를 들어 사용자 삭제 API는 `ROLE_ADMIN` 권한이 있는 사용자만 실행할 수 있습니다.

## 실행 방법

### 1. MySQL 실행

Docker를 이용하여 MySQL 컨테이너를 실행합니다.

```bash
docker start mysql-minilog
```

> 최초 실행 시에는 프로젝트에서 사용하는 MySQL 컨테이너를 먼저 생성해야 합니다.
> Minilog용 Docker MySQL은 호스트의 3308 포트를 사용합니다.

>기존 Minilog JPA에서 생성한 사용자의 비밀번호는 BCrypt가 적용되어 있지 않으므로
>기존 사용자 데이터를 삭제하고 새로운 사용자를 생성하여 테스트합니다.

### 2. 프로젝트 빌드

WSL2 터미널에서 프로젝트 디렉토리로 이동하여 빌드합니다.

```bash
cd ~/eog-springboot4/minilog-jpa-with-auth
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

## JWT 인증 테스트

1. Swagger UI에서 `POST /api/v2/user`를 이용하여 신규 사용자를 생성합니다.
2. `POST /api/v2/auth/login`으로 로그인합니다.
3. 로그인 응답으로 발급된 JWT를 복사합니다.
4. Swagger UI 상단의 **Authorize** 버튼을 클릭합니다.
5. `bearerAuth`에 발급받은 JWT를 입력합니다.
6. 인증이 필요한 API를 테스트합니다.

## 테스트

전체 테스트는 다음 명령어로 실행합니다.

```bash
gradle test
```

주요 테스트 내용은 다음과 같습니다.

- **Controller 테스트**
    - MockMvc를 이용한 API 요청 및 응답 검증
    - 사용자, 게시글, 피드 API의 상태 코드 및 응답 데이터 확인

- **Service 테스트**
    - 게시글 생성, 조회, 수정, 삭제 등의 비즈니스 로직 검증
    - 팔로우 기반 피드 조회 동작 확인

- **Entity 테스트**
    - 사용자 Entity의 생성 및 동작 확인
    - Entity 연관관계 동작 확인

- **Application Context 테스트**
    - Spring Application Context가 정상적으로 로드되는지 확인

## 학습 내용

이 프로젝트를 통해 기존 Minilog JPA 프로젝트의 JPA 구조에 더해 다음 내용을 학습했습니다.

- Spring Security의 인증(Authentication)과 인가(Authorization)
- `UserDetails`, `UserDetailsService`
- `GrantedAuthority`
- `Authentication`과 `SecurityContext`
- `AuthenticationManager`
- `PasswordEncoder`와 BCrypt를 이용한 비밀번호 해시
- JWT 생성 및 검증
- JWT 기반 Stateless 인증
- `JwtRequestFilter`
- `SecurityFilterChain`
- `@AuthenticationPrincipal`
- `@PreAuthorize`
- 역할(Role) 기반 권한 관리
- 데이터 소유권 기반 인가 처리
- Swagger UI를 이용한 JWT 인증 테스트
- MockMvc / Mockito를 이용한 Controller 테스트
- Testcontainers를 이용한 데이터베이스 연동 테스트

## 참고

이 프로젝트는 『스프링 부트 개발자 온보딩 가이드』의 예제를 참고하여 학습 목적으로 구현했습니다.

교재의 예제를 기반으로 하되, 현재 학습 환경에 맞게 다음 사항을 변경하여 진행했습니다.

- Spring Boot 3 → Spring Boot 4.1.0
- Spring Boot 버전 변경에 따른 일부 의존성 및 코드 수정