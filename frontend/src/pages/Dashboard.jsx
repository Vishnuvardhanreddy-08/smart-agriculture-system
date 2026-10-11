// Dashboard.jsx: the home page. Shows the CURRENT farmer's money at a glance.
//
// Two parts, like the other pages:
//   1. Dashboard           - checks that a farmer is chosen
//   2. DashboardForFarmer  - loads and shows that ONE farmer's numbers
// All words come from the translation files (dashboard.json, finance.json, common.json).

import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import PageHeader from '../components/PageHeader.jsx'
import SelectFarmerNotice from '../components/SelectFarmerNotice.jsx'
import SummaryCard from '../components/SummaryCard.jsx'
import RecentList from '../components/RecentList.jsx'
import IncomeExpenseChart from '../components/IncomeExpenseChart.jsx'
import CropProfitChart from '../components/CropProfitChart.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'
import { getCropWise, getProfitLoss } from '../services/financialService.js'
import { getIncome } from '../services/incomeService.js'
import { getExpenses } from '../services/expenseService.js'
import { errorText } from '../services/api.js'
import { profitDisplay } from '../utils/format.js'

// How many recent records to show in each list.
const RECENT_COUNT = 5

// ---------- 1. The outer component ----------
function Dashboard() {
    const { t } = useTranslation(['dashboard', 'finance', 'common'])
    const { currentFarmer } = useFarmer()

    if (!currentFarmer) {
        return (
            <>
                <PageHeader title={t('title')} subtitle={t('intro')} />
                <SelectFarmerNotice />
            </>
        )
    }

    // key={currentFarmer.id}: switching farmer builds a brand-new dashboard,
    // so the previous farmer's numbers can never stay on screen.
    return <DashboardForFarmer key={currentFarmer.id} farmer={currentFarmer} />
}

// ---------- 2. The inner component ----------
function DashboardForFarmer({ farmer }) {
    const { t } = useTranslation(['dashboard', 'finance', 'common'])
    const farmerId = farmer.id

    // State = the page's memory.
    const [summary, setSummary] = useState(null) // { totalIncome, totalExpenses, profitLoss, status }
    const [cropWise, setCropWise] = useState([]) // crop-wise profit rows
    const [recentIncome, setRecentIncome] = useState([]) // latest income records
    const [recentExpenses, setRecentExpenses] = useState([]) // latest expense records
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // Runs once when the dashboard appears (and again if the farmer id changes).
    useEffect(() => {
        async function loadDashboard() {
            try {
                // Promise.all starts all 4 requests at the same time and waits for ALL of them.
                const [profitLoss, cropRows, incomeList, expenseList] = await Promise.all([
                    getProfitLoss(farmerId),
                    getCropWise(farmerId),
                    getIncome(farmerId),
                    getExpenses(farmerId),
                ])

                setSummary(profitLoss)
                setCropWise(cropRows)
                // The backend sends newest first, so the first 5 are the latest 5.
                setRecentIncome(incomeList.slice(0, RECENT_COUNT))
                setRecentExpenses(expenseList.slice(0, RECENT_COUNT))
            } catch (err) {
                setError(errorText(err))
            } finally {
                setLoading(false)
            }
        }

        loadDashboard()
    }, [farmerId])

    const subtitle = t('welcome', { name: farmer.name })

    // ----- What to show while loading, or if something failed -----
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

            {/* ----- Row 1: three summary cards ----- */}
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

            {/* ----- Row 2: two charts ----- */}
            <div className="row g-3 mb-4">
                <div className="col-12 col-lg-6">
                    <div className="card shadow-sm h-100">
                        <div className="card-body">
                            <h2 className="h5 mb-3">{t('incomeVsExpenses')}</h2>
                            <IncomeExpenseChart
                                totalIncome={summary.totalIncome}
                                totalExpenses={summary.totalExpenses}
                            />
                        </div>
                    </div>
                </div>
                <div className="col-12 col-lg-6">
                    <div className="card shadow-sm h-100">
                        <div className="card-body">
                            <h2 className="h5 mb-3">{t('cropProfit')}</h2>
                            <CropProfitChart cropWise={cropWise} />
                        </div>
                    </div>
                </div>
            </div>

            {/* ----- Row 3: recent income and recent expenses ----- */}
            <div className="row g-3">
                <div className="col-12 col-lg-6">
                    <RecentList
                        title={t('recentIncome')}
                        records={recentIncome}
                        getLabel={(record) => record.source}
                        linkTo="/income"
                        amountClass="text-success"
                    />
                </div>
                <div className="col-12 col-lg-6">
                    <RecentList
                        title={t('recentExpenses')}
                        records={recentExpenses}
                        // The expense category is translated for display. The saved value stays English.
                        getLabel={(record) => t(`common:categories.${record.category}`, { defaultValue: record.category })}
                        linkTo="/expenses"
                        amountClass="text-danger"
                    />
                </div>
            </div>
        </>
    )
}

export default Dashboard