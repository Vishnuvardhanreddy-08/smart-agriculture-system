// authService.js: functions for login, register and "who am I?".

import { request } from './api.js'

// POST /api/auth/login -> { token, tokenType, farmer }
// noAuth: true because a farmer who is logging in has no token yet.
export function loginRequest(phone, password) {
    return request('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ phone, password }),
        noAuth: true,
    })
}

// POST /api/auth/register -> { token, tokenType, farmer }
// "details" is { name, phone, email, password }
export function registerRequest(details) {
    return request('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify(details),
        noAuth: true,
    })
}

// GET /api/farmers/me -> the logged-in farmer { id, name, phone, email }
// The server knows who it is from the token. We send no id.
export function getMyProfile() {
    return request('/api/farmers/me')
}