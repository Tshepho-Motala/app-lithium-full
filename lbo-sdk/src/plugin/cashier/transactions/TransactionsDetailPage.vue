<template>
  <div data-test-id="cnt-cashier-transaction-page">
    <v-overlay
        :opacity="0.3"
        v-if='loadingPage'
        :value="overlay"
    >
      <v-progress-circular indeterminate size="64">
        Loading...
      </v-progress-circular>
    </v-overlay>

    <div class="row" v-if="transactionData">
      <div class="col-12 mb-0 mt-2 pb-0">
        <transaction-statuses :table-status="false" :transaction-detail="transactionData"></transaction-statuses>
      </div>

      <div class="col-md-6 mt-0 ">
        <transaction-inform-block :processor="processor" :fees-data="feesData"
                                  :transaction-detail="transactionData"></transaction-inform-block>
        <player-details-block v-if="userData" :balance="balance" :transaction-detail="transactionData"
                              :restrictions="restrictions"
                              :user="userData"></player-details-block>
        <transaction-bank-account-lookup-table v-if="ngBankAccountSettingsActive && isWithdrawal"
                                               :transaction-detail="transactionData"></transaction-bank-account-lookup-table>
      </div>
      <div class="col-md-6 mt-0 ">
        <transaction-action-block
            :tran-amount.sync="tranAmount"
            :model="model"
            @refreshTransaction="refreshTransaction"
            @retryTransaction="retryTransaction"
            @clearTransaction="clearTransaction"
            @markSuccess="markSuccess"
            @markApproved="markApproved"
            @cancelTransaction="showCancelDialog = true"
            @setOnHold="showOnHoldDialog = true"
            :transaction-detail="transactionData"></transaction-action-block>
        <cancel-transaction-modal v-if="showCancelDialog" @submit="cancelTransaction"
                                  :transaction-detail="transactionData"
                                  @cancel="closeCancelTransactionModal"></cancel-transaction-modal>
        <on-hold-transaction-modal v-if="showOnHoldDialog" @submit="onHoldTransaction"
                                   :transaction-detail="transactionData"
                                   @cancel="closeOnHoldTransactionModal"></on-hold-transaction-modal>
        <transaction-remarks :transaction-id="transactionID"></transaction-remarks>
        <payment-methods-list @openPaymentEditModal="openPaymentEditModal"
                              :transaction-detail="transactionData"></payment-methods-list>
        <last-x-cashier-transactions :transaction-id="transactionID"></last-x-cashier-transactions>
        <div class="mt-3 d-flex justify-end">
          <img data-test-id="cnt-cashier-transactions-method-img" v-if="methodImg"
               style="width: 250px; height: auto; object-fit: contain;"
               :src="`data:image/png;base64, ${methodImg.base64}`"
               alt="">
        </div>

      </div>
      <div class="col-12">
        <transaction-workflow @workflowPagination="workflowPagination" v-if="workflowList.length"
                              :params-workflow-api="paramsWorkflowApi"
                              :workflow-list="workflowList"></transaction-workflow>
      </div>
    </div>

    <payment-edit-modal v-if="showPaymentEditModal" :payment-edit-method="paymentEdit" @reload="refreshTransaction"
                        @cancel="closePaymentEditModal" :transaction-detail="transactionData"></payment-edit-modal>

    <v-snackbar
        v-model="snackbar.show"
        :timeout="2000"
        :color="snackbar.color"
    >
      {{ snackbar.text }}

    </v-snackbar>

  </div>
</template>

<script lang="ts">
import {Component, Mixins, Inject} from 'vue-property-decorator'
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import UserApiInterface from "@/core/interface/axios-api/UserApiInterface";
import TransactionStatuses from "@/plugin/cashier/transactions/components/TransactionStatuses.vue";
import TransactionInformBlock from "@/plugin/cashier/transactions/components/TransactionInformBlock.vue";
import PlayerDetailsBlock from "@/plugin/cashier/transactions/components/PlayerDetailsBlock.vue";
import UserApi from "@/core/axios/axios-api/UserApi";
import LastXCashierTransactions from "@/plugin/cashier/transactions/components/LastXCashierTransactions.vue";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import TransactionRemarks from "@/plugin/cashier/transactions/components/TransactionRemarks.vue";
import TransactionWorkflow from "@/plugin/cashier/transactions/components/TransactionWorkflow.vue";
import TransactionActionBlock from "@/plugin/cashier/transactions/components/TransactionActionBlock.vue";
import CancelTransactionModal from "@/plugin/cashier/transactions/components/CancelTransactionModal.vue";
import OnHoldTransactionModal from "@/plugin/cashier/transactions/components/OnHoldTransactionModal.vue";
import PaymentMethodsList from "@/plugin/cashier/transactions/components/PaymentMethodsList.vue";
import TransactionBankAccountLookupTable
  from "@/plugin/cashier/transactions/components/TransactionBankAccountLookupTable.vue";
import {
  BalanceTransactionInterface,
  CashierFeesInterface,
  CashierTransactionsDataInterface, changeStatusParamsInterface, domainFindByNameParamsInterface,
  ModelTransactionInterface,
  ParamsWorkflowApiInterface, transactionCancelParamsInterface,
  transactionDataListItemInterface, transactionOnHoldParamsInterface,
  UserInterface, withdrawApprovableParamsInterface
} from "@/core/interface/cashier/cashierTransactions";
import PaymentEditModal from "@/plugin/cashier/transactions/components/PaymentEditModal.vue";


@Component({
  components: {
    TransactionStatuses,
    TransactionInformBlock,
    PlayerDetailsBlock,
    LastXCashierTransactions,
    TransactionRemarks,
    TransactionWorkflow,
    TransactionActionBlock,
    CancelTransactionModal,
    OnHoldTransactionModal,
    PaymentMethodsList,
    TransactionBankAccountLookupTable,
    PaymentEditModal
  }
})
export default class TransactionsDetailPage extends Mixins(cashierMixins) {

  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface

  transactionID: Number | undefined = undefined
  transactionData: CashierTransactionsDataInterface | null = null
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  UserApiService: UserApiInterface = new UserApi(this.userService)
  feesData: CashierFeesInterface = {
    flat: 0,
    minimum: 0,
    percentage: 0,
    percentageFee: 0,
    playerAmount: 0,
    playerAmountCents: 0,
    depositAmount: 0,
    depositAmountCents: 0,
    feeAmount: 0,
    minimumUsed: false,
  }
  userData: UserInterface | null = null
  domainName: string = ''
  processor: string = ''
  restrictions: any[] = []
  workflowList: any[] = []
  transactionDataList: transactionDataListItemInterface[] = []
  tranAmount: number = 0
  model: ModelTransactionInterface = {
    tranAmount: 0,
    isWithdrawalAndApprovedInWorkflow: false
  }
  paramsWorkflowApi: ParamsWorkflowApiInterface = {
    page: 0,
    pageSize: 5,
    totalPages: 1,
  }
  balance: BalanceTransactionInterface = {
    ltDeposits: 0,
    ltWithdrawals: 0,
    pendingWithdrawals: 0,
    escrowBalance: 0,
    currentBalance: 0,
  }
  loadingPage: boolean = true
  componentPlayerKey: number = 0
  isWithdrawal: boolean = false
  showCancelDialog: boolean = false
  showOnHoldDialog: boolean = false
  ngBankAccountSettingsActive: boolean = false
  methodImg: undefined
  showPaymentEditModal: boolean = false
  paymentEdit: any = undefined

  async mounted() {
    this.transactionID = this.rootScope.provide.cashierProvider.transactionID
    await this.refreshTransaction()
  }

  async refreshTransaction() {
    await this.loadTransactionDataInformation()
    await this.loadFees()
    await this.loadFeesLabels()
    await this.loadUser()
    await this.loadRestrictions()
    await this.getUserBalance()
    await this.loadLtDeposits()
    await this.findLtWithdrawals()
    await this.findPendingWithdrawals()
    await this.getEscrowWalletPlayerBalance()
    await this.domainFindByName()
    await this.domainMethodImage()
    await this.loadWorkflow()
  }

  async workflowPagination(page) {
    this.paramsWorkflowApi.page = page - 1
    await this.loadWorkflow()
  }

  async loadWorkflow() {

    const params: ParamsWorkflowApiInterface = {
      page: this.paramsWorkflowApi.page,
      pageSize: this.paramsWorkflowApi.pageSize
    }
    if (this.transactionID !== undefined) {
      try {
        const result = await this.CashierTransactionsApiService.getWorkflow(this.transactionID, params)
        if (result?.data?.successful) {
          this.$set(this.paramsWorkflowApi, 'totalPages', result.data.data.totalPages)
          this.paramsWorkflowApi.totalPages = result.data.data.totalPages
          const workflow: any[] = result.data.data.content
          if (workflow.length && workflow[0].processor) this.processor = workflow[0].processor.description

          for (const wf of workflow) {
            if ((this.transactionData?.transactionType === 'WITHDRAWAL') && (wf.status.code === 'APPROVED' || wf.status.code === 'AUTO_APPROVED')) {
              this.$set(this.model, 'isWithdrawalAndApprovedInWorkflow', true)
            }
            try {
              const attempt = await this.CashierTransactionsApiService.transactionAttempt(this.transactionID, wf.id)
              if (attempt?.data?.successful) {
                wf.attempt = {}
                if (attempt.data.data) wf.attempt = attempt.data.data
                if (!this.userService.hasAdminRole()) {
                  wf.attempt.processorRawRequest = ''
                  wf.attempt.processorRawResponse = ''
                }
                if (wf.attempt.processorRawRequest === '') wf.attempt.processorRawRequest = 'N/A'
                if (wf.attempt.processorRawResponse === '') wf.attempt.processorRawResponse = 'N/A'
                if (this.transactionDataList.length) {
                  const iodataPerStage = this.transactionDataList.filter((el: any) => el.stage === wf.stage)
                  wf.attempt.iodata = [...iodataPerStage]
                  wf.attempt.iodata.forEach((io: any) => {
                    if ((io.field === 'cvv') || (io.field === 'account_info')) {
                      const index = wf.attempt.iodata.indexOf(io);
                      wf.attempt.iodata.splice(index, 1)
                    } else if (io.field == 'ccnumber') {
                      const len = io.value.length
                      io.value = io.value.substring(0, 6) + '******' + io.value.substring(len - 4, len)
                    }
                  })
                }
              }
            } catch (err) {
              this.logService.error(err)
            }
          }
          this.workflowList = workflow
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async loadTransactionDataInformation() {
    this.transactionData = null
    if (this.transactionID !== undefined) {
      this.loadingPage = true
      try {
        const result = await this.CashierTransactionsApiService.transactionDetail(this.transactionID)
        if (result?.data?.successful) {
          this.transactionData = result.data.data
          if (this.transactionData !== null) {
            this.domainName = result.data.data.domainMethod.domain.name
            if (this.transactionData?.transactionType !== "DEPOSIT") this.isWithdrawal = true;
            if (this.transactionData?.domainMethod.method.code === 'cc') {
              const len = this.transactionData.accountInfo.length;
              if (len > 0) this.transactionData.accountInfo = this.transactionData.accountInfo.substring(0, 6) + '******' + this.transactionData.accountInfo.substring(len - 4, len);
            }
            if (this.transactionData?.current?.status?.code === "APPROVED") {
              let checkTime: any = new Date();
              checkTime = checkTime.setMinutes(checkTime.getMinutes() - 10);
              let checkTimeStamp: Date | Number = new Date(checkTime).getTime();
              this.transactionData.showRertyButtonInApprovedState = this.transactionData.current.timestamp < checkTimeStamp;
            } else {
              this.transactionData.showRertyButtonInApprovedState = undefined
            }
            this.transactionData.approveDisabled = true;
            this.transactionData.approveMessage = ''
            await this.withdrawApprovable()
          }
        }
      } catch (err) {
        this.logService.error(err)
      } finally {
        this.loadingPage = false
      }
    }
  }

  async loadFees() {
    if (this.transactionID !== undefined) {
      this.loadingPage = true
      try {
        const result = await this.CashierTransactionsApiService.transactionData(this.transactionID)
        if (result?.data?.successful) {
          const transactionDataList = result.data.data
          this.transactionDataList = [...result.data.data]
          transactionDataList.forEach((el: any) => {
            if (el.field === 'amount') {
              if (el.value !== null && el.value !== undefined && (Number(el.value) == el.value) && (el.value !== "")) {
                this.feesData.depositAmount = Number(el.value);
                const amountCent = Number(el.value) * 100
                this.feesData.depositAmountCents = Number(amountCent.toFixed(0))
                this.model.tranAmount = Number(el.value)
                this.tranAmount = Number(el.value)
                this.feesData.playerAmount = this.feesData.depositAmount;
                this.feesData.playerAmountCents = this.feesData.depositAmountCents;
              }
            }
          })
        }
      } catch (err) {
        this.logService.error(err)
      } finally {
        this.loadingPage = false
      }
    }
  }

  async loadFeesLabels() {
    if (this.transactionID !== undefined) {
      try {
        const result = await this.CashierTransactionsApiService.transactionLabels(this.transactionID)
        if (result?.data?.successful) {
          const transactionLabelList = result.data.data
          transactionLabelList.forEach((label: any) => {
            if (label.value !== null && label.value !== undefined && (Number(label.value) == label.value) && (label.value !== "")) {
              if (label.label.name === 'fees_flat') this.feesData.flat = Number(label.value)
              if (label.label.name === 'fees_minimum') this.feesData.minimum = Number(label.value)
              if (label.label.name === 'fees_percentage') this.feesData.percentage = Number(label.value)
              if (label.label.name === 'fees_percentage_fee') this.feesData.percentageFee = Number(label.value)
              if (label.label.name === 'fees_player_amount') {
                const amountCent = Number(label.value) * 100
                this.feesData.playerAmount = Number(label.value)
                this.feesData.playerAmountCents = Number(amountCent.toFixed(0))
              }
            }
            const feeAmount = this.feesData.flat + this.feesData.percentageFee
            if (feeAmount < this.feesData.minimum) {
              this.feesData.minimumUsed = true;
              this.feesData.feeAmount = this.feesData.minimum
            } else {
              this.feesData.minimumUsed = false;
              this.feesData.feeAmount = feeAmount
            }
          })
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async loadUser() {
    if (this.domainName && this.transactionData?.user) {
      try {
        const result = await this.UserApiService.userFindFromGuid(this.domainName, {guid: this.transactionData.user.guid})
        if (result?.data?.successful) this.userData = result.data.data
      } catch (err) {
        this.logService.error(err)
      }
    }

  }

  async loadRestrictions() {
    if (this.domainName && this.transactionData?.user) {
      try {
        const result = await this.UserApiService.userGetRestrictions(this.domainName, {userGuid: this.transactionData.user.guid})
        if (result?.data?.successful) this.restrictions = result.data.data
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async retryTransaction() {
    this.loadingPage = true
    if (this.domainName && this.transactionID !== undefined) {
      try {
        const result = await this.CashierTransactionsApiService.retryTransaction(this.domainName, this.transactionID)
        if (result?.data) {
          if (result.data.error) {
            this.snackbar = {
              show: true,
              text: this.translateAll(result.data.errorMessage),
              color: 'error'
            }
          }
          await this.refreshTransaction()
        } else {
          this.loadingPage = false
        }
      } catch (err) {
        this.logService.error(err)
        this.loadingPage = false
      }
    }

  }

  async clearTransaction() {
    this.loadingPage = true
    if (this.domainName && this.transactionID !== undefined) {
      try {
        const result = await this.CashierTransactionsApiService.clearTransaction(this.domainName, this.transactionID)
        if (result?.data) {
          if (result.data.error) {
            this.snackbar = {
              show: true,
              text: this.translateAll(result.data.errorMessage),
              color: 'error'
            }
          }
          await this.refreshTransaction()
        } else {
          this.loadingPage = false
        }
      } catch (err) {
        this.logService.error(err)
        this.loadingPage = false
      }
    }
  }

  async withdrawApprovable() {
    if (this.domainName && this.transactionID !== undefined && this.transactionData?.user) {
      const params: withdrawApprovableParamsInterface = {
        currencyCode: this.transactionData.currencyCode,
        guid: this.transactionData.user.guid,
        isWithdrawalFundsReserved: this.isWithdrawalFundsReserved()
      }
      try {
        const result = await this.CashierTransactionsApiService.withdrawApprovable(this.domainName, this.transactionID, params)
        if (result?.data) {
          this.transactionData.approveDisabled = !result.data.data.enoughBalance;
          if (this.transactionData.approveDisabled) {
            this.transactionData.approveMessage = result.data.data.message;
          } else {
            this.transactionData.approveMessage = undefined;
          }
        }
      } catch (err) {
        this.logService.error(err)
        this.loadingPage = false
      }
    }
  }

  async changeStatus(status: string, amount: number) {
    this.loadingPage = true
    const params: changeStatusParamsInterface = {
      amount: amount
    }
    if (this.domainName && this.transactionID !== undefined) {
      try {
        const result = await this.CashierTransactionsApiService.changeStatus(this.domainName, this.transactionID, status, params)
        if (result?.data) {
          if (result.data.error) {
            this.snackbar = {
              show: true,
              text: this.translateAll(result.data.errorMessage),
              color: 'error'
            }
          }
          await this.refreshTransaction()
        } else {
          this.loadingPage = false
        }
      } catch (err) {
        this.logService.error(err)
        this.loadingPage = false
      }
    }
  }

  async markApproved() {
    await this.changeStatus('approve', this.model.tranAmount)
  }

  async markSuccess() {
    if (this.isWithdrawalFundsReserved()) {
      if (this.domainName && this.transactionData?.user) {
        try {
          let playerBalancePendingWithdrawal: any = 0
          const result = await this.UserApiService.userBalance(this.domainName, 'PLAYER_BALANCE_PENDING_WITHDRAWAL', 'PLAYER_BALANCE', this.transactionData.currencyCode, this.transactionData.user.guid)
          if (result?.data?.successful) {
            playerBalancePendingWithdrawal = result.data.data
          }
          if ((playerBalancePendingWithdrawal - this.feesData.playerAmountCents) >= 0) {
            await this.changeStatus('success', this.tranAmount)
          } else {
            this.snackbar = {
              show: true,
              text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.FAILED.EXPLANATION'),
              color: 'error'
            }
            this.transactionData.approveDisabled = true;
            this.transactionData.approveMessage = "UI_NETWORK_ADMIN.CASHIER.TRANSACTION.FAILED.EXPLANATION";
          }
        } catch (err) {
          this.logService.error(err)
        }
      }
    } else {
      await this.changeStatus('success', this.tranAmount)
    }
  }

  isWithdrawalFundsReserved() {
    return (this.transactionData?.accRefToWithdrawalPending !== undefined && this.transactionData.accRefToWithdrawalPending !== null) &&
        (this.transactionData.accRefFromWithdrawalPending === undefined || this.transactionData.accRefFromWithdrawalPending === null);

  }

  async loadLtDeposits() {
    if (this.domainName && this.transactionData?.user) {
      try {
        const ltDepositsParams = {
          accountCode: 'PLAYER_BALANCE',
          currency: this.transactionData.currencyCode,
          granularity: 5,
          ownerGuid: this.transactionData.user.guid,
          transactionType: 'CASHIER_DEPOSIT'
        }
        const result = await this.UserApiService.tranTypeSummaryByOwnerGuid(this.domainName, ltDepositsParams)
        if (result?.data?.successful) {
          let total = 0;
          if (result?.data.data[0]) {
            total += (result.data.data[0].debitCents - result.data.data[0].creditCents) * -1
          }

          this.$set(this.balance, 'ltDeposits', total)
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async findLtWithdrawals() {
    if (this.domainName && this.transactionData?.user) {
      try {
        const ltDepositsParams = {
          accountCode: 'PLAYER_BALANCE_PENDING_WITHDRAWAL',
          currency: this.transactionData.currencyCode,
          granularity: 5,
          ownerGuid: this.transactionData.user.guid,
          transactionType: 'CASHIER_PAYOUT'
        }
        const result = await this.UserApiService.tranTypeSummaryByOwnerGuid(this.domainName, ltDepositsParams)
        if (result?.data?.successful) {
          let total = 0;
          if (result?.data.data[0]) {
            total += (result.data.data[0].debitCents - result.data.data[0].creditCents)
          }
          this.$set(this.balance, 'ltWithdrawals', total)
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async findPendingWithdrawals() {
    if (this.domainName && this.transactionData?.user) {
      try {
        const ltDepositsParams = {
          accountCode: 'PLAYER_BALANCE_PENDING_WITHDRAWAL',
          currency: this.transactionData.currencyCode,
          granularity: 5,
          ownerGuid: this.transactionData.user.guid
        }
        const result = await this.UserApiService.summaryAccountByOwnerGuid(this.domainName, ltDepositsParams)
        if (result?.data?.successful) {
          let total = 0;
          if (result?.data.data[0]) {
            total += (result.data.data[0].debitCents - result.data.data[0].creditCents) * -1
          }
          this.$set(this.balance, 'pendingWithdrawals', total)
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }


  async getEscrowWalletPlayerBalance() {
    if (this.domainName && this.transactionData?.user) {
      try {
        const params = {
          domainName: this.domainName,
          userGuid: this.transactionData.user.guid
        }
        const result = await this.UserApiService.getEscrowWalletPlayerBalance(params)
        if (result?.data?.successful) {
          this.$set(this.balance, 'escrowBalance', result.data.data)
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async getUserBalance() {
    if (this.domainName && this.transactionData?.user) {
      try {
        const result = await this.UserApiService.userBalance(this.domainName, 'PLAYER_BALANCE', 'PLAYER_BALANCE', this.transactionData.currencyCode, this.transactionData.user.guid)
        if (result?.data?.successful) {
          this.$set(this.balance, 'currentBalance', result.data.data)
        } else {
          this.$set(this.balance, 'currentBalance', 0)
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  closeCancelTransactionModal() {
    this.showCancelDialog = false
  }

  closeOnHoldTransactionModal() {
    this.showOnHoldDialog = false
  }

  openPaymentEditModal(method) {
    this.paymentEdit = method
    this.showPaymentEditModal = true
  }

  closePaymentEditModal() {
    this.showPaymentEditModal = false
  }

  async onHoldTransaction(comment: string) {
    const params: transactionOnHoldParamsInterface = {
      reason: comment
    }
    if (this.domainName && this.transactionID !== undefined) {
      try {
        const result = await this.CashierTransactionsApiService.transactionOnHold(this.domainName, this.transactionID, params)
        if (result?.data.successful) {
          if (result.data.data.error) {
            this.snackbar = {
              show: true,
              text: result.data.data.errorMessage,
              color: 'error'
            }
          } else {
            await this.refreshTransaction()
            this.snackbar = {
              show: true,
              text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.SUCCESS'),
              color: 'success'
            }
          }
          this.showOnHoldDialog = false

        } else {
          this.showOnHoldDialog = false
          this.snackbar = {
            show: true,
            text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.ERROR'),
            color: 'error'
          }
        }
      } catch (err) {
        this.showOnHoldDialog = false
        this.logService.error(err)
        this.snackbar = {
          show: true,
          text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.ERROR'),
          color: 'error'
        }
      }
    }
  }

  async cancelTransaction(comment: string) {
    this.loadingPage = true
    const params: transactionCancelParamsInterface = {
      comment: comment
    }
    if (this.domainName && this.transactionID !== undefined) {
      try {
        const result = await this.CashierTransactionsApiService.transactionCancel(this.domainName, this.transactionID, params)
        if (result?.data) {
          if (result.data.error) {
            this.snackbar = {
              show: true,
              text: result.data.errorMessage,
              color: 'error'
            }
          }
          this.showCancelDialog = false
          await this.refreshTransaction()
        } else {
          this.showCancelDialog = false
          this.loadingPage = false
        }
      } catch (err) {
        this.showCancelDialog = false
        this.logService.error(err)
        this.loadingPage = false
      }
    }
  }

  async domainFindByName() {
    if (this.domainName) {
      const params: domainFindByNameParamsInterface = {
        name: this.domainName
      }
      try {
        const request: any = await this.CashierTransactionsApiService.domainFindByName(params)
        const result = request.data.data
        if (result?.data?.successful) {
          for (var i = 0; i < result.current.labelValueList.length; i++) {
            if (result.labelValueList[i].label.name === "bank_account_lookup" && result.current.labelValueList[i].labelValue.value === "true") {
              this.ngBankAccountSettingsActive = true;
              break;
            }
          }
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async domainMethodImage() {
    if (this.transactionData?.domainMethod) {
      try {
        const result: any = await this.CashierTransactionsApiService.domainMethodImage(this.transactionData.domainMethod.id)
        if (result?.data?.successful) {
          this.methodImg = result.data.data
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }
}
</script>
