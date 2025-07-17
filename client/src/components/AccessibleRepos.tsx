import type { Repo } from "@/lib/types"
import { Button } from "@/components/ui/button"


function AccessibleRepos({ repos }: { repos: Repo[] }) {
  function handleInstall() {
    console.log('install')
    // Open the GitHub App installation page in a new tab
    const installUrl = `https://github.com/apps/devops-workflowgenie-2025/installations/select_target`
    window.open(installUrl, '_blank', 'noopener,noreferrer')
  }

  if (repos.length === 0) {
    return (
      <div>
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
      <div>
        <p>
          Workflow genie currently has access to these private repositories:
        </p>
        <ul>
          {repos.map((repo) => {
            return (
              <li key={repo.name}>
                {repo.name}: {repo.html_url}
              </li>
            )
          })}
        </ul>
        <Button
          className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
          onClick={handleInstall}
        >
          Grant additional or revoke existing Access Permission
        </Button>
      </div>
    )
  }
}

export default AccessibleRepos