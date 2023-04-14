'use strict';

angular.module('lithium')
    .controller('CruksEditModal', ['user', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'UserRest', 'AccessProviderRest',
        function (user, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, UserRest, AccessProviderRest) {
            var controller = this;

            controller.user = user;
            controller.options = {};
            controller.model = {};
            controller.model.additionalData = angular.copy(controller.user.additionalData);
            $scope.disableSubmitButton = true;
            controller.fields = [
                {
                    key: "additionalData['cruksId']",
                    type: 'textarea',
                    templateOptions : {
                        description: "",
                        valueProp: 'value',
                        labelProp: 'label',
                        optionsAttr: 'ui-options', ngOptions: 'ui-options',
                        options: [],
                        required: true,
                        minlength: 5, maxlength: 65535, rows: 20
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.CRUKS.FIELDS.CRUKS_ID.LABEL" | translate'
                    },
                }
            ];

            controller.referenceId = 'cruks-overlay';
            controller.submit = function() {
                bsLoadingOverlayService.start({referenceId:controller.referenceId});
                if (controller.form.$invalid) {
                    angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    return false;
                }
                if (controller.model.additionalData['cruksId'] !== controller.user.additionalData['cruksId']) {
                    UserRest.saveAdditionalDataByUserGuid(controller.user.domain.name, controller.user.id, {cruksId: controller.model.additionalData['cruksId']}).then(function (response) {
                        controller.user.additionalData['cruksId'] = controller.model.additionalData['cruksId'];
                        $uibModalInstance.close(controller.user);
                    }).catch(function (error) {
                        errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE", false)
                    }).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        $uibModalInstance.close(controller.user);
                    });
                } else {
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    $uibModalInstance.close(controller.user);
                }
            };

            controller.verify = function () {
                bsLoadingOverlayService.start({referenceId:controller.referenceId})
                if (controller.model.additionalData['cruksId'] !== undefined) {
                    AccessProviderRest.verifyCruksId(controller.model.additionalData['cruksId'], controller.user.domain.name).then(function (response) {
                        switch (response) {
                            case 'PASS':
                                notify.success('UI_NETWORK_ADMIN.PLAYER.CRUKS.RESPONSES.PASS');
                                break;
                            case 'FAIL':
                                notify.warning('UI_NETWORK_ADMIN.PLAYER.CRUKS.RESPONSES.FAIL');
                                break;
                            case 'INVALID':
                                notify.warning('UI_NETWORK_ADMIN.PLAYER.CRUKS.RESPONSES.INVALID');
                                break;
                            default:
                                notify.error('UI_NETWORK_ADMIN.PLAYER.CRUKS.RESPONSES.ERROR');
                        }
                    }).catch(function () {
                        errors.catch('UI_NETWORK_ADMIN.PLAYER.CRUKS.RESPONSES.ERROR', false)
                    }).finally(function () {
                        $scope.disableSubmitButton = false;
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                    });
                } else {
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                }

            }

            controller.cancel = function() {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]
);
