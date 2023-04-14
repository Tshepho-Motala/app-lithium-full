'use strict';

angular.module('lithium')
    .controller('PlayerNotificationModal', ['user', 'notification', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', '$filter', 'userThresholdRest',
        function (user, notification, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, $filter, userThresholdRest) {
            var controller = this;

            controller.user = user;
            console.log(user, notification);

            controller.options = {};
            controller.model = {};
            controller.model.notification = notification;

            controller.fields = [{
                key: "notification",
                // type: "checkbox",
                // 		type: "toggle-switch",
                type: "checkbox2",
                templateOptions: {
                    label: "notification",
                    description: 'Enable player to receive notifications'
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.THRESHOLDS.WARNINGS.PLAYER.NOTIFICATION_CHANGE_LABEL" | translate ',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.THRESHOLDS.WARNINGS.PLAYER.NOTIFICATION_CHANGE_PLACE" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.THRESHOLDS.WARNINGS.PLAYER.NOTIFICATION_CHANGE_DESC" | translate'
                }
            }];

            controller.referenceId = 'player-notification-overlay';
            controller.submit = function() {
                bsLoadingOverlayService.start({referenceId:controller.referenceId});
                if (controller.form.$invalid) {
                    angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    return false;
                }

                userThresholdRest.setNotifications(controller.user.domain.name, controller.user.guid, controller.model.notification).then(function(response) {
                    console.log(response);
                    if (response === undefined) {
                        notify.success("UI_NETWORK_ADMIN.THRESHOLDS.WARNINGS.PLAYER.NOTIFICATION_CHANGE_DISABLED");
                        $uibModalInstance.close(false);
                    } else {
                        notify.success("UI_NETWORK_ADMIN.THRESHOLDS.WARNINGS.PLAYER.NOTIFICATION_CHANGE_ENABLED");
                        $uibModalInstance.close(response);
                    }
                }).catch(function() {
                    errors.catch('', false);
                }).finally(function() {
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                });
            };

            controller.cancel = function() {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]
);
