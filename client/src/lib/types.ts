export type UserType = {
  github: GitHubUserType
  user: UserType2
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

export type UserType2 = {
  id: string
  username: string
  token: string
  analysis: AnalysisType[]
  github_id: string
}

export type AnalysisType = {
  id: string
  repository: string
  content: string
}
