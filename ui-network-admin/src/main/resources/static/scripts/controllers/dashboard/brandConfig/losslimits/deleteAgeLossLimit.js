'use strict';

angular.module('lithium').controller('deleteAgeLossLimit',[ 'limitDetails', 'domainAgeLimitsRest', "$uibModalInstance", "notify", "errors", 'userThresholdRest', '$q',
    function (limitDetails, domainAgeLimitsRest, $uibModalInstance, notify, errors, userThresholdRest, $q) {
        let controller = this;
        controller.model = {};
        controller.options = {};

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        controller.fields = [{
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
                disabled: true,
                placeholder: 'amount',
                options: []
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITS.DOMAIN.AMOUNT.NAME" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITS.DOMAIN.AMOUNT.PLACEHOLDER" | translate'
            }
        },{
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
                disabled: true,
                placeholder: 'Threshold Percentage',
                options: []
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.THRESHOLD_TITTLE" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.THRESHOLD_TITTLE" | translate'
            }
        }];

        controller.onSubmit = function () {
            var promises = [];
            var removeOneAgeRange = domainAgeLimitsRest.removeOneAgeRange(limitDetails.id).then(function(response) {
                return response;
            }).catch(function (error) {
                notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DELETE.ERROR");
                errors.catch("", false)(error)
            });
            promises.push(removeOneAgeRange);

            if (angular.isDefined(limitDetails.threshold.id)) {
                var disableLossLimitThreshold = userThresholdRest.disableLossLimitThreshold(
                  limitDetails.domainName, limitDetails.threshold.id).then(
                  function(response) {
                      return response;
                  }).catch(function(error) {
                    errors.catch("", false)(error)
                });
                promises.push(disableLossLimitThreshold);
            }

            $q.all(promises).then(function(results) {
                var resultsLength = Object.keys(results).length;
                if (resultsLength === 0) {
                } else if (resultsLength === 1) {
                    if (angular.isDefined(results[0]) && (results[0].status === 200)) {
                        notify.success("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DELETE.SUCCESS");
                        $uibModalInstance.close(results[0]);
                    } else {
                        // notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                        //TODO: add appropriate error message.
                    }
                } else if (resultsLength === 2) {
                    if ((angular.isDefined(results[0]) && (results[0].status
                        === 0))
                      && (angular.isDefined(results[1]) && (results[1].id
                        > 0))) {
                        notify.success(
                          "UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DELETE.SUCCESS");
                        $uibModalInstance.close(results[0]);
                    } else if ((angular.isDefined(results[0])
                        && (results[0].status === 0))
                      || (angular.isDefined(results[1]) && (results[1].id
                        > 0))) {
                        notify.success(
                          "UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DELETE.SUCCESS");
                        $uibModalInstance.close(results[0]);
                        // notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                        // $uibModalInstance.close();
                    } else {
                        // notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                        // $uibModalInstance.close();
                    }
                }
            });
        }
    }]
);
