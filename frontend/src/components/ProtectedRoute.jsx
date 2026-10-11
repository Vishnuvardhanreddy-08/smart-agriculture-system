// ProtectedRoute.jsx: the gate in front of the private pages.
// Logged in  -> show the page (<Outlet /> = "the child page goes here").
// Logged out -> go to the login page.

import { Navigate, Outlet } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useFarmer } from '../context/FarmerContext.jsx'

function ProtectedRoute() {
    const { t } = useTranslation()
    const { currentFarmer, loading } = useFarmer()

    // Still checking a saved token: wait, so a logged-in farmer does not flash to the login page.
    if (loading) {
        return <p className="container py-5 fs-5">{t('loading')}</p>
    }

    // Nobody is logged in: send them to /login.
    // "replace" means the Back button will not bring them back to this blocked page.
    if (!currentFarmer) {
        return <Navigate to="/login" replace />
    }

    return <Outlet />
}

export default ProtectedRoute