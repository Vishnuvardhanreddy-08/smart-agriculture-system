// Register.jsx: the page where a new farmer creates an account.

import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import AuthLayout from '../components/AuthLayout.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'
import { errorText } from '../services/api.js'

// 10 digits, starting with 6, 7, 8 or 9 (an Indian mobile number). The backend checks this too.
const PHONE_PATTERN = /^[6-9][0-9]{9}$/

const EMPTY_FORM = { name: '', phone: '', email: '', password: '', confirmPassword: '' }

function Register() {
    // t('register.title') finds the word in auth.json.
    const { t } = useTranslation(['auth', 'common'])

    const { currentFarmer, register } = useFarmer()

    const [form, setForm] = useState(EMPTY_FORM)
    const [showPassword, setShowPassword] = useState(false)
    const [error, setError] = useState('')
    const [submitting, setSubmitting] = useState(false)

    // Already logged in? Go straight to the Dashboard.
    if (currentFarmer) {
        return <Navigate to="/" replace />
    }

    // One function for all inputs: event.target.name says which field changed.
    function handleChange(event) {
        setForm({ ...form, [event.target.name]: event.target.value })
    }

    // Quick checks before sending (the backend checks again, it never trusts the browser).
    // The messages come from the translation files, so they appear in the chosen language.
    function validate() {
        if (!form.name.trim()) return t('register.errors.name')
        if (!PHONE_PATTERN.test(form.phone)) return t('register.errors.phone')
        if (form.email.trim() && !/^\S+@\S+\.\S+$/.test(form.email.trim())) {
            return t('register.errors.email')
        }
        if (form.password.length < 8) return t('register.errors.passwordShort')
        if (form.password !== form.confirmPassword) return t('register.errors.passwordMismatch')
        return ''
    }

    async function handleSubmit(event) {
        event.preventDefault() // stop the browser's default page reload

        const problem = validate()
        if (problem) {
            setError(problem)
            return
        }

        setError('')
        setSubmitting(true)
        try {
            // We send the password only once. confirmPassword is just for typing safety.
            await register({
                name: form.name.trim(),
                phone: form.phone,
                email: form.email.trim(),
                password: form.password,
            })
            // On success the farmer is logged in, and the <Navigate> above takes us to the Dashboard.
        } catch (err) {
            setError(errorText(err)) // for example "A farmer with this phone number is already registered."
            setSubmitting(false)
        }
    }

    return (
        <AuthLayout title={t('register.title')}>
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="name">{t('register.name')}</label>
                    <input
                        id="name"
                        name="name"
                        type="text"
                        autoComplete="name"
                        className="form-control"
                        value={form.name}
                        onChange={handleChange}
                    />
                </div>

                <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="phone">{t('register.phone')}</label>
                    <input
                        id="phone"
                        name="phone"
                        type="tel"
                        inputMode="numeric"
                        maxLength={10}
                        autoComplete="username"
                        className="form-control"
                        placeholder={t('register.phonePlaceholder')}
                        value={form.phone}
                        onChange={(event) => setForm({ ...form, phone: event.target.value.replace(/\D/g, '') })}
                    />
                    <div className="form-text">{t('register.phoneHelp')}</div>
                </div>

                <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="email">{t('register.email')}</label>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        autoComplete="email"
                        className="form-control"
                        value={form.email}
                        onChange={handleChange}
                    />
                </div>

                <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="password">{t('register.password')}</label>
                    <input
                        id="password"
                        name="password"
                        type={showPassword ? 'text' : 'password'}
                        autoComplete="new-password"
                        className="form-control"
                        value={form.password}
                        onChange={handleChange}
                    />
                    <div className="form-text">{t('register.passwordHelp')}</div>
                </div>

                <div className="mb-2">
                    <label className="form-label fw-semibold" htmlFor="confirmPassword">{t('register.confirmPassword')}</label>
                    <input
                        id="confirmPassword"
                        name="confirmPassword"
                        type={showPassword ? 'text' : 'password'}
                        autoComplete="new-password"
                        className="form-control"
                        value={form.confirmPassword}
                        onChange={handleChange}
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
                    <label className="form-check-label" htmlFor="showPassword">{t('register.showPassword')}</label>
                </div>

                <button type="submit" className="btn btn-success w-100" disabled={submitting}>
                    {submitting ? t('register.submitting') : t('register.submit')}
                </button>
            </form>

            <p className="text-center mt-4 mb-0">
                {t('register.haveAccount')} <Link to="/login">{t('register.loginLink')}</Link>
            </p>
        </AuthLayout>
    )
}

export default Register