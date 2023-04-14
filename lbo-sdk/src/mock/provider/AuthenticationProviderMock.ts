import AuthenticationProviderInterface from '@/core/interface/provider/AuthenticationProviderInterface'

export default class AuthenticationProviderMock implements AuthenticationProviderInterface {
  onSuccess(accessToken: string, refreshToken: string): void {
    return
  }

  onFail(): void {
    return
  }

  resetPassword(): void {
    return
  }
}
