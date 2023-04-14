<template>
  <div>
    <v-row no-gutters class="mt-4 mb-4">
      <v-col
          cols="6"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.minimumLimit" :currency="currency"
                                    :property-name="'minimumLimit'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
      <v-col
          cols="6"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.maximumLimit" :currency="currency"
                                    :property-name="'maximumLimit'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
    </v-row>
    <v-row no-gutters class="mb-4" v-if="type === 'deposit'">
      <v-col
          cols="6"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.firstMinLimit" :currency="currency"
                                    :property-name="'firstMinLimit'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
      <v-col
          cols="6"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.firstMaxLimit" :currency="currency"
                                    :property-name="'firstMaxLimit'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
    </v-row>
    <row no-gutters class="mb-2">
      <v-col cols="12">
        <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXAMOUNTS')}}</p>
      </v-col>
    </row>
    <v-row no-gutters class="mb-4">
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.maxDay" :currency="currency" :property-name="'maxDay'"
                                    @change="getDataFromInputs" :tab="tab" :viewAs="viewAs"
                                    :help="help" :fee="false"/>
      </v-col>
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.maxWeek" :currency="currency"
                                    :property-name="'maxWeek'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.maxMonth" :currency="currency"
                                    :property-name="'maxMonth'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
    </v-row>
    <row no-gutters class="mb-2">
      <v-col cols="12">
        <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.TRANSACTIONS')}}</p>
      </v-col>
    </row>
    <v-row no-gutters class="mb-4">
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.maxTransDay" :currency="currency"
                                    :property-name="'maxTransDay'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.maxTransWeek" :currency="currency"
                                    :property-name="'maxTransWeek'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.maxTransMonth" :currency="currency"
                                    :property-name="'maxTransMonth'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="false"/>
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Prop} from "vue-property-decorator";
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import TranslationMixin from "@/core/mixins/translationMixin";
import {
  CashierConfigInputChangesInterface,
  CashierConfigInputGroupInterface,
} from "@/core/interface/cashierConfig/CashierConfigInterface";
import CashierConfigInputGroup from "@/plugin/cashier/config/components/CashierConfigInputGroup.vue";

@Component({
  components: {
    CashierConfigInputGroup
  }
})

export default class LimitsTab extends Mixins( AssetTabMixin, TranslationMixin) {
  @Prop() groups!: CashierConfigInputGroupInterface
  @Prop() currency!: string
  @Prop() tab!: string
  @Prop() viewAs!: string
  @Prop() help!: boolean
  @Prop() type!: string

  getDataFromInputs(changes: CashierConfigInputChangesInterface) {
    this.$emit('change', changes)
  }
}
</script>

<style scoped>

</style>