// Navbar.jsx: the green top menu shown on every private page.
// On small screens (phones) the links hide behind a "hamburger" button.

import { useState } from 'react'
// NavLink is like <a>, but it does NOT reload the page,
// and it knows which link is the current page ("active").
import { NavLink, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import LanguageSwitcher from './LanguageSwitcher.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'

// The menu items. We keep the translation KEY (not the words), and translate when drawing.
const links = [
    { to: '/', labelKey: 'nav.dashboard' },
    { to: '/income', labelKey: 'nav.income' },
    { to: '/expenses', labelKey: 'nav.expenses' },
    { to: '/crops', labelKey: 'nav.crops' },
    { to: '/financial-analysis', labelKey: 'nav.analysis' },
    { to: '/reports', labelKey: 'nav.reports' },
]

function Navbar() {
    // t() translates a key into the current language.
    const { t } = useTranslation()

    // "open" is true when the phone menu is expanded. setOpen changes it.
    const [open, setOpen] = useState(false)

    // The logged-in farmer and the logout function, from the context.
    const { currentFarmer, logout } = useFarmer()
    const navigate = useNavigate()

    // Runs when the Logout button is pressed.
    function handleLogout() {
        logout()
        navigate('/login')
    }

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-success">
            <div className="container">
                <span className="navbar-brand fw-bold">🌾 {t('appName')}</span>

                {/* Hamburger button (only visible on small screens). */}
                <button
                    className="navbar-toggler"
                    type="button"
                    aria-label={t('nav.toggleMenu')}
                    onClick={() => setOpen(!open)}
                >
                    <span className="navbar-toggler-icon"></span>
                </button>

                {/* The links. The class "show" is added only when open is true. */}
                <div className={'collapse navbar-collapse' + (open ? ' show' : '')}>
                    <ul className="navbar-nav ms-auto">
                        {links.map((link) => (
                            <li className="nav-item" key={link.to}>
                                <NavLink
                                    to={link.to}
                                    end={link.to === '/'} /* "end": Dashboard is active only on exactly "/" */
                                    className={({ isActive }) => 'nav-link' + (isActive ? ' active fw-bold' : '')}
                                    onClick={() => setOpen(false)} /* close the phone menu after choosing a page */
                                >
                                    {t(link.labelKey)}
                                </NavLink>
                            </li>
                        ))}
                    </ul>

                    {/* The language switch, who is logged in, and the Logout button. */}
                    <div className="d-flex flex-column flex-lg-row align-items-lg-center gap-2 ms-lg-3 my-2 my-lg-0">
                        <LanguageSwitcher light />
                        <span className="navbar-text text-white">👤 {currentFarmer?.name}</span>
                        <button className="btn btn-outline-light" onClick={handleLogout}>
                            {t('logout')}
                        </button>
                    </div>
                </div>
            </div>
        </nav>
    )
}

export default Navbar