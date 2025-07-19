import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { ModeToggle } from '../mode-toggle'
import { useEffect, useState } from 'react'
import Profile from '../profile/Profile'
import type {
  AnalysisContent,
  GHConnectorResponse,
  Analysis,
  User,
} from '@/lib/types'
import { toast } from 'sonner'
import { Toaster } from '../ui/sonner'
import Analyses from '../Analyses'
import AccessibleRepos from '../AccessibleRepos'
import Logout from '../Logout'
import Login from '../Login'
import { useLoginQueryParameter } from '@/hooks/use-login-query-parameter'

const GH_CONNECTOR_URL = import.meta.env.VITE_GH_CONNECTOR_URL

function LandingPage() {
  const [repoUrl, setRepoUrl] = useState('')
  const [analyses, setAnalyses] = useState<Analysis[]>([])

  const [login, setLogin] = useLoginQueryParameter()
  const [loading, setLoading] = useState(false)

  // State to store the retrieved user data
  const [data, setData] = useState<User | null>(null)

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
      setAnalyses((oldAnalysis) => [
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
      if (login) {
        setLoading(true)
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
        const analyses = (await analysesRes.json()) as Analysis[]
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

        for (const analysis of analyses) {
          // Parse the content field of each analysis
          analysis.content = JSON.parse(
            analysis.content as unknown as string,
          ) as AnalysisContent[]
          // console.log(new Date((analysis.created_at as unknown as string).replace(/(\.\d{3})\d*/, '$1')).toLocaleString())
          analysis.created_at = new Date(
            (analysis.created_at as unknown as string).replace(
              /(\.\d{3})\d*/,
              '$1',
            ) + 'Z',
          )
        }
        setAnalyses(analyses)

        setData({
          ghUser: GHConGHUser.user_info,
          repos: GHConRepos.repos,
        })
        setLoading(false)
      }
    }
    void fetchData()
  }, [login])

  return (
    <div className="container mx-auto p-6 text-center relative">
      <ModeToggle className="absolute top-4 right-4" />
      {data && <Profile user={data} className="absolute top-4 right-16" />}
      {login && data && <AccessibleRepos repos={data.repos} />}
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
          onChange={(e) => {
            setRepoUrl(e.target.value)
          }}
        />
        <div className="flex gap-4">
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
            onClick={handleSearch}
          >
            Analyze
          </Button>
          {login ? (
            <Logout
              setLogin={setLogin}
              setData={setData}
              setAnalyses={setAnalyses}
            />
          ) : (
            <Login />
          )}
        </div>
      </div>
      {<Analyses analyses={analyses} loading={loading} />}
      <Toaster position="top-center" richColors />
    </div>
  )
}

export default LandingPage
