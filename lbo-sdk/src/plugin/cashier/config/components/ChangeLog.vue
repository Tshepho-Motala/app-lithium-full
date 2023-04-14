<template>
    <div class="d-flex align-start changelog-sign" data-test-id="change-log">
      <div v-if="log.type === 'create'" class="d-flex align-center justify-center pa-2 mx-3 light-blue border-radius-50">
        <v-icon >
          mdi-plus
        </v-icon>
      </div>
      <div v-if="log.type === 'edit'" class="d-flex align-center justify-center pa-2 mx-3 orange lighten-2 border-radius-50">
        <v-icon >
          mdi-pencil
        </v-icon>
      </div>
      <div v-if="log.type === 'enable'" class="d-flex align-center justify-center pa-2 mx-3 green darken-1 border-radius-50">
        <v-icon >
          mdi-check
        </v-icon>
      </div>
      <div v-if="log.type === 'archived'" class="d-flex align-center justify-center pa-2 mx-3 blue-grey lighten-1 border-radius-50">
        <v-icon >
          mdi-archive
        </v-icon>
      </div>
      <div  v-if="log.type === 'disable'" class="d-flex align-center justify-center pa-2 mx-3 deep-orange lighten-4 border-radius-50">
        <v-icon>
          mdi-cancel
        </v-icon>
      </div>
      <div v-if="log.type === 'delete'" class="d-flex align-center justify-center pa-2 mx-3 red darken-1 border-radius-50">
        <v-icon >
          mdi-delete
        </v-icon>
      </div>
      <div v-if="log.type === 'unarchived'" class="d-flex align-center justify-center pa-2 mx-3 light-blue border-radius-50">
        <v-icon >
          mdi-delete
        </v-icon>
      </div>
      <div v-if="log.type === 'markbonuscurrent'" class="d-flex align-center justify-center pa-2 mx-3 purple lighten-3 border-radius-50">
        <v-icon >
          mdi-swap-horizontal
        </v-icon>
      </div>
      <div v-if="log.type === 'copybonus'" class="d-flex align-center justify-center pa-2 mx-3 purple lighten-3 border-radius-50">
        <v-icon >
          mdi-content-copy
        </v-icon>
      </div>
      <div v-if="log.type === 'comment'" class="d-flex align-center justify-center pa-2 mx-3 purple lighten-3 border-radius-50">
        <v-icon >
          mdi-message-reply-text
        </v-icon>
      </div>
      <div class="d-flex flex-column mb-2 pa-2 changelog-line">
        <div class="d-flex  justify-space-between">
          <p>
            <span v-if="log.type === 'create'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.CREATEDBY')}}</span>
            <span v-if="log.type === 'edit'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.UPDATEDBY')}}</span>
            <span v-if="log.type === 'enable'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.ENABLEDBY')}}</span>
            <span v-if="log.type === 'disable'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.DISABLEDBY')}}</span>
            <span v-if="log.type === 'delete'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.DELETEDBY')}}</span>
            <span v-if="log.type === 'comment'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.COMMENTBY')}}</span>
            <span v-if="log.type === 'copybonus'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.COPYBONUSBY')}}</span>
            <span v-if="log.type === 'markbonuscurrent'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.MARKBONUSCURRENTBY')}}</span>
            {{ log.authorFullName }}
          </p>
          <div class="d-flex">
            <v-icon>
              mdi-clock-outline
            </v-icon>
            <p class="pl-2">
              {{timestampToDate(log.changeDate)}}
            </p>
          </div>
        </div>
        <v-divider />
        <div class="mb-3">
          <span v-if="log.type === 'create'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.CREATEDBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          <span v-if="log.type === 'edit'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.UPDATEDBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          <span v-if="log.type === 'enable'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.ENABLEDBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          <span v-if="log.type === 'disable'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.DISABLEDBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          <span v-if="log.type === 'delete'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.DELETEDBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          <span v-if="log.type === 'comment'">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.COMMENTBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          <span v-if="log.type === 'copybonus'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.COPYBONUSBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          <span v-if="log.type === 'markbonuscurrent'">{{$translate('UI_NETWORK_ADMIN.CHANGELOG.HEADER.MARKBONUSCURRENTBY')}} on {{timestampToDate(log.changeDate)}} by {{log.authorFullName}}</span>
          {{log.authorFullName}}
          <div v-if="log.changes">
            <p v-for="(fieldChange, index) in log.changes" :key="index" class="overflow-break-word">
              <span v-if="!log.type.includes('bonus') && !fieldChange.fromValue && !fieldChange.toValue">{{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FIELD')}} {{fieldChange.field}} {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.NOVALUE') }}  </span>
              <span v-if="!log.type.includes('bonus') && fieldChange.fromValue && fieldChange.toValue">{{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FIELD')}} {{fieldChange.field}} {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FROMTOVALUE') }}  {{fieldChange.fromValue}} to {{fieldChange.toValue}} </span>
              <span v-if="!log.type.includes('bonus') && !fieldChange.fromValue  && fieldChange.toValue">{{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FIELD')}} {{fieldChange.field}} {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.TOVALUE') }}   {{fieldChange.toValue}}</span>
              <span v-if="!log.type.includes('bonus') && fieldChange.fromValue && !fieldChange.toValue">{{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FIELD')}} {{fieldChange.field}} {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FROMVALUE') }}  {{fieldChange.fromValue}}</span>
              <span v-if="(log.entity === 'bonus') && (log.type === 'copybonus')">{{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FIELD')}} {{fieldChange.field}}. {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.COPYBONUS_START') }} {{fieldChange.fromValue}} {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.COPYBONUS_END') }} {{fieldChange.toValue}}</span>
              <span v-if="(log.entity === 'bonus') && (log.type === 'markbonuscurrent')">{{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.FIELD')}} {{fieldChange.field}}. {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.COPYBONUS_START')}} {{fieldChange.toValue}} from {{ ((fieldChange.fromValue === '')?'N/A':fieldChange.fromValue) }} {{ $translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOGS.CHANGE.MARKBONUSCURRENT')}}{{fieldChange.toValue}})</span>
              <span v-if="!fieldChange.message">{{ fieldChange.message }}</span>
            </p>
          </div>
          <span v-if="!log.comments"><b>{{ log.comments }}</b></span>
        </div>
        <p>{{log.longDescr}}</p>
      </div>
    </div>
</template>

<script lang="ts">
import {Component, Prop, Mixins} from "vue-property-decorator";
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import {CashierConfigChangelog} from "@/core/interface/cashierConfig/CashierConfigInterface";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component
export default class ChangeLog extends Mixins( AssetTabMixin, TranslationMixin) {
  @Prop() log!: CashierConfigChangelog
}
</script>

<style scoped>

</style>