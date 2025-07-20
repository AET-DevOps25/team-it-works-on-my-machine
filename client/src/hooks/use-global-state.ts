import type { Analysis, User } from '@/lib/types'
import { create } from 'zustand'
import Cookies from 'universal-cookie'

interface GlobalState {
  // State to manage the repository URL input
  repoUrl: string
  resetRepoUrl: () => void
  setRepoUrl: (url: string) => void

  // State to manage loading state
  loading: boolean
  setLoading: (loading: boolean) => void

  // State to store the retrieved user data
  data: User | null
  setData: (data: User | null) => void

  // State to store analyses
  analyses: Analysis[]
  addAnalysis: (analysis: Analysis) => void
  setAnalyses: (analyses: Analysis[]) => void

  // State to manage login query parameter and state
  login: string | null
  setLogin: (login: string | null) => void
}

export const useGlobalState = create<GlobalState>((set) => {
  // Extracting the 'code' parameter from the URL query string (used for authorization)
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.get('login')) {
    const cookies = new Cookies(document.cookie)
    const id = cookies.get<string | undefined>('id')
    if (id !== undefined) {
      localStorage.setItem('login', id)
    }
    // Remove "login=success" from the URL if present
    const url = new URL(window.location.href)
    if (url.searchParams.has('login')) {
      url.searchParams.delete('login')
      window.history.replaceState({}, document.title, url.toString())
    }
  }

  return {
    // State to manage the repository URL input
    repoUrl: '',
    resetRepoUrl: () => {
      set({ repoUrl: '' })
    },
    setRepoUrl: (url: string) => {
      set({ repoUrl: url })
    },
    // State to manage loading state
    loading: false,
    setLoading: (loading: boolean) => {
      set({ loading })
    },
    // State to store the retrieved user data
    data: null as User | null,
    setData: (data: User | null) => {
      set({ data })
    },
    // State to store analyses
    analyses: [] as Analysis[],
    addAnalysis: (analysis: Analysis) => {
      set((state) => ({
        analyses: [analysis, ...state.analyses],
      }))
    },
    setAnalyses: (analyses: Analysis[]) => {
      set({ analyses })
    },
    // State to manage login query parameter and state
    login: localStorage.getItem('login'),
    setLogin: (login: string | null) => {
      set({ login })
      if (login) {
        localStorage.setItem('login', login)
      } else {
        localStorage.removeItem('login')
      }
    },
  }
})
