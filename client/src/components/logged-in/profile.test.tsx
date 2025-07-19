import '@testing-library/jest-dom'
import { render, screen } from '@testing-library/react'
import Profile from './profile'
import type { User } from '@/lib/types'

describe('Profile', () => {
  it('renders user info', () => {
    const user = {
      ghUser: {
        avatar_url: 'avatar.png',
        login: 'testuser',
        id: '1',
        followers: 10,
        following: 5,
        public_repos: 3,
      },
    } as User
    render(<Profile user={user} />)
    expect(screen.getByText('User Information')).toBeInTheDocument()
    expect(screen.getByText('Login: testuser')).toBeInTheDocument()
  })
})
