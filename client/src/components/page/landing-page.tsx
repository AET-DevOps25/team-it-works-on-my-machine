import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { ModeToggle } from '../mode-toggle'
import { useEffect, useState } from 'react'
import Profile from '../profile/Profile'
import type {
  AnalysisContentType,
  GHConnectorResponse,
  AnalysisType,
  UserType,
} from '@/lib/types'
import Cookies from 'universal-cookie'
import { toast } from 'sonner'
import { Toaster } from '../ui/sonner'
import Analysis from '../Analysis'
import AccessibleRepos from '../AccessibleRepos'
import Logout from '../Logout'
import Login from '../Login'

const GH_CONNECTOR_URL = import.meta.env.VITE_GH_CONNECTOR_URL

function LandingPage() {
  const [repoUrl, setRepoUrl] = useState('')
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
  }

  function handleSearch() {
    if (!repoUrl) {
      toast.error('Please enter a GitHub repository URL')
      return
    }
    const repoUrlTmp = repoUrl
    setRepoUrl('')

    async function handleSearchInner() {
      const res = await fetch(
        `${GH_CONNECTOR_URL}/getInfo?repoUrl=${repoUrlTmp}`,
        {
          credentials: 'include',
        },
      )

      const data = (await res.json()) as GHConnectorResponse
      if (res.status !== 200) {
        console.error('Failed to fetch repo data:', data)
        toast.error(data.error_message || 'Failed to fetch repo data')
        return
      }
      setAnalysis((oldAnalysis) => [
        {
          id: 'unknown',
          repository: repoUrlTmp,
          created_at: new Date(Date.now()),
          content: data.results,
        },
        ...oldAnalysis,
      ])
    }

    toast.promise(handleSearchInner, {
      loading:
        'Your request is being processed - This may take up to a minute. Please be patient!',
      success: () => {
        return 'Your Analysis is ready now'
      },
      error: 'Failed to fetch repo data',
    })
  }

  useEffect(() => {
    const fetchData = async () => {
      if (isLoggedIn) {
        const [ghUserRes, analysesRes, reposRes] = await Promise.all([
          fetch(`${GH_CONNECTOR_URL}/user`, { credentials: 'include' }),
          fetch(
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            `${import.meta.env.VITE_USERS_URL}/users/${localStorage.getItem('login')!}/analysis`,
            { credentials: 'include' },
          ),
          fetch(`${GH_CONNECTOR_URL}/getPrivateRepos`, {
            credentials: 'include',
          }),
        ])

        const GHConGHUser = (await ghUserRes.json()) as GHConnectorResponse
        const analyses = (await analysesRes.json()) as AnalysisType[]
        const GHConRepos = (await reposRes.json()) as GHConnectorResponse

        if (ghUserRes.status !== 200) {
          console.error('Failed to fetch user data:', GHConGHUser)
          toast.error(GHConGHUser.error_message || 'Failed to fetch user data')
          return
        }
        if (analysesRes.status !== 200) {
          console.error('Failed to fetch analysis data:', analyses)
          toast.error('Failed to fetch analysis data')
          return
        }
        if (reposRes.status !== 200) {
          console.error('Failed to fetch analysis data:', GHConRepos.repos)
          toast.error(
            GHConRepos.error_message || 'Failed to fetch analysis data',
          )
          return
        }

        for (const a of analyses) {
          // Parse the content field of each analysis
          a.content = JSON.parse(
            a.content as unknown as string,
          ) as AnalysisContentType[]
          // console.log(new Date((a.created_at as unknown as string).replace(/(\.\d{3})\d*/, '$1')).toLocaleString())
          a.created_at = new Date(
            (a.created_at as unknown as string).replace(/(\.\d{3})\d*/, '$1') +
              'Z',
          )
        }
        setAnalysis(analyses)

        setData({
          ghUser: GHConGHUser.user_info,
          repos: GHConRepos.repos,
        })
      }
    }
    void fetchData()
  }, [isLoggedIn])

  return (
    <div className="container mx-auto p-6 text-center relative">
      <ModeToggle className="absolute top-4 right-4" />
      {isLoggedIn && data && <AccessibleRepos repos={data.repos} />}
      <h1 className="text-4xl font-extrabold text-primary mb-6 mt-16">
        Workflow Genie
      </h1>
      <div className="flex flex-col items-center gap-4">
        <Input
          value={repoUrl}
          type="search"
          placeholder="Insert GitHub Repo URL"
          className={
            'w-full max-w-md p-3 rounded-lg border-border hover:border-border'
          }
          onKeyUp={(e) => {
            if (e.key === 'Enter') {
              handleSearch()
            }
          }}
          onChange={handleInputChange}
        />
        <div className="flex gap-4">
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
            onClick={handleSearch}
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
      </div>
      {analysis.length > 0 && <Analysis analysis={analysis} />}
      <Toaster position="top-center" richColors />
    </div>
  )
}

export default LandingPage
