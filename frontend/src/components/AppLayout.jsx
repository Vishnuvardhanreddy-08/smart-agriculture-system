// AppLayout.jsx: the frame around every private page: the navbar on top, the page below it.

import { Outlet } from 'react-router-dom'
import Navbar from './Navbar.jsx'

function AppLayout() {
    return (
        <>
            <Navbar />
            {/* "container" centres the content and gives it side margins. */}
            <main className="container py-4">
                {/* Outlet = the current page (Dashboard, Crops, Income...) is drawn here. */}
                <Outlet />
            </main>
        </>
    )
}

export default AppLayout