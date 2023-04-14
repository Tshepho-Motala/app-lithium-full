'use strict';
angular.module('lithium').controller('ConfirmDeleteEcosystemRelationship', [ '$uibModalInstance', 'selectedId',
    'selectedEcosystemController', 'relationShipController', 'referenceId','errors', 'EcosysRest', 'bsLoadingOverlayService',
        function ($uibModalInstance , selectedId, selectedEcosystemController, relationShipController, referenceId, errors, EcosysRest
        ,bsLoadingOverlayService) {

            let controller = this;
            controller.referenceId = referenceId;

            controller.submit = function() {
                bsLoadingOverlayService.start({ referenceId: controller.referenceId });

                EcosysRest.remove(selectedId).then(function (response) {
                    relationShipController.list(selectedEcosystemController);
                    $uibModalInstance.close(response);
                }).catch(
                    errors.catch("UI_NETWORK_ADMIN.ECOSYSTEM.MESSAGES.ERROR_DOMAIN", false)
                ).finally(function () {
                    bsLoadingOverlayService.stop({ referenceId: controller.referenceId});
                });
            }

            controller.cancel = function () {
                $uibModalInstance.dismiss('cancel')
            }
        }]
);