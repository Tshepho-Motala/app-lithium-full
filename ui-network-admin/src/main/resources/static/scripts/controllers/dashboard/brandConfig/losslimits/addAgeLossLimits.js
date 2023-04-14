'use strict';

angular.module('lithium').controller('addAgeLossLimit', ["domainName", "type", "$uibModalInstance", "notify", "errors", 'domainAgeLimitsRest', '$security', 'userThresholdRest', '$q',
    function (domainName, type, $uibModalInstance, notify, errors, domainAgeLimitsRest, $security, userThresholdRest, $q) {
        let controller = this;

        controller.model = {};
        controller.confirmationModel = {};
        controller.options = {};

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        controller.fields = [{
                className: "col-xs-6",
                type: "ui-number-mask",
                key: "ageMin",
                templateOptions: {
                    decimals: 0,
                    hidesep: true,
                    neg: false,
                    min: 0,
                    max: 140,
                    required: true,
                    "placeholder": "ageMin",
                    "options": []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMIN.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMIN.PLACEHOLDER" | translate'
                }
            },
            {
                className: "col-xs-6",
                type: "ui-number-mask",
                key: "ageMax",
                templateOptions: {
                    decimals: 0,
                    hidesep: true,
                    neg: false,
                    min: 0,
                    max: 140,
                    "required": true,
                    "placeholder": "ageMax",
                    "options": []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMAX.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMAX.PLACEHOLDER" | translate'
                },
                data: {
                    fieldToMatch: 'ageMin',
                    modelToMatch: controller.model,
                    matchFieldMessage: '$viewValue + " does match " + options.data.modelToMatch.ageMin'
                }
            },
            {
                className: "col-xs-6",
                type: "ui-number-mask",
                key: "dailyLossLimit",
                templateOptions: {
                    decimals: 2,
                    hidesep: true,
                    neg: false,
                    max: '',
                    "placeholder": "dailyLossLimit",
                    "options": []
                },
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DAILYLOSSLIMIT.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DAILYLOSSLIMIT.PLACEHOLDER" | translate'
                }
            },
             {
                 className: "col-xs-6",
                 type: "ui-number-mask",
                 key: "dailyWarningThreshold",
                 templateOptions: {
                     decimals: 2,
                     hidesep: true,
                     neg: false,
                     max: '',
                     "placeholder": "dailyWarningThreshold",
                     "options": []
                 },

                 "expressionProperties": {
                     'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.DAILY_THRESHOLD" | translate',
                     'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.DAILY_THRESHOLD" | translate'
                 }
             },
            {
                className: "col-xs-6",
                type: "ui-number-mask",
                key: "weeklyLossLimit",
                templateOptions: {
                    decimals: 2,
                    hidesep: true,
                    neg: false,
                    max: '',
                    "placeholder": "weeklyLossLimit",
                    "options": []
                },
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.WEEKLYLOSSLIMIT.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.WEEKLYLOSSLIMIT.PLACEHOLDER" | translate'
                }
            },

             {
                 className: "col-xs-6",
                 type: "ui-number-mask",
                 key: "weeklyWarningThreshold",
                 templateOptions: {
                     decimals: 2,
                     hidesep: true,
                     neg: false,
                     max: '',
                     "placeholder": "weeklyWarningThreshold",
                     "options": []
                 },
                 "expressionProperties": {
                     'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.WEEKLY_THRESHOLD" | translate',
                     'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.WEEKLY_THRESHOLD" | translate'
                 }
             },

            {
                className: "col-xs-6",
                type: "ui-number-mask",
                key: "monthlyLossLimit",
                templateOptions: {
                    decimals: 2,
                    hidesep: true,
                    neg: false,
                    max: '',
                    "placeholder": "monthlyLossLimit",
                    "options": []
                },
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.MONTHLYLOSSLIMIT.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.MONTHLYLOSSLIMIT.PLACEHOLDER" | translate'
                }
            },
             {
                 className: "col-xs-6",
                 type: "ui-number-mask",
                 key: "monthlyWarningThreshold",
                 templateOptions: {
                     decimals: 2,
                     hidesep: true,
                     neg: false,
                     max: '',
                     "placeholder": "monthlyWarningThreshold",
                     "options": []
                 },
                 "expressionProperties": {
                     'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.MONTHLY_THRESHOLD" | translate',
                     'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.MONTHLY_THRESHOLD" | translate'
                 },
             },
            {
                className: "col-xs-6",
                type: "ui-number-mask",
                key: "annualLossLimit",
                templateOptions: {
                    decimals: 2,
                    hidesep: true,
                    neg: false,
                    max: '',
                    "placeholder": "annualLossLimit",
                    "options": []
                },
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ANNUALLOSSLIMIT.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ANNUALLOSSLIMIT.PLACEHOLDER" | translate'
                }
            },
            {
                className: "col-xs-6",
                type: "ui-number-mask",
                key: "annualWarningThreshold",
                templateOptions: {
                    decimals: 2,
                    hidesep: true,
                    neg: false,
                    max: '',
                    "placeholder": "annualWarningThreshold",
                    "options": []
                },
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.ANNUAL_THRESHOLD" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.ANNUAL_THRESHOLD" | translate'
                },
            }
        ];

        controller.onSubmit = function () {
            if (controller.form.$valid) {

                let listDto = [];

                if (controller.model.dailyLossLimit > 0) {
                    const dto = {
                        amount: controller.model.dailyLossLimit * 100,
                        domainName: domainName,
                        granularity: 3,
                        ageMax: controller.model.ageMax,
                        ageMin: controller.model.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.dailyWarningThreshold
                    };
                    listDto.push(dto);
                }
                if (controller.model.weeklyLossLimit > 0) {
                    const dto = {
                        amount: controller.model.weeklyLossLimit * 100,
                        domainName: domainName,
                        granularity: 4,
                        ageMax: controller.model.ageMax,
                        ageMin: controller.model.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.weeklyWarningThreshold
                    };
                    listDto.push(dto);
                }
                if (controller.model.monthlyLossLimit > 0) {
                    const dto = {
                        amount: controller.model.monthlyLossLimit * 100,
                        domainName: domainName,
                        granularity: 2,
                        ageMax: controller.model.ageMax,
                        ageMin: controller.model.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.monthlyWarningThreshold
                    };
                    listDto.push(dto);
                }
                if (controller.model.annualLossLimit > 0) {
                    const dto = {
                        amount: controller.model.annualLossLimit * 100,
                        domainName: domainName,
                        granularity: 1,
                        ageMax: controller.model.ageMax,
                        ageMin: controller.model.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.annualWarningThreshold
                    };
                    listDto.push(dto);
                }

                if (listDto.length === 0) {
                    notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ADD.EMPTY_INPUT");
                } else {
                    domainAgeLimitsRest.saveAgeRanges(listDto).then(function (response) {
                        if (response._status === 200 && response.length > 0) {
                            listDto.forEach(function(dto) {
                                console.log(dto);
                                userThresholdRest.saveLossLimitThreshold(dto.domainName, null, dto.warningThreshold,null,"TYPE_LOSS_LIMIT", dto.granularity, dto.ageMin, dto.ageMax).then(function(response1) {
                                    if (angular.isDefined(response1) && (response1.id > 0)) {
                                        notify.success("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ADD.SUCCESS");
                                    }
                                }).catch(function (error) {
                                    errors.catch("Failed to update age ranges for thresholds.", false)(error)
                                });
                            });
                        }
                    });
                    $uibModalInstance.close();
                }
            }
        }
    }
]);
