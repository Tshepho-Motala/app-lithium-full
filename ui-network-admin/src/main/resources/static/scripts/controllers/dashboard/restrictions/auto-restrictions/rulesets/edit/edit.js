'use strict'

angular.module('lithium').controller('AutoRestrictionRulesetEditController', ['ruleset', 'fields', 'operators', 'outcomes', 'events','$translate', '$uibModal', 'bsLoadingOverlayService', 'errors', 'notify', 'AutoRestrictionRulesetRest', 'RestrictionsRest', '$state',
    function(ruleset, fields, operators, outcomes, events, $translate, $uibModal, bsLoadingOverlayService, errors, notify, rest, restrictionsRest, $state) {
        let controller = this;
        controller.ruleset = ruleset.plain();
        controller.fields = fields.plain();
        controller.operators = operators.plain();
        controller.outcomes = outcomes.plain();
        controller.events = events.plain();

        console.debug("ruleset", controller.ruleset);
        console.debug("fields", controller.fields);
        console.debug("operators", controller.operators);
        console.debug("outcomes", controller.outcomes);

        // Removing fields that are already added
        controller.getEligibleFilterFields = function() {
            let fields = angular.copy(controller.fields);
            for (let i = 0; i < controller.ruleset.rules.length; i++) {
                for (let k = 0; k < fields.length; k++) {
                    if (controller.ruleset.rules[i].field === fields[k].id) {
                        fields.splice(k, 1);
                    }
                }
            }
            return fields;
        }

        controller.toggleEnabled = function() {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoRestrictionRulesetToggleEnabled(ruleset.domain.name, ruleset.id)
            .then(function (response) {
                if (response._status !== 0) {
                    notify.error(response._message);
                } else {
                    var msg = (controller.ruleset.enabled === true)
                            ? 'UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.EDIT.DISABLE.SUCCESS'
                            : 'UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.EDIT.ENABLE.SUCCESS';
                    notify.success(msg);
                    controller.ruleset = response.plain();
                }
            }).catch(
                errors.catch("Failed to toggle enabled flag on ruleset.", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.delete = function() {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoRestrictionRulesetDelete(ruleset.domain.name, ruleset.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.EDIT.DELETE.SUCCESS');
                        $state.go('dashboard.restrictions.autorestrictions.rulesets');
                    }
                }).catch(
                errors.catch("", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.deleteRule = function(ruleId) {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoRestrictionRulesetRuleDelete(ruleset.domain.name, ruleset.id, ruleId)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.EDIT.DELETERULE.SUCCESS');
                        controller.ruleset = response.plain();
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
                templateUrl: 'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/ruleset/ruleset-name.html',
                controller: 'AutoRestrictionRulesetNameModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    ruleset: function() { return angular.copy(controller.ruleset) },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/ruleset/ruleset-name.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.ruleset = response.plain();
            });
        }

        controller.editSkipTestUser = function(skipValue) {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoRestrictionRulesetSkipTestUser(ruleset.domain.name, ruleset.id, skipValue)
            .then(function (response) {
                if (response._status !== 0) {
                    notify.error(response._message);
                } else {
                    notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.SKIP_TEST_USER.SUCCESS");
                    controller.ruleset = response.plain();
                }
            }).catch(
                errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.SKIP_TEST_USER.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.editOutcome = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/ruleset/ruleset-outcome.html',
                controller: 'AutoRestrictionRulesetOutcomeModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    ruleset: function() { return angular.copy(controller.ruleset) },
                    outcomes: function() { return controller.outcomes },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/ruleset/ruleset-outcome.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.ruleset = response.plain();
            });
        }

        controller.editRestriction = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/ruleset/ruleset-restriction.html',
                controller: 'AutoRestrictionRulesetRestrictionModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    ruleset: function() { return angular.copy(controller.ruleset) },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/ruleset/ruleset-restriction.js'
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
                templateUrl: 'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/rule/rule.html',
                controller: 'AutoRestrictionRulesetRuleModal',
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
                                'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/rule/rule.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.ruleset = response.plain();
            });
        }

        controller.modifyRule = function($index) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/rule/rule.html',
                controller: 'AutoRestrictionRulesetRuleModal',
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
                                'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/components/rule/rule.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.ruleset = response.plain();
            });
        }

        controller.getFieldName = function(id) {
            for (var i = 0; i < controller.fields.length; i++) {
                if (controller.fields[i].id === id) return fields[i].displayName;
            }
            return null;
        }

        controller.getOperatorName = function(id) {
            for (var i = 0; i < controller.operators.length; i++) {
                if (controller.operators[i].id === id) return operators[i].displayName;
            }
            return null;
        }

        controller.getOutcomeName = function(id) {
            for (var i = 0; i < controller.outcomes.length; i++) {
                if (controller.outcomes[i].id === id) return outcomes[i].displayName;
            }
            return null;
        }

        controller.getEventName = function(id) {
            if (!id && id !== 0) return null;
            for (var i = 0; i < controller.events.length; i++) {
                if (controller.events[i].id === id) return events[i].displayName;
            }
            return null;
        }
    }
]);
