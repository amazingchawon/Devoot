// src\helpers\follow.js

import axios from 'axios'

const instance = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
})

// ✅ 팔로우 요청 수락 API 함수
const acceptFollowRequest = async (token, followId) => {
    if (!followId) {
        console.error('❌ 팔로우 ID가 없음!')
        return
    }

    try {
        console.log(`📨 팔로우 수락 요청: /api/follows/${followId}/accept`)
        await instance.post(
            `/api/follows/${followId}/accept`,
            {},
            {
                headers: { Authorization: `Bearer ${token}` },
            }
        )
        console.log('✅ 팔로우 수락 성공')
    } catch (error) {
        console.error('❌ 팔로우 수락 실패:', error)
        throw error
    }
}
// 팔로워 목록 조회 함수
const readFollowers = async (token, userId, page = 1, size = 10) => {
    try {
        const response = await instance.get(
            `/api/users/${userId}/followers?page=${page}&size=${size}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
            }
        )
        // console.log(response.data)
        return response.data // ✅ response의 data 부분만 반환
    } catch (error) {
        console.error('❌ 팔로워 목록 조회 에러 발생:', error)
        throw error // 에러를 호출한 곳에서 처리
    }
}

// 팔로잉 목록 조회 함수
const readFollowings = async (token, userId, page = 1, size = 10) => {
    try {
        const response = await instance.get(
            `/api/users/${userId}/following?page=${page}&size=${size}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
            }
        )
        return response.data // ✅ response의 data 부분만 반환
    } catch (error) {
        console.error('❌ 팔로워 목록 조회 에러 발생:', error)
        throw error // 에러를 호출한 곳에서 처리
    }
}

// 팔로우 요청 함수
const sendFollowRequest = async (token, userId) => {
    return instance.post(
        `/api/follows`,
        { profileId: userId },
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

// 팔로우 취소 함수
const cancelFollowRequest = async (token, followId) => {
    return instance.delete(`/api/follows/${followId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

export {
    acceptFollowRequest,
    readFollowers,
    readFollowings,
    sendFollowRequest,
    cancelFollowRequest,
}
export default instance
