# API Specification

Base URL: `http://localhost:8080`

인증:
- 기본적으로 Google OAuth 로그인 필요
- 예외: `GET /api/v1/ping`, `GET /actuator/health`

공통 에러 응답(예시):
```json
{
  "message": "validation failed"
}
```

## 1) Health
### GET `/api/v1/ping`
Response 200
```json
{
  "message": "pong",
  "timestamp": "2026-03-09T11:00:00Z"
}
```

### GET `/actuator/health`
Response 200
```json
{
  "status": "UP"
}
```

## 2) User
### GET `/api/v1/users/me`
Response 200
```json
{
  "id": "7c3443c5-1089-4af8-95a9-50357d57e526",
  "email": "user@gmail.com",
  "displayName": "홍길동"
}
```

## 3) Club
### POST `/api/v1/clubs`
Request
```json
{
  "name": "쇼타임 농구회",
  "homeCourt": "잠실 학생체육관",
  "imageUrl": "https://cdn.example.com/club/logo.png"
}
```
Response 200
```json
{
  "id": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
  "name": "쇼타임 농구회",
  "homeCourt": "잠실 학생체육관",
  "imageUrl": "https://cdn.example.com/club/logo.png",
  "myRole": "ADMIN"
}
```

### GET `/api/v1/clubs/my`
Response 200
```json
[
  {
    "id": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
    "name": "쇼타임 농구회",
    "homeCourt": "잠실 학생체육관",
    "imageUrl": null,
    "myRole": "MEMBER"
  }
]
```

### POST `/api/v1/clubs/{clubId}/invites` (ADMIN)
Response 200
```json
{
  "code": "84d03dca1f354f64a1112b8dd33f8db1",
  "inviteLink": "http://localhost:3000/join?code=84d03dca1f354f64a1112b8dd33f8db1",
  "expiresAt": "2026-03-16T12:00:00Z"
}
```

### POST `/api/v1/clubs/join`
Request
```json
{
  "code": "84d03dca1f354f64a1112b8dd33f8db1"
}
```
Response 200
```json
{
  "id": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
  "name": "쇼타임 농구회",
  "homeCourt": "잠실 학생체육관",
  "imageUrl": null,
  "myRole": "MEMBER"
}
```

## 4) Community
### POST `/api/v1/clubs/{clubId}/posts`
Request
```json
{
  "content": "이번 주는 7시 30분 시작입니다."
}
```
Response 200
```json
{
  "id": "fb97a67c-0349-4c0d-96fe-b4ea15f67c94",
  "clubId": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
  "authorUserId": "7c3443c5-1089-4af8-95a9-50357d57e526",
  "authorName": "홍길동",
  "content": "이번 주는 7시 30분 시작입니다.",
  "createdAt": "2026-03-09T12:00:00Z"
}
```

### GET `/api/v1/clubs/{clubId}/posts?limit=30`
Response 200
```json
[
  {
    "id": "fb97a67c-0349-4c0d-96fe-b4ea15f67c94",
    "clubId": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
    "authorUserId": "7c3443c5-1089-4af8-95a9-50357d57e526",
    "authorName": "홍길동",
    "content": "이번 주는 7시 30분 시작입니다.",
    "createdAt": "2026-03-09T12:00:00Z"
  }
]
```

## 5) Meeting
### PUT `/api/v1/clubs/{clubId}/meeting-schedule` (ADMIN)
Request
```json
{
  "dayOfWeek": "SATURDAY",
  "startTime": "19:00:00",
  "enabled": true
}
```
Response 200
```json
{
  "clubId": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
  "dayOfWeek": "SATURDAY",
  "startTime": "19:00:00",
  "enabled": true
}
```

### GET `/api/v1/clubs/{clubId}/meeting-schedule`
Response 200
```json
{
  "clubId": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
  "dayOfWeek": "SATURDAY",
  "startTime": "19:00:00",
  "enabled": true
}
```

### POST `/api/v1/clubs/{clubId}/meetings` (ADMIN)
Request
```json
{
  "meetingDate": "2026-03-14",
  "startTime": "19:00:00",
  "note": "체육관 A코트 사용"
}
```
Response 200
```json
{
  "id": "b66dc70b-f2b1-4dd4-9e56-09431d44c9e1",
  "clubId": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
  "meetingDate": "2026-03-14",
  "startTime": "19:00:00",
  "note": "체육관 A코트 사용",
  "attendances": []
}
```

### GET `/api/v1/clubs/{clubId}/meetings?from=2026-03-01&to=2026-03-31`
Response 200
```json
[
  {
    "id": "b66dc70b-f2b1-4dd4-9e56-09431d44c9e1",
    "clubId": "5c89e82b-32a1-418e-b6bb-7beaf3f22d73",
    "meetingDate": "2026-03-14",
    "startTime": "19:00:00",
    "note": "체육관 A코트 사용",
    "attendances": []
  }
]
```

### POST `/api/v1/meetings/{meetingId}/attendance/me`
Request
```json
{
  "status": "YES"
}
```

### POST `/api/v1/meetings/{meetingId}/attendance/member` (ADMIN)
Request
```json
{
  "userId": "7c3443c5-1089-4af8-95a9-50357d57e526",
  "status": "MAYBE"
}
```

### POST `/api/v1/meetings/{meetingId}/attendance/guest` (ADMIN)
Request
```json
{
  "guestName": "김철수(지인)",
  "status": "YES"
}
```

`attendance` 응답은 모두 `MeetingResponse`를 반환합니다.

## 6) Member Profile (선수 프로필)
### POST `/api/v1/members`
Request
```json
{
  "name": "김가드",
  "heightCm": 178,
  "skillLevel": "UPPER_MID",
  "position": "GUARD",
  "style": "PLAYMAKER"
}
```

### GET `/api/v1/members`
Response 200
```json
[
  {
    "id": "9f2dcd7d-d77a-4380-8a8b-4a59c5133efe",
    "name": "김가드",
    "heightCm": 178,
    "skillLevel": "UPPER_MID",
    "position": "GUARD",
    "style": "PLAYMAKER",
    "active": true
  }
]
```

Enums:
- `skillLevel`: `LOW`, `LOWER_MID`, `MID`, `UPPER_MID`, `HIGH`
- `position`: `GUARD`, `FORWARD`, `CENTER`
- `style`: `SHOOTER`, `SLASHER`, `PLAYMAKER`, `DEFENDER`, `REBOUNDER`, `BALANCED`

## 7) Attendance
### PUT `/api/v1/attendances/{date}`
Request
```json
{
  "memberIds": [
    "9f2dcd7d-d77a-4380-8a8b-4a59c5133efe"
  ]
}
```
Response 200
```json
{
  "date": "2026-03-14",
  "attendees": [
    {
      "id": "9f2dcd7d-d77a-4380-8a8b-4a59c5133efe",
      "name": "김가드",
      "heightCm": 178,
      "skillLevel": "UPPER_MID",
      "position": "GUARD",
      "style": "PLAYMAKER",
      "active": true
    }
  ]
}
```

### GET `/api/v1/attendances/{date}`
Response 형식은 위와 동일합니다.

## 8) Team Balancing
### POST `/api/v1/teams/generate`
Request
```json
{
  "teamNames": ["Blue", "Black", "White"],
  "attendees": [
    {
      "id": "m1",
      "name": "A",
      "heightCm": 182,
      "skillLevel": "HIGH",
      "position": "GUARD",
      "style": "PLAYMAKER"
    }
  ],
  "previousTeamByMemberId": {
    "m1": "Blue"
  }
}
```

### POST `/api/v1/teams/generate-from-attendance`
Request
```json
{
  "attendanceDate": "2026-03-14",
  "teamNames": ["Blue", "Black", "White"],
  "previousTeamByMemberId": {
    "9f2dcd7d-d77a-4380-8a8b-4a59c5133efe": "Blue"
  }
}
```

Response (두 API 공통)
```json
{
  "teams": [
    {
      "name": "Blue",
      "members": [
        {
          "id": "m1",
          "name": "A",
          "heightCm": 182,
          "skillLevel": "HIGH",
          "position": "GUARD",
          "style": "PLAYMAKER"
        }
      ],
      "totalSkillScore": 5,
      "averageHeightCm": 182.0,
      "positionCounts": {
        "GUARD": 1
      },
      "repeatedFromLastWeekCount": 0
    }
  ],
  "summary": {
    "teamSkillGap": 2,
    "teamSizeGap": 1,
    "repeatedAssignments": 1
  }
}
```

## 9) Match & Video
### POST `/api/v1/matches`
Request
```json
{
  "playedAt": "2026-03-14",
  "teamScores": {
    "Blue": 21,
    "Black": 18,
    "White": 16
  },
  "memo": "플레이오프 1라운드"
}
```

### GET `/api/v1/matches`
### GET `/api/v1/matches/{matchId}`
Response
```json
{
  "id": "b7fa28e8-6f0c-4627-8f39-7e9fb51f1647",
  "playedAt": "2026-03-14",
  "teamScores": {
    "Blue": 21,
    "Black": 18,
    "White": 16
  },
  "memo": "플레이오프 1라운드",
  "videos": [
    {
      "id": "f45a8c98-64d3-43a9-8d90-84ae9ab090f5",
      "fileName": "game1.mp4",
      "contentType": "video/mp4",
      "sizeBytes": 98273645,
      "uploadedAt": "2026-03-14T12:30:00Z",
      "downloadUrl": "/api/v1/videos/f45a8c98-64d3-43a9-8d90-84ae9ab090f5/download"
    }
  ],
  "createdAt": "2026-03-14T12:00:00Z"
}
```

### POST `/api/v1/matches/{matchId}/videos`
- Content-Type: `multipart/form-data`
- field name: `file`

Response 200
```json
{
  "id": "f45a8c98-64d3-43a9-8d90-84ae9ab090f5",
  "fileName": "game1.mp4",
  "contentType": "video/mp4",
  "sizeBytes": 98273645,
  "uploadedAt": "2026-03-14T12:30:00Z",
  "downloadUrl": "/api/v1/videos/f45a8c98-64d3-43a9-8d90-84ae9ab090f5/download"
}
```

### GET `/api/v1/videos/{videoId}/download`
- 영상 파일 다운로드 응답
