import type { AnalysisType, UserType } from "@/lib/types"
import Cookies from "universal-cookie"
import { Button } from "@/components/ui/button"

function Logout(p: {
  setLoggedIn: React.Dispatch<React.SetStateAction<boolean>>
  setData: React.Dispatch<React.SetStateAction<UserType | null>>
  setAnalysis: React.Dispatch<React.SetStateAction<AnalysisType[]>>
}) {
  function handleLogout() {
    console.log('logout')
    localStorage.removeItem('login')
    p.setLoggedIn(false)
    p.setData(null)
    p.setAnalysis([])
    // Remove "login=success" from the URL if present
    const url = new URL(window.location.href)
    if (url.searchParams.has('login')) {
      url.searchParams.delete('login')
      window.history.replaceState({}, document.title, url.toString())
    }
    const cookies = new Cookies(document.cookie)
    cookies.remove('id')
    cookies.update()
  }

  return (
    <Button
      className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
      onClick={handleLogout}
    >
      Logout
    </Button>
  )
}

export default Logout