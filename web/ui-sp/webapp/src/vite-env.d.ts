/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string;
  readonly VITE_BASE_URL: string;

  readonly VITE_API_BASE_URL: string;
  readonly VITE_API_SP_PATH: string;
  readonly VITE_API_SP_IF_PATH: string;

  readonly [key: string]: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
