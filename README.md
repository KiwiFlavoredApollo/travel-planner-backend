# Travel Planner Backend

Spring Boot 3.5.4 기반 여행 계획 생성 백엔드 서버입니다.

## 주요 기능

- **사용자 인증/인가**  
  - JWT 기반 Stateless 인증 (access token 9h, refresh token 7d, 리프레시 토큰 로테이션 + 블랙리스트)  
  - Spring Security 커스텀 필터 (`JwtAuthenticationFilter`)  
  - 회원가입, 로그인, 토큰 리프레시 API  

- **여행 계획 CRUD**  
  - 여행 계획 생성, 조회, 수정, 삭제  
  - 사용자별 여행 계획 목록 조회  

- **AI 기반 일정 추천**  
  - `/api/v1/ai/recommend` 엔드포인트에서 Spring AI (OpenAI)를 활용하여  
    - 입력: 지역, 날짜, 선호도, 동행 인원, 예산, 특이사항  
    - 출력: 일자별 상세 일정 (시간대, 장소, 비용, 팁 등)  
  - 생성된 일정은 `TravelPlanEntity` 및 `DestinationEntity`에 저장  

- **목적지(Destination) 관리**  
  - 각 일정에 장소, 날짜, 시간 저장 (`DestinationEntity.date`, `time`는 문자열)  

- **API 문서화**  
  - SpringDoc OpenAPI 2.7.0 (Swagger UI)  
  - 경로: `http://localhost:8000/swagger-ui.html`  

- **캐싱 및 퍼시스턴스**  
  - MariaDB (JPA + MyBatis 3.0.4)  
  - Redis (JWT 블랙리스트, 토큰 로테이션)  

- **CORS 설정**  
  - 개발용 `*` 허용 (프론트엔드와 분리된 환경에서 토큰 헤더 노출)  
  - 운영 시 구체적 오리진으로 교체 권장  

## 기술 스택

| 카테고리 | 기술 / 버전 |
|----------|-------------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.4 |
| Build Tool | Gradle 8.x |
| ORM | Spring Data JPA, MyBatis 3.0.4 |
| DB | MariaDB 10.5 |
| Cache / Store | Redis 7 |
| AI | Spring AI (OpenAI 1.0.0-M6) + BOM |
| Security | Spring Security, jjwt 0.11.5 |
| API Docs | SpringDoc OpenAPI 2.7.0 |
| Lombok | Lombok (빌드 타임) |
| Env Loading | dotenv-java + `EnvironmentPostProcessor` |
| Validation | Bean Validation (`@Valid`) |

## 프로젝트 구조

```
src/main/java/com/travelplanner/demo/
├─ common/
│   ├─ config/          # SecurityConfig, CorsConfiguration
│   ├─ filter/          # JwtAuthenticationFilter
│   ├─ exception/       # GlobalExceptionHandler, ErrorResponse
│   └─ token/           # JwtProvider, RedisService
├─ user/
│   ├─ entity/          # UserEntity
│   └─ repository/      # UserRepository
├─ travelplan/
│   ├─ controller/      # TravelPlanController
│   ├─ dto/             # TravelPlanRequest, TravelPlanResponse
│   ├─ entity/          # TravelPlanEntity
│   ├─ repository/      # TravelPlanRepository
│   └─ service/         # TravelPlanService
├─ destination/
│   ├─ controller/      # DestinationController (내부는 TravelPlanService 에서 처리)
│   ├─ dto/             # DestinationRequest, DestinationResponse
│   └─ entity/          # DestinationEntity
└─ ai/
    └─ agent/           # TravelPlanAIAgent (Spring AI 호출)
```

## 설정

### 환경 변수 (`.env`)

프로젝트 루트에 `.env` 파일을 생성하고 다음 변수를 정의합니다.

```dotenv
# Database
DB_URL=jdbc:mariadb://host.docker.internal:3306/travelplanner
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis
REDIS_HOST=host.docker.internal
REDIS_PORT=6379

# JWT
JWT_SECRET=your_strong_secret_key
JWT_ACCESS_TOKEN_EXPIRATION_MS=32400000   # 9h
JWT_REFRESH_TOKEN_EXPIRATION_MS=604800000 # 7d

# OpenAI (Spring AI)
OPENAI_API_KEY=your_openai_api_key
# (선택) OPENAI_BASE_URL=https://api.openai.com/v1
```

> **Note**: WSL에서 실행 시 Docker 호스트는 `host.docker.internal` 로 접근할 수 있습니다.  
> 로컬 MariaDB/Redis를 사용할 경우 해당 주소와 포트를 맞게 수정하세요.

### 애플리케이션 속성 (`src/main/resources/application.properties`)

```properties
server.port=8000
spring.application.name=travelplanner-backend

# Lombok
lombok.addGeneratedAnnotation=true

# MyBatis (선택적)
mybatis.configuration.map-underscore-to-camel-case=true

# SpringDoc
springdoc.api-docs.swagger-ui.paths-to-match=/api/**
springdoc.swagger-ui.path=/swagger-ui.html
```

## 빌드 및 실행

### 사전 요구사항

- JDK 17
- Gradle 8.x
- MariaDB (또는 Docker)
- Redis (또는 Docker)

### 로컬 실행

```bash
# 1. 저장소 클론 (이미 클론済み라면 생략)
git clone <repository-url>
cd miniproject_backend/demo

# 2. .env 파일 생성 (위 예시 참고)
cp .env.example .env   # 혹은 수동 생성

# 3. 의존성 다운로드 및 빌드
./gradlew clean build

# 4. 애플리케이션 실행
./gradlew bootRun
```

서버가 `http://localhost:8000` 에서 구동됩니다.

### Docker (선택)

`docker-compose.yml` 예시 (프로젝트 루트에 위치)

```yaml
version: "3.8"
services:
  app:
    build: .
    ports:
      - "8000:8000"
    env_file:
      - .env
    depends_on:
      - db
      - redis
  db:
    image: mariadb:10.5
    environment:
      MYSQL_DATABASE: travelplanner
      MYSQL_ROOT_PASSWORD: example
    ports:
      - "3306:3306"
    volumes:
      - mariadb_data:/var/lib/mysql
  redis:
    image: redis:7
    ports:
      - "6379:6379"
volumes:
  mariadb_data:
```

```bash
docker compose up --build
```

## API 엔드포인트 요약

### 인증

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/auth/register` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 → access & refresh token 반환 |
| POST | `/api/v1/auth/refresh` | refresh token으로 새 access token 발급 |
| POST | `/api/v1/auth/logout` | refresh token 블랙리스트 추가 (로그아웃) |

### 여행 계획

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/travelplans` | 새 여행 계획 생성 (요청에 AI 추천 포함) |
| GET | `/api/v1/travelplans` | 사용자별 여행 계획 목록 조회 |
| GET | `/api/v1/travelplans/{id}` | 특정 여행 계획 상세 조회 |
| PUT | `/api/v1/travelplans/{id}` | 여행 계획 수정 (지역, 날짜, 목적지 변경) |
| DELETE | `/api/v1/travelplans/{id}` | 여행 계획 삭제 |

### AI 추천 (내부 사용)

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/ai/recommend` | 지역, 날짜, 선호도 등을 입력받아 AI가 일정 생성 → JSON 반환 (내부에서 TravelPlanService 가 호출) |

> Swagger UI에서 각 API의 상세 요청/응답 스키마를 확인할 수 있습니다.

## 주요 구현 포인트

- **JWT 필터 중복 방지**  
  `JwtAuthenticationFilter` 에 `@Component` 제거하고 `SecurityConfig` 에서 `new JwtAuthenticationFilter(...)` 로 직접 빈 등록하여 필터가 한 번만 실행되도록 함.

- **UserDetailsService 구현**  
  더미 예외 대신 `UserRepository` 를 기반으로 실제 사용자 조회를 수행하여 향후 `@AuthenticationPrincipal` 또는 역할 기반 확장에 대비.

- **CORS 설정**  
  `CorsConfigurationSource` 빈에서 `setAllowedOriginPatterns(`" 및 `Authorization`, `Refresh-Token`"]으로 프론트엔드에서 토큰ヘッ더를 읽을 수 있도록 함. 운영 시 실제 오리진으로 교체 권장.

- **목적지 날짜/시간 처리**  
  `DestinationRequest` 에 `date` (YYYY-MM-DD) 와 `time` (HH:mm:ss) 필드 추가하고, `TravelPlanService.create` / `updateTravelPlan` 에서 해당 값을 `DestinationEntity`에 매핑하여 하드코딩된 `LocalTime.now()` 를 제거.

- **JPA Cascade 처리**  
  `TravelPlanEntity.destinations` 에 `CascadeType.ALL` 및 `orphanRemoval = true` 를 설정하고, 서비스에서 `clear()` 후 `addDestination()` 만 수행하여 불필요한 `deleteAll()`/`saveAll()` 제거.

- **AI 프롬프트 보정**  
  `TravelPlanAIAgent.generateTravelPlan` 에서 `%s` 플레이스홀더를 4개 사용하고, 목적지 키워드는 `Collectors.joining(", ")` 로 결합하여 올바른 프롬프트 생성.

## 테스트

```bash
./gradlew test
```

모든 단위 및 통합 테스트가 통과됩니다.

## 향후 작업 (선택)

- 운영 환경용 CORS 강화를 위한 구체적 오리진 설정  
- AI 응답에 대한 추가 검증 및 폴백 메커니즘 구현  
- 목적지 엔티티에 별도 `visitedAt` 타임스탬프 필드 추가 (방문 시간 기록)  
- Docker 이미지 최적화 및 멀티스테이지 빌드 적용  
- API 버저닝 (v2 등) 및 하위 호환성 전략 수립  

---

**문의사항이나 문제점이 있으면 Issue를 등록하거나 프로젝트 유지보수자에게 연락주세요.**  
Happy coding! 🚀