import {
  PromotionChallengeContract,
  PromotionChallengeRuleContract
} from '@/core/interface/contract-interfaces/service-promo/PromotionChallengeContract'

/**
 * Used in ChallengeGroups>Challenges in POST body to
 * {{gateway}}/service-promo/backoffice/promotion/v1/create-draft
 */
export class Challenge implements PromotionChallengeContract {
  groupId: string | null = null

  sharedValue: any | null = null
  sharedOperation: string | null = null

  get basicCompleted() {
    return !!this.description
  }

  constructor(
    public description: string = '',
    public rules: PromotionChallengeRuleContract[] = [],
    public requiresAllRules: boolean = false,
    public sequenceNumber: number = 0
  ) {
    // this.setExtraFieldValues()
  }

  static fromContract(contract: PromotionChallengeContract, groupId: string): Challenge {
    const item = new this(contract.description, contract.rules, contract.requiresAllRules, contract.sequenceNumber)
    item.groupId = groupId

    // Fuzzy logic check to see if we set the shared value,
    // if so, set it to the first rule's value and operation,
    // as the rest should be the same
    const sharedValues = !contract.requiresAllRules
    if (sharedValues) {
      item.sharedOperation = contract.rules[0].operation
      item.sharedValue = contract.rules[0].value
    }

    return item
  }

  toContract(): PromotionChallengeContract | null {
    return {
      description: this.description,
      requiresAllRules: this.requiresAllRules,
      sequenceNumber: this.sequenceNumber,
      rules: [...this.rules]
    }
  }

  reset() {
    this.groupId = null
    this.sharedValue = ''
    this.sharedOperation = ''
    this.description = ''
    this.rules = []
    this.requiresAllRules = false
    this.sequenceNumber = 0
  }

  checkRulesComplete() {
    for (const rule of this.rules) {
      if (!rule.promoProvider || !rule.promoProvider.url) {
        return false
      }
      if (!rule.operation || rule.value === undefined || rule.value === null) {
        return false
      }
      if (Number.isNaN(rule.value) || !Number.isInteger(rule.value) || !Number.isFinite(rule.value)) {
        return false
      }
    }
    return true
  }
}

// UI only virtual construct that we derive from GroupID
export class ChallengeGroup {
  challengeItems: Map<string, Challenge> = new Map()

  constructor(public id: string) {}
}
