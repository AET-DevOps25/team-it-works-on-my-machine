// import "./profile.css";

import type { UserType } from '@/lib/types'

const Profile = ({ user }: { user: UserType }) => {
  return (
    <div className="user-info">
      <h2>User Information</h2>
      <div className="avatar-container">
        <img src={user.avatar_url} alt="User Avatar" className="avatar" />
      </div>
      <div className="info-container">
        <p>Login: {user.login}</p>
        <p>ID: {user.id}</p>
        <p>Type: {user.type}</p>
        <p>Followers: {user.followers}</p>
        <p>Following: {user.following}</p>
        <p>Public Repos: {user.public_repos}</p>
      </div>
    </div>
  )
}

export default Profile
