// cropService.js: functions for the Crop API.
// Every function that reads or deletes sends the farmer's id,
// so the backend returns only THAT farmer's crops.

import { request } from './api.js'

// GET /api/crops/farmer/1 -> crops of farmer 1
export function getCrops(farmerId) {
    return request(`/api/crops/farmer/${farmerId}`)
}

// POST /api/crops -> create a crop. The crop object includes farmerId.
export function createCrop(crop) {
    return request('/api/crops', { method: 'POST', body: JSON.stringify(crop) })
}

// PUT /api/crops/5 -> update crop 5. The crop object includes farmerId.
export function updateCrop(id, crop) {
    return request(`/api/crops/${id}`, { method: 'PUT', body: JSON.stringify(crop) })
}

// DELETE /api/crops/5?farmerId=1 -> delete crop 5, only if it belongs to farmer 1
export function deleteCrop(id, farmerId) {
    return request(`/api/crops/${id}?farmerId=${farmerId}`, { method: 'DELETE' })
}