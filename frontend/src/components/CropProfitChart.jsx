// CropProfitChart.jsx: a bar chart of profit or loss for each crop.
// Props: cropWise (the list returned by the crop-wise API)
// Green bar = profit, red bar = loss.

import { useTranslation } from 'react-i18next'
import {
    Bar,
    BarChart,
    CartesianGrid,
    Cell,
    ReferenceLine,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from 'recharts'
import { cropLabel } from '../utils/cropNames.js'
import { formatCompactMoney, formatMoney } from '../utils/format.js'

function CropProfitChart({ cropWise }) {
    const { t } = useTranslation('finance')

    if (cropWise.length === 0) {
        return <p className="text-muted mb-0">{t('chart.noCrops')}</p>
    }

    // Turn each crop row into a simple object for the chart.
    // The "no crop" row (cropId is null) gets OUR translated name.
    // A real crop is shown with cropLabel (the current language for known crops).
    const data = cropWise.map((crop) => ({
        name: crop.cropId == null ? t('generalCrop') : cropLabel(crop.cropName),
        profit: Number(crop.profitLoss),
    }))

    return (
        <ResponsiveContainer width="70%" height={300}>
            <BarChart data={data}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis tickFormatter={formatCompactMoney} />
                <Tooltip formatter={(value) => formatMoney(value)} />
                {/* A line at zero, so profit (above) and loss (below) are easy to see. */}
                <ReferenceLine y={0} stroke="#000" />
                <Bar dataKey="profit" name={t('profitLoss')}>
                    {/* Green when profit is zero or more, red when it is a loss. */}
                    {data.map((item, index) => (
                        <Cell key={index} fill={item.profit >= 0 ? '#198754' : '#dc3545'} />
                    ))}
                </Bar>
            </BarChart>
        </ResponsiveContainer>
    )
}

export default CropProfitChart