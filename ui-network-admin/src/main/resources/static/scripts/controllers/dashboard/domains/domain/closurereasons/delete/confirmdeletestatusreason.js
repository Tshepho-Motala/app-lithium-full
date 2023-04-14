'use strict';
angular.module('lithium').controller(
    'ConfirmDeleteStatusReason', ['$uibModalInstance', 'controllerModel', 'closureReasonsRest', 'bsLoadingOverlayService','errors',"notify",
        function ($uibModalInstance, controllerModel, closureReasonsRest, bsLoadingOverlayService, errors, notify) {

            let controller = this;
            controller.model = controllerModel;
            controller.fields = [
                {
                    className: 'col-xs-12',
                    key: 'comment',
                    type: 'textarea',
                    templateOptions: {
                        label: '', description: '', placeholder: '', maxlength: 65535,
                        require: true
                    },
                    expressionProperties: {
                        'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.TEXT.INPUT_DESCRIPTION" | translate'
                    }
                }
            ]
            controller.referenceId = 'deletestatusreason-overlay';

            controller.submit = function () {
                let deleteReason = controller.model.comment;

                if(deleteReason == undefined || deleteReason == null || deleteReason === "") {
                    notify.error('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.DELETE.REASON');
                } else {
                    bsLoadingOverlayService.start({referenceId: controller.referenceId});
                    closureReasonsRest.delete(controller.model.domain.name, controller.model.id, deleteReason).then(function(response) {
                        if (response._status !== 0) {
                            notify.error('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.DELETE.ERROR');
                        } else {
                            $uibModalInstance.close(response);
                        }
                    }).catch(function() {
                        errors.catch('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.DELETE.ERROR', false);
                    }).finally(function() {
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                    });
                }
            }
            controller.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            }
        }]
)