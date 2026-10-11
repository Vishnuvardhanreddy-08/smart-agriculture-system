// FinancialAnalysis.jsx: overall income, expenses and profit/loss, plus a crop-wise table.
//
// Two parts, like the other pages:
//   1. FinancialAnalysis           - checks that a farmer is chosen
//   2. FinancialAnalysisForFarmer  - loads and shows that ONE farmer's numbers
// All words come from the translation files (analysis.json, finance.json, common.json).

import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import PageHeader from '../components/PageHeader.jsx'
import SelectFarmerNotice from '../components/SelectFarmerNotice.jsx'
import SummaryCard from '../components/SummaryCard.jsx'
import CropFinancialTable from '../components/CropFinancialTable.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'
import { getCropWise, getProfitLoss } from '../services/financialService.js'
import { errorText } from '../services/api.js'
import { profitDisplay } from '../utils/format.js'

// ---------- 1. The outer component ----------
function FinancialAnalysis() {
    const { t } = useTranslation(['analysis', 'finance', 'common'])
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
    return <FinancialAnalysisForFarmer key={currentFarmer.id} farmer={currentFarmer} />
}

// ---------- 2. The inner component ----------
function FinancialAnalysisForFarmer({ farmer }) {
    const { t } = useTranslation(['analysis', 'finance', 'common'])
    const farmerId = farmer.id

    const [summary, setSummary] = useState(null) // overall numbers
    const [cropWise, setCropWise] = useState([]) // one row per crop
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // Load both sets of numbers at the same time when the page appears.
    useEffect(() => {
        async function load() {
            try {
                const [profitLoss, cropRows] = await Promise.all([
                    getProfitLoss(farmerId),
                    getCropWise(farmerId),
                ])
                setSummary(profitLoss)
                setCropWise(cropRows)
            } catch (err) {
                setError(errorText(err))
            } finally {
                setLoading(false)
            }
        }
        load()
    }, [farmerId])

    const subtitle = t('subtitle', { name: farmer.name })

    if (loading) {
        return (
            <>
                <PageHeader title={t('title')} subtitle={subtitle} />
                <p>{t('loading')}</p>
            </>
        )
    }

    if (error) {
        return (
            <>
                <PageHeader title={t('title')} subtitle={subtitle} />
                <div className="alert alert-danger">{error}</div>
            </>
        )
    }

    // The card color comes from the backend's status code. The WORDS come from finance.json.
    const profit = profitDisplay(summary.status)

    return (
        <>
            <PageHeader title={t('title')} subtitle={subtitle} />

            {/* A simple reminder of how profit is worked out. */}
            <p className="fs-5">
                <strong>{t('formula')}</strong>
            </p>

            {/* ----- Overall: three summary cards ----- */}
            <h2 className="h4 mb-3">{t('overall')}</h2>
            <div className="row g-3 mb-4">
                <div className="col-12 col-md-4">
                    <SummaryCard title={t('finance:totalIncome')} amount={summary.totalIncome} colorClass="success" />
                </div>
                <div className="col-12 col-md-4">
                    <SummaryCard title={t('finance:totalExpenses')} amount={summary.totalExpenses} colorClass="danger" />
                </div>
                <div className="col-12 col-md-4">
                    <SummaryCard
                        title={t(`finance:card.${summary.status}.title`)}
                        amount={summary.profitLoss}
                        colorClass={profit.colorClass}
                        note={t(`finance:card.${summary.status}.note`)}
                    />
                </div>
            </div>

            {/* ----- Crop by crop ----- */}
            <h2 className="h4 mb-3">{t('cropByCrop')}</h2>
            <div className="card shadow-sm">
                <div className="card-body">
                    <CropFinancialTable rows={cropWise} />
                </div>
            </div>

            {/* {{general}} in the sentence is replaced by the translated name of the "no crop" row. */}
            <p className="text-muted mt-3">{t('generalNote', { general: t('finance:generalCrop') })}</p>
        </>
    )
}

export default FinancialAnalysis