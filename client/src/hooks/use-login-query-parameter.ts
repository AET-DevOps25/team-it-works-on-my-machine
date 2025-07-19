import { useState } from "react"
import Cookies from "universal-cookie"

export function useLoginQueryParameter(): [string | null, React.Dispatch<React.SetStateAction<string | null>>] {
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
  return useState(localStorage.getItem('login'))
}