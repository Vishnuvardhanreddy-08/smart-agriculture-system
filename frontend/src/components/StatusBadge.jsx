// StatusBadge.jsx: a small colored label: Profit (green), Loss (red) or Break-even (grey).
// Prop: status - the CODE the backend sends: "PROFIT", "LOSS" or "BREAK_EVEN".
// The code is turned into words in the chosen language (see finance.json).

import { useTranslation } from 'react-i18next'

function StatusBadge({ status }) {
    const { t } = useTranslation('finance')

    let color = 'secondary'
    if (status === 'PROFIT') {
        color = 'success'
    } else if (status === 'LOSS') {
        color = 'danger'
    }

    // t('status.PROFIT') -> "Profit" or "లాభం". defaultValue shows the code itself if we have no words for it.
    return <span className={`badge fs-6 bg-${color}`}>{t(`status.${status}`, { defaultValue: status })}</span>
}

export default StatusBadge