export type UserType = {
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
