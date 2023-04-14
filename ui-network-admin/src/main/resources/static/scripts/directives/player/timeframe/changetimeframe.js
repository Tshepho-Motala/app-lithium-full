"use strict";

angular.module("lithium").controller("ChangeTimeFrameModal", [
    "$uibModalInstance",
    "user",
    "$scope",
    "notify",
    "errors",
    "bsLoadingOverlayService",
    "$translate",
    "userLimitsRest",
    function (
        $uibModalInstance,
        user,
        $scope,
        notify,
        errors,
        bsLoadingOverlayService,
        $translate,
        userLimitsRest
    ) {
        let controller = this;
        controller.referenceId = "changetimeframelimits-overlay";

        controller.options = {};
        controller.model = {
            timeframeFromLimit: 0,
            timeframeToLimit: 0,
            currentFromLimit: null,
            currentToLimit: null,
            selectDelete: false,
        };

        controller.fields = [
            {
                className: "col-xs-12",
                key: "explanation",
                type: "examplewell",
                templateOptions: {
                    label: "",
                    explain: "",
                    description: "",
                },
                expressionProperties: {
                    'templateOptions.description': '"UI_NETWORK_ADMIN.TIME_SLOT.EXAMPLE_FST.SMALL_TEXT" | translate'
                },
            },
            {
                className: "col-md-6",
                key: "timeframeFromLimit",
                type: "input",
                templateOptions: {
                    type: "time",
                    label: "",
                    description: "",
                    required: true,
                    min:"00:00",
                    max:"23:59",
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.TIME_SLOT.FROM" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.TIME_SLOT.INPUT_DESC_UTC" | translate'
                },
            },
            {
                className: "col-md-6",
                key: "timeframeToLimit",
                type: "input",
                templateOptions: {
                    type: "time",
                    label: "",
                    description: "",
                    required: true,
                    min:"00:00",
                    max:"23:59",
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.TIME_SLOT.TO" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.TIME_SLOT.INPUT_DESC_UTC" | translate'
                },
            },
            {
                className: "col-xs-12",
                key: "",
                type: "examplewell",
                templateOptions: {
                    label: "",
                    explain: "",
                    description: "",
                },
                expressionProperties: {
                    'templateOptions.description': '"UI_NETWORK_ADMIN.TIME_SLOT.EXAMPLE_SCD.SMALL_TEXT" | translate'
                },
            },

            {
                className: "col-md-6",
                key: "currentFromLimit",
                type: "input",
                templateOptions: {
                    type: "text",
                    label: "",
                    description: "",
                    required: false,
                    readOnly: true,
                    disabled: true,
                    addFormControlClass: true,
                    placeholder: '',
                },
                expressionProperties: {
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TIME_SLOT.NOT_SET" | translate',
                    'templateOptions.label': '"UI_NETWORK_ADMIN.TIME_SLOT.CURRENT_FROM" | translate'
                },
            },
            {
                className: "col-md-6",
                key: "currentToLimit",
                type: "input",
                templateOptions: {
                    type: "text",
                    label: "Current To:",
                    description: "",
                    required: false,
                    readOnly: true,
                    disabled: true,
                    addFormControlClass: true,
                    placeholder: '',
                },
                expressionProperties: {
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TIME_SLOT.NOT_SET" | translate',
                    'templateOptions.label': '"UI_NETWORK_ADMIN.TIME_SLOT.CURRENT_TO" | translate'
                },
            },

            {
                className: "col-xs-12",
                key: "selectDelete",
                type: "checkbox",
                templateOptions: {
                    label: "",
                    placeholder: "",
                    description: "",
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.TIME_SLOT.SELECT_DELETE.LABEL" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.TIME_SLOT.SELECT_DELETE.DESC" | translate'
                },
            },
        ];

        //submit controller
        controller.submit = function () {
            bsLoadingOverlayService.start({referenceId: controller.referenceId});

            if (controller.model.selectDelete) {
                userLimitsRest
                    .removeTimeSlotLimit(user.guid, user.id, user.domain.name)
                    .then(onTimeSlotUpdateComplete)
                    .then(() => {
                        userLimitsRest.onTimeSlotLimitFetched()
                    })
                    .catch((error) => {
                        errors.catch("", false);
                    });
                return;
            }

            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("UI_NETWORK_ADMIN.TIME_SLOT.FORM_ERROR.EMPTY_INPUT");
                bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                return false;
            }

            const fromTime = controller.model.timeframeFromLimit;
            const toTime = controller.model.timeframeToLimit;

            let fromTimeUtcMs = 0;
            let toTimeUtcMs = 0;

            if(fromTime !== 0 && toTime !== 0) {
                fromTimeUtcMs = convertLocalDateToUtcDate(fromTime);
                toTimeUtcMs = convertLocalDateToUtcDate(toTime);
            }

            if(fromTimeUtcMs == toTimeUtcMs) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("UI_NETWORK_ADMIN.TIME_SLOT.ERROR.MESSAGE_EQUAL");
                bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                return false;
            }

            if (fromTime === undefined || toTime === undefined) {
                return; // We don't take invalid numbers
            }

            userLimitsRest
                .setTimeSlotLimit(
                    user.guid,
                    user.id,
                    user.domain.name,
                    fromTimeUtcMs,
                    toTimeUtcMs
                )
                .then(onTimeSlotUpdateComplete)
                .then(() => {
                    userLimitsRest.onTimeSlotLimitFetched()
                })
                .catch((error) => {
                    errors.catch("", false);
                });
        };

        controller.cancel = function () {
            $uibModalInstance.dismiss("cancel");
        };

        async function init() {
            const guid = user.guid;
            if(guid !== userLimitsRest.timeSlotLimitLastGuid) {
                await userLimitsRest.findTimeSlotLimit(guid, user.domain.name)
            }
            controller.model.currentFromLimit = userLimitsRest.timeSlotLastData.limitFromDisplay
            controller.model.currentToLimit = userLimitsRest.timeSlotLastData.limitToDisplay
        }

        function onTimeSlotUpdateComplete(response) {
            return new Promise((res, rej) => {
                bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                if(response === undefined) {
                    notify.error("UI_NETWORK_ADMIN.TIME_SLOT.SAVE.ERROR");
                    $uibModalInstance.close();
                    rej()
                }
                else {
                    notify.success("UI_NETWORK_ADMIN.TIME_SLOT.SAVE.SUCCESS");
                    $uibModalInstance.close();
                    res()
                }
            })
        }

        function convertLocalDateToUtcDate(local) {
            const utcDate = new Date()
            utcDate.setUTCHours(local.getHours(), local.getMinutes())
            return utcDate.getTime()
        }

        init()
    },
]);
