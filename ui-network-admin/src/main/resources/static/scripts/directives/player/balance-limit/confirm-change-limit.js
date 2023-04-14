'use strict';

angular.module('lithium')
    .controller('ConfirmBalanceLimitUpdateModal',
        ['$uibModalInstance','userData','domain', 'limit', 'currentLimit','bsLoadingOverlayService','userLimitsRest','errors', 'notify','changeBalanceLimitModalInstance',
            function ($uibModalInstance, userData, domain, limit, currentLimit, bsLoadingOverlayService, userLimitsRest, errors, notify, changeBalanceLimitModalInstance) {
                var controller = this;
               
                controller.referenceId = 'confirm-change-balance-limit-overlay';

                controller.newValue = limit;
                controller.oldValue = currentLimit;
                controller.domain = domain;
    
                controller.submit = function() {
                    bsLoadingOverlayService.start({referenceId:controller.referenceId});

                    userLimitsRest.balanceLimitSave(userData.domain.name, userData.guid, limit).then(function(response) {
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        if (response._successful) {
                            notify.success('UI_NETWORK_ADMIN.BALANCE_LIMITS.SAVE.SUCCESS');
                            $uibModalInstance.close(response);
                            changeBalanceLimitModalInstance.close(response);
                        } else {
                            notify.error(response._message);
                            notify.error('UI_NETWORK_ADMIN.BALANCE_LIMITS.SAVE.ERROR');
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
