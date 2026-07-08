# Travel Planner Backend

LG CNS INSPIRE Bootcamp 1차 프로젝트입니다.

지도에 선택한 장소를 중심으로 여행계획을 세워주는 웹 어플리케이션입니다.

본 프로젝트의 Front-End는 https://github.com/KiwiFlavoredApollo/travel-planner 에서 확인 할 수 있습니다

---

## 기술스택

* Java 17
* Spring Boot 3.5.4
* Spring Data JPA
* Spring Security + JWT
* Spring Boot Validation
* Lombok
* SpringDoc OpenAPI (Swagger UI)
* MariaDB
* Redis (Refresh Token 저장)

---

## Project Structure

```
travel-planner-backend/
├── demo/                    # Spring Boot + Java 17
│   ├── src/main/java/com/travelplanner/demo/
│   │   ├── common/          # 공통 설정, 필터, 토큰, 예외처리
│   │   │   ├── config/      # SecurityConfig, RedisConfig, DotEnvEnvironmentPostProcessor
│   │   │   ├── filter/      # JwtAuthenticationFilter
│   │   │   ├── token/       # JwtProvider
│   │   │   ├── service/     # RedisService
│   │   │   └── exception/   # GlobalExceptionHandler
│   │   ├── user/            # 회원 도메인
│   │   │   ├── controller/  # AuthController
│   │   │   ├── service/     # UserService
│   │   │   ├── dto/         # LoginRequest, RegisterRequest, TokenResponse
│   │   │   └── repository/  # UserRepository
│   │   ├── travelplan/      # 여행계획 도메인
│   │   │   ├── controller/  # TravelPlannerController
│   │   │   ├── service/     # TravelPlanService
│   │   │   ├── dto/         # TravelPlanRequest, DestinationRequest
│   │   │   └── repository/  # TravelPlanRepository, DestinationRepository
│   │   └── entity/          # JPA 엔티티 (User, TravelPlan, Destination)
│   └── src/main/resources/
│       ├── application.yml  # 설정 파일 (.env 기반)
│       ├── .env             # 환경변수 (gitignore)
│       └── META-INF/spring.factories  # EnvironmentPostProcessor 등록
└── database/                # SQL 스키마 및 초기 데이터
```

---

## Backend Setup (Spring Boot)

* Java 17
* Spring Boot 3.5.4
* Spring Data JPA
* Spring Security + JWT
* Spring Boot Validation
* Lombok
* SpringDoc OpenAPI 2.7.0

### Run Backend

```bash
cd demo
./gradlew bootRun
```

Access Swagger UI: http://localhost:8000/swagger-ui.html

---

## Database Setup

* MariaDB 11+
* HeidiSQL (recommended GUI) or MariaDB CLI

### Initialize Database

```bash
# MariaDB CLI 사용:
mysql -u root -p < database/schema.sql

# 또는 HeidiSQL에서 직접 실행
```

> **Note:** `ddl-auto: update` 설정으로 엔티티 기반 자동 테이블 생성이 가능합니다. 초기 실행 시 자동 생성됩니다.

---

## Redis Setup

Refresh Token 저장을 위해 Redis가 필요합니다.

```bash
# Docker로 실행 (권장)
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 또는 로컬 설치 후 실행
redis-server
```

---

## Architecture

* Layered Architecture
* Controller → Service → Repository
* Entity-DTO separation
* JWT Filter (Spring Security Filter Chain)
* Configuration classes (SecurityConfig, RedisConfig, DotEnvEnvironmentPostProcessor)
* `.env` 파일 자동 로드 (`dotenv-java` + `EnvironmentPostProcessor`)

---

## Environment Variables

프로젝트 루트(`demo/`)에 `.env` 파일을 생성하세요. 예시:

```bash
# JWT 설정
JWT_SECRET_KEY=travel-planner-secret-key-2026-min-256-bits-length

# DB 설정
DB_URL=jdbc:mariadb://localhost:3306/inspire?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul
DB_USERNAME=root
DB_PASSWORD=5242
DB_DRIVER_CLASS_NAME=org.mariadb.jdbc.Driver
```

> `.env` 파일은 `.gitignore`에 등록되어 있어 Git에 커밋되지 않습니다.
> 개발 환경에서는 위 기본값이 `application.yml`에 설정되어 있어 별도 설정 없이 실행 가능합니다.

---

## API Documentation

Swagger UI에서 전체 API 명세 확인 가능:

```
http://localhost:8000/swagger-ui.html
```

### 주요 엔드포인트

| 구분 | Method | Path | 설명 |
|------|--------|------|------|
| 인증 | POST | /api/v1/auth/register | 회원가입 |
| 인증 | POST | /api/v1/auth/login | 로그인 (Access/Refresh Token 발급) |
| 인증 | POST | /api/v1/auth/logout | 로그아웃 (Refresh Token 삭제) |
| 인증 | GET | /api/v1/auth/user/{userId} | 사용자 조회 |
| 여행계획 | POST | /api/v1/travel-planner | 여행계획 생성 |
| 여행계획 | GET | /api/v1/travel-planner | 여행계획 목록 조회 |
| 여행계획 | GET | /api/v1/travel-planner/{id} | 여행계획 상세 조회 |
| 여행계획 | PUT | /api/v1/travel-planner/{id} | 여행계획 수정 |
| 여행계획 | DELETE | /api/v1/travel-planner/{id} | 여행계획 삭제 |

> 여행계획 API는 `Authorization: Bearer <Access Token>` 헤더 필요

---

## Security

* **JWT Access Token**: 9시간 만료
* **JWT Refresh Token**: 7일 만료 (Redis 저장)
* **화이트리스트** (인증 없이 접근 허용):
  * `/swagger-ui/**`
  * `/v3/api-docs/**`
  * `/api/v1/auth/register`
  * `/api/v1/auth/login`

---

## Development

### 빌드
```bash
./gradlew build -x test
```

### 테스트
```bash
./gradlew test
```

### 코드 포맷팅
Lombok 설정으로 getter/setter/toString 자동 생성

---

## License

MIT License