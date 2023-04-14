import RewardTypeContract from './RewardTypeContract'

export default interface RewardContract {
  name: string
  code: string
  description: string
  domainName: string
  validFor: number
  validForGranularity: number
  rewardTypes: RewardComponentContract[]
}

export interface RewardComponentContract {
  /**
   * URL of the reward provider
   */
  url: string
  rewardTypeName: string
  instant: boolean
  notificationMessage: string
  rewardTypeValues: RewardComponentValueContract[]
  rewardTypeGames: RewardComponentGameContract[]
}

export interface RewardComponentGameContract {
  /**
   * GUID of the game
   */
  guid: string
  gameId: string
  /**
   * Ensure to use commercial name
   */
  gameName: string
}

export interface RewardComponentValueContract {
  rewardTypeFieldName: string
  value: string
}

export interface RewardFullDetailsContract {
  id: number
  editUser: {
    id: number
    version: number
    guid: string
    apiToken: string
    originalId: string
    testAccount: boolean
  }
  domain: {
    id: number
    version: number
    name: string
  }
  current: {
    id: number
    name: string
    code: string
    description: string
    enabled: boolean
    validFor: number
    validForGranularity: number
    activationNotificationName: string | null
    reward: number
    revisionTypes: RewardRevisionTypeContract[]
  }
}

export interface RewardRevisionTypeContract {
  id: number
  rewardRevision: number
  instant: boolean
  rewardType: RewardTypeContract
}
