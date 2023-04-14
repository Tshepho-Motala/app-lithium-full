<template>
  <v-form lazy-validation
          ref="form">
    <v-row no-gutters>
      <v-col cols="12">
        <v-select v-model="rewardType"
                  :items="selectableRewardTypes"
                  outlined
                  deletable-chips
                  label="Select Reward Type"
                  return-object
                  :loading="loading"
                  :disabled="loading"
                  @change="onRewardTypeSelected"
                  hide-details
                  dense>

          <template v-slot:selection="{ item }">
            {{ rewardFriendlyName(item) }}
          </template>

          <template v-slot:item="{ item }">
            <template>
              {{ rewardFriendlyName(item) }}
            </template>
          </template>
        </v-select>
      </v-col>
      <v-col cols="12"
             v-if="rewardType !== null">
        <v-row no-gutters>
          <v-col cols="12">
            <span class="text-caption grey--text"
                  v-text="rewardType.url"></span>
          </v-col>
          <v-col cols="12">
            <v-switch label="Player Acceptance Required"
                      :hide-details="rewardType.playerAcceptanceRequired"
                      v-model="rewardType.playerAcceptanceRequired"></v-switch>
          </v-col>
          <v-col cols="12"
                 v-if="rewardType.playerAcceptanceRequired">
            <v-textarea outlined
                        label="Acceptance Notification"
                        v-model="rewardType.playerAcceptanceMessage" />
          </v-col>
          <template v-if="rewardType.hasFields">
            <v-col cols="12"
                   v-for="(field, i) in rewardType.fieldValueBinding"
                   :key="`fields_${i}`"
                   class="pt-2">
              <v-text-field :type="rewardType.setupFields[i].dataType"
                            dense
                            hide-details
                            outlined
                            :label="field.rewardTypeFieldName + ' *'"
                            v-model="rewardType.fieldValueBinding[i].value"
                            :rules="rules.required"></v-text-field>
            </v-col>
          </template>
          <v-col v-else
                 class="pb-2">
            <span class="grey--text caption">This reward type has no custom fields.</span>
          </v-col>
        </v-row>
        <v-row v-if="rewardType.displayGames"
               no-gutters
               class="py-2">
          <v-col cols="12">
            <RewardProviderGameSelector multiple
                                        dense
                                        hideDetails
                                        v-model="rewardType.selectedGame"
                                        :rewardType="rewardType.name"
                                        :domain="domain"
                                        :provider="getProviderFor(rewardType)"
                                        required />
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-form>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { Vue, Component, Inject, Prop, Watch, VModel, Mixins } from 'vue-property-decorator'

import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import RewardProvider from '@/plugin/components/provider-selectors/RewardProvider'
import RewardProviderGameSelector from '@/plugin/components/game-selectors/RewardProviderGameSelector.vue'
import AxiosApiClients, { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import RewardType from './RewardType'
import RulesMixin from '@/plugin/mixins/RulesMixin'

@Component({
  components: {
    RewardProviderGameSelector
  }
})
export default class RewardTypeSelect extends Mixins(RulesMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel({ default: null }) rewardType!: RewardType | null

  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ required: true }) readonly providers!: RewardProvider[]
  @Prop({ default: '' }) hint!: string

  loading = false
  selectableRewardTypes: RewardType[] = []

  get hasRewardType(): boolean {
    return this.rewardType !== null
  }

  mounted() {
    this.doWork()
  }

  @Watch('providers')
  async doWork() {
    this.loading = true

    const rewardTypes = await this.apiClients.serviceReward.getRewardTypesForProviders(this.providers.map((p) => p.toContract()))
    if (rewardTypes !== null) {
      this.selectableRewardTypes = rewardTypes.map((t) => RewardType.fromContract(t))
    } else {
      this.selectableRewardTypes = []
    }

    this.loading = false
  }

  rewardFriendlyName(rewardType: RewardType) {

    const name = this.formatRewardComponentName(rewardType.name)

    if (typeof rewardType.code === 'undefined' || rewardType.code === null) {
      return name
    }

    return `${name} - ${rewardType.code}`

  }

  formatRewardComponentName(name: string): string {
    const nameWithoutSpecialChars: string = name.replace(/[^a-zA-Z ]/g, ' ')
    const nameParts: Array<string> = nameWithoutSpecialChars.split(' ')

    return nameParts.map(seg => {
      return seg.charAt(0).toUpperCase() + seg.slice(1).toLowerCase()
    }).join(' ');
  }

  onRewardTypeSelected(rewardType: RewardType) {
    this.$emit('change', rewardType)
  }

  getProviderFor(rewardType: RewardType): RewardProvider {
    return this.providers.find((x) => x.url === rewardType.url)!
  }

  validate() {
    return this.$refs['form'].validate()
  }
}
</script>

<style scoped></style>