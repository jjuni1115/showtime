import { computed, ref, watch } from 'vue'
import {
  createMeetingSchedule,
  createClub,
  createInvite,
  createMatch,
  createMeeting,
  createPost,
  deleteMeeting,
  deleteMeetingSchedule,
  generateTeamsFromAttendance,
  getMatches,
  getMeetingSchedules,
  getMeetings,
  getMembers,
  getMyClubs,
  getMyProfile,
  getPosts,
  joinClub,
  setGuestAttendance,
  uploadMatchVideo,
  updateMeeting,
  updateMeetingSchedule,
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
const pendingInviteCodeKey = 'showtime.pending_invite_code'
const pendingInviteRedirectKey = 'showtime.pending_invite_redirect'

const members = ref<MemberResponse[]>([])
const attendanceDate = ref(new Date().toISOString().slice(0, 10))
const teamNamesInput = ref('Blue, Black, White')
const teamResult = ref<TeamGenerateResponse | null>(null)

const meetingSchedules = ref<MeetingScheduleResponse[]>([])
const selectedScheduleId = ref<number | ''>('')
const scheduleForm = ref<{ name: string; dayOfWeek: MeetingScheduleResponse['dayOfWeek']; startTime: string; place: string; enabled: boolean }>({
  name: '',
  dayOfWeek: 'SATURDAY',
  startTime: '19:00:00',
  place: '',
  enabled: true,
})
const meetings = ref<MeetingResponse[]>([])
const meetingDate = ref(new Date().toISOString().slice(0, 10))
const meetingNote = ref('')
const selectedMeetingId = ref('')
const meetingEditDate = ref('')
const meetingEditScheduleId = ref<number | ''>('')
const meetingEditNote = ref('')
const myVoteStatus = ref<AttendanceStatus>('YES')
const guestName = ref('')
const guestStatus = ref<AttendanceStatus>('YES')
const attendanceFilter = ref<'ALL' | AttendanceStatus>('ALL')
const selectedRegularMeetingDate = ref('')

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
const selectedSchedule = computed(() =>
  meetingSchedules.value.find((schedule) => schedule.id === selectedScheduleId.value) ?? null,
)

const dayOfWeekOptions: Array<{ value: MeetingScheduleResponse['dayOfWeek']; label: string }> = [
  { value: 'MONDAY', label: '월요일' },
  { value: 'TUESDAY', label: '화요일' },
  { value: 'WEDNESDAY', label: '수요일' },
  { value: 'THURSDAY', label: '목요일' },
  { value: 'FRIDAY', label: '금요일' },
  { value: 'SATURDAY', label: '토요일' },
  { value: 'SUNDAY', label: '일요일' },
]
const attendanceStatusOptions: Array<{ value: AttendanceStatus; label: string }> = [
  { value: 'YES', label: '참가' },
  { value: 'MAYBE', label: '미정' },
  { value: 'NO', label: '불참' },
]
const dayOfWeekToKorean: Record<MeetingScheduleResponse['dayOfWeek'], string> = dayOfWeekOptions.reduce(
  (acc, it) => ({ ...acc, [it.value]: it.label }),
  {} as Record<MeetingScheduleResponse['dayOfWeek'], string>,
)
const attendanceStatusToKorean: Record<AttendanceStatus, string> = attendanceStatusOptions.reduce(
  (acc, it) => ({ ...acc, [it.value]: it.label }),
  {} as Record<AttendanceStatus, string>,
)
const dayOfWeekToIndex: Record<MeetingScheduleResponse['dayOfWeek'], number> = {
  MONDAY: 1,
  TUESDAY: 2,
  WEDNESDAY: 3,
  THURSDAY: 4,
  FRIDAY: 5,
  SATURDAY: 6,
  SUNDAY: 0,
}

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

const roundByMeetingId = computed(() => {
  const sorted = [...meetings.value].sort((a, b) => {
    const left = `${a.meetingDate}T${a.startTime}`
    const right = `${b.meetingDate}T${b.startTime}`
    if (left === right) return a.id.localeCompare(b.id)
    return left.localeCompare(right)
  })
  return sorted.reduce<Record<string, number>>((acc, it, index) => {
    acc[it.id] = index + 1
    return acc
  }, {})
})

const nextMeetingRound = computed(() => meetings.value.length + 1)
const isScheduleLocked = computed(() => selectedSchedule.value?.enabled === true)

const regularMeetingCandidates = computed(() => {
  const schedule = selectedSchedule.value
  if (!schedule || !schedule.enabled) return []

  const targetDay = dayOfWeekToIndex[schedule.dayOfWeek]
  const today = new Date()
  const start = new Date(today.getFullYear(), today.getMonth(), today.getDate())
  const first = new Date(start)
  while (first.getDay() !== targetDay) {
    first.setDate(first.getDate() + 1)
  }

  const existingDates = new Set(
    meetings.value.filter((m) => m.scheduleId === schedule.id).map((m) => m.meetingDate),
  )
  const result: Array<{ date: string; label: string; exists: boolean }> = []
  const end = new Date(start)
  end.setDate(start.getDate() + 31)
  const date = new Date(first)
  while (date <= end) {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    const dateText = `${y}-${m}-${d}`
    result.push({
      date: dateText,
      label: `${y}.${m}.${d} (${dayOfWeekToKorean[schedule.dayOfWeek]})`,
      exists: existingDates.has(dateText),
    })
    date.setDate(date.getDate() + 7)
  }
  return result
})

const getMeetingLabel = (meeting: MeetingResponse) => {
  const round = roundByMeetingId.value[meeting.id] ?? 0
  const master = meeting.scheduleName ? ` · ${meeting.scheduleName}` : ''
  return `${round}회차${master} · ${meeting.meetingDate} ${meeting.startTime}`
}

const getDayOfWeekLabel = (dayOfWeek: MeetingScheduleResponse['dayOfWeek']) => dayOfWeekToKorean[dayOfWeek] || dayOfWeek
const getAttendanceStatusLabel = (status: AttendanceStatus) => attendanceStatusToKorean[status] || status

const getAttendanceSourceLabel = (source: string) => {
  const sourceMap: Record<string, string> = {
    MEMBER_VOTE: '회원 투표',
    ADMIN_SET_MEMBER: '운영진 지정(회원)',
    ADMIN_SET_GUEST: '운영진 지정(게스트)',
    GUEST: '게스트',
    SELF: '본인',
    ADMIN: '운영진',
  }
  return sourceMap[source] || source
}

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
  const first = new Date(now.getFullYear(), 0, 1)
  const last = new Date(now.getFullYear() + 1, 11, 31)
  return {
    from: first.toISOString().slice(0, 10),
    to: last.toISOString().slice(0, 10),
  }
}

const refreshClubScopedData = async () => {
  if (!selectedClubId.value) {
    meetingSchedules.value = []
    selectedScheduleId.value = ''
    meetings.value = []
    posts.value = []
    selectedMeetingId.value = ''
    return
  }

  const { from, to } = getMonthRange()
  const [scheduleList, meetingList, postList] = await Promise.all([
    getMeetingSchedules(selectedClubId.value),
    getMeetings(selectedClubId.value, from, to),
    getPosts(selectedClubId.value, 30),
  ])

  meetingSchedules.value = scheduleList
  if (!selectedScheduleId.value && scheduleList.length) {
    selectedScheduleId.value = scheduleList[0].id
  }
  if (scheduleList.length) {
    const first = scheduleList[0]
    scheduleForm.value = {
      name: first.name,
      dayOfWeek: first.dayOfWeek,
      startTime: first.startTime,
      place: first.place || '',
      enabled: first.enabled,
    }
  } else {
    scheduleForm.value = {
      name: '',
      dayOfWeek: 'SATURDAY',
      startTime: '19:00:00',
      place: '',
      enabled: true,
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

    const pendingInviteCode = localStorage.getItem(pendingInviteCodeKey)
    if (pendingInviteCode?.trim()) {
      try {
        const joined = await joinClub(pendingInviteCode.trim())
        if (!clubs.value.find((club) => club.id === joined.id)) {
          clubs.value = [joined, ...clubs.value]
        }
        selectedClubId.value = joined.id
        localStorage.setItem(pendingInviteRedirectKey, '1')
      } finally {
        localStorage.removeItem(pendingInviteCodeKey)
      }
    }

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

const setPendingInviteCode = (code: string) => {
  const trimmed = code.trim()
  if (!trimmed) return
  localStorage.setItem(pendingInviteCodeKey, trimmed)
}

const consumePendingInviteRedirect = () => {
  const shouldRedirect = localStorage.getItem(pendingInviteRedirectKey) === '1'
  if (shouldRedirect) {
    localStorage.removeItem(pendingInviteRedirectKey)
  }
  return shouldRedirect
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

const submitCreateSchedule = async () => {
  if (!selectedClubId.value) {
    error.value = '클럽을 선택해주세요.'
    return
  }
  if (!scheduleForm.value.name.trim()) {
    error.value = '정기모임 이름을 입력해주세요.'
    return
  }

  await run(async () => {
    const created = await createMeetingSchedule(selectedClubId.value, {
      name: scheduleForm.value.name.trim(),
      dayOfWeek: scheduleForm.value.dayOfWeek,
      startTime: scheduleForm.value.startTime,
      place: scheduleForm.value.place.trim() || undefined,
      enabled: scheduleForm.value.enabled,
    })
    meetingSchedules.value = [...meetingSchedules.value, created]
    selectedScheduleId.value = created.id
  })
}

const submitUpdateSchedule = async () => {
  if (!selectedClubId.value) {
    error.value = '클럽을 선택해주세요.'
    return
  }
  if (!selectedScheduleId.value) {
    error.value = '수정할 정기모임을 선택해주세요.'
    return
  }
  if (!scheduleForm.value.name.trim()) {
    error.value = '정기모임 이름을 입력해주세요.'
    return
  }

  await run(async () => {
    const updated = await updateMeetingSchedule(selectedClubId.value, selectedScheduleId.value as number, {
      name: scheduleForm.value.name.trim(),
      dayOfWeek: scheduleForm.value.dayOfWeek,
      startTime: scheduleForm.value.startTime,
      place: scheduleForm.value.place.trim() || undefined,
      enabled: scheduleForm.value.enabled,
    })
    meetingSchedules.value = meetingSchedules.value.map((it) => (it.id === updated.id ? updated : it))
  })
}

const submitDeleteSchedule = async () => {
  if (!selectedClubId.value) {
    error.value = '클럽을 선택해주세요.'
    return
  }
  if (!selectedScheduleId.value) {
    error.value = '삭제할 정기모임을 선택해주세요.'
    return
  }
  if (!window.confirm('정기 모임을 삭제할까요?')) return

  await run(async () => {
    const deletingId = selectedScheduleId.value as number
    await deleteMeetingSchedule(selectedClubId.value, deletingId)
    meetingSchedules.value = meetingSchedules.value.filter((it) => it.id !== deletingId)
    selectedScheduleId.value = meetingSchedules.value[0]?.id || ''
  })
}

const submitCreateMeeting = async () => {
  if (!selectedClubId.value) {
    error.value = '클럽을 선택해주세요.'
    return false
  }
  if (!selectedScheduleId.value) {
    error.value = '회차를 생성할 정기모임을 선택해주세요.'
    return false
  }

  await run(async () => {
    const created = await createMeeting(selectedClubId.value, {
      scheduleId: selectedScheduleId.value as number,
      meetingDate: meetingDate.value,
      note: `${nextMeetingRound.value}회차${meetingNote.value ? ` - ${meetingNote.value}` : ''}`,
    })
    meetings.value = [created, ...meetings.value]
    selectedMeetingId.value = created.id
    meetingNote.value = ''
  })
  return !error.value
}

const submitUpdateMeeting = async () => {
  if (!selectedMeetingId.value) {
    error.value = '수정할 모임을 선택해주세요.'
    return
  }
  if (!meetingEditScheduleId.value) {
    error.value = '정기모임을 선택해주세요.'
    return
  }
  await run(async () => {
    const updated = await updateMeeting(selectedMeetingId.value, {
      scheduleId: meetingEditScheduleId.value as number,
      meetingDate: meetingEditDate.value,
      note: meetingEditNote.value.trim() || undefined,
    })
    meetings.value = meetings.value.map((it) => (it.id === updated.id ? updated : it))
  })
}

const submitDeleteMeeting = async () => {
  if (!selectedMeetingId.value) {
    error.value = '삭제할 모임을 선택해주세요.'
    return
  }
  if (!window.confirm('선택한 회차를 삭제할까요?')) return

  await run(async () => {
    const deletingId = selectedMeetingId.value
    await deleteMeeting(deletingId)
    meetings.value = meetings.value.filter((it) => it.id !== deletingId)
    selectedMeetingId.value = meetings.value[0]?.id || ''
  })
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

watch(
  selectedSchedule,
  (schedule) => {
    if (!schedule) return
    scheduleForm.value = {
      name: schedule.name,
      dayOfWeek: schedule.dayOfWeek,
      startTime: schedule.startTime,
      place: schedule.place || '',
      enabled: schedule.enabled,
    }
  },
  { immediate: true },
)

watch(
  regularMeetingCandidates,
  (candidates) => {
    const next = candidates.find((it) => !it.exists) ?? candidates[0]
    if (!next) return
    selectedRegularMeetingDate.value = next.date
    meetingDate.value = next.date
  },
  { immediate: true },
)

watch(
  selectedMeeting,
  (meeting) => {
    if (!meeting) return
    meetingEditScheduleId.value = meeting.scheduleId || ''
    meetingEditDate.value = meeting.meetingDate
    meetingEditNote.value = meeting.note || ''
  },
  { immediate: true },
)

const onSelectRegularMeetingDate = (date: string) => {
  selectedRegularMeetingDate.value = date
  meetingDate.value = date
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
    setPendingInviteCode,
    consumePendingInviteRedirect,
    members,
    attendanceDate,
    teamNamesInput,
    teamResult,
    meetingSchedules,
    selectedScheduleId,
    scheduleForm,
    dayOfWeekOptions,
    meetings,
    nextMeetingRound,
    isScheduleLocked,
    regularMeetingCandidates,
    selectedRegularMeetingDate,
    meetingDate,
    meetingNote,
    selectedMeetingId,
    meetingEditScheduleId,
    meetingEditDate,
    meetingEditNote,
    selectedMeeting,
    myVoteStatus,
    guestName,
    guestStatus,
    attendanceStatusOptions,
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
    getMeetingLabel,
    getDayOfWeekLabel,
    getAttendanceStatusLabel,
    getAttendanceSourceLabel,
    refreshAll,
    onChangeClub,
    submitCreateClub,
    submitJoinClub,
    submitInvite,
    copyInvite,
    submitTeamGenerate,
    submitCreateSchedule,
    submitUpdateSchedule,
    submitDeleteSchedule,
    onSelectRegularMeetingDate,
    submitCreateMeeting,
    submitUpdateMeeting,
    submitDeleteMeeting,
    submitMyVote,
    submitGuestAttendance,
    submitPost,
    submitCreateMatch,
    onVideoFileChange,
    submitUploadVideo,
  }
}
