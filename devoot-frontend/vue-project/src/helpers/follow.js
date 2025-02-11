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

export { acceptFollowRequest }
export default instance
