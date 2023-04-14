<template>
  <div>
    <v-card outlined>
      <v-data-table
        v-if="table"
        :loading="loading"
        :headers="headers"
        :items="table.data"
        :page.sync="page"
        :items-per-page="itemsPerPage"
      >
        <template v-slot:[`item.actwo`]="{ item }">
          <v-btn @click="beginDelete(item)" color="error">
            <v-icon left>mdi-cancel</v-icon>
            Delete
          </v-btn>
        </template>
        <template v-slot:[`item.acone`]="{ item }">
          <v-btn @click="enablePromotion(item)" color="success">
            <v-icon left>mdi-check</v-icon>
            Enable
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <v-dialog width="400" persistent v-model="warnDelete">
      <v-card>
        <v-card-title> Are you sure you want to DELETE this promotion? </v-card-title>
        <v-card-text> You can not undo this action. </v-card-text>
        <v-card-actions>
          <v-btn text @click="cancelDelete">Cancel</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="warning" @click="deletePromotion">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang='ts'>
import { TableContract } from '@/core/axios/axios-api/generic/TableContract'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import PromotionDraftContract from '@/core/interface/contract-interfaces/service-promo/PromotionDraftContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { format, formatDistance } from 'date-fns'
import { Vue, Component, Prop, Inject } from 'vue-property-decorator'

@Component
export default class PromotionListDisabled extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface
  @Prop({ required: true }) readonly domain!: DomainItemInterface

  table: TableContract<PromotionDraftContract> | null = null
  warnDelete = false
  deleteItem: PromotionDraftContract | null = null

  loading = false
  headers = [
    // TODO more headers
    { text: 'ID', value: 'id' },
    { text: 'Title', value: 'current.name' },
    { text: 'Start Date', value: 'current.startDate' },
    { text: 'Reward ID', value: 'current.reward.rewardId' },
    // { text: '', value: '', divider: true },
    { text: '', value: 'acone', sortable: false, align: 'end', width: 150 },
    { text: '', value: 'actwo', sortable: false, align: 'end', width: 150 }
  ]

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
    this.getTable()
  }

  onPageSelected() {
    this.getTable()
  }

  async getTable() {
    if (this.domain === null) {
      return
    }
    const start = ((this.page - 1) * this.itemsPerPage).toString()
    const length = this.itemsPerPage.toString()

    this.loading = true
    this.table = null

    // Hardcoded to '2020-01-01' as PROM only started after that date. We will be 
    // getting back to updating the disabled promotions page which will improve
    // the functionality.
    const data = await this.apiClients.servicePromo.getPromotionDisableTable([this.domain], '2020-01-01')
    console.log('Checking Data:', data)
    if (data !== null) {
      this.table = {
        draw: '0',
        recordsFiltered: data.length,
        recordsTotal: data.length,
        data: data
      }
    }

    this.loading = false
  }

  getDateFormat(dateStr: string) {
    if (!dateStr) {
      return '-'
    }
    return format(new Date(dateStr), 'yyyy-MM-dd')
  }

  getDateDistance(dateStr: string) {
    if (!dateStr) {
      return 'No Data'
    }
    return formatDistance(new Date(dateStr), new Date(), { addSuffix: true })
  }

  async enablePromotion(promotion: PromotionDraftContract) {
    this.loading = true
    await this.apiClients.servicePromo.toggleEnabled(promotion.id.toString(), 'true')
    await this.getTable()
    this.loading = false
  }

  beginDelete(promotion: PromotionDraftContract) {
    this.deleteItem = promotion
    this.warnDelete = true
  }

  cancelDelete() {
    this.deleteItem = null
    this.warnDelete = false
  }

  async deletePromotion() {
    if (this.deleteItem === null) {
      return
    }
    this.loading = true
    await this.apiClients.servicePromo.deletePromotion(this.deleteItem.id.toString())
    this.cancelDelete()
    await this.getTable()
    this.loading = false
  }
}
</script>

<style scoped>
</style>