'use strict';

angular.module('lithium').controller('addGranularity',
    ['$uibModalInstance', 'notify', 'domainAgeLimitsRest', 'granularityPending', 'domainLimit', 'limitFields', '$security', 'userThresholdRest', 'errors',
    function ($uibModalInstance, notify, domainAgeLimitsRest, granularityPending, domainLimit, limitFields, $security, userThresholdRest, errors) {
        let controller = this;

        controller.model = {};
        controller.confirmationModel = {};

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        controller.fields = [];

        granularityPending.forEach( d => {
            if(d === 3){
                controller.fields.push(limitFields.dailyLossLimit(false, 0));
                controller.fields.push(limitFields.dailyWarningThreshold(false, 0));
            }

            if(d === 4){
                controller.fields.push(limitFields.weeklyLossLimit(false, 0));
                controller.fields.push(limitFields.weeklyWarningThreshold(false, 0));
            }

            if(d === 2){
                controller.fields.push(limitFields.monthlyLossLimit(false, 0));
                controller.fields.push(limitFields.monthlyWarningThreshold(false, 0));
            }

            if(d === 1){
                controller.fields.push(limitFields.annualLossLimit(false, 0));
                controller.fields.push(limitFields.annualWarningThreshold(false, 0));
            }
        });

        controller.onSubmit = function () {
            controller.submitted = true;

            if (controller.form.$valid) {
                let dto = [];

                if (controller.model.dailyLossLimit > 0) {
                    const daily = {
                        amount: controller.model.dailyLossLimit * 100,
                        domainName: domainLimit.domainName,
                        granularity: 3,
                        ageMax: domainLimit.ageMax,
                        ageMin: domainLimit.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.dailyWarningThreshold
                    };
                    dto.push(daily);
                }

                if (controller.model.weeklyLossLimit > 0) {
                    const weekly = {
                        amount: controller.model.weeklyLossLimit * 100,
                        domainName: domainLimit.domainName,
                        granularity: 4,
                        ageMax: domainLimit.ageMax,
                        ageMin: domainLimit.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.weeklyWarningThreshold
                    };
                    dto.push(weekly);
                }

                if (controller.model.monthlyLossLimit > 0) {
                    const monthly = {
                        amount: controller.model.monthlyLossLimit * 100,
                        domainName: domainLimit.domainName,
                        granularity: 2,
                        ageMax: domainLimit.ageMax,
                        ageMin: domainLimit.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.monthlyWarningThreshold
                    };
                    dto.push(monthly);
                }

                if (controller.model.annualLossLimit > 0) {
                    const annual = {
                        amount: controller.model.annualLossLimit * 100,
                        domainName: domainLimit.domainName,
                        granularity: 1,
                        ageMax: domainLimit.ageMax,
                        ageMin: domainLimit.ageMin,
                        type: 2,
                        creatorGuid: $security.guid(),
                        warningThreshold: controller.model.annualWarningThreshold
                    };
                    dto.push(annual);
                }

                domainAgeLimitsRest.saveAgeRanges(dto).then(function(response) {
                    if (response._status === 200) {
                        dto.forEach(function(al) {
                            userThresholdRest.saveLossLimitThreshold(
                              al.domainName, null,
                              al.warningThreshold,
                              null,'TYPE_LOSS_LIMIT',
                              al.granularity, al.ageMin,
                              al.ageMax).then(function(response1) {
                                if (angular.isDefined(response1) && (response1.id > 0)) {
                                    notify.success("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ADD.SUCCESS");
                                }
                            }).catch(function(error) {
                                errors.catch("", false)(error)
                            });
                        });
                        notify.success("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ADD.SUCCESS");
                        $uibModalInstance.close();
                    } else {
                        notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ADD.ERROR");
                    }
                }).catch(function (error) {
                    notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ADD.ERROR");
                    errors.catch("", false)(error)
                });
            }
        };
    }]
)
