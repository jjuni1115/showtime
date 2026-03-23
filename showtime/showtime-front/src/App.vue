<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { useConsole } from '@/composables/useConsole'

const navItems = [
  { key: 'dashboard', label: '대시보드', short: '홈', path: '/dashboard' },
  { key: 'club', label: '클럽 관리', short: '클럽', path: '/club' },
  { key: 'meetings', label: '모임', short: '모임', path: '/meetings' },
  { key: 'teams', label: '팀 편성', short: '팀', path: '/teams' },
  { key: 'community', label: '커뮤니티', short: '소통', path: '/community' },
  { key: 'matches', label: '경기 기록', short: '경기', path: '/matches' },
  { key: 'members', label: '멤버', short: '멤버', path: '/members' },
]

const route = useRoute()
const {
  loading,
  error,
  profile,
  isAuthenticated,
  loginUrl,
  clubs,
  selectedClubId,
  refreshAll,
  onChangeClub,
} = useConsole()

const currentPath = computed(() => route.path)

onMounted(refreshAll)
</script>

<template>
  <div class="app-shell">
    <div class="mx-auto max-w-[1300px] p-4 pb-24 md:p-6 md:pb-8">
      <header class="mb-4 rounded-3xl bg-[#1f6fff] px-6 py-6 text-white shadow-[0_14px_30px_rgba(31,111,255,0.35)] md:mb-6 md:flex md:items-center md:justify-between">
        <div>
          <p class="text-xs font-medium uppercase tracking-[0.22em] text-blue-100">Showtime</p>
          <h1 class="mt-2 text-2xl font-bold leading-tight md:text-3xl">운영진 콘솔</h1>
          <p class="mt-1 text-sm text-blue-100">
            {{ profile ? `${profile.displayName}님, 클럽 운영을 시작하세요.` : '클럽 운영, 출석 기반 팀 편성, 경기 기록을 한 곳에서 관리합니다.' }}
          </p>
        </div>

        <div class="mt-4 flex flex-col gap-2 md:mt-0 md:min-w-72">
          <select :value="selectedClubId" class="field-input !bg-white/95" @change="onChangeClub(($event.target as HTMLSelectElement).value)">
            <option value="" disabled>클럽 선택</option>
            <option v-for="club in clubs" :key="club.id" :value="club.id">{{ club.name }} ({{ club.myRole }})</option>
          </select>
          <button class="btn-ghost" :disabled="loading" @click="refreshAll">데이터 새로고침</button>
        </div>
      </header>

      <section v-if="!isAuthenticated" class="surface p-8 text-center">
        <h2 class="text-2xl font-bold text-[#191f28]">Google 로그인이 필요합니다</h2>
        <p class="mt-2 text-sm text-slate-500">API 인증 세션(JSESSIONID)을 만든 뒤 다시 화면을 새로고침 해주세요.</p>
        <a class="btn-primary mt-6 inline-flex" :href="loginUrl">Google 로그인 시작</a>
      </section>

      <template v-else>
        <p v-if="error" class="mb-4 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{{ error }}</p>

        <div class="grid gap-4 lg:grid-cols-[260px_minmax(0,1fr)] lg:gap-6">
          <aside class="surface hidden p-3 lg:block lg:p-4">
            <p class="px-2 pb-2 text-xs font-semibold uppercase tracking-[0.12em] text-slate-400">Menu</p>
            <nav class="grid gap-2">
              <RouterLink
                v-for="item in navItems"
                :key="item.key"
                :to="item.path"
                class="nav-btn"
                :class="{ 'nav-btn-active': currentPath === item.path }"
              >
                <span class="block text-sm font-semibold">{{ item.label }}</span>
              </RouterLink>
            </nav>
          </aside>

          <section>
            <RouterView />
          </section>
        </div>

        <nav class="mobile-nav lg:hidden">
          <RouterLink
            v-for="item in navItems"
            :key="item.key"
            :to="item.path"
            class="mobile-nav-btn"
            :class="{ 'mobile-nav-btn-active': currentPath === item.path }"
          >
            {{ item.short }}
          </RouterLink>
        </nav>
      </template>
    </div>
  </div>
</template>
