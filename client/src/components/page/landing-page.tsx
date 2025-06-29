import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { ModeToggle } from '../mode-toggle'
import { useEffect, useState } from 'react'
import Profile from '../profile/Profile'
import type { OauthResponse, UserType } from '@/lib/types'

const GH_CONNECTOR_URL = import.meta.env.VITE_GH_CONNECTOR_URL
const GH_OAUTH_CLIENT_ID = import.meta.env.VITE_GH_OAUTH_CLIENT_ID

function Logout(p: {
  setLoggedIn: React.Dispatch<React.SetStateAction<boolean>>
  setData: React.Dispatch<React.SetStateAction<UserType | OauthResponse | null>>
}) {
  function handleLogout() {
    console.log('logout')
    localStorage.removeItem('login')
    p.setLoggedIn(false)
    p.setData(null)
    // Remove "login=success" from the URL if present
    const url = new URL(window.location.href)
    if (url.searchParams.has('login')) {
      url.searchParams.delete('login')
      window.history.replaceState({}, document.title, url.toString())
    }
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

function Login() {
  function handleLogin() {
    console.log('login')
    // Function to redirect the user to the GitHub OAuth authorization page
    const redirect_uri = GH_CONNECTOR_URL + '/oauth/redirect'
    // const scope = 'read:user,repo'
    const authUrl = `https://github.com/login/oauth/authorize?client_id=${GH_OAUTH_CLIENT_ID}&redirect_uri=${redirect_uri}`

    window.location.href = authUrl
  }

  return (
    <Button
      className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
      onClick={handleLogin}
    >
      Login
    </Button>
  )
}

function InstallApp() {
  function handleInstall() {
    console.log('install')
    // Open the GitHub App installation page in a new tab
    const installUrl = `https://github.com/apps/devops-workflowgenie-2025/installations/select_target`
    window.open(installUrl, '_blank', 'noopener,noreferrer')
  }
  return (
    <Button
      className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
      onClick={handleInstall}
    >
      Install App on GitHub
    </Button>
  )
}

function LandingPage() {
  const [repoUrl, setRepoUrl] = useState('')
  const [error, setError] = useState(false)

  // Extracting the 'code' parameter from the URL query string (used for authorization)
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.get('login')) {
    localStorage.setItem('login', 'true')
  }
  const [isLoggedIn, setLoggedIn] = useState(
    localStorage.getItem('login') === 'true',
  )
  // State to store the retrieved user data
  const [data, setData] = useState<UserType | OauthResponse | null>(null)

  function handleInputChange(e: React.ChangeEvent<HTMLInputElement>) {
    setRepoUrl(e.target.value)
    if (error) setError(false) // Clear error when user starts typing
  }

  async function handleSearch() {
    if (!repoUrl) {
      setError(true)
      return
    }
    const res = await fetch(`${GH_CONNECTOR_URL}/getInfo?repoUrl=${repoUrl}`, {
      credentials: 'include',
    })
    const data = (await res.json()) as object
    console.log(data)
    alert(JSON.stringify(data))
  }

  useEffect(() => {
    const fetchData = async () => {
      if (isLoggedIn) {
        const res = await fetch(`${GH_CONNECTOR_URL}/user`, {
          credentials: 'include',
        })
        const data = (await res.json()) as UserType
        setData(data)
      }
    }
    void fetchData()
  }, [isLoggedIn])

  return (
    <div className="container mx-auto p-6 text-center relative">
      <ModeToggle className="absolute top-4 right-4" />
      <ul>
        <li>Public Repo with simple workflows: </li>
        <li>https://github.com/AET-DevOps25/w06-template</li>
        <li>Jonas Private Repo:</li>
        <li>https://github.com/Funky-Punky/bachelor-cellbase</li>
      </ul>
      <h1 className="text-4xl font-extrabold text-primary mb-6 mt-16">
        Workflow Genie
      </h1>
      <div className="flex flex-col items-center gap-4">
        <Input
          type="search"
          placeholder="Insert GitHub Repo URL"
          className={`w-full max-w-md p-3 rounded-lg ${
            error
              ? 'border-red-500 focus-visible:ring-red-300 hover:border-red-500'
              : 'border-border hover:border-border'
          }`}
          onKeyUp={(e) => {
            if (e.key === 'Enter') {
              void handleSearch()
            }
          }}
          onChange={handleInputChange}
        />
        {error && (
          <p className="text-red-500 text-sm">
            Please enter a GitHub repository URL
          </p>
        )}
        <div className="flex gap-4">
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
            onClick={() => void handleSearch()}
          >
            Analyze
          </Button>
          {isLoggedIn ? (
            <Logout setLoggedIn={setLoggedIn} setData={setData} />
          ) : (
            <Login />
          )}
          {isLoggedIn && <InstallApp />}
        </div>
        {data && <Profile user={data as UserType} />}
      </div>
    </div>
  )
}

export default LandingPage
