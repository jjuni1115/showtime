<script setup lang="ts">
import { useConsole } from '@/composables/useConsole'

const { loading, selectedClubId, postContent, posts, submitPost } = useConsole()
</script>

<template>
  <div class="grid gap-4 xl:grid-cols-[420px_minmax(0,1fr)]">
    <article class="surface p-5">
      <h2 class="section-title">글 작성</h2>
      <textarea v-model="postContent" class="field-input mt-3 min-h-32" placeholder="공지/자유글을 입력하세요." />
      <button class="btn-primary mt-3 w-full" :disabled="loading || !selectedClubId" @click="submitPost">게시글 등록</button>
    </article>

    <article class="surface p-5">
      <h2 class="section-title">최신 글</h2>
      <ul class="mt-3 space-y-2">
        <li v-for="post in posts" :key="post.id" class="rounded-2xl border border-slate-200 bg-white px-4 py-3">
          <div class="flex items-center justify-between gap-2">
            <p class="text-sm font-semibold text-slate-800">{{ post.authorName }}</p>
            <p class="text-xs text-slate-500">{{ new Date(post.createdAt).toLocaleString() }}</p>
          </div>
          <p class="mt-2 text-sm text-slate-700 whitespace-pre-wrap">{{ post.content }}</p>
        </li>
      </ul>
    </article>
  </div>
</template>
