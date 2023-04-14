<template>
  <div>
    <v-btn @click="onClick" :loading="loading">
      <v-icon left>mdi-text-box-plus</v-icon>
      Click to generate CSV
    </v-btn>

    <v-dialog max-width="500" persistent v-model="showDialog">
      <v-card>
        <v-card-title> CSV Generation Started </v-card-title>

        <v-card-text> Please check on the MyDocuments page for progres </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn icon @click="showDialog = false">
            <v-icon>mdi-check</v-icon>
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Inject, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'

@Component
export default class ClientAuth extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface

  loading = false
  showDialog = false

  async onClick() {
    this.loading = true
    await this.rootScope.provide.documentGeneration.generateCsv()
    this.loading = false
    this.showDialog = true
  }
}
</script>

<style>
</style>