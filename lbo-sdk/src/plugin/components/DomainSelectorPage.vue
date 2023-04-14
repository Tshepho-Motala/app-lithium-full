<template>
  <v-card flat id="DomainSelectorPage">
    <v-card-title v-if="!invisible">
      <v-toolbar
        :short="!extended"
        :extended="extended && hasDomain"
        outlined
        rounded=""
        extension-height="60"
        :color="hasDomain ? 'primary' : ''"
        :dark="hasDomain"
        flat
        v-if="!hideToolbar"
      >
        <v-toolbar-title>
          <span v-text="title"></span>
        </v-toolbar-title>

        <v-spacer></v-spacer>

        <div>
          <span v-text="domainDisplay"></span>

          <v-tooltip v-if="hasDomain && !readonly" top v-model="showDomainTooltip">
            <template v-slot:activator="{ on, attrs }">
              <v-btn small icon :outlined="showDomainTooltip" class="mt-n1 ml-2" color="white" v-bind="attrs" v-on="on" @click="reset">
                <v-icon small>mdi-repeat</v-icon>
              </v-btn>
            </template>
            <span v-text="domainHint"></span>
          </v-tooltip>
        </div>

        <template #extension v-if="extended && hasDomain">
          <slot name="extension" />
        </template>
      </v-toolbar>
    </v-card-title>

    <template v-if="showDomainSelector">
      <v-card-text>
        {{ domain }}
        <DomainSelectorSimple v-model="domain" @change="onChange" :roles="roles" />
        <div class="px-4">
          <span v-text="description"></span>
        </div>
      </v-card-text>
    </template>

    <template v-if="hasDomain">
      <v-card-text>
        <slot> Template Here </slot>
      </v-card-text>
    </template>

    <v-divider v-if="showDivider"></v-divider>

    <template v-if="hasDomain">
      <v-card-actions>
        <slot name="actions"></slot>
      </v-card-actions>
    </template>
  </v-card>
</template>

<script lang='ts'>
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import { Vue, Component, Prop, Inject, VModel } from 'vue-property-decorator'
import DomainItem, { DomainItemInterface } from '../cms/models/DomainItem'
import DomainSelectorSimple from './domain-selector/DomainSelectorSimple.vue'

@Component({
  components: {
    DomainSelectorSimple
  }
})
export default class DomainSelectorPage extends Vue {
  @Inject('translateService') translateService!: TranslateServiceInterface

  @Prop({ default: '' }) readonly title!: string
  @Prop({ default: '' }) readonly description!: string
  @Prop({ type: Boolean, default: false }) readonly noGutters!: boolean
  @Prop({ type: Boolean, default: false }) readonly extended!: boolean
  @Prop({ type: Boolean, default: false }) readonly readonly!: boolean
  @Prop({ type: Boolean, default: false }) readonly showDivider!: boolean
  @Prop({ type: Boolean, default: false }) readonly hideToolbar!: boolean
  @Prop({ type: Boolean, default: false }) readonly invisible!: boolean
  @Prop({ type: Array, default: () => [] }) roles!: string[]

  @VModel({ default: null }) domain!: DomainItemInterface | null

  showDomainTooltip = false

  get showDomainSelector(): boolean {
    return !this.hasDomain
  }

  get hasDomain(): boolean {
    return this.domain !== null
  }

  get domainDisplay(): string {
    if (!this.domain) {
      return ''
    }
    return `${this.domain.displayName || ''} (${this.domain.name})`
  }

  get domainHint(): string {
    return this.translateService.instant('UI_NETWORK_ADMIN.DOMAIN_SELECTOR.OUTPUT.SELECTED_DOMAIN_HINT')
  }

  onChange(domain: DomainItem | null) {
    this.domain = domain
    this.$emit('change', domain)

    if (domain !== null) {
      this.showDomainTooltip = true
      setTimeout(() => {
        this.showDomainTooltip = false
      }, 2500)
    }
  }

  reset() {
    if (this.hasDomain) {
      this.domain = null
    }
  }
}
</script>

<style scoped>
</style>