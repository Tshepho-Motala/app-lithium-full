<template>
  <v-expansion-panel data-test-id="cashier-method" :class="isDisabled ? 'method-disabled-shadow' : ''" class="mb-1">
    <v-expansion-panel-header ripple="true">
      <div class="d-flex justify-space-between">
        <div class="d-flex align-center">
          <img :src="`data:image/png;base64, ${method.image ? method.image.base64 : ''}`" alt="processor's logo"
               class="cashier-method-logo mr-4" v-if="method.image"/>
          <div class="mr-5">
            <p>{{ method.name }}</p>
            <p>{{method.description}}</p>
            <p>{{ method.method ? method.method.name : '' }}</p>
          </div>
          <div class="d-flex pt-2">
            <span class="deep-purple lighten-4 pa-2 mr-2 border-radius-5"
                  v-if="method.accessRule">{{method.accessRule}}</span>
            <span class="green white--text pa-2 mr-2 border-radius-5"
                  v-if="!viewas && method.feDefault === true && method.deposit === true"
            >{{$translate('GLOBAL.FIELDS.DEFAULT')}}</span>
            <span class="red white--text pa-2 mr-2 border-radius-5"
                  v-if="isDisabled">{{$translate('GLOBAL.FIELDS.DISABLED')}}</span>
            <span class="override-icon domain" v-if="hasDMPLimits">
              <v-icon>mdi-tag-multiple</v-icon>
            </span>
            <span class="override-icon global" v-if="hasDMPFees || hasDMPLimits">
              <v-icon>mdi-tag-multiple</v-icon>
            </span>
            <span class="override-icon profile" v-if="hasDMPPFees || hasDMPPLimits">
              <v-icon>mdi-tag-multiple</v-icon>
            </span>
            <span class="override-icon individual" v-if="hasDMPUFees || hasDMPULimits">
              <v-icon>mdi-tag-multiple</v-icon>
            </span>
          </div>
        </div>
        <div class="d-flex align-center">
            <table data-test-id="tbi-method-accounting" class="table table-sm description-sm mb-0" v-if="method.accountingDay || method.accountingWeek || method.accountingMonth || method.accountingLastMonth">
              <tbody>
              <tr>
                <td data-test-id="tbl-accounting-general" class="domain d-flex flex-column">
                  <div class="bold-text">{{$translate(`UI_NETWORK_ADMIN.CASHIER_CONFIG.DOMAIN_${getMethodType()}_TOTALS`)}}</div>
                  <span v-if="method.accountingDay">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_TODAY')}} ({{method.accountingDay.tranCount}}):
                        {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingDay.debitCents : method.accountingDay.creditCents) }}
                      </span>
                  <span v-if="method.accountingWeek">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_WEEK')}} ({{method.accountingWeek.tranCount}}):
                        {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingWeek.debitCents : method.accountingWeek.creditCents) }}
                      </span>
                  <span v-if="method.accountingMonth">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_MONTH')}} ({{method.accountingMonth.tranCount}}):
                        {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingMonth.debitCents : method.accountingMonth.creditCents) }}
                    </span>
                  <span v-if="method.accountingLastMonth">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_LAST_MONTH')}} ({{method.accountingLastMonth.tranCount}}):
                        {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingLastMonth.debitCents : method.accountingLastMonth.creditCents) }}
                    </span>
                </td>
                <td data-test-id="tbl-accounting-user" class="user d-flex  flex-column" v-if="viewAs === 'user'">
                  <div class="bold-text">{{$translate(`UI_NETWORK_ADMIN.CASHIER_CONFIG.DOMAIN_${getMethodType()}_TOTALS`)}}</div>
                  <span v-if="method.accountingUserDay">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_TODAY')}} ({{method.accountingUserDay.tranCount}}):
                          {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingUserDay.debitCents : method.accountingUserDay.creditCents) }}
                    </span>
                  <span v-if="method.accountingUserWeek">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_WEEK')}} ({{method.accountingUserWeek.tranCount}}):
                          {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingUserWeek.debitCents : method.accountingUserWeek.creditCents) }}
                    </span>
                  <span v-if="method.accountingUserMonth">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_MONTH')}} ({{method.accountingUserMonth.tranCount}}):
                          {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingUserMonth.debitCents : method.accountingUserMonth.creditCents) }}
                    </span>
                  <span v-if="method.accountingUserLastMonth">
                          {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ACCOUNTING_LAST_MONTH')}} ({{method.accountingUserLastMonth.tranCount}}):
                          {{currency}}{{getCurrencyValue(type === 'deposit' ? method.accountingUserLastMonth.debitCents : method.accountingUserLastMonth.creditCents) }}
                    </span>
                </td>
              </tr>
              </tbody>
            </table>
          <v-menu offset-y :close-on-click="true" :close-on-content-click="true" class="light-green accent-2">
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" class="ma-4 mb-5 border-none" outlined data-test-id="btn-toggle-method-menu">
              <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>
          </template>
          <div class="d-flex flex-column white">
            <div v-if="!user && !profile">
              <v-btn
                  data-test-id="btn-show-modify-method"
                  class="mx-3 border-none"
                  outlined
                  @click.stop="showModifyDialog">
                {{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.MENU.EDITMETHOD')}}
              </v-btn>
              <v-dialog
                  transition="dialog-top-transition"
                  max-width="600"
                  v-model="modifyDialog"
              >
                <v-card>
                  <v-toolbar
                      color="primary"
                      dark
                  >{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.TITLE')}}
                  </v-toolbar>
                  <v-card-text>
                    <div>
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.NAME')}}</p>
                      <v-text-field
                          data-test-id="txt-modified-method"
                          v-model="modifiedMethod"
                          outlined
                          dense
                          disabled
                      />
                      <p class="text-muted">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.DESCRIPTION')}}</p>
                    </div>
                    <div>
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.NAME.NAME')}}</p>
                      <v-text-field
                          data-test-id="txt-modified-method-name"
                          v-model="modifiedMethodName"
                          outlined
                          dense
                          :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.NAME.PLACEHOLDER')"
                      />
                      <p class="text-muted">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.NAME.DESCRIPTION')}}</p>
                    </div>
                    <div>
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PROCESOR_DESCRIPTION.NAME')}}</p>
                      <v-text-field
                          data-test-id="txt-modified-method-descr"
                          v-model="modifiedMethodDescr"
                          outlined
                          dense
                          :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PROCESOR_DESCRIPTION.PLACEHOLDER')"
                      />
                      <p class="text-muted">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PROCESOR_DESCRIPTION.DESCRIPTION')}}</p>
                    </div>
                    <div>
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.IMAGE.NAME')}}</p>
                      <v-file-input
                          data-test-id="fil-modified-method-image"
                          :label="modifiedImageDescr"
                          v-model="modifiedImage"
                          dense
                          prepend-icon="mdi-camera"
                          clearable
                          show-size counter
                          @change="showImage"
                          accept="image/png, image/jpeg, image/bmp"
                      ></v-file-input>
                      <div class="d-flex align-center justify-center">
                        <img v-if="imageUrl" :src="imageUrl" style="max-width: 35%;max-height: 45%;" alt="Downloaded image">
                        <img v-else-if="method.image && method.image.base64" style="max-width: 35%;max-height: 45%;" class="img-thumbnail img-responsive"
                             :src="`data:${method.image.filetype ? method.image.filetype : 'image/PNG' };base64,${ method.image.base64 }`"
                             alt=""/>
                      </div>
                      <p class="text-muted">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.IMAGE.DESCRIPTION')}}</p>
                    </div>

                    <div>
                      <p>
                        {{$translate('GLOBAL.ACCESSRULE.LABEL')}}<span>({{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.EVALUATED_PRIOR')}})</span>
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
                          placeholder="Select Access Rule"
                      />
                      <p class="text-muted">{{$translate('GLOBAL.ACCESSRULE.PLACEHOLDER')}}</p>
                    </div>
                    <div>
                      <p>
                        {{$translate('GLOBAL.ACCESSRULE.LABEL')}}<span>({{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.EVALUATED_PRIOR')}})</span>
                      </p>
                      <v-autocomplete
                          data-test-id="slt-modified-rule-transition"
                          v-model="modifiedAccessRuleTransition"
                          :items="accessRules"
                          item-text="name"
                          item-value="name"
                          return-object
                          outlined
                          dense
                          clearable
                          placeholder="Select Access Rule"
                      />
                      <p class="text-muted">{{$translate('GLOBAL.ACCESSRULE.PLACEHOLDER')}}</p>
                    </div>

                  </v-card-text>
                  <v-card-actions class="justify-end">
                    <v-btn
                        data-test-id="btn-modify-method"
                        @click="modifyMethod"
                        class="mr-2 green lighten-2"
                        :disabled="!modifiedMethodName">
                      {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SUBMIT')}}
                    </v-btn>
                    <v-btn data-test-id="btn-hide-modify-dialog" @click="modifyDialog = false">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}</v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </div>
            <div>
              <v-btn
                  data-test-id="btn-method-enable"
                  @click="changeMethodStatus(method.id)"
                  class="mx-3 mb-0 border-none"
                  outlined
                  v-if="isDisabled"
              >{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.MENU.ENABLEMETHOD')}}
              </v-btn>
              <v-btn
                  data-test-id="btn-method-disable"
                  @click="changeMethodStatus(method.id)"
                  class="mx-3 mb-0 red lighten-4 border-none"
                  outlined
                  v-if="!isDisabled && method"
              >{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.MENU.DISABLEMETHOD')}}
              </v-btn>
            </div>
            <div v-if="!user && !profile">
              <v-btn
                  data-test-id="btn-set-default"
                  @click="setAsDefault(true, method.id)"
                  class="mx-3 mb-0 red lighten-4 border-none"
                  outlined
                  v-if="((!viewas && (method.feDefault === false || method.feDefault === null) && method.deposit === true))"
              >{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.MENU.SET_AS_DEFAULT_METHOD')}}
              </v-btn>
              <v-btn
                  data-test-id="btn-unset-default"
                  @click="setAsDefault(false, method.id)"
                  class="mx-3 mb-0 red lighten-4 border-none"
                  outlined
                  v-if="((!viewas && method.feDefault === true && method.deposit === true))"
              >{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.MENU.REMOVE_DEFAULT_METHOD')}}
              </v-btn>
            </div>
            <div v-if="!user && !profile">
              <v-btn
                  data-test-id="btn-show-add-processor"
                  @click.stop="showAddProcessorDialog"
                  class="mx-3 mb-0 border-none"
                  outlined>
                {{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.MENU.ADDPROCESSOR')}}
              </v-btn>
              <v-dialog
                  transition="dialog-top-transition"
                  max-width="600"
                  v-model="addProcessorDialog"
              >
                <v-card>
                  <v-toolbar
                      color="primary"
                      dark
                  >{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.TITLE')}}
                  </v-toolbar>
                  <v-card-text>
                    <div class="mt-5">
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.NAME')}}</p>
                      <v-text-field
                          data-test-id="txt-modified-method"
                          v-model="modifiedMethod"
                          outlined
                          dense
                          disabled
                      />
                      <p class="text-muted">
                        {{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.METHOD.DESCRIPTION')}}</p>
                    </div>
                    <div>
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.NAME')}}</p>
                      <v-text-field
                          data-test-id="txt-added-processor-name"
                          v-model="addedProcessorDescription"
                          outlined
                          dense
                          :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.PLACEHOLDER')"
                      />
                      <p class="text-muted">{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.DESCRIPTION')}}</p>
                    </div>
                    <div>
                      <p class="text-muted">{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.NAME')}}</p>
                      <v-autocomplete
                          data-test-id="slt-added-processor"
                          v-model="addedProcessor"
                          :items="availableProcessors"
                          item-text="name"
                          item-value="name"
                          return-object
                          outlined
                          dense
                          clearable
                          :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.PLACEHOLDER')"
                      />
                      <p class="text-muted">{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.DESCRIPTION')}}</p>
                    </div>
                  </v-card-text>
                  <v-card-actions class="justify-space-between d-flex">
                    <v-btn data-test-id="confirm-add-processor" :disabled="!addedProcessor || (addedProcessor && !(addedProcessor.name)) || !addedProcessorDescription" @click="createNewProcessor">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SUBMIT')}}</v-btn>
                    <v-btn data-test-id="hide-add-processor" @click="addProcessorDialog = false">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}</v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
              <v-dialog
                  transition="dialog-top-transition"
                  max-width="800"
                  v-model="propertiesDialog"
                  retain-focus="false"
              >
                <v-card>
                  <v-toolbar
                      color="primary"
                      dark
                  >{{$translate('UI_NETWORK_ADMIN.CASHIER.PROCESSORS.PROPERTIES.TITLE')}}
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
            <v-btn
                data-test-id="btn-change-processor-order"
                @click="editingOrder = true"
                v-if="!editingOrder"
                v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                class="mx-3 mb-0 border-none"
                outlined
            >{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGE_ORDER')}}</v-btn>
            <v-btn
                data-test-id="btn-save-processor-order"
                @click="saveOrder"
                v-if="editingOrder"
                v-show="hasRoleForDomain('CASHIER_CONFIG_EDIT', domain)"
                class="mx-3 mb-0 border-none"
                outlined
            >{{$translate('GLOBAL.ACTION.SAVEORDER')}}</v-btn>
            <div v-if="!user && !profile">
              <v-divider></v-divider>
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
                      {{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.DELETE.WARNING') }}</p>
                    <p class="mb-3">{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.DELETE.WARNING2') }}</p>
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
                        @click="deleteMethod(method)"
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
          </div>
        </v-menu>
        </div>
      </div>
    </v-expansion-panel-header>
    <v-expansion-panel-content>
        <template v-if="methodProcessors.length > 0">
          <draggable tag="v-expansion-panels" v-model="methodProcessors" class="w-100" :disabled="!editingOrder" @change="editingProcessorOrder = true">
            <cashier-processor
                v-for="(processor, index) in methodProcessors"
                :key="`${index}_${processor.id}`"
                :props-processor="processor"
                :currency="currency"
                :viewAs="viewAs"
                @changeProcessor="changeProcessor"
                :user="user"
                :profile="profile"
                :domain="domain"
                @delete="deleteProcessor"
                :type="type"
            />
          </draggable>
        </template>
      <template v-else>
        <v-expansion-panels>
            <div class="d-flex flex-column w-100 align-center">
              <v-divider></v-divider>
              <span>
                No processors setup
                {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.NO_PROCESSORS_SETUP')}}
              </span>
            </div>
        </v-expansion-panels>
      </template>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>

<script lang="ts">
import {Component, Inject, Prop, Mixins, Watch} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";

import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import CashierProcessor from "@/plugin/cashier/config/CashierProcessor.vue";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import {
  AccessRuleInterface,
  AddedProcessorInterface,
  CashierConfigDmpp,
  CashierConfigDmpu,
  CashierConfigLimits,
  CashierConfigMethod,
  CashierConfigProcessor,
  CashierConfigProcessorProperty,
  CashierConfigProfile,
  CashierConfigUser,
  NewAddedProcessorInterface
} from "@/core/interface/cashierConfig/CashierConfigInterface";
import TranslationMixin from "@/core/mixins/translationMixin";

import draggable from "vuedraggable";


@Component({
  components: {
    CashierProcessor,
    draggable
  }
})
export default class CashierMethod extends Mixins(TranslationMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  @Prop({default: '$'}) currency!: string
  @Prop() method!: CashierConfigMethod
  @Prop({default: ''}) viewAs!: string
  @Prop() user?: CashierConfigUser
  @Prop() profile?: CashierConfigProfile
  @Prop() domain!: string
  @Prop() filter?: boolean
  @Prop() type!: string

  addProcessorDialog: boolean = false
  modifyDialog: boolean = false
  methodProcessors: CashierConfigProcessor[] = []
  modifiedMethod: string | null = null
  modifiedMethodName: string = ''
  modifiedMethodDescr: string | null = null
  modifiedImage: any = null
  modifiedAccessRuleProcessor: AccessRuleInterface | null = null
  modifiedAccessRuleTransition: AccessRuleInterface | null = null
  modifiedImageDescr: string | null = null
  imageUrl: string | null = null
  accessRules: AccessRuleInterface[] = []
  hasDMPFees: boolean = false
  hasDMPLimits: boolean = false
  hasDMPPFees: boolean = false
  hasDMPPLimits: boolean = false
  hasDMPUFees: boolean = false
  hasDMPULimits: boolean = false
  tempMethodProcessors: CashierConfigProcessor[] = []
  deleteDialog: boolean = false
  deleteConfirm: string = ''
  availableProcessors: AddedProcessorInterface[] = []
  addedProcessorDescription: string = ''
  addedProcessor: AddedProcessorInterface | null = null
  addedProcessorId: number | null = null
  propertiesDialog: boolean = false
  processorProperties: CashierConfigProcessorProperty[] = []
  propertiesSearchedValue: string = ''
  editingProcessorOrder: boolean = false // means there were some changes in order
  editingOrder: boolean = false // means we enable/disable order editing


  mounted() {
    if (this.method) {
      if(this.method.method) {
        this.modifiedMethod = this.method.method.name
        this.modifiedImageDescr = this.method.method.image.filename
      }

      this.modifiedMethodName = this.method.name
      this.modifiedMethodDescr = this.method.description
    }

    this.getProcessors()
  }

  async getProcessors() {

    try {
      let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessors(this.method.id)

      if (result) {
        this.tempMethodProcessors = result.plain()
      }
    } catch (err) {
      this.logService.error(err)
    }

    this.tempMethodProcessors.forEach(item => {

      if (item.fees) {
        this.method.hasDMPFees = true
        this.hasDMPFees = true
      }
      if (item.limits) {
        this.method.hasDMPLimits = true;
        this.hasDMPLimits = true
      }
      if ((item.limits) && (!item.domainLimits)) {
        item.domainLimits = { ...this.initLimits()}
      }

      if( item.id) {
        this.getProcessorAccounting(item.id)

        item.changelog = {
          domainName: this.domain,
          entityId: item.id,
          restService: this.rootScope.provide.cashierConfigProvider.cashierDmpRest,
          reload: 0,
          collapsed: false
        }
      }
    })

    if (!this.viewAs) {
      this.methodProcessors = this.tempMethodProcessors
    }
  }

  @Watch('filter')
  async onFilterChanged() {
    if (this.filter) {

      await this.getProcessors()

      if (this.viewAs === 'profile' && this.profile) {
        await this.findProcessorsByProfile(this.profile)

      } else if (this.viewAs === 'user' && this.user) {
        await this.findProcessorsByUser(this.user)
      }
    }
  }


  async saveOrder() {
    let total = this.methodProcessors.length

    if (this.editingProcessorOrder && this.editingOrder) {

      if (this.viewAs === 'profile' && this.profile) {

        const dmpps: CashierConfigDmpp[]  = []

        this.methodProcessors.forEach( (item, index) => {

          if((item.dmpp === null || typeof item.dmpp == 'undefined') && this.profile ) {
            item.dmpp = {
              id: null,
              profile: {id: this.profile.id},
              domainMethodProcessor: {id:  item.id},
              weight: ((total - index)*0.1)
            }
          }
          if (item.dmpp) {
            if(item.dmpp.id != null){
              item.dmpp.weight = ((total - index) * 0.1)
            }

            dmpps.push(item.dmpp)
          }
        })

        try {
          await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileUpdateMultiple(dmpps)

        } catch(err) {
          this.logService.error(err)
        }

        await this.findProcessorsByProfile(this.profile)

      } else if (this.viewAs === 'user' && this.user) {

        const dmpus: CashierConfigDmpu[]  = []

        this.methodProcessors.forEach( (item, index) => {
        // || typeof item.dmpu == 'undefined')
          if( (item.dmpu === null || typeof item.dmpu == 'undefined')  && this.user) {
            item.dmpu = {
              id: null,
              user: {guid: this.user.guid},
              domainMethodProcessor: {id: item.id},
              weight: ((total - index)*0.1)
            }
          }
          if (item.dmpu) {
            if(item.dmpu.id != null) {
              item.dmpu.weight = ((total - index) * 0.1)
            }
            dmpus.push(item.dmpu)
          }
        })

        try {
          await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUserUpdateMultiple(dmpus)

        } catch(err) {
          this.logService.error(err)
        }

        await this.findProcessorsByUser(this.user)

      } else {
        this.methodProcessors.forEach( (item, index) => {
          item.weight = ((total - index) * 0.1)
        })

        try {
          await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorUpdateMultiple(this.methodProcessors)
        } catch(err) {
          this.logService.error(err)
        }

        await this.getProcessors()
      }
    }

    this.editingProcessorOrder = false
    this.editingOrder = false
  }

  async findProcessorsByProfile(profile: CashierConfigProfile) {
    try {
      let dmpps = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorsByProfileNoImage(profile)

          if(dmpps) {
            let tempProfileProcessors = dmpps.plain()

            tempProfileProcessors.forEach(dmpp => {

              this.tempMethodProcessors.forEach(dmp => {

                if (dmp.id && dmp.id === dmpp.domainMethodProcessor.id) {
                  dmp.dmpp = dmpp;

                  if (dmpp.fees) {
                    dmp.hasDMPPFees = true;
                  }
                  if (dmpp.limits) {
                    dmp.hasDMPPLimits = true;
                  }
                }
              })
            })
          }
    } catch (err) {
      this.logService.error(err)
    }

    this.methodProcessors = this.tempMethodProcessors.sort((a, b) =>
          (a.dmpp ? a.dmpp.weight : 0)  < (b.dmpp ? b.dmpp.weight : 0)  ? 1 : -1
    )
  }

  async findProcessorsByUser(user: CashierConfigUser) {
    try {
      let dmpus = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorsByUserNoImage(user.guid)

      if(dmpus) {
        let tempUserProcessors = dmpus.plain()

        tempUserProcessors.forEach(dmpu => {

          this.tempMethodProcessors.forEach(dmp => {

            if (dmp.id && dmp.id === dmpu.domainMethodProcessor.id) {
              dmp.dmpu = dmpu;

              if (dmpu.fees) {
                dmp.hasDMPUFees = true;
              }
              if (dmpu.limits) {
                dmp.hasDMPULimits = true;
              }
            }

            if(dmp.id) {
              this.getProcessorAccountingUser(dmp.id, user.username)
            }
          })
        })
      }
    } catch (err) {
      this.logService.error(err)
    }

    this.methodProcessors = this.tempMethodProcessors.sort((a, b) =>
            (a.dmpu ? a.dmpu.weight : 0)  < (b.dmpu ? b.dmpu.weight : 0)  ? 1 : -1
    )
  }

  setAsDefault(status: boolean, id: number | string) {
    this.method.feDefault = status
    let emittedMethod = {
      id: id,
      status: status
    }
    this.$emit('default', emittedMethod)
  }

  showModifyDialog() {
    this.modifyDialog = true
    this.getAccessRules()
  }


  changeMethodStatus(id: number) {
    this.$emit('status', id)
  }

 async modifyMethod() {
  this.method.name = this.modifiedMethodName
  this.method.description = this.modifiedMethodDescr
  this.method.accessRule = this.modifiedAccessRuleProcessor ? this.modifiedAccessRuleProcessor.name : null
  this.method.accessRuleOnTranInit = this.modifiedAccessRuleTransition ? this.modifiedAccessRuleTransition.name : null

  let copiedMethod = await this.rootScope.provide.cashierConfigProvider.methodCopy(this.method)

  await this.rootScope.provide.cashierConfigProvider.domainMethodUpdate(copiedMethod)

  this.$emit('reload')
    this.modifyDialog = false
  }

  deleteMethod(item: CashierConfigMethod) {
    this.$emit('delete-method', item)
  }

  showImage(file: any | null) {
    if(file && file.name) {
      this.imageUrl = URL.createObjectURL(file);

      const reader = new FileReader()
      reader.onload = (e: any) => {
        if (e !== null && e.target !== null) {

          let cuttedBase64 = e.target.result.slice(e.target.result.indexOf('base64,') + 7)

          let newImage = {
            base64: cuttedBase64,
            filename: file.name,
            filetype: file.type,
            filesize: file.size,
            id: this.method.method.image.id + 1,
            version: this.method.method.image.version,
          }

          this.method.method.image = newImage
          this.method.image = newImage
        }
      }
      reader.readAsDataURL(file)

    } else {
      this.imageUrl = null

      let newImage = {
        base64: '',
        filename: '',
        filetype: '',
        filesize: '',
        id: this.method.method.image.id + 1,
        version: this.method.method.image.version,
      }

      this.method.method.image = newImage
      this.method.image = newImage
    }
  }

  async changeProcessor(processor: CashierConfigProcessor) {

    switch (this.viewAs) {
      case 'user': {
        this.methodProcessors.forEach( item => {
          if(item.id && item.id === processor.id) {
            item = { ...processor}
            if (processor.dmpu && processor.dmpu.fees) {
              this.method.hasDMPUFees = true
              this.hasDMPUFees = true
            } else {
              this.method.hasDMPUFees = false
              this.hasDMPUFees = false
            }

            if (processor.dmpu && processor.dmpu.limits) {
              this.method.hasDMPULimits = true
              this.hasDMPULimits = true
            } else {
              this.method.hasDMPULimits = false
              this.hasDMPULimits = false
            }

          }
        })
        break;
      }
      case 'profile' : {
        this.methodProcessors.forEach( item => {
          if(item.id && item.id === processor.id) {
            item = { ...processor}
            if (processor.dmpp && processor.dmpp.fees) {
              this.method.hasDMPPFees = true
              this.hasDMPPFees = true
            } else {
              this.method.hasDMPPFees = false
              this.hasDMPPFees = false
            }

            if (processor.dmpp && processor.dmpp.limits) {
              this.method.hasDMPPLimits = true
              this.hasDMPPLimits = true
            } else {
              this.method.hasDMPPLimits = false
              this.hasDMPPLimits = false
            }

          }
        })
        break;
      }
      default: {
        this.methodProcessors.forEach( item => {
          if(item.id && item.id === processor.id) {
            item = { ...processor}
            if (processor.fees) {
              this.method.hasDMPFees = true
              this.hasDMPFees = true

              if (item.fees) {
                item.fees.strategy = processor.fees.strategy === 1 ? 1 : 2
              }

            } else {
              this.method.hasDMPFees = false
            }

            if (processor.limits) {
              this.method.hasDMPLimits = true
              this.hasDMPLimits = true
            } else {
              this.method.hasDMPLimits = false
            }
          }
        })
      }
    }
  }

  async getProcessorAccounting(id: number) {
    try {
      let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorAccounting(id)

      if(result) {
        this.tempMethodProcessors.forEach((item) => {
          if (item.id === id) {
            item.accountingDay = result.plain()[0]['day'];
            item.accountingWeek = result.plain()[0]['week'];
            item.accountingMonth = result.plain()[0]['month'];
            item.accountingLastMonth = result.plain()[0]['lastmonth'];
          }
        })
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async getProcessorAccountingUser(id: number, username: string) {
    try {
      let list = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorAccountingUser(id, username)

      if(list) {
        this.tempMethodProcessors.forEach((item) => {
          if(item.id === id) {
            item.accountingUserDay = list.plain()[0]['day']
            item.accountingUserWeek = list.plain()[0]['week']
            item.accountingUserMonth = list.plain()[0]['month']
            item.accountingUserLastMonth = list.plain()[0]['lastmonth']
          }
        })
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  deleteProcessor(processor: CashierConfigProcessor) {
    this.rootScope.provide.cashierConfigProvider.deleteProcessor(processor)


    this.methodProcessors = this.methodProcessors.filter(item => {
      if (item.id) item.id !== processor.id
    })
  }

  initLimits() {
    let limits: CashierConfigLimits = {
      id: null,
      minAmount: null,
      maxAmount: null,
      maxAmountDay: null,
      maxAmountWeek: null,
      maxAmountMonth: null,
      maxTransactionsDay: null,
      maxTransactionsWeek: null,
      maxTransactionsMonth: null,
    }

    return  limits
  }

  async getAccessRules() {
    try {
      let result = await this.rootScope.provide.cashierConfigProvider.getAccessRules(this.domain)

      if(result) {
        this.accessRules = result.plain()

        this.accessRules.forEach((item) => {
          if (this.method && item.name === this.method.accessRule) this.modifiedAccessRuleProcessor = item
          if (this.method && item.name === this.method.accessRuleOnTranInit) this.modifiedAccessRuleTransition = item
        })
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  get isDisabled() {
    if (!this.viewAs) {
      return !this.method.enabled

    } else if (this.viewAs === 'profile') {

      if (this.method.domainMethodProfile) {
        return !this.method.domainMethodProfile.enabled
      }

    } else if (this.viewAs === 'user') {

      if (this.method.domainMethodUser) {
        return !this.method.domainMethodUser.enabled
      }
    }
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

  async createNewProcessor() {
    if (this.addedProcessor) {

      let tempAddedProcessor: NewAddedProcessorInterface = {
        description: this.addedProcessorDescription,
        domainMethod: this.method,
        enabled: true,
        processor: {
          id: this.addedProcessor.id
        },
        weight: 0
      }

      let addedProcessorResponse: any = null

      try {
        let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProcessorAdd(tempAddedProcessor)

        if (result) {
          addedProcessorResponse = result.plain()
          this.addedProcessorId = addedProcessorResponse.id

          if(this.addedProcessorId) {
            await this.getProperties( this.addedProcessorId )
          }
        }
      } catch (err) {
        this.logService.error(err)
      }
    }

    this.addProcessorDialog = false
    this.propertiesDialog = true
  }

  async showAddProcessorDialog(){
    this.addProcessorDialog = true

    try {
      let result = await this.rootScope.provide.cashierConfigProvider.processors(this.method.method.id, this.type)

      if(result) this.availableProcessors = result.plain()

    } catch (err) {
      this.logService.error(err)
    }
  }

  async getProperties(id: number) {

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

  async saveProperties() {
    this.propertiesDialog = false

    if(this.addedProcessor && this.addedProcessorId) {
      try {
        let result = await this.rootScope.provide.cashierConfigProvider.saveProperties(this.addedProcessorId, this.processorProperties)

        if (result) {
          this.processorProperties = result.plain()
        }
      } catch (err) {
        this.logService.error(err)
      }

      await this.getProcessors()
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

  getMethodType() {
    return this.type.toUpperCase()
  }

  getCurrencyValue(cents: number) {
    return cents / 100
  }
}
</script>

<style scoped>
</style>