<template>
  <v-row>
    <v-col cols="12"> <h4> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BIG_WIN.TITLE") }}</h4></v-col>
    <v-col cols="12">
      <v-simple-table dense>
        <template v-slot:default>
          <tbody>
          <tr>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BIG_WIN.LAST_BIG_WIN") }}</td>
            <td>{{ bigWins.lblDateOfLastBigWin }}</td>
          </tr>
          <tr>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BIG_WIN.BIG_WIN_AMOUNT") }}</td>
            <td>{{ bigWins.bigWinAmount }}</td>
          </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Vue, Component, Mixins} from 'vue-property-decorator'
import { BigWins } from '../PlayerProtection'
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component
export default class BigWin extends  Mixins(AssetTabMixin,TranslationMixin) {
  bigWins: BigWins = new BigWins()

  mounted() {
    this.bigWins.getDetails()
  }
}
</script>

<style scoped>
</style>