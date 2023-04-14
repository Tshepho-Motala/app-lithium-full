<template>
<v-row>
    <v-col cols="6">
      <h4>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.CURRENT_TITLE") }}</h4>
      <v-simple-table dense>
        <template v-slot:default>
          <tbody>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.WAGERS_PLACED") }}</td>
            <td>
              {{ behaviourChanges.wagersPlaced }}
            </td>
          </tr>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.AVERAGE_STAKE_SIZE") }}</td>
            <td>${{ behaviourChanges.averageStakeSize }}</td>
          </tr>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.LOWEST_STAKE_SIZE") }}</td>
            <td>${{ behaviourChanges.lowestStakeSize }}</td>
          </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-col>


    <v-col cols="6">
      <h4>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.PREVIOUS_TITLE") }}</h4>
      <v-simple-table dense>
        <template v-slot:default>
          <tbody>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.WAGERS_PLACED") }}</td>
            <td>
              {{ behaviourChanges.wagersPlaced }}
            </td>
          </tr>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.AVERAGE_STAKE_SIZE") }}</td>
            <td>${{ behaviourChanges.averageStakeSize }}</td>
          </tr>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.BEHAVIOUR_CHANGE.LOWEST_STAKE_SIZE") }}</td>
            <td>${{ behaviourChanges.lowestStakeSize }}</td>
          </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Vue, Component, Mixins} from 'vue-property-decorator'
import {BehaviourChanges} from '../PlayerProtection'
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component
export default class BehaviourChange extends  Mixins(AssetTabMixin,TranslationMixin) {
  behaviourChanges = new BehaviourChanges()

  mounted() {
    this.behaviourChanges.getDetails()
  }
}
</script>

<style scoped>
</style>
