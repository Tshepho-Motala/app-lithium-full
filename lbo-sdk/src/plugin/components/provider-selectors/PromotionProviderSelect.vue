<template>
  <v-select
    outlined
    :hint="hint"
    :hide-details="!hint"
    :persistent-hint="!!hint"
    label="Select Promotion Provider"
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
import Category from '../Category'
import PromotionProvider from '../provider-selectors/PromotionProvider'

@Component
export default class PromotionProviderSelect extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel() providers!: PromotionProvider[] | PromotionProvider

  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ default: '' }) readonly category!: Category
  @Prop({ default: '' }) hint!: string
  @Prop({ default: false, type: Boolean }) multiple!: boolean
  @Prop({ default: false, type: Boolean }) disabled!: boolean

  loading = false
  selectableProviders: PromotionProvider[] = []

  mounted() {
    this.doWork()
  }

  @Watch('domain')
  @Watch('category')
  async doWork() {
    this.loading = true

    if (!this.category) {
      const providers = await this.apiClients.servicePromo.getProviders(this.domain)
      if (!providers) {
        return
      }

      this.selectableProviders = providers.map((p) => PromotionProvider.fromContract(p, this.domain))
    } else {
      const providers = await this.apiClients.servicePromo.getProvidersByCategory(this.domain, this.category.toContract())
      if (!providers) {
        return
      }

      this.selectableProviders = providers.map((p) => PromotionProvider.fromContract(p, this.domain))
    }

    this.loading = false
  }

  onChange(providers: PromotionProvider | PromotionProvider[]) {
    this.$emit('change', providers)
  }

  reset() {
    // this.localProvider = null
  }
}
</script>

<style scoped>
</style>