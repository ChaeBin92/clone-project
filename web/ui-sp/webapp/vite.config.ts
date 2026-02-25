import { fileURLToPath, URL } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';

const DEFAULT_BASE_URL = '/ui-sp';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const baseUrl = env.VITE_BASE_URL || DEFAULT_BASE_URL;
  const devHost = env.VITE_DEV_HOST || 'localhost';
  const devPort = Number(env.VITE_DEV_PORT || 55000);

  const proxy: Record<string, { target: string; changeOrigin: boolean; secure: boolean }> = {};
  const shouldUseProxy = env.VITE_USE_PROXY === 'true' && !!env.VITE_API_BASE_URL;

  if (shouldUseProxy && env.VITE_API_SP_PATH) {
    proxy[env.VITE_API_SP_PATH] = {
      target: env.VITE_API_BASE_URL,
      changeOrigin: true,
      secure: false
    };
  }

  if (shouldUseProxy && env.VITE_API_SP_IF_PATH) {
    proxy[env.VITE_API_SP_IF_PATH] = {
      target: env.VITE_API_BASE_URL,
      changeOrigin: true,
      secure: false
    };
  }

  return {
    // 실제 배포 서브패스와 동일한 값으로 관리해야 한다.
    base: baseUrl,
    plugins: [vue()],
    resolve: {
      // 절대 경로 alias를 사용해 OS별 경로 차이를 제거한다.
      alias: [{ find: '@', replacement: fileURLToPath(new URL('./src', import.meta.url)) }]
    },
    server: {
      host: devHost,
      port: devPort,
      ...(Object.keys(proxy).length > 0 ? { proxy } : {})
    }
  };
});
