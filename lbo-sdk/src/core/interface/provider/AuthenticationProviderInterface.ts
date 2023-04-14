export default interface AuthenticationProviderInterface {
  onSuccess(accessToken: string, refreshToken: string): void
  onFail(): void

  resetPassword(): void
}

export interface CredentialsInterface {
  domain: string
  username: string
  password: string
}
