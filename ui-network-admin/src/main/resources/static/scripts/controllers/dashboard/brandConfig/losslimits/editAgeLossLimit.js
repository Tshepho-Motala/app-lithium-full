'use strict';

angular.module('lithium').controller('editAgeLossLimit', ['limitDetails', 'domainAgeLimitsRest', "$uibModalInstance", "notify", "errors", '$q', "userThresholdRest",
    function (limitDetails, domainAgeLimitsRest, $uibModalInstance, notify, errors, $q, userThresholdRest) {
        let controller = this;
        controller.model = {};
        controller.amountCopy = angular.copy(limitDetails.amount);
        controller.thresholdPercentageCopy = angular.copy(limitDetails.thresholdPercentage);

        controller.options = {};

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        controller.fields = [
            {
                className: 'col-xs-6',
                type: 'ui-number-mask',
                key: 'ageMin',
                defaultValue: limitDetails.ageMin,
                templateOptions: {
                    decimals: 0,
                    hidesep: true,
                    neg: false,
                    min: 0,
                    max: 140,
                    disabled: true,
                    placeholder: 'ageMin',
                    options: []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMIN.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMIN.PLACEHOLDER" | translate'
                }
            },
            {
                className: 'col-xs-6',
                type: 'ui-number-mask',
                key: 'ageMax',
                defaultValue: limitDetails.ageMax,
                templateOptions: {
                    decimals: 0,
                    hidesep: true,
                    neg: false,
                    min: 0,
                    max: 140,
                    disabled: true,
                    placeholder: 'ageMax',
                    options: []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMAX.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITSAGE.FROMAGE.AGEMAX.PLACEHOLDER" | translate'
                }
            },
            {
                className: 'col-xs-6',
                type: 'ui-number-mask',
                key: 'amount',
                defaultValue: limitDetails.amount / 100,
                templateOptions: {
                    decimals: 2,
                    hidesep: true,
                    neg: false,
                    max: '',
                    required: true,
                    placeholder: 'amount',
                    options: []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITS.DOMAIN.AMOUNT.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITS.DOMAIN.AMOUNT.PLACEHOLDER" | translate'
                }
            },
            {
                className: 'col-xs-6',
                type: 'ui-number-mask',
                key: 'thresholdPercentage',
                defaultValue: limitDetails.thresholdPercentage,
                templateOptions: {
                    decimals: 2,
                    hidesep: true,
                    neg: false,
                    max: '',
                    required: true,
                    placeholder: 'Threshold Percentage',
                    options: []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.THRESHOLD_TITTLE" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.THRESHOLD_TITTLE" | translate'
                }
            }
        ];

        controller.onSubmit = function () {
            if (controller.form.$valid) {
                limitDetails.amount = (Big(controller.model.amount).times(100).toString());
                limitDetails.thresholdPercentage = controller.model.thresholdPercentage;
                var promises = [];
                if (limitDetails.amount !== controller.amountCopy.toString() && limitDetails.amount > 0) {
                    var domainAgeLimitsPromise = domainAgeLimitsRest.editAgeRange(limitDetails).then(function(response) {
                        return response;
                    }).catch(function() {
                        errors.catch('Could not save amount.', false);
                    });
                    promises.push(domainAgeLimitsPromise);
                }

                if (limitDetails.thresholdPercentage !== controller.thresholdPercentageCopy && controller.model.thresholdPercentage > 0) {
                    var userThresholdPromise = userThresholdRest.saveLossLimitThreshold(limitDetails.domainName, limitDetails.threshold.id, limitDetails.thresholdPercentage,null,'TYPE_LOSS_LIMIT', limitDetails.granularity, limitDetails.ageMin, limitDetails.ageMax).then(function(response) {
                        return response;
                    }).catch(function() {
                        errors.catch('Could not save threshold.', false);
                    });
                    promises.push(userThresholdPromise);
                }
                $q.all(promises).then(function(results) {
                   var resultsLength = Object.keys(results).length;
                   if (resultsLength === 0) {
                   } else if (resultsLength === 1) {
                       if (angular.isDefined(results[0]) && (results[0].id > 0)) {
                           notify.success("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.EDIT.SUCCESS");
                           $uibModalInstance.close();
                       } else {
                           notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.EDIT.ERROR");
                       }
                   } else if (resultsLength === 2) {
                       if ((angular.isDefined(results[0]) && (results[0].id > 0)) && (angular.isDefined(results[1]) && (results[1].id > 0))) {
                           notify.success(
                             "UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.EDIT.SUCCESS");
                           $uibModalInstance.close();
                       } else if ((angular.isDefined(results[0]) && (results[0].id > 0)) || (angular.isDefined(results[1]) && (results[1].id > 0))) {
                           notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.EDIT.ERROR");
                       } else {
                           notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.EDIT.ERROR");
                       }
                   }
                });
            }
        }
    }]
);
