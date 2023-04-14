'use strict'

angular.module('lithium').controller('AutoWithdrawalRulesetEditController', ['ruleset', 'fields', 'operators', '$translate', '$uibModal', 'bsLoadingOverlayService', 'errors', 'notify', 'AutoWithdrawalRulesetRest', '$state',
    function(ruleset, fields, operators, $translate, $uibModal, bsLoadingOverlayService, errors, notify, rest, $state) {
        var controller = this;
        controller.ruleset = ruleset.plain();
        controller.fields = fields.plain();
        controller.operators = operators.plain();

        console.debug("ruleset", controller.ruleset);
        console.debug("fields", controller.fields);
        console.debug("operators", controller.operators);

        // Removing fields that are already added
        controller.getEligibleFilterFields = function() {
            var fields = angular.copy(controller.fields);
            for (var i = 0; i < controller.ruleset.rules.length; i++) {
                for (var k = 0; k < fields.length; k++) {
                    if (controller.ruleset.rules[i].field == fields[k].id) {
                        fields.splice(k, 1);
                    }
                }
            }
            return fields;
        }

        controller.toggleEnabled = function() {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoWithdrawalRulesetToggleEnabled(ruleset.domain.name, ruleset.id)
            .then(function (response) {
                if (response._status !== 0) {
                    notify.error(response._message);
                } else {
                    var msg = (controller.ruleset.enabled === true)
                            ? 'UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EDIT.DISABLE.SUCCESS'
                            : 'UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EDIT.ENABLE.SUCCESS';
                    notify.success(msg);
                    controller.ruleset = response.plain();
                    if (controller.ruleset.enabled === true) controller.processRuleset();
                }
            }).catch(
                errors.catch("Failed to toggle enabled flag on ruleset.", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.delete = function() {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoWithdrawalRulesetDelete(ruleset.domain.name, ruleset.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EDIT.DELETE.SUCCESS');
                        $state.go('dashboard.cashier.autowithdrawals.rulesets');
                    }
                }).catch(
                errors.catch("", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.deleteRule = function(ruleId) {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoWithdrawalRulesetRuleDelete(ruleset.domain.name, ruleset.id, ruleId)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EDIT.DELETERULE.SUCCESS');
                        controller.ruleset = response.plain();
                        if (controller.ruleset.rules !== undefined &&
                            controller.ruleset.rules !== null &&
                            controller.ruleset.rules.length > 0) controller.processRuleset();
                    }
                }).catch(
                errors.catch("", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.editName = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/ruleset/ruleset-name.html',
                controller: 'AutoWithdrawalRulesetNameModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    ruleset: function() { return angular.copy(controller.ruleset) },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/ruleset/ruleset-name.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.ruleset = response.plain();
            });
        }

        controller.editDelay = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/ruleset/ruleset-delay.html',
                controller: 'AutoWithdrawalRulesetDelayModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    ruleset: function() { return angular.copy(controller.ruleset) },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/components/ruleset/ruleset-delay.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.ruleset = response.plain();
            });
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
                    domainName: function() { return controller.ruleset.domain.name; },
                    ruleset: function() { return ruleset },
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
                controller.ruleset = response.plain();
                controller.processRuleset();
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
                    domainName: function() { return controller.ruleset.domain.name; },
                    ruleset: function() { return ruleset },
                    rule: function() {
                        var ruleCopy = angular.copy(controller.ruleset.rules[$index]);
                        return ruleCopy;
                    },
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
                controller.ruleset = response.plain();
                controller.processRuleset();
            });
        }

        controller.getFieldName = function(id) {
            for (var i = 0; i < controller.fields.length; i++) {
                if (controller.fields[i].id === id) return fields[i].displayName;
            }
            return null;
        }

        controller.getOperatorName = function(rule) {
            for (var i = 0; i < controller.operators.length; i++) {
                if (rule.field === 11 && controller.operators[i].operator === "IN") {
                    operators[i].displayName = $translate.instant('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESETS.RULE.EXCHANGER.IN');
                }
                if (controller.operators[i].id === rule.operator) return operators[i].displayName;
            }
            return null;
        }

        controller.getValue = function(rule) {
            var value = '';
            for (var i = 0; i < rule.value.length; i++) {
                value = value + rule.value[i].description
                if (i < rule.value.length - 1) {
                    value = value+ ', '
                }
            }
            if (rule.value2 !== undefined && rule.value2 !== null) {
                value = value +  " to " + rule.value2;
            }
            return value;
        }

        controller.processRuleset = function() {
            if (controller.ruleset.enabled) {
                setTimeout(function () {
                    // $translate("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESET.PROCESS.ONEDIT").then(function(text) {
                    var confirmed = window.confirm("A modification has been made to this ruleset. Do you want to run this ruleset on currently pending withdrawals?");
                    if (confirmed) {
                        rest.queueAutoWithdrawalRulesetProcess(ruleset.domain.name, ruleset.id)
                            .then(function (response) {
                                if (response._status !== 0) {
                                    notify.error(response._message);
                                } else {
                                    var data = response.plain();
                                    notify.success("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESET.PROCESS.SUCCESS");
                                }
                            }).catch(
                            errors.catch("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESET.PROCESS.ERROR", false)
                        ).finally(function () {
                        });
                    }
                    // });
                }, 500);
            }
        }
    }
]);
