'use strict';

angular.module('lithium').controller('RestrictionSetNameModal', ["set", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "RestrictionsRest",
    function (set, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest) {
        var controller = this;

        controller.model = {name: set.name};

        controller.fields = [
            {
                className: 'col-xs-12 col-md-6',
                key: "name",
                type: "input",
                templateOptions: {
                    label: "Name",
                    description: "A unique name for the restriction",
                    required: true
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.NAME.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.NAME.DESCRIPTION" | translate'
                }
            }
        ];

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.domainRestrictionSetChangeName(set.id, controller.model.name)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.CHANGENAME.SUCCESS");
                        $uibModalInstance.close(response);
                    }
                }).catch(
                errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.CHANGENAME.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);