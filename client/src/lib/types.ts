export type UserType = {
  ghUser: GitHubUserType
  repos: Repo[]
}

export type Repo = {
  name: string
  visibility: string
  html_url: string
}

export type GitHubUserType = {
  avatar_url: string
  login: string
  id: string
  followers: number
  following: number
  public_repos: number
}

export type OauthResponse = {
  userData: UserType
  tokenType: string
  token: string
}

export type GHConnectorResponse = {
  status: number
  error_message: string
  results: AnalysisContentType[]
  user_info: GitHubUserType
  repos: Repo[]
}

export type AnalysisType = {
  id: string
  repository: string
  created_at: Date
  content: AnalysisContentType[]
}

export type AnalysisContentType = {
  filename: string
  summary: string
  related_docs: string[]
  detailed_analysis: string
}
