// SelectFarmerNotice.jsx: shown by a page if, somehow, nobody is logged in.
// The ProtectedRoute normally prevents this, so it is only a safety net.

import { Link } from 'react-router-dom'
import { Trans } from 'react-i18next'

function SelectFarmerNotice() {
    return (
        <div className="alert alert-warning fs-5">
            {/* Trans lets the translated sentence contain a link.
          In the translation file: "Please <loginLink>log in</loginLink> to see your data."
          The tag <loginLink> is replaced by the <Link> below. The Telugu text can move
          the link to wherever the Telugu sentence needs it. */}
            <Trans i18nKey="common:notice.loginToSee" components={{ loginLink: <Link to="/login" /> }} />
        </div>
    )
}

export default SelectFarmerNotice