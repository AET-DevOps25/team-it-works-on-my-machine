// import "./profile.css";

import type { UserType } from '@/lib/types'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../ui/card'

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
              {analysis.content.map((content) => (
                <div key={content.filename} className="mb-4">
                  <Card>
                    <CardHeader>
                      <CardTitle>{content.filename}</CardTitle>
                      <CardDescription>{content.summary}</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="mb-2">
                        <strong>Related Docs:</strong>{' '}
                        {content.related_docs.length > 0
                          ? content.related_docs.join(', ')
                          : 'None'}
                      </div>
                      <div>
                        <strong>Detailed Analysis:</strong>
                        <p className="mt-1 text-muted-foreground">
                          {content.detailed_analysis}
                        </p>
                      </div>
                    </CardContent>
                  </Card>
                </div>
              ))}
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

export default Profile
