// Income.jsx: add, view, search, edit and delete income for the CURRENT farmer.
//
// Same two-part idea as the Crops page:
//   1. Income           - checks that a farmer is chosen
//   2. IncomeForFarmer  - does the real work for that ONE farmer
// All words come from the translation files (income.json and common.json).

import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import PageHeader from '../components/PageHeader.jsx'
import SelectFarmerNotice from '../components/SelectFarmerNotice.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'
import { getCrops } from '../services/cropService.js'
import { createIncome, deleteIncome, getIncome, updateIncome } from '../services/incomeService.js'
import { errorText } from '../services/api.js'
import { cropLabel } from '../utils/cropNames.js'
import { formatDate, formatMoney, sumAmounts, todayString } from '../utils/format.js'

// What the search filters look like when empty.
const EMPTY_FILTERS = { search: '', cropId: '', from: '', to: '' }

// What the form looks like when empty. It is a function so the date is always TODAY.
function emptyForm() {
    return { source: '', amount: '', date: todayString(), cropId: '', description: '' }
}

// ---------- 1. The outer component ----------
function Income() {
    // The first name in the list ('income') is the default file.
    const { t } = useTranslation(['income', 'common'])
    const { currentFarmer } = useFarmer()

    if (!currentFarmer) {
        return (
            <>
                <PageHeader title={t('title')} subtitle={t('intro')} />
                <SelectFarmerNotice />
            </>
        )
    }

    // key={currentFarmer.id}: switching farmer builds a brand-new page, so nothing
    // from the previous farmer (records, crops, form, filters) can remain on screen.
    return <IncomeForFarmer key={currentFarmer.id} farmer={currentFarmer} />
}

// ---------- 2. The inner component ----------
function IncomeForFarmer({ farmer }) {
    const { t } = useTranslation(['income', 'common'])
    const farmerId = farmer.id

    // State = the page's memory.
    const [records, setRecords] = useState([]) // income rows shown in the table
    const [crops, setCrops] = useState([]) // THIS farmer's crops (for the dropdowns)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('') // problems loading or deleting
    const [cropsError, setCropsError] = useState('') // problem loading the crop list
    const [message, setMessage] = useState('') // success message
    const [formError, setFormError] = useState('') // problems with the form
    const [saving, setSaving] = useState(false)
    const [form, setForm] = useState(emptyForm) // what is typed in the add/edit form
    const [editingId, setEditingId] = useState(null) // null = adding, a number = editing
    const [filters, setFilters] = useState(EMPTY_FILTERS) // what is TYPED in the search bar
    const [appliedFilters, setAppliedFilters] = useState(EMPTY_FILTERS) // what was last SEARCHED

    // Loads this farmer's income, using the applied filters.
    // It depends on [farmerId, appliedFilters], so when appliedFilters changes,
    // React makes a new loadIncome and the effect below runs again automatically.
    const loadIncome = useCallback(async () => {
        try {
            const data = await getIncome(farmerId, appliedFilters)
            setRecords(data)
            setError('')
        } catch (err) {
            setRecords([])
            setError(errorText(err))
        } finally {
            setLoading(false)
        }
    }, [farmerId, appliedFilters])

    useEffect(() => {
        loadIncome()
    }, [loadIncome])

    // Load THIS farmer's crops once (for the crop dropdowns).
    // The backend only returns this farmer's crops, so other farmers' crops can never appear.
    useEffect(() => {
        getCrops(farmerId)
            .then((data) => setCrops(data))
            .catch((err) => setCropsError(errorText(err)))
    }, [farmerId])

    // ----- Form handlers -----

    // One function for all form inputs: event.target.name says which field changed.
    function handleChange(event) {
        setForm({ ...form, [event.target.name]: event.target.value })
    }

    function validate() {
        if (!form.source.trim()) return t('errors.source')
        if (!form.amount || Number(form.amount) <= 0) return t('common:form.errors.amount')
        if (!form.date) return t('common:form.errors.date')
        return ''
    }

    function resetForm() {
        setForm(emptyForm())
        setEditingId(null)
    }

    async function handleSubmit(event) {
        event.preventDefault() // stop the browser's default page reload
        setMessage('')

        const problem = validate()
        if (problem) {
            setFormError(problem)
            return
        }
        setFormError('')
        setSaving(true)

        // The body sent to the backend. farmerId always comes from the current farmer.
        // cropId is optional: an empty choice is sent as null ("no crop").
        const body = {
            source: form.source.trim(),
            amount: Number(form.amount),
            date: form.date,
            description: form.description.trim(),
            farmerId: farmerId,
            cropId: form.cropId ? Number(form.cropId) : null,
        }

        try {
            if (editingId === null) {
                await createIncome(body) // POST
                setMessage(t('added'))
            } else {
                await updateIncome(editingId, body) // PUT
                setMessage(t('updated'))
            }
            resetForm()
            await loadIncome() // reload so the table shows exactly what is in the database
        } catch (err) {
            setFormError(errorText(err))
        } finally {
            setSaving(false)
        }
    }

    // Put a record's values into the form so it can be edited.
    function handleEdit(record) {
        setForm({
            source: record.source,
            amount: String(record.amount),
            date: record.date,
            cropId: record.cropId ? String(record.cropId) : '',
            description: record.description || '',
        })
        setEditingId(record.id)
        setMessage('')
        setFormError('')
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    async function handleDelete(record) {
        // {{name}} and {{amount}} in the translation are replaced by these values.
        const question = t('confirmDelete', { name: record.source, amount: formatMoney(record.amount) })
        if (!window.confirm(question)) {
            return
        }
        setMessage('')
        setError('')
        try {
            await deleteIncome(record.id, farmerId) // DELETE
            setMessage(t('deleted'))
            if (editingId === record.id) {
                resetForm()
            }
            await loadIncome()
        } catch (err) {
            setError(errorText(err))
        }
    }

    // ----- Search / filter handlers -----

    // Typing in the search bar only changes "filters" (no server call yet).
    function handleFilterChange(event) {
        setFilters({ ...filters, [event.target.name]: event.target.value })
    }

    // Pressing Search copies the typed filters into "appliedFilters".
    // That change makes loadIncome run again with the new filters.
    function handleSearch(event) {
        event.preventDefault()
        setMessage('')
        setAppliedFilters({ ...filters })
    }

    function handleClearFilters() {
        setFilters(EMPTY_FILTERS)
        setAppliedFilters(EMPTY_FILTERS)
        setMessage('')
    }

    // true if any filter is in use (to choose the right "empty" message).
    const hasFilters = Object.values(appliedFilters).some((value) => value !== '')

    return (
        <>
            <PageHeader title={t('title')} subtitle={t('subtitle', { name: farmer.name })} />

            {/* ----- The add / edit form ----- */}
            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <h2 className="h5 mb-3">{editingId === null ? t('addTitle') : t('editTitle')}</h2>

                    {formError && <div className="alert alert-danger">{formError}</div>}

                    <form onSubmit={handleSubmit}>
                        <div className="row g-3">
                            <div className="col-12 col-md-6">
                                <label className="form-label fw-semibold" htmlFor="source">{t('source')}</label>
                                <input
                                    id="source"
                                    name="source"
                                    type="text"
                                    className="form-control"
                                    placeholder={t('sourcePlaceholder')}
                                    value={form.source}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="col-12 col-md-3">
                                <label className="form-label fw-semibold" htmlFor="amount">{t('common:fields.amountRupees')}</label>
                                <input
                                    id="amount"
                                    name="amount"
                                    type="number"
                                    step="0.01"
                                    min="0"
                                    className="form-control"
                                    placeholder={t('amountPlaceholder')}
                                    value={form.amount}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="col-12 col-md-3">
                                <label className="form-label fw-semibold" htmlFor="date">{t('common:fields.date')}</label>
                                {/* type="date" shows a calendar and gives a value like 2026-10-06 */}
                                <input
                                    id="date"
                                    name="date"
                                    type="date"
                                    className="form-control"
                                    value={form.date}
                                    onChange={handleChange}
                                />
                            </div>

                            <div className="col-12 col-md-4">
                                <label className="form-label fw-semibold" htmlFor="cropId">{t('common:form.optionalCrop')}</label>
                                <select
                                    id="cropId"
                                    name="cropId"
                                    className="form-select"
                                    value={form.cropId}
                                    onChange={handleChange}
                                >
                                    <option value="">{t('common:form.noCrop')}</option>
                                    {/* Only THIS farmer's crops are listed. A known crop is shown in the current language. */}
                                    {crops.map((crop) => (
                                        <option key={crop.id} value={crop.id}>{cropLabel(crop.name)}</option>
                                    ))}
                                </select>
                                {cropsError && <div className="form-text text-danger">{cropsError}</div>}
                            </div>

                            <div className="col-12 col-md-8">
                                <label className="form-label fw-semibold" htmlFor="description">{t('common:form.optionalDescription')}</label>
                                <input
                                    id="description"
                                    name="description"
                                    type="text"
                                    className="form-control"
                                    placeholder={t('descriptionPlaceholder')}
                                    value={form.description}
                                    onChange={handleChange}
                                />
                            </div>
                        </div>

                        <div className="mt-4">
                            <button type="submit" className="btn btn-success me-2" disabled={saving}>
                                {saving
                                    ? t('common:actions.saving')
                                    : editingId === null
                                        ? t('add')
                                        : t('common:actions.saveChanges')}
                            </button>
                            {editingId !== null && (
                                <button type="button" className="btn btn-outline-secondary" onClick={resetForm}>
                                    {t('common:actions.cancel')}
                                </button>
                            )}
                        </div>
                    </form>
                </div>
            </div>

            {/* ----- The search / filter bar ----- */}
            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <h2 className="h5 mb-3">{t('findTitle')}</h2>
                    <form onSubmit={handleSearch}>
                        <div className="row g-3">
                            <div className="col-12 col-md-6 col-lg-3">
                                <label className="form-label fw-semibold" htmlFor="search">{t('common:filters.searchText')}</label>
                                <input
                                    id="search"
                                    name="search"
                                    type="text"
                                    className="form-control"
                                    placeholder={t('searchPlaceholder')}
                                    value={filters.search}
                                    onChange={handleFilterChange}
                                />
                            </div>

                            <div className="col-12 col-md-6 col-lg-3">
                                <label className="form-label fw-semibold" htmlFor="filterCrop">{t('common:fields.crop')}</label>
                                <select
                                    id="filterCrop"
                                    name="cropId"
                                    className="form-select"
                                    value={filters.cropId}
                                    onChange={handleFilterChange}
                                >
                                    <option value="">{t('common:filters.allCrops')}</option>
                                    {crops.map((crop) => (
                                        <option key={crop.id} value={crop.id}>{cropLabel(crop.name)}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-6 col-lg-3">
                                <label className="form-label fw-semibold" htmlFor="from">{t('common:filters.fromDate')}</label>
                                <input
                                    id="from"
                                    name="from"
                                    type="date"
                                    className="form-control"
                                    value={filters.from}
                                    onChange={handleFilterChange}
                                />
                            </div>

                            <div className="col-6 col-lg-3">
                                <label className="form-label fw-semibold" htmlFor="to">{t('common:filters.toDate')}</label>
                                <input
                                    id="to"
                                    name="to"
                                    type="date"
                                    className="form-control"
                                    value={filters.to}
                                    onChange={handleFilterChange}
                                />
                            </div>
                        </div>

                        <div className="mt-3">
                            <button type="submit" className="btn btn-primary me-2">{t('common:actions.search')}</button>
                            <button type="button" className="btn btn-outline-secondary" onClick={handleClearFilters}>
                                {t('common:actions.clear')}
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            {/* ----- Messages ----- */}
            {message && <div className="alert alert-success">{message}</div>}
            {error && <div className="alert alert-danger">{error}</div>}

            {/* ----- The table ----- */}
            <div className="card shadow-sm">
                <div className="card-body">
                    <h2 className="h5 mb-3">{t('listTitle')}</h2>

                    {loading && <p className="mb-0">{t('loading')}</p>}

                    {!loading && records.length === 0 && !error && (
                        <p className="mb-0 text-muted">
                            {hasFilters ? t('common:records.noMatch') : t('empty')}
                        </p>
                    )}

                    {!loading && records.length > 0 && (
                        <>
                            {/* Summary of the rows currently shown.
                  t('common:records.showing', { count }) picks the singular or plural text. */}
                            <p className="text-muted">
                                {t('common:records.showing', { count: records.length })} · {t('common:fields.total')}{' '}
                                <strong className="text-success">{formatMoney(sumAmounts(records))}</strong>
                            </p>

                            <div className="table-responsive">
                                <table className="table table-striped align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th>{t('common:fields.date')}</th>
                                        <th>{t('source')}</th>
                                        <th>{t('common:fields.crop')}</th>
                                        <th className="text-end">{t('common:fields.amount')}</th>
                                        <th>{t('common:fields.description')}</th>
                                        <th className="text-end">{t('common:fields.actions')}</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {records.map((record) => (
                                        <tr key={record.id}>
                                            <td className="text-nowrap">{formatDate(record.date)}</td>
                                            {/* The source is shown exactly as the farmer typed it (any language). */}
                                            <td>{record.source}</td>
                                            {/* A known crop is shown in the current language. "—" means no crop. */}
                                            <td>{record.cropName ? cropLabel(record.cropName) : '—'}</td>
                                            <td className="text-end fw-semibold text-nowrap">{formatMoney(record.amount)}</td>
                                            <td>{record.description}</td>
                                            <td className="text-end text-nowrap">
                                                <button
                                                    className="btn btn-outline-primary me-2"
                                                    onClick={() => handleEdit(record)}
                                                >
                                                    {t('common:actions.edit')}
                                                </button>
                                                <button
                                                    className="btn btn-outline-danger"
                                                    onClick={() => handleDelete(record)}
                                                >
                                                    {t('common:actions.delete')}
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </>
    )
}

export default Income