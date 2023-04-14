<template>
  <div>
    <template>
      <!-- <v-hover v-slot="{ hover }"> -->
      <v-card id="AddChallenge"
              @click="onCardClick"
              width="200"
              style="border: 1px #ccc dashed">
        <v-card-text>
          <div class="px-8 d-flex flex-column justify-center align-center rounded">
            <div>
              <v-icon small>mdi-plus</v-icon>
              <v-icon large>mdi-flag-checkered</v-icon>
            </div>
            <div class="text-center">
              <span class="text-subtitle-2"> Add Challenge </span>
            </div>
          </div>
        </v-card-text>
      </v-card>
      <!-- </v-hover> -->
    </template>

    <v-dialog scrollable
              v-model="showAddDialog"
              max-width="1000"
              persistent>
      <ChallengeEdit v-model="challenge"
                     :domain="domain"
                     @save="onChallengeSave"
                     @cancel="showAddDialog = false" />
    </v-dialog>
  </div>
</template>

<script lang='ts'>
import { Vue, Component, Prop, VModel } from 'vue-property-decorator'
import ChallengeEdit from '@/plugin/promotions/challenge/ChallengeEdit.vue'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Challenge } from '../../challenge/Challenge'

@Component({
  components: {
    ChallengeEdit
  }
})
export default class StepAddChallenge extends Vue {
  @Prop({ required: true }) readonly promotionId!: number
  @Prop({ required: true }) readonly domain!: DomainItemInterface

  showAddDialog = false
  challenge: Challenge | null = null

  async onCardClick() {
    this.createNewChallenge()

    this.showAddDialog = true
  }

  async onChallengeSave() {
    if (this.challenge) {
      this.$emit('save', this.challenge)
    }
    this.showAddDialog = false
  }

  reset() {
    this.createNewChallenge()
  }

  createNewChallenge() {
    this.challenge = new Challenge()
  }
}
</script>

<style scoped></style>