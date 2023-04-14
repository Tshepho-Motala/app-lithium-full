<template>
  <DomainSelectorPage v-model="domain" noGutters @change="onDomainSelected" :invisible="!!domain" style="width: 100%">
    <v-text-field
      v-if="!playerGuid"
      v-model="playerUsername"
      label="Player Username"
      append-outer-icon="mdi-account-search-outline"
      @click:append-outer="getTable"
    ></v-text-field>

    <v-card outlined>
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
  </DomainSelectorPage>
</template>

<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { Vue, Component, Inject } from 'vue-property-decorator'
import DomainSelectorPage from '@/plugin/components/DomainSelectorPage.vue'
import RewardHistoryList from './RewardHistoryList.vue'
import { TableContract } from '@/core/axios/axios-api/generic/TableContract'
import { PlayerRewardHistoryContract } from '@/core/interface/contract-interfaces/service-reward/RewardHistoryContact'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import {DataOptions, DataTableHeader} from "vuetify";
import {constructVDataTablesRequest} from "@/core/utils/helpers";

@Component({
  components: {
    DomainSelectorPage,
    RewardHistoryList
  }
})
export default class RewardPlayerHistory extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface
  domain: DomainItemInterface | null = null
  queryString : { [x: string]: string | boolean | number } = {}
  loading = false
  table: TableContract<PlayerRewardHistoryContract> | null = null

  pageSizes = [5, 10, 15, 20, 50, 100]

  page = 1
  itemsPerPage = 10
  get pageCount() {
    if (!this.table) {
      return 0
    }
    return Math.ceil(this.table.recordsTotal / this.itemsPerPage)
  }

  playerUsername = ''
  playerGuid = ''

  mounted() {
    if (this.rootScope.provide.data?.user) {
      this.domain = {
        name: this.rootScope.provide.data.user.domain.name,
        displayName: this.rootScope.provide.data?.user.domain.name,
        pd: true
      }
      this.playerGuid = this.rootScope.provide.data.user.guid

      this.getTable()
    }
  }

  async onDomainSelected(domain: DomainItemInterface) {
    this.getTable()
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

  async getTable() {
    const missingUsername = !this.playerUsername
    const missingGuid = !this.playerGuid
    if (this.domain === null || (missingUsername && missingGuid)) {
      return
    }
    const start = ((this.page - 1) * this.itemsPerPage).toString()
    const length = this.itemsPerPage.toString()

    let username = this.playerGuid
    if (!username) {
      username = this.domain.name + '/' + this.playerUsername
    }

    this.loading = true
    this.apiClients.serviceReward.getPlayerRewardHistory(this.domain, username, start, length, this.queryString)
        .then((response) => {
          this.table = response
        }).catch((err) => {
      console.error(err)
    }).finally(() => {
      this.loading = false
    })
    this.loading = false
  }
}
</script>

<style scoped>
</style>