// Crops.jsx: add, view, edit and delete crops for the CURRENT farmer.
//
// The page is split in two components:
//   1. Crops           - checks that a farmer is chosen
//   2. CropsForFarmer  - does the real work for that ONE farmer
// All words come from the translation files (crops.json and common.json).

import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import PageHeader from '../components/PageHeader.jsx'
import SelectFarmerNotice from '../components/SelectFarmerNotice.jsx'
import { useFarmer } from '../context/FarmerContext.jsx'
import { createCrop, deleteCrop, getCrops, updateCrop } from '../services/cropService.js'
import { errorText } from '../services/api.js'
import { cropLabel, knownCropKeys } from '../utils/cropNames.js'

// The seasons the farmer can choose from. These English words are what we SAVE.
// The farmer SEES them translated (for example Kharif -> ఖరీఫ్).
const SEASONS = ['Kharif', 'Rabi', 'Zaid']

// What the form looks like when it is empty.
const EMPTY_FORM = { name: '', season: '', area: '' }

// ---------- 1. The outer component ----------
function Crops() {
    // The first name in the list ('crops') is the default file.
    // For words from the other file, write its name first, like t('common:actions.edit').
    const { t } = useTranslation(['crops', 'common'])
    const { currentFarmer } = useFarmer()

    // No farmer logged in (the login gate normally prevents this): show only the notice.
    if (!currentFarmer) {
        return (
            <>
                <PageHeader title={t('title')} subtitle={t('intro')} />
                <SelectFarmerNotice />
            </>
        )
    }

    // key={currentFarmer.id}: when the farmer changes, React builds a brand-new
    // CropsForFarmer, so the old farmer's table, form and messages are gone completely.
    return <CropsForFarmer key={currentFarmer.id} farmer={currentFarmer} />
}

// ---------- 2. The inner component (props: farmer) ----------
function CropsForFarmer({ farmer }) {
    const { t } = useTranslation(['crops', 'common'])
    const farmerId = farmer.id

    // State = the page's memory.
    const [crops, setCrops] = useState([]) // the crops shown in the table
    const [loading, setLoading] = useState(true) // true until the first load finishes
    const [error, setError] = useState('') // problems loading or deleting
    const [message, setMessage] = useState('') // success message
    const [formError, setFormError] = useState('') // problems with the form
    const [saving, setSaving] = useState(false) // true while saving (disables the button)
    const [form, setForm] = useState(EMPTY_FORM) // what is typed in the form
    const [editingId, setEditingId] = useState(null) // null = adding, a number = editing that crop

    // The text shown for a season: the translated word, or the saved word itself if we have no translation.
    function seasonLabel(season) {
        return t(`common:seasons.${season}`, { defaultValue: season })
    }

    // Loads THIS farmer's crops from the backend.
    // useCallback keeps the same function between redraws unless farmerId changes.
    const loadCrops = useCallback(async () => {
        try {
            const data = await getCrops(farmerId)
            setCrops(data)
            setError('')
        } catch (err) {
            setError(errorText(err))
        } finally {
            setLoading(false)
        }
    }, [farmerId])

    // Load the crops once when the page appears.
    useEffect(() => {
        loadCrops()
    }, [loadCrops])

    // Runs on every keystroke. event.target.name tells us WHICH field changed.
    function handleChange(event) {
        setForm({ ...form, [event.target.name]: event.target.value })
    }

    // Quick checks before sending (the backend checks again, it never trusts the browser).
    function validate() {
        if (!form.name.trim()) return t('errors.name')
        if (!form.season) return t('errors.season')
        if (!form.area || Number(form.area) <= 0) return t('errors.area')
        return ''
    }

    function resetForm() {
        setForm(EMPTY_FORM)
        setEditingId(null)
    }

    // Runs when the farmer presses "Add Crop" or "Save Changes".
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

        // The body sent to the backend. farmerId comes from the current farmer.
        // The crop name is saved exactly as typed.
        const body = {
            name: form.name.trim(),
            season: form.season,
            area: Number(form.area),
            farmerId: farmerId,
        }

        try {
            if (editingId === null) {
                await createCrop(body) // POST
                setMessage(t('added'))
            } else {
                await updateCrop(editingId, body) // PUT
                setMessage(t('updated'))
            }
            resetForm()
            await loadCrops() // reload so the table shows exactly what is in the database
        } catch (err) {
            setFormError(errorText(err))
        } finally {
            setSaving(false)
        }
    }

    // Runs when "Edit" is pressed: put the crop's values into the form.
    // (The form shows the name exactly as it was saved.)
    function handleEdit(crop) {
        setForm({ name: crop.name, season: crop.season, area: String(crop.area) })
        setEditingId(crop.id)
        setMessage('')
        setFormError('')
        window.scrollTo({ top: 0, behavior: 'smooth' }) // bring the form into view
    }

    // Runs when "Delete" is pressed.
    async function handleDelete(crop) {
        // confirm() shows a Yes/No box. {{name}} in the translation is replaced by the crop's name
        // (shown in the current language when it is a known crop).
        if (!window.confirm(t('confirmDelete', { name: cropLabel(crop.name) }))) {
            return
        }
        setMessage('')
        setError('')
        try {
            await deleteCrop(crop.id, farmerId) // DELETE
            setMessage(t('deleted'))
            if (editingId === crop.id) {
                resetForm() // we were editing this crop, so clear the form
            }
            await loadCrops()
        } catch (err) {
            // For example the friendly 409: "Cannot delete crop 'Rice' because it has records..."
            setError(errorText(err))
        }
    }

    // The seasons shown in the dropdown. If we are editing a crop whose season is NOT in our list
    // (for example one created in Postman), we add it for now, so it is shown and not blank.
    const seasonOptions =
        form.season && !SEASONS.includes(form.season) ? [...SEASONS, form.season] : SEASONS

    return (
        <>
            <PageHeader title={t('title')} subtitle={t('subtitle', { name: farmer.name })} />

            {/* ----- The add / edit form ----- */}
            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <h2 className="h5 mb-3">{editingId === null ? t('addTitle') : t('editTitle')}</h2>

                    {formError && <div className="alert alert-danger">{formError}</div>}

                    {/* onSubmit runs handleSubmit when the form is submitted (button or Enter key). */}
                    <form onSubmit={handleSubmit}>
                        <div className="row g-3">
                            <div className="col-12 col-md-4">
                                <label className="form-label fw-semibold" htmlFor="name">{t('name')}</label>
                                {/* value + onChange = a "controlled" input.
                    list="cropSuggestions" connects it to the <datalist> below, which shows
                    suggestions while the farmer types (or when the box is tapped). */}
                                <input
                                    id="name"
                                    name="name"
                                    type="text"
                                    list="cropSuggestions"
                                    autoComplete="off"
                                    className="form-control"
                                    placeholder={t('namePlaceholder')}
                                    value={form.name}
                                    onChange={handleChange}
                                />
                                {/* The suggestions: all known crops, in the CURRENT language. */}
                                <datalist id="cropSuggestions">
                                    {knownCropKeys().map((key) => (
                                        <option key={key} value={t(`names.${key}`)} />
                                    ))}
                                </datalist>
                                <div className="form-text">{t('nameHint')}</div>
                            </div>

                            <div className="col-12 col-md-4">
                                <label className="form-label fw-semibold" htmlFor="season">{t('common:fields.season')}</label>
                                <select
                                    id="season"
                                    name="season"
                                    className="form-select"
                                    value={form.season}
                                    onChange={handleChange}
                                >
                                    <option value="">{t('chooseSeason')}</option>
                                    {/* value = the English word we save, label = the translated word the farmer sees. */}
                                    {seasonOptions.map((season) => (
                                        <option key={season} value={season}>{seasonLabel(season)}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-12 col-md-4">
                                <label className="form-label fw-semibold" htmlFor="area">{t('area')}</label>
                                <input
                                    id="area"
                                    name="area"
                                    type="number"
                                    step="0.01"
                                    min="0"
                                    className="form-control"
                                    placeholder={t('areaPlaceholder')}
                                    value={form.area}
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
                            {/* Cancel is shown only while editing. */}
                            {editingId !== null && (
                                <button type="button" className="btn btn-outline-secondary" onClick={resetForm}>
                                    {t('common:actions.cancel')}
                                </button>
                            )}
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

                    {!loading && crops.length === 0 && !error && (
                        <p className="mb-0 text-muted">{t('empty')}</p>
                    )}

                    {!loading && crops.length > 0 && (
                        // table-responsive lets the table scroll sideways on a small phone screen.
                        <div className="table-responsive">
                            <table className="table table-striped align-middle mb-0">
                                <thead>
                                <tr>
                                    <th>{t('common:fields.crop')}</th>
                                    <th>{t('common:fields.season')}</th>
                                    <th>{t('area')}</th>
                                    <th className="text-end">{t('common:fields.actions')}</th>
                                </tr>
                                </thead>
                                <tbody>
                                {/* One table row for every crop. key helps React track rows. */}
                                {crops.map((crop) => (
                                    <tr key={crop.id}>
                                        {/* A known crop is shown in the current language (Rice -> వరి).
                          Any other name is shown exactly as the farmer typed it. */}
                                        <td>{cropLabel(crop.name)}</td>
                                        <td>{seasonLabel(crop.season)}</td>
                                        <td>{Number(crop.area).toFixed(2)}</td>
                                        <td className="text-end text-nowrap">
                                            <button
                                                className="btn btn-outline-primary me-2"
                                                onClick={() => handleEdit(crop)}
                                            >
                                                {t('common:actions.edit')}
                                            </button>
                                            <button
                                                className="btn btn-outline-danger"
                                                onClick={() => handleDelete(crop)}
                                            >
                                                {t('common:actions.delete')}
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </>
    )
}

export default Crops