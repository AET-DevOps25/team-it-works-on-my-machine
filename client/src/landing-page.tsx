import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { ModeToggle } from '@/components/mode-toggle'
import { useState } from 'react'
import Profile from '@/components/logged-in/profile'
import type { Analysis, User } from '@/lib/types'
import { Toaster } from '@/components/ui/sonner'
import Analyses from '@/components/analyses'
import AccessibleRepos from '@/components/logged-in/accessible-repos'
import Logout from '@/components/logged-in/logout'
import Login from '@/components/anonymous/login'
import { useLoginQueryParameter } from '@/hooks/use-login-query-parameter'
import { useDataFromBackend } from './hooks/use-data-from-backend'
import { handleSearch } from './lib/handleSearch'

function LandingPage() {
  // State to manage the repository URL input
  const [repoUrl, setRepoUrl] = useState('')

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
              handleSearch(repoUrl, setRepoUrl, setAnalyses)
            }
          }}
          onChange={(e) => {
            setRepoUrl(e.target.value)
          }}
        />
        <div className="flex gap-4">
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
            onClick={() => {
              handleSearch(repoUrl, setRepoUrl, setAnalyses)
            }}
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
