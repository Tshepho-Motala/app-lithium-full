<template>
  <v-row>
    <v-col cols="12">
      <v-progress-linear v-if="loading" indeterminate></v-progress-linear>
    </v-col>

    <v-col cols="12"> <h4> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.WAGER_HISTORY.TITLE") }}</h4></v-col>
    <v-col cols="12">
      <v-simple-table dense>
        <template v-slot:default>
          <tbody>
          <tr>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.WAGER_HISTORY.LAST_TWENTY_FOUR") }}</td>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.WAGER_HISTORY.LAST_TWENTY_FOUR_SPORT") }}</td>
            <td>
              {{ wagerHistoryClass.countPlacedLastHours }}
            </td>
          </tr>
          <tr>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.WAGER_HISTORY.LAST_MONTH") }}</td>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.WAGER_HISTORY.LAST_MONTH_SPORT") }}</td>
            <td>{{ wagerHistoryClass.countPlacedLastDays }}</td>
          </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Vue, Component, Mixins} from 'vue-property-decorator'
import { WagerHistoryClass } from '../PlayerProtection'
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import TranslationMixin from "@/core/mixins/translationMixin";


@Component
export default class WagerHistory extends  Mixins(AssetTabMixin,TranslationMixin) {
   wagerHistoryClass = new WagerHistoryClass()

  mounted() {
    this.wagerHistoryClass.getDetails()
  }
}
</script>

<style scoped>
</style>