<template>
  <div style="width: 100%">
    <RRule v-if="false" />
    <DomainSelectorPage v-model="domain"
                        noGutters
                        extended
                        :hideToolbar="!domain"
                        v-else
                        :roles="['PROMOTIONS_VIEW', 'PROMOTIONS_EDIT', 'USER_PROMOTIONS_VIEW']">
      <template #extension>
        <v-tabs v-model="tab"
                centered
                grow
                icons-and-text>
          <v-tabs-slider></v-tabs-slider>

          <v-tab>
            Active Promotions
            <v-icon>mdi-calendar</v-icon>
          </v-tab>

          <v-tab>
            Reward History
            <v-icon>mdi-flag-checkered</v-icon>
          </v-tab>

          <v-tab>
            Disabled Promotions
            <v-icon>mdi-calendar-minus-outline</v-icon>
          </v-tab>

          <v-tab v-if="technical.show">
            Technical
            <v-icon>mdi-cogs</v-icon>
          </v-tab>
        </v-tabs>
      </template>

      <v-tabs-items v-model="tab">
        <v-tab-item>
          <v-row style="width: 100%">
            <v-col cols="12"
                   class="pt-8">
              <ScheduleDisplay @edit="onPromotionEdit"
                               @disable="onPromotionDisable"
                               :promotions="promotions"
                               @create="onCreatePromotionClick"
                               @calendarChange="onCalendarChange" />
            </v-col>
          </v-row>
        </v-tab-item>
        <v-tab-item>
          <RewardDomainHistory :domain="domain" />
        </v-tab-item>
        <v-tab-item>
          <PromotionListDisabled :domain="domain" />
        </v-tab-item>
        <v-tab-item>
          <div>
            <v-checkbox label="Draft"
                        v-model="technical.draft"></v-checkbox>
          </div>
          <div>
            {{ technical.data }}
          </div>
        </v-tab-item>
      </v-tabs-items>

      <!-- CREATE DIALOG -->
      <v-dialog scrollable
                v-model="showCreatePromotionDialog"
                max-width="1000"
                persistent>
        <!-- TODO PROM Edit should be standalone and take in an ID if we're editing an existing' -->
        <PromotionEdit v-if="draftPromotion"
                       v-model="draftPromotion"
                       @cancel="onEditCancel"
                       @save="showMarkFinalDialog" />
      </v-dialog>

      <v-dialog v-model="showMarkCurrentDialog"
                persistent
                max-width="500">
        <v-card>
          <v-card-title> Publish Promotion? </v-card-title>
          <v-card-text>
            <span>
              When creating or editing a Promotion you have the option to save it as a Draft or to Publish it. A draft
              version of a Promotion will not
              be active to be used until it is Published.
            </span>
            <br />
            <br />
            <span> If you save a draft you will be able to edit and publish it later. </span>
          </v-card-text>
          <v-card-actions>
            <v-btn text
                   color="success"
                   @click="onMarkFinalSelected(false)">Save Draft</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary"
                   @click="onMarkFinalSelected(true)">Publish</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </DomainSelectorPage>
  </div>
</template>

<script lang='ts'>
import { Component, Inject, Vue } from 'vue-property-decorator'
import { Promotion } from './Promotion'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'

import DomainSelectorPage from '../components/DomainSelectorPage.vue'
import ScheduleDisplay from './schedule/ScheduleDisplay.vue'
import PromotionEdit from './promotion/PromotionEdit.vue'
import PromotionListDisabled from './promotion/PromotionListDisabled.vue'
import RewardDomainHistory from './reward/RewardDomainHistory.vue'

import { DomainItemInterface } from '../cms/models/DomainItem'
import PromotionDraftContract from '@/core/interface/contract-interfaces/service-promo/PromotionDraftContract'

import RRule from '@/plugin/components/RRule.vue'

@Component({
  components: {
    ScheduleDisplay,
    PromotionEdit,
    PromotionListDisabled,
    DomainSelectorPage,
    RewardDomainHistory,
    RRule
  }
})
export default class Promotions extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  promotions: Promotion[] = []
  loading = false
  showTabs = false

  tabUnmappedIndex = 0
  tabArea = 0

  showCreatePromotionDialog = false
  showMarkCurrentDialog = false
  publishAfterSaving = false

  draftPromotion: Promotion | null = null

  calStartDate: string | null = null
  calEndDate: string | null = null

  tab = null

  domain: DomainItemInterface | null = null

  technical: any = {
    draft: false, // True = dont save, just see
    show: false,
    data: null
  }

  get hasDraftPromotion() {
    return this.draftPromotion !== null
  }

  created() {
    this.draftPromotion = new Promotion()
  }

  async onCalendarChange({ start, end }) {
    this.calStartDate = start.date
    this.calEndDate = end.date
    await this.showEventsOnCalendar()
  }

  async showEventsOnCalendar() {
    if (!this.calStartDate || !this.calEndDate || this.domain === null) {
      return
    }

    this.loading = true
    this.promotions = []

    const promotions = await this.apiClients.servicePromo.getPromotionsBetween([this.domain], this.calStartDate, this.calEndDate)
    if (promotions !== null) {
      for (const p of promotions) {
        const pr = new Promotion()

        if (p.current) {
          pr.fromContract(p.current, p.id)
        }

        if (p.edit) {
          pr.edit = p.edit
        }

        this.promotions.push(pr)
      }
    }

    this.loading = false
  }

  onCreatePromotionClick(date: string | undefined) {
    this.draftPromotion = new Promotion()
    if (date) {
      this.draftPromotion.schedule.dateStart = new Date(date)
    }
    this.draftPromotion.domain = this.domain
    this.showPromotionCreateDialog()
  }

  onEditCancel() {
    this.hidePromotionCreateDialog()
    this.draftPromotion = null
  }

  showPromotionCreateDialog() {
    this.showCreatePromotionDialog = true
  }

  hidePromotionCreateDialog() {
    this.showCreatePromotionDialog = false
  }

  showMarkFinalDialog() {
    this.showMarkCurrentDialog = true
  }

  onMarkFinalSelected(publishAfterSaving: boolean) {
    this.publishAfterSaving = publishAfterSaving
    this.hideMarkFinalDialog()
    this.onPromotionSave()
  }

  hideMarkFinalDialog() {
    this.showMarkCurrentDialog = false
  }

  async onPromotionSave() {
    this.hidePromotionCreateDialog()

    if (this.technical.draft) {
      this.technical.data = this.draftPromotion?.toContract()
      return
    }

    this.loading = true
    if (this.draftPromotion !== null) {
      try {
        let draft: PromotionDraftContract | null = null

        if (this.draftPromotion.id) {
          // EDIT
          draft = await this.apiClients.servicePromo.editPromotionDraft({
            id: this.draftPromotion.id,
            edit: this.draftPromotion.toContract()
          })
        } else {
          // CREATE
          draft = await this.apiClients.servicePromo.createPromotionDraft({
            edit: this.draftPromotion.toContract()
          })
        }

        if (draft !== null && this.publishAfterSaving) {
          await this.apiClients.servicePromo.publishPromotionDraft(draft.id.toString())
        }
      } catch (e) {
        console.error(e)
        this.loading = false
        this.showCreatePromotionDialog = true
        return
      }

      // TODO: This can be optimised by just adding it locally, and not having to call it all
      await this.showEventsOnCalendar()
    }

    this.draftPromotion = null

    this.loading = false
  }

  async onPromotionEdit(promotion: Promotion) {
    this.loading = true

    // const livePromotion = await this.apiClients.servicePromo.getPromotion(promotion.id!.toString())

    // this.promotions.find(x => x.id === id)
    // const promotion = null //  await this.rootScope.provide.promotionProvider.getById(id)

    if (promotion) {
      if (promotion.edit !== null) {
        const prom = new Promotion()
        prom.fromContract(promotion.edit, null)
        prom.id = promotion.id // Ensure we set the version ID to the parent ID
        this.draftPromotion = prom
      } else {
        this.draftPromotion = promotion
      }

      this.showPromotionCreateDialog()
    }
    this.loading = false
  }

  async onPromotionDisable(promotion: Promotion) {
    if (!promotion.id) {
      return
    }
    this.loading = true
    await this.apiClients.servicePromo.toggleEnabled(promotion.id.toString(), 'false')
    await this.showEventsOnCalendar()
    this.loading = false
  }
}
</script>

<style></style>