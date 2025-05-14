import { ThemeProvider } from '@/components/theme-provider'
import LandingPage from './components/page/landing-page'

function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <LandingPage />
    </ThemeProvider>
  )
}

export default App
