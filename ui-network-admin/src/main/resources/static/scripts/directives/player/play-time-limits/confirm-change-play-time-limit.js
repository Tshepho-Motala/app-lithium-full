'use strict';

function attachDecreaseToMessage(controller, $translate) {
    controller.message =
        $translate.instant('UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.CONFIRMATION_POP_UP_DIALOG.DECREASE_WARNING_DESCRIPTION')
        + " to " + controller.applyDaysHoursMinutesFormat(controller.model.newGranularity.type, controller.model.newPtlTimeInSeconds).toLowerCase() + "?";
}

function attachMessage(controller, $translate, warningDescription) {
    controller.message =
        $translate.instant(warningDescription)
        + " from " + controller.applyDaysHoursMinutesFormat(controller.model.currentConfigRevision.granularity.type, controller.model.currentConfigRevision.secondsAllocated).toLowerCase()
        + " to " + controller.applyDaysHoursMinutesFormat(controller.model.newGranularity.type, controller.model.newPtlTimeInSeconds).toLowerCase() + "?";
}


angular.module('lithium')
.controller('ConfirmChangePlayTimeLimitModal',
    ['$uibModalInstance', 'fullPlayTimeLimitUserData', 'user', '$translate', 'bsLoadingOverlayService', 'UserRest', 'userLimitsRest', 'errors', 'notify', 'modifyUiModalInstance',
        function ($uibModalInstance, fullPlayTimeLimitUserData, user, $translate, bsLoadingOverlayService, UserRest, userLimitsRest, errors, notify, modifyuiModalInstance) {
            let controller = this;

            controller.referenceId = 'confirm-change-play-time-limit-overlay';
            controller.model = fullPlayTimeLimitUserData;
            controller.message = undefined;

            controller.init = function () {
                if (controller.model.currentConfigRevision.secondsAccumulated === undefined) {
                    attachDecreaseToMessage(controller, $translate);
                } else {
                    if (controller.model.newGranularity !== undefined && controller.model.newPtlTimeInSeconds !== undefined
                        && controller.model.currentConfigRevision.secondsAllocated !== undefined) {
                        const init = controller.model.newPtlTimeInSeconds >= controller.model.currentConfigRevision.secondsAllocated;
                        let warningDescription = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.CONFIRMATION_POP_UP_DIALOG.' + (init ? 'INCREASE_WARNING_DESCRIPTION':'DECREASE_WARNING_DESCRIPTION');
                        attachMessage(controller, $translate, warningDescription);
                    }
                }
            }

            controller.applyDaysHoursMinutesFormat = function (type, timeInSeconds) {
                let translation;
                switch (type) {
                    case 'GRANULARITY_DAY':
                        translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.DAILY';
                        break;
                    case 'GRANULARITY_WEEK':
                        translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.WEEKLY';
                        break;
                    case 'GRANULARITY_MONTH':
                        translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.MONTHLY';
                        break;
                    default:
                        translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.NOT_SET';
                        break;
                }
                const translated = $translate.instant(translation);
                let diff = new moment.duration(timeInSeconds, 'seconds');

                return translated + " " + diff.days() + 'd ' + diff.hours() + 'h ' + diff.minutes() + 'm ' + diff.seconds() + 's ';
            }

            controller.init();

            controller.submit = function () {
                bsLoadingOverlayService.start({referenceId: controller.referenceId});
                UserRest.setPlayerPlayTimeLimitConfigHttp(user.id, controller.model.newGranularity.id, controller.model.newPtlTimeInSeconds).then(function (response) {
                    bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                    if(response !== undefined && response.status === 200){
                        notify.success("UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.CONFIRMATION_POP_UP_DIALOG.SAVE.SUCCESS");
                        modifyuiModalInstance.close(response.data);
                    } else {
                        notify.error('UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.CONFIRMATION_POP_UP_DIALOG.SAVE.ERROR');
                    }
                    $uibModalInstance.close();

                });
            }

            controller.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]
);
