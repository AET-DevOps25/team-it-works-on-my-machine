import type { Analysis, Repo } from '@/lib/types'
import { Button } from '@/components/ui/button'
import { Skeleton } from '../ui/skeleton'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select'
import { cn } from '@/lib/utils'
import { handleSearch } from '@/lib/handleSearch'

function handleInstall() {
  // Open the GitHub App installation page in a new tab
  const installUrl = `https://github.com/apps/devops-workflowgenie-2025/installations/select_target`
  window.open(installUrl, '_blank', 'noopener,noreferrer')
}

function AccessibleRepos({
  privateRepoUrl,
  setPrivateRepoUrl,
  setRepoUrl,
  repos,
  setAnalyses,
  className,
}: {
  privateRepoUrl: string
  setPrivateRepoUrl: React.Dispatch<React.SetStateAction<string>>
  repoUrl: string
  setRepoUrl: React.Dispatch<React.SetStateAction<string>>
  repos: Repo[] | undefined
  setAnalyses: React.Dispatch<React.SetStateAction<Analysis[]>>
  className?: string
}) {
  if (!repos) {
    return <Skeleton className={cn('h-32', className)} />
  }

  if (repos.length === 0) {
    return (
      <div className={cn('flex flex-col gap-2', className)}>
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
      <div className={cn('flex flex-col gap-2 items-center', className)}>
        <p className="mb-5">Select from one of your private repositories</p>
        <Select
          value={privateRepoUrl}
          onValueChange={(url) => {
            handleSearch(url, setRepoUrl, setAnalyses)
            setPrivateRepoUrl('')
          }}
        >
          <SelectTrigger className="w-[90%]">
            <SelectValue placeholder="Repo URL" />
          </SelectTrigger>
          <SelectContent>
            {repos.map((repo) => {
              return (
                <SelectItem key={repo.name} value={repo.html_url}>
                  {repo.html_url}
                </SelectItem>
              )
            })}
          </SelectContent>
        </Select>
        <Button
          className="m-3 px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
          onClick={handleInstall}
        >
          Change Access Permission
        </Button>
      </div>
    )
  }
}

export default AccessibleRepos
