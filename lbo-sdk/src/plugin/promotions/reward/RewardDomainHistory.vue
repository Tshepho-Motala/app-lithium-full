<template>
  <div>
    <v-card outlined>
      <!-- <v-autocomplete v-model="selectedPromotion" label="Select Promotion" :items="selectablePromotions" item-text="name" return-object clearable @change="onPromotionSelected"></v-autocomplete> -->
      <RewardHistoryList v-if="table !== null" :domain="domain" :table="table" :itemsPerPage="itemsPerPage" :page.sync="page" @optionsChanged="onPageOptionsChanged"/>
    </v-card>
    <div class="d-flex">
      <v-spacer></v-spacer>
      <div class="shrink pt-2" style="max-width: 100px">
        <v-select @change="getTable" label="Items Per Page" :items="pageSizes" v-model="itemsPerPage"> </v-select>
      </div>
      <div class="pt-4">
        <v-pagination v-model="page" :length="pageCount" @input="onPageSelected" :total-visible="7"></v-pagination>
      </div>
    </div>
  </div>
</template>

<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import DomainItem from '@/plugin/cms/models/DomainItem'
import { Vue, Component, Inject, Prop } from 'vue-property-decorator'

import { TableContract } from '@/core/axios/axios-api/generic/TableContract'
import { PlayerRewardHistoryContract } from '@/core/interface/contract-interfaces/service-reward/RewardHistoryContact'
import PromotionContract from '@/core/interface/contract-interfaces/service-promo/PromotionContract'
import RewardHistoryList from './RewardHistoryList.vue'
import {DataOptions, DataTableHeader} from "vuetify";
import {constructVDataTablesRequest} from "@/core/utils/helpers";

@Component({
  components: {
    RewardHistoryList
  }
})
export default class RewardDomainHistory extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface
  @Prop({ required: true }) readonly domain!: DomainItem

  queryString : { [x: string]: string | boolean | number } = {}
  loading = false
  table: TableContract<PlayerRewardHistoryContract> | null = null

  // TODO PROM Dont use this. Get a new endpoint (1)
  selectablePromotions: PromotionContract[] = []
  selectedPromotion: PromotionContract | null = null

  pageSizes = [5, 10, 15, 20, 50, 100]

  page = 1
  itemsPerPage = 10

  get pageCount() {
    if (!this.table) {
      return 0
    }
    return Math.ceil(this.table.recordsTotal / this.itemsPerPage)
  }

  mounted() {
    this.getTable();
  }

  onPageOptionsChanged(params: { options: DataOptions, headers: DataTableHeader[] }) {
    this.queryString = constructVDataTablesRequest(params.options, params.headers)
    Object.keys(this.queryString).forEach(k => {
      if (this.queryString[k] === 'rewardName') {
        this.queryString[k] = 'rewardRevision.name'
      }
    });
    this.getTable()
  }

  onPageSelected() {
    this.getTable()
  }

  async getPromotions() {
    // TODO PROM Dont use this. Get a new endpoint (1) - Changing in new version of Vue
    const promotions = await this.apiClients.servicePromo.getPromotionTable([this.domain], '0', '500')
    if (promotions !== null) {
      this.selectablePromotions = promotions.data.map((x) => x.current!)
    }
  }

  async getTable() {
    if (this.domain === null) {
      return
    }
    const start = ((this.page - 1) * this.itemsPerPage).toString()
    const length = this.itemsPerPage.toString()

    this.loading = true
    this.apiClients.serviceReward.getPlayerRewardHistoryOnDomain(this.domain, start, length, this.queryString)
        .then((response) => {
          this.table = response
      }).catch((err) => {
      console.error(err)
    }).finally(() => {
      this.loading = false
    })
  }

  onPromotionSelected() {
    debugger
  }
}
</script>

<style scoped>
</style>