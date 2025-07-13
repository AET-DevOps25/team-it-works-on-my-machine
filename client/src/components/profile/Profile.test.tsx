import '@testing-library/jest-dom'
import { render, screen } from '@testing-library/react'
import Profile from './Profile'
import type { UserType } from '@/lib/types'

describe('Profile', () => {
  it('renders user info', () => {
    const user = {
      avatar_url: 'avatar.png',
      login: 'testuser',
      id: '1',
      type: 'User',
      followers: 10,
      following: 5,
      public_repos: 3,
    } as UserType
    render(<Profile user={user} />)
    expect(screen.getByText('User Information')).toBeInTheDocument()
    expect(screen.getByText('Login: testuser')).toBeInTheDocument()
  })
})
