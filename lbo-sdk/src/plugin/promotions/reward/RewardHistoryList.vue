<template>
  <div>
    <v-data-table v-if="table"
                  :loading="loading"
                  :headers="headers"
                  :items="table.data"
                  :page.sync="pageNumber"
                  :items-per-page="itemsPerPage"
                  @update:options="updateOptions"
                  hide-default-footer>
      <template v-slot:[`item.rewardName`]="{ item }">
        <div class="py-2">
          <div>
            {{ item.rewardName }}
          </div>
          <div class="text-caption grey--text">
            <v-chip v-for="(rType, i) of item.rewardTypes"
                    :key="`r_${i}`"
                    v-text="rType.rewardTypeName"
                    outlined
                    x-small></v-chip>
          </div>
        </div>
      </template>

      <template v-slot:[`item.awardedDate`]="{ item }">
        <div class="py-2">
          <div>{{ getDateFormat(item.awardedDate) }}</div>
          <div class="text-caption grey--text">{{ getDateDistance(item.awardedDate) }}</div>
        </div>
      </template>

      <template v-slot:[`item.playerGuid`]="{ item }">
        <span v-text="item.playerGuid"
              class="text-subtitle-2"></span>
      </template>

      <template v-slot:[`item.expiryDate`]="{ item }">
        <div class="py-2">
          <div>{{ getDateFormat(item.expiryDate) }}</div>
          <div class="text-caption grey--text">{{ getDateDistance(item.expiryDate) }}</div>
        </div>
      </template>

      <template v-slot:[`item.redeemedDate`]="{ item }">
        <div class="py-2">
          <div>{{ getDateFormat(item.redeemedDate) }}</div>
          <div class="text-caption grey--text">{{ getDateDistance(item.redeemedDate) }}</div>
        </div>
      </template>

      <template v-slot:[`item.actions`]="{ item }">
        <v-icon color="primary"
                @click="viewRewardDetails(item)">mdi-information-outline</v-icon>
      </template>
    </v-data-table>

    <v-dialog max-width="500"
              v-model="viewRewardDialog"
              persistent
              scrollable>
      <v-card v-if="viewReward !== null"
              max-height="800"
              :loading="loading">
        <v-card-title>
          <v-toolbar outlined
                     rounded
                     color="primary"
                     flat
                     dark>
            <v-toolbar-title>
              <span v-text="viewReward.rewardName"></span>
            </v-toolbar-title>
          </v-toolbar>
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text class="pt-4">
          <div class="d-flex flex-column"
               style="gap: 10px">
            <div class="d-flex justify-space-between">
              <span>Reward ID</span>
              <span v-text="viewReward.id"></span>
            </div>

            <div class="d-flex justify-space-between">
              <span>Reward Status</span>
              <span v-text="viewReward.status"></span>
            </div>

            <div class="d-flex justify-space-between">
              <span>Player GUID</span>
              <span v-text="viewReward.playerGuid"></span>
            </div>

            <div class="d-flex justify-space-between">
              <span>Awarded</span>
              <span v-text="getDateFormat(viewReward.awardedDate)"></span>
            </div>

            <div class="d-flex justify-space-between">
              <span>Expires</span>
              <span v-text="getDateFormat(viewReward.expiryDate)"></span>
            </div>

            <div class="py-2">
              <v-divider></v-divider>
              <div class="py-2">
                <span class="black--text text-subtitle-1"> Reward Components </span>
              </div>
            </div>

            <div class="d-flex flex-column"
                 style="gap: 10px">
              <v-card v-for="(comp, i) of viewReward.rewardTypes"
                      :key="`type-${i}`"
                      outlined
                      class="pa-4">
                <div class="d-flex justify-space-between">
                  <span>Component ID</span>
                  <span v-text="comp.id"></span>
                </div>
                <div class="d-flex justify-space-between">
                  <span>Component Status</span>
                  <span v-text="comp.status"></span>
                </div>
                <div class="d-flex justify-space-between">
                  <span>Component Type</span>
                  <span v-text="comp.rewardTypeName"></span>
                </div>
                <div class="d-flex justify-space-between">
                  <span>Awarded</span>
                  <span v-text="getDateFormat(comp.awardedOn)"></span>
                </div>
                <div class="d-flex pt-4"
                     v-if="comp.cancellable">
                  <v-spacer></v-spacer>
                  <v-btn @click="cancelRewardComponent(comp)"
                         color="error"
                         text
                         x-small>
                    <v-icon left>mdi-cancel</v-icon>
                    <span>Cancel Component</span>
                  </v-btn>
                </div>
              </v-card>
            </div>
          </div>
        </v-card-text>

        <v-divider class="my-2"></v-divider>

        <v-card-actions>
          <v-btn text
                 @click="closeRewardDetails">Close</v-btn>
          <v-spacer></v-spacer>
          <v-btn @click="cancelReward"
                 color="error"
                 text
                 v-if="viewReward.cancellable">
            <v-icon left>mdi-cancel</v-icon>
            <span>Cancel Reward</span>
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog persistent
              max-width="500"
              v-model="confirmQuery">
      <v-card>
        <v-card-title> Are you sure? </v-card-title>
        <v-card-text> This action can not be undone. </v-card-text>
        <v-card-actions>
          <v-btn color="primary"
                 @click="confirmCancel">Cancel</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="warning"
                 text
                 @click="confirmApprove">Confirm</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
</div>
</template>

<script lang='ts'>
import { TableContract } from '@/core/axios/axios-api/generic/TableContract'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import {
  PlayerRewardHistoryContract,
  PlayerRewardTypeHistoryContract
} from '@/core/interface/contract-interfaces/service-reward/RewardHistoryContact'
import DomainItem from '@/plugin/cms/models/DomainItem'
import { format, formatDistance } from 'date-fns'
import { Vue, Component, Prop, PropSync, Inject } from 'vue-property-decorator'

@Component
export default class RewardHistoryList extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @Prop({ required: true }) readonly domain!: DomainItem
  @Prop() readonly table!: TableContract<PlayerRewardHistoryContract>
  @Prop({ default: 5 }) itemsPerPage!: number
  @PropSync('page', { default: 1 }) pageNumber!: number

  loading = false
  headers = [
    { text: 'Name', value: 'rewardName' },
    { text: 'Awarded', value: 'awardedDate' },
    { text: 'Player', value: 'playerGuid' },
    { text: 'Expires', value: 'expiryDate' },
    { text: 'Redeemed On', value: 'redeemedDate' },
    { text: 'Status', value: 'status' },
    { text: 'Actions', value: 'actions', sortable: false }
  ]

  viewReward: PlayerRewardHistoryContract | null = null
  viewRewardDialog = false
  confirmQueryFn: null | (() => void) = null

  updateOptions(newValue) {
    this.$emit('optionsChanged', { options: newValue, headers: this.headers })
  }

  get confirmQuery() {
    return this.confirmQueryFn !== null
  }

  get showViewReward(): boolean {
    return this.viewReward !== null
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

  cancelRewardComponent(component: PlayerRewardTypeHistoryContract) {
    this.confirmQueryFn = () => this.doCancelRewardComponent(component)
  }

  async doCancelRewardComponent(component: PlayerRewardTypeHistoryContract) {
    this.loading = true
    await this.apiClients.serviceReward.cancelRewardComponentForPlayer(this.domain, component.id.toString(), component.playerGuid)
    await this.getRewardDetails()
    this.loading = false
  }

  cancelReward() {
    this.confirmQueryFn = () => this.doCancelReward()
  }

  async doCancelReward() {
    if (!this.viewReward) {
      return
    }
    this.loading = true
    await this.apiClients.serviceReward.cancelReward(this.domain, this.viewReward.id.toString(), this.viewReward.playerGuid)
    await this.getRewardDetails()
    this.loading = false
  }

  viewRewardDetails(reward: PlayerRewardHistoryContract) {
    this.viewReward = reward
    this.viewRewardDialog = true
  }

  async getRewardDetails() {
    if (!this.viewReward) {
      return
    }
    const rewards = await this.apiClients.serviceReward.getPlayerRewardHistory(this.domain, this.viewReward.playerGuid, '1', '500', {})
    if (!rewards || !this.viewReward) {
      return
    }
    this.viewReward = rewards.data.find((x) => x.id === this.viewReward!.id) || null
  }

  closeRewardDetails() {
    this.viewRewardDialog = false

    this.$nextTick(() => {
      this.viewReward = null
    })
  }

  confirmApprove() {
    if (this.confirmQueryFn !== null) {
      this.confirmQueryFn()
    }
    this.confirmQueryFn = null
  }

  confirmCancel() {
    this.confirmQueryFn = null
  }
}
</script>

<style scoped></style>