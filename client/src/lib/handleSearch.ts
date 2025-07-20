import { toast } from 'sonner'
import type { Analysis, GHConnectorResponse } from './types'

const GH_CONNECTOR_URL = import.meta.env.VITE_GH_CONNECTOR_URL

export function handleSearch(
  repoUrl: string,
  resetRepoUrl: () => void,
  addAnalysis: (analysis: Analysis) => void,
) {
  if (!repoUrl) {
    toast.warning('Please enter a GitHub repository URL')
    return
  }
  const repoUrlTmp = repoUrl
  resetRepoUrl()

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
    data.analysis.created_at = new Date(
      (data.analysis.created_at as unknown as string).replace(
        /(\.\d{3})\d*/,
        '$1',
      ) + 'Z',
    )
    data.analysis.highlighted = true
    addAnalysis(data.analysis)
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
