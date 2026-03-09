# Showtime Workspace

이 워크스페이스는 `showtime` 하위에 백엔드/프론트 모듈을 둔 구조입니다.

## 모듈
- `showtime/showtime-api`: Spring Boot + Kotlin API 서버
- `showtime/showtime-front`: Vue 3 + TypeScript + Tailwind CSS 웹앱

## 시작
### API
```bash
cd showtime/showtime-api
./gradlew bootRun
```

### Front
```bash
cd showtime/showtime-front
npm install
npm run dev
```
