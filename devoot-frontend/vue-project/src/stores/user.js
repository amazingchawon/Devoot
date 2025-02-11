// src\stores\user.js

import { defineStore } from 'pinia'
import {
    auth,
    googleProvider,
    githubProvider,
    signInWithPopup,
    setPersistence,
    browserLocalPersistence,
    onAuthStateChanged,
} from '@/firebase'
import { getUserInfo } from '@/helpers/api' // API 함수 불러오기
import router from '../router'

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null, // 사용자 정보
        token: null, // Firebase 토큰
        userId: null, // 사용자 ID
    }),

    getters: {
        isAuthenticated: (state) => !!state.token, // 로그인 여부 -> 동적으로 계산
    },

    actions: {
        // 구글 로그인
        async loginWithGoogle() {
            try {
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지
                const result = await signInWithPopup(auth, googleProvider)

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)
                console.log(this.token)

                // API 파일에서 getUserInfo() 호출
                const res = await getUserInfo(this.token)

                this.userId = res.data.profileId // 서비스의 유저 ID 저장

                return true // 로그인 성공
            } catch (error) {
                if (!error.response) {
                    // Firebase 로그인 자체 오류만 로깅
                    console.error('🚨 Firebase 로그인 실패:', error)
                }
                return false
            }
        },

        // 깃허브 로그인
        async loginWithGithub() {
            try {
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지
                const result = await signInWithPopup(auth, githubProvider)

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)

                // API 파일에서 getUserInfo() 호출
                const res = await getUserInfo(this.token)
                console.log('Login success! user:', res.data)

                this.userId = res.data.profileId // 서비스의 유저 ID 저장

                return true // 로그인 성공
            } catch (error) {
                if (!error.response) {
                    // Firebase 로그인 자체 오류만 로깅
                    console.error('🚨 Firebase 로그인 실패:', error)
                }
                return false
            }
        },

        async logout() {
            await auth.signOut()
            this.user = null
            this.token = null
            this.userId = null
            router.push({ name: 'home' }) // 홈 페이지로 이동
        },

        // 로그인 유지 기능 추가
        async fetchUser() {
            onAuthStateChanged(auth, async (user) => {
                if (user) {
                    this.user = user
                    this.token = await user.getIdToken(true)

                    // API에서 추가 유저 정보 가져오기
                    try {
                        const res = await getUserInfo(this.token)
                        this.userId = res.data.profileId // 로그인 유지 시에도 userId 설정
                    } catch (error) {
                        console.error('🚨 유저 정보 가져오기 실패:', error)
                    }
                } else {
                    this.user = null
                    this.token = null
                    this.userId = null // 로그아웃 시 userId 초기화
                }
            })
        },
    },
})
