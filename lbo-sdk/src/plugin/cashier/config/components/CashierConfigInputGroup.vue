<template>
  <div class="d-flex flex-column" data-test-id="cashier-config-input-group">
    <p>{{group.title}}</p>
    <div v-if="group.showProfile">
      <v-tooltip top open-delay="500">
        <template v-slot:activator="{ on, attrs }">
          <div class="d-flex align-center"
               v-bind="attrs"
               v-on="on">
            <p class="mb-0 text-muted">{{group.pText}}</p>
            <v-icon>mdi-help-circle</v-icon>
          </div>
        </template>
        <p class="cashier-method-tooltip">{{group.pHint}}</p>
      </v-tooltip>
      <v-text-field
          data-test-id="txt-profile-value"
          v-model.number="profileValue"
          :placeholder="group.pPlaceholder"
          outlined
          type="number"
          hide-details
          class="cashier-input-profile"
          @input="changeValue"
          :prefix="currency ? currency : ''"
          :suffix=" propertyName === 'percentageFee' ? '%' :''"
      ></v-text-field>
      <transition name="cashier__help" >
        <p v-if="help">{{group.gHint}}</p>
      </transition>
    </div>
    <div v-if="group.showUser">
      <v-tooltip top open-delay="500">
        <template v-slot:activator="{ on, attrs }">
          <div class="d-flex align-center"
               v-bind="attrs"
               v-on="on">
            <p class="mb-0 text-muted">{{group.uText}}</p>
            <v-icon>mdi-help-circle</v-icon>
          </div>
        </template>
        <p class="cashier-method-tooltip">{{group.uHint}}</p>
      </v-tooltip>
      <v-text-field
          data-test-id="txt-user-value"
          v-model.number="userValue"
          :placeholder="group.uPlaceholder"
          outlined
          type="number"
          hide-details
          class="cashier-input-user"
          @input="changeValue"
          :prefix="currency ? currency : ''"
          :suffix=" propertyName === 'percentageFee' ? '%' :''"
      ></v-text-field>
      <transition name="cashier__help" >
        <p v-if="help">{{group.uHint}}</p>
      </transition>
    </div>
    <div>
      <v-tooltip top open-delay="500">
        <template v-slot:activator="{ on, attrs }">
          <div class="d-flex align-center"
               v-bind="attrs"
               v-on="on">
            <p class="mb-0 text-muted">{{group.gText}}</p>
            <v-icon>mdi-help-circle</v-icon>
          </div>
        </template>
        <p class="cashier-method-tooltip">{{group.gHint}}</p>
      </v-tooltip>
      <v-text-field
          data-test-id="txt-global-value"
          v-model.number="globalValue"
          :placeholder="group.gPlaceholder"
          :outlined="!viewAs"
          type="number"
          :readonly="!!viewAs"
          :filled="!!viewAs"
          class="cashier-input-global"
          hide-details
          @input="changeValue"
          :prefix="currency ? currency : ''"
          :suffix=" propertyName === 'percentageFee' ? '%' :''"
      ></v-text-field>
      <transition name="cashier__help" >
        <p v-if="help">{{group.gHint}}</p>
      </transition>
    </div>
    <div v-if="group.showDomain">
      <v-tooltip top open-delay="500">
        <template v-slot:activator="{ on, attrs }">
          <div class="d-flex align-center"
               v-bind="attrs"
               v-on="on">
            <p class="mb-0 text-muted">{{group.dText}}</p>
            <v-icon>mdi-help-circle</v-icon>
          </div>
        </template>
        <p class="cashier-method-tooltip">{{group.dHint}}</p>
      </v-tooltip>
      <v-text-field
          data-test-id="txt-domain-value"
          v-model.number="domainValue"
          :placeholder="group.dPlaceholder"
          :outlined="!viewAs"
          type="number"
          :readonly="!!viewAs"
          :filled="!!viewAs"
          class="cashier-input-domain"
          hide-details
          @input="changeValue"
          :prefix="currency ? currency : ''"
          :suffix=" propertyName === 'percentageFee' ? '%' :''"
      ></v-text-field>
      <transition name="cashier__help" >
        <p v-if="help">{{group.dHint}}</p>
      </transition>
    </div>
  </div>
</template>
<script lang="ts">
import {Component, Prop, Vue} from "vue-property-decorator";
import {CashierConfigInputChangesInterface, IGInterface} from "@/core/interface/cashierConfig/CashierConfigInterface";

@Component
export default class CashierConfigInputGroup extends Vue {

  @Prop() group!: IGInterface
  @Prop() currency?: string
  @Prop() propertyName!: string
  @Prop() viewAs?: string
  @Prop() help!: boolean
  @Prop() fee?: boolean
  @Prop() tab!: string

  globalValue: number | null = null
  profileValue: number | null = null
  userValue: number | null = null
  domainValue: number | null = null

  mounted() {
    this.globalValue = this.group.gValue
    this.profileValue = this.group.pValue
    this.domainValue = this.group.dValue
    this.userValue = this.group.uValue
  }

  changeValue() {
    let changedData: CashierConfigInputChangesInterface = {
        name: this.propertyName,
        tab: this.tab,
        fields: {
          gValue: this.globalValue,
          pValue: this.profileValue,
          uValue: this.userValue,
          dValue: this.domainValue
        }
    }

    this.$emit('change', changedData)

    if(this.fee) {
      this.$emit('calculate')
    }
  }
}
</script>

<style scoped>

</style>