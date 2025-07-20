import { ModeToggle } from '@/components/ui/mode-toggle'
import Profile from '@/components/logged-in/profile'
import { Toaster } from '@/components/ui/sonner'
import Analyses from '@/components/analyses'
import AccessibleRepos from '@/components/logged-in/accessible-repos'
import { useDataFromBackend } from './hooks/use-data-from-backend'
import Search from './components/search'
import Logout from './components/logged-in/logout'
import { useGlobalState } from './hooks/use-global-state'
import { ArrowLeft } from './components/icons/tabler'

function LandingPage() {
  const login = useGlobalState((state) => state.login)
  // Fetch data from the backend using the login state
  useDataFromBackend()

  return (
    <div className="container mx-auto p-6 text-center relative">
      <ModeToggle className="absolute top-4 right-37" />
      {login && <Profile className="absolute top-4 right-25" />}
      {login && <Logout className="absolute top-4 right-0" />}
      <h1 className="text-4xl font-extrabold text-primary mb-6 mt-16">
        Workflow Genie
      </h1>
      <div className="flex">
        <Search className={login ? 'w-[47.5%]' : 'w-full'} />
        {login && (
          <>
            <div className="flex flex-col items-center w-[5%]">
              <div className="mb-8">or</div>
              <div>
                <ArrowLeft />
              </div>
              <div></div>
            </div>
            <AccessibleRepos className="w-[47.5%]" />
          </>
        )}
      </div>
      <Analyses />
      <Toaster richColors />
    </div>
  )
}

export default LandingPage
