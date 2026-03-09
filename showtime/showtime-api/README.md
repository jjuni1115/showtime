# Showtime Backend

농구 동호회 운영을 위한 백엔드 API 서버입니다.

핵심 기능:
- Google OAuth 로그인
- 동호회(팀) 생성/관리
- 초대 링크 기반 자동 가입
- 정기 모임 일정 설정
- 모임 참가 투표(멤버) 및 강제 설정(운영진)
- 비가입 게스트 참가 등록
- 커뮤니티 게시글
- 멤버 프로필 기반 팀 자동 편성
- 경기 결과/영상 업로드 및 조회

## 기술 스택
- Kotlin 2.2.x
- Spring Boot 4.0.x
- Spring Security + OAuth2 Client (Google)
- Spring Data JPA
- PostgreSQL
- Flyway
- Gradle

## 프로젝트 구조
- `src/main/kotlin/com/basketman/showtime/config`: 보안/CORS/예외처리
- `src/main/kotlin/com/basketman/showtime/user`: OAuth 기반 사용자
- `src/main/kotlin/com/basketman/showtime/club`: 동호회/멤버십/초대 링크
- `src/main/kotlin/com/basketman/showtime/meeting`: 정기 모임/참가 투표
- `src/main/kotlin/com/basketman/showtime/community`: 동호회 게시글
- `src/main/kotlin/com/basketman/showtime/member`: 선수(농구 멤버) 프로필
- `src/main/kotlin/com/basketman/showtime/attendance`: 특정 날짜 참석자
- `src/main/kotlin/com/basketman/showtime/team`: 팀 자동 편성 알고리즘/API
- `src/main/kotlin/com/basketman/showtime/match`: 경기 결과/영상
- `src/main/resources/db/migration`: Flyway SQL

## 로컬 실행
### 1) PostgreSQL 준비
예시 DB:
- DB: `showtime`
- USER: `showtime`
- PASSWORD: `showtime`

### 2) 환경변수 설정
```bash
export DB_URL='jdbc:postgresql://localhost:5432/showtime'
export DB_USERNAME='showtime'
export DB_PASSWORD='showtime'

export GOOGLE_CLIENT_ID='364547942373-iuus86rnhhp131bmkv2ja1e2nsftcg4r.apps.googleusercontent.com'
export GOOGLE_CLIENT_SECRET='YOUR_NEW_SECRET'

export FRONTEND_ORIGIN='http://localhost:3000'
export INVITE_BASE_URL='http://localhost:3000/join'
export INVITE_EXPIRES_HOURS='168'
export VIDEO_STORAGE_DIR='./storage/videos'
```

### 3) 실행
```bash
./gradlew bootRun
```

### 4) 테스트
```bash
./gradlew test
```

## DB 마이그레이션
서버 시작 시 Flyway가 자동 실행됩니다.

- `V1__init_core_tables.sql`
  - 멤버, 참석, 경기, 경기 점수, 경기 영상
- `V2__club_membership_meeting_community.sql`
  - 사용자, 동호회, 멤버십, 초대, 모임, 투표, 커뮤니티

## 도메인 프로세스
### 1) 동호회 운영 시작
1. 운영진이 Google 로그인
2. `POST /api/v1/clubs`로 동호회 생성
3. `POST /api/v1/clubs/{clubId}/invites`로 초대 링크 생성
4. 동호회원은 링크 코드로 `POST /api/v1/clubs/join` 호출
5. 자동으로 멤버십(MEMBER) 생성

### 2) 정기 모임 운영
1. 운영진이 `PUT /api/v1/clubs/{clubId}/meeting-schedule` 설정
2. 운영진이 `POST /api/v1/clubs/{clubId}/meetings`로 실제 모임 생성
3. 멤버는 `POST /api/v1/meetings/{meetingId}/attendance/me`로 투표
4. 운영진은 멤버/게스트 참가 상태를 강제 반영 가능

### 3) 팀 편성 운영
1. 운영진이 선수 프로필 등록(`members`)
2. 당일 참석자 등록(`attendances/{date}`)
3. `POST /api/v1/teams/generate-from-attendance`로 자동 편성
4. 이전 주 팀 정보를 `previousTeamByMemberId`로 전달해 중복 완화

### 4) 경기 기록/영상
1. `POST /api/v1/matches`로 경기 결과 저장
2. `POST /api/v1/matches/{matchId}/videos`로 영상 업로드
3. `GET /api/v1/videos/{videoId}/download`로 다운로드/조회

## 권한 규칙
- 인증 불필요: `/api/v1/ping`, `/actuator/health`
- 나머지 API: Google OAuth 로그인 필요
- 동호회 Admin 전용:
  - 초대 링크 생성
  - 정기 모임 설정
  - 모임 생성
  - 멤버/게스트 참가 강제 설정

## 팀 편성 알고리즘 요약
입력:
- 신장, 실력 5단계(`LOW`~`HIGH`), 포지션, 스타일
- 팀명 목록(3팀 이상 가능)
- 이전 주 팀 정보(선택)

목표:
- 팀 인원 균등 분배
- 팀 실력 합계 편차 최소화
- 포지션/스타일 편중 완화
- 지난주 같은 팀 재배정 패널티

## API 문서
상세 명세는 아래 문서 참고:
- [docs/API_SPEC.md](docs/API_SPEC.md)
