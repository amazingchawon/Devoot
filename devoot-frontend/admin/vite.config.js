import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'
import svgLoader from 'vite-svg-loader' // SVG Loader 가져오기
import dotenv from 'dotenv'
// https://vite.dev/config/

dotenv.config({ path: 'front.env' })

export default defineConfig({
    define: {
        'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development'),
        '__BACKEND_URL__': JSON.stringify(
            process.env.NODE_ENV === 'production' ? 'http://i12a209.p.ssafy.io' : 'http://localhost:8080'
        ),
    },
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
    preview: {
        proxy: {
            "/api": {
                target: "http://devoot-backend:8080",
                changeOrigin:true,
                secure:false,
                prependPath: true,
            }
        },
        allowedHosts: ['i12a209.p.ssafy.io'],
        port: 3000, // 미리보기 서버 포트 설정
        strictPort: true, // 포트 충돌 시 오류 발생
        host: '0.0.0.0', // 외부에서도 접근 가능하도록 설정
    },
})
