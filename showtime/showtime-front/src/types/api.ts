export type MembershipRole = 'ADMIN' | 'MEMBER'
export type SkillLevel = 'LOW' | 'LOWER_MID' | 'MID' | 'UPPER_MID' | 'HIGH'
export type Position = 'GUARD' | 'FORWARD' | 'CENTER'
export type PlayStyle = 'SHOOTER' | 'SLASHER' | 'PLAYMAKER' | 'DEFENDER' | 'REBOUNDER' | 'BALANCED'
export type AttendanceStatus = 'YES' | 'NO' | 'MAYBE'

export interface ApiError {
  message: string
}

export interface MyProfileResponse {
  id: string
  email: string
  displayName: string
}

export interface ClubResponse {
  id: string
  name: string
  homeCourt: string
  imageUrl: string | null
  myRole: MembershipRole
}

export interface InviteResponse {
  code: string
  inviteLink: string
  expiresAt: string | null
}

export interface MeetingScheduleResponse {
  clubId: string
  dayOfWeek: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY'
  startTime: string
  enabled: boolean
}

export interface MeetingAttendanceResponse {
  userId: string | null
  userName: string | null
  guestName: string | null
  status: AttendanceStatus
  source: string
}

export interface MeetingResponse {
  id: string
  clubId: string
  meetingDate: string
  startTime: string
  note: string | null
  attendances: MeetingAttendanceResponse[]
}

export interface PostResponse {
  id: string
  clubId: string
  authorUserId: string
  authorName: string
  content: string
  createdAt: string
}

export interface MemberResponse {
  id: string
  name: string
  heightCm: number
  skillLevel: SkillLevel
  position: Position
  style: PlayStyle
  active: boolean
}

export interface TeamGenerateResponse {
  teams: {
    name: string
    members: Array<{
      id: string
      name: string
      heightCm: number
      skillLevel: SkillLevel
      position: Position
      style: PlayStyle
    }>
    totalSkillScore: number
    averageHeightCm: number
    positionCounts: Record<string, number>
    repeatedFromLastWeekCount: number
  }[]
  summary: {
    teamSkillGap: number
    teamSizeGap: number
    repeatedAssignments: number
  }
}

export interface MatchVideoMetadata {
  id: string
  fileName: string
  contentType: string
  sizeBytes: number
  uploadedAt: string
  downloadUrl: string
}

export interface MatchResultResponse {
  id: string
  playedAt: string
  teamScores: Record<string, number>
  memo: string | null
  videos: MatchVideoMetadata[]
  createdAt: string
}
