<template>
  <div style="height: 100%">
    <template v-if="loading">
      <v-card height="100%" style="border: 1px #ccc dashed">
        <div class="px-8 d-flex flex-column justify-center align-center rounded" style="height: 100%">
          <v-progress-circular indeterminate :color="color"></v-progress-circular>
        </div>
      </v-card>
    </template>

    <template v-if="!loading">
      <v-card id="AddReward" @click="onCardClick" height="100%" style="border: 1px #ccc dashed">
        <v-card-text style="height: 100%">
          <div class="px-8 d-flex flex-column justify-center align-center rounded" style="height: 100%">
            <div>
              <v-icon small>mdi-plus</v-icon>
              <v-icon large>mdi-seal</v-icon>
            </div>
            <div class="text-center">
              <span class="text-subtitle-2"> Add Reward </span>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </template>

    <v-dialog scrollable v-model="showAdd" max-width="1000" persistent>
      <RewardSelect :domain="domain" @save="onRewardSave" @cancel="showAdd = false" />
    </v-dialog>
  </div>
</template>

<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Vue, Component, Prop, Inject } from 'vue-property-decorator'
import Reward from '../../reward/Reward'
import RewardSelect from '../../reward/RewardSelect.vue'

@Component({
  components: {
    RewardSelect
  }
})
export default class StepAddReward extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  // @VModel({ required: true }) reward!: RewardFullDetailsContract | null

  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ default: '' }) readonly color!: string

  showAdd = false
  loading = false

  async onCardClick() {
    this.showAdd = true
  }

  async onRewardSave(reward: Reward) {
    this.loading = true
    this.showAdd = false
    const rewardResponse = await this.apiClients.serviceReward.create(this.domain, reward.toContract())
    if (rewardResponse !== null) {
      this.$emit('save', rewardResponse)
    }
    this.loading = false
  }
}
</script>

<style scoped>
</style>