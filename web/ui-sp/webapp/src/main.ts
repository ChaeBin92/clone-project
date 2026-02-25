import { createApp } from 'vue';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import VueDOMPurifyHTML from 'vue-dompurify-html';
import App from './App.vue';
import router from './router';

import '@/assets/scss/style.scss';

if (import.meta.env.DEV) {
  // 개발 전용 스타일은 dev 환경에서만 로드한다.
  void import('@/assets/scss/_dev.scss');
}

const app = createApp(App);

// 전역 상태 스토어를 초기화하고 persistedstate 플러그인을 등록한다.
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);

// v-html 사용 시 XSS 방지를 위해 sanitize 플러그인을 기본 활성화한다.
app.use(VueDOMPurifyHTML, {
  default: {
    USE_PROFILES: { html: true }
  }
});

router.isReady().then(() => {
  app.mount('#app');

  // 첫 화면 스피너는 앱 마운트 완료 시 제거한다.
  const spinner = document.querySelector('.spinner-dimmed');
  if (spinner) {
    spinner.remove();
  }

  console.log('ui-sp local dev running');
});
