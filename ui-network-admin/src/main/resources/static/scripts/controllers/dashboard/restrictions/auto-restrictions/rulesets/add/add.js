'use strict'

angular.module('lithium').controller('AutoRestrictionRulesetAddController', ['fields', 'operators', 'outcomes', 'events', '$state', '$scope', '$userService', '$uibModal', 'bsLoadingOverlayService', 'errors', 'notify', 'AutoRestrictionRulesetRest', 'RestrictionsRest',
    function(fields, operators, outcomes, events, $state, $scope, $userService, $uibModal, bsLoadingOverlayService, errors, notify, rest, restrictionsRest) {
        let controller = this;

        controller.fields = fields.plain();
        controller.operators = operators.plain();
        controller.outcomes = outcomes.plain();
        controller.events = events.plain();

        console.debug("fields", controller.fields);
        console.debug("operators", controller.operators);
        console.debug("outcomes", controller.outcomes);

        controller.model = {domain: {name: undefined}, restrictionSet: {id: undefined}, rules: []};
        controller.domains = [];
        controller.showFields = {
            rootOnly: false,
            allEcosystem: false
        };

        controller.initFields = function() {
            return [
                {
                    className: 'col-xs-12 col-md-6',
                    key: "domain.name",
                    type: "ui-select-single",
                    templateOptions: {
                        label: "Domain",
                        description: "Choose the domain that you are creating the ruleset for",
                        required: true,
                        valueProp: 'name',
                        labelProp: 'name',
                        optionsAttr: 'ui-options',
                        "ngOptions": 'ui-options',
                        placeholder: '',
                        options: []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.DOMAIN.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.DOMAIN.DESCRIPTION" | translate'
                    },
                    controller: ['$scope', function($scope) {
                        $scope.to.options = $userService.domainsWithRole("AUTORESTRICTION_RULESETS_ADD");
                        controller.domains = $scope.to.options;
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
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.NAME.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.NAME.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'col-xs-12 col-md-6',
                    key: "restrictionSet.id",
                    type: "ui-select-single",
                    templateOptions: {
                        label: "Restriction",
                        description: "Choose the restriction for the auto-restriction outcome",
                        required: true,
                        valueProp: 'id',
                        labelProp: 'name',
                        optionsAttr: 'ui-options',
                        "ngOptions": 'ui-options',
                        placeholder: '',
                        options: []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.RESTRICTION.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.RESTRICTION.DESCRIPTION" | translate'
                    },
                    controller: ['$scope', function($scope) {
                        $scope.to.options = [];
                    }]
                }, {
                    className: 'col-xs-12 col-md-6',
                    key: "outcome",
                    type: "ui-select-single",
                    templateOptions: {
                        label: "Outcome",
                        description: "Choose the outcome of the ruleset",
                        required: true,
                        valueProp: 'id',
                        labelProp: 'displayName',
                        optionsAttr: 'ui-options',
                        "ngOptions": 'ui-options',
                        placeholder: '',
                        options: []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.OUTCOME.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.OUTCOME.DESCRIPTION" | translate'
                    },
                    controller: ['$scope', function($scope) {
                        $scope.to.options = outcomes;
                    }]
                }, {
                    className: 'top-space-10 col-xs-12 col-md-6',
                    type: 'checkbox2',
                    key: 'enabled',
                    templateOptions: {
                        label: 'Enabled',
                        fontWeight:'bold',
                        description: 'Should this ruleset be enabled?',
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.ENABLED.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.ENABLED.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'top-space-10 col-xs-12 col-md-6',
                    type: 'checkbox2',
                    key: 'skipTestUser',
                    templateOptions: {
                        label: 'Skip Test User',
                        fontWeight:'bold',
                        description: 'Should this ruleset skip test users?'
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.FIELDS.SKIP_TEST_USER.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.FIELDS.SKIP_TEST_USER.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'top-space-10 col-xs-12 col-md-6' + (controller.showFields.rootOnly ? ' ' : ' visibility: hidden'),
                    type: 'checkbox2',
                    key: 'rootOnly',
                    templateOptions: {
                        label: 'Apply on Root User Only',
                        fontWeight:'bold',
                        description: 'Should this ruleset only be applied to the linked root ecosystem user?'
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.FIELDS.ROOT_ONLY.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.FIELDS.ROOT_ONLY.DESCRIPTION" | translate'
                    }
                }, {
                    className: 'top-space-10 col-xs-12 col-md-6' + (controller.showFields.allEcosystem ? ' ' : ' visibility: hidden'),
                    type: 'checkbox2',
                    key: 'allEcosystem',
                    templateOptions: {
                        label: 'Apply on all Ecosystem Users',
                        fontWeight:'bold',
                        description: 'Should this ruleset be applied on all linked ecosystem users?'
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.FIELDS.ALL_ECOSYSTEM.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTO_RESTRICTIONS.RULE_SETS.FIELDS.ALL_ECOSYSTEM.DESCRIPTION" | translate'
                    }
                }
            ]
        }

        controller.inputFields = controller.initFields();

        $scope.$watch(function() { return controller.model.domain.name }, function(newValue, oldValue) {
            if (controller.domains.length > 0) {
                let d = controller.domains.find(s => s.name === newValue)
                if (!d.inEcosystem) {
                    controller.showFields = {
                        rootOnly: false,
                        allEcosystem: false
                    };
                    controller.model.rootOnly = false;
                    controller.model.allEcosystem = false;
                } else {
                    if (d.ecosystemRelationshipType === 'exclusive'
                        || d.ecosystemRelationshipType === 'member') {
                        controller.showFields = {
                            rootOnly: true,
                            allEcosystem: true
                        };
                        // controller.model.rootOnly = false;
                        // controller.model.allEcosystem = false;
                    } else if (d.ecosystemRelationshipType === 'root') {
                        controller.showFields = {
                            rootOnly: false,
                            allEcosystem: true
                        };
                        controller.model.rootOnly = false;
                        // controller.model.allEcosystem = false;
                    }
                }
            }

            controller.inputFields = controller.initFields();
        }, true);

        $scope.$watch(function() { return controller.model.rootOnly }, function(newValue, oldValue) {
            if (newValue) {
                controller.model.allEcosystem = false;
            }
        }, true);

        $scope.$watch(function() { return controller.model.allEcosystem }, function(newValue, oldValue) {
            if (newValue) {
                controller.model.rootOnly = false;
            }
        }, true);

        controller.setDomainRestrictionSets = function() {
            controller.model.restrictionSet.id = undefined;
            if (controller.model.domain !== undefined && controller.model.domain !== null &&
                controller.model.domain.name !== undefined && controller.model.domain.name !== null) {
                restrictionsRest.domainRestrictionSets(controller.model.domain.name).then(function (response) {
                    var domainRestrictionSets = response.plain();
                    controller.inputFields[2].templateOptions.options = domainRestrictionSets;
                });
            }
        }

        // Removing fields that are already added
        controller.getEligibleFilterFields = function() {
            var fields = angular.copy(controller.fields);
            for (var i = 0; i < controller.model.rules.length; i++) {
                for (var k = 0; k < fields.length; k++) {
                    if (controller.model.rules[i].field === fields[k].id) {
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

        controller.getOperatorName = function(id) {
            for (var i = 0; i < controller.operators.length; i++) {
                if (controller.operators[i].id === id) return operators[i].displayName;
            }
            return data;
        }

        controller.getEventName = function(id) {
            if (!id && id !== 0) return null;
            for (var i = 0; i < controller.events.length; i++) {
                if (controller.events[i].id === id) return events[i].displayName;
            }
            return null;
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
                    domainName: function() { return controller.model.domain.name; },
                    ruleset: function() { return null },
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
                controller.model.rules.push(response);
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
                    domainName: function() { return controller.model.domain.name; },
                    ruleset: function() { return null },
                    rule: function() { return angular.copy(controller.model.rules[$index]); },
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
            rest.autoRestrictionRulesetCreate(controller.model)
            .then(function (response) {
                if (response._status !== 0) {
                    notify.error(response._message);
                } else {
                    var data = response.plain();
                    notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.ADD.SUCCESS");
                    $state.go("^.view", { id: data.id });
                }
            }).catch(
                errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.ADD.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        $scope.$watch(function() { return controller.model.domain.name }, function(newValue, oldValue) {
            if (newValue !== oldValue) {
                controller.setDomainRestrictionSets();
            }
        });
    }
]);
