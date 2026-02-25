import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';

// 앱 기본 진입 라우트
const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  }
];

export default createRouter({
  // Vite base 경로와 동일하게 맞춰 배포 시 라우팅 불일치를 방지한다.
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});
