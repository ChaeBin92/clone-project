import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  { path: '/', name: 'home', component: { template: '<div>ui-sp local ready</div>' } }
];

export default createRouter({
  history: createWebHistory(),
  routes
});
