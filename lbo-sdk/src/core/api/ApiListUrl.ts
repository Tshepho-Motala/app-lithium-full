export default {
  cashierApi: {
    // CASHIER  Auto Withdrawal
    getAllRulesetsUrl: () => {
      return 'services/service-cashier/admin/auto-withdrawal/ruleset/table?1=1'
    },
    createRulesetUrl: (domainName: String) => {
      return `/services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/create`
    },
    getAutoWithdrawalUrl: (id: Number) => {
      return `/services/service-cashier/admin/auto-withdrawal/ruleset/${id}`
    },
    sendExportApiUrl: `services/service-cashier/backoffice/auto-withdrawal/export`,
    sendImportFileUrl: 'services/service-cashier/backoffice/auto-withdrawal/import',
    submitImportDataUrl: `services/service-cashier/backoffice/auto-withdrawal/submit`,
    getAutoWithdrawalChangeLogUrl: (id: Number) => {
      return `/services/service-cashier/admin/auto-withdrawal/ruleset/${id}/changelogs`
    },
    findUsersByUsernamesUrl: (domainName: String) => {
      return `services/service-user/${domainName}/users/find-users-by-usernames-or-guids`
    },
    queueprocessUrl: (domainName: String, id: Number) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/${id}/queueprocess`
    },

    enabledAutoWithdrawalRulesetUrl: (domainName: String, id: Number) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/${id}/toggle/enabled`
    },
    deleteAutoWithdrawalRulesetUrl: (domainName: String, id: Number) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/${id}/delete`
    },
    addRuleUrl: (domainName: String, id: Number) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/${id}/rule/add`
    },
    deleteRuleUrl: (domainName: String, id: Number, ruleId: Number) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/${id}/rule/${ruleId}/delete`
    },
    updateRuleUrl: (domainName: String, id: Number, ruleId: Number) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/${id}/rule/${ruleId}/update`
    },
    updateRulesetUrl: (domainName: String) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/${domainName}/update`
    },
    ruleOperatorDataUrl: (domainName: String, ruleId: Number) => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/rule/${domainName}/init-data/${ruleId}`
    },
    ruleFieldsDataUrl: () => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/rule/fields`
    },
    ruleOperatorsListUrl: () => {
      return `services/service-cashier/admin/auto-withdrawal/ruleset/rule/operators`
    },

    // CASHIER TRANSACTIONS
    transactionDetailUrl: (id: Number) => {
      return `services/service-cashier/cashier/transaction/${id}`
    },
    transactionDatalUrl: (id: Number) => {
      return `services/service-cashier/cashier/transaction/${id}/data`
    },
    transactionDataPerStageUrl: (id: Number, stage: Number) => {
      return `services/service-cashier/cashier/transaction/${id}/data/${stage}`
    },
    transactionLabelsUrl: (id: Number) => {
      return `services/service-cashier/cashier/transaction/${id}/labels`
    },
    lastXCashierTransactionsUrl: () => {
      return `services/service-cashier/cashier/transaction/lastXtransactions`
    },
    getTransactionRemarksUrl: (id) => {
      return `services/service-cashier/cashier/transaction/${id}/get-transaction-remarks`
    },
    sendTransactionRemarksUrl: (id) => {
      return `services/service-cashier/cashier/transaction/${id}/add-transaction-remark`
    },
    getWorkflowUrl: (id) => {
      return `services/service-cashier/cashier/transaction/${id}/workflow-pageable`
    },
    transactionAttemptUrl: (id, attempt) => {
      return `services/service-cashier/cashier/transaction/${id}/attempt/${attempt}`
    },
    retryTransactionUrl: (domainName: String, id) => {
      return `services/service-cashier/admin/${domainName}/changestatus/${id}/retry`
    },
    clearTransactionUrl: (domainName: String, id) => {
      return `services/service-cashier/admin/${domainName}/changestatus/${id}/clearProvider`
    },
    changeStatusUrl: (domainName: String, id, status: String) => {
      return `services/service-cashier/admin/${domainName}/changestatus/${id}/${status}`
    },
    withdrawApprovableUrl: (domainName: String, id) => {
      return `services/service-cashier/admin/${domainName}/changestatus/${id}/is-enough-balance`
    },
    transactionCancelUrl: (domainName: String, id) => {
      return `services/service-cashier/admin/${domainName}/changestatus/${id}/cancel`
    },
    transactionOnHoldUrl: (domainName: String, id) => {
      return `services/service-cashier/admin/${domainName}/changestatus/${id}/on-hold`
    },
    getPaymentMethodsByTranIdUrl: (id) => {
      return `services/service-cashier/cashier/transaction/${id}/payment-methods`
    },
    bankAccountLookupUrl: () => {
      return `services/service-cashier/backoffice/cashier/bank-account-lookup/find-by-transaction-id`
    },
    domainFindByNameUrl: () => {
      return `services/service-domain/domains/findByName`
    },
    domainMethodImageUrl: (domainMethodid: number) => {
      return `services/service-cashier/cashier/dm/${domainMethodid}/imageonly`
    },
    paymentMethodStatusesUrl: () => {
      return `services/service-cashier/cashier/transaction/payment-methods/status-all`
    },
    paymentMethodStatusUpdateUrl: (domainName: String, id) => {
      return `services/service-cashier/cashier/transaction/${domainName}/payment-methods/${id}/status-update`
    },
    //Balance Movement
    balanceMovementTypesUrl: () => {
      return `services/service-accounting-provider-internal/backoffice/balance-movement/types`
    },
    balanceMovementListUrl: () => {
      return `services/service-accounting-history/backoffice/balance-movement/list`
    },
    balanceMovementExlUrl: () => {
      return `services/service-accounting-history/backoffice/balance-movement/xls`
    },
    userAffiliatesUrl: () => {
      return `/services/service-user/backoffice/affiliates/list`
    },
    cashierTransactionListUrl: () => {
      return 'services/service-cashier/cashier/transaction/table'
    },
    cashierTransactionBulkListUrl: () => {
      return 'services/service-cashier/backoffice/cashier/transaction-bulk-processing/list'
    },
    cashierSendBulkListUrl: () => {
        return 'services/service-cashier/backoffice/cashier/transaction-bulk-processing/proceed-by-ids'
    },
    cancelTransactionsBulkProcessingUrl: () => {
      return 'services/service-cashier/backoffice/cashier/transaction-bulk-processing/cancel-by-ids'
    },
    cashierTransactionBalanceAdjustUrl: () => {
      return 'services/service-cashier/cashier/transaction/reverse'
    },

  },
  mailApi: {
    loadMailTemplatesUrl: (domainName: String) => {
      return `services/service-mail/${domainName}/emailtemplates/findByDomainName`
    },
    loadMailTemplateUrl: (id: number) => {
      return `services/service-mail/emailtemplate/${id}`
    },
    loadMailPlaceholderUrl: (id: number) => {
      return `services/service-mail/quick-action-email/${id}/get-placeholders`
    },
    sendUserTemplateMailUrl: (id: number) => {
      return `services/service-mail/quick-action-email/${id}/send-template`
    },
    sendUserMailUrl: (id: number, user: number) => {
      return `services/service-mail/quick-action-email/${id}/send-email/${user}`
    }
  },
  userApi: {
    userFindFromGuidUrl: (domainName: String) => {
      return `services/service-user/${domainName}/users/findFromGuid`
    },

    userGetRestrictionsUrl: (domainName: String) => {
      return `services/service-limit/backoffice/user-restrictions/${domainName}`
    },

    userListUrl: (domainName: String) => {
      return `services/service-user/${domainName}/users/list`
    },

    getUserBalanceUrl: (domainName: String) => {
      return `services/service-accounting/summary/trantype/${domainName}/findByOwnerGuid`
    },

    tranTypeSummaryByOwnerGuidUrl: (domainName: String) => {
      return `services/service-accounting/summary/trantype/${domainName}/findByOwnerGuid`
    },

    summaryAccountByOwnerGuidUrl: (domainName: String) => {
      return `services/service-accounting/summary/account/${domainName}/findByOwnerGuid`
    },

    getEscrowWalletPlayerBalanceUrl: () => {
      return `/services/service-cashier/cashier/direct-withdrawal/get-escrow-wallet-player-balance`
    },

    userBalanceUrl: (domainName: String, accountCode: String, accountType: String, currencyCode: String, ownerGuid: String) => {
      return `/services/service-accounting/balance/get/${domainName}/${accountCode}/${accountType}/${currencyCode}/${ownerGuid}`
    },

    userAffiliatesUrl: () => {
      return `/services/service-user/backoffice/affiliates/list`
    },

    playerProtectionApi:{
      depositLimitThresholdUrl:()=>{
        return '/'
      },
      lossLimitThresholdUrl:()=>{
        return"/"
      }


    }



  }
}
