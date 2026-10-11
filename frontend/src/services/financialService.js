// financialService.js: functions for the Financial API.
// The BACKEND calculates every number. React only asks for them and shows them.

import { request } from './api.js'

// GET /api/financial/farmer/1/profit-loss
// -> { farmerId, totalIncome, totalExpenses, profitLoss, status }
export function getProfitLoss(farmerId) {
    return request(`/api/financial/farmer/${farmerId}/profit-loss`)
}

// GET /api/financial/farmer/1/crop-wise
// -> a list: [{ cropId, cropName, totalIncome, totalExpenses, profitLoss, status }, ...]
export function getCropWise(farmerId) {
    return request(`/api/financial/farmer/${farmerId}/crop-wise`)
}

// GET /api/financial/farmer/1/monthly?year=2026
// -> 12 rows: [{ month, monthName, totalIncome, totalExpenses, profitLoss }, ...]
export function getMonthly(farmerId, year) {
    return request(`/api/financial/farmer/${farmerId}/monthly?year=${year}`)
}