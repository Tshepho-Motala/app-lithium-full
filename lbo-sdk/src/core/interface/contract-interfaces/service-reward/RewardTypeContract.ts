export default interface RewardTypeContract {
  id: number
  url: string
  name: string
  code: null | string
  setupFields: RewardSetupFieldContract[]
  displayGames: boolean
}

export interface RewardTypeListContract extends Array<RewardTypeContract> {}

export interface RewardSetupFieldContract {
  id: number
  name: string
  dataType: string
  description: string
}
