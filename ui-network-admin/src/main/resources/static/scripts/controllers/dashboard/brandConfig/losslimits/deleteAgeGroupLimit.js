'use strict';

angular.module('lithium').controller('deleteAgeGroupLimit', ['$uibModalInstance', 'ageRanges', 'idsToDelete', 'limitFields', 'notify', 'domainAgeLimitsRest', 'thresholdIds', 'userThresholdRest',
    function ($uibModalInstance, ageRanges, idsToDelete, limitFields, notify, domainAgeLimitsRest, thresholdIds, userThresholdRest) {
        let controller = this;

        controller.model = {};
        controller.confirmationModel = {};

        controller.fields = [
            limitFields.ageMin(true, ageRanges.ageMin),
            limitFields.ageMax(true, ageRanges.ageMax)
        ];

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        controller.onSubmit = function () {
            controller.submitted = true;

            if (controller.form.$valid) {
                domainAgeLimitsRest.removeAgeRange(idsToDelete).then(function(response) {
                    if (response._status === 200 && angular.isDefined(thresholdIds)) {
                        thresholdIds.forEach(function(tid) {
                            userThresholdRest.disableLossLimitThreshold(ageRanges.domainName, tid).then(function(answer) {
                                notify.success("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DELETE.SUCCESS");
                            }).catch(function(error) {
                                errors.catch("", false)(error)
                            });
                        });
                        $uibModalInstance.close();
                    }
                    $uibModalInstance.close();
                }).catch(function (error) {
                    notify.error("UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.DELETE.ERROR");
                    errors.catch("", false)(error)
                    $uibModalInstance.close();
                });
            }
        };

    }]
);
