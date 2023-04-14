<template>
  <v-card :flat="solo" :class="cardClass">
    <v-card-title v-if="displayCardTitle">
      <span class="title" v-text="title"></span>
    </v-card-title>
    <v-card-subtitle v-if="displayCardSubtitle">
      <span v-text="description"></span>
    </v-card-subtitle>
    <v-card-text>
      <v-row>
        <v-col cols="12">
          <div class="d-flex">
            <DomainSelectorSimple v-model="domain" @change="onChange" :roles="roles" :hint="hint" :unlocked="unlocked">
              <template #append-outer v-if="showReset">
                <v-tooltip top>
                  <template v-slot:activator="{ on, attrs }">
                    <div style="align-items: center; display: inline-flex; flex: 1 0 auto; justify-content: center; width: 24px; height: 24px">
                      <v-btn v-bind="attrs" v-on="on" icon color="primary" @click="reset()">
                        <v-icon>mdi-repeat</v-icon>
                      </v-btn>
                    </div>
                  </template>
                  <span v-text="description"></span>
                </v-tooltip>
              </template>
            </DomainSelectorSimple>
          </div>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script lang='ts'>
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Vue, Component, Prop, VModel, Inject } from 'vue-property-decorator'
import DomainSelectorSimple from './DomainSelectorSimple.vue'

@Component({
  components: {
    DomainSelectorSimple
  }
})
export default class DomainSelectorStacked extends Vue {
  @Inject('translateService') translateService!: TranslateServiceInterface

  @Prop({ type: Array, default: () => ['ADMIN'] }) roles!: string[]
  @Prop({ default: '' }) title!: string
  @Prop({ default: '' }) description!: string
  @Prop({ default: false, type: Boolean }) solo!: boolean
  @Prop({ default: false, type: Boolean }) unlocked!: boolean

  @VModel({ default: null }) domain!: DomainItemInterface | null

  get hint() {
    if (this.hasDomain && !this.unlocked) {
      return this.translateService.instant('UI_NETWORK_ADMIN.DOMAIN_SELECTOR.OUTPUT.SELECTED_DOMAIN_HINT')
    }
    return ''
  }

  get cardClass() {
    return {
      'px-2': this.solo
    }
  }

  get displayCardTitle(): boolean {
    return !!this.title
  }

  get displayCardSubtitle(): boolean {
    return !!this.description
  }

  get hasDomain(): boolean {
    return this.domain !== null
  }

  get showReset(): boolean {
    return !this.unlocked && this.hasDomain
  }

  onChange(domain: DomainItemInterface | null) {
    this.$emit('change', domain)
  }

  reset() {
    this.domain = null
  }
}
</script>

<style scoped>
</style>