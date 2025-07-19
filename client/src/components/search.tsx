import { handleSearch } from '@/lib/handleSearch'
import { Input } from './ui/input'
import { Button } from './ui/button'
import Logout from './logged-in/logout'
import Login from './anonymous/login'
import type { Analysis, User } from '@/lib/types'
import { cn } from '@/lib/utils'

export default function Search(
{  repoUrl,
    setRepoUrl,
    login,
    setLogin,
    setAnalyses,
    setData,
    className
}: {
    repoUrl: string
    setRepoUrl: React.Dispatch<React.SetStateAction<string>>
    login: string | null
    setLogin: React.Dispatch<React.SetStateAction<string | null>>
    setAnalyses: React.Dispatch<React.SetStateAction<Analysis[]>>
    setData: React.Dispatch<React.SetStateAction<User | null>>,
    className?: string
}
) {
  return (
    <div className={cn("flex flex-col gap-2", className)}>
      <p className="mb-5">Insert GitHub Repo URL</p>
      <Input
        value={repoUrl}
        type="search"
        placeholder="https://github.com/AET-DevOps25/team-it-works-on-my-machine"
        className={'mx-5 w-[90%] p-3 rounded-lg border-border hover:border-border'}
        onKeyUp={(e) => {
          if (e.key === 'Enter') {
            handleSearch(repoUrl, setRepoUrl, setAnalyses)
          }
        }}
        onChange={(e) => {
          setRepoUrl(e.target.value)
        }}
      />
      <div>
        <Button
          className="m-3 px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
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
  )
}
