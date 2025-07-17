// import "./profile.css";

import type { UserType } from '@/lib/types'

const Profile = ({ user }: { user: UserType }) => {
  return (
    <div className="user-info">
      <h2>User Information</h2>
      <div className="avatar-container flex justify-center">
        <img
          src={user.ghUser.avatar_url}
          alt="User Avatar"
          className="avatar h-[64px] w-[64px] rounded-full"
        />
      </div>
      <div className="info-container">
        <p>Login: {user.ghUser.login}</p>
        <p>Followers: {user.ghUser.followers}</p>
        <p>Following: {user.ghUser.following}</p>
        <p>Public Repos: {user.ghUser.public_repos}</p>
      </div>
    </div>
  )
}

export default Profile
