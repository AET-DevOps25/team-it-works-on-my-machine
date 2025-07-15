import '@testing-library/jest-dom'
import { render, screen } from '@testing-library/react'
import Profile from './Profile'
import type { UserType } from '@/lib/types'

describe('Profile', () => {
  it('renders user info', () => {
    const user = {
      github: {
        avatar_url: 'avatar.png',
        login: 'testuser',
        id: '1',
        type: 'User',
        followers: 10,
        following: 5,
        public_repos: 3,
      },
      user: {
        id: '1',
        username: 'testuser',
        token: 'testtoken',
        analysis: [
          {
            id: '1',
            repository: 'test-repo',
            content: [
              {
                fileName: 'test-file.txt',
                summary: 'Test summary',
                related_docs: ['doc1', 'doc2'],
                detailed_analysis: 'Detailed analysis content',
              },
            ],
          },
        ],
        github_id: '1',
      },
    } as UserType
    render(<Profile user={user} />)
    expect(screen.getByText('User Information')).toBeInTheDocument()
    expect(screen.getByText('Login: testuser')).toBeInTheDocument()
  })
})
