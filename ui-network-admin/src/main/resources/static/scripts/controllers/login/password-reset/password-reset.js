'use strict';

angular.module('lithium')
    .controller('PasswordController', ["$scope", "$state", "$rootScope", "$userService", "$sce","$uibModalInstance","UserRest",'notify', 'errors','$uibModal', function($scope, $state, $rootScope, $userService, $sce,$uibModalInstance,userRest,notify,errors,$uibModal) {
        var service = {};
        var controller = this;
        controller.enterToken = false;
        controller.domainName = "";
        controller.token = "";
        controller.password = "";
        controller.email = "";
        controller.username = "";
        controller.resetPassword = function(){
            if(!controller.email.toString().contains('@')) {
                controller.username = controller.email;
            }
            userRest.passwordReset(controller.domainName, controller.email, controller.username).then(function(response) {
                if (response._status == 0) {
                    notify.success('UI_NETWORK_ADMIN.PASSWORDRESET.PART1.SUCCESS');
                    self.resetPasswordPt2Route();
                    controller.cancel();
                } else if(response._status == 409) {
                    notify.warning('UI_NETWORK_ADMIN.PASSWORDRESET.PART1.ERROR409');
                } else {
                    notify.warning(response._message);
                }
            }).catch(function(error) {
                notify.error('UI_NETWORK_ADMIN.PASSWORDRESET.PART1.ERROR');
                errors.catch('', false)(error)
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };


        service.getDomain = function () {
            return controller.domainName;
        }

        service.getEmail = function () {
            return controller.email;
        }

        service.getUsername = function () {
            return controller.username;
        }

        self.resetPasswordPt2Route = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                backdrop: 'static',
                templateUrl: 'scripts/controllers/login/password-reset/password-resetPt2.html',
                controller: 'PasswordPt2Controller',
                controllerAs: 'controller',
                size: 'md',
                resolve: {
                    domainName: function() {
                        return controller.domainName;
                    },
                    email: function() {
                        return controller.email;
                    },
                    username: function() {
                        return controller.username;
                    }
                }
            });

            modalInstance.result.then(function() {
                $state.go('login.password-reset.password-resetPt2',{domainName: controller.domainName, email: controller.email, username: controller.username}, {reload: true});
            });
        }
    }]);
