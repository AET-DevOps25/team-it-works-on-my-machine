import { BrandGithub } from '../icons/tabler'
import { Button } from '../ui/button'

function Login() {
  function handleLogin() {
    // Function to redirect the user to the GitHub OAuth authorization page
    const client_id = import.meta.env.VITE_GH_OAUTH_CLIENT_ID
    const redirect_uri =
      import.meta.env.VITE_GH_CONNECTOR_URL + '/oauth/redirect'
    const authUrl = `https://github.com/login/oauth/authorize?client_id=${client_id}&redirect_uri=${redirect_uri}`

    window.location.href = authUrl
  }

  return (
    <Button
      className="m-3 px-6 py-2 bg-primary text-primary-foreground rounded-lg shadow hover:bg-primary/80"
      onClick={handleLogin}
    >
      {/* TODO fix formatting */}
      <BrandGithub />
      Login
    </Button>
  )
}

export default Login
