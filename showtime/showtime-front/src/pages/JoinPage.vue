<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useConsole } from '@/composables/useConsole'

const route = useRoute()
const router = useRouter()
const { loading, isAuthenticated, loginUrl, setPendingInviteCode, refreshAll } = useConsole()

onMounted(async () => {
  const code = typeof route.query.code === 'string' ? route.query.code : ''
  if (code) {
    setPendingInviteCode(code)
  }

  await refreshAll()

  if (!isAuthenticated.value) {
    window.location.href = loginUrl
    return
  }

  await router.replace('/meetings')
})
</script>

<template>
  <article class="surface mx-auto max-w-xl p-8 text-center">
    <h2 class="text-2xl font-bold text-[#191f28]">초대 링크 처리 중</h2>
    <p class="mt-2 text-sm text-slate-500">로그인 확인 후 동호회에 자동 가입하고 모임 화면으로 이동합니다.</p>
    <p class="mt-4 text-xs text-slate-400">{{ loading ? '처리 중...' : '잠시만 기다려주세요.' }}</p>
  </article>
</template>
