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
export default class FormTextField extends Vue {
    key_generic = "form-text-field"
    key_specific: string | null = null

    get key() {
        return this.key_specific || this.key_generic
    }

    get hasPermission() {
        // return this.permissionModule.whitelist.contains(x => x === this.key)
        return true
    }

    mounted() {

        // Check input binding

        // Check outpout binding 

        // Validate functional requirements
    }
}
</script>

<style scoped>
</style>
