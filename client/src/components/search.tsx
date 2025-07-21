import { handleSearch } from '@/lib/handleSearch'
import { Input } from './ui/input'
import { Button } from './ui/button'
import Login from './anonymous/login'
import { cn } from '@/lib/utils'
import { useGlobalState } from '@/hooks/use-global-state'

export default function Search({ className }: { className?: string }) {
  const resetRepoUrl = useGlobalState((state) => state.resetRepoUrl)
  const setRepoUrl = useGlobalState((state) => state.setRepoUrl)
  const addAnalysis = useGlobalState((state) => state.addAnalysis)
  const repoUrl = useGlobalState((state) => state.repoUrl)
  const login = useGlobalState((state) => state.login)
  return (
    <div className={cn('flex flex-col gap-2', className)}>
      <p className="mb-5">Insert GitHub Repo URL</p>
      <Input
        value={repoUrl}
        type="search"
        placeholder="https://github.com/AET-DevOps25/team-it-works-on-my-machine"
        className={
          'mx-5 w-[90%] p-3 rounded-lg border-border hover:border-border'
        }
        onKeyUp={(e) => {
          if (e.key === 'Enter') {
            handleSearch(repoUrl, resetRepoUrl, addAnalysis)
          }
        }}
        onChange={(e) => {
          setRepoUrl(e.target.value)
        }}
      />
      <div className="flex justify-center items-center gap-4 mt-4">
        <Button
          variant={'default'}
          onClick={() => {
            handleSearch(repoUrl, resetRepoUrl, addAnalysis)
          }}
        >
          Analyse
        </Button>
        {!login && <Login />}
      </div>
    </div>
  )
}
