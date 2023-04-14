export default interface AuthenticationContract {
  access_token: string
  token_type: string
  refresh_token: string
  expires_in: number
  scope: string
  userId: number
  username: string
  email: string
  userGuid: string
  shortGuid: string
  firstName: string
  lastName: string
  registrationDate: number
  sessionId: number
  lastLogin: number
  lastIP: string
  optOutEmail: boolean
  optOutSms: boolean
  optOutCall: boolean
  optOutPush: boolean
  optOutPost: boolean
  verificationLevel: number
  contraAccountSet: boolean
  ageVerified: boolean
  addressVerified: boolean
  commsOptInComplete: boolean
  termsAndConditionsVersion: TermsAndConditionsVersionContract
}

export interface TermsAndConditionsVersionContract {
  acceptedUserVersion: string | null
  currentDomainVersion: string
}
