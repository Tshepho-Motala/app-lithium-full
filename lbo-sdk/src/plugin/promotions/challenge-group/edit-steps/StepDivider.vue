<template>
  <v-hover v-slot="{ hover }">
    <v-card :flat="!hover" id="AddChallenge" @click="onCardClick" height="100%" min-width="40px">
      <v-card-text style="height: 100%">
        
        <div class="d-flex justify-center align-center" style="height: 100%">
          <div class="grow">
            <v-divider></v-divider>
          </div>
          <!-- <div class="grow">
            <v-divider></v-divider>
          </div>
          <div class="shrink">
            <v-icon large>
              mdi-plus
            </v-icon>
          </div>
          <div class="grow">
            <v-divider></v-divider>
          </div> -->
        </div>
      </v-card-text>
    </v-card>
  </v-hover>
</template>

<script lang='ts'>
import { Vue, Component, VModel } from 'vue-property-decorator'
import { Promotion } from '../../Promotion'

@Component
export default class StepDivider extends Vue {
  @VModel({ required: true, type: Promotion }) readonly promotion!: Promotion | null

  onCardClick() {}
}
</script>

<style scoped>
</style>