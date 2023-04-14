'use strict'

angular.module('lithium').controller('AutoWithdrawalRulesetAddController', ['$translate', 'fields', 'operators', '$state', '$userService', '$uibModal', 'bsLoadingOverlayService', 'errors', 'notify', 'AutoWithdrawalRulesetRest',"$scope",
    function($translate, fields, operators, $state, $userService, $uibModal, bsLoadingOverlayService, errors, notify, rest, $scope) {
        var controller = this;

        controller.fields = fields.plain();
        controller.operators = operators.plain();

        console.debug("fields", controller.fields);
        console.debug("operators", controller.operators);

        controller.model = {rules: []};
        controller.setupFields = function () {
            controller.inputFields = [
                {
                    className: 'col-xs-12 col-md-6',
                    key: "domain.name",
                    type: "ui-select-single",
                    templateOptions: {
                        label: "Domain",
                        description: "Choose the domain that you are creating the ruleset for",
                        required: true,
                        optionsAttr: 'bs-options',
                        valueProp: 'name',
                        labelProp: 'name',
                        optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                        placeholder: '',
                        options: []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DOMAIN.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DOMAIN.DESCRIPTION" | translate'
                    },
                    controller: ['$scope', function ($scope) {
                        $scope.to.options = $userService.domainsWithRole("AUTOWITHDRAWALS_RULESETS_ADD");
                    }]
                }, {
                    className: 'col-xs-12 col-md-6',
                    key: "name",
                    type: "input",
                    templateOptions: {
                        label: "Name",
                        description: "Add a unique name for the ruleset",
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.NAME.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.NAME.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'top-space-15 col-xs-12 col-md-6',
                    type: 'checkbox2',
                    key: 'enabled',
                    templateOptions: {
                        label: 'Enabled',
                        fontWeight: 'bold',
                        description: 'Should this ruleset be enabled?',
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.ENABLED.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.ENABLED.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'top-space-15 col-xs-12 col-md-6',
                    type: 'checkbox2',
                    key: 'delayedStart',
                    templateOptions: {
                        label: 'delayedStart',
                        fontWeight: 'bold',
                        description: 'Should this ruleset be delay?',
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DELAYED_START.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.ENABLED.DESCRIPTION" | translate'
                    }
                }
            ]
            if (controller.model.delayedStart === true) {
                controller.inputFields.push({
                    className: 'top-space-15 col-xs-12 col-md-6',
                    key: "delay",
                    type: 'ui-number-mask',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Delay',
                        description: "The withdraw processing delay in ms after approve.",
                        decimals: 0,
                        hidesep: true,
                        neg: false,
                        min: '0',
                        max: '',
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DELAY.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DELAY.DESCRIPTION" | translate'
                    }
                });
            }
        }

        controller.setupFields();

        $scope.$watch('[controller.model.delayedStart]', function (newValue, oldValue) {
            if (newValue != oldValue) {
                controller.setupFields();
            }
        });

        // Removing fields that are already added
        controller.getEligibleFilterFields = function() {
            var fields = angular.copy(controller.fields);
            for (var i = 0; i < controller.model.rules.length; i++) {
                for (var k = 0; k < fields.length; k++) {
                    if (controller.model.rules[i].field == fields[k].id) {
                        fields.splice(k, 1);
                    }
                }
            }
            return fields;
        }

        controller.getFieldName = function(id) {
            for (var i = 0; i < controller.fields.length; i++) {
                if (controller.fields[i].id === id) return fields[i].displayName;
            }
            return field;
        }

        controller.getOperatorName = function(rule) {
            for (var i = 0; i < controller.operators.length; i++) {
                if (rule.field === 11 && controller.operators[i].operator === "IN") {
                    operators[i].displayName = $translate.instant('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESETS.RULE.EXCHANGER.IN');
                }
                if (controller.operators[i].id === rule.operator) return operators[i].displayName;
            }
            return data;
        }

        controller.addRule = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/rule/rule.html',
                controller: 'AutoWithdrawalRulesetRuleModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    domainName: function() { return controller.model.domain.name; },
                    ruleset: function() { return null },
                    rule: function() { return null; },
                    filterFields: function() { return controller.getEligibleFilterFields(); },
                    filterOperators: function() { return controller.operators; },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/rule/rule.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.model.rules.push(response);
            });
        }

        controller.modifyRule = function($index) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/rule/rule.html',
                controller: 'AutoWithdrawalRulesetRuleModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    domainName: function() { return controller.model.domain.name; },
                    ruleset: function() { return null },
                    rule: function() { return angular.copy(controller.model.rules[$index]); },
                    filterFields: function() { return controller.fields; },
                    filterOperators: function() { return controller.operators; },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/rule/rule.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.model.rules[$index] = response;
            });
        }

        controller.removeRule = function($index) {
            controller.model.rules.splice($index, 1);
        }

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoWithdrawalRulesetCreate(controller.model.domain.name, controller.model)
            .then(function (response) {
                if (response._status !== 0) {
                    notify.error(response._message);
                } else {
                    var data = response.plain();
                    notify.success("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.ADD.SUCCESS");
                    $state.go("^.view", { id: data.id });
                }
            }).catch(
                errors.catch("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.ADD.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }
    }
]);
