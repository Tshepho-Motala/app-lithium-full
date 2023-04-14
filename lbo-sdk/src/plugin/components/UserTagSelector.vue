<template>
  <v-autocomplete
    outlined
    :hint="hint"
    :persistent-hint="!!hint"
    label="Select Tag"
    :items="selectableTags"
    v-model="tags"
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
  ></v-autocomplete>
</template>

<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { Vue, Component, Inject, Prop, VModel, Watch } from 'vue-property-decorator'
import { DomainItemInterface } from '../cms/models/DomainItem'
import UserTagContract from '@/core/interface/contract-interfaces/service-user/UserTagContract'
import UserTag from './UserTag'

@Component
export default class UserTagSelector extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel() tags!: UserTagContract[] | UserTagContract

  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ default: '' }) readonly hint!: string
  @Prop({ default: false, type: Boolean }) readonly multiple!: boolean
  @Prop({ default: false, type: Boolean }) readonly loading!: boolean

  localLoading = false
  selectableTags: UserTag[] = []

  mounted() {
    this.doWork()
  }

  @Watch('domain')
  async doWork() {
    if (!this.domain) {
      return
    }
    this.localLoading = true

    const tags = await this.apiClients.serviceUser.getTagsForDomain(this.domain)
    if (tags !== null) {
      this.selectableTags = tags.map((contract) => UserTag.fromContract(contract, this.domain))
    }

    this.localLoading = false
  }

  onChange(tags: UserTagContract[] | UserTagContract) {
    this.$emit('change', tags)
  }

  onRefreshClick() {
    this.doWork()
  }

  reset() {}
}
</script>

<style scoped>
</style>