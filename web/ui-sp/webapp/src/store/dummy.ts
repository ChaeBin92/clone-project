import { defineStore } from 'pinia';

export const useDummyStore = defineStore('dummy', {
  state: () => ({ ready: true })
});
