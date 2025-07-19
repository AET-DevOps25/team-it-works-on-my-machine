import { Button } from '../ui/button'

function Login() {
  function handleLogin() {
    // Function to redirect the user to the GitHub OAuth authorization page
    const redirect_uri =
      import.meta.env.VITE_GH_CONNECTOR_URL + '/oauth/redirect'
    // const scope = 'read:user,repo'
    const authUrl = `https://github.com/login/oauth/authorize?client_id=${import.meta.env.VITE_GH_OAUTH_CLIENT_ID}&redirect_uri=${redirect_uri}`

    window.location.href = authUrl
  }

  return (
    <Button
      className="px-6 py-2 bg-secondary text-secondary-foreground rounded-lg shadow hover:bg-secondary/80"
      onClick={handleLogin}
    >
      Login
    </Button>
  )
}

export default Login
