<template>
  <v-row>
    <template v-if="domain !== null">
      <v-col cols="3" class="shrink" v-for="i of amountOfRewardTypes" :key="`type_${i}`">
        <v-card class="pa-3" :disabled="disableAddAnotherRewardType" outlined>
          <RewardTypeSelect ref="rt" v-model="rewardTypes[i - 1]" :providers="providers" :domain="domain" />
        </v-card>
      </v-col>
    </template>
  </v-row>
</template>

<script lang='ts'>
import RewardTypeSelect from './RewardTypeSelect.vue'
import { Vue, Component, Prop, VModel } from 'vue-property-decorator'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import GameProvider from '@/plugin/cms/models/GameProvider'
import RewardType from './RewardType'

@Component({
  components: {
    RewardTypeSelect
  }
})
export default class RewardTypeMultiSelect extends Vue {
  @VModel({ type: Array }) rewardTypes!: RewardType[]
  @Prop({ required: true }) readonly providers!: GameProvider[]
  @Prop({ required: true }) readonly domain!: DomainItemInterface

  get canAddAnotherRewardType(): boolean {
    return true
  }

  get disableAddAnotherRewardType(): boolean {
    return false
  }

  get amountOfRewardTypes(): number {
    return this.rewardTypes.length + 1
  }

  validate() {
    const refs: any[] = this.$refs['rt'] as any[]
    const valids: boolean[] = []

    for (const ref of refs) {
      const valid: boolean = ref.validate()
      valids.push(valid)
    }
    return valids.every(x => x === true)
  }
}
</script>

<style scoped>
</style>