import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { ModeToggle } from '../mode-toggle'
import { useEffect, useState } from 'react'
import Profile from '../profile/Profile'
import type { OauthResponse, UserType } from '@/lib/types'

function LandingPage() {
  const [repoUrl, setRepoUrl] = useState('')
  const [error, setError] = useState(false)

  // Extracting the 'code' parameter from the URL query string (used for authorization)
  const urlParams = new URLSearchParams(window.location.search)
  const code = urlParams.get('code')
  // State to store the retrieved user data
  const [data, setData] = useState<UserType | OauthResponse | null>(null)
  // State to indicate if data is being fetched
  const [loading, setLoading] = useState(false)

  function handleInputChange(e: React.ChangeEvent<HTMLInputElement>) {
    setRepoUrl(e.target.value)
    if (error) setError(false) // Clear error when user starts typing
  }

  async function handleSearch() {
    if (!repoUrl) {
      setError(true)
      return
    }
    const res = await fetch(
      `http://localhost:8589/getInfo?repoUrl=${repoUrl}`,
      {
        credentials: 'include',
      },
    )
    const data = (await res.json()) as object
    console.log(data)
    alert(data)
  }

  // Runs whenever the 'code' variable changes (likely on authorization flow)
  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      setLoading(true) // Set loading to true while fetching data
      void fetch('https://api.github.com/user')
        .then((res) => res.json()) // Parse the response as JSON
        .then((data) => {
          setData(data as UserType) // Update state with fetched user data
          setLoading(false) // Set loading to false when done fetching
        })
    } else if (code) {
      // If no token but 'code' is available (GitHub OAuth flow)
      setLoading(true) // Set loading to true while fetching data
      void fetch(
        `http://localhost:8589/oauth/redirect?code=${code}&state=YOUR_RANDOMLY_GENERATED_STATE`,
        { credentials: 'include' },
      )
        .then((res) => res.json()) // Parse the response as JSON
        .then((data) => {
          const dataTyped = data as OauthResponse
          setData(dataTyped.userData) // Update state with user data from response
          localStorage.setItem(
            'token',
            `${dataTyped.tokenType} ${dataTyped.token}`,
          ) // Store access token in local storage
          setLoading(false) // Set loading to false when done fetching
        })
    }
  }, [code])

  function handleLogin() {
    console.log('login')
    // Function to redirect the user to the GitHub OAuth authorization page
    const client_id = 'Ov23liKRNopX1GiZzGT6'
    const redirect_uri = 'http://localhost:5173/'
    const scope = 'read:user,repo'
    const authUrl = `https://github.com/login/oauth/authorize?client_id=${client_id}&redirect_uri=${redirect_uri}&scope=${scope}`

    window.location.href = authUrl
  }

  if (loading) {
    return <h4>Loading...</h4>
  }

  return (
    <div className="container mx-auto p-6 text-center relative">
      <ModeToggle className="absolute top-4 right-4" />
      <h1 className="text-4xl font-extrabold text-primary mb-6 mt-16">
        GitHub Actions Analyzer
      </h1>
      <div className="flex flex-col items-center gap-4">
        <Input
          type="search"
          placeholder="Insert GitHub Repo URL"
          className={`w-full max-w-md p-3 rounded-lg ${
            error
              ? 'border-red-500 focus-visible:ring-red-300 hover:border-red-500'
              : 'border-border hover:border-border'
          }`}
          onKeyUp={(e) => {
            if (e.key === 'Enter') {
              void handleSearch()
            }
          }}
          onChange={handleInputChange}
        />
        {error && (
          <p className="text-red-500 text-sm">
            Please enter a GitHub repository URL
          </p>
        )}
        <div className="flex gap-4">
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
            onClick={void handleSearch}
          >
            Analyze
          </Button>
          <Button
            className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
            onClick={handleLogin}
          >
            Login
          </Button>
        </div>
        {data && <Profile user={data as UserType} />}
      </div>
    </div>
  )
}

export default LandingPage
