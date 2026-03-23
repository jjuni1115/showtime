<script setup lang="ts">
import { useConsole } from '@/composables/useConsole'

const { members } = useConsole()
</script>

<template>
  <div class="surface p-5">
    <h2 class="section-title">멤버 목록</h2>
    <div class="mt-4 grid gap-2 sm:grid-cols-2 xl:grid-cols-3">
      <article v-for="member in members" :key="member.id" class="rounded-2xl border border-slate-200 bg-white px-3 py-3">
        <div class="flex items-center justify-between">
          <h3 class="text-sm font-semibold text-slate-900">{{ member.name }}</h3>
          <span class="chip">{{ member.position }}</span>
        </div>
        <p class="mt-1 text-xs text-slate-500">{{ member.skillLevel }} · {{ member.heightCm }} cm · {{ member.style }}</p>
      </article>
    </div>
  </div>
</template>
