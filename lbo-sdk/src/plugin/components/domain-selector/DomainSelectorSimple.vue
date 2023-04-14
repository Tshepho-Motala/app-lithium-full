<template>
  <v-autocomplete
    :class="classFields"
    :placeholder="placeholder"
    :readonly="readonly"
    :persistent-hint="disabled"
    :hint="hint"
    autofocus
    outlined
    v-model="domain"
    @change="onChange"
    :items="domains"
    item-value="name"
    item-text="name"
    return-object
    :append-outer-icon="outerIcon"
    @click:outer-icon="onAppendOuterIconClick"
  >
    <template #append-outer>
      <slot name="append-outer" />
    </template>
  </v-autocomplete>
</template>

<script lang='ts'>
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Vue, Component, Inject, VModel, Prop } from 'vue-property-decorator'

@Component
export default class DomainSelectorSimple extends Vue {
  @Inject('translateService') translateService!: TranslateServiceInterface
  @Inject('userService') userService!: UserServiceInterface

  @Prop({ type: Array, default: () => ['ADMIN'] }) roles!: string[]
  @Prop({ type: Boolean, default: false }) showReset!: boolean
  @Prop({ type: Boolean, default: false }) unlocked!: boolean
  @Prop({ type: String, default: '' }) hint!: string

  @VModel({ default: null }) domain!: DomainItemInterface | null

  domains: DomainItemInterface[] = []

  get classFields() {
    return {
      'pt-4': true,
      'v-input--is-disabled': this.disabled && !this.unlocked
    }
  }

  get placeholder() {
    return this.translateService.instant('UI_NETWORK_ADMIN.DOMAIN_SELECTOR.OUTPUT.SELECT_DOMAIN')
  }

  get disabled() {
    return this.domain !== null
  }

  get readonly() {
    return this.disabled && !this.unlocked
  }

  get outerIcon(): string | undefined {
    if (this.showReset) {
      return 'mdi-repeat'
    }
    return undefined
  }

  mounted() {
    this.domains = this.userService.domainsWithAnyRole(this.roles)
  }

  onChange(domain: DomainItemInterface | null) {
    this.$emit('change', domain)
  }

  onAppendOuterIconClick() {
    this.reset()
  }

  reset() {
    this.domain = null
  }
}
</script>

<style scoped>
</style>