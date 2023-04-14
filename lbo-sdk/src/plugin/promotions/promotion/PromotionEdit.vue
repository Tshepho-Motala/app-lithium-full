<template>
  <DomainSelectorPage
    v-model="promotion.domain"
    ref="domainSelector"
    :title="titleDisplay"
    :description="description"
    noGutters
    :extended="hasDomain"
    showDivider
  >
    <template #extension>
      <v-tabs v-model="tabModel" centered grow style="margin-bottom: 1px" class="px-4">
        <v-tab>
          <v-icon left>mdi-pencil</v-icon>
          <span>Details *</span>
        </v-tab>
        <v-tab>
          <v-icon left>mdi-calendar</v-icon>
          <span>Schedule *</span>
        </v-tab>
        <v-tab>
          <v-icon left>mdi-cash</v-icon>
          <span>Activation Rules *</span>
        </v-tab>
        <v-tab>
          <v-icon left>mdi-account-cancel-outline</v-icon>
          <span>Player Rules</span>
        </v-tab>
      </v-tabs>
    </template>

    <template #actions>
      <v-btn text @click="onCancel">Cancel</v-btn>
      <v-spacer></v-spacer>
      <v-btn color="success" @click="onSave" :disabled="!canSave">Save</v-btn>
    </template>

    <template>
      <div style="max-height: 800px">
        <v-tabs-items v-model="tabModel" class="pt-6">
          <v-tab-item>
            <v-row>
              <v-col cols="12">
                <v-select outlined disabled label="Select Promotion Dependency"></v-select>
              </v-col>
              <v-col cols="8">
                <BasicDetails :promotion="promotion" />
              </v-col>
              <v-col cols="4">
                <StepViewReward v-if="hasReward" :reference="promotion.reward" :domain="promotion.domain" :color="promotion.theme.color"  @delete="onRewardDelete"/>
                <StepAddReward v-if="!hasReward" :domain="promotion.domain" :color="promotion.theme.color" @save="onRewardSaved" />
              </v-col>
              <v-col cols="12">
                <div>
                  <span class="text-h6">Challenges</span>
                </div>
                <div>
                  <v-alert :color="color" outlined icon="mdi-help" border="left">
                    <span class="text-body-2">
                      Create challenges for the player to complete via <strong>Challenge Paths</strong>. A player can complete any
                      <strong>Path</strong> to activate the <strong>Promotion Reward</strong>. All <strong>Challenges</strong> in a
                      <strong>Path</strong> are required to be completed before the <strong>Path</strong> is complete.</span
                    >
                  </v-alert>
                </div>
              </v-col>
            </v-row>

            <ChallengeGroupEdit v-model="promotion" class="pt-3" />
          </v-tab-item>
          <v-tab-item>
            <Scheduling :promotion="promotion" />
          </v-tab-item>
          <v-tab-item>
            <ActivationDetails :promotion="promotion" />
          </v-tab-item>
          <v-tab-item>
            <v-row>
              <v-col cols="6"><ExclusiveConfig v-model="promotion" /></v-col>
              <v-col cols="6"><TagBlacklistWhitelist v-model="promotion" :domain="promotion.domain" /></v-col>
            </v-row>
          </v-tab-item>
        </v-tabs-items>
      </div>
    </template>
  </DomainSelectorPage>
</template>

<script lang='ts'>
import { Vue, Component, Inject, VModel } from 'vue-property-decorator'
import { Promotion } from '../Promotion'

import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'

import DomainSelectorPage from '@/plugin/components/DomainSelectorPage.vue'
import ChallengeGroupEdit from '../challenge-group/ChallengeGroupEdit.vue'
import StepAddReward from '../challenge-group/edit-steps/StepAddReward.vue'
import StepViewReward from '../challenge-group/edit-steps/StepViewReward.vue'
import BasicDetails from './promotion-edit/BasicDetails.vue'
import Scheduling from './promotion-edit/Scheduling.vue'
import ExclusiveConfig from './promotion-edit/ExclusiveConfig.vue'
import ActivationDetails from './promotion-edit/ActivationDetails.vue'
import TagBlacklistWhitelist from './promotion-edit/TagBlacklistWhitelist.vue'
import { RewardFullDetailsContract } from '@/core/interface/contract-interfaces/service-reward/RewardContract'

@Component({
  components: {
    DomainSelectorPage,
    BasicDetails,
    Scheduling,
    ChallengeGroupEdit,
    StepAddReward,
    StepViewReward,
    ExclusiveConfig,
    TagBlacklistWhitelist,
    ActivationDetails
  }
})
export default class PromotionEdit extends Vue {
  @VModel({ required: true, type: Promotion }) readonly promotion!: Promotion
  @Inject('translateService') translateService!: TranslateServiceInterface

  tabModel = null

  get hasDomain(): boolean {
    if (!this.promotion) {
      return false
    }
    return !!this.promotion.domain
  }

  get hasBasicDetails(): boolean {
    if (!this.promotion) {
      return false
    }
    return !!this.promotion.title && !!this.promotion.description
  }

  get hasReward() {
    if (!this.promotion) {
      return false
    }
    return !!this.promotion.reward
  }

  get hasChallenges() {
    if (!this.promotion) {
      return false
    }
    return this.promotion.challengeAmount > 0
  }

  get hasSchedule(): boolean {
    if (!this.promotion) {
      return false
    }
    const hasRedemption = !!this.promotion.redeemOverEvents && !!this.promotion.redeemOverPromotion
    return !!this.promotion.schedule.dateStart && !!this.promotion.schedule.rruleString && hasRedemption
  }

  get canSave(): boolean {
    return this.hasDomain && this.hasBasicDetails && this.hasReward && this.hasSchedule
  }

  get domainHint(): string {
    return this.translateService.instant('UI_NETWORK_ADMIN.DOMAIN_SELECTOR.OUTPUT.SELECTED_DOMAIN_HINT')
  }

  get description(): string {
    return 'The promotion will be available to all players registered under the selected domain.'
  }

  get titleDisplay(): string {
    if (!this.promotion || !this.promotion.domain) {
      return 'Please select a Domain'
    }
    return this.promotion?.title || 'Name your Promotion'
  }

  get domainDisplay(): string {
    if (!this.promotion || !this.promotion.domain) {
      return ''
    }
    return `${this.promotion.domain.displayName} (${this.promotion.domain.name})`
  }

  get domainSelectNextDisabled() {
    return !this.hasDomain
  }

  get color() {
    if (!this.promotion) {
      return 'primary'
    }
    return this.promotion.theme.color
  }

  onRewardSaved(reward: RewardFullDetailsContract) {
    this.promotion.reward = {
      id: reward.id,
      rewardId: reward.current.id
    }
  }
  onRewardDelete(){
    // TODO Make sure to call delete endpoint
    this.promotion.reward = null
  }

  onSave() {
    this.$emit('save')
    
  }

  onCancel() {
    this.$emit('cancel')
  }
}
</script>

<style>
.feature-display {
  border: 1px solid blue;
  border-radius: 5px;
}
</style>