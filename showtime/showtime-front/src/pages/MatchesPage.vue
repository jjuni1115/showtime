<script setup lang="ts">
import { computed } from 'vue'
import { useConsole } from '@/composables/useConsole'

const {
  loading,
  matches,
  selectedMatchMeetingId,
  finishedMeetingOptions,
  matchMemo,
  matchTeamScores,
  selectedMatchId,
  selectedMatch,
  selectedVideoFile,
  submitCreateMatch,
  onVideoFileChange,
  submitUploadVideo,
} = useConsole()

const selectedVideoName = computed(() => selectedVideoFile.value?.name || '선택된 파일 없음')
</script>

<template>
  <div class="grid gap-4 xl:grid-cols-[420px_minmax(0,1fr)]">
    <article class="surface p-5">
      <h2 class="section-title">경기 결과 등록</h2>
      <div class="mt-3 space-y-2">
        <select v-model="selectedMatchMeetingId" class="field-input">
          <option value="" disabled>종료된 회차 선택</option>
          <option v-for="meeting in finishedMeetingOptions" :key="meeting.id" :value="meeting.id">{{ meeting.label }}</option>
        </select>
        <div class="space-y-2 rounded-xl border border-slate-200 bg-slate-50 p-3">
          <div v-for="(score, teamName) in matchTeamScores" :key="teamName" class="grid grid-cols-[1fr_120px] items-center gap-2">
            <p class="text-sm font-medium text-slate-700">{{ teamName }}</p>
            <input v-model.number="matchTeamScores[teamName]" class="field-input !py-2" type="number" min="0" />
          </div>
        </div>
        <input v-model="matchMemo" class="field-input" placeholder="메모" />
        <button class="btn-primary w-full" :disabled="loading" @click="submitCreateMatch">경기 저장</button>
      </div>

      <h3 class="mt-6 text-sm font-semibold text-slate-800">영상 업로드</h3>
      <select v-model="selectedMatchId" class="field-input mt-2">
        <option value="" disabled>경기 선택</option>
        <option v-for="match in matches" :key="match.id" :value="match.id">{{ match.playedAt }} - {{ match.id.slice(0, 8) }}</option>
      </select>

      <input class="mt-2 block w-full text-sm" type="file" accept="video/*" @change="onVideoFileChange(($event.target as HTMLInputElement).files)" />
      <p class="mt-1 text-xs text-slate-500">{{ selectedVideoName }}</p>
      <button class="btn-soft mt-2 w-full" :disabled="loading || !selectedMatchId || !selectedVideoFile" @click="submitUploadVideo">영상 업로드</button>
    </article>

    <article class="surface p-5">
      <h2 class="section-title">경기 기록</h2>
      <ul class="mt-3 space-y-2">
        <li v-for="match in matches" :key="match.id" class="rounded-2xl border border-slate-200 bg-white px-4 py-3">
          <div class="flex items-center justify-between">
            <p class="text-sm font-semibold text-slate-800">{{ match.playedAt }}</p>
            <span class="chip">영상 {{ match.videos.length }}</span>
          </div>
          <p class="mt-2 text-sm text-slate-700">
            {{ Object.entries(match.teamScores).map(([name, score]) => `${name} ${score}`).join(' · ') }}
          </p>
          <p v-if="match.memo" class="mt-1 text-xs text-slate-500">{{ match.memo }}</p>
          <ul v-if="selectedMatch && selectedMatch.id === match.id" class="mt-2 space-y-1 text-xs text-slate-500">
            <li v-for="video in match.videos" :key="video.id" class="rounded-lg bg-slate-50 px-2 py-1">{{ video.fileName }} ({{ Math.round(video.sizeBytes / 1024 / 1024) }}MB)</li>
          </ul>
        </li>
      </ul>
    </article>
  </div>
</template>
