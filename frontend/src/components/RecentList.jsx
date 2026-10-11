// RecentList.jsx: a short list of the latest records (used for income AND expenses).
// Props:
//   title       - heading of the card
//   records     - the list of records to show
//   getLabel    - a function that returns the main text of a record
//                 (the source for income, the TRANSLATED category for expenses)
//   linkTo      - the page that shows all records
//   amountClass - text color class for the amounts

import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { cropLabel } from '../utils/cropNames.js'
import { formatDate, formatMoney } from '../utils/format.js'

function RecentList({ title, records, getLabel, linkTo, amountClass }) {
    const { t } = useTranslation('dashboard')

    return (
        <div className="card shadow-sm h-100">
            <div className="card-body">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h2 className="h5 mb-0">{title}</h2>
                    {/* Link goes to another page without reloading. */}
                    <Link to={linkTo}>{t('viewAll')}</Link>
                </div>

                {records.length === 0 && <p className="text-muted mb-0">{t('nothingYet')}</p>}

                <ul className="list-group list-group-flush">
                    {records.map((record) => (
                        <li
                            key={record.id}
                            className="list-group-item px-0 d-flex justify-content-between align-items-center"
                        >
                            <div>
                                {/* getLabel(record) decides what to show as the main text. */}
                                <div className="fw-semibold">{getLabel(record)}</div>
                                <small className="text-muted">
                                    {formatDate(record.date)}
                                    {/* cropLabel shows a known crop in the current language. */}
                                    {record.cropName ? ` · ${cropLabel(record.cropName)}` : ''}
                                </small>
                            </div>
                            <div className={`fw-semibold text-nowrap ${amountClass}`}>{formatMoney(record.amount)}</div>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    )
}

export default RecentList