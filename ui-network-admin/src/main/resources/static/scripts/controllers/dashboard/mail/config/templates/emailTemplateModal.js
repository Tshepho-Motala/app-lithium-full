'use strict';

angular.module('lithium').controller('emailTemplateModal', ['$uibModalInstance', 'notify', 'formData', 'EmailTemplateRest','$window',
    function ($uibModalInstance, notify,formData, rest,$window) {
        let controller = this;

        controller.onSubmit = function () {
            console.log(1, formData);
            rest.save(formData).then(function(response) {
                notify.success("UI_NETWORK_ADMIN.MAIL.TEMPLATES.SUCCESS.SAVE");
                $window.location.reload();
                $uibModalInstance.dismiss('cancel');
                $state.go("^.view", { domainName:response.domain.name, id:response.id });
            });
        };

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]
);