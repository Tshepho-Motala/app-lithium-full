'use strict';

angular.module('lithium')
    .controller('ChangeStatusVerificationModal', ['$uibModalInstance', 'user', "UserRest", 'userFields', 'notify',
        function ($uibModalInstance, user, UserRest, userFields, notify) {
            var vm = this;
            vm.submitCalled = false;
            vm.options = {removeChromeAutoComplete: true};
            vm.model = user;

            vm.fields = [
                userFields.verificationStatus,
                {
                    className: "col-xs-12",
                    key: "comment",
                    type: "textarea",
                    templateOptions: {
                        label: "", description: "", placeholder: "",
                        required: true, minlength: 5, maxlength: 65535
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION" | translate'
                    }
                }
            ];
            vm.submit = function () {
                vm.submitCalled = true;
                if (vm.form.$invalid) {
                    angular.element("[name='" + vm.form.$name + "']").find('.ng-invalid:visible:first').focus();
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                    return false;
                }
                var statusUpdate = {
                    userId: user.id,
                    statusId: vm.model.verificationStatus,
                    comment: vm.model.comment
                }
                UserRest.saveVerificationStatus(user.domain.name, statusUpdate).then(function (response) {
                    $uibModalInstance.close(response);
                }, function (status) {
                    notify.error("Unable to save. Please try again.");
                });
            };

            vm.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
        }]);