// CropFinancialTable.jsx: income, expenses and profit/loss for each crop, with a Total row.
// Prop: rows - the list returned by the crop-wise API.
// Used on BOTH the Financial Analysis page and the Reports page.

import { useTranslation } from 'react-i18next'
import StatusBadge from './StatusBadge.jsx'
import { cropLabel } from '../utils/cropNames.js'
import { formatMoney, profitClass, sumField } from '../utils/format.js'

function CropFinancialTable({ rows }) {
    // 'finance' is the default file for this component.
    const { t } = useTranslation('finance')

    if (rows.length === 0) {
        return <p className="text-muted mb-0">{t('cropTable.empty')}</p>
    }

    return (
        <div className="table-responsive">
            <table className="table table-striped align-middle mb-0">
                <thead>
                <tr>
                    <th>{t('cropTable.crop')}</th>
                    <th className="text-end">{t('income')}</th>
                    <th className="text-end">{t('expenses')}</th>
                    <th className="text-end">{t('profitLoss')}</th>
                    <th>{t('statusHeader')}</th>
                </tr>
                </thead>

                <tbody>
                {rows.map((row) => (
                    // The "General (no crop)" row has no cropId, so we use the word "general" as its key.
                    <tr key={row.cropId ?? 'general'}>
                        {/* The general row shows OUR translated name. A real crop is shown with cropLabel,
                  which uses the current language for known crops. */}
                        <td className="fw-semibold">
                            {row.cropId == null ? t('generalCrop') : cropLabel(row.cropName)}
                        </td>
                        <td className="text-end text-nowrap">{formatMoney(row.totalIncome)}</td>
                        <td className="text-end text-nowrap">{formatMoney(row.totalExpenses)}</td>
                        <td className={`text-end text-nowrap fw-semibold ${profitClass(row.profitLoss)}`}>
                            {formatMoney(row.profitLoss)}
                        </td>
                        <td>
                            <StatusBadge status={row.status} />
                        </td>
                    </tr>
                ))}
                </tbody>

                {/* The Total row adds up the rows above. */}
                <tfoot className="table-group-divider">
                <tr className="fw-bold">
                    <td>{t('cropTable.total')}</td>
                    <td className="text-end text-nowrap">{formatMoney(sumField(rows, 'totalIncome'))}</td>
                    <td className="text-end text-nowrap">{formatMoney(sumField(rows, 'totalExpenses'))}</td>
                    <td className={`text-end text-nowrap ${profitClass(sumField(rows, 'profitLoss'))}`}>
                        {formatMoney(sumField(rows, 'profitLoss'))}
                    </td>
                    <td></td>
                </tr>
                </tfoot>
            </table>
        </div>
    )
}

export default CropFinancialTable