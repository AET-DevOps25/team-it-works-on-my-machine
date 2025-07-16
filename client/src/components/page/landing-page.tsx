import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { ModeToggle } from '../mode-toggle'
import { useEffect, useState } from 'react'
import Profile from '../profile/Profile'
import type {
  AnalysisContentType,
  AnalysisSingleType,
  AnalysisType,
  GitHubUserType,
  Repo,
  UserType,
  UserType2,
} from '@/lib/types'
import Cookies from 'universal-cookie'
import { toast, Toaster } from 'sonner'
import Analysis from '../profile/Analysis'

const GH_CONNECTOR_URL = import.meta.env.VITE_GH_CONNECTOR_URL
const GH_OAUTH_CLIENT_ID = import.meta.env.VITE_GH_OAUTH_CLIENT_ID

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

function AccessibleRepos({ repos }: { repos: Repo[] }) {
  function handleInstall() {
    console.log('install')
    // Open the GitHub App installation page in a new tab
    const installUrl = `https://github.com/apps/devops-workflowgenie-2025/installations/select_target`
    window.open(installUrl, '_blank', 'noopener,noreferrer')
  }

  if (repos.length === 0) {
    return (
      <div>
        <p>Workflow Genie cannot access any of you private repositories</p>
        <Button
          className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
          onClick={handleInstall}
        >
          Grant Access to Workflow Genie
        </Button>
      </div>
    )
  } else {
    return (
      <div>
        <p>
          Workflow genie currently has access to these private repositories:
        </p>
        <ul>
          {repos.map((repo) => {
            return (
              <li key={repo.name}>
                {repo.name}: {repo.html_url}
              </li>
            )
          })}
        </ul>
        <Button
          className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
          onClick={handleInstall}
        >
          Grant additional or revoke existing Access Permission
        </Button>
      </div>
    )
  }
}

function LandingPage() {
  const [repoUrl, setRepoUrl] = useState('')
  const [error, setError] = useState(false)
  const [analysis, setAnalysis] = useState<AnalysisType[]>([])

  // Extracting the 'code' parameter from the URL query string (used for authorization)
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.get('login')) {
    const cookies = new Cookies(document.cookie)
    const id = cookies.get<string | undefined>('id')
    if (id !== undefined) {
      localStorage.setItem('login', id)
    }
    // Remove "login=success" from the URL if present
    const url = new URL(window.location.href)
    if (url.searchParams.has('login')) {
      url.searchParams.delete('login')
      window.history.replaceState({}, document.title, url.toString())
    }
  }
  const [isLoggedIn, setLoggedIn] = useState(
    localStorage.getItem('login') !== null,
  )
  // State to store the retrieved user data
  const [data, setData] = useState<UserType | null>(null)

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
    const data = (await res.json()) as AnalysisSingleType
    if (res.status !== 200) {
      console.error('Failed to fetch repo data:', data)
      toast.error(data.message || 'Failed to fetch repo data')
      return
    }
    setAnalysis((oldAnalysis) => [
      ...oldAnalysis,
      {
        id: 'unknown',
        repository: repoUrl,
        content: data.results,
      },
    ])
  }

  useEffect(() => {
    const fetchData = async () => {
      if (isLoggedIn) {
        const [ghRes, userRes, reposRes] = await Promise.all([
          fetch(`${GH_CONNECTOR_URL}/user`, { credentials: 'include' }),
          fetch(
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            `${import.meta.env.VITE_USERS_URL}/users/${localStorage.getItem('login')!}`,
            { credentials: 'include' },
          ),
          fetch(`${GH_CONNECTOR_URL}/getPrivateRepos`, {
            credentials: 'include',
          }),
        ])

        const data = (await ghRes.json()) as GitHubUserType
        const userData = (await userRes.json()) as UserType2
        const repos = (await reposRes.json()) as Repo[]

        if (ghRes.status !== 200) {
          console.error('Failed to fetch user data:', data)
          return
        }
        if (userRes.status !== 200) {
          console.error('Failed to fetch user data:', userData)
          return
        }
        if (reposRes.status !== 200) {
          console.error('Failed to fetch repo data:', repos)
          return
        }

        for (const analysis of userData.analysis) {
          // Parse the content field of each analysis
          analysis.content = JSON.parse(
            analysis.content as unknown as string,
          ) as AnalysisContentType[]
        }
        setAnalysis(userData.analysis)

        setData({
          github: data,
          user: userData,
          repos: repos,
        })
      }
    }
    void fetchData()
  }, [isLoggedIn])

  return (
    <div className="container mx-auto p-6 text-center relative">
      <ModeToggle className="absolute top-4 right-4" />
      <ul>
        <li>Public Repo with simple workflows:</li>
        <li>https://github.com/AET-DevOps25/w06-template</li>
        <li>Jonas Private Repo:</li>
        <li>https://github.com/Funky-Punky/bachelor-cellbase</li>
      </ul>
      {isLoggedIn && data && <AccessibleRepos repos={data.repos} />}
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
            <Logout
              setLoggedIn={setLoggedIn}
              setData={setData}
              setAnalysis={setAnalysis}
            />
          ) : (
            <Login />
          )}
        </div>
        {data && <Profile user={data} />}
        {analysis.length > 0 && <Analysis analysis={analysis} />}
      </div>
      <Toaster />
    </div>
  )
}

export default LandingPage
