<template>
    <v-row data-id="ChallengeRuleMultiSelect">
        <template v-if="domain !== null">
            <v-col cols="3"
                   class="shrink"
                   v-for="i of amountOfRules"
                   :key="`type_${i}`">
                <v-card class="pa-3"
                        outlined>
                    <ChallengeRuleSelect ref="cs"
                                         v-model="rules[i - 1]"
                                         :providers="providers"
                                         :selectableProviders="selectableProviders"
                                         :category="category"
                                         :operations="operations"
                                         :domain="domain"
                                         :isOperationShared="isOperationShared"
                                         :operation="operation"
                                         :operationValue="operationValue"
                                         :selectableRuleTypes="selectableRuleTypes"
                                         :sharedRuleType="sharedRuleType"
                                         @change="$emit('change')" />
                </v-card>
            </v-col>
            <v-col v-if="showAddSingle"
                   cols="3"
                   class="shrink align-center justify-space-around d-flex px-0">
                <div>
                    <v-card ripple
                            width="200"
                            style="border: 1px #ccc dashed"
                            @click="onAdd">
                        <v-card-text>
                            <div class="px-8 d-flex flex-column justify-center align-center rounded">
                                <div>
                                    <v-icon small>mdi-plus</v-icon>
                                    <v-icon large>mdi-flag-plus</v-icon>
                                </div>
                                <div class="text-center">
                                    <span class="text-subtitle-2"> Add Provider </span>
                                </div>
                            </div>
                        </v-card-text>
                    </v-card>
                </div>
            </v-col>
            <v-col v-if="showAddAll"
                   cols="3"
                   class="shrink align-center justify-space-around d-flex">
                <div>
                    <v-card ripple
                            @click="onAddAll">
                        <v-card-text>
                            <div class="px-8 d-flex flex-column justify-center align-center rounded">
                                <div>
                                    <v-icon small>mdi-plus</v-icon>
                                    <v-icon large>mdi-flag-plus-outline</v-icon>
                                </div>
                                <div class="text-center">
                                    <span class="text-subtitle-2"
                                          v-text="btnAddAllText"> </span>
                                </div>
                            </div>
                        </v-card-text>
                    </v-card>
                </div>
            </v-col>
        </template>
    </v-row>
</template>

<script lang='ts'>
import { PromotionChallengeRuleContract } from '@/core/interface/contract-interfaces/service-promo/PromotionChallengeContract';
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem';
import Category from '@/plugin/components/Category';
import PromotionProvider from '@/plugin/components/provider-selectors/PromotionProvider';
import { Vue, Component, VModel, Prop, Watch } from 'vue-property-decorator'
import ChallengeRuleSelect from './ChallengeRuleSelect.vue';

@Component({
    components: {
        ChallengeRuleSelect
    }
})
export default class ChallengeRuleMultiSelect extends Vue {
    @VModel({ required: true, type: Array }) rules!: PromotionChallengeRuleContract[]

    @Prop({ required: true }) readonly selectableRuleTypes!: string[]

    @Prop({ required: true }) readonly providers!: PromotionProvider[]
    @Prop({ required: true }) readonly category!: Category

    @Prop({ required: false, default: false, type: Boolean }) readonly isOperationShared!: boolean // True if the values for this challenge should be pulled by the shared values
    @Prop({ required: false, type: String }) readonly operation!: string
    @Prop({ required: false }) readonly operationValue!: any
    @Prop({ required: true }) readonly sharedRuleType!: string

    @Prop({ required: false, default: () => [] }) readonly operations!: String[]
    @Prop({ required: true }) readonly domain!: DomainItemInterface

    get amountOfRules(): number {
        return this.rules.length
    }

    get btnAddAllText() {
        return 'Set Providers'
        // const available = this.providers.length
        // const remaining = this.selectableProviders.length
        // if (available === remaining && this.rules.length === 0) {
        //     return "All Providers"
        // } else if (this.selectableProviders.length - this.rules.length > 1) {
        //     return "Remining Providers"
        // } else {
        //     return "Remining Provider"
        // }
    }


    get showAddAll() {
        if (this.selectableProviders.length > 0) {
            return this.rules.length < this.selectableProviders.length
        }
        return true
    }

    // TODO: We need to determine if providers have subproviders
    // For now we can just allow the user to keep on adding providers
    allowAlwaysAddSingle = true

    get showAddSingle() {
        if (this.allowAlwaysAddSingle) {
            return true
        }
        return this.selectableProviders.length - this.rules.length > 1
    }

    get selectableProviders() {
        if (this.allowAlwaysAddSingle) {
            return this.providers
        }
        return this.providers.filter(p => !this.rules.some(c => c.promoProvider && c.promoProvider.url === p.url))
    }

    // For the future when we want to default
    // mounted() {
    //     this.addDefaultIfNoRules()
    // }

    // @Watch('sharedRuleType')
    // addDefaultIfNoRules() {
    //     if (this.rules.length === 0) {
    //         if (this.providers.length === 1) {
    //             this.onAddAll()
    //         }
    //     }
    // }


    onAdd() {
        this.add()
        this.$emit('change')
    }
    
    onAddAll() {
        // Remove any without provider
        this.rules = this.rules.filter(x => !!x.promoProvider && !!x.promoProvider.url)

        this.$nextTick(() => {
            for (const remaining of this.selectableProviders) {
                this.add(remaining.url)
            }
            this.$emit('change')
        })
    }

    add(providerUrl = '') {
        this.rules.push({
            activity: {
                name: this.sharedRuleType,
            },
            operation: this.operation,
            value: this.operationValue,
            promoProvider: {
                category: this.category.name,
                url: providerUrl
            },
            activityExtraFieldRuleValues: []
        })
    }
}
</script>

<style scoped></style>