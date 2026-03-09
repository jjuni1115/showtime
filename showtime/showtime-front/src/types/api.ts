export type MembershipRole = 'ADMIN' | 'MEMBER'
export type SkillLevel = 'LOW' | 'LOWER_MID' | 'MID' | 'UPPER_MID' | 'HIGH'
export type Position = 'GUARD' | 'FORWARD' | 'CENTER'
export type PlayStyle = 'SHOOTER' | 'SLASHER' | 'PLAYMAKER' | 'DEFENDER' | 'REBOUNDER' | 'BALANCED'

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
