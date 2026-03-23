<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useConsole } from '@/composables/useConsole'

const router = useRouter()
const { clubs, adminClubCount, memberClubCount, members, avgHeight, selectedClub, loading } = useConsole()

const go = (path: string) => router.push(path)
</script>

<template>
  <div class="space-y-4 lg:space-y-6">
    <div class="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
      <article class="surface p-5">
        <p class="metric-label">내 클럽</p>
        <p class="metric-value">{{ clubs.length }}</p>
        <p class="metric-sub">가입된 클럽 수</p>
      </article>
      <article class="surface p-5">
        <p class="metric-label">운영중</p>
        <p class="metric-value">{{ adminClubCount }}</p>
        <p class="metric-sub">ADMIN 권한 클럽</p>
      </article>
      <article class="surface p-5">
        <p class="metric-label">참여중</p>
        <p class="metric-value">{{ memberClubCount }}</p>
        <p class="metric-sub">MEMBER 권한 클럽</p>
      </article>
      <article class="surface p-5">
        <p class="metric-label">등록 멤버</p>
        <p class="metric-value">{{ members.length }}</p>
        <p class="metric-sub">평균 {{ avgHeight.toFixed(1) }} cm</p>
      </article>
    </div>

    <article class="surface p-5">
      <h2 class="section-title">빠른 액션</h2>
      <div class="mt-4 grid gap-2 sm:grid-cols-2 xl:grid-cols-4">
        <button class="btn-primary" :disabled="loading" @click="go('/club')">클럽 생성/가입</button>
        <button class="btn-soft" :disabled="loading" @click="go('/meetings')">모임 관리</button>
        <button class="btn-soft" :disabled="loading" @click="go('/teams')">팀 편성 실행</button>
        <button class="btn-soft" :disabled="loading" @click="go('/matches')">경기 기록 작성</button>
      </div>
    </article>

    <article class="surface p-5">
      <h2 class="section-title">선택된 클럽</h2>
      <div v-if="selectedClub" class="mt-4 grid gap-3 sm:grid-cols-[1fr_auto] sm:items-center">
        <div>
          <p class="text-lg font-semibold text-slate-900">{{ selectedClub.name }}</p>
          <p class="mt-1 text-sm text-slate-500">{{ selectedClub.homeCourt }} · {{ selectedClub.myRole }}</p>
        </div>
        <span class="chip">{{ selectedClub.myRole }}</span>
      </div>
      <p v-else class="mt-4 text-sm text-slate-500">선택된 클럽이 없습니다. 클럽을 생성하거나 가입해 주세요.</p>
    </article>
  </div>
</template>
