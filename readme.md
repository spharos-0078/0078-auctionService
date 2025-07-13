# 🏷️ Auction Service

> **실시간 경매 서비스** - 투표 기반 경매 시스템 및 실시간 입찰 처리를 담당하는 서비스

## 📋 목차

- [프로젝트 개요](#-프로젝트-개요)
- [기술 스택](#-기술-스택)
- [주요 기능](#-주요-기능)
- [환경 설정](#-환경-설정)
- [실행 방법](#-실행-방법)
- [API 문서](#-api-문서)
- [팀원 정보](#-팀원-정보)

## 🎯 프로젝트 개요

Auction Service는 **실시간 경매 시스템**의 핵심 서비스로, 다음과 같은 특징을 가지고 있습니다:

- **실시간 입찰 처리**: Redis Lua Script를 통한 원자적 동시성 제어
- **고성능 배치 처리**: Redis 기반 배치 스케줄러로 대용량 트래픽 처리
- **실시간 알림**: Server-Sent Events(SSE)를 통한 실시간 가격 업데이트
- **MSA 아키텍처**: Spring Cloud 기반의 확장 가능한 서비스 구조

### 🏆 코드 품질

- **동시성 제어**: Redis + micro batch 기반 처리
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

## 🚀 주요 기능

### 1. 실시간 경매 관리
- **경매 생성/수정/종료**: 투표 결과에 따른 자동 경매 생성
- **실시간 입찰**: Redis Lua Script 기반 원자적 입찰 처리
- **자동 경매 종료**: TaskScheduler를 통한 정확한 시간 기반 종료

### 2. 동시성 제어
- **분산 락**: Redis를 통한 분산 락 획득
- **원자적 처리**: 입찰 처리 시 Race Condition 완전 해결
- **성능 최적화**: 대용량 트래픽 처리 최적화

### 3. 실시간 알림
- **Server-Sent Events (SSE)**: 실시간 가격 업데이트 스트리밍
- **Redis Pub/Sub**: 다중 서버 환경에서 이벤트 동기화

### 4. 배치 처리
- **고성능 배치 스케줄러**: Redis 기반 배치 처리로 대용량 트래픽 처리
- **개별 실패 격리**: 한 경매 실패가 전체 배치에 영향 주지 않음

### 5. 투표 시스템
- **조각 보유자 투표**: 조각 보유자들의 경매 진행 여부 투표
- **과반수 결정**: 투표 과반 수 찬성 시 자동 경매 생성
- **투표 기간 관리**: 투표 시작/종료 자동 관리

## ⚙️ 환경 설정

### 필수 요구사항
- Java 17 이상
- MySQL 8.0 이상
- Redis 7.0 이상
- Kafka 3.0 이상
- Docker (선택사항)

### 환경 변수
- **EC2_DB**: MySQL 데이터베이스 연결 정보
- **SPRING_DATASOURCE_USERNAME**: 데이터베이스 사용자명
- **SPRING_DATASOURCE_PASSWORD**: 데이터베이스 비밀번호
- **REDIS_PASSWORD**: Redis 접속 비밀번호
- **EC2_HOST2**: Kafka 서버 정보
- **EC2_HOST**: Eureka 서버 정보

## 🚀 실행 방법

### 1. 저장소 클론
프로젝트 저장소를 로컬에 클론합니다.

### 2. 환경 변수 설정
필요한 환경 변수를 설정합니다.

### 3. 데이터베이스 설정
MySQL 데이터베이스를 생성합니다.

### 4. 애플리케이션 실행
Gradle을 통해 애플리케이션을 실행합니다.

### 5. 접속 확인
- API 엔드포인트: http://localhost:8088
- Swagger UI: http://localhost:8088/swagger-ui/index.html

## 📚 API 문서

### Swagger UI 접속
API 문서는 Swagger UI를 통해 확인할 수 있습니다.

### 주요 API 엔드포인트

#### 경매 관리
- **최고 입찰가 조회**: 특정 경매의 현재 최고 입찰가 조회
- **경매 생성**: 새로운 경매 생성
- **경매 목록 조회**: 진행 중인 경매 목록 조회
- **실시간 가격 업데이트**: SSE를 통한 실시간 가격 정보

#### 입찰 관리
- **입찰 생성**: 경매에 입찰 참여
- **입찰 목록 조회**: 사용자별 입찰 내역 조회
- **내 경매 목록**: 사용자별 참여 경매 목록
- **입찰 숨기기**: 입찰 내역 비공개 처리

#### 투표 관리
- **투표 생성**: 경매 진행 여부 투표 생성
- **투표 상세 생성**: 투표 세부 항목 생성
- **투표 목록 조회**: 진행 중인 투표 목록

## 🔧 주요 설정

### 동시성 제어 설정
Redis + micro atch를 통한 원자적 입찰 처리가 구성되어 있습니다.

### 실시간 스트리밍 설정
SSE를 통한 실시간 가격 업데이트 스트리밍이 설정되어 있습니다.

### 배치 처리 설정
Spring Batch를 통한 대용량 경매 처리 시스템이 구성되어 있습니다.

## 👥 팀원 정보

**Piece of Cake 팀**
- **팀장**: 이수진 (Backend & Leader)
- **팀원**: 정동섭 (Backend), 이영인 (Backend), 오은서 (Backend), 정진우 (Frontend)

**Auction Service 담당**
- **정동섭**: 실시간 경매 시스템, 투표 시스템, 입찰 처리

---

## 📞 연락처

- **프로젝트 홈페이지**: https://mobile.pieceofcake.site/
- **개발 기간**: 2025년 4월 30일 ~ 7월 10일

---

*"Investment is Easy and Fun" - Piece of Cake 프로젝트*
