// main.jsx is where the React app STARTS.

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
// BrowserRouter lets us show different pages for different URLs.
import { BrowserRouter } from 'react-router-dom'

// The Telugu font (regular and bold), bundled with our app.
import '@fontsource/noto-sans-telugu/400.css'
import '@fontsource/noto-sans-telugu/700.css'

// Bootstrap's styles first, then ours (so ours can override if needed).
import 'bootstrap/dist/css/bootstrap.min.css'
import './styles/main.css'

// Sets up translation. It must run before the app is drawn.
import './i18n.js'

import App from './App.jsx'
import { FarmerProvider } from './context/FarmerContext.jsx'

// Find <div id="root"> in index.html and draw our App inside it.
createRoot(document.getElementById('root')).render(
    // StrictMode adds extra checks while developing (it does nothing in production).
    <StrictMode>
        {/* BrowserRouter wraps the whole app so every component can use routing. */}
        <BrowserRouter>
            {/* FarmerProvider wraps everything so every page can ask "who is the logged-in farmer?" */}
            <FarmerProvider>
                <App />
            </FarmerProvider>
        </BrowserRouter>
    </StrictMode>,
)