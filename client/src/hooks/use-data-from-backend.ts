import type {
  Analysis,
  AnalysisContent,
  GHConnectorResponse,
} from '@/lib/types'
import { useEffect } from 'react'
import { toast } from 'sonner'
import { useGlobalState } from './use-global-state'

const GH_CONNECTOR_URL = import.meta.env.VITE_GH_CONNECTOR_URL

export function useDataFromBackend() {
  const login = useGlobalState((state) => state.login)
  const setLoading = useGlobalState((state) => state.setLoading)
  const setData = useGlobalState((state) => state.setData)
  const setAnalyses = useGlobalState((state) => state.setAnalyses)
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
  }, [login, setAnalyses, setData, setLoading])
}

