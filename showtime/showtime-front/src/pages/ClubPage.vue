<script setup lang="ts">
import { useConsole } from '@/composables/useConsole'

const {
  loading,
  clubs,
  selectedClubId,
  clubForm,
  joinCode,
  lastInviteLink,
  submitCreateClub,
  submitJoinClub,
  submitInvite,
  copyInvite,
  onChangeClub,
} = useConsole()
</script>

<template>
  <div class="grid gap-4 xl:grid-cols-2">
    <article class="surface p-5">
      <h2 class="section-title">클럽 생성</h2>
      <div class="mt-4 space-y-3">
        <div>
          <label class="field-label">팀명</label>
          <input v-model="clubForm.name" class="field-input" placeholder="쇼타임 농구회" />
        </div>
        <div>
          <label class="field-label">주 경기장</label>
          <input v-model="clubForm.homeCourt" class="field-input" placeholder="잠실 학생체육관" />
        </div>
        <div>
          <label class="field-label">팀 이미지 URL</label>
          <input v-model="clubForm.imageUrl" class="field-input" placeholder="https://..." />
        </div>
        <button class="btn-primary w-full" :disabled="loading" @click="submitCreateClub">클럽 만들기</button>
      </div>
    </article>

    <article class="surface p-5">
      <h2 class="section-title">가입/초대</h2>
      <div class="mt-4 space-y-3">
        <label class="field-label">Invite code</label>
        <input v-model="joinCode" class="field-input" placeholder="84d03dca..." />
        <button class="btn-primary w-full" :disabled="loading" @click="submitJoinClub">코드로 가입</button>
      </div>

      <h3 class="mt-6 text-sm font-semibold text-slate-800">내 클럽 선택</h3>
      <select :value="selectedClubId" class="field-input mt-2" @change="onChangeClub(($event.target as HTMLSelectElement).value)">
        <option value="" disabled>클럽 선택</option>
        <option v-for="club in clubs" :key="club.id" :value="club.id">{{ club.name }} ({{ club.myRole }})</option>
      </select>

      <div class="mt-3 grid gap-2 sm:grid-cols-2">
        <button class="btn-soft" :disabled="loading || !selectedClubId" @click="submitInvite">초대 링크 만들기</button>
        <button class="btn-soft" :disabled="!lastInviteLink" @click="copyInvite">초대 링크 복사</button>
      </div>

      <p v-if="lastInviteLink" class="mt-3 rounded-xl bg-slate-50 px-3 py-2 text-xs text-slate-600 break-all">{{ lastInviteLink }}</p>
    </article>
  </div>
</template>
