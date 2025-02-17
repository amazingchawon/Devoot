import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'
import svgLoader from 'vite-svg-loader' // SVG Loader 가져오기

// https://vite.dev/config/
export default defineConfig({
    plugins: [vue(), vueDevTools(), tailwindcss(), svgLoader()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    server: {
        port: 3000, // ✅ 포트 설정을 server 객체 내부에 추가
        strictPort: true, // (선택) 포트가 사용 중이면 실행 중지 (자동 변경 방지)
        host: true, // (선택) LAN에서 접속 가능하도록 설정 (기본값: 'localhost')
    },
})
