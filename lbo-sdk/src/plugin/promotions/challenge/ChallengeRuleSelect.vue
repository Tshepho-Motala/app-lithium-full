<template>
    <div data-id='ChallengeRuleSelect'>
        <template v-if="!!rule && !!selectedProvider">
            <div class="text-center pb-2">
                <span class="text-caption"
                      v-text="selectedProvider.url"></span>
            </div>
        </template>
        <template v-else>
            <v-select @change="onProviderSelected"
                      :rules="rules.required"
                      outlined
                      :items="providerSelect"
                      v-model="selectedProvider"
                      label="Select Provider *"
                      item-text="name"
                      item-value="id"
                      return-object
                      placeholder="Required"></v-select>
        </template>

        <!-- <template v-if="!!provider">
                                                        <div class="pb-2">
                                                            <v-select @change="onChallengeSelected"
                                                                        :rules="rules.required"
                                                                        outlined
                                                                        :items="challengeSelect"
                                                                        v-model="selectedChallenge"
                                                                        label="Select Challenge *"
                                                                        item-text="name"
                                                                        item-value="id"
                                                                        return-object
                                                                        placeholder="Required"
                                                                        dense
                                                                        hide-details
                                                                        disabled></v-select>
                                                        </div>
                                                    </template> -->

        <template v-if="!!rule">
            <!-- 
                                                        <div v-if="challenge.requiresValue && !sharedOperation">
                                                            <div class="pb-2">
                                                                <v-text-field @input="checkChallengeComplete"
                                                                                :rules="rules.required"
                                                                                prepend-inner-icon="mdi-link"
                                                                                hide-details
                                                                                outlined
                                                                                v-model="challenge.value"
                                                                                label="Value *"
                                                                                placeholder="Required"
                                                                                dense
                                                                                :disabled="sharedOperation"></v-text-field>
                                                            </div>
                                                            <div class="pb-2">
                                                                <v-select @change="checkChallengeComplete"
                                                                            :rules="rules.required"
                                                                            prepend-inner-icon="mdi-link"
                                                                            outlined
                                                                            v-model="challenge.valueOperation"
                                                                            :items="operations"
                                                                            label="Select Operation *"
                                                                            placeholder="Required"
                                                                            dense
                                                                            hide-details
                                                                            :disabled="sharedOperation"></v-select>
                                                            </div>
                                                        </div> 
                                                    -->
            <template v-if="rule.promoProvider">
                <template v-for="(field, i) in rule.activityExtraFieldRuleValues">
                    <ChallengeExtraFieldInput @change="$emit('change')"
                                              :key="`input_${i}`"
                                              :domain="domain"
                                              :provider="rule.promoProvider"
                                              v-model="rule.activityExtraFieldRuleValues[i]"
                                              dense
                                              hideDetails />
                </template>
            </template>
        </template>
    </div>
</template>     

<script lang='ts'>
import PromotionProvider from '@/plugin/components/provider-selectors/PromotionProvider';
import { Component, Prop, Mixins, Watch, VModel } from 'vue-property-decorator'
import RulesMixin from '@/plugin/mixins/RulesMixin'
import Category from '@/plugin/components/Category';
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem';
import ChallengeExtraFieldInput from './ChallengeExtraFieldInput.vue'
import { PromotionChallengeRuleContract } from '@/core/interface/contract-interfaces/service-promo/PromotionChallengeContract';

@Component({
    components: {
        ChallengeExtraFieldInput
    }
})
export default class ChallengeRuleSelect extends Mixins(RulesMixin) {
    @VModel({ required: true }) readonly rule!: PromotionChallengeRuleContract

    @Prop({ required: true }) readonly providers!: PromotionProvider[]
    @Prop({ required: true }) readonly selectableProviders!: PromotionProvider[]
    @Prop({ required: true }) readonly category!: Category

    @Prop({ required: true }) selectableRuleTypes!: string[]
    @Prop({ required: true }) readonly sharedRuleType!: string

    @Prop({ required: false, default: false, type: Boolean }) readonly isOperationShared!: boolean // True if the values for this challenge should be pulled by the shared values
    @Prop({ required: false, type: String }) readonly operation!: string
    @Prop({ required: false }) readonly operationValue!: any

    @Prop({ required: false, default: () => [] }) readonly operations!: String[]
    @Prop({ required: true }) readonly domain!: DomainItemInterface

    selectedProvider: { url: string } | null = null
    selectedChallenge: { name: string, id: string } | null = null


    get providersWithChallenge() {
        return this.selectableProviders.filter(p => p.activities.some(a => a.name === this.rule.activity.name))
    }

    get providerSelect(): { name: string, url: string }[] {
        return this.providersWithChallenge.map((p) => { return { name: p.name, url: p.url } })
    }

    mounted() {
        if (this.rule.promoProvider?.url) {
            this.onProviderSelected({
                url: this.rule.promoProvider.url
            })
        }
    }

    onProviderSelected(iProvider: { url: string }) {
        this.selectedProvider = iProvider
        this.rule.promoProvider = {
            url: iProvider.url,
            category: this.category.name
        }
        const provider = this.providers.find(p => p.url === iProvider.url) || null
        if (provider === null) {
            return
        }
        const activity = provider.activities.find(a => a.name === this.sharedRuleType) || null
        if (activity === null) {
            return
        }

        for (const field of activity.extraFields) {
            const existingField = this.rule.activityExtraFieldRuleValues.find(f => f.activityExtraField.name === field.name)

            if (!existingField) {
                this.rule.activityExtraFieldRuleValues.push({
                    activityExtraField: field.toContract(),
                    id: 1,
                    value: []
                })
            }
        }

        this.syncRuleOperation()
    }

    @Watch('operation')
    @Watch('operationValue')
    @Watch('isOperationShared')
    syncRuleOperation() {
        if (!!this.rule) {
            if (this.isOperationShared) {
                this.rule.value = parseInt(this.operationValue)
                this.rule.operation = this.operation
            } else {
                this.rule.value = null
                this.rule.operation = ''
            }
        }

        this.$emit('change')
    }
}
</script>

<style scoped></style>