<template>
  <div id="RewardPaths">
    <div class="d-flex flex-column"
         style="gap: 16px">
      <template v-for="(groupId, g) in groups">
        <div :key="`challenge_list_${g}`">
          <v-sheet>
            <div>
              <span class="text-subtitle-2">
                Challenge Path <strong>{{ g + 1 }}</strong>
              </span>
            </div>
            <v-slide-group show-arrows
                           v-model="slideGroup"
                           v-if="!!promotion">
              <template v-for="(challenge, i) in promotion.challenges">
                <v-slide-item :key="`challenge_${i}`">
                  <template v-if="challenge.groupId === groupId">
                    <div>
                      <div class="d-flex">
                        <div>
                          <StepViewChallenge :sequenceNumber="challenge.sequenceNumber || i"
                                             :domain="promotion.domain"
                                             :challenge="challenge"
                                             :promotion="promotion" />
                        </div>
                        <div v-if="i < promotion.challengeAmount">
                          <StepAnd />
                        </div>
                      </div>
                    </div>
                  </template>
                </v-slide-item>
              </template>

              <v-slide-item>
                <StepAddChallenge @save="(c) => onSave(c, groupId)"
                                  :promotionId="promotion.id"
                                  :domain="promotion.domain"
                                  style="align-self: center" />
              </v-slide-item>
            </v-slide-group>
          </v-sheet>
        </div>
      </template>
    </div>
    <div class="py-4">
      <v-btn block
             color="success"
             outlined
             @click="onAddNewPath">Add new path</v-btn>
    </div>
  </div>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { nanoid } from 'nanoid'
import { VModel, Inject, Component, Vue } from 'vue-property-decorator'
import { Challenge } from '../challenge/Challenge'
import { Promotion } from '../Promotion'
import StepAddChallenge from './edit-steps/StepAddChallenge.vue'
import StepAnd from './edit-steps/StepAnd.vue'
import StepDivider from './edit-steps/StepDivider.vue'
import StepViewChallenge from './edit-steps/StepViewChallenge.vue'
import draggable from 'vuedraggable'

@Component({
  components: {
    StepAddChallenge,
    StepViewChallenge,
    StepDivider,
    StepAnd,
    draggable
  }
})
export default class ChallengeGroupEdit extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface

  @VModel({ required: true, type: Promotion }) readonly promotion!: Promotion | null

  groups: string[] = []
  loading = false
  slideGroup = null

  mounted() {
    this.groupPromotionChallenges()
  }

  groupPromotionChallenges() {
    if (this.promotion === null) {
      return
    }

    const items = this.promotion.challenges.filter((x) => x.groupId !== null).map((x) => x.groupId || '')
    this.groups = Array.from(new Set(items))
    this.ensureFirstGroup()
  }

  ensureFirstGroup() {
    if (this.groups.length === 0) {
      this.groups.push(nanoid())
    }
  }

  challengesByGroupId(groupId: string) {
    if (this.promotion === null) {
      return
    }

    return this.promotion.challenges.filter((x) => !!x.groupId && x.groupId === groupId) || []
  }

  onAddNewPath() {
    this.groups.push(nanoid())
  }

  getLatestSequenceNumberInGroup(groupId: string) {
    if (!this.promotion) {
      return 0
    }
    const challengesInGroup = this.promotion.challenges.filter(c => c.groupId === groupId) || null
    if (!challengesInGroup) {
      return 0
    }
    return challengesInGroup.length
  }

  onSave(challenge: Challenge, groupId: string) {

    challenge.groupId = groupId
    challenge.sequenceNumber = this.getLatestSequenceNumberInGroup(groupId)

    if (!this.promotion) {
      return
    }

    this.promotion.challenges.push(challenge)
  }

  challengeByGroup(groupId: string) {
    if (!this.promotion) {
      return
    }
    return this.promotion.challenges.filter((x) => x.groupId === groupId)
  }
}
</script>

<style scoped></style>