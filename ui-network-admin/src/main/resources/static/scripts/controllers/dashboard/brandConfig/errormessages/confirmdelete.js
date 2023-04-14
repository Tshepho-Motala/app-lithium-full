'use strict';

angular.module('lithium')
    .controller('ConfirmErrorMessageNoteDeleteModal',
        ['$uibModalInstance', 'entityId', 'rest-translate', 'notify', 'errors', 'bsLoadingOverlayService','domainSelected',
            function ($uibModalInstance, entityId, translateRest, notify, errors, bsLoadingOverlayService, domainSelected) {
                var controller = this;

                console.log("Managed to open the controller");
                controller.referenceId = 'addcomment-overlay';
                controller.submit = function() {
                    bsLoadingOverlayService.start({referenceId:controller.referenceId});
                    translateRest.deleteTranslationKeyAndValues(domainSelected, entityId).then(function(response) {
                        notify.success('UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.DELETE.SUCCESS');
                        $uibModalInstance.close(response);
                    }).catch(
                        errors.catch('UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.DELETE.ERROR', false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    });
                };

                controller.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            }]);
