'use strict'

angular.module('lithium').controller('CashierTransListBetaController', ['$state', '$scope', '$rootScope',  'DocumentGenerationRest',
    function($state, $scope, $rootScope,  documentRest) {

        $rootScope.provide.cashierProvider['openTransactionAdd'] = (domain) => {
            $state.go('dashboard.cashier.transactions.add', {
                domainName: domain
            });
        }
        $scope.reference = () => {
            const reference = localStorage.getItem(`export_reference_transactions_list`)

            if (angular.isUndefinedOrNull(reference)) {
                return null;
            }

            return reference.replace('export_reference_transactions_list', '')

        }
        $rootScope.provide['csvGeneratorProvider'] = {}

        $rootScope.provide.csvGeneratorProvider.generate = async (config) => {
            const response = await documentRest.generateDocument({
                ...config,
            });
            localStorage.setItem(`export_reference_transactions_list`, response.reference);
            return response
        }

        $rootScope.provide.csvGeneratorProvider.download = (reference) => {
            const a = document.createElement("a")
            const url = `services/service-document-generation/document/${reference}/download`;
            a.href = url;
            a.setAttribute('download', reference)
            document.body.appendChild(a);
            a.click();

            setTimeout(() => document.body.removeChild(a), 1500)
        }

        $rootScope.provide.csvGeneratorProvider.progress = async (config) => {
            return documentRest.documentStatus($scope.reference());
        }

        $rootScope.provide.csvGeneratorProvider.cancelGeneration = async (reference) => {
            return documentRest.documentCancel(reference);
        }
        window.VuePluginRegistry.loadByPage("TransactionsListPage")

    }
]);
