# 🏷️ Auction Service

> **실시간 경매 시스템** - MSA 아키텍처 기반의 고성능 경매 서비스

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)
[![Kafka](https://img.shields.io/badge/Kafka-3.0-purple.svg)](https://kafka.apache.org/)

## 📋 목차

- [프로젝트 개요](#-프로젝트-개요)
- [기술 스택](#-기술-스택)
- [아키텍처](#-아키텍처)
- [주요 기능](#-주요-기능)
- [설치 및 실행](#-설치-및-실행)
- [API 문서](#-api-문서)
- [개발 가이드](#-개발-가이드)
- [성능 최적화](#-성능-최적화)
- [모니터링](#-모니터링)
- [기여 가이드](#-기여-가이드)

## 🎯 프로젝트 개요

Auction Service는 **실시간 경매 시스템**의 핵심 서비스로, 다음과 같은 특징을 가지고 있습니다:

- **실시간 입찰 처리**: Redis Lua Script를 통한 원자적 동시성 제어
- **고성능 배치 처리**: Redis 기반 배치 스케줄러로 대용량 트래픽 처리
- **실시간 알림**: Server-Sent Events(SSE)를 통한 실시간 가격 업데이트
- **MSA 아키텍처**: Spring Cloud 기반의 확장 가능한 서비스 구조

### 🏆 코드 품질

- **동시성 제어**: Redis Lua Script 기반 원자적 처리
- **입력 검증**: Bean Validation으로 보안 및 에러 메시지 강화
- **성능 최적화**: 배치 처리 및 에러 핸들링 최적화

## 🛠️ 기술 스택

### Backend
- **Java 17** - LTS 버전 사용
- **Spring Boot 3.5.0** - 최신 Spring Boot 버전
- **Spring Cloud 2025.0.0** - 마이크로서비스 인프라
- **Spring Data JPA** - 데이터 접근 계층
- **Spring Batch** - 배치 처리
- **Spring Kafka** - 메시징

### Database & Cache
- **MySQL 8.0** - 메인 데이터베이스
- **Redis 7.0** - 캐시 및 세션 저장소
- **Lettuce** - Redis 클라이언트

### Message Queue
- **Apache Kafka 3.0** - 비동기 메시징
- **Spring Cloud Stream** - 스트림 처리

### Service Discovery & Communication
- **Netflix Eureka** - 서비스 디스커버리
- **OpenFeign** - 서비스 간 HTTP 통신

### Documentation
- **SpringDoc OpenAPI 3** - API 문서화
- **Swagger UI** - API 테스트 인터페이스

### Build & Deploy
- **Gradle** - 빌드 도구
- **Docker** - 컨테이너화
- **GitHub Actions** - CI/CD

## 🏗️ 아키텍처

### 서비스 내부 아키텍처 (Clean Architecture)
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐  │
│  │   Controller    │  │   DTO/VO        │  │   Exception  │  │
│  │                 │  │                 │  │   Handler    │  │
│  └─────────────────┘  └─────────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐  │
│  │   Service       │  │   Saga Service  │  │   Scheduler  │  │
│  │   (Business)    │  │   (Transaction) │  │   Service    │  │
│  └─────────────────┘  └─────────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐  │
│  │   Repository    │  │   Feign Client  │  │   Kafka      │  │
│  │   (Data Access) │  │   (HTTP Client) │  │   Producer   │  │
│  └─────────────────┘  └─────────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 주요 기능

### 1. 실시간 경매 관리
- **경매 생성/수정/종료**: 투표 결과에 따른 자동 경매 생성
- **실시간 입찰**: Redis Lua Script 기반 원자적 입찰 처리
- **자동 경매 종료**: 스케줄러를 통한 정확한 시간 기반 종료

### 2. 동시성 제어
```java
// Redis Lua Script를 통한 원자적 입찰 처리
@Service
public class AtomicBidService {
    public boolean processBidAtomically(String auctionUuid, Bid bid) {
        // 분산 락 획득 → 원자적 처리 → 락 해제
        // Race Condition 완전 해결
    }
}
```

### 3. 실시간 알림
- **Server-Sent Events (SSE)**: 실시간 가격 업데이트 스트리밍
- **Redis Pub/Sub**: 다중 서버 환경에서 이벤트 동기화

### 4. 배치 처리
- **고성능 배치 스케줄러**: Redis 기반 배치 처리로 대용량 트래픽 처리
- **개별 실패 격리**: 한 경매 실패가 전체 배치에 영향 주지 않음

## 📦 설치 및 실행

### Prerequisites
- Java 17+
- MySQL 8.0+
- Redis 7.0+
- Kafka 3.0+
- Docker (선택사항)

### 1. 저장소 클론
```bash
git clone https://github.com/spharos-0078/0078-auctionService.git
cd 0078-auctionService
```

### 2. 환경 설정
```bash
# 환경 변수 설정
cp .env.example .env

# 환경 변수 편집
vim .env
```

```env
# Database
EC2_DB=localhost
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# Redis
REDIS_PASSWORD=password

# Kafka
EC2_HOST2=localhost

# Eureka
EC2_HOST=localhost
```

### 3. 데이터베이스 설정
```sql
-- MySQL 데이터베이스 생성
CREATE DATABASE piece_of_cake;
```

### 4. 애플리케이션 실행
```bash
# Gradle을 통한 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
./gradlew build
java -jar build/libs/auction-service-0.0.1-SNAPSHOT.jar
```

### 5. Docker를 통한 실행
```bash
# Docker 이미지 빌드
docker build -t auction-service .

# Docker Compose로 실행
docker-compose up -d
```

## 📚 API 문서

### Swagger UI
애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
```
http://localhost:8088/swagger-ui/index.html
```

### 주요 API 엔드포인트

#### 경매 관리
```
GET    /api/v1/auction/highest-price/{auctionUuid}    # 최고 입찰가 조회
POST   /api/v1/auction                                # 경매 생성
GET    /api/v1/auction/list                           # 경매 목록 조회
GET    /api/v1/auction/sse/price-updates/{auctionUuid} # 실시간 가격 업데이트
```

#### 입찰 관리
```
POST   /api/v1/bid                                    # 입찰 생성
GET    /api/v1/bid/list                               # 입찰 목록 조회
GET    /api/v1/bid/my-auctions                        # 내 경매 목록
POST   /api/v1/bid/hide                               # 입찰 숨기기
```

#### 투표 관리
```
POST   /api/v1/vote                                   # 투표 생성
POST   /api/v1/vote/detail                            # 투표 상세 생성
GET    /api/v1/vote/list                              # 투표 목록 조회
```

## 👨‍💻 개발 가이드

### 프로젝트 구조
```
src/main/java/com/pieceofcake/auction_service/
├── auction/                    # 경매 도메인
│   ├── application/           # 애플리케이션 계층
│   │   ├── AuctionService.java
│   │   ├── AuctionServiceImpl.java
│   │   └── scheduler/
│   │       └── BatchAuctionScheduler.java
│   ├── dto/                   # 데이터 전송 객체
│   ├── entity/                # 엔티티
│   ├── infrastructure/        # 인프라 계층
│   │   ├── AuctionRepository.java
│   │   └── client/
│   │       └── AuctionFeignClient.java
│   └── presentation/          # 프레젠테이션 계층
│       └── AuctionController.java
├── bid/                       # 입찰 도메인
│   ├── application/
│   │   ├── BidService.java
│   │   ├── BidServiceImpl.java
│   │   └── AtomicBidService.java       # 원자적 처리
│   └── ...
├── vote/                      # 투표 도메인
├── common/                    # 공통 모듈
│   ├── config/               # 설정
│   ├── entity/               # 공통 엔티티
│   ├── exception/            # 예외 처리
│   └── scheduler/            # 공통 스케줄러
└── kafka/                    # Kafka 관련
    └── producer/
```

### 코딩 컨벤션

#### 1. 패키지 명명 규칙
- 도메인별 패키지 분리: `auction`, `bid`, `vote`
- 계층별 패키지 분리: `presentation`, `application`, `infrastructure`

#### 2. 클래스 명명 규칙
- Controller: `*Controller`
- Service: `*Service`, `*ServiceImpl`
- Repository: `*Repository`
- Entity: 도메인명 (예: `Auction`, `Bid`)
- DTO: `*Dto`, `*RequestDto`, `*ResponseDto`
- VO: `*Vo`, `*RequestVo`, `*ResponseVo`

#### 3. 메서드 명명 규칙
- CRUD: `create*`, `read*`, `update*`, `delete*`
- 비즈니스 로직: `process*`, `validate*`, `calculate*`

## ⚡ 성능 최적화

### 1. 동시성 제어
- **Redis Lua Script**: 원자적 연산으로 Race Condition 해결
- **분산 락**: Redis 기반 락 메커니즘
- **Connection Pool**: HikariCP를 통한 DB 연결 최적화

### 2. 캐싱 전략
- **Redis 캐시**: 최고가 정보 캐싱
- **TTL 설정**: 14일 만료로 메모리 관리
- **Pub/Sub**: 실시간 이벤트 동기화

### 3. 배치 처리
- **배치 스케줄러**: 1초 간격으로 배치 처리
- **개별 실패 격리**: 한 경매 실패가 전체에 영향 주지 않음
- **리소스 정리**: 처리 완료 후 자동 플래그 제거

### 4. 데이터베이스 최적화
- **인덱스**: UUID, 상태, 시간 기반 인덱스
- **Soft Delete**: 논리적 삭제로 데이터 보존
- **JPA Auditing**: 생성/수정 시간 자동 관리

## 👥 팀

- **개발팀**: Spharos 6기, 팀 0078
