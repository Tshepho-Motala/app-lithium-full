<template>
  <div id="AxiosExecutor">
    <v-card outlined class="pa-4">
      <v-row no-gutters>
        <v-col cols="12">
          <v-text-field
            outlined
            :loading="loading"
            :disabled="loading"
            label="GET"
            v-model="get"
            append-outer-icon="mdi-arrow-right-thick"
            @click:append-outer="onGetClick"
          ></v-text-field>
        </v-col>
        <v-col cols="12">
          <span v-if="getResult === null" class="text-caption grey--text">No GET Result</span>
          <span v-text="getResult"></span>
        </v-col>
      </v-row>
    </v-card>
  </div>
</template>

<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { Vue, Component, Inject } from 'vue-property-decorator'

@Component
export default class AxiosExecutor extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  get = ''
  post = ''
  loading = false

  getResult: any = null
  postResult: any = null

  async onGetClick() {
    this.loading = true
    try {
      this.getResult = await this.apiClients.generic.get(this.get)
    } catch (e: any) {
      this.getResult = e.message
    }
    this.loading = false
  }
}
</script>

<style scoped>
</style>