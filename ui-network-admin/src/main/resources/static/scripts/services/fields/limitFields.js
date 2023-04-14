angular.module('lithium').factory('limitFields', ['validatorService',
        function (validatorService) {
            let service = this;

            service.ageMax = function (disabled, value) {
                return {
                    className: "col-xs-6",
                    type: "ui-number-mask",
                    key: "ageMax",
                    defaultValue: value,
                    templateOptions: {
                        decimals: 0,
                        hidesep: true,
                        neg: false,
                        min: 0,
                        max: 140,
                        disabled: disabled,
                        "placeholder": "ageMax",
                        "description": "ageMax",
                        "options": []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMAX.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMAX.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMAX.DESCRIPTION" | translate'
                    }
                };
            }

            service.ageMin = function (disabled, value) {
                return {
                    className: "col-xs-6",
                    type: "ui-number-mask",
                    key: "ageMin",
                    defaultValue: value,
                    templateOptions: {
                        decimals: 0,
                        hidesep: true,
                        neg: false,
                        min: 0,
                        max: 140,
                        disabled: disabled,
                        "placeholder": "ageMin",
                        "description": "ageMin",
                        "options": []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMIN.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMIN.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMIN.DESCRIPTION" | translate'
                    }
                };
            }

            service.dailyLossLimit = function (disabled, value) {
                return {
                    className: "col-xs-6",
                    type: "ui-number-mask",
                    key: "dailyLossLimit",
                    defaultValue: value,
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        max: '',
                        disabled: disabled,
                        "placeholder": "dailyLossLimit",
                        "description": "dailyLossLimit",
                        "options": []
                    },
                    "expressionProperties": {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DAILYLOSSLIMIT.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DAILYLOSSLIMIT.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DAILYLOSSLIMIT.DESCRIPTION" | translate'
                    }
                };
            }
            service.dailyWarningThreshold= function(disabled,value) {
               return {
                    className: "col-xs-6",
                    type: "ui-percentage-mask",
                    key: "dailyWarningThreshold",
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        max: 100,
                        disabled: disabled,
                        "placeholder": "dailyWarningThreshold",
                        "options": []
                    },
                    "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.DAILY_THRESHOLD" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.DAILY_THRESHOLD" | translate'
                    }
                }
            }

            service.weeklyLossLimit = function (disabled, value) {
                return {
                    className: "col-xs-6",
                    type: "ui-number-mask",
                    key: "weeklyLossLimit",
                    defaultValue: value,
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        disabled: disabled,
                        max: '',
                        "placeholder": "weeklyLossLimit",
                        "description": "weeklyLossLimit",
                        "options": []
                    },
                    "expressionProperties": {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.WEEKLYLOSSLIMIT.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.WEEKLYLOSSLIMIT.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.WEEKLYLOSSLIMIT.DESCRIPTION" | translate'
                    }
                };
            }
            service.weeklyWarningThreshold = function(disabled,value) {
                return {
                    className: "col-xs-6",
                    type: "ui-percentage-mask",
                    key: "weeklyWarningThreshold",
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        max: 100,
                        disabled: disabled,
                        "placeholder": "weeklyWarningThreshold",
                        "options": []
                    },
                    "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.WEEKLY_THRESHOLD" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.WEEKLY_THRESHOLD" | translate'
                    }
                }
            }

            service.monthlyLossLimit = function (disabled, value) {
                return {
                    className: "col-xs-6",
                    type: "ui-number-mask",
                    key: "monthlyLossLimit",
                    defaultValue: value,
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        disabled: disabled,
                        max: '',
                        "placeholder": "monthlyLossLimit",
                        "description": "monthlyLossLimit",
                        "options": []
                    },
                    "expressionProperties": {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.MONTHLYLOSSLIMIT.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.MONTHLYLOSSLIMIT.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.MONTHLYLOSSLIMIT.DESCRIPTION" | translate'
                    }
                };
            }
            service.monthlyWarningThreshold = function(disabled,value) {
              return {
                    className: "col-xs-6",
                    type: "ui-percentage-mask",
                    key: "monthlyWarningThreshold",
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        max: 100,
                        disabled: disabled,
                        "placeholder": "monthlyWarningThreshold",
                        "options": []
                    },
                    "expressionProperties": {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.MONTHLY_THRESHOLD" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.MONTHLY_THRESHOLD" | translate'
                    }
                }
            }

            service.annualLossLimit = function (disabled, value) {
                return {
                    className: "col-xs-6",
                    type: "ui-number-mask",
                    key: "annualLossLimit",
                    defaultValue: value,
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        disabled: disabled,
                        max: '',
                        "placeholder": "annualLossLimit",
                        "description": "annualLossLimit",
                        "options": []
                    },
                    "expressionProperties": {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ANNUALLOSSLIMIT.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ANNUALLOSSLIMIT.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ANNUALLOSSLIMIT.DESCRIPTION" | translate'
                    }
                };
            }

            service.annualWarningThreshold = function(disabled,value) {
                return {
                    className: "col-xs-6",
                    type: "ui-percentage-mask",
                    key: "annualWarningThreshold",
                    templateOptions: {
                        decimals: 2,
                        hidesep: true,
                        neg: false,
                        max: 100,
                        disabled: disabled,
                        "placeholder": "annualWarningThreshold",
                        "options": []
                    },
                    "expressionProperties": {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.ANNUAL_THRESHOLD" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.ANNUAL_THRESHOLD" | translate'
                    }
                }
            }

            return service;
        }
    ]
);

