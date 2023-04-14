'use strict';

angular.module('lithium').controller('AutoRestrictionRulesetRuleModal', ["domainName", "ruleset", "rule", "filterFields", "filterOperators", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "AutoRestrictionRulesetRest", "VerificationStatusRest", "RestrictionsRest",
    function (domainName, ruleset, rule, filterFields, filterOperators, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest, verificationStatusRest, restrictionsRest) {
        var controller = this;

        console.debug("filterFields", filterFields);
        console.debug("filterOperators", filterOperators);

        console.debug("ruleset", ruleset);

        console.debug("rule", rule);
        controller.rule = rule;

        controller.splitValuesIntoCodes = function (rule) {
            var values = rule.value.split(',');
            var codes = [];
            for (var i = 0; i < values.length; i++) {
                codes.push({code: values[i]});
            }
            return codes;
        }

        controller.model = rule || {enabled: true};
        if (rule !== undefined && rule !== null &&
                rule.operator !== undefined && rule.operator !== null &&
                rule.operator === 6 /*IN*/) {
            controller.model.value = controller.splitValuesIntoCodes(rule);
        }

        controller.fields = [];

        controller.setupFields = function() {
            controller.fields = [
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
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.ENABLED.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.ENABLED.DESCRIPTION" | translate'
                    }
                }, {
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
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.FIELD.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.FIELD.DESCRIPTION" | translate'
                    }
                }]

            if (controller.model.field != 6) {
                controller.fields.push({
                    className: 'col-xs-12',
                    key : "operator",
                    type : "ui-select-single",
                    templateOptions : {
                        label : "Operator",
                        description : "The operator to be used against the filter field value",
                        placeholder : 'Select Operator',
                        required : true,
                        optionsAttr: 'bs-options',
                        valueProp : 'id',
                        labelProp : 'displayName',
                        optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                        options: []
                    },
                    controller: ['$scope', function($scope) {
                        var operators = angular.copy(filterOperators);
                        var filteredOperators = [];
                        if (controller.model.field === 1) { // VERIFICATION_STATUS
                            for (var i = 0; i < operators.length; i++) {
                                if (operators[i].id === 6) { // IN
                                    filteredOperators.push(operators[i]);
                                    break;
                                }
                            }
                            controller.model.operator = 6;
                        } else if (controller.model.field === 4 ) { // CONTRA_PAYMENT_ACCOUNT_SET
                            var operators = angular.copy(filterOperators);
                            var filteredOperators = [];
                            for (var i = 0; i < operators.length; i++) {
                                if (operators[i].id === 1) { // EQUALS
                                    filteredOperators.push(operators[i]);
                                    break;
                                }
                            }
                            controller.model.operator = 1;
                        } else if (controller.model.field === 10 ) { // USER_STATUS_IS_USER_ENABLED
                            var operators = angular.copy(filterOperators);
                            var filteredOperators = [];
                            for (var i = 0; i < operators.length; i++) {
                                if (operators[i].id === 1) { // EQUALS
                                    filteredOperators.push(operators[i]);
                                    break;
                                }
                            }
                            controller.model.operator = 1;
                        } else {
                            filteredOperators = operators;

                            // Hiding the IN operator for now until it is used for other fields.
                            if (controller.model.operator === 6) controller.model.operator = null;
                            for (var i = 0; i < operators.length; i++) {
                                if (operators[i].id === 6) { // IN
                                    filteredOperators.splice(i, 1);
                                    break;
                                }
                            }
                        }
                        $scope.to.options = filteredOperators;
                    }],
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.OPERATOR.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.OPERATOR.DESCRIPTION" | translate'
                    }
                });
                if (controller.model.operator === undefined || controller.model.operator === null) return;
            }

            if (controller.model.operator === 0) { // BETWEEN
                controller.fields.push({
                    className: 'col-xs-12',
                    key: 'value',
                    type: 'ui-number-mask',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Value: Range Start',
                        description: "",
                        decimals: 0,
                        hidesep: true,
                        neg: false,
                        min: '0',
                        max: '',
                        required: true
                    }
                });
                controller.fields.push({
                    className: 'col-xs-12',
                    key: 'value2',
                    type: 'ui-number-mask',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Value: Range End',
                        description: "",
                        decimals: 0,
                        hidesep: true,
                        neg: false,
                        min: '0',
                        max: '',
                        required: true
                    }
                });
            } else {
                controller.model.value2 = null;
                if (controller.model.field === 1) { // VERIFICATION_STATUS
                    controller.fields.push({
                        className: 'col-xs-12',
                        key: 'value',
                        type: 'ui-select-multiple',
                        templateOptions : {
                            label: "Value",
                            valueProp: 'code',
                            labelProp: 'code',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: true
                        },
                        controller: ['$scope', function($scope) {
                            // if (domainName !== undefined && domainName !== null && domainName !== '') {
                                verificationStatusRest.findAll().then(function (response) {
                                    $scope.to.options = response.plain();
                                });
                            // }
                        }]
                    });
                } else if (controller.model.field === 4) { // CONTRA_PAYMENT_ACCOUNT_SET
                    controller.fields.push({
                        className: 'col-xs-12',
                        key: 'value',
                        type: 'ui-select-single',
                        templateOptions : {
                            label: 'Value',
                            valueProp: 'value',
                            labelProp: 'value',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            required: true,
                            options: []
                        },
                        controller: ['$scope', function($scope) {
                               $scope.to.options = [{value: 'True'},{value:'False'}]
                        }]
                    });

                } else if (controller.model.field === 6) { // SPECIFIC_RESTRICTION_EVENT
                    controller.model.operator = 1;
                    controller.fields.push({
                        className: 'col-xs-12',
                        key: 'event',
                        type: "ui-select-single",
                        templateOptions : {
                            label: "Event",
                            valueProp: 'id',
                            labelProp: 'displayName',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: true
                        },
                        controller: ['$scope', function($scope) {
                            rest.restrictionEvents().then(function (response) {
                                var restrictionEvents = response.plain();
                                $scope.to.options = restrictionEvents;
                            });
                        }],
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.EVENT.NAME" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.EVENT.DESCRIPTION" | translate'
                        }
                    });
                    controller.fields.push({
                        className: 'col-xs-12',
                        key: 'delay',
                        type: 'ui-number-mask',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            label: 'Delay',
                            description: "",
                            decimals: 0,
                            hidesep: true,
                            neg: false,
                            min: '0',
                            max: '',
                            required: false
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.DELAY.NAME" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.DELAY.DESCRIPTION" | translate'
                        }
                    });
                    if (controller.model.event == 2) { //SPECIFIC_RESTRICTION_APPLIED
                        controller.fields.push({
                            className: 'col-xs-12',
                            key: 'value',
                            type: "ui-select-single",
                            templateOptions : {
                                label: "Restriction",
                                description: "",
                                valueProp: 'name',
                                labelProp: 'name',
                                optionsAttr: 'ui-options',
                                ngOptions: 'ui-options',
                                options: [],
                                required: true
                            },
                            controller: ['$scope', function($scope) {
                                restrictionsRest.domainRestrictionSets(domainName).then(function (response) {
                                    $scope.to.options = response.plain();
                                });
                            }],
                            expressionProperties: {
                                'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.RESTRICTION.NAME" | translate',
                                'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.FIELDS.RESTRICTION.DESCRIPTION" | translate'
                            }
                        });
                    }
                } else if (controller.model.field === 10) { // USER_STATUS_IS_USER_ENABLED
                    controller.fields.push({
                        className: 'col-xs-12',
                        key: 'value',
                        type: 'ui-select-single',
                        templateOptions : {
                            label: 'Value',
                            valueProp: 'value',
                            labelProp: 'value',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            required: true,
                            options: []
                        },
                        controller: ['$scope', function($scope) {
                            $scope.to.options = [{value: 'True'},{value:'False'}]
                        }]
                    });

                } else {
                        controller.fields.push({
                            className: 'col-xs-12',
                            key: 'value',
                            type: 'ui-number-mask',
                            optionsTypes: ['editable'],
                            templateOptions: {
                                label: 'Value',
                                description: "",
                                decimals: 0,
                                hidesep: true,
                                neg: false,
                                min: '0',
                                max: '',
                                required: true
                            }
                        });
                    }
                }
            }

        controller.setupFields();

        $scope.$watch('[controller.model.field, controller.model.operator, controller.model.event]', function(newValue, oldValue) {
            if (newValue != oldValue) {
                controller.setupFields();
            }
        });

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            if (controller.model.operator === 0) { // BETWEEN
                if (controller.model.value2 < controller.model.value) {
                    notify.warning("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.BETWEEN.RANGEWARN");
                    return false;
                }
            }

            var model = angular.copy(controller.model);

            // TODO: Handle IN operator properly.
            //       Shouldn't be checking specific fields...
            if (model.field === 1) { //VERIFICATION_STATUS
                var value = '';
                for (var i = 0; i < model.value.length; i++) {
                    if (value.length > 0) value += ',';
                    value += model.value[i].code;
                }
                model.value = value;
            }

            if (ruleset !== undefined && ruleset !== null) {
                // We're editing
                if (rule !== undefined && rule !== null) {
                    // Modifying an existing rule
                    bsLoadingOverlayService.start({referenceId: "loading"});
                    rest.autoRestrictionRulesetRuleUpdate(ruleset.domain.name, ruleset.id, rule.id, model)
                        .then(function (response) {
                            if (response._status !== 0) {
                                notify.error(response._message);
                            } else {
                                notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.UPDATE.SUCCESS");
                                $uibModalInstance.close(response);
                            }
                        }).catch(
                        errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.UPDATE.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: "loading"});
                    });
                } else {
                    // Adding a new rule
                    bsLoadingOverlayService.start({referenceId: "loading"});
                    rest.autoRestrictionRulesetRuleAdd(ruleset.domain.name, ruleset.id, model)
                        .then(function (response) {
                            if (response._status !== 0) {
                                notify.error(response._message);
                            } else {
                                notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.ADD.SUCCESS");
                                $uibModalInstance.close(response);
                            }
                        }).catch(
                        errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.RULE.ADD.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: "loading"});
                    });
                }
            } else {
                // We're adding
                $uibModalInstance.close(model);
            }
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);
