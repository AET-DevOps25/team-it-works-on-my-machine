// import "./profile.css";

import type { UserType } from '@/lib/types'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../ui/card'
import Markdown from 'react-markdown'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@radix-ui/react-accordion'

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
      <div>
        <h2>Analysis:</h2>
        <Accordion type="single" collapsible>
          {user.user.analysis.map((analysis) => (
            <AccordionItem value={analysis.id} key={analysis.id}>
              <AccordionTrigger>
                <strong>Repository:</strong> {analysis.repository} <br />
              </AccordionTrigger>
              <AccordionContent>
                {analysis.content.map((content) => (
                  <div key={content.filename} className="mb-4">
                    <Card>
                      <CardHeader>
                        <CardTitle>{content.filename}</CardTitle>
                        <CardDescription>{content.summary}</CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="mb-2 w-[700px] rounded-md border text-left">
                          <strong>Related Docs:</strong>{' '}
                          <Markdown>
                            {content.related_docs.length > 0
                              ? content.related_docs.join(', ')
                              : 'None'}
                          </Markdown>
                        </div>
                        <div>
                          <strong>Detailed Analysis:</strong>
                          <div className="w-[700px] rounded-md border p-4 mt-1 text-muted-foreground text-left">
                            <Markdown>{content.detailed_analysis}</Markdown>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  </div>
                ))}
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    </div>
  )
}

export default Profile
