import type { User } from '@/lib/types'
import { cn } from '@/lib/utils'
import { Button } from '../ui/button'

const Profile = ({ user, className }: { user: User; className?: string }) => {
  return (
    <div className={cn('user-info', className)}>
      <Button
        variant="outline"
        size="icon"
        className="flex justify-center overflow-hidden"
      >
        <img src={user.ghUser.avatar_url} alt="User Avatar" />
      </Button>
    </div>
  )
}

export default Profile
