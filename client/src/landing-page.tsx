import { ModeToggle } from '@/components/ui/mode-toggle'
import { useState } from 'react'
import Profile from '@/components/logged-in/profile'
import type { Analysis, User } from '@/lib/types'
import { Toaster } from '@/components/ui/sonner'
import Analyses from './components/analyses'
import AccessibleRepos from '@/components/logged-in/accessible-repos'
import { useLoginQueryParameter } from '@/hooks/use-login-query-parameter'
import { useDataFromBackend } from './hooks/use-data-from-backend'
import Search from './components/search'

function LandingPage() {
  // State to manage the repository URL input
  const [repoUrl, setRepoUrl] = useState('')
  const [privateRepoUrl, setPrivateRepoUrl] = useState('')

  // State to manage loading state
  const [loading, setLoading] = useState(false)

  // State to store the retrieved user data
  const [data, setData] = useState<User | null>(null)

  // State to store analyses
  const [analyses, setAnalyses] = useState<Analysis[]>([])

  // Custom hook to manage login query parameter and state
  const [login, setLogin] = useLoginQueryParameter()

  // Fetch data from the backend using the login state
  useDataFromBackend(login, setLoading, setData, setAnalyses)

  return (
    <div className="container mx-auto p-6 text-center relative">
      <ModeToggle className="absolute top-4 right-4" />
      {login && <Profile user={data} className="absolute top-4 right-16" />}
      <h1 className="text-4xl font-extrabold text-primary mb-6 mt-16">
        Workflow Genie
      </h1>
      <div className="flex content-stretch">
        <Search
          repoUrl={repoUrl}
          setRepoUrl={setRepoUrl}
          login={login}
          setLogin={setLogin}
          setAnalyses={setAnalyses}
          setData={setData}
          className={login ? 'w-1/2' : 'w-full'}
        />
        {login && (
          <AccessibleRepos
            privateRepoUrl={privateRepoUrl}
            setPrivateRepoUrl={setPrivateRepoUrl}
            repoUrl={repoUrl}
            setRepoUrl={setRepoUrl}
            repos={data?.repos}
            setAnalyses={setAnalyses}
            className="w-1/2"
          />
        )}
      </div>
      {<Analyses analyses={analyses} loading={loading} />}
      <Toaster richColors />
    </div>
  )
}

export default LandingPage
