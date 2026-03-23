import { computed, ref } from 'vue'
import {
  createClub,
  createInvite,
  createMatch,
  createMeeting,
  createPost,
  generateTeamsFromAttendance,
  getMatches,
  getMeetingSchedule,
  getMeetings,
  getMembers,
  getMyClubs,
  getMyProfile,
  getPosts,
  joinClub,
  setGuestAttendance,
  uploadMatchVideo,
  upsertMeetingSchedule,
  voteMyAttendance,
} from '@/lib/api'
import type {
  AttendanceStatus,
  ClubResponse,
  MatchResultResponse,
  MeetingResponse,
  MeetingScheduleResponse,
  MemberResponse,
  MyProfileResponse,
  PostResponse,
  TeamGenerateResponse,
} from '@/types/api'

export type ViewKey = 'dashboard' | 'club' | 'meetings' | 'teams' | 'community' | 'matches' | 'members'

const loading = ref(false)
const error = ref<string | null>(null)

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const loginUrl = `${apiBaseUrl}/oauth2/authorization/google`

const isAuthenticated = ref(true)
const profile = ref<MyProfileResponse | null>(null)

const clubs = ref<ClubResponse[]>([])
const selectedClubId = ref('')
const lastInviteLink = ref('')

const clubForm = ref({ name: '', homeCourt: '', imageUrl: '' })
const joinCode = ref('')

const members = ref<MemberResponse[]>([])
const attendanceDate = ref(new Date().toISOString().slice(0, 10))
const teamNamesInput = ref('Blue, Black, White')
const teamResult = ref<TeamGenerateResponse | null>(null)

const meetingSchedule = ref<MeetingScheduleResponse | null>(null)
const scheduleForm = ref<{ dayOfWeek: MeetingScheduleResponse['dayOfWeek']; startTime: string; enabled: boolean }>({
  dayOfWeek: 'SATURDAY',
  startTime: '19:00:00',
  enabled: true,
})
const meetings = ref<MeetingResponse[]>([])
const meetingDate = ref(new Date().toISOString().slice(0, 10))
const meetingStartTime = ref('19:00:00')
const meetingNote = ref('')
const selectedMeetingId = ref('')
const myVoteStatus = ref<AttendanceStatus>('YES')
const guestName = ref('')
const guestStatus = ref<AttendanceStatus>('YES')
const attendanceFilter = ref<'ALL' | AttendanceStatus>('ALL')

const posts = ref<PostResponse[]>([])
const postContent = ref('')

const matches = ref<MatchResultResponse[]>([])
const matchPlayedAt = ref(new Date().toISOString().slice(0, 10))
const matchMemo = ref('')
const matchScoresInput = ref('Blue:21, Black:18, White:16')
const selectedMatchId = ref('')
const selectedVideoFile = ref<File | null>(null)

const selectedClub = computed(() => clubs.value.find((club) => club.id === selectedClubId.value) ?? null)
const selectedMeeting = computed(() => meetings.value.find((meeting) => meeting.id === selectedMeetingId.value) ?? null)
const selectedMatch = computed(() => matches.value.find((match) => match.id === selectedMatchId.value) ?? null)

const adminClubCount = computed(() => clubs.value.filter((club) => club.myRole === 'ADMIN').length)
const memberClubCount = computed(() => clubs.value.filter((club) => club.myRole === 'MEMBER').length)
const avgHeight = computed(() => {
  if (!members.value.length) return 0
  return members.value.reduce((sum, member) => sum + member.heightCm, 0) / members.value.length
})

const yesCount = computed(() => selectedMeeting.value?.attendances.filter((it) => it.status === 'YES').length ?? 0)
const maybeCount = computed(() => selectedMeeting.value?.attendances.filter((it) => it.status === 'MAYBE').length ?? 0)
const noCount = computed(() => selectedMeeting.value?.attendances.filter((it) => it.status === 'NO').length ?? 0)

const filteredAttendances = computed(() => {
  if (!selectedMeeting.value) return []
  if (attendanceFilter.value === 'ALL') return selectedMeeting.value.attendances
  return selectedMeeting.value.attendances.filter((it) => it.status === attendanceFilter.value)
})

const run = async (fn: () => Promise<void>) => {
  loading.value = true
  error.value = null
  try {
    await fn()
  } catch (e: any) {
    const status = e?.response?.status
    if (status === 401) {
      isAuthenticated.value = false
      return
    }
    error.value = e?.response?.data?.message || '요청에 실패했습니다. 로그인 상태/권한/API 서버를 확인해주세요.'
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getMonthRange = () => {
  const now = new Date()
  const first = new Date(now.getFullYear(), now.getMonth(), 1)
  const last = new Date(now.getFullYear(), now.getMonth() + 1, 0)
  return {
    from: first.toISOString().slice(0, 10),
    to: last.toISOString().slice(0, 10),
  }
}

const refreshClubScopedData = async () => {
  if (!selectedClubId.value) {
    meetingSchedule.value = null
    meetings.value = []
    posts.value = []
    selectedMeetingId.value = ''
    return
  }

  const { from, to } = getMonthRange()
  const [schedule, meetingList, postList] = await Promise.all([
    getMeetingSchedule(selectedClubId.value),
    getMeetings(selectedClubId.value, from, to),
    getPosts(selectedClubId.value, 30),
  ])

  meetingSchedule.value = schedule
  if (schedule) {
    scheduleForm.value = {
      dayOfWeek: schedule.dayOfWeek,
      startTime: schedule.startTime,
      enabled: schedule.enabled,
    }
  }

  meetings.value = meetingList
  posts.value = postList

  if (meetingList.length && !selectedMeetingId.value) {
    selectedMeetingId.value = meetingList[0].id
  }
}

const refreshAll = async () => {
  await run(async () => {
    profile.value = await getMyProfile()
    isAuthenticated.value = true

    const [clubList, memberList, matchList] = await Promise.all([getMyClubs(), getMembers(), getMatches()])
    clubs.value = clubList
    members.value = memberList
    matches.value = matchList

    if (!selectedClubId.value && clubs.value.length) {
      selectedClubId.value = clubs.value[0].id
    }
    if (!selectedMatchId.value && matches.value.length) {
      selectedMatchId.value = matches.value[0].id
    }

    await refreshClubScopedData()
  })
}

const onChangeClub = async (clubId: string) => {
  selectedClubId.value = clubId
  await run(async () => {
    await refreshClubScopedData()
  })
}

const submitCreateClub = async () => {
  if (!clubForm.value.name.trim() || !clubForm.value.homeCourt.trim()) {
    error.value = '팀명과 주 경기장은 필수입니다.'
    return false
  }

  await run(async () => {
    const created = await createClub({
      name: clubForm.value.name.trim(),
      homeCourt: clubForm.value.homeCourt.trim(),
      imageUrl: clubForm.value.imageUrl.trim() || undefined,
    })
    clubs.value = [created, ...clubs.value]
    selectedClubId.value = created.id
    clubForm.value = { name: '', homeCourt: '', imageUrl: '' }
    await refreshClubScopedData()
  })
  return !error.value
}

const submitJoinClub = async () => {
  if (!joinCode.value.trim()) {
    error.value = '초대 코드를 입력해주세요.'
    return false
  }

  await run(async () => {
    const joined = await joinClub(joinCode.value.trim())
    if (!clubs.value.find((club) => club.id === joined.id)) {
      clubs.value.push(joined)
    }
    selectedClubId.value = joined.id
    joinCode.value = ''
    await refreshClubScopedData()
  })
  return !error.value
}

const submitInvite = async () => {
  if (!selectedClubId.value) {
    error.value = '먼저 클럽을 선택해주세요.'
    return
  }

  await run(async () => {
    const invite = await createInvite(selectedClubId.value)
    lastInviteLink.value = invite.inviteLink
  })
}

const copyInvite = async () => {
  if (!lastInviteLink.value) return
  try {
    await navigator.clipboard.writeText(lastInviteLink.value)
  } catch {
    error.value = '클립보드 복사에 실패했습니다.'
  }
}

const submitTeamGenerate = async () => {
  const teamNames = teamNamesInput.value
    .split(',')
    .map((name) => name.trim())
    .filter(Boolean)

  if (!teamNames.length) {
    error.value = '팀명을 하나 이상 입력해주세요.'
    return false
  }

  await run(async () => {
    teamResult.value = await generateTeamsFromAttendance({
      attendanceDate: attendanceDate.value,
      teamNames,
      previousTeamByMemberId: {},
    })
  })
  return !error.value
}

const submitSchedule = async () => {
  if (!selectedClubId.value) {
    error.value = '클럽을 선택해주세요.'
    return
  }

  await run(async () => {
    meetingSchedule.value = await upsertMeetingSchedule(selectedClubId.value, scheduleForm.value)
  })
}

const submitCreateMeeting = async () => {
  if (!selectedClubId.value) {
    error.value = '클럽을 선택해주세요.'
    return false
  }

  await run(async () => {
    const created = await createMeeting(selectedClubId.value, {
      meetingDate: meetingDate.value,
      startTime: meetingStartTime.value,
      note: meetingNote.value || undefined,
    })
    meetings.value = [created, ...meetings.value]
    selectedMeetingId.value = created.id
    meetingNote.value = ''
  })
  return !error.value
}

const submitMyVote = async () => {
  if (!selectedMeetingId.value) {
    error.value = '모임을 선택해주세요.'
    return
  }

  await run(async () => {
    const updated = await voteMyAttendance(selectedMeetingId.value, myVoteStatus.value)
    meetings.value = meetings.value.map((it) => (it.id === updated.id ? updated : it))
  })
}

const submitGuestAttendance = async () => {
  if (!selectedMeetingId.value || !guestName.value.trim()) {
    error.value = '모임과 게스트 이름을 입력해주세요.'
    return
  }

  await run(async () => {
    const updated = await setGuestAttendance(selectedMeetingId.value, {
      guestName: guestName.value.trim(),
      status: guestStatus.value,
    })
    meetings.value = meetings.value.map((it) => (it.id === updated.id ? updated : it))
    guestName.value = ''
  })
}

const submitPost = async () => {
  if (!selectedClubId.value || !postContent.value.trim()) {
    error.value = '클럽을 선택하고 내용을 입력해주세요.'
    return
  }

  await run(async () => {
    const created = await createPost(selectedClubId.value, postContent.value.trim())
    posts.value = [created, ...posts.value]
    postContent.value = ''
  })
}

const submitCreateMatch = async () => {
  const scores: Record<string, number> = {}
  matchScoresInput.value
    .split(',')
    .map((token) => token.trim())
    .filter(Boolean)
    .forEach((token) => {
      const [name, score] = token.split(':').map((v) => v.trim())
      if (!name || !score) return
      const parsed = Number(score)
      if (Number.isNaN(parsed)) return
      scores[name] = parsed
    })

  if (!Object.keys(scores).length) {
    error.value = '점수 형식이 올바르지 않습니다. 예: Blue:21, Black:18'
    return false
  }

  await run(async () => {
    const created = await createMatch({
      playedAt: matchPlayedAt.value,
      teamScores: scores,
      memo: matchMemo.value || undefined,
    })
    matches.value = [created, ...matches.value]
    selectedMatchId.value = created.id
    matchMemo.value = ''
  })
  return !error.value
}

const onVideoFileChange = (files: FileList | null) => {
  selectedVideoFile.value = files && files.length ? files[0] : null
}

const submitUploadVideo = async () => {
  if (!selectedMatchId.value || !selectedVideoFile.value) {
    error.value = '업로드할 경기와 파일을 선택해주세요.'
    return
  }

  await run(async () => {
    const uploaded = await uploadMatchVideo(selectedMatchId.value, selectedVideoFile.value as File)
    matches.value = matches.value.map((match) => {
      if (match.id !== selectedMatchId.value) return match
      return { ...match, videos: [uploaded, ...match.videos] }
    })
    selectedVideoFile.value = null
  })
}

export const useConsole = () => {
  return {
    apiBaseUrl,
    loginUrl,
    loading,
    error,
    isAuthenticated,
    profile,
    clubs,
    selectedClubId,
    selectedClub,
    lastInviteLink,
    clubForm,
    joinCode,
    members,
    attendanceDate,
    teamNamesInput,
    teamResult,
    meetingSchedule,
    scheduleForm,
    meetings,
    meetingDate,
    meetingStartTime,
    meetingNote,
    selectedMeetingId,
    selectedMeeting,
    myVoteStatus,
    guestName,
    guestStatus,
    attendanceFilter,
    filteredAttendances,
    posts,
    postContent,
    matches,
    matchPlayedAt,
    matchMemo,
    matchScoresInput,
    selectedMatchId,
    selectedMatch,
    selectedVideoFile,
    adminClubCount,
    memberClubCount,
    avgHeight,
    yesCount,
    maybeCount,
    noCount,
    refreshAll,
    onChangeClub,
    submitCreateClub,
    submitJoinClub,
    submitInvite,
    copyInvite,
    submitTeamGenerate,
    submitSchedule,
    submitCreateMeeting,
    submitMyVote,
    submitGuestAttendance,
    submitPost,
    submitCreateMatch,
    onVideoFileChange,
    submitUploadVideo,
  }
}
