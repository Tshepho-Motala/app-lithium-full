<template>
  <div>
    <v-row no-gutters class="mt-4">
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.flatFee" :currency="currency"
                                    :property-name="'flatFee'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="true"
                                    @calculate="calculateFe"/>
      </v-col>
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.percentageFee" :property-name="'percentageFee'"
                                    @change="getDataFromInputs" :tab="tab" :viewAs="viewAs"
                                    :help="help" :fee="true" @calculate="calculateFe"
                                    :percent="true"/>
      </v-col>
      <v-col
          cols="4"
          class="px-3"
      >
        <cashier-config-input-group :group="groups.minimumFee" :currency="currency"
                                    :property-name="'minimumFee'" @change="getDataFromInputs" :tab="tab"
                                    :viewAs="viewAs" :help="help" :fee="true"
                                    @calculate="calculateFe"/>
      </v-col>
    </v-row>
    <v-row no-gutters>
      <v-col>
        <p>{{$translate("UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.EXAMPLE.TITLE")}}
          <span class="text-muted">{{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.EXAMPLE.START')}}{{currency}}150.00</span>
        </p>
        <div class="pa-5 blue-grey lighten-4" v-if="!processor.fe" >
          <p class="mb-0">{{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.EXAMPLE.NO_FEES')}}({{currency}}150.00).</p>
        </div>
        <div class="pa-5 blue-grey lighten-4" v-if="processor.fe && (perc || flatDec || minimumDec)">
            <p v-if="perc">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.FEES_CALC.PERCENTAGE')}}
              ({{perc ? perc : 0}}%) {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.FEES_CALC.SPECIFIED')}}.
              {{currency}}{{percAmount ? percAmount : 0}}
              {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.FEES_CALC.WILL_BE_DEDUCTED')}}.</p>
            <p v-if="flatDec">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.FEES_CALC.FLAT_FEE')}}
              ({{currency}}{{flatDec ? flatDec : 0}})
              {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.FEES_CALC.WILL_BE_ADDED')}}.</p>
            <p v-if="minimumDec">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.FEES_CALC.MIN_FEE')}}
              ({{currency}}{{minimumDec ? minimumDec : 0}})
              {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.FEES_CALC.SPECIFIED')}}.</p>
            <template v-if="perc || flatDec || minimumDec">
              <p v-if="minimumDec >= feeAmount">
                {{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.EXAMPLE.MIN_MORE')}}</p>
              <p v-else>
                {{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.EXAMPLE.MIN_LESS')}}{{currency}}{{feeAmount ? feeAmount : 0}}</p>
              <p>
                {{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.EXAMPLE.TOTAL')}}{{currency}}{{totalAmount ? totalAmount : 0}}</p>
            </template>
        </div>
        <template v-if="viewAs || type !== 'deposit' || processor.dmpp || processor.dmpu">
          <p :data-test-id="`slt-strategy-options${dataTestName}`">{{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.PROCESSOR_COMM_STRAT')}}</p>
          <v-select
              :data-test-id="`slt-strategy-options${dataTestName}`"
              v-if="showStrategy"
              v-model="strategy"
              :items="strategyOptions"
              item-text="label"
              item-value="value"
              @change="changeStrategy"
          >
          </v-select>
          <div class="pa-5 blue-grey lighten-4" v-if="showStrategyDescription">
            <template>
              <p v-if="isStrategyOption1Set()">{{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.CHARGE_OPTION_1_HELP_MESSAGE')}}</p>
              <p v-if="isStrategyOption2Set()">{{$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.CHARGE_OPTION_2_HELP_MESSAGE')}}</p>
            </template>
          </div>
        </template>
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
  CashierConfigInputGroupInterface, CashierConfigProcessor, StrategyOptionInterface
} from "@/core/interface/cashierConfig/CashierConfigInterface";
import CashierConfigInputGroup from "@/plugin/cashier/config/components/CashierConfigInputGroup.vue";

@Component({
  components: {
    CashierConfigInputGroup
  }
})

export default class FeesTab extends Mixins( AssetTabMixin, TranslationMixin) {
  @Prop() groups!: CashierConfigInputGroupInterface
  @Prop() processor!: CashierConfigProcessor
  @Prop() currency!: string
  @Prop() tab!: string
  @Prop() viewAs!: string
  @Prop() help!: boolean
  @Prop() type!: string

  @Prop() perc? : number | null
  @Prop() percAmount? : number | null
  @Prop() flatDec? : number | null
  @Prop() minimumDec? : number | null
  @Prop() feeAmount? : number | null
  @Prop() totalAmount? : number | null

  strategy?: number | null  = null

  strategyOptions: StrategyOptionInterface[] = [
    {
      label: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.CHARGE_OPTION_1'),
      value: 1
    },
    {
      label: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.CHARGE_OPTION_2'),
      value: 2
    }
  ]

  mounted() {
    if(this.processor) {
      if(this.processor.dmpp && this.processor.dmpp.fees) {
        this.strategy = this.processor.dmpp.fees.strategy
      } else if (this.processor.dmpu && this.processor.dmpu.fees) {
        this.strategy = this.processor.dmpu.fees.strategy
      } else if (this.processor.fees) {
        this.strategy = this.processor.fees.strategy
      }
    }

  }

  getDataFromInputs(changes: CashierConfigInputChangesInterface) {
    this.$emit('change', changes)
  }

  calculateFe() {
    this.$emit('calculateFe')
  }

  get showStrategy() {
    if(this.processor) {
      if (this.viewAs === 'profile') {
          return this.processor.dmpp && this.processor.dmpp.fees
      } else if (this.viewAs === 'user') {
          return this.processor.dmpu && this.processor.dmpu.fees
      } else if (this.type !== 'deposit') {
        return this.processor.fees
      }
      return false
    } else return false
  }

  showStrategyDescription() {
    if(this.strategy){
      return true
    }
    return false
  }

  isStrategyOption1Set() {
    if(this.viewAs === 'profile'){
      return this.processor?.dmpp?.fees?.strategy == 1
    } else if(this.viewAs === 'user'){
      return this.processor?.dmpu?.fees?.strategy == 1
    }

    return this.processor?.fees?.strategy == 1
  }

  isStrategyOption2Set() {
    if(this.viewAs === 'profile'){
      return this.processor?.dmpp?.fees?.strategy == 2
    } else if(this.viewAs === 'user'){
      return this.processor?.dmpu?.fees?.strategy == 2
    }

    return this.processor?.fees?.strategy == 2
  }


  changeStrategy() {
    this.$emit('changeStrategy', this.strategy)
  }

  get dataTestName() : string {
    return this.viewAs === 'user' ? '-user' : '-profile'
  }
}
</script>

<style scoped>

</style>