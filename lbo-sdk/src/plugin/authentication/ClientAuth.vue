<template>
  <v-card flat class="pa-0 ma-0" :disabled="loading">
    <v-form @submit.prevent="login" v-model="form_valid" ref="form" data-test-id="frm-login">
      <v-text-field
        v-if="!disableDomain"
        v-model="domain"
        label="Domain"
        outlined
        append-icon="mdi-cloud"
        id="login-input-domain"
        data-test-id="txt-domain"
        :dense="dense"
      ></v-text-field>
      <v-text-field
        v-model="username"
        required
        :rules="rules_username"
        label="Username"
        outlined
        append-icon="mdi-email"
        id="login-input-username"
        data-test-id="txt-username"
        :dense="dense"
      ></v-text-field>
      <v-text-field
        v-model="password"
        required
        :rules="rules_password"
        label="Password"
        type="password"
        outlined
        append-icon="mdi-lock"
        id="login-input-password"
        data-test-id="txt-password"
        :dense="dense"
      ></v-text-field>

      <v-row no-gutters>
        <v-btn
          v-if="!disableForgotPassword"
          @click="forgotPassword"
          text
          color="#3c8dbc"
          class="text-capitalize"
          :loading="loading"
          small
          id="login-button-forgotpassword"
          data-test-id="btn-forgot-password"
          >Forgot Password?</v-btn
        >
        <v-spacer></v-spacer>
        <v-btn type="submit" color="#3c8dbc" dark min-width="150px" :loading="loading" id="login-button-signin">Sign In</v-btn>
      </v-row>
    </v-form>
  </v-card>
</template>

<script lang="ts">
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import StoreServiceInterface from '@/core/interface/service/StoreServiceInterface'
import AuthenticationStore from './meta/store'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import AuthenticationContract from '@/core/interface/contract-interfaces/service-oauth/AuthenticationContract'
import Cookies from 'js-cookie'

@Component
export default class ClientAuth extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('storeService') readonly storeService!: StoreServiceInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @Prop({ type: Boolean, default: false }) readonly disableDomain!: boolean
  @Prop({ type: Boolean, default: false }) readonly disableForgotPassword!: boolean
  @Prop({ type: Boolean, default: false }) readonly dense!: boolean

  domain = ''
  username = ''
  password = ''
  loading = false

  form_valid = false
  rules_username = [(v: string) => !!v || 'Username is required']
  rules_password = [(v: string) => !!v || 'Password is required']

  mounted() {
    this.storeService.add(AuthenticationStore)
    this.checkExistingSession()
  }

  async login() {
    ;(this.$refs.form as any).validate()

    if (!this.form_valid) {
      return
    }

    this.loading = true

    try {
      const response = await this.apiClients.serviceOauth.authenticate(this.domain.trim(), this.username.trim(), this.password)
      if (response === null) {
        this.processUnsuccessfulLogin()
        return
      }
      this.processSuccessfulLogin(response)
    } catch(e) {
      console.error('Caught an error during login')
      console.error(e)
      this.processUnsuccessfulLogin()
    }
  }

  async forgotPassword() {
    this.rootScope.provide.authentication.resetPassword()
  }

  checkExistingSession() {
    const access_token = localStorage.getItem('lithium-oauth-token')
    const refresh_token = localStorage.getItem('lithium-refresh-token')

    if (!!access_token && !!refresh_token) {
      // Previous session exists
      this.saveStore(access_token, refresh_token)
    }
  }

  processSuccessfulLogin({ access_token, refresh_token }: AuthenticationContract): void {
    this.saveCookie(access_token, refresh_token)
    this.saveStore(access_token, refresh_token)

    this.rootScope.provide.authentication.onSuccess(access_token, refresh_token)
  }

  saveCookie(access_token: string, refresh_token: string) {
    // Save cookie
    localStorage.setItem('lithium-oauth-token', access_token)
    localStorage.setItem('lithium-refresh-token', refresh_token)
  }

  saveStore(access_token: string, refresh_token: string) {
    // Save store
    const store = this.storeService.get(AuthenticationStore)
    if (store) {
      store.access_token = access_token
      store.refresh_token = refresh_token
    }
  }

  processUnsuccessfulLogin() {
    this.rootScope.provide.authentication.onFail()
    this.loading = false
  }
}
</script>

<style scoped>
.lithiumPrimary {
  background-color: #3c8dbc;
}
</style>