<template>
  <DomainSelectorPage v-model="domain"
                      readonly
                      title="Reward Configuration"
                      showDivider
                      data-id="RewardSelect">
    <template>
      <v-form lazy-validation
              ref="form">
        <div style="max-height: 800px">
          <v-row class="pt-4">
            <v-col cols="12"
                   v-if="loading">
              <v-progress-linear indeterminate></v-progress-linear>
            </v-col>

            <v-col cols="12">
              <template v-if="hasCategory">
                <RewardProviderSelect multiple
                                      :domain="domain"
                                      v-model="providers" />
              </template>
              <template v-else>
                <span>The selected category currently has no providers.</span>
              </template>
            </v-col>

            <template>
              <v-col cols="6"
                     v-if="hasCategory">
                <div class="pb-2">
                  <strong>
                    <span>Reward Details</span>
                  </strong>
                </div>
                <v-text-field hint="This reward may be reused so create an informative name"
                              label="Reward Name *"
                              outlined
                              persistent-hint
                              v-model="name"
                              :rules="rulesBasic.required"></v-text-field>

                <v-textarea hide-details
                            outlined
                            label="Reward Description *"
                            v-model="description"
                            :rules="rulesBasic.required"></v-textarea>
              </v-col>

              <v-col cols="6">
                <div class="pb-2">
                  <strong>
                    <span>Reward Lifetime</span>
                  </strong>
                </div>
                <v-text-field outlined
                              persistent-hint
                              hint="How long before this reward expires *"
                              type="number"
                              label="Length *"
                              v-model="lifetimeValue"
                              :rules="rules.gtZero"></v-text-field>
                <v-select outlined
                          label="Granularity *"
                          v-model="lifetimeGranularity"
                          :items="granularityList"
                          item-text="label"
                          item-value="value"
                          :rules="rulesBasic.required"></v-select>
              </v-col>

              <v-col cols="12"
                     v-if="hasCategory">
                <v-card flat
                        :disabled="!hasProviders">
                  <div>
                    <span class="text-h6">Reward Types</span>
                  </div>
                  <div>
                    <span v-if="!hasProviders"> Please select one or more providers to continue </span>
                    <RewardTypeMultiSelect ref="mult"
                                           class="pt-3"
                                           v-else
                                           v-model="rewardTypes"
                                           :providers="providers"
                                           :domain="domain" />
                  </div>
                </v-card>
              </v-col>
            </template>
          </v-row>
        </div>
      </v-form>
    </template>

    <template #actions>
      <v-btn text
             @click="onCancel">Cancel</v-btn>
      <v-spacer></v-spacer>
      <v-btn color="success"
             @click="onSave"
             :disabled="saveDisabled">Save</v-btn>
    </template>
</DomainSelectorPage>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { Component, Inject, Prop, Mixins } from 'vue-property-decorator'

import RewardProviderSelect from '@/plugin/components/provider-selectors/RewardProviderSelect.vue'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'

import DomainSelectorPage from '@/plugin/components/DomainSelectorPage.vue'
import CategorySelector from '@/plugin/components/CategorySelector.vue'
import RewardTypeMultiSelect from './RewardTypeMultiSelect.vue'
import RewardProvider from '@/plugin/components/provider-selectors/RewardProvider'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import Reward from './Reward'
import RewardType from './RewardType'
import { nanoid } from 'nanoid'
import RulesMixin from '@/plugin/mixins/RulesMixin'

@Component({
  components: {
    RewardProviderSelect,
    DomainSelectorPage,
    CategorySelector,
    RewardTypeMultiSelect
  }
})
export default class RewardSelect extends Mixins(RulesMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @Prop({ required: true }) readonly domain!: DomainItemInterface

  loading = false

  rewardTypes: RewardType[] = []
  providers: RewardProvider[] = []

  showNoProviders = false

  name = ''
  description = ''
  // code = ''
  enabled = true
  lifetimeValue = ''
  lifetimeGranularity = ''

  granularityList = [
    {
      label: 'Day',
      value: '3'
    },
    {
      label: 'Week',
      value: '4'
    },
    {
      label: 'Month',
      value: '2'
    },
    {
      label: 'Year',
      value: '1'
    },
    {
      label: 'Total',
      value: '5'
    }
  ]

  get hasCategory(): boolean {
    return true
    // return this.category !== null
  }

  get hasProviders(): boolean {
    return this.providers.length > 0
  }

  get hasRewardTypes(): boolean {
    return this.rewardTypes.length > 0
  }

  get saveDisabled() {
    const noData = !this.name || !this.description || !this.lifetimeValue || !this.lifetimeGranularity
    const noProviders = !this.providers || this.providers.length === 0
    const noRewardComps = !this.rewardTypes || this.rewardTypes.length === 0
    const nameTooLong = this.name.length > 25
    return noData || noProviders || noRewardComps || nameTooLong
  }

  async onSave() {
    if (this.saveDisabled) {
      return
    }

    const valid = this.$refs.form.validate()
    if (!valid) {
      return
    }

    const compValid = (this.$refs.mult as any).validate()
    if (!compValid) {
      return
    }

    const code = nanoid()
    const reward: Reward = new Reward(
      this.name,
      this.rewardTypes,
      code,
      this.description,
      this.enabled,
      this.domain,
      this.lifetimeValue,
      this.lifetimeGranularity
    )
    this.$emit('save', reward)
    this.reset()
  }

  onCancel() {
    this.reset()
    this.$emit('cancel')
  }

  reset() {
    this.name = ''
    this.description = ''
    this.providers = []
    this.rewardTypes = []
  }
}
</script>

<style scoped></style>