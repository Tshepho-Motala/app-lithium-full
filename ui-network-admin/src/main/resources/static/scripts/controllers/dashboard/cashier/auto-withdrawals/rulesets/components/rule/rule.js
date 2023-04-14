'use strict';

angular.module('lithium').controller('AutoWithdrawalRulesetRuleModal', ["$translate", "domainName", "ruleset", "rule", "filterFields", "filterOperators", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "AutoWithdrawalRulesetRest", "rest-cashier", "VerificationStatusRest", "UserRest",
    function ($translate, domainName, ruleset, rule, filterFields, filterOperators, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest, cashierRest, verificationStatusRest, UserRest) {
        var controller = this;

        console.debug("filterFields", filterFields);
        console.debug("filterOperators", filterOperators);

        console.debug("ruleset", ruleset);

        console.debug("rule", rule);
        controller.rule = rule;
        //init fields START
        controller.setSettingFields = function(initData) {
            controller.settingsFields = [];
            if (initData.settings) {
                angular.forEach(initData.settings, function (setting, index) {
                    controller.settingsFields.push(controller.getRuleField(setting.type,'settings['+ index + ']', setting.options,"Settings", setting.description,""));
                });
            }
        }

        controller.setValueFields = function(valueField, isBetween) {
            controller.valueFields = [];
            if (valueField) {
                var valueLabel = isBetween === true ? "Value: Range Start" : "Value";
                controller.valueFields.push(controller.getRuleField(valueField.type, 'value', valueField.options, valueLabel, "", ""));
                if (isBetween === true) { // BETWEEN operator
                    controller.valueFields.push(controller.getRuleField(valueField.type, 'value2', valueField.options, 'Value: Range End', "", ""));
                }
            }
        }

        controller.setOperator = function(operator) {
            controller.operatorField = null;
            if (operator) {
                controller.operatorField = controller.getRuleField(operator.type, 'operator', operator.options,
                    "Operator",
                    "The operator to be used against the filter field value", 'Select Operator',false);
            }
        }

        controller.getRuleField = function(type, model, options, label, description, placeholder) {
            var field = {
                className: 'col-xs-12',
                key: model,
                type: "input",
                templateOptions: {
                    label: label,
                    description: description,
                    placeholder: placeholder,
                    required: true
                }
            };
            switch (type) {
                case 'LONG':
                    field.type = 'ui-number-mask';
                    field.optionsTypes = ['editable'];
                    field.templateOptions.decimals = 0;
                    field.templateOptions.hidesep = true;
                    field.templateOptions.neg = false;
                    field.templateOptions.min = '0';
                    field.templateOptions.max = '';
                    break;
                case 'BOOLEAN':
                case "SINGLESELECT":
                    if (options && options.length <= 0) {
                        field.templateOptions.disabled = true;
                        field.templateOptions.placeholder = 'No data to select';
                    }
                    field.type = 'ui-select-single';
                    field.templateOptions.valueProp = 'id';
                    field.templateOptions.labelProp = 'name';
                    field.templateOptions.optionsAttr = 'ui-options';
                    field.templateOptions.ngOptions = 'ui-options';
                    field.templateOptions.options = [];
                    field.controller = ['$scope', function ($scope) {
                        $scope.to.options = options;
                    }];
                    break;
                case "MULTISELECT":
                    if (options && options.length <= 0) {
                        field.templateOptions.disabled = true;
                        field.templateOptions.placeholder = 'No data to select';
                    }
                    field.type = 'ui-select-multiple';
                    field.templateOptions.valueProp = 'id';
                    field.templateOptions.labelProp = 'name';
                    field.templateOptions.optionsAttr = 'ui-options';
                    field.templateOptions.ngOptions = 'ui-options';
                    field.templateOptions.options = [];
                    field.controller = ['$scope', function ($scope) {
                        $scope.to.options = options;
                    }];
                    break;
                case 'STRING':
                default:
                    break;
            }
            return field;
        }

        controller.setStaticFields = function () {
             controller.staticFields = [
                {
                    className: 'col-xs-12 col-md-6',
                    type: 'checkbox2',
                    key: 'enabled',
                    templateOptions: {
                        label: 'Enabled',
                        fontWeight: 'bold',
                        description: 'Should this rule be enabled?',
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELDS.ENABLED.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELDS.ENABLED.DESCRIPTION" | translate'
                    }
                },
                {
                    className: 'col-xs-12',
                    key: "field",
                    type: "ui-select-single",
                    templateOptions: {
                        label: "Field",
                        description: "The field to filter by",
                        placeholder: 'Select Field',
                        required: true,
                        optionsAttr: 'bs-options',
                        valueProp: 'id',
                        labelProp: 'displayName',
                        optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                        options: []
                    },
                    controller: ['$scope', function ($scope) {
                        $scope.to.options = filterFields;
                    }],
                    expressionProperties: {
                        'templateOptions.disabled': function (viewValue, modelValue, scope) {
                            return (controller.rule !== null);
                        },
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELDS.FIELD.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELDS.FIELD.DESCRIPTION" | translate'
                    }
                }
            ]
        }
        //init fields END

        //handle change of rule type or operator START
        controller.handleRuleTypeChanged = function () {
            if (controller.ruleInitData) {
                if (controller.firstInit === false) {
                    controller.model.operator = null;
                    controller.model.value = null;
                    controller.model.settings = [];
                }
                controller.setSettingFields(controller.ruleInitData);
                controller.setOperator(controller.ruleInitData.operator);
                controller.setValueFields(controller.ruleInitData.value, controller.model.operator == 0);
                controller.updateRuleFields();
            }
        }

        controller.handleOperatorChanged = function (operator) {
            if (controller.ruleInitData) {
                if (controller.firstInit === false && (!operator || operator != 0)) {
                   controller.model.value2 = null;
                }
                controller.setValueFields(controller.ruleInitData.value, controller.model.operator == 0);
                controller.updateRuleFields();
            }
        }

        controller.updateRuleFields = function() {
            var fields = angular.copy(controller.staticFields)

            angular.forEach(controller.settingsFields, function(value) {
                fields.push(value);
            });

            if (controller.operatorField) fields.push(controller.operatorField);

            angular.forEach(controller.valueFields, function(value) {
                fields.push(value);
            });
            controller.fields = fields;
        }
        //handle change of rule type or operator START

        //map model to and from rule START
        controller.optionsToString = function(options) {
            var value = [];
            angular.forEach(options, function (option) {
                value.push(option.id);
            });
            return value.join(',');
        }

        controller.optionsFromString = function(value) {
            return value.toString().split(',');
        }

        controller.fromRuleSettings = function() {
            if (controller.firstInit === true && rule.settings) {
                var settingsOptions = [];
                angular.forEach(rule.settings, function (setting) {
                    const map = new Map(Object.entries(controller.ruleInitData.settings));
                    var initSetting = [...map.values()].find(o => o.key == setting.key);
                    settingsOptions[initSetting.id] = [];
                    if (initSetting.type === 'MULTISELECT') {
                        angular.forEach(controller.optionsFromString(setting.value), function (option) {
                            var initOption = initSetting.options.find(o => o.id == option);
                            if (initOption) {
                                settingsOptions[initSetting.id].push(initOption);
                            } else {
                                console.error("Unsupported option id=" + option.id + " for rule settings: " + rule.field.displayName);
                            }
                        });
                    } else {
                        settingsOptions[initSetting.id] = setting.value;
                    }
                });
                controller.model.settings = settingsOptions;
            }
        }

        controller.fromRuleValue = function() {
            controller.fromRuleField('value', controller.ruleInitData);
        }
        controller.fromRuleOperator = function() {
            if (controller.firstInit == true && (rule.operator || rule.operator == 0)) {
                controller.model.operator = rule.operator + '';
            }
        }

        controller.fromRuleField = function(fieldName, data) {
            if (controller.firstInit == true && rule[fieldName]) {
                if (data[fieldName].type === 'MULTISELECT') {
                    var valueOptions = [];
                    angular.forEach(rule[fieldName], function (option) {
                        var valueOption = data[fieldName].options.find(o => o.id == option.value);
                        if (valueOption) {
                            valueOptions.push(valueOption);
                        } else {
                            console.error("Unsupported option id=" + option.id + " for rule settings: " + rule.field.displayName);
                        }
                    });
                    controller.model[fieldName] = valueOptions;
                } else {
                    controller.model[fieldName] = rule[fieldName][0].description;
                }
            }
        }

        controller.toRuleSettings = function() {
            if (controller.model.settings) {
                var ruleSettings = [];
                angular.forEach(controller.model.settings, function (setting, settingId) {
                    if (setting || setting === 0) {
                        var ruleSetting = {
                            code: { id: controller.ruleInitData.settings[settingId].id,
                                type: controller.ruleInitData.settings[settingId].type },
                            key: controller.ruleInitData.settings[settingId].key
                        };
                        ruleSetting.value = (controller.ruleInitData.settings[settingId].type === 'MULTISELECT')
                            ? controller.optionsToString(setting)
                            : (setting.id) ? setting.id : setting;
                    }
                    return ruleSettings.push(ruleSetting);
                });
                return ruleSettings;
            }
        }
        //map model to and from rule END

        controller.getInitializationData = function(domainName, ruleTypeId) {
            rest.autoWithdrawalRuleInitData(domainName, ruleTypeId).then(function (response) {
                if (response._status !== 0) {
                    notify.error(response._message);
                } else {
                    controller.ruleInitData = response.plain();

                    controller.fromRuleValue();
                    controller.fromRuleOperator();
                    controller.fromRuleSettings();
                    controller.handleRuleTypeChanged();
                    controller.firstInit = false;
                }
            });
        }

        ///////////////////////////  EXECUTION /////////////////////////////////
        controller.firstInit = rule ? true : false;

        controller.model = rule || {enabled: true};

        controller.setStaticFields();

        if (controller.model.field || controller.model.field === 0) {
            controller.getInitializationData(domainName, controller.model.field);
        } else {
            controller.updateRuleFields();
        }

        $scope.$watch('[controller.model.operator]', function (newValue, oldValue) {
            if (newValue != oldValue && (oldValue == 0 || newValue == 0)) { //rerender on BETWEEN only
                controller.handleOperatorChanged(newValue);
            }
        });
        $scope.$watch('[controller.model.field]', function (newValue, oldValue) {
            if (newValue != oldValue) {
                controller.getInitializationData(domainName, newValue[0]);//TODO handle exception by showing error and leave old value
            }
        });


        controller.onSubmit = function () {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            if (controller.model.operator == 0) { // BETWEEN
                if (controller.model.value2 < controller.model.value) {
                    notify.warning("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.BETWEEN.RANGEWARN");
                    return false;
                }
            }

            var model = angular.copy(controller.model);

            model.settings = controller.toRuleSettings();
            model.operator = parseInt(model.operator);

            if (controller.ruleInitData.value.type === 'MULTISELECT') {
                model.value = controller.optionsToString(model.value);
            }

            if (ruleset !== undefined && ruleset !== null) {
                // We're editing
                if (rule !== undefined && rule !== null) {
                    // Modifying an existing rule
                    bsLoadingOverlayService.start({referenceId: "loading"});
                    rest.autoWithdrawalRulesetRuleUpdate(ruleset.domain.name, ruleset.id, rule.id, model)
                        .then(function (response) {
                            if (response._status !== 0) {
                                notify.error(response._message);
                            } else {
                                notify.success("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.UPDATE.SUCCESS");
                                $uibModalInstance.close(response);
                            }
                        }).catch(
                        errors.catch("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.UPDATE.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: "loading"});
                    });
                } else {
                    // Adding a new rule
                    bsLoadingOverlayService.start({referenceId: "loading"});
                    rest.autoWithdrawalRulesetRuleAdd(ruleset.domain.name, ruleset.id, model)
                        .then(function (response) {
                            if (response._status !== 0) {
                                notify.error(response._message);
                            } else {
                                notify.success("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.ADD.SUCCESS");
                                $uibModalInstance.close(response);
                            }
                        }).catch(
                        errors.catch("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.ADD.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: "loading"});
                    });
                }
            } else {
                // We're adding
                $uibModalInstance.close(model);
            }
        }

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);
