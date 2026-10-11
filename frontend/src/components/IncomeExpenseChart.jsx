// IncomeExpenseChart.jsx: a bar chart comparing total income and total expenses.
// Props: totalIncome, totalExpenses

import { useTranslation } from 'react-i18next'
import { Bar, BarChart, CartesianGrid, Cell, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { formatCompactMoney, formatMoney } from '../utils/format.js'

function IncomeExpenseChart({ totalIncome, totalExpenses }) {
    const { t } = useTranslation('finance')

    // Nothing to draw if both are zero.
    if (Number(totalIncome) === 0 && Number(totalExpenses) === 0) {
        return <p className="text-muted mb-0">{t('chart.noData')}</p>
    }

    // Recharts needs an array of objects. Each object becomes one bar.
    // The names are translated, so the bottom labels change with the language.
    const data = [
        { name: t('income'), amount: Number(totalIncome), color: '#198754' }, // green
        { name: t('expenses'), amount: Number(totalExpenses), color: '#dc3545' }, // red
    ]

    return (
        // ResponsiveContainer makes the chart fit its parent's width (good for phones).
        <ResponsiveContainer width="80%" height={300}>
            <BarChart data={data}>
                <CartesianGrid strokeDasharray="3 3" />
                {/* XAxis shows the "name" of each bar. */}
                <XAxis dataKey="name" />
                {/* YAxis shows short money labels like ₹51K. */}
                <YAxis tickFormatter={formatCompactMoney} />
                {/* Tooltip is the small box that appears when you hover or tap a bar. */}
                <Tooltip formatter={(value) => formatMoney(value)} />
                {/* Bar draws the bars using the "amount" of each object. */}
                <Bar dataKey="amount" name={t('chart.amount')}>
                    {/* Cell colors each bar separately. */}
                    {data.map((item) => (
                        <Cell key={item.color} fill={item.color} />
                    ))}
                </Bar>
            </BarChart>
        </ResponsiveContainer>
    )
}

export default IncomeExpenseChart