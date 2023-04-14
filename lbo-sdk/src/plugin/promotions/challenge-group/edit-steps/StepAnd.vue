<template>
  <!-- <v-hover v-slot="{ hover }"> -->
    <v-card flat id="AddChallenge"  height="100%">
      <v-card-text style="height: 100%">
        <div class="d-flex justify-center align-center" style="height: 100%">
          <!-- <div>AND</div> -->
          <div>
            <v-icon>mdi-chevron-right</v-icon>
          </div>
        </div>
      </v-card-text>
    </v-card>
  <!-- </v-hover> -->
</template>

<script lang='ts'>
import { Vue, Component } from 'vue-property-decorator'

@Component
export default class StepAnd extends Vue {
  onCardClick() {}
}
</script>

<style scoped>
</style>