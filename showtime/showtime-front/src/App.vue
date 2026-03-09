<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createClub, createInvite, generateTeamsFromAttendance, getMembers, getMyClubs, joinClub } from './lib/api'
import type { ClubResponse, MemberResponse, TeamGenerateResponse } from './types/api'

const loading = ref(false)
const error = ref<string | null>(null)

const clubs = ref<ClubResponse[]>([])
const selectedClubId = ref('')
const lastInviteLink = ref('')

const clubForm = ref({ name: '', homeCourt: '', imageUrl: '' })
const joinCode = ref('')

const members = ref<MemberResponse[]>([])
const attendanceDate = ref(new Date().toISOString().slice(0, 10))
const teamNamesInput = ref('Blue,Black,White')
const teamResult = ref<TeamGenerateResponse | null>(null)

const run = async (fn: () => Promise<void>) => {
  loading.value = true
  error.value = null
  try {
    await fn()
  } catch (e) {
    error.value = '요청에 실패했습니다. 로그인 상태/권한/API 서버를 확인해주세요.'
    console.error(e)
  } finally {
    loading.value = false
  }
}

const refresh = async () => {
  await run(async () => {
    clubs.value = await getMyClubs()
    members.value = await getMembers()
    if (!selectedClubId.value && clubs.value.length) {
      selectedClubId.value = clubs.value[0].id
    }
  })
}

const submitCreateClub = async () => {
  await run(async () => {
    const created = await createClub({
      name: clubForm.value.name,
      homeCourt: clubForm.value.homeCourt,
      imageUrl: clubForm.value.imageUrl || undefined,
    })
    clubs.value = [created, ...clubs.value]
    selectedClubId.value = created.id
    clubForm.value = { name: '', homeCourt: '', imageUrl: '' }
  })
}

const submitJoinClub = async () => {
  await run(async () => {
    const joined = await joinClub(joinCode.value)
    if (!clubs.value.find((club) => club.id === joined.id)) {
      clubs.value.push(joined)
    }
    selectedClubId.value = joined.id
    joinCode.value = ''
  })
}

const submitInvite = async () => {
  if (!selectedClubId.value) return
  await run(async () => {
    const invite = await createInvite(selectedClubId.value)
    lastInviteLink.value = invite.inviteLink
  })
}

const submitTeamGenerate = async () => {
  await run(async () => {
    const teamNames = teamNamesInput.value
      .split(',')
      .map((name) => name.trim())
      .filter(Boolean)

    teamResult.value = await generateTeamsFromAttendance({
      attendanceDate: attendanceDate.value,
      teamNames,
      previousTeamByMemberId: {},
    })
  })
}

onMounted(refresh)
</script>

<template>
  <main class="mx-auto max-w-7xl px-4 py-8 md:px-6">
    <section class="mb-6 rounded-3xl bg-court-dark p-6 text-white shadow-card">
      <p class="mb-2 text-xs uppercase tracking-[0.22em] text-court-mint">Showtime Console</p>
      <h1 class="font-display text-3xl font-bold md:text-4xl">동호회 운영 대시보드</h1>
      <p class="mt-3 text-sm text-slate-200">
        Google 로그인 세션으로 API를 호출합니다. 먼저 API 서버를 켜고 OAuth 로그인을 완료한 뒤 사용하세요.
      </p>
    </section>

    <p v-if="error" class="mb-4 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{{ error }}</p>

    <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      <article class="card p-5">
        <h2 class="font-display text-lg font-bold">클럽 생성</h2>
        <div class="mt-4 space-y-3">
          <div>
            <label class="label">팀명</label>
            <input v-model="clubForm.name" class="input" placeholder="쇼타임 농구회" />
          </div>
          <div>
            <label class="label">주 경기장</label>
            <input v-model="clubForm.homeCourt" class="input" placeholder="잠실 학생체육관" />
          </div>
          <div>
            <label class="label">팀 이미지 URL</label>
            <input v-model="clubForm.imageUrl" class="input" placeholder="https://..." />
          </div>
          <button class="btn-primary w-full" :disabled="loading" @click="submitCreateClub">생성</button>
        </div>
      </article>

      <article class="card p-5">
        <h2 class="font-display text-lg font-bold">초대 코드로 가입</h2>
        <div class="mt-4 space-y-3">
          <div>
            <label class="label">Invite Code</label>
            <input v-model="joinCode" class="input" placeholder="84d03dca..." />
          </div>
          <button class="btn-primary w-full" :disabled="loading" @click="submitJoinClub">가입</button>
        </div>
      </article>

      <article class="card p-5">
        <h2 class="font-display text-lg font-bold">내 클럽</h2>
        <div class="mt-4 space-y-3">
          <select v-model="selectedClubId" class="input">
            <option value="" disabled>클럽 선택</option>
            <option v-for="club in clubs" :key="club.id" :value="club.id">
              {{ club.name }} ({{ club.myRole }})
            </option>
          </select>
          <button class="btn-soft w-full" :disabled="loading" @click="refresh">새로고침</button>
          <button class="btn-primary w-full" :disabled="loading || !selectedClubId" @click="submitInvite">
            초대 링크 생성
          </button>
          <p v-if="lastInviteLink" class="rounded-xl bg-slate-100 px-3 py-2 text-xs break-all">
            {{ lastInviteLink }}
          </p>
        </div>
      </article>
    </section>

    <section class="mt-6 grid gap-4 xl:grid-cols-[380px_1fr]">
      <article class="card p-5">
        <h2 class="font-display text-lg font-bold">팀 자동 편성</h2>
        <p class="mt-1 text-xs text-slate-500">참석 데이터는 API 서버 DB에 이미 등록되어 있어야 합니다.</p>

        <div class="mt-4 space-y-3">
          <div>
            <label class="label">참석일</label>
            <input v-model="attendanceDate" class="input" type="date" />
          </div>
          <div>
            <label class="label">팀명 목록 (쉼표 구분)</label>
            <input v-model="teamNamesInput" class="input" placeholder="Blue,Black,White" />
          </div>
          <button class="btn-primary w-full" :disabled="loading" @click="submitTeamGenerate">편성 실행</button>
        </div>
      </article>

      <article class="card p-5">
        <h2 class="font-display text-lg font-bold">편성 결과</h2>
        <div v-if="!teamResult" class="mt-4 rounded-xl border border-dashed border-slate-300 px-4 py-12 text-center text-sm text-slate-500">
          결과가 여기에 표시됩니다.
        </div>

        <div v-else class="mt-4 space-y-4">
          <div class="grid gap-3 md:grid-cols-3">
            <div class="rounded-xl bg-slate-100 p-3 text-sm">
              실력 갭: <b>{{ teamResult.summary.teamSkillGap }}</b>
            </div>
            <div class="rounded-xl bg-slate-100 p-3 text-sm">
              인원 갭: <b>{{ teamResult.summary.teamSizeGap }}</b>
            </div>
            <div class="rounded-xl bg-slate-100 p-3 text-sm">
              지난주 중복: <b>{{ teamResult.summary.repeatedAssignments }}</b>
            </div>
          </div>

          <div class="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            <div v-for="team in teamResult.teams" :key="team.name" class="rounded-2xl border border-slate-200 bg-white p-4">
              <h3 class="font-display text-base font-bold">{{ team.name }}</h3>
              <p class="mt-1 text-xs text-slate-500">
                총 실력 {{ team.totalSkillScore }} · 평균 신장 {{ team.averageHeightCm.toFixed(1) }}cm
              </p>
              <ul class="mt-3 space-y-1 text-sm">
                <li v-for="member in team.members" :key="member.id" class="flex justify-between rounded-lg bg-slate-50 px-2 py-1">
                  <span>{{ member.name }}</span>
                  <span class="text-slate-500">{{ member.position }} / {{ member.skillLevel }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </article>
    </section>

    <section class="mt-6 card p-5">
      <h2 class="font-display text-lg font-bold">멤버 현황</h2>
      <p class="mt-1 text-xs text-slate-500">현재 DB에 저장된 멤버 프로필</p>
      <div class="mt-3 grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
        <div v-for="member in members" :key="member.id" class="rounded-xl border border-slate-200 px-3 py-2 text-sm">
          <div class="font-semibold">{{ member.name }}</div>
          <div class="text-xs text-slate-500">
            {{ member.position }} · {{ member.skillLevel }} · {{ member.heightCm }}cm
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
