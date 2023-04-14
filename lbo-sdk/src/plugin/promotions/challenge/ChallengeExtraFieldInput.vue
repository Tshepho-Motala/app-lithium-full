<template>
  <div data-id="ChallengeExtraField"
       class="pb-2">
    <!-- MULTIPLE SELECT BOX -->
    <template v-if="showMultiSelect && multiple">
      <v-autocomplete @change="onMultiChange"
                      outlined
                      v-model="localMultiSelect"
                      :label="label"
                      :items="fieldValueList"
                      item-text="label"
                      item-value="value"
                      :loading="loading"
                      multiple
                      :rules="fieldValue.activityExtraField.required ? rules.required : undefined"
                      :search-input.sync="search"
                      :dense="dense"
                      :hide-details="hideDetails">
      </v-autocomplete>
    </template>

    <!-- SINGLE SELECT BOX -->
    <template v-if="showMultiSelect && !multiple">
      <v-autocomplete @change="onSingleChange"
                      outlined
                      v-model="localSingleSelect"
                      :label="label"
                      :items="fieldValueList"
                      item-text="label"
                      item-value="value"
                      :loading="loading"
                      :rules="fieldValue.activityExtraField.required ? rules.required : undefined"
                      :dense="dense"
                      :hide-details="hideDetails">
      </v-autocomplete>
    </template>

    <template v-if="showText">
      <v-text-field @input="onTextChange"
                    :type="fieldValue.activityExtraField.dataType"
                    outlined
                    v-model="localText"
                    :label="label"
                    :rules="fieldValue.activityExtraField.required ? rules.required : undefined"
                    :dense="dense"
                    :hide-details="hideDetails"></v-text-field>
    </template>
  </div>
</template>

<script lang='ts'>
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import { ExtraFieldValueContract } from '@/core/interface/contract-interfaces/service-promo/ExtraFieldContract'
import { FieldValueListContract } from '@/core/interface/contract-interfaces/service-promo/FieldValueContract'
import { PromotionChallengeProviderContract } from '@/core/interface/contract-interfaces/service-promo/PromotionChallengeContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import RulesMixin from '@/plugin/mixins/RulesMixin'
import { Component, Prop, Inject, VModel, Mixins } from 'vue-property-decorator'

@Component
export default class ChallengeExtraFieldInput extends Mixins(RulesMixin) {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel({ required: true }) fieldValue!: ExtraFieldValueContract

  @Prop({ required: true }) readonly domain!: DomainItemInterface
  @Prop({ required: true }) readonly provider!: PromotionChallengeProviderContract

  @Prop({ required: false, default: false, type: Boolean }) readonly dense!: boolean
  @Prop({ required: false, default: false, type: Boolean }) readonly hideDetails!: boolean

  showMultiSelect = false
  loading = false
  search = ''
  fieldValueList: FieldValueListContract | null = null

  localMultiSelect: string[] = []
  localSingleSelect: string | null = null
  localText: string = ''

  primitive = true
  multiple = false

  get label() {
    let suffix = ''
    if (this.fieldValue.activityExtraField.required) {
      suffix = ' *'
    }
    return this.fieldValue.activityExtraField.name + suffix
  }

  get showText() {
    return !this.showMultiSelect
  }

  mounted() {
    this.doWork()
  }

  async doWork() {
    if (this.fieldValue.activityExtraField.fetchExternalData) {
      await this.getFieldValues()
    }

    const objects = ['singleselect', 'multiselect']

    this.$nextTick(() => {
      this.primitive = !objects.includes(this.fieldValue.activityExtraField.dataType)

      if (this.fieldValue.activityExtraField.fieldType === 'input') {
        this.localText = this.fieldValue.value[0]
      } else if (this.fieldValue.activityExtraField.fieldType === 'singleselect') {
        this.localSingleSelect = this.fieldValue.value[0]
        this.multiple = false
      } else if (this.fieldValue.activityExtraField.fieldType === 'multiselect') {
        this.localMultiSelect = this.fieldValue.value
        this.multiple = true
      }
    })
  }

  async getFieldValues() {
    this.loading = true
    this.fieldValueList = (await this.apiClients.servicePromo.getProviderFieldValues(this.domain, this.provider, this.fieldValue.activityExtraField.name)) || []
    this.loading = false
    this.showMultiSelect = true
  }

  onMultiChange() {
    this.fieldValue.value = []

    for (const multiVal of this.localMultiSelect) {
      this.fieldValue.value.push(multiVal)
    }

    this.search = ''
    this.$emit('change')
  }

  onSingleChange() {
    this.fieldValue.value = [this.localSingleSelect || '']

    this.$emit('change')
  }

  onTextChange() {
    this.fieldValue.value = [this.localText]
    this.$emit('change')
  }

  onChange() {
    this.$emit('change')
  }
}
</script>

<style scoped></style>