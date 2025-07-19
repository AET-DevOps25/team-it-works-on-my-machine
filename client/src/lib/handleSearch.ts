import { toast } from 'sonner'
import type { Analysis, GHConnectorResponse } from './types'

const GH_CONNECTOR_URL = import.meta.env.VITE_GH_CONNECTOR_URL

export function handleSearch(
  repoUrl: string,
  setRepoUrl: React.Dispatch<React.SetStateAction<string>>,
  setAnalyses: React.Dispatch<React.SetStateAction<Analysis[]>>,
) {
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
      throw new Error(data.error_message || 'Failed to fetch repo data')
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
    error: (e: Error) => e.message,
  })
}
