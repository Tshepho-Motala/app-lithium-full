<template>
  <v-row id="ClientUser" class="text-center" no-gutters>
    <v-col cols="12">
      <span>Authenticated</span>
    </v-col>
    <v-col cols="12">
      <span v-text="username"></span>
    </v-col>
    <v-col cols="12">
      <strong>
        <span v-text="fullName"></span>
      </strong>
    </v-col>
    <v-col cols="12">
      <span class="text-caption" v-text="email"></span>
    </v-col>
    <v-col cols="12">
      <v-btn @click="logout">Logout</v-btn>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import StoreServiceInterface from '@/core/interface/service/StoreServiceInterface'
import Cookies from 'js-cookie'
import { Vue, Component, Inject } from 'vue-property-decorator'
import AuthenticationStore from './meta/store'

@Component
export default class ClientUser extends Vue {
  @Inject('storeService') readonly storeService!: StoreServiceInterface

  store: AuthenticationStore | null = null

  get username(): string {
    return ''
    // if (!this.store || !this.store.authenticationContract) {
    //   return ''
    // }
    // return this.store.authenticationContract.username
  }

  get fullName(): string {
    return ''
    // if (!this.store || !this.store.authenticationContract) {
    //   return ''
    // }
    // return this.store.authenticationContract.firstName + ' ' + this.store.authenticationContract.lastName
  }

  get email(): string {
    return ''
    // if (!this.store || !this.store.authenticationContract) {
    //   return ''
    // }
    // return this.store.authenticationContract.email
  }

  mounted() {
    this.store = this.storeService.get(AuthenticationStore)
  }

  logout() {
    localStorage.removeItem('lithium-oauth-token')
    localStorage.removeItem('lithium-refresh-token')

    if (this.store === null) {
      return
    }
    this.store.clear()
  }
}
</script>

<style scoped>
</style>