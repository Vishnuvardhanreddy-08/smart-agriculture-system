// api.js: ONE place that knows how to talk to our Spring Boot backend.
// Every page uses this, so token handling and error handling are written only once.

import i18n from '../i18n.js'
import { SESSION_EXPIRED_EVENT, clearToken, getToken } from './tokenStorage.js'

// import.meta.env reads the variable from .env.development
// (and from the production settings later).
const BASE_URL = import.meta.env.VITE_API_BASE_URL

// request("/api/crops/farmer/5") sends a request and returns the JSON answer.
// "async/await" means: wait for the server's answer without freezing the page.
export async function request(path, options = {}) {
    // "noAuth" is OUR OWN option: login and register must not send a token.
    // We take it out of the options, because fetch does not know it.
    const { noAuth, ...fetchOptions } = options

    // The saved login token (or null if nobody is logged in).
    const token = noAuth ? null : getToken()

    // NEW: Accept-Language tells the server which language the farmer is using ("en" or "te"),
    // so the server's own messages (errors) come back in that language.
    const headers = { 'Accept-Language': i18n.language }

    // Only send the "Content-Type: JSON" header when we send a body (POST/PUT).
    if (fetchOptions.body) {
        headers['Content-Type'] = 'application/json'
    }
    // The token goes in the Authorization header, in the standard "Bearer" format.
    if (token) {
        headers.Authorization = `Bearer ${token}`
    }

    let response
    try {
        // fetch sends the HTTP request, like Postman does.
        response = await fetch(BASE_URL + path, {
            ...fetchOptions,
            headers: { ...headers, ...(fetchOptions.headers || {}) },
        })
    } catch {
        // fetch itself fails when the server is off or unreachable.
        // i18n.t gives the message in the farmer's current language.
        throw new Error(i18n.t('errors.network'))
    }

    // 401 on a request that DID carry a token means the login is no longer valid
    // (expired or fake). We delete the token and tell the app, which logs the farmer out.
    if (response.status === 401 && token) {
        clearToken()
        window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT))
        throw new Error(i18n.t('errors.sessionEnded'))
    }

    // 204 means "success, nothing to send back" (used after DELETE).
    if (response.status === 204) {
        return null
    }

    // Read the JSON body (if there is one).
    let data = null
    try {
        data = await response.json()
    } catch {
        // The response had no JSON body. That is fine.
    }

    // response.ok is false for 400, 403, 404, 409, 500...
    // Our backend's error JSON has a "message", which now arrives in the farmer's language.
    if (!response.ok) {
        const error = new Error((data && data.message) || i18n.t('errors.generic'))
        // For validation errors the backend also sends "fieldErrors". We keep them on the error.
        error.fieldErrors = data && data.fieldErrors
        throw error
    }

    return data
}

// errorText(err) turns an error into one readable sentence for the screen.
// If there are field errors (like "Area must be greater than 0"), those are more helpful.
export function errorText(err) {
    if (err.fieldErrors) {
        return Object.values(err.fieldErrors).join(' ')
    }
    return err.message
}