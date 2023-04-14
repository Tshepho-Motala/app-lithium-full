<template>
  <v-select
    outlined
    :hint="hint"
    :hide-details="!hint"
    :persistent-hint="!!hint"
    label="Select Reward Provider"
    :items="selectableProviders"
    v-model="providers"
    item-text="name"
    return-object
    @change="onChange"
    :multiple="multiple"
    :chips="multiple"
    deletable-chips
    :small-chips="multiple || undefined"
    :disabled="disabled"
  ></v-select>
</template>

<script lang='ts'>
import AxiosApiClients, { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Vue, Component, Inject, Prop, Watch, VModel } from 'vue-property-decorator'
import RewardProvider from '../provider-selectors/RewardProvider'

@Component
export default class RewardProviderSelect extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel() providers!: RewardProvider[] | RewardProvider

  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ default: '' }) hint!: string
  @Prop({ default: false, type: Boolean }) multiple!: boolean
  @Prop({ default: false, type: Boolean }) disabled!: boolean

  loading = false
  selectableProviders: RewardProvider[] = []

  mounted() {
    this.doWork()
  }

  @Watch('domain')
  async doWork() {
    this.loading = true

    const providers = await this.apiClients.serviceReward.getProviders(this.domain)
    if (!providers) {
      return
    }

    this.selectableProviders = providers.map((p) => RewardProvider.fromContract(p, this.domain))

    this.loading = false
  }

  onChange(providers: RewardProvider | RewardProvider[]) {
    this.$emit('change', providers)
  }

  reset() {
    // this.localProvider = null
  }
}
</script>

<style scoped>
</style>