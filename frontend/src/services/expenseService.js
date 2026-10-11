// expenseService.js: functions for the Expense API.
// Every function sends the farmer's id, so the backend returns/changes only THAT farmer's expenses.

import { request } from './api.js'

// GET /api/expenses/farmer/1?cropId=..&from=..&to=..&search=..
// "filters" is optional: { cropId, from, to, search }. Empty filters are left out of the URL.
export function getExpenses(farmerId, filters = {}) {
    // URLSearchParams builds the "?name=value&name2=value2" part safely
    // (it also encodes spaces and special characters).
    const params = new URLSearchParams()
    if (filters.cropId) params.append('cropId', filters.cropId)
    if (filters.from) params.append('from', filters.from)
    if (filters.to) params.append('to', filters.to)
    if (filters.search && filters.search.trim()) params.append('search', filters.search.trim())

    const query = params.toString()
    return request(`/api/expenses/farmer/${farmerId}` + (query ? `?${query}` : ''))
}

// POST /api/expenses -> create an expense. The expense object includes farmerId.
export function createExpense(expense) {
    return request('/api/expenses', { method: 'POST', body: JSON.stringify(expense) })
}

// PUT /api/expenses/5 -> update expense 5. The expense object includes farmerId.
export function updateExpense(id, expense) {
    return request(`/api/expenses/${id}`, { method: 'PUT', body: JSON.stringify(expense) })
}

// DELETE /api/expenses/5?farmerId=1 -> delete expense 5, only if it belongs to farmer 1
export function deleteExpense(id, farmerId) {
    return request(`/api/expenses/${id}?farmerId=${farmerId}`, { method: 'DELETE' })
}