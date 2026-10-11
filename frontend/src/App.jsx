// App.jsx: the map of the whole app: which URL shows which page.
//
//   /login, /register   -> open to everyone
//   everything else     -> only for a logged-in farmer (ProtectedRoute),
//                          shown inside the navbar frame (AppLayout)

import { Routes, Route } from 'react-router-dom'

import ProtectedRoute from './components/ProtectedRoute.jsx'
import AppLayout from './components/AppLayout.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Income from './pages/Income.jsx'
import Expenses from './pages/Expenses.jsx'
import Crops from './pages/Crops.jsx'
import FinancialAnalysis from './pages/FinancialAnalysis.jsx'
import Reports from './pages/Reports.jsx'

// A component is a function that returns JSX (HTML-like code).
function App() {
    return (
        // Routes picks ONE page depending on the URL in the browser.
        <Routes>
            {/* Open pages */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Private pages: ProtectedRoute is the gate, AppLayout is the frame (navbar + container). */}
            <Route element={<ProtectedRoute />}>
                <Route element={<AppLayout />}>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/income" element={<Income />} />
                    <Route path="/expenses" element={<Expenses />} />
                    <Route path="/crops" element={<Crops />} />
                    <Route path="/financial-analysis" element={<FinancialAnalysis />} />
                    <Route path="/reports" element={<Reports />} />
                    {/* "*" matches any other URL. A logged-out visitor is sent to /login first. */}
                    <Route path="*" element={<p className="fs-4">Page not found.</p>} />
                </Route>
            </Route>
        </Routes>
    )
}

// "export default" lets other files import this component.
export default App