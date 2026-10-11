// PageHeader.jsx: the big title at the top of every page.
// It receives "props" (inputs): title and subtitle.

// { title, subtitle } picks the two props out of the props object.
function PageHeader({ title, subtitle }) {
    return (
        <div className="mb-4">
            <h1 className="h2 text-success fw-bold">{title}</h1>
            {/* The subtitle is shown only if it was given ("&&" means "and then show"). */}
            {subtitle && <p className="text-muted fs-5 mb-0">{subtitle}</p>}
        </div>
    )
}

export default PageHeader