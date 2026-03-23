<script setup lang="ts">
import { useConsole } from '@/composables/useConsole'

const { loading, attendanceDate, teamNamesInput, teamResult, submitTeamGenerate } = useConsole()
</script>

<template>
  <div class="grid gap-4 xl:grid-cols-[380px_minmax(0,1fr)]">
    <article class="surface p-5">
      <h2 class="section-title">팀 자동 편성</h2>
      <p class="mt-1 text-xs text-slate-500">참석 데이터가 등록된 날짜를 기준으로 분배합니다.</p>

      <div class="mt-4 space-y-3">
        <input v-model="attendanceDate" class="field-input" type="date" />
        <input v-model="teamNamesInput" class="field-input" placeholder="Blue, Black, White" />
        <button class="btn-primary w-full" :disabled="loading" @click="submitTeamGenerate">편성 실행</button>
      </div>
    </article>

    <article class="surface p-5">
      <h2 class="section-title">편성 결과</h2>
      <div v-if="!teamResult" class="mt-4 rounded-2xl border border-dashed border-slate-300 px-4 py-12 text-center text-sm text-slate-500">
        아직 결과가 없습니다.
      </div>

      <div v-else class="mt-4 space-y-4">
        <div class="grid gap-2 sm:grid-cols-3">
          <div class="chip-card"><p class="chip-title">실력 격차</p><p class="chip-value">{{ teamResult.summary.teamSkillGap }}</p></div>
          <div class="chip-card"><p class="chip-title">인원 격차</p><p class="chip-value">{{ teamResult.summary.teamSizeGap }}</p></div>
          <div class="chip-card"><p class="chip-title">중복</p><p class="chip-value">{{ teamResult.summary.repeatedAssignments }}</p></div>
        </div>

        <div class="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          <section v-for="team in teamResult.teams" :key="team.name" class="rounded-2xl border border-slate-200 bg-white p-4">
            <h3 class="text-base font-bold text-slate-900">{{ team.name }}</h3>
            <p class="mt-1 text-xs text-slate-500">총 {{ team.totalSkillScore }} · 평균 {{ team.averageHeightCm.toFixed(1) }} cm</p>
            <ul class="mt-3 space-y-1.5">
              <li v-for="member in team.members" :key="member.id" class="flex items-center justify-between rounded-lg bg-slate-50 px-2.5 py-1.5 text-sm">
                <span class="font-medium text-slate-700">{{ member.name }}</span>
                <span class="text-xs text-slate-500">{{ member.position }} · {{ member.skillLevel }}</span>
              </li>
            </ul>
          </section>
        </div>
      </div>
    </article>
  </div>
</template>
