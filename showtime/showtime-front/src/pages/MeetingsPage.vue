<script setup lang="ts">
import { useConsole } from '@/composables/useConsole'

const {
  loading,
  selectedClubId,
  meetingSchedules,
  selectedScheduleId,
  scheduleForm,
  dayOfWeekOptions,
  nextMeetingRound,
  isScheduleLocked,
  regularMeetingCandidates,
  selectedRegularMeetingDate,
  meetingDate,
  meetingNote,
  meetings,
  selectedMeetingId,
  meetingEditScheduleId,
  meetingEditDate,
  meetingEditNote,
  myVoteStatus,
  guestName,
  guestStatus,
  attendanceStatusOptions,
  attendanceFilter,
  selectedMeeting,
  filteredAttendances,
  yesCount,
  maybeCount,
  noCount,
  getMeetingLabel,
  getDayOfWeekLabel,
  getAttendanceStatusLabel,
  getAttendanceSourceLabel,
  submitCreateSchedule,
  submitUpdateSchedule,
  submitDeleteSchedule,
  onSelectRegularMeetingDate,
  submitCreateMeeting,
  submitUpdateMeeting,
  submitDeleteMeeting,
  submitMyVote,
  submitGuestAttendance,
} = useConsole()
</script>

<template>
  <div class="grid gap-4 xl:grid-cols-[420px_minmax(0,1fr)]">
    <article class="surface p-5">
      <h2 class="section-title">정기 모임 설정</h2>
      <div class="mt-4 space-y-2">
        <label class="field-label">정기모임 목록</label>
        <select v-model="selectedScheduleId" class="field-input">
          <option value="">새 정기모임 생성</option>
          <option v-for="schedule in meetingSchedules" :key="schedule.id" :value="schedule.id">
            {{ schedule.name }} · {{ getDayOfWeekLabel(schedule.dayOfWeek) }} {{ schedule.startTime }}
          </option>
        </select>
      </div>
      <div class="mt-4 space-y-3">
        <input v-model="scheduleForm.name" class="field-input" placeholder="정기모임 이름 (예: 토요 오전 모임)" />
        <select v-model="scheduleForm.dayOfWeek" class="field-input">
          <option v-for="option in dayOfWeekOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
        </select>
        <input v-model="scheduleForm.startTime" class="field-input" type="time" step="1" />
        <input v-model="scheduleForm.place" class="field-input" placeholder="정기 모임 장소" />
        <label class="flex items-center gap-2 text-sm text-slate-700">
          <input v-model="scheduleForm.enabled" type="checkbox" /> 활성화
        </label>
        <button class="btn-primary w-full" :disabled="loading || !selectedClubId" @click="submitCreateSchedule">정기 모임 생성</button>
        <button class="btn-soft w-full" :disabled="loading || !selectedClubId || !selectedScheduleId" @click="submitUpdateSchedule">정기 모임 수정</button>
        <button class="btn-soft w-full" :disabled="loading || !selectedClubId || !selectedScheduleId" @click="submitDeleteSchedule">정기 모임 삭제</button>
      </div>

      <h3 class="mt-6 text-sm font-semibold text-slate-800">회차 생성</h3>
      <div class="mt-2 space-y-2">
        <select v-model="selectedScheduleId" class="field-input">
          <option value="" disabled>회차 생성할 정기모임 선택</option>
          <option v-for="schedule in meetingSchedules" :key="schedule.id" :value="schedule.id">
            {{ schedule.name }} · {{ getDayOfWeekLabel(schedule.dayOfWeek) }} {{ schedule.startTime }}
          </option>
        </select>
        <p class="text-xs text-slate-500">다음 생성 회차: {{ nextMeetingRound }}회차</p>
        <div v-if="selectedScheduleId && isScheduleLocked" class="rounded-xl border border-slate-200 bg-slate-50 p-3">
          <p class="text-xs font-semibold text-slate-700">정기모임 자동 세팅</p>
          <p class="mt-1 text-xs text-slate-600">요일: {{ getDayOfWeekLabel(scheduleForm.dayOfWeek) }} / 시간: {{ scheduleForm.startTime }}</p>
          <p v-if="scheduleForm.place" class="mt-1 text-xs text-slate-600">장소: {{ scheduleForm.place }}</p>
        </div>
        <select
          v-if="selectedScheduleId && isScheduleLocked"
          v-model="selectedRegularMeetingDate"
          class="field-input"
          @change="onSelectRegularMeetingDate(selectedRegularMeetingDate)"
        >
          <option v-for="candidate in regularMeetingCandidates" :key="candidate.date" :value="candidate.date">
            {{ candidate.label }}{{ candidate.exists ? ' (이미 생성됨)' : '' }}
          </option>
        </select>
        <input v-model="meetingDate" class="field-input" type="date" :disabled="!!selectedScheduleId && isScheduleLocked" />
        <input v-model="meetingNote" class="field-input" placeholder="메모" />
        <button class="btn-soft w-full" :disabled="loading || !selectedClubId || !selectedScheduleId" @click="submitCreateMeeting">모임 생성</button>
      </div>
    </article>

    <article class="surface p-5">
      <h2 class="section-title">참가 현황</h2>
      <select v-model="selectedMeetingId" class="field-input mt-3">
        <option value="" disabled>모임 선택</option>
        <option v-for="meeting in meetings" :key="meeting.id" :value="meeting.id">{{ getMeetingLabel(meeting) }}</option>
      </select>

      <div v-if="selectedMeeting" class="mt-4 space-y-4">
        <div class="grid gap-2 rounded-2xl border border-slate-200 p-3 sm:grid-cols-2">
          <select v-model="meetingEditScheduleId" class="field-input sm:col-span-2">
            <option value="" disabled>정기모임 선택</option>
            <option v-for="schedule in meetingSchedules" :key="schedule.id" :value="schedule.id">
              {{ schedule.name }} · {{ getDayOfWeekLabel(schedule.dayOfWeek) }} {{ schedule.startTime }}
            </option>
          </select>
          <input v-model="meetingEditDate" class="field-input" type="date" />
          <input v-model="meetingEditNote" class="field-input" placeholder="메모" />
          <button class="btn-soft sm:col-span-2" :disabled="loading" @click="submitUpdateMeeting">회차 수정</button>
          <button class="btn-soft" :disabled="loading" @click="submitDeleteMeeting">회차 삭제</button>
        </div>

        <div class="grid gap-2 sm:grid-cols-3">
          <div class="chip-card"><p class="chip-title">참가</p><p class="chip-value">{{ yesCount }}</p></div>
          <div class="chip-card"><p class="chip-title">미정</p><p class="chip-value">{{ maybeCount }}</p></div>
          <div class="chip-card"><p class="chip-title">불참</p><p class="chip-value">{{ noCount }}</p></div>
        </div>

        <div class="grid gap-2 sm:grid-cols-[1fr_auto]">
          <select v-model="myVoteStatus" class="field-input">
            <option v-for="option in attendanceStatusOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
          </select>
          <button class="btn-soft" :disabled="loading" @click="submitMyVote">내 투표</button>
        </div>

        <div class="grid gap-2 sm:grid-cols-[1.2fr_1fr_auto]">
          <input v-model="guestName" class="field-input" placeholder="게스트 이름" />
          <select v-model="guestStatus" class="field-input">
            <option v-for="option in attendanceStatusOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
          </select>
          <button class="btn-soft" :disabled="loading" @click="submitGuestAttendance">게스트 반영</button>
        </div>

        <div class="flex items-center gap-2">
          <label class="field-label !mb-0">상태 필터</label>
          <select v-model="attendanceFilter" class="field-input !w-36">
            <option value="ALL">전체</option>
            <option v-for="option in attendanceStatusOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
          </select>
        </div>

        <div class="overflow-x-auto rounded-2xl border border-slate-200">
          <table class="min-w-full text-sm">
            <thead class="bg-slate-50 text-slate-600">
              <tr>
                <th class="px-3 py-2 text-left">이름</th>
                <th class="px-3 py-2 text-left">구분</th>
                <th class="px-3 py-2 text-left">상태</th>
                <th class="px-3 py-2 text-left">입력 방식</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="attendance in filteredAttendances" :key="`${attendance.userId || attendance.guestName}-${attendance.source}`" class="border-t border-slate-100">
                <td class="px-3 py-2">{{ attendance.userName || attendance.guestName }}</td>
                <td class="px-3 py-2">{{ attendance.userId ? '회원' : '게스트' }}</td>
                <td class="px-3 py-2"><span class="chip">{{ getAttendanceStatusLabel(attendance.status) }}</span></td>
                <td class="px-3 py-2">{{ getAttendanceSourceLabel(attendance.source) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </article>
  </div>
</template>
