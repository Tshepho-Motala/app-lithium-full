
<template>
  <!-- TODO: Move the CMS GameSelector to this one -->
  <v-card data-test-id="cnt-game-selector" flat>
    <v-autocomplete
      outlined
      :hint="hint"
      :persistent-hint="!!hint"
      :label="label"
      :items="selectableGames"
      v-model="games"
      item-text="name"
      return-object
      @change="onChange"
      :multiple="multiple"
      :chips="multiple"
      deletable-chips
      :small-chips="multiple"
      :disabled="disabled"
      :dense="dense"
      :hide-details="hideDetails"
      :rules="rules.selectedGtZero"
      :search-input.sync="search"
    >
    </v-autocomplete>
  </v-card>
</template>

<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { RewardProviderGameListContract } from '@/core/interface/contract-interfaces/service-reward/RewardProviderGameContract'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import RulesMixin from '@/plugin/mixins/RulesMixin'
import { Component, Inject, Mixins, Prop, VModel, Vue, Watch } from 'vue-property-decorator'
import RewardProvider from '../provider-selectors/RewardProvider'
import RewardProviderGame from './RewardProviderGame'

@Component
export default class RewardProviderGameSelector extends Mixins(RulesMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel() games!: RewardProviderGame[] | RewardProviderGame

  @Prop({ required: true }) domain!: DomainItemInterface
  @Prop({ required: true }) provider!: RewardProvider

  @Prop({ required: false, default: null }) rewardType!: string | null

  @Prop({ default: '' }) hint!: string
  @Prop({ default: false, type: Boolean }) multiple!: boolean
  @Prop({ default: false, type: Boolean }) disabled!: boolean
  @Prop({ default: false, type: Boolean }) dense!: boolean
  @Prop({ default: false, type: Boolean }) hideDetails!: boolean
  @Prop({ default: false, type: Boolean }) required!: boolean

  selectableGames: RewardProviderGame[] = []
  loading = false
  search = ''

  get label() {
    let suffix = ''
    if (this.required) {
      suffix = ' *'
    }
    return 'Select Game' + suffix
  }

  mounted() {
    this.doWork()
  }

  @Watch('domain')
  @Watch('provider')
  @Watch('rewardType')
  async doWork() {
    if (!this.domain) {
      return
    }
    this.loading = true

    let games: RewardProviderGameListContract | null = null
    if (!this.rewardType) {
      games = await this.apiClients.serviceReward.getProviderGames(this.domain, this.provider.toContract())
    } else {
      games = await this.apiClients.serviceReward.getProviderGamesByRewardType(this.domain, this.provider.toContract(), this.rewardType)
    }

    if (games !== null) {
      this.selectableGames = []
      this.selectableGames = games.map((g) => RewardProviderGame.fromContract(g))
    }

    this.loading = false
  }

  onChange(games: RewardProviderGame | RewardProviderGame[]) {
    this.search = ''
    this.$emit('change', games)
  }
}
</script>