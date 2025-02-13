import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from 'tailwindcss'
import svgLoader from 'vite-svg-loader' // SVG Loader 가져오기

// https://vite.dev/config/
export default defineConfig({
    plugins: [vue(), vueDevTools(), svgLoader({ defaultExport: 'component' })],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    css: {
        postcss: {
            plugins: [tailwindcss()],
        },
    },
    server: {
        port: 3000, // Vite 개발 서버 포트를 3000으로 변경
    },
    preview: {
        port: 3000, // 미리보기 서버 포트 설정
        strictPort: true, // 포트 충돌 시 오류 발생
        host: '0.0.0.0', // 외부에서도 접근 가능하도록 설정
    },
})
