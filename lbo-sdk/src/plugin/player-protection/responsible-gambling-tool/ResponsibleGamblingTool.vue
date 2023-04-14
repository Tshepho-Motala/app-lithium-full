<template>
  <v-row>
    <v-col cols="12">
      <v-progress-linear v-if="loading" indeterminate></v-progress-linear>
    </v-col>

    <v-col cols="12"> <h4> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.RESPONSIBLE_GAMBLING_TOOL.TITLE") }}</h4></v-col>
    <v-col cols="12">
      <v-simple-table dense>
        <template v-slot:default>
          <tbody>
          <tr>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.RESPONSIBLE_GAMBLING_TOOL.CURRENT_DEPOSIT_LIMIT") }}</td>
            <td>
              {{ responsibleGamblingTools.currentDepositLimit }}
            </td>
            <td>
              <a href="">{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.RESPONSIBLE_GAMBLING_TOOL.WEEKLY") }}</a>
            </td>
          </tr>
          <tr>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.RESPONSIBLE_GAMBLING_TOOL.MONTHLY_LOST_LIMIT") }}</td>
            <td>${{ responsibleGamblingTools.monthlyLossLimit }}</td>
            <td>
            </td>
          </tr>
          <tr>
            <td> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.RESPONSIBLE_GAMBLING_TOOL.MONTHLY_DEPOSIT_LIMIT") }}</td>
            <td>${{ responsibleGamblingTools.monthlyDepositLimit }}</td>
            <td>
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
import {ResponsibleGamblingTools} from '../PlayerProtection'
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component
export default class ResponsibleGamblingTool extends  Mixins(AssetTabMixin,TranslationMixin) {
  responsibleGamblingTools = new ResponsibleGamblingTools()

  mounted() {
    this.responsibleGamblingTools.getDetails()
  }
}
</script>

<style scoped>
</style>