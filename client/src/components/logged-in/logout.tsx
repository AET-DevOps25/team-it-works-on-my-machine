import Cookies from 'universal-cookie'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { useGlobalState } from '@/hooks/use-global-state'

function removeCookie(cookies: Cookies, name: string) {
  const domain = window.location.hostname
  const domainParts = domain.split('.')

  for (let i = 0; i < domainParts.length; i++) {
    const currentDomain = domainParts.slice(i).join('.')
    cookies.remove(name, {
      domain: currentDomain,
    })
  }
}

function Logout(p: { className?: string }) {
  const setLogin = useGlobalState((state) => state.setLogin)
  const setData = useGlobalState((state) => state.setData)
  const setAnalyses = useGlobalState((state) => state.setAnalyses)
  const setLoading = useGlobalState((state) => state.setLoading)

  function handleLogout() {
    localStorage.removeItem('login')
    setLogin(null)
    setData(null)
    setAnalyses([])
    setLoading(false)
    // Remove "login=success" from the URL if present
    const url = new URL(window.location.href)
    if (url.searchParams.has('login')) {
      url.searchParams.delete('login')
      window.history.replaceState({}, document.title, url.toString())
    }
    const cookies = new Cookies(document.cookie)
    removeCookie(cookies, 'id')
    cookies.update()
  }

  return (
    <Button
      className={cn(
        'absolute px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80',
        p.className,
      )}
      onClick={handleLogout}
    >
      Logout
    </Button>
  )
}

export default Logout
