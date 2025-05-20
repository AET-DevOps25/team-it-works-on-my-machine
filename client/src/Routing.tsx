import LandingPage from './components/page/landing-page'
import { BrowserRouter, Routes, Route } from 'react-router'

export default function Routing() {
  return (
    <BrowserRouter>
      <Routes>
        <Route index element={<LandingPage />} />
      </Routes>
    </BrowserRouter>
  )
}
