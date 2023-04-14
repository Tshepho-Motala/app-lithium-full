'use strict';

angular.module('lithium').directive('cashiertranoverview', function() {
    return {
        templateUrl:'scripts/directives/cashier/transactions/overview/overview.html',
        scope: {
            data: '='
        },
        restrict: 'E',
        replace: true,
        controller: ['$dt', '$uibModal', '$translate', 'rest-cashier', '$state', '$scope', '$stateParams', 'notify',
            function($dt, $uibModal, $translate, cashierRest, $state, $scope, $stateParams, notify) {
                console.debug("data", $scope.data);
                $scope.domain = $stateParams.domainName;

                $scope.statusMap = [
                    {name:"ACTIVE", id:1},
                    {name:"BLOCKED", id:2},
                    {name:"DISABLED", id:3},
                    {name:"DEPOSIT_ONLY", id:4},
                    {name:"WITHDRAWAL_ONLY", id:5},
                    {name:"EXPIRED", id:6} ];

                $scope.loadPaymentMethods = function () {
                    function extractPaymentMethods(paymentMethods) {
                        $scope.paymentMethods = paymentMethods;
                        $scope.showVerification = true;
                        $scope.sum = function (items, prop) {
                            return items.reduce(function (a, b) {
                                return a + b[prop];
                            }, 0);
                        };
                        $scope.depositTotal = $scope.sum(paymentMethods, 'depositSum');
                        $scope.withdrawalTotal = $scope.sum(paymentMethods, 'withdrawSum');
                        $scope.netTotal = $scope.sum(paymentMethods, 'netDeposit');
                        $scope.currencyCode = paymentMethods && paymentMethods.length > 0 ? paymentMethods[0].currencyCode : null;
                    }

                    if ($scope.data && $scope.data.user) {
                        cashierRest.getPaymentMethodsByUser($scope.data.user.guid, $scope.data.user.domain.name).then(function (paymentMethods) {
                            extractPaymentMethods(paymentMethods);
                        });
                    } else if ($scope.data && $scope.data.tranId) {
                        cashierRest.getPaymentMethodsByTranId($scope.data.tranId).then(function (paymentMethods) {
                            extractPaymentMethods(paymentMethods);
                        });
                    }

                }

                $scope.editStatus = function(pm) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/cashier/transactions/overview/editStatus.html',
                        controller: 'EditPMStatusModal',
                        controllerAs: 'controller',
                        size: 'md',
                        resolve: {
                            pm: pm,
                            showVerification: $scope.showVerification,
                            statuses: function() {
                                return cashierRest.paymentMethodStatusAll().then(function(statuses) {
                                    return statuses.plain();
                                });
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/cashier/transactions/overview/editStatus.js']
                                })
                            }
                        }

                    });

                    modalInstance.result.then(function (pa) {
                        pm.status = $scope.statusMap.find(m => m.name === pa.status);
                        pm.verified = pa.verified;
                        pm.verificationError = pa.failedVerification;
                        if (pa.contraAccount === true) {
                            angular.forEach($scope.paymentMethods, function (item) {
                                item.contraAccount = false;
                            });
                        }
                        pm.contraAccount = pa.contraAccount;
                        notify.success("Payment method updated successfully");
                    });
                };

                $scope.loadPaymentMethods();
            }
        ]
    }
});
