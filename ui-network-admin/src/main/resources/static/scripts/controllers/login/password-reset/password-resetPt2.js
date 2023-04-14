'use strict';

angular.module('lithium')
    .controller('PasswordPt2Controller', ["$scope", "$state", "$rootScope", "$userService", "$sce","$uibModalInstance","UserRest",'notify', 'errors','domainName','email', 'username',
        function($scope, $state, $rootScope, $userService, $sce,$uibModalInstance,userRest,notify,errors,domainName,email,username) {

        var controller = this;
        controller.token = "";
        controller.password = "";
        controller.confirmPassword = "";
        controller.domain = domainName;
        controller.emailAddress = email;
        controller.username = username;

        controller.resetPasswordPt2 = function() {
            userRest.passwordResetPt2( controller.domain,controller.emailAddress,controller.token, controller.password, controller.username).then(function(response) {
                if (response._status == 0) {
                    notify.success('UI_NETWORK_ADMIN.PASSWORDRESET.PART2.SUCCESS');
                    $uibModalInstance.close(response);
                } else {
                    notify.warning(response._message);
                    controller.cancel();
                }
            }).catch(function(error) {
                notify.error('UI_NETWORK_ADMIN.PASSWORDRESET.PART2.ERROR');
                errors.catch('', false)(error)
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }]);
