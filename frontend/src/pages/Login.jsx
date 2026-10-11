// Login.jsx: the login page. The farmer types phone number + password.

import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import AuthLayout from '../components/AuthLayout.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'
import { errorText } from '../services/api.js'

function Login() {
    // The first name in the list ('auth') is the default file. t('login.title') finds auth.json.
    // To use a word from the other file, write its name first: t('common:errors.sessionEnded').
    const { t } = useTranslation(['auth', 'common'])

    // login() comes from the context. sessionExpired is true if the last login ran out.
    const { currentFarmer, login, sessionExpired } = useFarmer()

    // State = the page's memory.
    const [phone, setPhone] = useState('')
    const [password, setPassword] = useState('')
    const [showPassword, setShowPassword] = useState(false)
    const [error, setError] = useState('')
    const [submitting, setSubmitting] = useState(false)

    // Already logged in? Go straight to the Dashboard.
    if (currentFarmer) {
        return <Navigate to="/" replace />
    }

    // Runs when the farmer presses Login (or Enter).
    async function handleSubmit(event) {
        event.preventDefault() // stop the browser's default page reload

        if (!phone || !password) {
            setError(t('login.missingFields'))
            return
        }

        setError('')
        setSubmitting(true)
        try {
            await login(phone, password)
            // On success, currentFarmer is set, and the <Navigate> above takes us to the Dashboard.
        } catch (err) {
            setError(errorText(err)) // for example "Invalid phone number or password"
            setSubmitting(false)
        }
    }

    return (
        <AuthLayout title={t('login.title')}>
            {/* Shown only when the farmer was logged out because the login expired. */}
            {sessionExpired && <div className="alert alert-info">{t('common:errors.sessionEnded')}</div>}

            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="phone">{t('login.phone')}</label>
                    {/* type="tel" shows the number keypad on phones. */}
                    <input
                        id="phone"
                        type="tel"
                        inputMode="numeric"
                        maxLength={10}
                        autoComplete="username"
                        className="form-control"
                        placeholder={t('login.phonePlaceholder')}
                        value={phone}
                        // replace(/\D/g, '') removes everything that is not a digit.
                        onChange={(event) => setPhone(event.target.value.replace(/\D/g, ''))}
                    />
                </div>

                <div className="mb-2">
                    <label className="form-label fw-semibold" htmlFor="password">{t('login.password')}</label>
                    <input
                        id="password"
                        type={showPassword ? 'text' : 'password'}
                        autoComplete="current-password"
                        className="form-control"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                    />
                </div>

                <div className="form-check mb-4">
                    <input
                        id="showPassword"
                        type="checkbox"
                        className="form-check-input"
                        checked={showPassword}
                        onChange={(event) => setShowPassword(event.target.checked)}
                    />
                    <label className="form-check-label" htmlFor="showPassword">{t('login.showPassword')}</label>
                </div>

                <button type="submit" className="btn btn-success w-100" disabled={submitting}>
                    {submitting ? t('login.submitting') : t('login.submit')}
                </button>
            </form>

            <p className="text-center mt-4 mb-0">
                {t('login.newFarmer')} <Link to="/register">{t('login.registerLink')}</Link>
            </p>
        </AuthLayout>
    )
}

export default Login