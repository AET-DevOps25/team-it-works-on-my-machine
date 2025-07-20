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
    <Button variant={'secondary'} onClick={handleLogin}>
      <BrandGithub className="h-5" />
      Login
    </Button>
  )
}

export default Login
