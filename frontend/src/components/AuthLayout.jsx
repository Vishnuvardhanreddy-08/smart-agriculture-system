// AuthLayout.jsx: the frame for the Login and Register pages: a centred card with the app name.
// Props: title (the card heading) and children (the form inside the card).

import { useTranslation } from 'react-i18next'
import LanguageSwitcher from './LanguageSwitcher.jsx'

function AuthLayout({ title, children }) {
    // t() translates a key. "common" is the default file, so t('appName') finds common.json.
    const { t } = useTranslation()

    return (
        <div className="container py-5">
            <div className="row justify-content-center">
                <div className="col-12 col-md-8 col-lg-5">
                    {/* The language switch, top right, so it can be found before logging in. */}
                    <div className="d-flex justify-content-end mb-3">
                        <LanguageSwitcher />
                    </div>

                    <div className="text-center mb-4">
                        <div className="display-4">🌾</div>
                        <h1 className="h3 text-success fw-bold">{t('appName')}</h1>
                        <p className="text-muted">{t('tagline')}</p>
                    </div>

                    <div className="card shadow-sm">
                        <div className="card-body p-4">
                            <h2 className="h4 mb-3">{title}</h2>
                            {children}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AuthLayout