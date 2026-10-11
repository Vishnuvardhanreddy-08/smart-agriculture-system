// SummaryCard.jsx: one big number card (for example "Total Income").
// Props: title, amount, colorClass ("success", "danger" or "secondary"), note (optional text)

import { formatMoney } from '../utils/format.js'

function SummaryCard({ title, amount, colorClass, note }) {
    return (
        // h-100 makes all three cards the same height.
        <div className={`card shadow-sm h-100 border border-2 border-${colorClass}`}>
            <div className="card-body">
                <div className="text-muted">{title}</div>
                <div className={`fs-2 fw-bold text-${colorClass}`}>{formatMoney(amount)}</div>
                {/* The note is shown only if one was given. */}
                {note && <div className="text-muted">{note}</div>}
            </div>
        </div>
    )
}

export default SummaryCard