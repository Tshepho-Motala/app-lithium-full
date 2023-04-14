<template>
  <v-row>
    <v-col cols="12">
      <v-progress-linear v-if="loading" indeterminate></v-progress-linear>
    </v-col>
    <v-col cols="12"> <h4> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.SESSION_HISTORY.TITLE") }}</h4></v-col>
    <v-col cols="12">
      <v-simple-table dense>
        <template v-slot:default>
          <tbody>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.SESSION_HISTORY.SUCCESSFUL_LOGINS") }}</td>
            <td>{{ sessionHistory.amountOfSuccessfulLogins }}</td>
            <td>
              <v-btn
                  @click="doWork"
                  x-small
              >
                {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.SESSION_HISTORY.SUCCESSFUL_LOGINS_DETAILS") }}
              </v-btn>
            </td>
          </tr>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.SESSION_HISTORY.UNSUCCESSFUL_LOGINS") }}</td>
            <td>{{ sessionHistory.amountOfUnsuccessfulLogins }}</td>
            <td>
              <v-btn
                  @click="doWork"
                  x-small
              >
                {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.SESSION_HISTORY.UNSUCCESSFUL_LOGINS_DETAILS") }}
              </v-btn>
            </td>
          </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Vue, Component, Mixins} from 'vue-property-decorator'
import { SessionHistory } from '../PlayerProtection'
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component
export default class SessionHistroy extends  Mixins(AssetTabMixin,TranslationMixin) {
  loading = false
  sessionHistory = new SessionHistory()

  mounted() {
    // this.doWork()
  }

  async doWork() {
    this.loading = true
    await this.sessionHistory.getSuccessfulLogins('adiaudhiua')
    await this.sessionHistory.getUnsuccessfulLogins('adiaudhiua')
    this.loading = false
  }
}
</script>

<style scoped>
</style>