'use strict';

angular.module('lithium').controller('AccessRulesView', ['accessRule', 'accessRulesRest', 'rest-provider', '$uibModal', 'errors', 'authorizationRest', 'notify', 'GeoRest', '$rootScope', 'UserRest', '$q',
    function (accessRule, accessRulesRest, providerRest, $uibModal, errors, authorizationRest, notify, GeoRest, $rootScope, userRest, $q) {
        var controller = this;

        controller.model = accessRule.plain();

        controller.modelOriginal = angular.copy(accessRule.plain());
        controller.options = { formState: { readOnly: true } };

        function populateRules() {
            var promises = [];
            controller.model.rules = [];
            controller.maxPriority = 0;
            angular.forEach(controller.model.accessControlList, function (acl) {
                if (acl.priority > controller.maxPriority) controller.maxPriority = acl.priority;
                promises.push(accessRulesRest.ruleHasTranData(controller.model.id, acl.id, 'list').then(function(response) {
                    controller.model.rules.push({
                        id: acl.id,
                        priority: acl.priority,
                        actionFailed: acl.actionFailed,
                        actionSuccess: acl.actionSuccess,
                        enabled: acl.enabled,
                        message: acl.message,
                        timeoutMessage: 'N\\A',
                        reviewMessage: 'N\\A',
                        type: 'list',
                        validateOnce: false,
                        ipResetTime: (acl.ipResetTime !== null) ? acl.ipResetTime : '',
                        subType: acl.list.listType.displayName,
                        subTypeName: acl.list.listType.name,
                        tranData: [], //acltd.plain();
                        hasTranData: (angular.isUndefined(response)?false:response),
                        description: acl.list.name,
                        ruleOutcomes: acl.accessControlListRuleStatusOptionConfigList
                    });
                }));
            });
            angular.forEach(controller.model.externalList, function (el) {
                if (el.priority > controller.maxPriority) controller.maxPriority = el.priority;
                promises.push(accessRulesRest.ruleHasTranData(controller.model.id, el.id, 'provider').then(function(response) {
                    controller.model.rules.push({
                        id: el.id,
                        priority: el.priority,
                        actionFailed: el.actionFailed,
                        actionSuccess: el.actionSuccess,
                        enabled: el.enabled,
                        message: el.message,
                        timeoutMessage: el.timeoutMessage,
                        reviewMessage: el.reviewMessage,
                        type: 'provider',
                        validateOnce: el.validateOnce,
                        ipResetTime: -1,
                        subType: el.providerUrl,
                        subTypeName: el.providerUrl,
                        tranData: [], //eltd.plain();
                        hasTranData: (angular.isUndefined(response)?false:response),
                        description: el.listName,
                        ruleOutcomes: el.externalListRuleStatusOptionConfigList
                    });
                }));
            });
            $q.all(promises).then(function() {
                controller.configTestResult = null;
                controller.setupConfigTest();
            });
        }
        populateRules();

        controller.fields = [{
            key : "domain.name",
            type: "input",
            templateOptions: {
                label: "",
                description: "",
                placeholder: "",
                disabled: true,
                required: true
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.DOMAIN.LABEL" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.DOMAIN.PLACEHOLDER" | translate',
                'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.DOMAIN.DESCRIPTION" | translate'
            },
            controller: ['$scope', function($scope) {
            }]
        },{
            key: "name",
            type: "input",
            templateOptions: {
                required: true,disabled: true,
                minlength: 2, maxlength: 35,
                //focus: true, --this causes issues when cancelling form
                onKeydown: function(value, options) {
                    options.validation.show = false;
                },
                onBlur: function(value, options) {
                    options.validation.show = true;
                }
            },
            modelOptions: {
                updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.NAME.LABEL" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.NAME.PLACEHOLDER" | translate',
                'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.NAME.DESCRIPTION" | translate'
            },
            validators: {
                pattern: {
                    expression: function($viewValue, $modelValue, scope) {
                        return /^[0-9a-zA-Z_\\.]+$/.test($viewValue);
                    },
                    message: '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.NAME.PATTERN" | translate'
                }
            },
            asyncValidators: {
                nameUnique: {
                    expression: function($viewValue, $modelValue, scope) {
                        var success = false;
                        return accessRulesRest.findByName(controller.model.domain.name, encodeURIComponent($viewValue)).then(function(response) {
                            if (angular.isUndefined(response) || (response._status == 404) || (response.length === 0) || (response.id === controller.model.id)) {
                                success = true;
                            }
                        }).catch(function() {
                            scope.options.validation.show = true;
                            errors.catch("UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.NAME.UNIQUE", false);
                        }).finally(function () {
                            scope.options.templateOptions.loading = false;
                            if (!success) {
                                return $q.reject("The access rule already exists");
                            }
                        });
                    },
                    message: '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.NAME.UNIQUE" | translate'
                }
            }
        },{
            key: "description",
            type: "input",
            templateOptions: {
                required: false,
                minlength: 0, maxlength: 235
            },
            optionsTypes: ['editable'],
            modelOptions: {
                updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.DESC.LABEL" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.DESC.PLACEHOLDER" | translate',
                'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.DESC.DESCRIPTION" | translate'
            }
        },{
            // className: "col-xs-4",
            key: "enabled",
//		type: "toggle-switch",
            type: "checkbox2",
            templateOptions: {
                label: "Enabled",
                description: "Should this bonus be enabled once created?",
                placeholder: "",
                required: false,
                fontWeight:'bold'
                //, onLabel:'enabled2', offLabel:'disabled2'
            },
            optionsTypes: ['editable'],
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.STATUS.LABEL" | translate',
                'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDIT.BASIC.STATUS.DESCRIPTION" | translate'
            }
        }];

        controller.onEdit = function() {
            controller.options.formState.readOnly = false;
            // controller.fields_personal[1].templateOptions.focus = true;
        }
        controller.onCancel = function() {
            controller.onReset();
            controller.options.formState.readOnly = true;
        }
        controller.onReset = function() {
            controller.model = angular.copy(controller.modelOriginal);
            populateRules();
        }

        controller.saveRuleset = function() {
            accessRulesRest.saveRuleset(controller.model.id, controller.model.name, controller.model.description, controller.model.enabled).then(function (response) {
                if(response._successful){
                    controller.options.formState.readOnly = true;
                    controller.model = response.plain();
                    controller.modelOriginal = angular.copy(response.plain());
                    populateRules();
                } else {
                    notify.warning(response._message);
                }
            });
        }

        controller.changeRuleOrderUp = function(rulesetId, type, ruleId) {
            accessRulesRest.changeRuleOrderUp(rulesetId, type, ruleId).then(function (response) {
                controller.model = response.plain();
                controller.modelOriginal = angular.copy(response.plain());
                populateRules();
            });
        }
        controller.changeRuleOrderDown = function(rulesetId, type, ruleId) {
            accessRulesRest.changeRuleOrderDown(rulesetId, type, ruleId).then(function (response) {
                controller.model = response.plain();
                controller.modelOriginal = angular.copy(response.plain());
                populateRules();
            });
        }

        controller.addRule = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/accesscontrol/rules/view/addrule.html',
                controller: 'AccessRuleAddListModal',
                controllerAs: 'controller',
                size: 'lg cascading-modal',
                backdrop: 'static',
                resolve: {
                    accessRule: function () {
                        return accessRule
                    },
                    lists: ['accessRulesRest', function (accessRulesRest) {
                        return accessRulesRest.eligableLists(accessRule.id).then(function (data) {
                            return data;
                        }).catch(function (error) {
                            errors.catch("", false)(error)
                        });
                    }],
                    externalProviders: ['rest-provider', function (providerRest) {
                        return providerRest.listByDomainAndType(accessRule.domain.name, "access").then(function (data) {
                            return data;
                        }).catch(function (error) {
                            errors.catch("", false)(error)
                        });
                    }],
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: ['scripts/controllers/dashboard/accesscontrol/rules/view/addrule.js']
                        })
                    }
                }
            });

            modalInstance.result.then(function (response) {
                controller.model = response;
                populateRules();
            });
        };

        controller.editRule = function(rule) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/accesscontrol/rules/view/editrule.html',
                controller: 'EditRulesetRule',
                controllerAs: 'controller',
                size: 'lg cascading-modal',
                resolve: {
                    accessRule: function () {
                        return controller.model;
                    },
                    rule: function () {
                        return rule;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: ['scripts/controllers/dashboard/accesscontrol/rules/view/editrule.js']
                        });
                    }
                }
            });

            modalInstance.result.then(function (response) {
                controller.model = response.plain();
                populateRules();
            });
        }

        controller.changeMessageModal = function(rule) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/accesscontrol/rules/view/savemessage.html',
                controller: 'AccessControlSaveMessage',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                resolve: {
                    accessRule: function () {
                        return controller.model;
                    },
                    rule: function () {
                        return rule;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: ['scripts/controllers/dashboard/accesscontrol/rules/view/savemessage.js']
                        })
                    }
                }
            });

            modalInstance.result.then(function (response) {
                controller.model = response.plain();
                populateRules();
            });
        }

        controller.deleteRule = function (rule) {
            accessRulesRest.deleteRule(accessRule.id, rule.id, rule.type).then(function (response) {
                controller.model = response.plain();
                populateRules();
            });
        }

        controller.changelogs = {
            domainName: accessRule.domain.name,
            entityId: accessRule.id,
            restService: accessRulesRest,
            reload: 0
        }

        controller.testConfig = function () {
            if (controller.configtestform.$invalid) {
                angular.element("[name='" + controller.configtestform.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }
            if (angular.isUndefined(controller.selectedUser)) {
                controller.configtestmodel.userGuid = $rootScope.principal.guid;
            } else {
                controller.configtestmodel.userGuid = controller.selectedUser.guid;
            }
            authorizationRest.checkAuthorization(accessRule.domain.name, accessRule.name, controller.configtestmodel).then(function (response) {
                controller.configTestResult = response.plain();
            });
        }

        controller.resetUserSearch = function() {
            controller.selectedUser = undefined;
        }
        controller.searchUsers = function(userGuid) {
            return userRest.search(controller.model.domain.name, userGuid).then(function(searchResult) {
                return searchResult.plain();
            }).catch(function(error) {
                errors.catch("", false)(error)
            });
        };

        controller.setupConfigTest = function () {
            controller.configtestoptions = {formState: {}};
            controller.configtestmodel = {};
            controller.configtestfields = [];
            controller.configtestkeys = [];

            angular.forEach(controller.model.rules, function(rule) {
                if (rule.enabled) {
                    var existingKey = false;
                    angular.forEach(controller.configtestkeys, function(key) {
                       if (key === rule.subTypeName) existingKey = true;
                    });
                    if (existingKey) return;
                    switch (rule.subTypeName.toLowerCase()) {
                        case "ip_list":
                        case "ip_range":
                            controller.configtestfields.push({
                                key: "ipAddress",
                                type: "input",
                                templateOptions: {
                                    label: "IP Address",
                                    description: "",
                                    placeholder: "",
                                    required: true
                                },
                                validators: {
                                    ipAddress: {
                                        expression: function ($viewValue, $modelValue, scope) {
                                            var value = $modelValue || $viewValue;
                                            return /^([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})$/.test(value);
                                        },
                                        message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid IP Address" : ""'
                                    }
                                }
                            });
                            break;
                        case "country_list":
                            controller.configtestfields.push({
                                key: "country",
                                type: "ui-select-single",
                                templateOptions: {
                                    label: "Country List (via ip data)",
                                    required: true,
                                    optionsAttr: 'bs-options',
                                    description: "",
                                    valueProp: 'name',
                                    labelProp: 'name',
                                    placeholder: 'Select Country',
                                    options: []
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.LIST.TYPES.COUNTRY_LIST" | translate'
                                },
                                controller: ['$scope', function ($scope) {
                                    GeoRest.countries().then(function (response) {
                                        $scope.to.options = response;
                                    });
                                }]
                            });
                            break;
                        case "country_list_profile":
                            controller.configtestfields.push({
                                key: "claimedCountry",
                                type: "ui-select-single",
                                templateOptions: {
                                    label: "Country List (via profile)",
                                    required: true,
                                    optionsAttr: 'bs-options',
                                    description: "",
                                    valueProp: 'name',
                                    labelProp: 'name',
                                    placeholder: 'Select Country',
                                    options: []
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.LIST.TYPES.COUNTRY_LIST_PROFILE" | translate'
                                },
                                controller: ['$scope', function ($scope) {
                                    GeoRest.countries().then(function (response) {
                                        $scope.to.options = response;
                                    });
                                }]
                            });
                            break;
                        case "state_list":
                            controller.configtestfields.push({
                                key: "state",
                                type: "uib-typeahead",
                                templateOptions: {
                                    label: "State List (via ip data)",
                                    description: "",
                                    placeholder: "",
                                    required: true,
                                    valueProp: 'name',
                                    labelProp: 'name',
                                    displayProp: 'name',
                                    displayOnly: false
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.LIST.TYPES.STATE_LIST" | translate'
                                },
                                controller: ['$scope', function ($scope) {
                                    $scope.searchTypeAhead = function (searchValue) {
                                        return GeoRest.level1s(searchValue).then(function (response) {
                                            $scope.to.options = response;
                                            return response;
                                        });
                                    }
                                }]
                            });
                            break;
                        case "state_list_profile":
                            controller.configtestfields.push({
                                key: "claimedState",
                                type: "uib-typeahead",
                                templateOptions: {
                                    label: "State List (via profile)",
                                    description: "",
                                    placeholder: "",
                                    required: true,
                                    valueProp: 'name',
                                    labelProp: 'name',
                                    displayProp: 'name',
                                    displayOnly: false
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.LIST.TYPES.STATE_LIST_PROFILE" | translate'
                                },
                                controller: ['$scope', function ($scope) {
                                    $scope.searchTypeAhead = function (searchValue) {
                                        return GeoRest.level1s(searchValue).then(function (response) {
                                            $scope.to.options = response;
                                            return response;
                                        });
                                    }
                                }]
                            });
                            break;
                        case "city_list":
                            controller.configtestfields.push({
                                key: "city",
                                type: "uib-typeahead",
                                templateOptions: {
                                    label: "City List (via ip data)",
                                    description: "",
                                    placeholder: "",
                                    required: true,
                                    valueProp: 'name',
                                    labelProp: 'name',
                                    displayProp: 'name',
                                    displayOnly: false
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.LIST.TYPES.CITY_LIST" | translate'
                                },
                                controller: ['$scope', function ($scope) {
                                    $scope.searchTypeAhead = function (searchValue) {
                                        return GeoRest.cities(searchValue).then(function (response) {
                                            $scope.to.options = response;
                                            return response;
                                        });
                                    }
                                }]
                            });
                            break;
                        case "city_list_profile":
                            controller.configtestfields.push({
                                key: "claimedCity",
                                type: "uib-typeahead",
                                templateOptions: {
                                    label: "City List (via profile)",
                                    description: "",
                                    placeholder: "",
                                    required: true,
                                    valueProp: 'name',
                                    labelProp: 'name',
                                    displayProp: 'name',
                                    displayOnly: false
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.LIST.TYPES.CITY_LIST_PROFILE" | translate'
                                },
                                controller: ['$scope', function ($scope) {
                                    $scope.searchTypeAhead = function (searchValue) {
                                        return GeoRest.cities(searchValue).then(function (response) {
                                            $scope.to.options = response;
                                            return response;
                                        });
                                    }
                                }]
                            });
                            break;
                        case "browser_list":
                            controller.configtestfields.push({
                                key: "browser",
                                type: "input",
                                templateOptions: {
                                    label: "Browser", description: "", placeholder: "", required: true
                                }
                            });
                            break;
                        case "os_list":
                            controller.configtestfields.push({
                                key: "os",
                                type: "input",
                                templateOptions: {
                                    label: "Operating System", description: "", placeholder: "", required: true
                                }
                            });
                            break;
                        case "post_list":
                            controller.configtestfields.push({
                                key: "postCode",
                                type: "input",
                                templateOptions: {
                                    label: "Postal Code", description: "", placeholder: "", required: true
                                }
                            });
                            break;
                    }
                    controller.configtestkeys.push(rule.subTypeName);
                }
            });
        }

        controller.setupConfigTest();
    }]
);
