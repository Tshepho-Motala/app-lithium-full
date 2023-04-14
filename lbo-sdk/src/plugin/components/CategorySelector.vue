<template>
  <v-select
    outlined
    :hint="hint"
    :persistent-hint="!!hint"
    label="Select Category"
    :items="selectableCategories"
    v-model="categories"
    item-text="name"
    return-object
    @change="onChange"
    hide-details
    :multiple="multiple"
    :chips="multiple"
    append-outer-icon="mdi-refresh"
    :disabled="localLoading || loading"
    :localLoading="localLoading || loading"
    @click:append-outer="onRefreshClick"
  ></v-select>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import Category from './Category'
import { Vue, Component, Inject, Prop, Watch, VModel } from 'vue-property-decorator'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'

@Component
export default class CategorySelector extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel() categories!: Category[] | Category

  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ default: '' }) readonly hint!: string
  @Prop({ default: false, type: Boolean }) readonly multiple!: boolean
  @Prop({ default: false, type: Boolean }) readonly loading!: boolean

  localLoading = false
  selectableCategories: Category[] = []

  mounted() {
    this.doWork()
  }

  @Watch('domain')
  async doWork() {
    if (!this.domain) {
      return
    }
    this.localLoading = true

    const categories = await this.apiClients.servicePromo.getProviderCategories(this.domain)
    if (categories !== null) {
      this.selectableCategories = categories.map((contract) => Category.fromContract(contract, this.domain))
    }

    this.localLoading = false
  }

  onChange(categories: Category[] | Category) {
    this.$emit('change', categories)
  }

  onRefreshClick() {
    this.doWork()
  }

  reset() {}
}
</script>

<style scoped>
</style>