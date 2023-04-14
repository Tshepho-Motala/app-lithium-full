'use strict';

angular.module('lithium')
    .controller('ConfirmDepositUpdateModal',
        ['$uibModalInstance','userData', 'limit', 'limitUsed','bsLoadingOverlayService','userLimitsRest','errors', 'notify','granularity','modifyuiModalInstance',
            function ($uibModalInstance, userData, limit, limitUsed, bsLoadingOverlayService, userLimitsRest, errors, notify, granularity, modifyuiModalInstance) {
                var controller = this;
               
                controller.referenceId = 'confirmchangelimit-overlay';

                controller.newValue = limit;
                controller.oldValue = limitUsed;    
    
                controller.submit = function() {
                    bsLoadingOverlayService.start({referenceId:controller.referenceId});

                    userLimitsRest.depositLimitSave(userData.guid, granularity,limit).then(function(response) {
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        if (response._successful) {
                            notify.success('UI_NETWORK_ADMIN.DEPOSITLIMITS.SAVE.SUCCESS');
                            $uibModalInstance.close();
                            modifyuiModalInstance.close(response);
                        } else {
                            notify.error(response._message);
                            notify.error('UI_NETWORK_ADMIN.DEPOSITLIMITS.SAVE.ERROR');
                            $uibModalInstance.close();
                        }
                    }).catch(function(error) {
                        console.log(error);
                        errors.catch('', false);
                    });
                };
                
                controller.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            }]);
