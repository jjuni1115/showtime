import axios from 'axios'
import type {
  ClubResponse,
  InviteResponse,
  MatchResultResponse,
  MatchVideoMetadata,
  MeetingResponse,
  MeetingScheduleResponse,
  MemberResponse,
  MyProfileResponse,
  PostResponse,
  TeamGenerateResponse,
} from '@/types/api'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  withCredentials: true,
})

export const getMyProfile = async () => {
  const { data } = await api.get<MyProfileResponse>('/api/v1/users/me')
  return data
}

export const getMyClubs = async () => {
  const { data } = await api.get<ClubResponse[]>('/api/v1/clubs/my')
  return data
}

export const createClub = async (payload: { name: string; homeCourt: string; imageUrl?: string }) => {
  const { data } = await api.post<ClubResponse>('/api/v1/clubs', payload)
  return data
}

export const joinClub = async (code: string) => {
  const { data } = await api.post<ClubResponse>('/api/v1/clubs/join', { code })
  return data
}

export const createInvite = async (clubId: string) => {
  const { data } = await api.post<InviteResponse>(`/api/v1/clubs/${clubId}/invites`)
  return data
}

export const getMeetingSchedule = async (clubId: string) => {
  const { data } = await api.get<MeetingScheduleResponse | null>(`/api/v1/clubs/${clubId}/meeting-schedule`)
  return data
}

export const upsertMeetingSchedule = async (
  clubId: string,
  payload: { dayOfWeek: MeetingScheduleResponse['dayOfWeek']; startTime: string; enabled: boolean },
) => {
  const { data } = await api.put<MeetingScheduleResponse>(`/api/v1/clubs/${clubId}/meeting-schedule`, payload)
  return data
}

export const getMeetings = async (clubId: string, from: string, to: string) => {
  const { data } = await api.get<MeetingResponse[]>(`/api/v1/clubs/${clubId}/meetings`, { params: { from, to } })
  return data
}

export const createMeeting = async (
  clubId: string,
  payload: { meetingDate: string; startTime?: string; note?: string },
) => {
  const { data } = await api.post<MeetingResponse>(`/api/v1/clubs/${clubId}/meetings`, payload)
  return data
}

export const voteMyAttendance = async (meetingId: string, status: 'YES' | 'NO' | 'MAYBE') => {
  const { data } = await api.post<MeetingResponse>(`/api/v1/meetings/${meetingId}/attendance/me`, { status })
  return data
}

export const setGuestAttendance = async (
  meetingId: string,
  payload: { guestName: string; status: 'YES' | 'NO' | 'MAYBE' },
) => {
  const { data } = await api.post<MeetingResponse>(`/api/v1/meetings/${meetingId}/attendance/guest`, payload)
  return data
}

export const createPost = async (clubId: string, content: string) => {
  const { data } = await api.post<PostResponse>(`/api/v1/clubs/${clubId}/posts`, { content })
  return data
}

export const getPosts = async (clubId: string, limit = 30) => {
  const { data } = await api.get<PostResponse[]>(`/api/v1/clubs/${clubId}/posts`, { params: { limit } })
  return data
}

export const getMembers = async () => {
  const { data } = await api.get<MemberResponse[]>('/api/v1/members')
  return data
}

export const generateTeamsFromAttendance = async (payload: {
  attendanceDate: string
  teamNames: string[]
  previousTeamByMemberId: Record<string, string>
}) => {
  const { data } = await api.post<TeamGenerateResponse>('/api/v1/teams/generate-from-attendance', payload)
  return data
}

export const createMatch = async (payload: { playedAt: string; teamScores: Record<string, number>; memo?: string }) => {
  const { data } = await api.post<MatchResultResponse>('/api/v1/matches', payload)
  return data
}

export const getMatches = async () => {
  const { data } = await api.get<MatchResultResponse[]>('/api/v1/matches')
  return data
}

export const uploadMatchVideo = async (matchId: string, file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await api.post<MatchVideoMetadata>(`/api/v1/matches/${matchId}/videos`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return data
}

export default api
