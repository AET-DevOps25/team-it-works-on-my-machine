export type User = {
  ghUser: GitHubUser
  repos: Repo[]
}

export type Repo = {
  name: string
  visibility: string
  html_url: string
}

export type GitHubUser = {
  avatar_url: string
  login: string
  id: string
  followers: number
  following: number
  public_repos: number
}

export type OauthResponse = {
  userData: User
  tokenType: string
  token: string
}

export type GHConnectorResponse = {
  status: number
  error_message: string
  analysis: Analysis
  user_info: GitHubUser
  repos: Repo[]
}

export type Analysis = {
  id: string
  repository: string
  created_at: Date
  content: AnalysisContent[]
  highlighted?: boolean
}

export type AnalysisContent = {
  filename: string
  summary: string
  related_docs: string[]
  detailed_analysis: string
}
