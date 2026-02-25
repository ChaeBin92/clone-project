import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

import VueDOMPurifyHTML from 'vue-dompurify-html';

import '@vuepic/vue-datepicker/dist/main.css';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-quartz.css';

import '@/assets/scss/style.scss';
import '@/assets/scss/_dev.scss';

const app = createApp(App);

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);
app.use(VueDOMPurifyHTML, { default: { USE_PROFILES: { html: true } } });

router.isReady().then(() => {
  app.mount('#app');
  console.log('ui-sp local dev running on 55000');
});
