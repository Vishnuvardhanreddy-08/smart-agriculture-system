// FarmerContext.jsx: shares "who is the logged-in farmer?" with the whole app.
// It also holds login(), register() and logout().
// Pages still just call useFarmer() and read currentFarmer.

import { createContext, useContext, useEffect, useState } from 'react'
import { getMyProfile, loginRequest, registerRequest } from '../services/authService.js'
import { SESSION_EXPIRED_EVENT, clearToken, getToken, setToken } from '../services/tokenStorage.js'

// createContext makes the shared "notice board".
const FarmerContext = createContext(null)

// FarmerProvider holds the data and gives it to every component inside it.
export function FarmerProvider({ children }) {
    // The logged-in farmer, or null if nobody is logged in.
    const [currentFarmer, setCurrentFarmer] = useState(null)

    // true while we check a saved token when the app starts.
    // We only need to wait if a token is saved.
    const [loading, setLoading] = useState(() => getToken() !== null)

    // true if the farmer was logged out because the login expired (shown on the Login page).
    const [sessionExpired, setSessionExpired] = useState(false)

    // When the app starts: if a token is saved, ask the server who it belongs to.
    useEffect(() => {
        // The dropdown from Step 12 saved this old value. It is not used any more.
        localStorage.removeItem('currentFarmerId')

        if (getToken()) {
            getMyProfile()
                .then((farmer) => setCurrentFarmer(farmer)) // the token works: we know the farmer
                .catch(() => clearToken()) // the token does not work: forget it
                .finally(() => setLoading(false))
        }
    }, [])

    // Listen for the "session-expired" event that api.js sends after a 401.
    useEffect(() => {
        function handleExpired() {
            setCurrentFarmer(null)
            setSessionExpired(true)
        }
        window.addEventListener(SESSION_EXPIRED_EVENT, handleExpired)
        // Cleanup: stop listening when this component goes away.
        return () => window.removeEventListener(SESSION_EXPIRED_EVENT, handleExpired)
    }, [])

    // Check phone + password. On success: save the token and remember the farmer.
    // If the login fails, the error goes up to the Login page, which shows it.
    async function login(phone, password) {
        const data = await loginRequest(phone, password)
        setToken(data.token)
        setCurrentFarmer(data.farmer)
        setSessionExpired(false)
    }

    // Create an account. The server logs the new farmer in straight away.
    async function register(details) {
        const data = await registerRequest(details)
        setToken(data.token)
        setCurrentFarmer(data.farmer)
        setSessionExpired(false)
    }

    // Forget the token and the farmer.
    function logout() {
        clearToken()
        setCurrentFarmer(null)
        setSessionExpired(false)
    }

    // Everything other components can read.
    const value = { currentFarmer, loading, sessionExpired, login, register, logout }

    return <FarmerContext.Provider value={value}>{children}</FarmerContext.Provider>
}

// useFarmer() is the shortcut every page uses to read the shared data.
// eslint-disable-next-line react-refresh/only-export-components
export function useFarmer() {
    return useContext(FarmerContext)
}