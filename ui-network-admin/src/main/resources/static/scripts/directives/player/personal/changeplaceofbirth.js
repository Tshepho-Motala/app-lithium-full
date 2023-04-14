'use strict';

angular.module('lithium').controller('EditPlaceOfBirthModal', ['$uibModalInstance','user',
    "UserRest", 'userFields', 'notify', 'errors', 'bsLoadingOverlayService', 'domainSettings',

    function ($uibModalInstance, user, UserRest, userFields, notify, errors, bsLoadingOverlayService, domainSettings) {

        const controller = this;
        controller.referenceId = 'editPlaceOfBirth-overlay'
        controller.submitCalled = false;
        controller.options = {removeChromeAutoComplete : true};

        controller.model = {};
        controller.model = user;

        controller.fields = [
            userFields.placeOfBirthInput('placeOfBirth', domainSettings)
        ]

        controller.submit = function() {
            bsLoadingOverlayService.start({referenceId:controller.referenceId});

            UserRest.updateUserPlaceOfBirth(user.domain.name, user).then(function(response) {
                $uibModalInstance.close(response);
            }).catch(
                errors.catch('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.UPDATE.ERROR', false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId:controller.referenceId});
            });

        }

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);
