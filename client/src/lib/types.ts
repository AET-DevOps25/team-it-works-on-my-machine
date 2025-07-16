export type UserType = {
  github: GitHubUserType
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
  type: string
  followers: number
  following: number
  public_repos: number
}

export type OauthResponse = {
  userData: UserType
  tokenType: string
  token: string
}

export type AnalysisSingleType = {
  status: string
  results: AnalysisContentType[]
  message: string
}

export type AnalysisType = {
  id: string
  repository: string
  content: AnalysisContentType[]
}

export type AnalysisContentType = {
  filename: string
  summary: string
  related_docs: string[]
  detailed_analysis: string
}
