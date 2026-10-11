// LanguageSwitcher.jsx: two buttons, "English" and "తెలుగు". The active one is highlighted.
// Prop: light - true on the green navbar (white buttons), false on white pages (green buttons).

import { useTranslation } from 'react-i18next'

// Each language name is written in ITS OWN language, so people can always find theirs.
const LANGUAGES = [
    { code: 'en', label: 'English' },
    { code: 'te', label: 'తెలుగు' },
]

function LanguageSwitcher({ light = false }) {
    // t = the translate function. i18n = the translation engine (it knows the current language).
    const { t, i18n } = useTranslation()

    const buttonColor = light ? 'btn-outline-light' : 'btn-outline-success'

    return (
        // btn-group puts the two buttons side by side as one control.
        <div className="btn-group" role="group" aria-label={t('language.label')}>
            {LANGUAGES.map((language) => {
                const isActive = i18n.resolvedLanguage === language.code
                return (
                    <button
                        key={language.code}
                        type="button"
                        className={`btn ${buttonColor}${isActive ? ' active' : ''}`}
                        aria-pressed={isActive}
                        // changeLanguage switches the whole app at once. Nothing reloads.
                        onClick={() => i18n.changeLanguage(language.code)}
                    >
                        {language.label}
                    </button>
                )
            })}
        </div>
    )
}

export default LanguageSwitcher