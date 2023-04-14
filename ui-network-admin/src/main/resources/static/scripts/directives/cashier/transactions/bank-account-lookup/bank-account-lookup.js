'use strict';

angular.module('lithium').directive('cashierBankAccountLookup', function () {
    return {
        templateUrl: 'scripts/directives/cashier/transactions/bank-account-lookup/bank-account-lookup.html',
        scope: {
            domainData: '=',
            transactionData: '='
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['rest-cashier', '$scope', '$rootScope',
            function (cashierRest, $scope, $rootScope) {
                var controller = this;
                controller.model = {
                    domain: $scope.domainData,
                    processorCode: $scope.transactionData.current.processor.processor.code,
                    processorDescription: $scope.transactionData.current.processor.description,
                    processorUrl: $scope.transactionData.current.processor.processor.url,
                    transaction: $scope.transactionData,
                    transactionId: $scope.transactionData.id,
                    bankAccountInfo: {
                        status: undefined,
                        failedStatusReasonMessage: undefined,
                        accountName: undefined,
                        accountNumber: undefined,
                        bankCode: undefined,
                        bankName: undefined
                    }
                }

                controller.getBankAccountInfo = function () {
                    cashierRest.bankAccountLookup(
                        controller.model.domain.name, controller.model.processorCode, controller.model.processorDescription, controller.model.processorUrl, controller.model.transactionId
                    ).then(function (response) {
                        controller.model.bankAccountInfo = response.plain();
                    });
                }

                controller.refresh = function () {
                    controller.getBankAccountInfo();
                }

                controller.getBankAccountInfo();


                $rootScope.provide.bankAccountLookupGeneration.getModel = ( ) =>  {
                    return cashierRest.bankAccountLookup(
                        controller.model.domain.name, controller.model.processorCode, controller.model.processorDescription, controller.model.processorUrl, controller.model.transactionId
                    ).then((res) => res.plain())
                }

                $rootScope.provide.bankAccountLookupGeneration.refresh = ( ) =>  {
                    return Promise.resolve(controller.refresh())
                }

                window.VuePluginRegistry.loadByPage("dashboard/cashier/bank-account-lookup/table")
            }
        ]
    }
});
