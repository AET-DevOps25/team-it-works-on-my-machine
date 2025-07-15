// import "./profile.css";

import type { UserType } from '@/lib/types'

const Profile = ({ user }: { user: UserType }) => {
  return (
    <div className="user-info">
      <h2>User Information</h2>
      <div className="avatar-container">
        <img
          src={user.github.avatar_url}
          alt="User Avatar"
          className="avatar"
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
      <div>
        <h2>Analysis:</h2>
        <ul>
          {user.user.analysis.map((analysis) => (
            <li key={analysis.id}>
              <div>
                <strong>Repository:</strong> {analysis.repository} <br />
              </div>
              <div>
                <strong>Content:</strong>
                <ul>
                  {analysis.content.map((content) => (
                    <li key={content.filename}>
                      <p>File Name: {content.filename}</p>
                      <p>Summary: {content.summary}</p>
                      <p>Related Docs: {content.related_docs.join(', ')}</p>
                      <p>Detailed Analysis: {content.detailed_analysis}</p>
                    </li>
                  ))}
                </ul>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

export default Profile
