<template>
  <v-expansion-panel data-test-id="cashier-processor" v-if="processor" :class="isDisabled ? 'method-disabled-shadow' : ''" class="mb-1">
    <v-expansion-panel-header ripple="true">
      <div class="d-flex justify-space-between mr-5">
        <div class="d-flex">
          <div class="mr-5">
            <p>{{ processor.description }}</p>
            <p class="text-muted">{{ processor.description }}</p>
          </div>
          <div v-if="processor && processor.default">
            <p class="primary mr-2 px-3 py-2 rounded">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.DEFAULT')}}</p>
          </div>
          <div class="d-flex pt-2">
            <span class="pa-2 mr-2 red white--text missing-limits-mark border-radius-5"
                  v-if="!(processor.limits && processor.domainLimits)">
              {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.MISSING_LIMITS')}}
            </span>
            <span class="override-icon domain" v-if="processor.domainLimits">
              <v-icon>mdi-tag-multiple</v-icon>
            </span>
            <span class="override-icon global" v-if="processor.limits || processor.fees">
              <v-icon>mdi-tag-multiple</v-icon>
            </span>
            <span class="override-icon profile" v-if="processor.dmpp">

              <v-icon>mdi-tag-multiple</v-icon>
            </span>
            <span class="override-icon individual" v-if="processor.dmpu">
              <v-icon>mdi-tag-multiple</v-icon>
            </span>
          </div>
        </div>
        <div class="d-flex align-center">
          <table data-test-id="tbl-processor-accounting" class="table table-sm description-sm mb-0" v-if="processor.accountingDay || processor.accountingWeek || processor.accountingMonth || processor.accountingLastMonth">
            <tbody>
            <tr>
              <td data-test-id="tbi-processor-accounting-general" class="domain d-flex flex-column">
                <div class="bold-text">{{$translate(`UI_NETWORK_ADMIN.CASHIER_CONFIG.DOMAIN_${getMethodType()}_TOTALS`)}}</div>
                <span class="description-sm" v-if="processor.accountingDay">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_TODAY')}} ({{processor.accountingDay.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingDay.debitCents : processor.accountingDay.creditCents)}}
                </span>
                <span class="description-sm" v-if="processor.accountingWeek">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_WEEK')}} ({{processor.accountingWeek.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingWeek.debitCents : processor.accountingWeek.creditCents)}}
                </span>
                <span class="description-sm" v-if="processor.accountingMonth">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_MONTH')}} ({{processor.accountingMonth.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingMonth.debitCents : processor.accountingMonth.creditCents)}}
                </span>
                <span class="description-sm" v-if="processor.accountingLastMonth">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_LAST_MONTH')}} ({{processor.accountingLastMonth.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingLastMonth.debitCents : processor.accountingLastMonth.creditCents)}}
                </span>
              </td>
              <td data-test-id="tbi-processor-accounting-user" class="user d-flex flex-column" v-if="viewAs === 'user'">
                <div class="bold-text">{{$translate(`UI_NETWORK_ADMIN.CASHIER_CONFIG.DOMAIN_${getMethodType()}_TOTALS`)}}</div>
                <span v-if="processor.accountingUserDay">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_TODAY')}} ({{processor.accountingUserDay.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingUserDay.debitCents : processor.accountingUserDay.creditCents)}}
                </span>
                <span v-if="processor.accountingUserWeek">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_WEEK')}} ({{processor.accountingUserWeek.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingUserWeek.debitCents : processor.accountingUserWeek.creditCents)}}
                </span>
                <span v-if="processor.accountingUserMonth">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_MONTH')}} ({{processor.accountingUserMonth.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingUserMonth.debitCents : processor.accountingUserMonth.creditCents)}}
                </span>
                <span v-if="processor.accountingUserLastMonth">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_LAST_MONTH')}} ({{processor.accountingUserLastMonth.tranCount}}):
                  {{getCurrencyValue(type === 'deposit' ? processor.accountingUserLastMonth.debitCents : processor.accountingUserLastMonth.creditCents)}}
                </span>
              </td>
            </tr>
            </tbody>
          </table>
          <v-menu offset-y :close-on-click="true" :close-on-content-click="true">
            <template v-slot:activator="{ on }">
              <v-btn v-on="on" class="ma-4 mb-5 border-none" outlined data-test-id="btn-toggle-processor-menu">
                <v-icon>mdi-dots-vertical</v-icon>
              </v-btn>
            </template>
            <v-card class="py-4">
              <div v-if="!user && !profile">
                <v-dialog
                    transition="dialog-top-transition"
                    max-width="600"
                    v-model="modifyDialog"
                    retain-focus="false"
                    v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                        data-test-id="btn-show-edit-processor"
                        outlined
                        v-bind="attrs"
                        v-on="on"
                        class="mx-3 border-none"
                        @click="getAccessRules(domain)"
                    >{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.MENU.EDITPROCESSOR')}}
                    </v-btn>
                  </template>
                  <v-card>
                    <v-toolbar
                        color="primary"
                        dark
                    ></v-toolbar>
                    <v-card-text>
                      <div>
                        <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.TITLE')}}</p>
                        <v-text-field
                            data-test-id="txt-modified-processor-name"
                            v-model="modifiedProcessorName"
                            dense
                            filled
                            readonly
                            hide-details
                        />
                      </div>
                      <div>
                        <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.NAME')}}</p>
                        <v-text-field
                            data-test-id="txt-modified-processor"
                            v-model="modifiedProcessor"
                            dense
                            filled
                            readonly
                            hide-details
                        />
                      </div>
                      <div>
                        <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.NAME')}}</p>
                        <v-text-field
                            data-test-id="txt-modified-processor-description"
                            v-model="modifiedProcessorDescription"
                            outlined
                            dense
                            hide-details
                            :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.PLACEHOLDER')"
                        />
                        <p class="text-muted">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.DESCRIPTION')}}</p>
                      </div>
                      <div>
                        <p>
                          {{$translate('GLOBAL.ACCESSRULE.LABEL')}}<span>({{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.EVALUATED_PRIOR_PROCESSOR')}})</span>
                        </p>
                        <v-autocomplete
                            data-test-id="slt-modified-rule-processor"
                            v-model="modifiedAccessRuleProcessor"
                            :items="accessRules"
                            item-text="name"
                            item-value="name"
                            return-object
                            outlined
                            dense
                            clearable
                            :placeholder="$translate('GLOBAL.ACCESSRULE.PLACEHOLDER')"
                            hide-details
                        />
                        <p class="text-muted">{{$translate('GLOBAL.ACCESSRULE.DESCRIPTION')}}</p>
                      </div>
                      <div>
                        <p>
                          {{$translate('GLOBAL.ACCESSRULE.LABEL')}}<span>({{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.EVALUATED_PRIOR')}})</span>
                        </p>
                        <v-autocomplete
                            data-test-id="txt-modified-rule-transition"
                            v-model="modifiedAccessRuleTransition"
                            :items="accessRules"
                            item-text="name"
                            item-value="name"
                            return-object
                            outlined
                            dense
                            clearable
                            :placeholder="$translate('GLOBAL.ACCESSRULE.PLACEHOLDER')"
                            hide-details
                        />
                        <p class="text-muted">{{$translate('GLOBAL.ACCESSRULE.DESCRIPTION')}}</p>
                      </div>
                      <div class="d-flex" v-if="type !== 'deposit'">
                        <v-checkbox
                            data-test-id="chk-reserve-funds"
                            v-model="reserveFundsOnWithdrawal"
                            :label="$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.RESERVEFUNDSONWITHDRAWAL.NAME')"
                        ></v-checkbox>
                      </div>
                    </v-card-text>
                    <v-card-actions class="justify-end">
                      <v-btn
                          data-test-id="btn-save-modified-processor"
                          text
                          :disabled="false"
                          @click="saveModifiedProcessor(tab.type)"
                      >
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SAVE')}}
                      </v-btn>
                      <v-btn
                          data-test-id="btn-close-modify-processor"
                          text
                          @click="modifyDialog = false"
                      >
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-dialog>
              </div>
              <div>
                <v-btn v-if="isDisabled"
                       :data-test-id="`btn-enable-processor${dataTestName}`"
                       @click="changeProcessorStatus(tab.type)"
                       class="mx-3 mb-0 border-none"
                       v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                       outlined>
                  {{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.MENU.ENABLEPROCESSOR')}}
                </v-btn>
                <v-btn v-else
                       :data-test-id="`btn-disable-processor${dataTestName}`"
                       @click="changeProcessorStatus(tab.type)"
                       class="mx-3 mb-0 border-none"
                       v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                       outlined>
                  {{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.MENU.DISABLEPROCESSOR')}}
                </v-btn>
              </div>
              <div v-if="!user && !profile">
                <v-dialog
                    transition="dialog-top-transition"
                    max-width="800"
                    v-model="propertiesDialog"
                    retain-focus="false"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                        outlined
                        data-test-id="btn-toggle-properties-dialog"
                        v-bind="attrs"
                        v-on="on"
                        class="mx-3 border-none"
                        v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                        @click="getProperties(processor.id)"
                    >{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.PROPERTIES')}}
                    </v-btn>
                  </template>
                  <v-card>
                    <v-toolbar
                        color="primary"
                        dark
                    >{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.PROPERTIES.TITLE')}}
                      {{ processor.processor ? processor.processor.code : ''}}
                    </v-toolbar>
                    <v-card-text>
                      <v-text-field
                          data-test-id="txt-searched-property"
                          v-model="propertiesSearchedValue"
                          outlined
                          dense
                          hide-details
                          class="my-3"
                          label="Search"
                      />
                      <div class="d-flex">
                        <div class="font-weight-bold w-10">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.PROPERTIES.TABLE.OVERRIDE')}}
                        </div>
                        <div class="font-weight-bold w-30">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.PROPERTIES.TABLE.NAME')}}
                        </div>
                        <div class="font-weight-bold w-60">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.PROPERTIES.TABLE.VALUE')}}
                        </div>
                      </div>
                      <v-divider/>
                      <div v-for="(prop, index) in filteredProcessorProperties" :key="index">
                        <div class="d-flex">
                          <div class="w-10">
                            <v-checkbox
                                data-test-id="chk-property-override"
                                v-model="prop.override"
                                class="mt-0 pt-0"
                                color="primary"
                                hide-details
                            ></v-checkbox>
                          </div>
                          <div class="w-30">
                            <p>{{prop['processorProperty']['name']}}</p>
                          </div>
                          <div class="w-60">
                            <v-text-field
                                data-test-id="txt-property-value"
                                v-model="prop['value']"
                                outlined
                                dense
                                v-if="prop.override"
                                transition="scale-transition"
                            />
                            <v-text-field
                                data-test-id="txt-property-default"
                                :value="prop['processorProperty']['defaultValue']"
                                outlined
                                dense
                                disabled
                            />
                            <p class="ma-0">{{prop['processorProperty']['description']}}</p>
                          </div>
                        </div>
                        <v-divider/>
                      </div>
                    </v-card-text>
                    <v-card-actions class="justify-end">
                      <v-btn
                          text
                          data-test-id="btn-save-properties"
                          :disabled="false"
                          @click="saveProperties"
                      >
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SAVE')}}
                      </v-btn>
                      <v-btn
                          text
                          data-test-id="btn-hide-properties-dialog"
                          @click="propertiesDialog = false"
                      >
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-dialog>
              </div>
              <div>
                <v-btn
                    data-test-id="btn-show-help"
                    @click="showHelp"
                    class="mx-3 mb-0 border-none"
                    v-if="!helpIsVisible"
                    v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                    outlined>
                  {{$translate('UI_NETWORK_ADMIN.CASHIER.SHOW_HELP')}}
                </v-btn>
                <v-btn
                    data-test-id="btn-hide-help"
                    @click="hideHelp"
                    class="mx-3 mb-0 border-none"
                    v-if="helpIsVisible"
                    v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                    outlined>
                  {{$translate('UI_NETWORK_ADMIN.CASHIER.HIDE_HELP')}}
                </v-btn>
              </div>
              <div v-if="!user && !profile">
                <v-dialog
                    transition="dialog-top-transition"
                    max-width="600"
                    v-model="deleteDialog"
                    v-show="hasRoleForDomain('CASHIER_CONFIG_DELETE', domain)"
                    retain-focus="false"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                        data-test-id="btn-show-delete-dialog"
                        outlined
                        v-bind="attrs"
                        v-on="on"
                        class="mx-3 mb-0 border-none"
                    >{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.DELETE')}}
                    </v-btn>
                  </template>
                  <v-card>
                    <v-toolbar
                        dark
                        class="red darken-4"
                    >
                      <v-icon>mdi-delete-outline</v-icon>
                      {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.DELETE')}}
                    </v-toolbar>
                    <v-card-text>
                      <p class="mt-3 font-weight-bold red--text">
                        {{ $translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.DELETE.WARNING') }}</p>
                      <p class="mb-3">{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.DELETE.WARNING2') }}</p>
                      <v-text-field
                          data-test-id="txt-confirm-delete-text"
                          v-model="deleteConfirm"
                          outlined
                          dense
                          :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.TYPE_DELETE')"
                      ></v-text-field>
                    </v-card-text>
                    <v-card-actions class="justify-end">
                      <v-btn
                          data-test-id="btn-confirm-delete"
                          text
                          :disabled="deleteConfirm != 'DELETE'"
                          @click="deleteProcessor"
                          class="red"
                      >
                        <v-icon>mdi-delete-outline</v-icon>
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.DELETE')}}
                      </v-btn>
                      <v-btn
                          data-test-id="btn-hide-delete-dialog"
                          text
                          @click="deleteDialog = false"
                      >{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-dialog>
              </div>
            </v-card>
          </v-menu>
        </div>
      </div>
    </v-expansion-panel-header>
    <v-expansion-panel-content>
      <v-divider></v-divider>
      <v-tabs
          v-model="activeTab"
          centered
          slider-color="yellow"
          class="method-tabs"
          grow
      >
        <v-tab
            v-for="(tab, index)  in processorTabs"
            :key="index"
            v-show="hasRoleForDomain(tab.roles, domain)"
            class="processor-tab"
        >
          <div class="d-flex justify-space-between w-100">
            <p class="d-flex align-center justify-center mb-0">{{ $translate(tab.title) }}</p>
            <div class="d-flex align-center">
              <span class="override-icon domain black--text" v-if="processor.limits">
                <v-icon>mdi-tag-multiple</v-icon>
              </span>
              <span class="override-icon global black--text" v-if="processor.fees || processor.limits">
                <v-icon>mdi-tag-multiple</v-icon>
              </span>
              <span class="override-icon profile  black--text" v-if="processor.dmpp && ( processor.dmpp.fees || processor.dmpp.limits)">
                <v-icon>mdi-tag-multiple</v-icon>
              </span>
              <span class="override-icon individual black--text" v-if="processor.dmpu && ( processor.dmpu.fees || processor.dmpu.limits)">
                <v-icon>mdi-tag-multiple</v-icon>
              </span>
            </div>
            <v-menu offset-y :close-on-click="true" :close-on-content-click="true">
              <template v-slot:activator="{ on }">
                <v-btn
                    v-on="on"
                    :data-test-id="`btn-open-menu-${tab.type}`"
                    class="ma-4 mb-5 border-none"
                    outlined>
                  <v-icon>mdi-dots-vertical</v-icon>
                </v-btn>
              </template>
              <v-card class="py-4">
                <div class="d-flex flex-column">
                  <template >
                    <v-btn
                        :data-test-id="`btn-save-overrides${dataTestName}`"
                        @click="saveOverrides(tab.type)"
                        class="mx-3 mb-0 border-none"
                        outlined
                        v-show="hasRoleForDomain('CASHIER_CONFIG_ADD,CASHIER_CONFIG_EDIT', domain)"
                        v-if="showDeleteButton(tab.type)">
                      {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SAVE_OVERRIDES')}}
                    </v-btn>
                    <v-btn
                        :data-test-id="`btn-override${dataTestName}`"
                        @click="override(tab.type)"
                        class="mx-3 mb-0 border-none"
                        outlined
                        v-show="hasRoleForDomain('CASHIER_CONFIG_ADD,CASHIER_CONFIG_EDIT', domain)"
                        v-else>
                      {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OVERRIDE')}}
                    </v-btn>
                    <v-btn
                        :data-test-id="`btn-delete-${tab.type}${dataTestName}`"
                        @click="deleteFeesLimits(tab.type)"
                        class="mx-3 mb-0 border-none"
                        v-if="showDeleteButton(tab.type)"
                        v-show="hasRoleForDomain('CASHIER_CONFIG_DELETE', domain)"
                        outlined>
                      {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.DELETE')}}
                    </v-btn>
                  </template>
                </div>
              </v-card>
            </v-menu>
          </div>
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="activeTab">
        <v-tab-item
            v-for="(tab, index)  in processorTabs"
            :key="index"
        >
          <v-card flat v-if="tab.type === 'fees'">
            <div v-if="showFees">
              <fees-tab
                  :processor="processor"
                  :groups="inputGroups"
                  :currency="currency"
                  @change="getDataFromInputs"
                  :tab="tab.type"
                  :viewAs="viewAs"
                  :help="helpIsVisible"
                  @calculate="calculateFe"
                  :perc-amount = "perc_amount"
                  :flatDec = "flatDec"
                  :minimumDec = "minimumDec"
                  :fee-amount = "fee_amount"
                  :total-amount = "total_amount"
                  @changeStrategy = "changeStrategy"
                  :type="type"
              />
            </div>
            <div v-else class="pa-4">
              <p class="text-muted d-flex justify-center">{{$translate('UI_NETWORK_ADMIN.CASHIER.FEES_NOT_SET')}}</p>
              <v-divider></v-divider>
              <div class="d-flex justify-center">
                <v-icon dark>
                  mdi-plus-thick
                </v-icon>
                <v-btn
                    @click="override(tab.type)"
                    class="teal lighten-3"
                    v-show="hasRole('CASHIER_CONFIG,CASHIER_CONFIG_ADD,CASHIER_CONFIG_EDIT')"
                    :data-test-id="`btn-${tab.type}-override`">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OVERRIDE')}}
                </v-btn>
              </div>
            </div>
          </v-card>
          <v-card flat v-if="tab.type === 'limits'">
            <div v-if="showLimits">
              <limits-tab
                  :groups="inputGroups"
                  :currency="currency"
                  @change="getDataFromInputs"
                  :tab="tab.type"
                  :viewAs="viewAs"
                  :help="helpIsVisible"
                  :type="type"
              />
            </div>
            <div v-else class="pa-4">
              <p class="text-muted d-flex justify-center">{{$translate('UI_NETWORK_ADMIN.CASHIER.LIMITS_NOT_SET')}}</p>
              <v-divider></v-divider>
              <div class="d-flex justify-center">
                <v-icon dark>
                  mdi-plus-thick
                </v-icon>
                <v-btn
                    @click="override(tab.type)"
                    class="teal lighten-3"
                    :data-test-id="`btn-${tab.type}-override`">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OVERRIDE')}}
                </v-btn>
              </div>
            </div>
          </v-card>
        </v-tab-item>
      </v-tabs-items>
      <v-expansion-panels>
        <v-expansion-panel class="mt-5">
          <v-expansion-panel-header>
            <div class="d-flex flex-column">
              <h2>{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGELOG')}}</h2>
              <p class="text-muted">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.VIEW_CHANGE_HISTORY_PROCESSOR')}}</p>
              <v-divider/>
            </div>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGE_HISTORY')}}</p>
            <div v-for="(log,index) in changeLogs" :key="index">
              <change-log :log="log"/>
            </div>
            <div class="d-flex justify-content-start">
              <div class="d-flex align-center justify-center pa-2 changelog-refresh-btn">
                <v-icon>
                  mdi-clock-outline
                </v-icon>
              </div>
              <v-btn
                  data-test-id="btn-load-more"
                  @click="loadMore(false)"
                  class="blue--text ml-4 border-none"
              >{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.LOADMORE')}}
              </v-btn>
            </div>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>

<script lang="ts">
import {Component, Inject, Prop, Mixins, Watch} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import ChangeLog from "@/plugin/cashier/config/components/ChangeLog.vue";
import CashierConfigInputGroup from "@/plugin/cashier/config/components/CashierConfigInputGroup.vue";
import FeesTab from "@/plugin/cashier/config/components/FeesTab.vue";
import LimitsTab from "@/plugin/cashier/config/components/LimitsTab.vue";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import {
  AccessRuleInterface, CashierConfigChangelog,
  CashierConfigInputChangesInterface,
  CashierConfigInputGroupInterface,
  CashierConfigProcessor,
  CashierConfigProcessorProperty, CashierConfigProfile,
  CashierConfigUser, ProcessorTabInterface,
  StrategyOptionInterface
} from "@/core/interface/cashierConfig/CashierConfigInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component({
  components: {
    ChangeLog,
    CashierConfigInputGroup,
    FeesTab,
    LimitsTab
  }
})
export default class CashierProcessor extends Mixins(TranslationMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  @Prop() propsProcessor!: CashierConfigProcessor
  @Prop({default: '$'}) currency!: string
  @Prop({default: ''}) viewAs!: string
  @Prop() user?: CashierConfigUser
  @Prop() profile?: CashierConfigProfile
  @Prop({default: ''}) domain!: string
  @Prop() type!: string

  processorTabs: ProcessorTabInterface[] = [
    {type: 'fees', title: "UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.TITLE", roles: "ADMIN,CASHIER_CONFIG,CASHIER_CONFIG_VIEW"},
    {type: 'limits', title: "UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.TITLE", roles: "ADMIN,CASHIER_CONFIG,CASHIER_CONFIG_VIEW"}
  ]

  changeLogs: CashierConfigChangelog[] = []
  changeLogsPage: number = 0
  processor: CashierConfigProcessor | null = null
  modifyDialog: boolean = false
  propertiesDialog: boolean = false
  helpIsVisible: boolean = false
  activeTab: boolean = false
  processorProperties: CashierConfigProcessorProperty[] = []
  propertiesSearchedValue: string = ''
  deleteDialog: boolean = false
  deleteConfirm: string = ''
  showFees: boolean = false
  showLimits: boolean = false
  reserveFundsOnWithdrawal: boolean = false
  accessRules: AccessRuleInterface[] = []
  deposit_amount: number = 150
  hasMore: boolean = false

  //computed properties
  perc: number | null = null
  perc_amount: number | null = null
  flatDec: number | null = null
  minimumDec: number | null = null
  fee_amount: number | null = null
  total_amount: number | null = null

  // modify data
  modifiedProcessorName: string | null = null
  modifiedProcessor: string | null = null
  modifiedProcessorDescription: string | null = null
  modifiedAccessRuleProcessor: AccessRuleInterface | null = null
  modifiedAccessRuleTransition: AccessRuleInterface | null = null

  inputGroups: CashierConfigInputGroupInterface = {
    flatFee: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.FLAT.NAME'),
      gValue: null,// global value
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.GPF.TITLE'),//global text
      gHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.GPF.DESC_FLAT'),//global hint
      gPlaceholder: `E.g ${this.currency}20.00`,//global placeholder
      showDomain: false,//show or not domain inputs
      dValue: null,//domain value
      dText: '',//domain text
      dHint: '',//domain hint
      dPlaceholder: '',//domain placeholder
      showProfile: false,//show or not profile inputs
      pValue: null,//profile value
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.PF.TITLE'),//profile text
      pHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.PF.DESC_FLAT'),//profile hint
      pPlaceholder: `E.g ${this.currency}20.00`,//profile placeholder
      showUser: false,//show or not user inputs
      uValue: null,//user value
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.IPF.TITLE'),//user text
      uHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.IPF.DESC_FLAT'),//user hint
      uPlaceholder: `E.g ${this.currency}20.00`//user placeholder
    },
    percentageFee: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.PERCENTAGE.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.GPF.TITLE'),
      gHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.GPF.DESC_PERC'),
      gPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.PERCENTAGE.PLACEHOLDER'),
      showDomain: false,
      dValue: null,
      dText: '',
      dHint: '',
      dPlaceholder: '',
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.PF.TITLE'),
      pHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.PF.DESC_PERC'),
      pPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.PERCENTAGE.PLACEHOLDER'),
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.IPF.TITLE'),
      uHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.IPF.DESC_PERC'),
      uPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.PERCENTAGE.PLACEHOLDER')
    },
    minimumFee: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.MINIMUM.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.GPF.TITLE'),
      gHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.GPF.DESC_MIN'),
      gPlaceholder: `E.g ${this.currency}15.50`,
      showDomain: false,
      dValue: null,
      dText: '',
      dHint: '',
      dPlaceholder: '',
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.PF.TITLE'),
      pHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.PF.DESC_MIN'),
      pPlaceholder: `E.g ${this.currency}15.50`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.IPF.TITLE'),
      uHint: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.FEES.IPF.DESC_MIN'),
      uPlaceholder: `E.g ${this.currency}15.50`
    },
    firstMinLimit: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MIN_FIRST_AMOUNT.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.MIN_AMT`),
      gPlaceholder: `E.g ${this.currency}15.50`,
      showDomain: false,
      dValue: null,
      dText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.MIN_AMT`),
      dPlaceholder: '',
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.MIN_AMT`),
      pPlaceholder: `E.g ${this.currency}15.50`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.MIN_AMT`),
      uPlaceholder: `E.g ${this.currency}15.50`
    },
    firstMaxLimit: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAX_FIRST_AMOUNT.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.MIN_AMT`),
      gPlaceholder: `E.g ${this.currency}15.50`,
      showDomain: false,
      dValue: null,
      dText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.MIN_AMT`),
      dPlaceholder: '',
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.MIN_AMT`),
      pPlaceholder: `E.g ${this.currency}15.50`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.MIN_AMT`),
      uPlaceholder: `E.g ${this.currency}15.50`
    },
    minimumLimit: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MINAMOUNT.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.MIN_AMT`),
      gPlaceholder: `E.g ${this.currency}15.50`,
      showDomain: false,
      dValue: null,
      dText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.MIN_AMT`),
      dPlaceholder: '',
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.MIN_AMT`),
      pPlaceholder: `E.g ${this.currency}15.50`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.MIN_AMT`),
      uPlaceholder: `E.g ${this.currency}15.50`
    },
    maximumLimit: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXAMOUNT.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.MAX_AMT`),
      gPlaceholder: `E.g ${this.currency}200.00`,
      showDomain: false,
      dValue: null,
      dText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.MAX_AMT`),
      dPlaceholder: '',
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.MAX_AMT`),
      pPlaceholder: `E.g ${this.currency}200.00`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.MAX_AMT`),
      uPlaceholder: `E.g ${this.currency}200.00`
    },
    maxDay: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXAMOUNTPERDAY.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.MAX_AMT_PD`),
      gPlaceholder: `E.g ${this.currency}50.00`,
      showDomain: true,
      dValue: null,
      dText: (this.processor && this.processor.domainLimits) ? this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.DL') : this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: (this.processor && this.processor.domainLimits) ? this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.DL.MAX_AMT_PD`) : this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.MAX_AMT_PD`),
      dPlaceholder: `E.g ${this.currency}50.00`,
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.MAX_AMT_PD`),
      pPlaceholder: `E.g ${this.currency}50.00`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.MAX_AMT_PD`),
      uPlaceholder: `E.g ${this.currency}50.00`
    },
    maxWeek: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXAMOUNTPERWEEK.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.MAX_AMT_PW`),
      gPlaceholder: `E.g ${this.currency}100.00`,
      showDomain: true,
      dValue: null,
      dText: (this.processor && this.processor.domainLimits) ? this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.DL') : this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: (this.processor && this.processor.domainLimits) ? this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.DL.MAX_AMT_PW`) : this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.MAX_AMT_PW`),
      dPlaceholder: `E.g ${this.currency}100.00`,
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.MAX_AMT_PW`),
      pPlaceholder: `E.g ${this.currency}100.00`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.MAX_AMT_PW`),
      uPlaceholder: `E.g ${this.currency}100.00`
    },
    maxMonth: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXAMOUNTPERMONTH.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.MAX_AMT_PM`),
      gPlaceholder: `E.g ${this.currency}200.00`,
      showDomain: true,
      dValue: null,
      dText: (this.processor && this.processor.domainLimits) ? this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.DL') : this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: (this.processor && this.processor.domainLimits) ? this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.DL.MAX_AMT_PM`) : this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.MAX_AMT_PM`),
      dPlaceholder: `E.g ${this.currency}200.00`,
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.MAX_AMT_PM`),
      pPlaceholder: `E.g ${this.currency}200.00`,
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.MAX_AMT_PM`),
      uPlaceholder: `E.g ${this.currency}200.00`
    },
    maxTransDay: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERDAY.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.TRANS_PD`),
      gPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERDAY.PLACEHOLDER'),
      showDomain: true,
      dValue: null,
      dText: (this.processor && this.processor.domainLimits) ? this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.DL') : this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: (this.processor && this.processor.domainLimits) ? this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.DL.TRANS_PD`) : this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.TRANS_PD`),
      dPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERDAY.PLACEHOLDER'),
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.TRANS_PD`),
      pPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERDAY.PLACEHOLDER'),
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.TRANS_PD`),
      uPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERDAY.PLACEHOLDER')
    },
    maxTransWeek: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERWEEK.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.TRANS_PW`),
      gPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERWEEK.PLACEHOLDER'),
      showDomain: true,
      dValue: null,
      dText: (this.processor && this.processor.domainLimits) ? this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.DL') : this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: (this.processor && this.processor.domainLimits) ? this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.DL.TRANS_PW`) : this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.TRANS_PW`),
      dPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERWEEK.PLACEHOLDER'),
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.TRANS_PW`),
      pPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERWEEK.PLACEHOLDER'),
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.TRANS_PW`),
      uPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERWEEK.PLACEHOLDER')
    },
    maxTransMonth: {
      title: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERMONTH.NAME'),
      gValue: null,
      gText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GPL'),
      gHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GPL.TRANS_PM`),
      gPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERMONTH.PLACEHOLDER'),
      showDomain: true,
      dValue: null,
      dText: (this.processor && this.processor.domainLimits) ? this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.DL') : this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.GL'),
      dHint: (this.processor && this.processor.domainLimits) ? this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.DL.TRANS_PM`) : this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.GL.TRANS_PM`),
      dPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERMONTH.PLACEHOLDER'),
      showProfile: false,
      pValue: null,
      pText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.PL'),
      pHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.PL.TRANS_PM`),
      pPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERMONTH.PLACEHOLDER'),
      showUser: false,
      uValue: null,
      uText: this.$translate('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIMITS.IPL'),
      uHint: this.$translate(`UI_NETWORK_ADMIN.SERVICE_CASHIER.${this.getMethodType()}.IPL.TRANS_PM`),
      uPlaceholder: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.MAXTRANSPERMONTH.PLACEHOLDER')
    },
  }



  mounted() {
    this.processor = JSON.parse(JSON.stringify(this.propsProcessor))
    // this.processor = this.propsProcessor

    if (this.processor) {
      if(this.processor && this.processor.domainMethod && this.processor.domainMethod.method) {
        this.modifiedProcessorName = this.processor.domainMethod.method.name
      }

      if (this.processor && this.processor.description) this.modifiedProcessorDescription = this.processor.description
      if (this.processor && this.processor.processor) this.modifiedProcessor = this.processor.processor.name

      if (this.processor.dmpp) { // if  profile is selected in filter
        if (this.processor.dmpp.fees) {

          this.inputGroups.flatFee.pValue = this.processor.dmpp.fees.flatDec ? this.processor.dmpp.fees.flatDec : null
          this.inputGroups.minimumFee.pValue = this.processor.dmpp.fees.minimumDec ? this.processor.dmpp.fees.minimumDec : null
          this.inputGroups.percentageFee.pValue = this.processor.dmpp.fees.percentage ? this.processor.dmpp.fees.percentage : null
        }

        if (this.processor.dmpp.limits) {
          this.inputGroups.maximumLimit.pValue = this.processor.dmpp.limits.maxAmountDec ? this.processor.dmpp.limits.maxAmountDec : null
          this.inputGroups.minimumLimit.pValue = this.processor.dmpp.limits.minAmountDec ? this.processor.dmpp.limits.minAmountDec : null
          this.inputGroups.firstMinLimit.pValue = this.processor.dmpp.limits.minFirstTransactionAmountDec ? this.processor.dmpp.limits.minFirstTransactionAmountDec : null
          this.inputGroups.firstMaxLimit.pValue = this.processor.dmpp.limits.maxFirstTransactionAmountDec ? this.processor.dmpp.limits.maxFirstTransactionAmountDec : null
          this.inputGroups.maxDay.pValue = this.processor.dmpp.limits.maxAmountDayDec ? this.processor.dmpp.limits.maxAmountDayDec : null
          this.inputGroups.maxMonth.pValue = this.processor.dmpp.limits.maxAmountMonthDec ? this.processor.dmpp.limits.maxAmountMonthDec : null
          this.inputGroups.maxWeek.pValue = this.processor.dmpp.limits.maxAmountWeekDec ? this.processor.dmpp.limits.maxAmountWeekDec : null
          this.inputGroups.maxTransDay.pValue = this.processor.dmpp.limits.maxTransactionsDay ? this.processor.dmpp.limits.maxTransactionsDay : null
          this.inputGroups.maxTransMonth.pValue = this.processor.dmpp.limits.maxTransactionsMonth ? this.processor.dmpp.limits.maxTransactionsMonth : null
          this.inputGroups.maxTransWeek.pValue = this.processor.dmpp.limits.maxTransactionsWeek ? this.processor.dmpp.limits.maxTransactionsWeek : null
        }

      } else if (this.processor.dmpu) {// if  user is selected in filter
        if (this.processor.dmpu.fees) {
          this.inputGroups.flatFee.uValue = this.processor.dmpu.fees.flatDec ? this.processor.dmpu.fees.flatDec : null
          this.inputGroups.minimumFee.uValue = this.processor.dmpu.fees.minimumDec ? this.processor.dmpu.fees.minimumDec : null
          this.inputGroups.percentageFee.uValue = this.processor.dmpu.fees.percentage ? this.processor.dmpu.fees.percentage : null
        }

        if (this.processor.dmpu.limits) {
          this.inputGroups.maximumLimit.uValue = this.processor.dmpu.limits.maxAmountDec ? this.processor.dmpu.limits.maxAmountDec : null
          this.inputGroups.minimumLimit.uValue = this.processor.dmpu.limits.minAmountDec ? this.processor.dmpu.limits.minAmountDec : null
          this.inputGroups.firstMinLimit.uValue = this.processor.dmpu.limits.minFirstTransactionAmountDec ? this.processor.dmpu.limits.minFirstTransactionAmountDec : null
          this.inputGroups.firstMaxLimit.uValue = this.processor.dmpu.limits.maxFirstTransactionAmountDec ? this.processor.dmpu.limits.maxFirstTransactionAmountDec : null
          this.inputGroups.maxDay.uValue = this.processor.dmpu.limits.maxAmountDayDec ? this.processor.dmpu.limits.maxAmountDayDec : null
          this.inputGroups.maxMonth.uValue = this.processor.dmpu.limits.maxAmountMonthDec ? this.processor.dmpu.limits.maxAmountMonthDec : null
          this.inputGroups.maxWeek.uValue = this.processor.dmpu.limits.maxAmountWeekDec ? this.processor.dmpu.limits.maxAmountWeekDec : null
          this.inputGroups.maxTransDay.uValue = this.processor.dmpu.limits.maxTransactionsDay ? this.processor.dmpu.limits.maxTransactionsDay : null
          this.inputGroups.maxTransMonth.uValue = this.processor.dmpu.limits.maxTransactionsMonth ? this.processor.dmpu.limits.maxTransactionsMonth : null
          this.inputGroups.maxTransWeek.uValue = this.processor.dmpu.limits.maxTransactionsWeek ? this.processor.dmpu.limits.maxTransactionsWeek : null
        }

      }

      if (this.processor.fees) {
        this.inputGroups.flatFee.gValue = this.processor.fees.flatDec ? this.processor.fees.flatDec : null
        this.inputGroups.minimumFee.gValue = this.processor.fees.minimumDec ? this.processor.fees.minimumDec : null
        this.inputGroups.percentageFee.gValue = this.processor.fees.percentage ? this.processor.fees.percentage : null
      }

      if (this.processor.limits) {
        this.inputGroups.maximumLimit.gValue = this.processor.limits.maxAmountDec ? this.processor.limits.maxAmountDec : null
        this.inputGroups.minimumLimit.gValue = this.processor.limits.minAmountDec ? this.processor.limits.minAmountDec : null
        this.inputGroups.firstMinLimit.gValue = this.processor.limits.minFirstTransactionAmountDec ? this.processor.limits.minFirstTransactionAmountDec : null
        this.inputGroups.firstMaxLimit.gValue = this.processor.limits.maxFirstTransactionAmountDec ? this.processor.limits.maxFirstTransactionAmountDec : null
        this.inputGroups.maxDay.gValue = this.processor.limits.maxAmountDayDec ? this.processor.limits.maxAmountDayDec : null
        this.inputGroups.maxMonth.gValue = this.processor.limits.maxAmountMonthDec ? this.processor.limits.maxAmountMonthDec : null
        this.inputGroups.maxWeek.gValue = this.processor.limits.maxAmountWeekDec ? this.processor.limits.maxAmountWeekDec : null
        this.inputGroups.maxTransDay.gValue = this.processor.limits.maxTransactionsDay ? this.processor.limits.maxTransactionsDay : null
        this.inputGroups.maxTransMonth.gValue = this.processor.limits.maxTransactionsMonth ? this.processor.limits.maxTransactionsMonth : null
        this.inputGroups.maxTransWeek.gValue = this.processor.limits.maxTransactionsWeek ? this.processor.limits.maxTransactionsWeek : null
      }

      if (this.processor.domainLimits) {
        this.inputGroups.maxDay.dValue = this.processor.domainLimits.maxAmountDayDec ? this.processor.domainLimits.maxAmountDayDec : null
        this.inputGroups.maxMonth.dValue = this.processor.domainLimits.maxAmountMonthDec ? this.processor.domainLimits.maxAmountMonthDec : null
        this.inputGroups.maxWeek.dValue = this.processor.domainLimits.maxAmountWeekDec ? this.processor.domainLimits.maxAmountWeekDec : null
        this.inputGroups.maxTransDay.dValue = this.processor.domainLimits.maxTransactionsDay ? this.processor.domainLimits.maxTransactionsDay : null
        this.inputGroups.maxTransMonth.dValue = this.processor.domainLimits.maxTransactionsMonth ? this.processor.domainLimits.maxTransactionsMonth : null
        this.inputGroups.maxTransWeek.dValue = this.processor.domainLimits.maxTransactionsWeek ? this.processor.domainLimits.maxTransactionsWeek : null
      } else if (this.processor.processor && this.processor.processor.limits) {
        this.inputGroups.maxDay.dValue = this.processor.processor.limits.maxAmountDayDec ? this.processor.processor.limits.maxAmountDayDec : null
        this.inputGroups.maxMonth.dValue = this.processor.processor.limits.maxAmountMonthDec ? this.processor.processor.limits.maxAmountMonthDec : null
        this.inputGroups.maxWeek.dValue = this.processor.processor.limits.maxAmountWeekDec ? this.processor.processor.limits.maxAmountWeekDec : null
        this.inputGroups.maxTransDay.dValue = this.processor.processor.limits.maxTransactionsDay ? this.processor.processor.limits.maxTransactionsDay : null
        this.inputGroups.maxTransMonth.dValue = this.processor.processor.limits.maxTransactionsMonth ? this.processor.processor.limits.maxTransactionsMonth : null
        this.inputGroups.maxTransWeek.dValue = this.processor.processor.limits.maxTransactionsWeek ? this.processor.processor.limits.maxTransactionsWeek : null
      }

      if (this.viewAs === 'user') {

        for (let key in this.inputGroups) {
          this.inputGroups[key].showUser = true
        }

        if (this.processor.dmpu === null) this.initUser()

        if (this.processor.fees && this.processor.dmpu) this.showFees = true
        if (this.processor.limits && this.processor.dmpu) this.showLimits = true

      } else if (this.viewAs === 'profile' || this.processor.dmpp) {

        for (let key in this.inputGroups) {
          this.inputGroups[key].showProfile = true
        }

        if (this.processor.dmpu === null) this.initProfile()

      } else if (!this.viewAs) {
        this.processor.enabled = true;
        this.processor.weight = 0;
      }

      this.loadMore(true)
      this.showFeesMethod()
      this.showLimitsMethod()

      this.calculateFe()
    }
  }

  async changeProcessorStatus(tab: string) {
    if(this.processor) {
      switch (this.viewAs) {
        case 'user': {
          if (this.processor.dmpu === null || typeof this.processor.dmpu === 'undefined') this.initUser()

          if (this.processor.dmpu && typeof this.processor.dmpu.enabled === 'undefined') {

            if ((this.processor.dmpp === null) || (this.processor.dmpp && typeof this.processor.dmpp.enabled === 'undefined') || typeof this.processor.dmpp === 'undefined') {

              this.processor.dmpu.enabled = this.processor.enabled
            } else if (this.processor.dmpp) {

              this.processor.dmpu.enabled = this.processor.dmpp.enabled
            }
          }
          if (this.processor.dmpu) {
            this.processor.dmpu.enabled = !this.processor.dmpu.enabled
            let tempProcessor: CashierConfigProcessor = JSON.parse(JSON.stringify(this.processor))

            try {
              let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUserCreateOrUpdate(this.processor.dmpu)

              if (result) {
                tempProcessor = {
                  ...tempProcessor,
                  dmpu: result.plain()
                }
                this.processor = tempProcessor
              }
            } catch (err) {
              this.logService.error(err)
            }
          }
          break;
        }
        case 'profile': {
          if (this.processor.dmpp === null || typeof this.processor.dmpp === 'undefined') this.initProfile()

          if (this.processor.dmpp && typeof this.processor.dmpp.enabled === 'undefined') {
            this.processor.dmpp.enabled = this.processor.enabled
          }

          if (this.processor.dmpp) {
            this.processor.dmpp.enabled = !this.processor.dmpp.enabled
            let tempProcessor: CashierConfigProcessor = JSON.parse(JSON.stringify(this.processor))

            try {
              let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileCreateOrUpdate(this.processor.dmpp)

              if (result) {
                tempProcessor = {
                  ...tempProcessor,
                  dmpp: result.plain()
                }
                this.processor = tempProcessor
              }
            } catch (err) {
              this.logService.error(err)
            }
          }
          break;
        }
        default: {
          this.processor.enabled = !this.processor.enabled
          if (!this.processor.enabled && this.processor.active !== null) this.processor.active = false

          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUpdate(this.processor)

            if (result) {
              this.processor = {...this.processor, ...result.plain()}
            }
          } catch (err) {
            this.logService.error(err)
          }
        }
      }

      this.$emit('changeProcessor', this.processor)
    }
  }

  override(tab: string) {
    if (!this.viewAs) {

      if (tab === 'fees') {
        this.initFees()
        this.showFees = true

      } else if (tab === 'limits') {
        this.initLimits()
        this.showLimits = true
      }

      if (this.processor) {
        // this.processor[tab] = {}
        this.processor.enabled = true;
        this.processor.weight = 0;
      }

      if (this.processor && this.processor.domainLimits === null) this.processor.domainLimits = {};

    } else {
      this.checkVisibility(tab)
    }

    this.showFeesMethod()
    this.showLimitsMethod()
  }

  saveOverrides(tab: string) {
    // if user has left some field empty we add there zero value

    if (this.processor && (this.viewAs === 'profile' || this.processor.dmpp)) {

      if (tab === 'fees' && this.processor.dmpp && this.processor.dmpp.fees) {
        this.processor.dmpp.fees.flat = this.processor.dmpp.fees.flatDec ? +this.processor.dmpp.fees.flatDec * 100 : 0
        this.processor.dmpp.fees.flatDec = this.processor.dmpp.fees.flatDec ? +this.processor.dmpp.fees.flatDec : 0
        this.processor.dmpp.fees.minimum = this.processor.dmpp.fees.minimumDec ? +this.processor.dmpp.fees.minimumDec * 100 : 0
        this.processor.dmpp.fees.minimumDec = this.processor.dmpp.fees.minimumDec ? +this.processor.dmpp.fees.minimumDec : 0
        this.processor.dmpp.fees.percentage = this.processor.dmpp.fees.percentage ? +this.processor.dmpp.fees.percentage : 0

      } else if (tab === 'limits'  && this.processor.dmpp && this.processor.dmpp.limits) {
        this.processor.dmpp.limits.maxAmount = this.processor.dmpp.limits.maxAmountDec ? +this.processor.dmpp.limits.maxAmountDec * 100 : 0
        this.processor.dmpp.limits.maxAmountDec = this.processor.dmpp.limits.maxAmountDec ? +this.processor.dmpp.limits.maxAmountDec : 0
        this.processor.dmpp.limits.minAmount = this.processor.dmpp.limits.minAmountDec ? +this.processor.dmpp.limits.minAmountDec * 100 : 0
        this.processor.dmpp.limits.minAmountDec = this.processor.dmpp.limits.minAmountDec ? +this.processor.dmpp.limits.minAmountDec : 0
        this.processor.dmpp.limits.minFirstTransactionAmount = this.processor.dmpp.limits.minFirstTransactionAmountDec ? +this.processor.dmpp.limits.minFirstTransactionAmountDec * 100 : 0
        this.processor.dmpp.limits.minFirstTransactionAmountDec = this.processor.dmpp.limits.minFirstTransactionAmountDec ? +this.processor.dmpp.limits.minFirstTransactionAmountDec: 0
        this.processor.dmpp.limits.maxFirstTransactionAmount = this.processor.dmpp.limits.maxFirstTransactionAmountDec ? +this.processor.dmpp.limits.maxFirstTransactionAmountDec * 100 : 0
        this.processor.dmpp.limits.maxFirstTransactionAmountDec = this.processor.dmpp.limits.maxFirstTransactionAmountDec ? +this.processor.dmpp.limits.maxFirstTransactionAmountDec: 0
        this.processor.dmpp.limits.maxAmountDay = this.processor.dmpp.limits.maxAmountDayDec ? +this.processor.dmpp.limits.maxAmountDayDec * 100 : 0
        this.processor.dmpp.limits.maxAmountDayDec = this.processor.dmpp.limits.maxAmountDayDec ? +this.processor.dmpp.limits.maxAmountDayDec : 0
        this.processor.dmpp.limits.maxAmountWeek = this.processor.dmpp.limits.maxAmountWeekDec ? +this.processor.dmpp.limits.maxAmountWeekDec * 100 : 0
        this.processor.dmpp.limits.maxAmountWeekDec = this.processor.dmpp.limits.maxAmountWeekDec ? +this.processor.dmpp.limits.maxAmountWeekDec : 0
        this.processor.dmpp.limits.maxAmountMonth = this.processor.dmpp.limits.maxAmountMonthDec ? +this.processor.dmpp.limits.maxAmountMonthDec * 100 : 0
        this.processor.dmpp.limits.maxAmountMonthDec = this.processor.dmpp.limits.maxAmountMonthDec ? +this.processor.dmpp.limits.maxAmountMonthDec : 0
        this.processor.dmpp.limits.maxTransactionsDay = this.processor.dmpp.limits.maxTransactionsDay ? +this.processor.dmpp.limits.maxTransactionsDay : 0
        this.processor.dmpp.limits.maxTransactionsMonth = this.processor.dmpp.limits.maxTransactionsMonth ? +this.processor.dmpp.limits.maxTransactionsMonth : 0
        this.processor.dmpp.limits.maxTransactionsWeek = this.processor.dmpp.limits.maxTransactionsWeek ? +this.processor.dmpp.limits.maxTransactionsWeek : 0
      }

    } else if (this.processor && (this.viewAs === 'user' || this.processor.dmpu)) {

      if (tab === 'fees'  && this.processor.dmpu && this.processor.dmpu.fees) {
        this.processor.dmpu.fees.flat = this.processor.dmpu.fees.flatDec ? +this.processor.dmpu.fees.flatDec * 100 : 0
        this.processor.dmpu.fees.flatDec = this.processor.dmpu.fees.flatDec ? +this.processor.dmpu.fees.flatDec : 0
        this.processor.dmpu.fees.minimum = this.processor.dmpu.fees.minimumDec ? +this.processor.dmpu.fees.minimumDec * 100 : 0
        this.processor.dmpu.fees.minimumDec = this.processor.dmpu.fees.minimumDec ? +this.processor.dmpu.fees.minimumDec : 0
        this.processor.dmpu.fees.percentage = this.processor.dmpu.fees.percentage ? +this.processor.dmpu.fees.percentage : 0

      } else if (tab === 'limits'  && this.processor.dmpu && this.processor.dmpu.limits) {
        this.processor.dmpu.limits.maxAmount = this.processor.dmpu.limits.maxAmountDec ? +this.processor.dmpu.limits.maxAmountDec * 100 : 0
        this.processor.dmpu.limits.maxAmountDec = this.processor.dmpu.limits.maxAmountDec ? +this.processor.dmpu.limits.maxAmountDec : 0
        this.processor.dmpu.limits.minAmount = this.processor.dmpu.limits.minAmountDec ? +this.processor.dmpu.limits.minAmountDec * 100 : 0
        this.processor.dmpu.limits.minAmountDec = this.processor.dmpu.limits.minAmountDec ? +this.processor.dmpu.limits.minAmountDec : 0
        this.processor.dmpu.limits.minFirstTransactionAmount = this.processor.dmpu.limits.minFirstTransactionAmountDec ? +this.processor.dmpu.limits.minFirstTransactionAmountDec * 100 : 0
        this.processor.dmpu.limits.minFirstTransactionAmountDec = this.processor.dmpu.limits.minFirstTransactionAmountDec ? +this.processor.dmpu.limits.minFirstTransactionAmountDec: 0
        this.processor.dmpu.limits.maxFirstTransactionAmount = this.processor.dmpu.limits.maxFirstTransactionAmountDec ? +this.processor.dmpu.limits.maxFirstTransactionAmountDec * 100 : 0
        this.processor.dmpu.limits.maxFirstTransactionAmountDec = this.processor.dmpu.limits.maxFirstTransactionAmountDec ? +this.processor.dmpu.limits.maxFirstTransactionAmountDec: 0
        this.processor.dmpu.limits.maxAmountDay = this.processor.dmpu.limits.maxAmountDayDec ? +this.processor.dmpu.limits.maxAmountDayDec * 100 : 0
        this.processor.dmpu.limits.maxAmountDayDec = this.processor.dmpu.limits.maxAmountDayDec ? +this.processor.dmpu.limits.maxAmountDayDec : 0
        this.processor.dmpu.limits.maxAmountWeek = this.processor.dmpu.limits.maxAmountWeekDec ? +this.processor.dmpu.limits.maxAmountWeekDec * 100 : 0
        this.processor.dmpu.limits.maxAmountWeekDec = this.processor.dmpu.limits.maxAmountWeekDec ? +this.processor.dmpu.limits.maxAmountWeekDec : 0
        this.processor.dmpu.limits.maxAmountMonth = this.processor.dmpu.limits.maxAmountMonthDec ? +this.processor.dmpu.limits.maxAmountMonthDec * 100 : 0
        this.processor.dmpu.limits.maxAmountMonthDec = this.processor.dmpu.limits.maxAmountMonthDec ? +this.processor.dmpu.limits.maxAmountMonthDec : 0
        this.processor.dmpu.limits.maxTransactionsDay = this.processor.dmpu.limits.maxTransactionsDay ? +this.processor.dmpu.limits.maxTransactionsDay : 0
        this.processor.dmpu.limits.maxTransactionsMonth = this.processor.dmpu.limits.maxTransactionsMonth ? +this.processor.dmpu.limits.maxTransactionsMonth : 0
        this.processor.dmpu.limits.maxTransactionsWeek = this.processor.dmpu.limits.maxTransactionsWeek ? +this.processor.dmpu.limits.maxTransactionsWeek : 0
      }

    } else {

      if (tab === 'fees' && this.processor && this.processor.fees) {
        this.processor.fees.flat = this.processor.fees.flatDec ? +this.processor.fees.flatDec * 100 : 0
        this.processor.fees.flatDec = this.processor.fees.flatDec ? +this.processor.fees.flatDec : 0
        this.processor.fees.minimum = this.processor.fees.minimumDec ? +this.processor.fees.minimumDec * 100 : 0
        this.processor.fees.minimumDec = this.processor.fees.minimumDec ? +this.processor.fees.minimumDec : 0
        this.processor.fees.percentage = this.processor.fees.percentage ? +this.processor.fees.percentage : 0

      } else if (tab === 'limits' && this.processor && this.processor.limits) {

        this.processor.limits.maxAmount = this.processor.limits.maxAmountDec ? +this.processor.limits.maxAmountDec * 100 : 0
        this.processor.limits.maxAmountDec = this.processor.limits.maxAmountDec ? +this.processor.limits.maxAmountDec : 0
        this.processor.limits.minAmount = this.processor.limits.minAmountDec ? +this.processor.limits.minAmountDec * 100 : 0
        this.processor.limits.minAmountDec = this.processor.limits.minAmountDec ? +this.processor.limits.minAmountDec : 0
        this.processor.limits.minFirstTransactionAmount = this.processor.limits.minFirstTransactionAmountDec ? +this.processor.limits.minFirstTransactionAmountDec * 100 : 0
        this.processor.limits.minFirstTransactionAmountDec = this.processor.limits.minFirstTransactionAmountDec ? +this.processor.limits.minFirstTransactionAmountDec: 0
        this.processor.limits.maxFirstTransactionAmount = this.processor.limits.maxFirstTransactionAmountDec ? +this.processor.limits.maxFirstTransactionAmountDec * 100 : 0
        this.processor.limits.maxFirstTransactionAmountDec = this.processor.limits.maxFirstTransactionAmountDec ? +this.processor.limits.maxFirstTransactionAmountDec: 0
        this.processor.limits.maxAmountDay = this.processor.limits.maxAmountDayDec ? +this.processor.limits.maxAmountDayDec * 100 : 0
        this.processor.limits.maxAmountDayDec = this.processor.limits.maxAmountDayDec ? +this.processor.limits.maxAmountDayDec : 0
        this.processor.limits.maxAmountWeek = this.processor.limits.maxAmountWeekDec ? +this.processor.limits.maxAmountWeekDec * 100 : 0
        this.processor.limits.maxAmountWeekDec = this.processor.limits.maxAmountWeekDec ? +this.processor.limits.maxAmountWeekDec : 0
        this.processor.limits.maxAmountMonth = this.processor.limits.maxAmountMonthDec ? +this.processor.limits.maxAmountMonthDec * 100 : 0
        this.processor.limits.maxAmountMonthDec = this.processor.limits.maxAmountMonthDec ? +this.processor.limits.maxAmountMonthDec : 0
        this.processor.limits.maxTransactionsDay = this.processor.limits.maxTransactionsDay ? +this.processor.limits.maxTransactionsDay : 0
        this.processor.limits.maxTransactionsMonth = this.processor.limits.maxTransactionsMonth ? +this.processor.limits.maxTransactionsMonth : 0
        this.processor.limits.maxTransactionsWeek = this.processor.limits.maxTransactionsWeek ? +this.processor.limits.maxTransactionsWeek : 0

        if (this.processor && this.processor.domainLimits) {
          this.processor.domainLimits.maxAmountDay = this.processor.domainLimits.maxAmountDayDec ? +this.processor.domainLimits.maxAmountDayDec * 100 : 0
          this.processor.domainLimits.maxAmountDayDec = this.processor.domainLimits.maxAmountDayDec ? +this.processor.domainLimits.maxAmountDayDec : 0
          this.processor.domainLimits.maxAmountMonth = this.processor.domainLimits.maxAmountMonthDec ? +this.processor.domainLimits.maxAmountMonthDec * 100 : 0
          this.processor.domainLimits.maxAmountMonthDec = this.processor.domainLimits.maxAmountMonthDec ? +this.processor.domainLimits.maxAmountMonthDec : 0
          this.processor.domainLimits.maxAmountWeek = this.processor.domainLimits.maxAmountWeekDec ? +this.processor.domainLimits.maxAmountWeekDec * 100 : 0
          this.processor.domainLimits.maxAmountWeekDec = this.processor.domainLimits.maxAmountWeekDec ? +this.processor.domainLimits.maxAmountWeekDec : 0
          this.processor.domainLimits.maxTransactionsDay = this.processor.domainLimits.maxTransactionsDay ? +this.processor.domainLimits.maxTransactionsDay : 0
          this.processor.domainLimits.maxTransactionsMonth = this.processor.domainLimits.maxTransactionsMonth ? +this.processor.domainLimits.maxTransactionsMonth : 0
          this.processor.domainLimits.maxTransactionsWeek = this.processor.domainLimits.maxTransactionsWeek ? +this.processor.domainLimits.maxTransactionsWeek : 0
        } else if (this.processor && this.processor.processor && this.processor.processor.limits) {
          this.processor.processor.limits.maxAmountDay = this.processor.processor.limits.maxAmountDayDec ? this.processor.processor.limits.maxAmountDayDec * 100 : 0
          this.processor.processor.limits.maxAmountDayDec = this.processor.processor.limits.maxAmountDayDec ? this.processor.processor.limits.maxAmountDayDec : 0
          this.processor.processor.limits.maxAmountMonth = this.processor.processor.limits.maxAmountMonthDec ? this.processor.processor.limits.maxAmountMonthDec * 100 : 0
          this.processor.processor.limits.maxAmountMonthDec = this.processor.processor.limits.maxAmountMonthDec ? this.processor.processor.limits.maxAmountMonthDec : 0
          this.processor.processor.limits.maxAmountWeek = this.processor.processor.limits.maxAmountWeekDec ? this.processor.processor.limits.maxAmountWeekDec * 100 : 0
          this.processor.processor.limits.maxAmountWeekDec = this.processor.processor.limits.maxAmountWeekDec ? this.processor.processor.limits.maxAmountWeekDec : 0
          this.processor.processor.limits.maxTransactionsDay = this.processor.processor.limits.maxTransactionsDay ? this.processor.processor.limits.maxTransactionsDay : 0
          this.processor.processor.limits.maxTransactionsMonth = this.processor.processor.limits.maxTransactionsMonth ? this.processor.processor.limits.maxTransactionsMonth : 0
          this.processor.processor.limits.maxTransactionsWeek = this.processor.processor.limits.maxTransactionsWeek ? this.processor.processor.limits.maxTransactionsWeek : 0
        }// end of tab === 'limits'
      }
    }

    this.setOverride(tab.toLowerCase())
  }

  async setOverride(type: string) {

    if (this.viewAs === 'user' && this.processor) {
      if(this.processor.dmpu) {
        if (this.processor.dmpu.id === null) {
          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUserCreateOrUpdate(this.processor.dmpu, type)

            if (result) {
              this.processor.dmpu = result.plain()
            }
          } catch (err) {
            this.logService.error(err)
          }

        } else {
          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUserSave(this.processor.dmpu, type)

            if (result) {
              this.processor.dmpu[type] = result[type]
            }
          } catch (err) {
            this.logService.error(err)
          }
        }
      }

    } else if (this.viewAs === 'profile'  && this.processor) {
      if(this.processor.dmpp) {
        if (this.processor.dmpp.id === null) {
          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileCreateOrUpdate(this.processor.dmpp)

            if (result) {
              this.processor.dmpp[type] = result.plain()
            }
          } catch (err) {
            this.logService.error(err)
          }

        } else {
          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileSave(this.processor.dmpp, type)

            if (result) {
              this.processor.dmpp[type] = result[type]
            }
          } catch (err) {
            this.logService.error(err)
          }
        }
      }

    } else {
      if(this.processor) {
        try {
          let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorSave(this.processor, type)

          if (result) {
            this.processor[type] = JSON.parse(JSON.stringify(result[type]))

            try {
              let pp = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorSaveDL(this.processor)

              if (pp) {
                this.processor.domainLimits = pp.domainLimits
              }
            } catch (err) {
              this.logService.error(err)
            }
          }
        } catch (err) {
          this.logService.error(err)
        }
      }
    }

    this.$emit('changeProcessor', this.processor)
  }

  async saveModifiedProcessor(tab: string) {
    this.modifyDialog = false

    if(this.processor) {
      this.processor.accessRule = this.modifiedAccessRuleProcessor ? this.modifiedAccessRuleProcessor.name : null
      this.processor.accessRuleOnTranInit = this.modifiedAccessRuleTransition ? this.modifiedAccessRuleTransition.name : null
      this.processor.description = this.modifiedProcessorDescription

      try {
        let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUpdate(this.processor)

        if (result) {
          this.processor = {...this.processor, ...result.plain()}
        }
      } catch (err) {
        this.logService.error(err)
      }

      this.$emit('changeProcessor', this.processor)
    }
  }

  async saveProperties() {
    this.propertiesDialog = false

    if(this.processor && this.processor.id) {
      try {
        let result = await this.rootScope.provide.cashierConfigProvider.saveProperties(this.processor.id, this.processorProperties)

        if (result) {
          this.processorProperties = result.plain()
        }
      } catch (err) {
        this.logService.error(err)
      }

      this.$emit('changeProcessor', this.processor)
    }
  }

  get filteredProcessorProperties() {
    let searchedValue = this.propertiesSearchedValue.toLowerCase()

    let filtered = this.processorProperties.filter(item => {
          if (
              (item.processorProperty.name ?
                  item.processorProperty.name.toLowerCase().includes(searchedValue)
                  : false)
              || (item.processorProperty.defaultValue ?
                  item.processorProperty.defaultValue.toString().toLowerCase().includes(searchedValue)
                  : false)
              || (item.processorProperty.description ?
                  item.processorProperty.description.toLowerCase().includes(searchedValue)
                  : false)
          ) {
            return item
          }
        }
    )

    return filtered
  }

  deleteProcessor() {
    if(this.processor) {
      this.processor.deleted = true
      this.deleteDialog = false

      this.$emit('delete', this.processor)
    }
  }

 async deleteFeesLimits(tab: string) {
    switch (this.viewAs) {
      case 'user': {
        if(this.processor && this.processor.dmpu) {
          if (this.processor.dmpu[tab].id !== null) {
            try {
              let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUserDelete(this.processor.dmpu, tab)

              if (result) {
                this.processor = {...this.processor, ...result.plain()}
              }
            } catch (err) {
              this.logService.error(err)
            }
          }

          if(this.processor && this.processor.dmpu) this.processor.dmpu[tab] = null
        }
        break;
      }
      case 'profile': {
        if(this.processor && this.processor.dmpp) {
          if (this.processor.dmpp[tab].id !== null) {
            try {
              let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileDelete(this.processor.dmpp, tab)

              if (result) {
                this.processor = {...this.processor, ...result.plain()}
              }
            } catch (err) {
              this.logService.error(err)
            }
          }

          if (this.processor && this.processor.dmpp && this.processor[tab]) this.processor.dmpp[tab] = null
        }
        break;
      }
      default: {

        if(this.processor) {
          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorDelete(this.processor, tab)

            if (result) {
              this.processor = {...this.processor, ...result.plain()}

              if (this.processor && this.processor[tab]) this.processor[tab] = null
            }
          } catch (err) {
            this.logService.error(err)
          }
        }
      }
    }

   this.$emit('changeProcessor', this.processor)

    if( this.viewAs === 'user') {
      if (tab === 'fees') {
        // this.processor.dmpu.fees = null// maybe not needed because we did it upper
        this.showFees = false
      } else if (tab === 'limits') {
        // this.processor.dmpu.limits = null
        this.showLimits = false
      }
    } else if(this.viewAs === 'profile') {
      if (tab === 'fees') {
        // this.processor.dmpp.fees = null
        this.showFees = false
      } else if (tab === 'limits') {
        // this.processor.dmpp.limits = null
        this.showLimits = false
      }
    } else if(!this.viewAs) {
      if (tab === 'fees') {
        // this.processor.fees = null
        this.showFees = false
      } else if (tab === 'limits') {
        // this.processor.limits = null
        if (this.processor) this.processor.domainLimits = null
        this.showLimits = false
      }
    }
  }

 async getProperties(id: string) {
    try {
      let result = await this.rootScope.provide.cashierConfigProvider.findProperties(id)

      if(result) {
        let tempProps = result.plain()
        tempProps = tempProps.map((item) => {
          return {
            ...item,
            override: item.id !== null
          }
        })

        this.processorProperties = JSON.parse(JSON.stringify(tempProps))
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  getDataFromInputs(changes: CashierConfigInputChangesInterface) {
    let prop = changes.name
    this.inputGroups[prop].gValue = changes.fields.gValue
    this.inputGroups[prop].dValue = changes.fields.dValue
    this.inputGroups[prop].pValue = changes.fields.pValue
    this.inputGroups[prop].uValue = changes.fields.uValue

    if(this.processor) {
      if (this.processor.dmpp) { // if  profile is selected in filter

        if (this.processor.dmpp.fees) {
          this.processor.dmpp.fees.flatDec = this.inputGroups.flatFee.pValue
          this.processor.dmpp.fees.minimumDec = this.inputGroups.minimumFee.pValue
          this.processor.dmpp.fees.percentage = this.inputGroups.percentageFee.pValue
        }

        if (this.processor.dmpp.limits) {
          this.processor.dmpp.limits.maxAmountDec = this.inputGroups.maximumLimit.pValue
          this.processor.dmpp.limits.minAmountDec = this.inputGroups.minimumLimit.pValue
          this.processor.dmpp.limits.minFirstTransactionAmountDec = this.inputGroups.firstMinLimit.pValue
          this.processor.dmpp.limits.maxFirstTransactionAmountDec = this.inputGroups.firstMaxLimit.pValue
          this.processor.dmpp.limits.maxAmountDayDec = this.inputGroups.maxDay.pValue
          this.processor.dmpp.limits.maxAmountMonthDec = this.inputGroups.maxMonth.pValue
          this.processor.dmpp.limits.maxAmountWeekDec = this.inputGroups.maxWeek.pValue
          this.processor.dmpp.limits.maxTransactionsDay = this.inputGroups.maxTransDay.pValue
          this.processor.dmpp.limits.maxTransactionsMonth = this.inputGroups.maxTransMonth.pValue
          this.processor.dmpp.limits.maxTransactionsWeek = this.inputGroups.maxTransWeek.pValue
        }

      } else if (this.processor.dmpu ) {// if  user is selected in filter

        if (this.processor.dmpu.fees) {
          this.processor.dmpu.fees.flatDec = this.inputGroups.flatFee.uValue
          this.processor.dmpu.fees.minimumDec = this.inputGroups.minimumFee.uValue
          this.processor.dmpu.fees.percentage = this.inputGroups.percentageFee.uValue
        }

        if (this.processor.dmpu.limits) {
          this.processor.dmpu.limits.maxAmountDec = this.inputGroups.maximumLimit.uValue
          this.processor.dmpu.limits.minAmountDec = this.inputGroups.minimumLimit.uValue
          this.processor.dmpu.limits.minFirstTransactionAmountDec = this.inputGroups.firstMinLimit.uValue
          this.processor.dmpu.limits.maxFirstTransactionAmountDec = this.inputGroups.firstMaxLimit.uValue
          this.processor.dmpu.limits.maxAmountDayDec = this.inputGroups.maxDay.uValue
          this.processor.dmpu.limits.maxAmountMonthDec = this.inputGroups.maxMonth.uValue
          this.processor.dmpu.limits.maxAmountWeekDec = this.inputGroups.maxWeek.uValue
          this.processor.dmpu.limits.maxTransactionsDay = this.inputGroups.maxTransDay.uValue
          this.processor.dmpu.limits.maxTransactionsMonth = this.inputGroups.maxTransMonth.uValue
          this.processor.dmpu.limits.maxTransactionsWeek = this.inputGroups.maxTransWeek.uValue
        }
      }

      if (this.processor.fees) {
        this.processor.fees.flatDec = this.inputGroups.flatFee.gValue
        this.processor.fees.minimumDec = this.inputGroups.minimumFee.gValue
        this.processor.fees.percentage = this.inputGroups.percentageFee.gValue
      }

      if (this.processor.limits) {
        this.processor.limits.maxAmountDec = this.inputGroups.maximumLimit.gValue
        this.processor.limits.minAmountDec = this.inputGroups.minimumLimit.gValue
        this.processor.limits.minFirstTransactionAmountDec = this.inputGroups.firstMinLimit.gValue
        this.processor.limits.maxFirstTransactionAmountDec  = this.inputGroups.firstMaxLimit.gValue
        this.processor.limits.maxAmountDayDec = this.inputGroups.maxDay.gValue
        this.processor.limits.maxAmountMonthDec = this.inputGroups.maxMonth.gValue
        this.processor.limits.maxAmountWeekDec = this.inputGroups.maxWeek.gValue
        this.processor.limits.maxTransactionsDay = this.inputGroups.maxTransDay.gValue
        this.processor.limits.maxTransactionsMonth = this.inputGroups.maxTransMonth.gValue
        this.processor.limits.maxTransactionsWeek = this.inputGroups.maxTransWeek.gValue
      }

      if (this.processor.domainLimits) {
        this.processor.domainLimits.maxAmountDayDec = this.inputGroups.maxDay.dValue
        this.processor.domainLimits.maxAmountMonthDec = this.inputGroups.maxMonth.dValue
        this.processor.domainLimits.maxAmountWeekDec = this.inputGroups.maxWeek.dValue
        this.processor.domainLimits.maxTransactionsDay = this.inputGroups.maxTransDay.dValue
        this.processor.domainLimits.maxTransactionsMonth = this.inputGroups.maxTransMonth.dValue
        this.processor.domainLimits.maxTransactionsWeek = this.inputGroups.maxTransWeek.dValue
      } else if (this.processor.processor && this.processor.processor.limits) {
        this.processor.processor.limits.maxAmountDayDec = this.inputGroups.maxDay.dValue
        this.processor.processor.limits.maxAmountMonthDec = this.inputGroups.maxMonth.dValue
        this.processor.processor.limits.maxAmountWeekDec = this.inputGroups.maxWeek.dValue
        this.processor.processor.limits.maxTransactionsDay = this.inputGroups.maxTransDay.dValue
        this.processor.processor.limits.maxTransactionsMonth = this.inputGroups.maxTransMonth.dValue
        this.processor.processor.limits.maxTransactionsWeek = this.inputGroups.maxTransWeek.dValue
      }

      this.calculateFe()
    }
  }

  async getAccessRules(domain: string) {

    try {
      let result = await this.rootScope.provide.cashierConfigProvider.getAccessRules(domain)

      if (result) {
        this.accessRules = result.plain()

        this.accessRules.forEach((item) => {
          if (this.processor && item.name === this.processor.accessRule) this.modifiedAccessRuleProcessor = item
          if (this.processor && item.name === this.processor.accessRuleOnTranInit) this.modifiedAccessRuleTransition = item
        })
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  @Watch('viewAs')
  async onViewAsChanged() {
    if (this.viewAs === 'user') {

      for (let key in this.inputGroups) {
        this.inputGroups[key].showUser = true
      }

      for (let key in this.inputGroups) {
        this.inputGroups[key].showProfile = false
      }

    } else if (this.viewAs === 'profile') {

      for (let key in this.inputGroups) {
        this.inputGroups[key].showUser = false
      }

      for (let key in this.inputGroups) {
        this.inputGroups[key].showProfile = true
      }

    } else if (!this.viewAs) {

      for (let key in this.inputGroups) {
        this.inputGroups[key].showUser = false
      }

      for (let key in this.inputGroups) {
        this.inputGroups[key].showProfile = false
      }
    }

    await this.showFeesMethod()
    await this.showLimitsMethod()
  }

  showFeesMethod() {
    this.showFees = false

    if(this.processor) {
      if (!this.viewAs) {
        if (this.processor.fees) this.showFees = true

      } else if (this.viewAs === 'profile') {
        if (this.processor.dmpp && this.processor.dmpp.fees) this.showFees = true

      } else if (this.viewAs === 'user') {
        if (this.processor.dmpu && this.processor.dmpu.fees) this.showFees = true
      }
    }
  }

  showLimitsMethod() {
    this.showLimits = false

    if(this.processor) {
      if (!this.viewAs) {
        if (this.processor.limits) this.showLimits = true

      } else if (this.viewAs === 'profile') {
        if (this.processor.dmpp && this.processor.dmpp.limits) this.showLimits = true

      } else if (this.viewAs === 'user') {
        if (this.processor.dmpu && this.processor.dmpu.limits) this.showLimits = true
      }
    }
  }

  checkVisibility(tab: string) {
    if(this.processor) {
      if (this.viewAs === 'profile') {

        for (let key in this.inputGroups) {
          this.inputGroups[key].showProfile = true
        }

        if (this.processor.dmpp === null || typeof this.processor.dmpp === 'undefined') {
          this.initProfile()
        }

        if (tab === 'fees') {
          this.showFees = true

          if (this.processor.dmpp && this.processor.dmpp.fees === null) this.processor.dmpp.fees = {}
          if (this.processor.dmpp && !this.processor.dmpp.fees) {
            this.initFees()
          }

        } else if (tab === 'limits') {
          this.showLimits = true

          if (this.processor.dmpp && this.processor.dmpp.limits === null) this.processor.dmpp.limits = {}
          if (this.processor.dmpp && !this.processor.dmpp.limits) {
            this.initLimits()
          }
        }

      } else if (this.viewAs === 'user') {

        for (let key in this.inputGroups) {
          this.inputGroups[key].showUser = true
        }

        if (this.processor.dmpu === null || typeof this.processor.dmpu === 'undefined') {
          this.initUser()
        }

        if (tab === 'fees') {
          this.showFees = true
          if (this.processor.dmpu && this.processor.dmpu.fees === null) this.processor.dmpu.fees = {}
          if (this.processor.dmpu && !this.processor.dmpu.fees) {
            this.initFees()
          }

        } else if (tab === 'limits') {
          this.showLimits = true
          if (this.processor.dmpu && this.processor.dmpu.limits === null) this.processor.dmpu.limits = {}
          if (this.processor.dmpu && !this.processor.dmpu.limits) {
            this.initLimits()
          }
        }
      }
    }
  }

  initLimits() {
    if (this.processor){
      if (this.viewAs === 'profile') {
        if (this.processor.dmpp) {
          this.processor.dmpp.limits = {id: null}
        }

      } else if (this.viewAs === 'user') {
        if (this.processor.dmpu) {
          this.processor.dmpu.limits = {id: null}
        }

      } else if (!this.viewAs) {
        if (this.processor && !this.processor.limits) {
          this.processor.limits = {id: null}
        }

        if (this.processor && !this.processor.domainLimits) {
          this.processor.domainLimits = {id: null}
        }
      }
    }
  }

  initFees() {
    if (this.processor) {
      if (this.viewAs === 'profile') {
        if(this.processor.dmpp  && !this.processor.dmpp.fees){
          this.processor.dmpp.fees = {id: null, strategy: 1}
        }

      } else if (this.viewAs === 'user') {
        if(this.processor.dmpu && !this.processor.dmpu.fees){
          this.processor.dmpu.fees = {id: null, strategy: 1}
        }

      } else if (!this.viewAs && !this.processor.fees) {
        this.processor.fees = {id: null, strategy: 1}
      }
    }
  }

  calculateFe() {
    const amount = this.deposit_amount

    if ( this.processor) {
      if (!this.processor.fe) this.processor.fe = {}

      switch (this.viewAs) {
        case 'user': {
          if (this.processor.dmpu && this.processor.dmpu.fees) {
            this.processor.fe.perc = this.processor.dmpu.fees.percentage;
            this.processor.fe.perc_amount = +(amount * (this.processor.fe.perc / 100)).toFixed(2)
            this.processor.fe.flatDec = !this.processor.dmpu.fees.flatDec ? 0 : this.processor.dmpu.fees.flatDec;
            this.processor.fe.minimumDec = !this.processor.dmpu.fees.minimumDec ? 0 : this.processor.dmpu.fees.minimumDec;
            this.processor.fe.fee_amount = (+this.processor.fe.flatDec + +this.processor.fe.perc_amount).toFixed(2);
            if (+this.processor.fe.minimumDec >= +this.processor.fe.fee_amount) {
              this.processor.fe.total_amount = +(amount - this.processor.fe.minimumDec).toFixed(2);
            } else {
              this.processor.fe.total_amount = +(amount - this.processor.fe.fee_amount).toFixed(2);
            }
          }
          break;
        }

        case 'profile': {
          if (this.processor.dmpp && this.processor.dmpp.fees) {
            this.processor.fe.perc = this.processor.dmpp.fees.percentage;
            this.processor.fe.perc_amount = +(amount * (this.processor.fe.perc / 100)).toFixed(2)
            this.processor.fe.flatDec = !this.processor.dmpp.fees.flatDec ? 0 : this.processor.dmpp.fees.flatDec;
            this.processor.fe.minimumDec = !this.processor.dmpp.fees.minimumDec ? 0 : this.processor.dmpp.fees.minimumDec;
            this.processor.fe.fee_amount = (+this.processor.fe.flatDec + +this.processor.fe.perc_amount).toFixed(2);
            if (+this.processor.fe.minimumDec >= +this.processor.fe.fee_amount) {
              this.processor.fe.total_amount = +(amount - this.processor.fe.minimumDec).toFixed(2);
            } else {
              this.processor.fe.total_amount = +(amount - this.processor.fe.fee_amount).toFixed(2);
            }
          }
          break;
        }

        default: {
          if (this.processor.fees) {
            this.processor.fe.perc = !this.processor.fees.percentage ? 0 : this.processor.fees.percentage.toFixed(2);
            this.processor.fe.perc_amount = +(amount * (this.processor.fe.perc / 100)).toFixed(2)
            this.processor.fe.flatDec = !this.processor.fees.flatDec ? 0 : this.processor.fees.flatDec.toFixed(2);
            this.processor.fe.minimumDec = !this.processor.fees.minimumDec ? 0 : this.processor.fees.minimumDec.toFixed(2);
            this.processor.fe.fee_amount = (+this.processor.fe.flatDec + +this.processor.fe.perc_amount).toFixed(2);
            if (+this.processor.fe.minimumDec >= +this.processor.fe.fee_amount) {
              this.processor.fe.total_amount = (amount - this.processor.fe.minimumDec).toFixed(2);
            } else {
              this.processor.fe.total_amount = (amount - this.processor.fe.fee_amount).toFixed(2);
            }
          }
          break;
        }
      }

      this.perc = this.processor.fe.perc
      this.perc_amount = this.processor.fe.perc_amount
      this.flatDec = this.processor.fe.flatDec
      this.minimumDec = this.processor.fe.minimumDec
      this.fee_amount = this.processor.fe.fee_amount
      this.total_amount = this.processor.fe.total_amount
    }
  }

  loadMore(initialRequest: boolean) {
    if ((initialRequest || (!initialRequest && this.hasMore)) && this.processor && this.processor.id) {
      this.getChangelogs(this.domain, this.processor.id, this.changeLogsPage)
    }
  }

  async getChangelogs(domain: string, id: number, page: number) {
    let changelogsList = []

    try {
      let result = await this.rootScope.provide.cashierConfigProvider.changelogs(domain, id, page)

      if(result) {
        changelogsList = result.list
        this.hasMore = result.hasMore
      }
    } catch (err) {
      this.logService.error(err)
    }

    try {
      let authorRes = await this.rootScope.provide.cashierConfigProvider.mapAuthorNameToChangeLogs(domain, changelogsList)

      if(authorRes) {
        if (this.changeLogs.length === 0) {
          this.changeLogs = [...authorRes]
        } else {
          this.changeLogs = [...this.changeLogs, ...authorRes]
        }
      }
    } catch (err) {
      this.logService.error(err)
    }

    this.changeLogsPage++
  }

  initProfile() {
    if (this.processor && this.profile && this.processor.id) {
      this.processor.dmpp = {
        id: null,
        profile: {id: this.profile.id},
        domainMethodProcessor: {id:  this.processor.id},
        weight: 0
      }
    }
  }

  initUser() {
    if (this.processor && this.user && this.processor.id) {
      this.processor.dmpu = {
        id: null,
        user: {guid: this.user.guid},
        domainMethodProcessor: {id: this.processor.id},
        weight: 0
      }
    }
  }

  get isDisabled() {
    if (!this.viewAs && this.processor) {
      return !this.processor.enabled

    } else if (this.viewAs === 'profile') {

      if (this.processor && this.processor.dmpp) {
        return !this.processor.dmpp.enabled
      }

    } else if (this.viewAs === 'user') {

      if (this.processor && this.processor.dmpu) {
        return !this.processor.dmpu.enabled
      }
    }
  }

  hasRole(role: string) {
    let arr: string[] = role.split(',')

    return arr.some( r => this.userService.hasRole(r) )
  }

  hasRoleForDomain (role, domain) {
    if(Array.isArray(role)) {
      const results:any = []
      role.forEach((el:any) => {
        let isHas =  this.userService.hasRoleForDomain(domain, el)
        results.push(isHas)
      })
      const isNotHasRole:boolean = results.find((elem:any) => elem === false)
      return !isNotHasRole ? false : true
    }

    if(typeof role === 'string') {
      const roles = role.split(',')
      if (roles.length > 1) {
        return roles.some(r => this.userService.hasRole(r))
      }
    }

    return  this.userService.hasRoleForDomain(domain, role)
  }

  showDeleteButton(tab: string): boolean {
    if(this.processor) {
      if(this.processor.dmpu) {
        return (this.processor.dmpu[tab] && this.showFees) || (this.processor.dmpu[tab] && this.showLimits)
      } else if (this.processor.dmpp) {
        return (this.processor.dmpp[tab] && this.showFees) || (this.processor.dmpp[tab] && this.showLimits)
      } else {
        return (this.processor[tab] && this.showFees) || (this.processor[tab] && this.showLimits)
      }

    }
    else return false
  }

  get dataTestName() : string {
    return this.user ? '-user' : this.profile ? '-profile' : 'general'
  }

  getCurrencyValue( cents: number) {
    return cents / 100
  }

  showHelp() {
    this.helpIsVisible = true
  }

  hideHelp() {
    this.helpIsVisible = false
  }

  changeStrategy(strategy: number | null) {
    if (this.viewAs === 'profile') {
      if(this.processor && this.processor.dmpp && this.processor.dmpp.fees) {
        this.processor.dmpp.fees.strategy = strategy
      }

    } else if (this.viewAs === 'user') {
      if(this.processor && this.processor.dmpu && this.processor.dmpu.fees) {
        this.processor.dmpu.fees.strategy = strategy
      }
    } else {
      if(this.processor && this.processor.fees) {
        this.processor.fees.strategy = strategy
      }
    }
  }

  getMethodType() {
    return this.type.toUpperCase()
  }

}
</script>

<style scoped>

</style>