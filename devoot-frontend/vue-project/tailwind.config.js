/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}', './src/**/*.css'],
    theme: {
        extend: {
            colors: {
                primary: {
                    100: '#CDE3FF',
                    200: '#9AC7FF',
                    300: '#68AAFF',
                    400: '#358EFF',
                    500: '#0372FF',
                },
                gray: {
                    100: '#F4F6F8',
                    200: '#E8EBEE',
                    300: '#9DA0A3',
                    400: '#75787B',
                    500: '#25282B',
                },
            },
            fontFamily: {
                sans: ['Pretendard', 'ui-sans-serif', 'system-ui'], // Pretendard를 기본 sans로 설정
            },
            fontSize: {
                h1: ['28px', { lineHeight: '1.2', fontWeight: '700' }],
                h2: ['24px', { lineHeight: '1.3', fontWeight: '500' }],
                h3: ['18px', { lineHeight: '1.4', fontWeight: '600' }],
                body: ['14px', { lineHeight: '1.5', fontWeight: '400' }],
                'body-bold': ['14px', { lineHeight: '1.5', fontWeight: '600' }],
                caption: ['12px', { lineHeight: '1.4', fontWeight: '400' }],
                'caption-sm': ['8px', { lineHeight: '1.2', fontWeight: '400' }],
            },
        },
    },
    safelist: [
        // Tailwind가 반드시 빌드할 클래스 목록
        'text-primary-100',
        'text-primary-200',
        'text-primary-300',
        'text-primary-400',
        'text-primary-500',
    ],
    plugins: [],
}
