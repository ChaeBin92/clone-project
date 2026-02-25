import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    base: env.VITE_BASE_URL || '/ui-sp',
    plugins: [vue()],
    resolve: {
      alias: [{ find: '@', replacement: '/src' }]
    },
    server: {
      host: '0.0.0.0',
      port: 55000,
      proxy: {
        [env.VITE_API_SP_PATH]: {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true,
          secure: false
        },
        [env.VITE_API_SP_IF_PATH]: {
          target: env.VITE_API_BASE_URL,
          changeOrigin: true,
          secure: false
        }
      }
    }
  };
});
