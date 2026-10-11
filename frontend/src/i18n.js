// i18n.js: sets up translation. English ("en") and Telugu ("te").
// This file is imported once, in main.jsx, before the app starts.

import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'

// The translation files. Each area of the app has its own file ("namespace").
import enCommon from './locales/en/common.json'
import enAuth from './locales/en/auth.json'
import enCrops from './locales/en/crops.json'
import enIncome from './locales/en/income.json'
import enExpenses from './locales/en/expenses.json'
import enFinance from './locales/en/finance.json'
import enDashboard from './locales/en/dashboard.json'
import enAnalysis from './locales/en/analysis.json'
import enReports from './locales/en/reports.json'
import teCommon from './locales/te/common.json'
import teAuth from './locales/te/auth.json'
import teCrops from './locales/te/crops.json'
import teIncome from './locales/te/income.json'
import teExpenses from './locales/te/expenses.json'
import teFinance from './locales/te/finance.json'
import teDashboard from './locales/te/dashboard.json'
import teAnalysis from './locales/te/analysis.json'
import teReports from './locales/te/reports.json'

const SUPPORTED_LANGUAGES = ['en', 'te']

// Which language should the app start in?
function chooseStartLanguage() {
    // 1. The language the farmer chose last time (saved in the browser).
    const saved = localStorage.getItem('language')
    if (SUPPORTED_LANGUAGES.includes(saved)) {
        return saved
    }
    // 2. First visit: if the phone or browser is set to Telugu, start in Telugu.
    if (navigator.language && navigator.language.toLowerCase().startsWith('te')) {
        return 'te'
    }
    // 3. Otherwise English.
    return 'en'
}

i18n.use(initReactI18next).init({
    // All the words, grouped: language -> namespace -> keys.
    resources: {
        en: {
            common: enCommon,
            auth: enAuth,
            crops: enCrops,
            income: enIncome,
            expenses: enExpenses,
            finance: enFinance,
            dashboard: enDashboard,
            analysis: enAnalysis,
            reports: enReports,
        },
        te: {
            common: teCommon,
            auth: teAuth,
            crops: teCrops,
            income: teIncome,
            expenses: teExpenses,
            finance: teFinance,
            dashboard: teDashboard,
            analysis: teAnalysis,
            reports: teReports,
        },
    },
    lng: chooseStartLanguage(), // the language to start with
    fallbackLng: 'en', // if a Telugu word is missing, show the English one
    ns: ['common', 'auth', 'crops', 'income', 'expenses', 'finance', 'dashboard', 'analysis', 'reports'],
    defaultNS: 'common', // t('loading') means t('common:loading')
    interpolation: { escapeValue: false }, // React already protects against script injection
    initAsync: false, // our words are already in memory, so set up immediately
    react: { useSuspense: false }, // no "waiting" screen is needed
})

// Runs at start and every time the language changes:
// remember the choice, tell the browser the page language, and set the tab title.
function applyLanguage(language) {
    localStorage.setItem('language', language)
    document.documentElement.lang = language
    document.title = i18n.t('appName')
}

applyLanguage(i18n.language)
i18n.on('languageChanged', applyLanguage)

export default i18n