<script setup lang="ts">
import { useConsole } from '@/composables/useConsole'

const {
  loading,
  selectedClubId,
  scheduleForm,
  meetingDate,
  meetingStartTime,
  meetingNote,
  meetings,
  selectedMeetingId,
  myVoteStatus,
  guestName,
  guestStatus,
  attendanceFilter,
  selectedMeeting,
  filteredAttendances,
  yesCount,
  maybeCount,
  noCount,
  submitSchedule,
  submitCreateMeeting,
  submitMyVote,
  submitGuestAttendance,
} = useConsole()
</script>

<template>
  <div class="grid gap-4 xl:grid-cols-[420px_minmax(0,1fr)]">
    <article class="surface p-5">
      <h2 class="section-title">정기 모임 설정</h2>
      <div class="mt-4 space-y-3">
        <select v-model="scheduleForm.dayOfWeek" class="field-input">
          <option value="MONDAY">MONDAY</option>
          <option value="TUESDAY">TUESDAY</option>
          <option value="WEDNESDAY">WEDNESDAY</option>
          <option value="THURSDAY">THURSDAY</option>
          <option value="FRIDAY">FRIDAY</option>
          <option value="SATURDAY">SATURDAY</option>
          <option value="SUNDAY">SUNDAY</option>
        </select>
        <input v-model="scheduleForm.startTime" class="field-input" type="time" step="1" />
        <label class="flex items-center gap-2 text-sm text-slate-700">
          <input v-model="scheduleForm.enabled" type="checkbox" /> 활성화
        </label>
        <button class="btn-primary w-full" :disabled="loading || !selectedClubId" @click="submitSchedule">정기 모임 저장</button>
      </div>

      <h3 class="mt-6 text-sm font-semibold text-slate-800">회차 생성</h3>
      <div class="mt-2 space-y-2">
        <input v-model="meetingDate" class="field-input" type="date" />
        <input v-model="meetingStartTime" class="field-input" type="time" step="1" />
        <input v-model="meetingNote" class="field-input" placeholder="메모" />
        <button class="btn-soft w-full" :disabled="loading || !selectedClubId" @click="submitCreateMeeting">모임 생성</button>
      </div>
    </article>

    <article class="surface p-5">
      <h2 class="section-title">참가 현황</h2>
      <select v-model="selectedMeetingId" class="field-input mt-3">
        <option value="" disabled>모임 선택</option>
        <option v-for="meeting in meetings" :key="meeting.id" :value="meeting.id">{{ meeting.meetingDate }} {{ meeting.startTime }}</option>
      </select>

      <div v-if="selectedMeeting" class="mt-4 space-y-4">
        <div class="grid gap-2 sm:grid-cols-3">
          <div class="chip-card"><p class="chip-title">YES</p><p class="chip-value">{{ yesCount }}</p></div>
          <div class="chip-card"><p class="chip-title">MAYBE</p><p class="chip-value">{{ maybeCount }}</p></div>
          <div class="chip-card"><p class="chip-title">NO</p><p class="chip-value">{{ noCount }}</p></div>
        </div>

        <div class="grid gap-2 sm:grid-cols-[1fr_auto]">
          <select v-model="myVoteStatus" class="field-input">
            <option value="YES">YES</option>
            <option value="MAYBE">MAYBE</option>
            <option value="NO">NO</option>
          </select>
          <button class="btn-soft" :disabled="loading" @click="submitMyVote">내 투표</button>
        </div>

        <div class="grid gap-2 sm:grid-cols-[1.2fr_1fr_auto]">
          <input v-model="guestName" class="field-input" placeholder="게스트 이름" />
          <select v-model="guestStatus" class="field-input">
            <option value="YES">YES</option>
            <option value="MAYBE">MAYBE</option>
            <option value="NO">NO</option>
          </select>
          <button class="btn-soft" :disabled="loading" @click="submitGuestAttendance">게스트 반영</button>
        </div>

        <div class="flex items-center gap-2">
          <label class="field-label !mb-0">상태 필터</label>
          <select v-model="attendanceFilter" class="field-input !w-36">
            <option value="ALL">ALL</option>
            <option value="YES">YES</option>
            <option value="MAYBE">MAYBE</option>
            <option value="NO">NO</option>
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
                <td class="px-3 py-2"><span class="chip">{{ attendance.status }}</span></td>
                <td class="px-3 py-2">{{ attendance.source }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </article>
  </div>
</template>
