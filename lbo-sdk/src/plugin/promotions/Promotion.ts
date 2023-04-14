import PromotionContract, {
  PromotionExclusivePlayer,
  PromotionRewardReference,
  PromotionUserCategory
} from '@/core/interface/contract-interfaces/service-promo/PromotionContract'
import { PromotionChallengeGroupContract } from '@/core/interface/contract-interfaces/service-promo/PromotionChallengeGroupContract'
import { nanoid } from 'nanoid'
import { RRule } from 'rrule'
import { DomainItemInterface } from '../cms/models/DomainItem'
import Category from '../components/Category'
import { Challenge } from './challenge/Challenge'
import { RRuleContract } from '../components/RRule'
import { toSimpleDateTimeFormat } from '@/core/utils/dateUtils'

export class Schedule implements RRuleContract {
  id: string = nanoid()

  rruleString = ''
  lengthInDays: string = '1'

  dateStart: Date | null = null
  dateStartString: string | null = null
  dateUntil: Date | null = null

  singleDay: boolean = true
  timeStart = ''

  get dateStartFormatted() {
    if (this.dateStart) {
      return toSimpleDateTimeFormat(this.dateStart)
    }
    return ''
  }

  get dateUntilFormatted() {
    if (this.dateUntil) {
      return this.dateUntil.toLocaleDateString('en-CA')
    }
    return null
  }

  setDetailsByRrule() {
    if (!this.rruleString) {
      return
    }

    const rrule = RRule.fromString(this.rruleString)
    this.dateStart = rrule.options.dtstart
    this.dateUntil = rrule.options.until
    this.singleDay = rrule.options.count === null || rrule.options.count <= 1

    return rrule
  }
}

export class PromotionTheme {
  colorName: string = 'primary'
  colorHex: string | null = '#1976d2'

  get color(): string {
    return this.colorHex || this.color
  }
}

export class Promotion {
  id: number | null = null

  title: string = ''
  description: string = ''

  schedule: Schedule = new Schedule()
  reward: PromotionRewardReference | null = null

  category: Category | null = null

  challenges: Challenge[] = [] // TODO: All these need to be interfaces

  domain: DomainItemInterface | null = null

  theme: PromotionTheme = new PromotionTheme()

  redeemOverPromotion: null | string = null // The total amount a player can redeem this promotion over the entire promotion period
  redeemOverEvents: null | string = null // The total amount a player can redeem this promotion over each of the promotion's events

  exclusive = false
  exclusivePlayers: PromotionExclusivePlayer[] = []

  tagList: PromotionUserCategory[] = []

  edit: PromotionContract | null = null // This will hold the edit object from when we fetch the promotions.

  requiresAllChallenges: boolean = true
  requiresAllChallengeGroups: boolean = false

  get challengeAmount() {
    return this.challenges.length
  }

  get hasChallenges(): boolean {
    return this.challenges.length > 0
  }

  get hasReward(): boolean {
    return this.reward !== null && this.reward !== null
  }

  get hasDraft() {
    return !!this.edit
  }

  fromContract(contract: PromotionContract, promotionId: number | null): void {
    this.id = promotionId
    this.domain = contract.domain
    this.title = contract.name
    this.description = contract.description

    this.schedule.dateStart = new Date(contract.startDate)
    this.schedule.dateStartString = contract.startDate

    if (contract.endDate) {
      this.schedule.dateUntil = new Date(contract.endDate)
    }
    this.schedule.lengthInDays = contract.eventDuration.toString()
    this.schedule.singleDay = contract.eventDuration === 1

    this.schedule.rruleString = contract.recurrencePattern
    this.reward = contract.reward
    this.redeemOverEvents = contract.redeemableInEvent.toString()
    this.redeemOverPromotion = contract.redeemableInTotal.toString()

    this.exclusive = contract.exclusive
    this.exclusivePlayers = contract.exclusivePlayers
    this.tagList = contract.userCategories

    for (const group of contract.challengeGroups) {
      for (const challenge of group.challenges) {
        const c = Challenge.fromContract(challenge, nanoid())
        this.challenges.push(c)
      }
    }
  }

  toContract(): PromotionContract {
    if (!this.domain) {
      throw Error('Domain needed')
    }

    if (!this.schedule.dateStartFormatted) {
      throw Error('Date Start needed')
    }

    if (!this.reward) {
      throw Error('Reward needed')
    }

    // Convert challenges into groups
    const items = this.challenges.filter((x) => x.groupId !== null).map((x) => x.groupId || '')
    const groupIds = Array.from(new Set(items))
    const challengeGroups: PromotionChallengeGroupContract[] = new Array(groupIds.length)

    let index = 0
    for (const groupId of groupIds) {
      const sequenced = true // TODO PROM get sequenced option from group
      challengeGroups[index] = { sequenced, challenges: [], requiresAllChallenges: this.requiresAllChallenges }

      const challenges = this.challenges.filter((x) => x.groupId === groupId)
      for (const challenge of challenges) {
        const contract = challenge.toContract()
        if (contract !== null) {
          challengeGroups[index].challenges.push(contract)
        }
      }

      index++
    }

    const redeemableInTotal = parseInt(this.redeemOverPromotion || '1')
    const redeemableInEvent = parseInt(this.redeemOverEvents || '1')
    const eventDuration = parseInt(this.schedule.lengthInDays || '1')

    return {
      domain: this.domain,
      name: this.title,
      description: this.description,
      xpLevel: 0,
      startDate: this.schedule.dateStartFormatted,
      endDate: this.schedule.dateUntilFormatted,
      recurrencePattern: this.schedule.rruleString,
      redeemableInTotal,
      redeemableInEvent,
      eventDuration,
      eventDurationGranularity: 3, // Hardcode 3 because we're always in days
      challengeGroups,
      reward: this.reward,
      exclusive: this.exclusive,
      exclusivePlayers: this.exclusivePlayers,
      userCategories: this.tagList,
      requiresAllChallengeGroups: this.requiresAllChallengeGroups
    }
  }
}
