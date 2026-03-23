<script setup lang="ts">
import { computed } from 'vue'
import { useConsole } from '@/composables/useConsole'

const {
  loading,
  matches,
  matchPlayedAt,
  matchMemo,
  matchScoresInput,
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
        <input v-model="matchPlayedAt" class="field-input" type="date" />
        <input v-model="matchScoresInput" class="field-input" placeholder="Blue:21, Black:18, White:16" />
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
