// incomeService.js: functions for the Income API.
// Every function sends the farmer's id, so the backend returns/changes only THAT farmer's income.

import { request } from './api.js'

// GET /api/income/farmer/1?cropId=..&from=..&to=..&search=..
// "filters" is optional: { cropId, from, to, search }. Empty filters are left out of the URL.
export function getIncome(farmerId, filters = {}) {
    // URLSearchParams builds the "?name=value&name2=value2" part safely
    // (it also encodes spaces and special characters).
    const params = new URLSearchParams()
    if (filters.cropId) params.append('cropId', filters.cropId)
    if (filters.from) params.append('from', filters.from)
    if (filters.to) params.append('to', filters.to)
    if (filters.search && filters.search.trim()) params.append('search', filters.search.trim())

    const query = params.toString()
    return request(`/api/income/farmer/${farmerId}` + (query ? `?${query}` : ''))
}

// POST /api/income -> create income. The income object includes farmerId.
export function createIncome(income) {
    return request('/api/income', { method: 'POST', body: JSON.stringify(income) })
}

// PUT /api/income/5 -> update income 5. The income object includes farmerId.
export function updateIncome(id, income) {
    return request(`/api/income/${id}`, { method: 'PUT', body: JSON.stringify(income) })
}

// DELETE /api/income/5?farmerId=1 -> delete income 5, only if it belongs to farmer 1
export function deleteIncome(id, farmerId) {
    return request(`/api/income/${id}?farmerId=${farmerId}`, { method: 'DELETE' })
}