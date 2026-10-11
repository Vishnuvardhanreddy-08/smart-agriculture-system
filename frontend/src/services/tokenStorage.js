// tokenStorage.js: saves, reads and deletes the login token in the browser.
// localStorage keeps it even after the page is refreshed or the browser is closed.

const TOKEN_KEY = 'smartAgricultureToken'

// The name of the browser event that api.js sends when the server says "your login is no longer valid".
export const SESSION_EXPIRED_EVENT = 'session-expired'

export function getToken() {
    return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
    localStorage.removeItem(TOKEN_KEY)
}