'use strict';

angular.module('lithium')
    .controller('ChangeBalanceLimitModal',
        ['$uibModalInstance', 'user', 'domain', 'newLimit', 'currentLimit', '$scope', 'notify', 'errors', '$uibModal', 'bsLoadingOverlayService', 'userLimitsRest', 'domainLimitsRest', '$translate',
            function ($uibModalInstance, user, domain, newLimit, currentLimit, $scope, notify, errors, $uibModal, bsLoadingOverlayService, userLimitsRest, domainLimitsRest, $translate) {
                var controller = this;

                controller.user = user;

                controller.referenceId = 'change-user-balance-limit-overlay';

                controller.options = {};
                controller.model = {};
                controller.fields = [];

                controller.model.newLimit = newLimit;
                controller.model.currentLimit = currentLimit;

                controller.fields = [{
                    className: 'col-xs-12',
                    key: 'newLimit',
                    type: 'ui-money-mask',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: '',
                        description: '',
                        placeholder: 'undefined',
                        required: false,
                        addFormControlClass: true,
                        onChange: function () {
                            if (controller.model.newLimit < 0) {
                                controller.model.newLimit = 0;
                            }
                            if (controller.model.newLimit > 9999999999999.99) {
                                controller.model.newLimit = 9999999999999.99;
                            }
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.BALANCE_LIMITS.CHANGE.NEW_LIMIT.LABEL" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.BALANCE_LIMITS.CHANGE.NEW_LIMIT.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'col-xs-12',
                    key: 'currentLimit',
                    type: 'ui-money-mask',
                    templateOptions: {
                        label: '',
                        description: '',
                        placeholder: 'undefined',
                        required: false,
                        readOnly: true,
                        disabled: true,
                        addFormControlClass: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.BALANCE_LIMITS.CHANGE.CURRENT_LIMIT.LABEL" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.BALANCE_LIMITS.CHANGE.CURRENT_LIMIT.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'col-xs-12',
                    key: "explanation",
                    type: "examplewell",
                    templateOptions: {
                        label: "",
                        explain: ""
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.BALANCE_LIMITS.CHANGE.EXPLANATION" | translate',
                        'templateOptions.explain': function (viewValue, modelValue, $scope) {
                            if (controller.model.currentLimit === undefined || controller.model.newLimit <= currentLimit) {
                                $translate("UI_NETWORK_ADMIN.BALANCE_LIMITS.CHANGE.EXPLAIN.SMALLER").then(function success(translate) {
                                    $scope.options.templateOptions.explain = translate;
                                });
                            } else {
                                $translate("UI_NETWORK_ADMIN.BALANCE_LIMITS.CHANGE.EXPLAIN.LARGER").then(function success(translate) {
                                    $scope.options.templateOptions.explain = translate;
                                });
                            }
                        }
                    }
                }];

                controller.confirm = function () {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/balance-limit/confirm-change-limit.html',
                        controller: 'ConfirmBalanceLimitUpdateModal',
                        controllerAs: 'controller',
                        size: 'md',
                        resolve: {
                            changeBalanceLimitModalInstance: function () {
                                return $uibModalInstance;
                            },
                            userLimitsRest: function () {
                                return userLimitsRest;
                            },
                            userData: function () {
                                return user;
                            },
                            domain: function () {
                                return domain;
                            },
                            limit: function () {
                                return controller.model.newLimit;
                            },
                            currentLimit: function () {
                                return currentLimit;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/player/balance-limit/confirm-change-limit.js']
                                })
                            }
                        }
                    });
                };

                controller.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            }]);

