<template>
  <div>
    <v-card id="StepViewChallenge"
            width="200"
            :color="color">

      <v-card-text class="pb-0">

        <div class="d-flex flex-column align-end">
          <div>
            <v-btn dark
                   text
                   class="text-none"
                   small
                   @click="showDetails = !showDetails">
              Details
              <v-icon small
                      right
                      v-if="showDetails">mdi-eye-off-outline</v-icon>
              <v-icon small
                      right
                      v-else>mdi-eye-outline</v-icon>
            </v-btn>
          </div>
        </div>

        <div class="d-flex flex-column white pa-2">
          <div class="d-flex flex-row justify-space-around">
            <div>
              <span class="text-caption font-weight-bold"
                    v-text="challengeType"></span>
            </div>
          </div>
          <div class="d-flex flex-row justify-space-between">
            <div>
              <span class="text-caption">Value</span>
            </div>
            <div>
              <strong v-text="challenge.sharedValue"></strong>
            </div>
          </div>
          <div class="d-flex flex-row justify-space-between">
            <div>
              <span class="text-caption">Operation</span>
            </div>
            <div>
              <strong v-text="challenge.sharedOperation"></strong>
            </div>
          </div>
        </div>

        <v-divider v-if="showDetails"></v-divider>

        <!-- SHOW PROVIDERS -->
        <div class="pb-2 white"
             v-if="showDetails">
          <div class="d-flex flex-column"
               v-for="(rule, i) in challenge.rules"
               :key="`rule_${i}`">
            <div v-if="rule.promoProvider"
                 class="pb-2">
              <div style="background-color: #adcbee; line-height: normal;"
                   class="px-1 text-center">
                <span class="text-caption font-weight-bold"
                      v-text="rule.promoProvider.url"></span>
              </div>
              <div v-for="(val, ix) in rule.activityExtraFieldRuleValues"
                   :key="`val_${i}_${ix}`"
                   class="px-1 py-">
                <div>
                  <span class="text-caption"
                        v-text="val.activityExtraField.name"></span>
                </div>

                <div v-if="val.value.length === 0">
                  <span class="text-caption font-weight-bold">All</span>
                </div>
                <div v-for="(v, iy) in val.value"
                     :key="`val_${i}_${ix}_${iy}`">
                  <span class="text-caption font-weight-bold"
                        v-text="v"></span>
                </div>

                <v-divider class="mt-1"
                           v-if="ix < rule.activityExtraFieldRuleValues.length - 1"></v-divider>
              </div>
            </div>
          </div>
        </div>

        <v-divider ></v-divider>

        <div class="white pa-2">
          <span v-html="challenge.description"></span>
        </div>

      </v-card-text>

      <v-card-actions>
        <v-btn icon
               dark
               @click="onDelete">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn icon
               dark
               @click="onEdit">
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-dialog scrollable
              v-model="showEdit"
              max-width="1000"
              persistent>
      <v-card>
        <ChallengeEdit ref="challengeEdit"
                       v-model="challenge"
                       :domain="domain"
                       @save="showEdit = false"
                       @cancel="showEdit = false"
                       :sequenceNumber="sequenceNumber" />
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'
import { Challenge } from '../../challenge/Challenge'
import { Promotion } from '../../Promotion'
import ChallengeEdit from '@/plugin/promotions/challenge/ChallengeEdit.vue'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'

// TODO: Check this out: https://github.com/awtkns/vue-glow/blob/master/src/VueGlow.vue
@Component({
  components: {
    ChallengeEdit
  }
})
export default class StepViewChallenge extends Vue {
  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ required: true }) readonly sequenceNumber!: number

  @Prop({ required: true }) readonly promotion!: Promotion
  @Prop({ required: true }) readonly challenge!: Challenge

  showEdit = false
  showDetails = false

  get color() {
    return this.promotion.theme.color
  }

  get style() {
    return {
      border: `1px solid ${this.color}`
    }
  }

  get challengeType() {
    if (this.challenge.rules.length === 0) {
      return 'None'
    }
    return this.challenge.rules[0].activity.name
  }

  onEdit() {
    this.showEdit = true
    setTimeout(() => {
      if (this.$refs['challengeEdit']) {
        ; (this.$refs['challengeEdit'] as any).loadExisting()
      }
    }, 200)
  }

  onDelete() {
    if (!this.promotion) {
      return
    }
    const index = this.promotion.challenges.findIndex((x) => x.description === this.challenge.description)
    if (index > -1) {
      this.$delete(this.promotion.challenges, index)
    }
  }
}
</script>

<style scoped></style>