<template>
  <DomainSelectorPage data-id="ChallengeEdit"
                      v-model="domain"
                      readonly
                      title="Challenge Edit"
                      showDivider>
    <template>
      <v-form ref="form">
        <div style="max-height: 800px">
          <v-row class="pt-4">
            <v-col cols="12"
                   v-if="loading">
              <v-progress-linear indeterminate></v-progress-linear>
            </v-col>

            <v-col cols="12"
                   class="pt-0">
              <CategorySelector v-model="category"
                                :domain="domain"
                                @change="onCategorySelected"
                                :loading="loading" />
            </v-col>

            <v-col cols="12"
                   v-if="hasCategory"
                   class="py-0">
              <v-select :rules="rules.required"
                        outlined
                        :items="selectableRuleTypes"
                        v-model="sharedRuleType"
                        label="Select Type *"
                        item-text="name"
                        return-object
                        placeholder="Required"
                        @change="onRuleTypeSelected"
                        hide-details></v-select>
            </v-col>

            <template v-if="hasRuleType">
              <v-col cols="6"
                     class="pb-0">
                <div class="pb-2 d-flex justify-space-between align-center">
                  <div>
                    <strong>
                      <span>Challenge Details</span>
                    </strong>
                  </div>
                  <!-- <div>
                                                    <v-switch stlye=" visibility: hidden;"></v-switch>
                                                  </div> -->
                </div>
                <v-textarea outlined
                            label="Challenge Description *"
                            v-model="challenge.description"
                            :rules="rulesBasic.required"></v-textarea>
              </v-col>

              <v-col cols="6"
                     class="pb-0">
                <div class="pb-2 d-flex justify-space-between align-center">
                  <div>
                    <strong>
                      <span>Shared Challenge Values</span>
                    </strong>
                  </div>
                  <!-- <div>
                                                  <v-switch dense
                                                            v-model="sharedOperation">Enabled</v-switch>
                                                </div> -->
                </div>
                <!-- <div class="pb-2">
                                                <span class="text-caption">
                                                  <strong>Enabled</strong> with a value of 500 over 3 challenges would mean the player could have a value
                                                  of 50 on the first challenge, 200 on the second challenge, and 250 on the third challenge.
                                                  <br>
                                                  <strong>Disabled</strong> with a value of 500 over 3 challenges would mean the player must have a value
                                                  of 500 on each challenge.
                                                </span>
                                              </div> -->
                <template v-if="bypassValue">
                  <!-- <div class="d-flex flex-row justify-space-around"> -->
                  <div style="border: 1px solid #CCC"
                       class="rounded pa-2">
                    <!-- <v-chip v-text="sharedRuleType"
                            dense
                            outlined
                            label
                            small></v-chip> -->
                    <span class="text-caption font-weight-bold"
                          v-text="sharedRuleType"></span>
                    <span class="text-caption"> does not require any values.</span>
                  </div>
                  <!-- </div> -->
                </template>
                <template v-else>
                  <div class="pb-2">

                    <v-text-field :rules="rules.required"
                                  prepend-inner-icon="mdi-link"
                                  hide-details
                                  outlined
                                  v-model="challenge.sharedValue"
                                  label="Value *"
                                  placeholder="Required"
                                  type="number"
                                  :disabled="!isOperationShared || bypassValue"></v-text-field>
                  </div>
                  <v-select :rules="rules.required"
                            prepend-inner-icon="mdi-link"
                            outlined
                            v-model="challenge.sharedOperation"
                            :items="operations"
                            label="Select Operation *"
                            placeholder="Required"
                            :disabled="!isOperationShared || bypassValue"></v-select>
                </template>
              </v-col>

              <v-col cols="12">
                <ChallengeRuleMultiSelect v-model="challenge.rules"
                                          :providers="providers"
                                          :category="category"
                                          :operations="operations"
                                          :domain="domain"
                                          :isOperationShared="isOperationShared"
                                          :operation="challenge.sharedOperation"
                                          :operationValue="challenge.sharedValue"
                                          :selectableRuleTypes="selectableRuleTypes"
                                          :sharedRuleType="sharedRuleType"
                                          @change="validateChallengeRules" />
              </v-col>
            </template>
          </v-row>
        </div>
      </v-form>
    </template>

    <template #actions>
      <v-btn @click="onCancel"
             text
             :disabled="loading">Cancel</v-btn>
      <v-spacer></v-spacer>
      <v-btn @click="reset"
             outlined
             :disabled="!hasCategory || loading">Reset</v-btn>
      <v-spacer></v-spacer>
      <v-btn color="success"
             :disabled="!canSave || loading"
             @click="onSave"> Save </v-btn>
    </template>
  </DomainSelectorPage>
</template>

<script lang='ts'>
import { Component, Inject, Prop, Mixins, VModel } from 'vue-property-decorator'
import { Challenge } from './Challenge'
import Category from '@/plugin/components/Category'
import CategorySelector from '@/plugin/components/CategorySelector.vue'
import DomainSelectorPage from '@/plugin/components/DomainSelectorPage.vue'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import PromotionProvider from '@/plugin/components/provider-selectors/PromotionProvider'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import RulesMixin from '@/plugin/mixins/RulesMixin'
import ChallengeRuleMultiSelect from './ChallengeRuleMultiSelect.vue'

@Component({
  components: {
    CategorySelector,
    DomainSelectorPage,
    ChallengeRuleMultiSelect
  }
})
export default class ChallengeEdit extends Mixins(RulesMixin) {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel({ required: true }) challenge!: Challenge

  @Prop({ required: true }) readonly domain!: DomainItemInterface

  // Bypass any requirement for shared values
  // with default values and operations
  bypassWithDefaultValue = [
    {
      ruleType: 'registration-success',
      value: 1,
      operation: 'counter'
    }
  ]

  isOperationShared = true
  sharedRuleType = ''

  selectableRuleTypes: string[] = []

  providers: PromotionProvider[] = []
  category: Category | null = null
  operations: string[] = []

  loading = false

  get hasCategory(): boolean {
    return this.category !== null
  }

  get hasRuleType(): boolean {
    return this.sharedRuleType !== ''
  }

  get bypassValue(): boolean {
    return this.bypassWithDefaultValue.some(n => n.ruleType === this.sharedRuleType)
  }

  challengeRulesValid = false

  get canSave() {
    return this.challengeRulesValid && this.challenge.basicCompleted && this.challenge.rules.length > 0
  }

  mounted() {
    // If we have any rules, then we're loading an existing challenge 
    if (this.challenge.rules.length > 0) {
      this.loadExisting()
    }
  }

  async loadExisting() {
    // TODO: If we ever introduce non-shared rules, then this needs to change
    // Get the category by the first rule
    const referenceRule = this.challenge.rules[0]
    if (!referenceRule) {
      return
    }
    const provider = referenceRule.promoProvider
    if (!provider) {
      return
    }
    this.loading = true

    const categories = await this.apiClients.servicePromo.getProviderCategories(this.domain)
    if (!categories) {
      this.loading = false
      return
    }

    const catContract = categories.find(c => c.name === provider.category)
    if (!catContract) {
      this.loading = false
      return
    }

    const category = Category.fromContract(catContract, this.domain)
    await this.onCategorySelected(category)

    this.sharedRuleType = referenceRule.activity.name

    //   this.challenge = new Challenge(this.existing.description, )

    //   // Set provider
    //   const provider = await this.getProviderByUrl(this.existing.)

    //   if (provider) {
    //     // Set category
    //     this.category = provider.category
    //     await this.getProvidersByCategory(this.category)

    //     // this.selectedChallenge = this.existing
    //     // Set challenge
    //   }

    //   await this.getChallengeOperations()

    this.loading = false
  }

  onRuleTypeSelected(ruleType: string) {
    // Automatically update values based on selection
    // including any bypasses
    const bypass = this.bypassWithDefaultValue.find(n => n.ruleType === ruleType)
    if (!bypass) {
      this.challenge.sharedValue = ''
      this.challenge.sharedOperation = null
    } else {
      this.challenge.sharedValue = bypass.value
      this.challenge.sharedOperation = bypass.operation
    }
    this.challenge.rules = []
  }

  async onCategorySelected(category: Category) {
    this.reset(category)

    await this.getChallengeOperations()
    await this.getProvidersByCategory(category)
  }

  async getProvidersByCategory(category: Category) {
    this.loading = true

    const providers = await this.apiClients.servicePromo.getProvidersByCategory(this.domain, category.toContract())
    if (providers) {
      this.providers = providers.map((x) => PromotionProvider.fromContract(x, this.domain))
    }


    const set = new Set<string>()
    for (const provider of this.providers) {
      for (const activity of provider.activities) {
        set.add(activity.name)
      }
    }

    this.selectableRuleTypes = Array.from(set)

    this.loading = false
  }

  async getChallengeOperations() {
    this.loading = true
    this.operations = (await this.apiClients.servicePromo.getOperations()) || []
    this.loading = false
  }

  validateChallengeRules() {
    this.challengeRulesValid = this.challenge.checkRulesComplete()
  }

  async onSave() {
    const valid = this.$refs.form.validate()
    if (!valid) {
      return
    }
    this.$emit('save')
    this.reset()
  }

  onCancel() {
    this.$emit('cancel')
    this.reset()
  }

  reset(category: Category | null = null) {
    this.category = category
    this.providers = []
    this.selectableRuleTypes = []
    this.sharedRuleType = ''
    this.isOperationShared = true
    this.loading = false
  }
}
</script>

<style scoped></style>