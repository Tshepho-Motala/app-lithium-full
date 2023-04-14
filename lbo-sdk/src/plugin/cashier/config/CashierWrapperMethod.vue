<template>
  <div :data-test-id="`cashier-${type}-method`">
    <div class="mx-4">
      <div class="d-flex mb-3 align-center">
        <div v-if="!userOrProfileIsChosen">
          <v-btn
              data-test-id="btn-show-add-dialog"
              @click.stop="showAddMethodDialog"
              v-show="hasRoleForDomain('CASHIER_CONFIG_ADD,CASHIER_CONFIG_EDIT', domain)"
              class="mr-2">
            {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.ADD')}}
          </v-btn>
          <v-dialog
              transition="dialog-top-transition"
              max-width="600"
              v-model="addMethodDialog"
          >
            <v-card>
              <v-toolbar
                  color="primary"
                  dark
              >{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.ADD.TITLE')}}</v-toolbar>
              <v-card-text  class="pt-4">
                <p>{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.NAME')}}</p>
                <v-text-field data-test-id="txt-new-method-name" v-model="newMethodName" outlined dense />
                <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.NAME.DESCRIPTION')}}</p>
                <p>{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.METHOD')}}</p>
                <v-autocomplete
                    data-test-id="slt-new-associated-method"
                    v-model="newMethod"
                    :items="addMethods"
                    item-text="name"
                    item-value="name"
                    return-object
                    outlined
                    dense
                    clearable
                />
                <p class="">{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.DESCRIPTION')}}</p>
              </v-card-text>
              <v-card-actions class="d-flex justify-space-between">
                <v-btn
                    data-test-id="btn-add-method"
                    @click="addMethod"
                    class="mr-2 green lighten-2"
                    :disabled="!(newMethodName  && newMethod)">
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SUBMIT')}}
                </v-btn>
                <v-btn
                    data-test-id="btn-hide-method-dialog"
                    @click="addMethodDialog = false"
                    class="mr-2 deep-orange lighten-2" >
                  {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </div>
        <v-btn data-test-id="btn-change-order" v-show="hasRoleForDomain('CASHIER_CONFIG_ADD,CASHIER_CONFIG_EDIT', domain)" v-if="!editingOrder" @click="editingOrder = true" class="mr-2">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGE_ORDER')}}</v-btn>
        <v-btn data-test-id="btn-save-order" v-show="hasRoleForDomain('CASHIER_CONFIG_ADD,CASHIER_CONFIG_EDIT', domain)" v-if="editingOrder" @click="saveOrder" class="mr-2">{{$translate('GLOBAL.ACTION.SAVEORDER')}}</v-btn>
        <v-btn v-if="userOrProfileIsChosen" data-test-id="btn-panel-clear" @click="clear" class="mr-2">{{$translate('GLOBAL.ACTION.CLEAR')}}</v-btn>
        <v-text-field
            data-test-id="slt-filtered-method"
            v-model="filteredValue"
            placeholder="Enter a search string"
            outlined
            dense
            clearable
            hide-details
        ></v-text-field>
      </div>
      <!--     Filter extension-->
      <v-expansion-panels class="mb-4" v-if="!userOrProfileIsChosen">
        <v-expansion-panel>
          <v-expansion-panel-header   ripple="true" expand-icon="$expand" class="teal accent-1" @click="getProfiles">
            {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SELECT_FILTERS')}}
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <p class="pt-2">Select profile</p>
            <v-autocomplete
                data-test-id="slt-profile"
                v-model="profile"
                :items="profiles"
                item-text="name"
                item-value="name"
                return-object
                outlined
                dense
                clearable
                @change="clearUser"
                class="mt-4"
                hide-no-data
            ></v-autocomplete>
            <div class="d-flex justify-center align-center">
              <p class="pb-0">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OR')}}</p>
            </div>
            <p>Select user</p>
            <v-autocomplete
                data-test-id="slt-user"
                v-model="user"
                :items="users"
                item-text="username"
                item-value="username"
                return-object
                outlined
                dense
                clearable
                @change="clearProfile"
                hide-no-data
                :search-input.sync="searchUser"
            ></v-autocomplete>
            <div class="d-flex justify-end">
              <v-btn data-test-id="btn-filter" @click="filter">{{$translate('GLOBAL.ACTION.FILTER')}}</v-btn>
            </div>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
      <div class="pa-5" v-else>
        <div class="d-flex flex-column align-center">
          <div class="d-flex align-center" v-if="user">
            <v-icon class="mr-3" color="#2196f3" x-large>mdi-account</v-icon>
            <div class="d-flex ml-5">
              <span style="color: #2196f3;" class="font-weight-bold">{{user.firstName}} {{user.lastName}} ({{user.username}})</span>
            </div>
          </div>
          <div v-if="user && userProfile">
            <span style="color: #009688;"><span class="font-weight-bold">{{$translate('UI_NETWORK_ADMIN.CASHIER.LEGEND.USERPROFILE')}}:</span> {{userProfile.code}} - {{userProfile.description}}</span>
          </div>
          <div class="d-flex align-center" v-if="profile">
            <v-icon class="mr-3" color="#009688" x-large>mdi-account-group</v-icon>
            <div class="d-flex ml-5">
              <span style="color: #009688;">{{profile.name}}</span>
            </div>
          </div>
          <p class="font-weight-bold">{{$translate('UI_NETWORK_ADMIN.CASHIER.LEGEND.USERFILTERDESC')}}</p>
          <p class="text-muted">({{$translate('UI_NETWORK_ADMIN.CASHIER.LEGEND.NOTE')}})</p>
        </div>
      </div>
      <!--    End of filter extension-->
      <div v-if="!reloading">
        <draggable tag="v-expansion-panels" v-model="changedOrderMethods" class="w-100" :disabled="!editingOrder" @change="editingMethodOrder = true">
          <cashier-method v-for="(method, index) in filteredMethods"
                          :key="`${index}_${method.id}`"
                          :method="method"
                          :currency="currency"
                          :viewAs="viewAs"
                          :user="user"
                          :profile="profile"
                          :domain="domain"
                          :filter="startFilter"
                          :type="type"
                          @default="setDefault"
                          @delete-method="deleteMethod"
                          @reload="reload"
                          @status="changeStatus"
          />
        </draggable>
        <div v-if="!filteredMethods.length">
          <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.METHODS.SEARCHEMPTY')}}</p>
        </div>
      </div>
      <div class="d-flex justify-center py-5" v-else>
        <v-progress-circular
            indeterminate
            color="primary"
            :size="70"
            :width="7"
        ></v-progress-circular>
      </div>
    </div>
    <div class="d-flex justify-space-between ma-5">
      <div class="d-flex flex-column">
        <div v-for="( ride,index ) in overRidesList" :key="index" class="d-flex mb-2 align-center">
          <span  :class="ride.color" class="override-icon">
            <v-icon>mdi-tag-multiple</v-icon>
          </span>
          <span>- &nbsp; {{ride.description}}</span>
        </div>
      </div>
      <div style="width: 40%;">
        <p v-if="!user" class="pa-4 elevation-4">{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.NOSELECTED')}}</p>
        <template v-else>
          <div class="d-flex" v-if="user">
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.EXAMPLE_CASHIER')}} {{user.firstName}}{{user.lastName}}({{user.username}})</p>
          </div>
          <div v-for="(cm, index) in comments" :key="index" class="elevation-3 mb-3">
            <cashier-config-comment-component comment="cm" />
          </div>
          <v-btn
            @click="showMoreComments"
          >
            <v-icon>
              mdi-refresh
            </v-icon>
            Refresh
          </v-btn>
        </template>
      </div>
    </div>
    <div>
      <div class="mx-5" v-if="!user">
        <p>{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGE_HISTORY')}}</p>
        <div v-for="(log,index) in changeLogs" :key="index">
          <change-log :log="log" />
        </div>
        <div class="d-flex justify-content-start">
          <div class="d-flex align-center justify-center pa-2" style="border-radius: 50%; background-color: #666; max-width: 40px;">
            <v-icon>
              mdi-clock-outline
            </v-icon>
          </div>
          <v-btn
              @click="loadMore(false)"
              class="blue--text ml-4 border-none"
          >{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.LOADMORE')}}</v-btn>
        </div>
      </div>
      <div v-else>
        <p class="d-flex justify-center" v-if="user">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.EXAMPLE_CASHIER')}} {{user.firstName}}{{user.lastName}}({{user.username}})</p>
        <div>
          <p>{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CHANGE_HISTORY')}}</p>
        </div>
        <div v-for="(log,index) in changeLogs" :key="index">
          <change-log :log="log" />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Prop, Mixins, Watch} from "vue-property-decorator";
import draggable from 'vuedraggable'
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import ChangeLog from "@/plugin/cashier/config/components/ChangeLog.vue";
import CashierMethod from "@/plugin/cashier/config/CashierMethod.vue";
import CashierConfigCommentComponent from "@/plugin/cashier/config/components/CashierConfigCommentComponent.vue";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import {
  AddedProcessorInterface,
  CashierConfigChangelog,
  CashierConfigComment, CashierConfigMethod, CashierConfigOverride,
  CashierConfigProfile,
  CashierConfigUser,
} from "@/core/interface/cashierConfig/CashierConfigInterface";

import RoleCheck from '@/plugin/components/PermissionValidatior.vue'
import '@/core/directive/role-check/RoleCheckDirective'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import TranslationMixin from "@/core/mixins/translationMixin";

@Component({
  components: {
    ChangeLog,
    CashierMethod,
    CashierConfigCommentComponent,
    RoleCheck,
    draggable
  }
})
export default class CashierWrapperMethod extends Mixins(TranslationMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  @Prop() currency!: string
  @Prop() domain!: string
  @Prop() type!: string
  @Prop() tab!: number

  userOrProfileIsChosen: boolean = false
  addMethodDialog: boolean = false
  newMethodName: string = ''
  newMethod: AddedProcessorInterface | null = null
  filteredValue: string = ''
  profile: CashierConfigProfile | null = null
  user: CashierConfigUser | null = null
  viewAs: string = ''
  hasMore: boolean = false
  methods: CashierConfigMethod[] = []
  users: CashierConfigUser[] = []
  profiles: CashierConfigProfile[] = []
  comments: CashierConfigComment[] = []
  tempComments: CashierConfigComment[] = []
  startFilter: boolean = false
  changeLogs: CashierConfigChangelog[] = []
  userProfile: CashierConfigProfile | null = null
  changeLogsPage: number = 0
  commentsPage: number = 0
  addMethods: AddedProcessorInterface[] = []
  editingOrder: boolean = false
  editingMethodOrder: boolean = false
  reloading: boolean = false
  searchUser: string = ''

  overRidesList: CashierConfigOverride[] =  [
    {
      color: 'domain',
      description: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OVERRIDE_LIST.DOMAIN')
    },
    {
      color: 'global',
      description: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OVERRIDE_LIST.GLOBAL')
    },
    {
      color: 'profile',
      description: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OVERRIDE_LIST.PROFILE')
    },
    {
      color: 'individual',
      description: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.OVERRIDE_LIST.USER')
    },
  ]

  mounted() {
    this.doWork()
  }

  async doWork() {
    await this.getMethods()
    await this.loadMore(true)
  }

  async reload(){
    await this.getMethods()
  }

  async getMethods(){
    this.reloading = true
    this.methods = []

    try {
      let result = await this.rootScope.provide.cashierConfigProvider.domainMethods(this.domain, this.type)

      if (result) {
        this.methods = result.plain()
      }
    } catch (err) {
      this.logService.error(err)
    }

    for ( const method of this.methods) {
        let accountingInfo = await this.getMethodAccounting(method.id)

        if (accountingInfo) {
          method.accountingUserDay = accountingInfo.accountingDay
          method.accountingUserWeek = accountingInfo.accountingWeek
          method.accountingUserMonth = accountingInfo.accountingMonth
          method.accountingUserLastMonth = accountingInfo.accountingLastMonth
        }
    }

    if(!this.viewAs) {
      this.reloading = false
    }
  }

  async getProfiles() {
    try {
      let result = await this.rootScope.provide.cashierConfigProvider.findProfiles(this.domain)

      if (result) {
        this.profiles = result.plain()

        this.profiles = this.profiles.sort((a,b) => (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0))
      }
    }  catch (err) {
      this.logService.error(err)
    }
  }

  async getUsers(){
    try {
      // let result = await this.rootScope.provide.cashierConfigProvider.searchUsers(this.domain, '')
      let result = await this.rootScope.provide.cashierConfigProvider.searchUsers(this.domain, this.searchUser)

      if (result) {
        this.users = result.plain()
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async loadMore(initialRequest: boolean) {
    if(initialRequest || (!initialRequest && this.hasMore)) {
      await this.getChangelogs()
    }
  }

  async getChangelogs(){

    // let initialChangelogs = {
    //   domainName: this.domain,
    //   entityId: true, //$stateParams.type === 'deposit'
    //   restService: this.rootScope.provide['cashierConfigProvider']['cashierDmRest'],
    //   // restService: this.rootScope.provide.cashierConfigProvider.cashierDmRest,
    //   reload: 1
    // }


    try {
      let result = await this.rootScope.provide.cashierConfigProvider.methodChangelogs(this.domain, true, this.changeLogsPage)

      if(result) {
        this.hasMore = result.hasMore

        try {
          let authorRes = await this.rootScope.provide.cashierConfigProvider.mapAuthorNameToChangeLogs(this.domain, result.list)

          if (authorRes) {

            if (this.changeLogs.length === 0) {
              this.changeLogs = [...authorRes]
            } else {
              this.changeLogs = [...this.changeLogs, ...authorRes]
            }
          }
        } catch (err) {
          this.logService.error(err)
        }
      }
    } catch (err) {
      this.logService.error(err)
    }

    this.changeLogsPage++
  }

  @Watch('domain')
  async onViewAsChanged() {
    try {
      let result = await this.rootScope.provide.cashierConfigProvider.domainMethods(this.domain, this.type)

      if (result) {
        this.methods = result.plain()
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async addMethod() {
    if(this.newMethod) {
      let addedMethod = {
        name: this.newMethodName,
        method: this.newMethod.id,
        enabled: true,
        priority: 999,
      }

      await this.rootScope.provide.cashierConfigProvider.domainMethodAdd(this.domain, addedMethod, this.type)
      await this.getMethods()
    }

    this.addMethodDialog = false
  }

  async filter() {
    this.userOrProfileIsChosen = true
    this.startFilter = true
    this.userProfile = null

    if(this.user) {
      this.selectUser()
      await this.findDomainProfile(this.user)
      await this.findDomainMethodUser(this.user)
    }

    if(this.profile)  {
      this.selectProfile()
      await this.findDomainMethodProfile(this.profile)
    }

    this.reloading = false
  }

  async findDomainMethodUser(user: CashierConfigUser) {
    let copyMethods = JSON.parse(JSON.stringify(this.methods))

    for ( const method of copyMethods) {
      if (this.userProfile === null) {
        method.domainMethodProfile = null;
      }

      try {
        let domainMethodUser = await this.rootScope.provide.cashierConfigProvider.domainMethodUser(method.id, user.guid)

        if (domainMethodUser) {

          if (Array.isArray(domainMethodUser.plain()) && !domainMethodUser.plain().length) {

            method.domainMethodUser = {
              id: null,
              domainMethod: {
                id: method.id
              },
              enabled: method.enabled,
              user: {
                guid: user.guid
              },
              priority: method.priority
            }

          } else {
            method.domainMethodUser = domainMethodUser.plain();
          }

        }

      } catch (err) {
        this.logService.error(err)
      }

        let accountingUserInfo = await this.getMethodAccountingUser(method.id, user.username)

        if (accountingUserInfo) {
          method.accountingUserDay = accountingUserInfo.accountingUserDay
          method.accountingUserWeek = accountingUserInfo.accountingUserWeek
          method.accountingUserMonth = accountingUserInfo.accountingUserMonth
          method.accountingUserLastMonth = accountingUserInfo.accountingUserLastMonth
        }
    }

    this.methods = copyMethods.sort((a,b) => (a.domainMethodUser.priority > b.domainMethodUser.priority) ? 1 : ((b.domainMethodUser.priority > a.domainMethodUser.priority) ? -1 : 0))

    await this.getComments(user)
  }

  async findDomainProfile(user: CashierConfigUser) {
    try {
      let result = await this.rootScope.provide.cashierConfigProvider.user(user.guid)

      if ( result) this.userProfile = result.profile

    } catch (err) {
      this.logService.error(err)
    }
  }

  async getMethodAccounting(id: number) {

    try {
      let list = await this.rootScope.provide.cashierConfigProvider.domainMethodAccounting(id)

      if(list) {

        let accountingInfo = {
          accountingDay: list.plain()[0]['day'],
          accountingWeek: list.plain()[0]['week'],
          accountingMonth: list.plain()[0]['month'],
          accountingLastMonth: list.plain()[0]['lastmonth']
        }

        return accountingInfo
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async getMethodAccountingUser(id: number, username: string) {

    try {
      let list = await this.rootScope.provide.cashierConfigProvider.domainMethodAccountingUser(id, username)

      if (list) {
        let accountingInfo = {
          accountingUserDay: list.plain()[0]['day'],
          accountingUserWeek: list.plain()[0]['week'],
          accountingUserMonth: list.plain()[0]['month'],
          accountingUserLastMonth: list.plain()[0]['lastmonth']
        }

        return accountingInfo
      }

    } catch (err) {
      this.logService.error(err)
    }

  }

  showMoreComments() {
    this.comments = this.tempComments.slice(0, 10+10*this.commentsPage)
    this.commentsPage++
  }

  async getComments(user: CashierConfigUser) {
    try {
      let domainMethods = await this.rootScope.provide.cashierConfigProvider.frontendMethods(this.type, user.guid)

      if (domainMethods) {
        this.tempComments = domainMethods.plain()
      }
    } catch (err) {
      this.logService.error(err)
    }

    for ( const cm of this.tempComments) {

      try {
        let processors = await this.rootScope.provide.cashierConfigProvider.frontendProcessors(cm.domainMethodId, user.guid)

        if (processors) {
          cm.processors = processors.plain()
          cm.processor = cm.processors[0]
        }
      } catch (err) {
        this.logService.error(err)
      }
    }

    this.showMoreComments()
  }

  async findDomainMethodProfile(profile: CashierConfigProfile) {

    let copyMethods = JSON.parse(JSON.stringify(this.methods))
    for ( const method of copyMethods) {

      try {
        let domainMethodProfile = await this.rootScope.provide.cashierConfigProvider.domainMethodProfile(method.id, profile.id)

        if (domainMethodProfile) {
          if (Array.isArray(domainMethodProfile.plain()) && !domainMethodProfile.plain().length) {

            method.domainMethodProfile = {
              id: null,
              domainMethod: {
                id: method.id
              },
              enabled: method.enabled,
              profile: {
                id: profile.id
              },
              priority: method.priority
            }

          } else {
            method.domainMethodProfile = domainMethodProfile.plain();
          }
        }
      } catch (err) {
        this.logService.error(err)
      }
    }

    this.methods = copyMethods.sort((a,b) => (a.domainMethodProfile.priority > b.domainMethodProfile.priority) ? 1 : ((b.domainMethodProfile.priority > a.domainMethodProfile.priority) ? -1 : 0))
  }

  async clear() {
    this.profile = null
    this.user = null
    this.viewAs = ''
    this.startFilter = false
    this.userOrProfileIsChosen = false

    await this.getMethods()
  }

  get filteredMethods(): CashierConfigMethod[] {
    if(this.filteredValue) {
      if(this.methods.length) {
        let filtered = this.methods.filter( m => m.name.toLowerCase().includes(this.filteredValue.toLowerCase()))

        return filtered
      }
    }
    return this.methods
  }

  selectProfile() {
    this.viewAs = 'profile'
  }

  selectUser() {
    this.viewAs = 'user'
  }

  async deleteMethod(method: CashierConfigMethod) {
    await this.rootScope.provide.cashierConfigProvider.domainMethodDeleteFull(method)
    this.methods = this.methods.filter( item => item.id != method.id )
  }

  async saveOrder() {
    if(this.editingMethodOrder && this.editingOrder) {

      if (this.viewAs === 'profile' && this.profile) {
        const domainMethodProfiles: any = []

        this.methods.forEach( (item, index) => {
            item.domainMethodProfile.priority = index
            domainMethodProfiles.push(item.domainMethodProfile)
        })

        try {
          await this.rootScope.provide.cashierConfigProvider.domainMethodProfileUpdateMultiple(domainMethodProfiles)
        } catch (err) {
          this.logService.error(err)
        }

      } else if (this.viewAs === 'user' && this.user) {

        const domainMethodsUser: any = []

        this.methods.forEach( (item, index) => {
          item.domainMethodUser.priority = index
          domainMethodsUser.push(item.domainMethodUser)
        })

        try {
          await this.rootScope.provide.cashierConfigProvider.domainMethodUserUpdateMultiple(domainMethodsUser)
        } catch (err) {
          this.logService.error(err)
        }

      } else {

        this.methods.forEach( (item, index) => {
          item.priority = index
        })

        try {
          await this.rootScope.provide.cashierConfigProvider.domainMethodUpdateMultiple(this.methods)
        } catch (err) {
          this.logService.error(err)
        }

        await this.getMethods()
      }
    }

    this.editingOrder = false
  }

  async setDefault(method: CashierConfigMethod) {
    this.methods.forEach(item => {
      if(method.status === true) { // set default
        if (item.id === method.id){
          item.feDefault = true
        } else {
          item.feDefault = false
        }
      } else {// unset default
        item.feDefault = false
      }
    })

    try {
      let result = await this.rootScope.provide.cashierConfigProvider.domainMethodUpdateMultiple(this.methods)

      if (result) {
        this.methods = result.plain()
      }
    } catch (err) {
      this.logService.error(err)
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

    const roles = role.split(',')
    if(roles.length > 1) {
      return roles.some( r => this.userService.hasRole(r) )
    }

    return  this.userService.hasRoleForDomain(domain, role)
  }

  async showAddMethodDialog(){
    this.addMethodDialog = true

    try {
      let result = await this.rootScope.provide.cashierConfigProvider.cashierMethods()

      if (result) {
        this.addMethods = result.plain()
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async changeStatus(id: number) {
    let copyMethods = JSON.parse(JSON.stringify(this.methods))
    let domainMethod: CashierConfigMethod | null = null
    domainMethod = this.methods.filter( item => item.id === id)[0]

    if(domainMethod) {

      switch (this.viewAs) {
        case 'user': {
          if (typeof  domainMethod.domainMethodUser.enabled === 'undefined' || domainMethod.domainMethodUser.enabled === null) {
            if (domainMethod.domainMethodProfile === null || typeof domainMethod.domainMethodProfile.enabled === 'undefined' || domainMethod.domainMethodProfile.enabled === null) {
              domainMethod.domainMethodUser.enabled = domainMethod.enabled;
            } else {
              domainMethod.domainMethodUser.enabled = domainMethod.domainMethodProfile.enabled;
            }
          }

          domainMethod.domainMethodUser.enabled = !domainMethod.domainMethodUser.enabled;

          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodUserUpdate(domainMethod.domainMethodUser)

            if (result) {
              domainMethod.domainMethodUser = result.plain()

              copyMethods = copyMethods.map(obj =>  id === obj.id ? domainMethod : obj) // maybe not needed

              this.methods = JSON.parse(JSON.stringify(copyMethods))
            }
          } catch (err) {
            this.logService.error(err)
          }

          if(this.user) {
            await this.getComments(this.user)
          }
          break;
        }
        case 'profile': {
          if (typeof domainMethod.domainMethodProfile.enabled === 'undefined') {
            domainMethod.domainMethodProfile.enabled = domainMethod.enabled;
          }
          domainMethod.domainMethodProfile.enabled = !domainMethod.domainMethodProfile.enabled;

          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodProfileUpdate(domainMethod.domainMethodProfile)

            if (result) {
              domainMethod.domainMethodProfile = result.plain()

              copyMethods = copyMethods.map(obj =>  id === obj.id ? domainMethod : obj)

              this.methods = JSON.parse(JSON.stringify(copyMethods))
            }
          } catch (err) {
            this.logService.error(err)
          }

          break;
        }
        default: {
          let copyMethods = JSON.parse(JSON.stringify(this.methods))

          copyMethods = copyMethods.map(obj => id === obj.id ? {
            ...obj,
            enabled: !obj.enabled
          } : obj)

          try {
            let result = await this.rootScope.provide.cashierConfigProvider.domainMethodUpdateMultiple(copyMethods)

            if (result) {
              copyMethods = result.plain()
            }
          } catch (err) {
            this.logService.error(err)
          }

          copyMethods = copyMethods.sort((a, b) => (a.priority > b.priority) ? 1 : ((b.priority > a.priority) ? -1 : 0))

          this.methods = JSON.parse(JSON.stringify(copyMethods))
        }

      }
    }
  }

  @Watch('tab')
  async onTabChanged() { // to clear filter if user changed tab
    this.clear()
  }

  get changedOrderMethods(): CashierConfigMethod[] {
    return this.filteredMethods
  }

  set changedOrderMethods(newValue: CashierConfigMethod[]) {
    this.methods = newValue
  }

  clearUser(){
    this.user = null
  }

  @Watch('searchUser')
  async onSearchUserChanged() {
    if(this.searchUser && this.searchUser.length > 1) {
      await this.getUsers()
    }
  }

  clearProfile(){
    this.profile = null
  }
}
</script>

<style scoped>

</style>
