<template>
  <v-card :flat="solo" class="px-4">
    <v-row class="v-row-reset" align="center">
      <v-col md="5">
        <h4 class="title text">{{ title }}</h4>
        <h5>{{ description }}</h5>
      </v-col>
      <v-col md="4">
        <p>{{ domainName }}</p>
      </v-col>
      <v-col md="3" class="d-flex justify-end">
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
      </v-col>
    </v-row>
  </v-card>
</template>

<script lang='ts'>
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Vue, Component, VModel, Prop, Inject } from 'vue-property-decorator'
import DomainSelectorSimple from './DomainSelectorSimple.vue'

@Component({
  components: {
    DomainSelectorSimple
  }
})
export default class DomainSelectorHorizontal extends Vue {
  @Inject('translateService') translateService!: TranslateServiceInterface

  @Prop({ type: Array, default: () => ['ADMIN'] }) roles!: string[]
  @Prop({ default: '' }) title!: string
  @Prop({ default: '' }) description!: string
  @Prop({ default: false, type: Boolean }) solo!: boolean
  @Prop({ default: false, type: Boolean }) unlocked!: boolean

  @VModel({ default: null }) domain!: DomainItemInterface | null

  get hasDomain(): boolean {
    return this.domain !== null
  }

  get domainName(): string {
    if (this.domain === null) {
      return ''
    }
    return this.domain.displayName
  }

  get hint() {
    if (this.hasDomain && !this.unlocked) {
      return this.translateService.instant('UI_NETWORK_ADMIN.DOMAIN_SELECTOR.OUTPUT.SELECTED_DOMAIN_HINT')
    }
    return ''
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