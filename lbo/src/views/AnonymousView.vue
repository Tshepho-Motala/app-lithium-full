<template>
  <v-app>
      <v-container>
          <span class="text-title">Anonymous View</span>
        <router-view></router-view>
      </v-container>
  </v-app>
</template>

<script lang='ts'>
import { Vue, Component } from 'vue-property-decorator'

@Component
export default class AnonymousView extends Vue {}
</script>

<style scoped>
</style>
