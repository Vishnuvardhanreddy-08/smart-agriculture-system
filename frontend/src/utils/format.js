// format.js: small helper functions for showing money and dates nicely.
// They only change how things LOOK. The real values stay untouched.

import i18n from '../i18n.js'

// The locale used for DATES: Telugu month names when the language is Telugu.
// (Money always uses the Indian format, which uses the same digits in both languages.)
function dateLocale() {
    return i18n.language === 'te' ? 'te-IN' : 'en-IN'
}

// Today's date as "2026-10-06" using the farmer's own time zone.
// (toISOString alone uses UTC, which can show yesterday's date in India early in the morning.)
export function todayString() {
    const now = new Date()
    const localTime = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
    return localTime.toISOString().slice(0, 10)
}

// 36000 -> "₹36,000.00" (Indian number grouping)
export function formatMoney(amount) {
    return Number(amount).toLocaleString('en-IN', { style: 'currency', currency: 'INR' })
}

// 51000 -> "₹51K", 150000 -> "₹1.5L" (short form, used on chart axes)
export function formatCompactMoney(amount) {
    return Number(amount).toLocaleString('en-IN', {
        style: 'currency',
        currency: 'INR',
        notation: 'compact',
        maximumFractionDigits: 1,
    })
}

// "2026-10-06" -> "06 Oct 2026" in English, or the same date with Telugu month names in Telugu.
export function formatDate(dateString) {
    if (!dateString) return ''
    // Adding "T00:00:00" makes JavaScript read it as a LOCAL date (no time zone shifting).
    return new Date(dateString + 'T00:00:00').toLocaleDateString(dateLocale(), {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
    })
}

// Month number (1 to 12) -> month name in the current language.
// style is "long" (October) or "short" (Oct). Used by the Reports page in Step 23.
export function formatMonthName(month, style = 'long') {
    return new Date(2000, month - 1, 1).toLocaleDateString(dateLocale(), { month: style })
}

// Adds up one field (for example "totalIncome") of a list of rows.
// We add in paise (whole numbers) first, so 0.1 + 0.2 style errors cannot happen.
export function sumField(rows, field) {
    let paise = 0
    for (const row of rows) {
        paise += Math.round(Number(row[field]) * 100)
    }
    return paise / 100
}

// Adds up the "amount" of a list of records (used by the Income and Expenses pages).
export function sumAmounts(records) {
    return sumField(records, 'amount')
}

// Text color for a profit/loss number: green if positive, red if negative, grey if zero.
export function profitClass(amount) {
    const value = Number(amount)
    if (value > 0) return 'text-success'
    if (value < 0) return 'text-danger'
    return 'text-muted'
}

// Turns the backend's status word into a color, a title and a short note for a summary card.
// (The words are still English here. Step 23 translates them.)
export function profitDisplay(status) {
    if (status === 'PROFIT') {
        return { colorClass: 'success', title: 'Profit', note: 'You are in profit' }
    }
    if (status === 'LOSS') {
        return { colorClass: 'danger', title: 'Loss', note: 'You are in loss' }
    }
    return { colorClass: 'secondary', title: 'Profit / Loss', note: 'Break-even: income equals expenses' }
}