<template>

      <v-card id="StepViewReward" height="100%" outlined :loading="loading">
        <v-toolbar>
          <v-toolbar-title>
            <div class="px-3 pt-3 d-flex flex-row justify-start">
              <v-icon large left :color="color">mdi-flag-checkered</v-icon>
              <span class="text-h6 d-inline-block text-truncate" style="max-width: 180px" v-text="name"></span>
            </div>
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-menu top :close-on-content-click="false">
            <template v-slot:activator="{ on, attrs }">
                <v-btn icon v-bind="attrs" v-on="on">
                <v-icon>mdi-dots-vertical</v-icon>
              </v-btn>
            </template>
            <v-card>
              <v-card-actions>
                <v-btn icon @click="resetReward">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-menu>
        </v-toolbar>
        <v-card-text class="px-2">
          <!-- <div class="px-3 pt-3 d-flex flex-row justify-start">
            <v-icon large left :color="color">mdi-flag-checkered</v-icon>
            <span class="text-h6 d-inline-block text-truncate" style="max-width: 180px" v-text="name"></span>
          </div> -->
          <div class="pt-4 ma-0">
            <v-list dense class="ma-0 pa-0">
              <v-list-item dense v-for="(item, i) in displayList" :key="`item_${i}`">
                <v-list-item-content>
                  <v-list-item-title v-html="item.name"></v-list-item-title>
                  <v-list-item-subtitle v-html="item.value"></v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action v-if="item.copy">
                  <v-tooltip top>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn v-bind="attrs" v-on="on" icon small @click="writeToClipboard(item.value)">
                        <v-icon small>mdi-content-copy</v-icon>
                      </v-btn>
                    </template>
                    <span>Copy to clipboard</span>
                  </v-tooltip>
                </v-list-item-action>
              </v-list-item>
            </v-list>
          </div>

          <div class="pt-4 pb-2 pl-0">
            <span class="text-subtitle-2">Reward Components</span>
          </div>
          <v-expansion-panels>
            <v-expansion-panel v-for="(comp, i) in components" :key="`comp_${i}`">
              <v-expansion-panel-header>
                {{ comp[1].value }}
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-list dense class="ma-0 pa-0">
                  <v-list-item dense v-for="(item, i) in comp" :key="`item_${i}`">
                    <v-list-item-content>
                      <v-list-item-title v-html="item.name"></v-list-item-title>
                      <v-list-item-subtitle v-html="item.value"></v-list-item-subtitle>
                    </v-list-item-content>
                  </v-list-item>
                </v-list>
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>          
        </v-card-text>
      </v-card>
    </template>

  <!-- </v-menu> -->


<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { PromotionRewardReference } from '@/core/interface/contract-interfaces/service-promo/PromotionContract'
import { RewardFullDetailsContract } from '@/core/interface/contract-interfaces/service-reward/RewardContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Vue, Component, Prop, Inject, Watch } from 'vue-property-decorator'

// TODO: Check this out: https://github.com/awtkns/vue-glow/blob/master/src/VueGlow.vue
@Component
export default class StepViewReward extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @Prop({ required: true }) readonly domain!: DomainItemInterface

  @Prop({ default: null }) readonly reward!: RewardFullDetailsContract | null
  @Prop({ default: null }) readonly reference!: PromotionRewardReference | null
  @Prop({ default: '' }) readonly color!: string

  rewardReference: RewardFullDetailsContract | null = null

  loading = false

  granularityList = [
    {
      label: 'Day',
      value: 3
    },
    {
      label: 'Week',
      value: 4
    },
    {
      label: 'Month',
      value: 2
    },
    {
      label: 'Year',
      value: 1
    },
    {
      label: 'Total',
      value: 5
    }
  ]

  get style() {
    return {
      border: `1px solid ${this.color}`
    }
  }

  get name(): string {
    if (!this.rewardReference) {
      return ''
    }
    return this.rewardReference.current.name
  }

  // get displayList(): { [key: string]: string } {
  get displayList(): any[] {
    if (!this.rewardReference) {
      return []
    }

    const granularity = this.granularityList.find((x) => x.value === this.rewardReference!.current.validForGranularity)
    return [
      {
        name: 'Code',
        value: this.rewardReference.current.code,
        copy: true
      },
      {
        name: 'Description',
        value: this.rewardReference.current.description
      },
      {
        name: 'Valid',
        value: this.rewardReference.current.validFor + ' ' + granularity?.label || ''
      }
    ]
  }

  get chips(): { name: string }[] {
    const chips: { name: string }[] = []

    if (!this.rewardReference) {
      return chips
    }

    for (const rType of this.rewardReference.current.revisionTypes) {
      if (!rType.rewardType) {
        continue
      }
      chips.push({
        name: rType.rewardType.name
      })
    }

    return chips
  }

  get components() {
    if (!this.rewardReference) {
      return []
    }

    return this.rewardReference.current.revisionTypes.map((x) => {
      return [
        {
          name: 'ID',
          value: x.rewardType.id
        },
        {
          name: 'Name',
          value: x.rewardType.name
        },
        {
          name: 'URL',
          value: x.rewardType.url
        },
        {
          name: 'Instant',
          value: x.instant || 'false'
        },
        {
          name: 'Code',
          value: x.rewardType.code
        }
      ]
    })
  }

  mounted() {
    if (this.reference !== null) {
      this.getLiveReward()
    } else if (this.reward !== null) {
      this.rewardReference = this.reward
    }
  }

  @Watch('reference')
  async getLiveReward() {
    if (!this.reference) {
      return
    }

    this.loading = true
    const id = this.reference.rewardId || this.reference.id || 0
    this.rewardReference = await this.apiClients.serviceReward.getRewardById(this.domain, id.toString())
    this.loading = false
  }

  async writeToClipboard(text: string) {
    await navigator.clipboard.writeText(text)
  }
  resetReward(){
    this.$emit('delete')
  }
}
</script>

<style scoped>
</style>