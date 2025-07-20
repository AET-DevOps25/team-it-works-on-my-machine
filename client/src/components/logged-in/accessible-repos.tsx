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
import { useGlobalState } from '@/hooks/use-global-state'
import { useState } from 'react'
import { ExternalLink } from '../icons/tabler'

function handleInstall() {
  // Open the GitHub App installation page in a new tab
  const installUrl = `https://github.com/apps/devops-workflowgenie-2025/installations/select_target`
  window.open(installUrl, '_blank', 'noopener,noreferrer')
}

function AccessibleRepos({ className }: { className?: string }) {
  const repos = useGlobalState((state) => state.data?.repos)
  const setRepoUrl = useGlobalState((state) => state.setRepoUrl)
  const [privateRepoUrl, setPrivateRepoUrl] = useState('')
  if (!repos) {
    return (
      <div className={className}>
        <Skeleton className="h-35 mx-4" />
      </div>
    )
  }

  if (repos.length === 0) {
    return (
      <div className={cn('flex flex-col gap-2', className)}>
        <p>Workflow Genie cannot access any of you private repositories</p>
        <Button
          className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
          onClick={handleInstall}
        >
          Grant Workflow Genie Access
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
            setRepoUrl(url)
            setPrivateRepoUrl('')
          }}
        >
          <SelectTrigger className="w-[90%] mx-5">
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
        <div className="flex justify-center items-center gap-4 mt-4">
          <Button variant={'secondary'} onClick={handleInstall}>
            Choose Accessible Repositories
            <ExternalLink />
          </Button>
        </div>
      </div>
    )
  }
}

export default AccessibleRepos
