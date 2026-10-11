// MonthlyChart.jsx: grouped bar chart. For each month: a green bar (income) and a red bar (expenses).
// Prop: rows - the 12 monthly rows returned by the monthly API.

import { useTranslation } from 'react-i18next'
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { formatCompactMoney, formatMoney, formatMonthName } from '../utils/format.js'

function MonthlyChart({ rows }) {
    const { t } = useTranslation('finance')

    // Turn each monthly row into a simple object for the chart.
    // formatMonthName gives "Jan" or the Telugu short month name, depending on the language.
    const data = rows.map((row) => ({
        name: formatMonthName(row.month, 'short'),
        income: Number(row.totalIncome),
        expenses: Number(row.totalExpenses),
    }))

    return (
        <ResponsiveContainer width="100%" height={300}>
            <BarChart data={data}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis tickFormatter={formatCompactMoney} />
                <Tooltip formatter={(value) => formatMoney(value)} />
                {/* Legend shows which color means what, using the "name" of each Bar. */}
                <Legend />
                {/* Two Bar elements side by side = a grouped chart. */}
                <Bar dataKey="income" name={t('income')} fill="#198754" />
                <Bar dataKey="expenses" name={t('expenses')} fill="#dc3545" />
            </BarChart>
        </ResponsiveContainer>
    )
}

export default MonthlyChart