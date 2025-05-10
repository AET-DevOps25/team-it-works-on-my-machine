import { useState } from 'react'
import { Button } from './components/ui/button'
import { Input } from './components/ui/input'

function App() {
  const [repoUrl, setRepoUrl] = useState('')

  function handleInputChange(e: React.ChangeEvent<HTMLInputElement>) {
    setRepoUrl(e.target.value)
  }

  function handleSearch() {
    if (!repoUrl) {
      alert('Please enter a GitHub repository URL')
      return
    }
    alert('Search for: ' + repoUrl)
  }

  return (
    <div className="container mx-auto p-6 text-center">
      <h1 className="text-4xl font-extrabold text-primary mb-6 mt-16">
        GitHub Actions Analyzer
      </h1>
      <div className="flex flex-col items-center gap-4">
        <Input
          type="search"
          placeholder="Insert GitHub Repo URL"
          className="w-full max-w-md p-3 border border-border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-ring"
          onKeyUp={(e) => {
            if (e.key === 'Enter') {
              handleSearch()
            }
          }}
          onChange={handleInputChange}
        />
        <div className="flex gap-4">
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
            onClick={handleSearch}
          >
            Analyze
          </Button>
          <Button className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80">
            Login
          </Button>
        </div>
      </div>
    </div>
  )
}

export default App
