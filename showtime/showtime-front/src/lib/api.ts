import axios from 'axios'
import type { ClubResponse, InviteResponse, MemberResponse, TeamGenerateResponse } from '@/types/api'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  withCredentials: true,
})

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

export default api
