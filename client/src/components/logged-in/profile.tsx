import type { User } from '@/lib/types'
import { cn } from '@/lib/utils'
import { Button } from '../ui/button'
import { Skeleton } from '../ui/skeleton';

const Profile = ({ user, className }: { user: User | null; className?: string }) => {
  if (!user) {
    return <Skeleton className={cn('size-9', className)} />
  }

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
