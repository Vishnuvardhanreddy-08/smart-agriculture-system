// Reports.jsx: a monthly report (for a chosen year) and a crop-wise report, for the CURRENT farmer.
//
// Two parts, like the other pages:
//   1. Reports           - checks that a farmer is chosen
//   2. ReportsForFarmer  - loads and shows that ONE farmer's reports
// All words come from the translation files (reports.json, finance.json, common.json).

import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import PageHeader from '../components/PageHeader.jsx'
import SelectFarmerNotice from '../components/SelectFarmerNotice.jsx'
import MonthlyChart from '../components/MonthlyChart.jsx'
import CropFinancialTable from '../components/CropFinancialTable.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'
import { getCropWise, getMonthly } from '../services/financialService.js'
import { errorText } from '../services/api.js'
import { formatMoney, formatMonthName, profitClass, sumField } from '../utils/format.js'

// The years the farmer can choose from: this year and the 5 years before it.
const CURRENT_YEAR = new Date().getFullYear()
const YEARS = Array.from({ length: 6 }, (_, index) => CURRENT_YEAR - index)

// ---------- 1. The outer component ----------
function Reports() {
    const { t } = useTranslation(['reports', 'finance', 'common'])
    const { currentFarmer } = useFarmer()

    if (!currentFarmer) {
        return (
            <>
                <PageHeader title={t('title')} subtitle={t('intro')} />
                <SelectFarmerNotice />
            </>
        )
    }

    // key={currentFarmer.id}: switching farmer builds a brand-new page.
    return <ReportsForFarmer key={currentFarmer.id} farmer={currentFarmer} />
}

// ---------- 2. The inner component ----------
function ReportsForFarmer({ farmer }) {
    const { t } = useTranslation(['reports', 'finance', 'common'])
    const farmerId = farmer.id

    // The year being shown.
    const [year, setYear] = useState(CURRENT_YEAR)

    // Monthly report data (its own loading and error, because it reloads when the year changes).
    const [monthly, setMonthly] = useState([])
    const [monthlyLoading, setMonthlyLoading] = useState(true)
    const [monthlyError, setMonthlyError] = useState('')

    // Crop-wise report data.
    const [cropWise, setCropWise] = useState([])
    const [cropLoading, setCropLoading] = useState(true)
    const [cropError, setCropError] = useState('')

    // Load the monthly report. Runs again whenever the year changes.
    useEffect(() => {
        // "ignore" protects against a race: if the farmer changes the year quickly,
        // an OLD answer arriving late must not replace the newer one.
        let ignore = false

        getMonthly(farmerId, year)
            .then((data) => {
                if (!ignore) setMonthly(data)
            })
            .catch((err) => {
                if (!ignore) {
                    setMonthly([])
                    setMonthlyError(errorText(err))
                }
            })
            .finally(() => {
                if (!ignore) setMonthlyLoading(false)
            })

        // Cleanup: React runs this before the effect runs again (or when the page closes).
        return () => {
            ignore = true
        }
    }, [farmerId, year])

    // Load the crop-wise report once.
    useEffect(() => {
        getCropWise(farmerId)
            .then((data) => setCropWise(data))
            .catch((err) => setCropError(errorText(err)))
            .finally(() => setCropLoading(false))
    }, [farmerId])

    // Runs when the farmer picks another year in the dropdown.
    function handleYearChange(event) {
        setMonthlyLoading(true)
        setMonthlyError('')
        setYear(Number(event.target.value))
    }

    // true if at least one month has some income or expenses.
    const hasData = monthly.some(
        (row) => Number(row.totalIncome) !== 0 || Number(row.totalExpenses) !== 0,
    )

    return (
        <>
            <PageHeader title={t('title')} subtitle={t('subtitle', { name: farmer.name })} />

            {/* ----- Toolbar: year and print. "no-print" hides it on paper. ----- */}
            <div className="row g-3 align-items-end mb-4 no-print">
                <div className="col-12 col-sm-6 col-md-4">
                    <label className="form-label fw-semibold" htmlFor="year">{t('year')}</label>
                    <select id="year" className="form-select" value={year} onChange={handleYearChange}>
                        {YEARS.map((y) => (
                            <option key={y} value={y}>{y}</option>
                        ))}
                    </select>
                </div>
                <div className="col-12 col-sm-6 col-md-4">
                    {/* window.print() opens the browser's print window. */}
                    <button className="btn btn-outline-secondary" onClick={() => window.print()}>
                        {t('print')}
                    </button>
                </div>
            </div>

            {/* ----- Monthly report ----- */}
            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <h2 className="h4 mb-3">{t('monthlyTitle', { year })}</h2>

                    {monthlyError && <div className="alert alert-danger">{monthlyError}</div>}
                    {monthlyLoading && <p className="mb-0">{t('loadingMonthly')}</p>}

                    {!monthlyLoading && !monthlyError && (
                        <>
                            {/* The chart is drawn only when there is something to show. */}
                            {hasData ? (
                                <div className="mb-4">
                                    <MonthlyChart rows={monthly} />
                                </div>
                            ) : (
                                <p className="text-muted">{t('noData', { year })}</p>
                            )}

                            <div className="table-responsive">
                                <table className="table table-striped align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th>{t('month')}</th>
                                        <th className="text-end">{t('finance:income')}</th>
                                        <th className="text-end">{t('finance:expenses')}</th>
                                        <th className="text-end">{t('finance:profitLoss')}</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {monthly.map((row) => {
                                        // Months with nothing recorded are shown in grey.
                                        const empty = Number(row.totalIncome) === 0 && Number(row.totalExpenses) === 0
                                        return (
                                            <tr key={row.month} className={empty ? 'text-muted' : ''}>
                                                {/* The month name comes from the browser, in the chosen language
                              (the English name sent by the server is not used). */}
                                                <td>{formatMonthName(row.month)}</td>
                                                <td className="text-end text-nowrap">{formatMoney(row.totalIncome)}</td>
                                                <td className="text-end text-nowrap">{formatMoney(row.totalExpenses)}</td>
                                                <td className={`text-end text-nowrap fw-semibold ${empty ? '' : profitClass(row.profitLoss)}`}>
                                                    {formatMoney(row.profitLoss)}
                                                </td>
                                            </tr>
                                        )
                                    })}
                                    </tbody>

                                    {/* Year totals: the 12 months added up. */}
                                    <tfoot className="table-group-divider">
                                    <tr className="fw-bold">
                                        <td>{t('totalYear', { year })}</td>
                                        <td className="text-end text-nowrap">{formatMoney(sumField(monthly, 'totalIncome'))}</td>
                                        <td className="text-end text-nowrap">{formatMoney(sumField(monthly, 'totalExpenses'))}</td>
                                        <td className={`text-end text-nowrap ${profitClass(sumField(monthly, 'profitLoss'))}`}>
                                            {formatMoney(sumField(monthly, 'profitLoss'))}
                                        </td>
                                    </tr>
                                    </tfoot>
                                </table>
                            </div>
                        </>
                    )}
                </div>
            </div>

            {/* ----- Crop-wise report ----- */}
            <div className="card shadow-sm">
                <div className="card-body">
                    <h2 className="h4 mb-1">{t('cropTitle')}</h2>
                    <p className="text-muted">{t('allTime')}</p>

                    {cropError && <div className="alert alert-danger">{cropError}</div>}
                    {cropLoading && <p className="mb-0">{t('loadingCrop')}</p>}
                    {!cropLoading && !cropError && <CropFinancialTable rows={cropWise} />}
                </div>
            </div>
        </>
    )
}

export default Reports