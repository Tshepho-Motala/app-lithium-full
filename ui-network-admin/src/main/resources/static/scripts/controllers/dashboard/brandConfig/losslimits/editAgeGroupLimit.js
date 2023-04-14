'use strict';

angular.module('lithium').controller('editAgeGroupLimit', ['$uibModalInstance', 'ageLimits', 'ageRanges', 'idsToEdit', 'limitFields', 'notify', 'domainAgeLimitsRest','userThresholdRest', 'errors',
    function ($uibModalInstance, ageLimits, ageRanges, idsToEdit, limitFields, notify, domainAgeLimitsRest, userThresholdRest, errors) {
        let controller = this;

        controller.model = {};
        controller.confirmationModel = {};

        controller.fields = [
            limitFields.ageMin(false, ageRanges.ageMin),
            limitFields.ageMax(false, ageRanges.ageMax)
        ];

        controller.rangeCheck = function(current, fromForm) {
            if (current.ageMin === fromForm.ageMin && current.ageMax === fromForm.ageMax) {
                notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.SIMILAR.ERROR");
                return true;
            }
            if (fromForm.ageMin > fromForm.ageMax) {
                notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.MINMAX.ERROR");
                return true;
            }
            return false;
        };

        controller.onSubmit = function() {
            controller.submitted = true;
            if (controller.rangeCheck(ageRanges, controller.model)) {
                return;
            }

            if (controller.form.$valid) {
                const data = {
                    domainName: ageRanges.domainName,
                    previousAgeMin: ageRanges.ageMin,
                    previousAgeMax: ageRanges.ageMax,
                    nextAgeMin: controller.model.ageMin,
                    nextAgeMax: controller.model.ageMax,
                    idsToEdit: idsToEdit
                };

                domainAgeLimitsRest.editAgeRangeMinMax(data).then(function(response) {
                    if (response._status === 200) {
                        ageLimits.forEach(function(al) {
                            if (angular.isDefined(al.thresholdPercentage)) {
                                userThresholdRest.saveLossLimitThreshold(al.domainName, al.threshold.id, al.thresholdPercentage,null,'TYPE_LOSS_LIMIT', al.granularity, controller.model.ageMin, controller.model.ageMax).then(function(response1) {
                                    if (angular.isDefined(response1) && (response1.id > 0)) {
                                        notify.success("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.EDIT.SUCCESS");
                                    }
                                }).catch(function (error) {
                                    errors.catch("Failed to update age ranges for thresholds.", false)(error)
                                });
                            }
                        });
                        $uibModalInstance.close();
                    };
                }).catch(function (error) {
                    notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.EDIT.ERROR");
                    errors.catch("", false)(error)
                });
            }
        };

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]
);
