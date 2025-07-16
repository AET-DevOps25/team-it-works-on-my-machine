// import "./profile.css";

import type { UserType } from '@/lib/types'

const Profile = ({ user }: { user: UserType }) => {
  return (
    <div className="user-info">
      <h2>User Information</h2>
      <div className="avatar-container flex justify-center">
        <img
          src={user.github.avatar_url}
          alt="User Avatar"
          className="avatar h-[64px] w-[64px] rounded-full"
        />
      </div>
      <div className="info-container">
        <p>Login: {user.github.login}</p>
        <p>ID: {user.github.id}</p>
        <p>Type: {user.github.type}</p>
        <p>Followers: {user.github.followers}</p>
        <p>Following: {user.github.following}</p>
        <p>Public Repos: {user.github.public_repos}</p>
      </div>
    </div>
  )
}

export default Profile
