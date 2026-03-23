import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/dashboard', component: () => import('@/pages/DashboardPage.vue') },
    { path: '/club', component: () => import('@/pages/ClubPage.vue') },
    { path: '/meetings', component: () => import('@/pages/MeetingsPage.vue') },
    { path: '/teams', component: () => import('@/pages/TeamsPage.vue') },
    { path: '/community', component: () => import('@/pages/CommunityPage.vue') },
    { path: '/matches', component: () => import('@/pages/MatchesPage.vue') },
    { path: '/members', component: () => import('@/pages/MembersPage.vue') },
  ],
})

export default router
