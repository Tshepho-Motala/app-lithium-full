'use strict';

angular.module('lithium').controller('AutoRestrictionRulesetNameModal', ["ruleset", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "AutoRestrictionRulesetRest",
    function (ruleset, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest) {
        var controller = this;

        controller.model = {name: ruleset.name};

        controller.fields = [
            {
                className: 'col-xs-12',
                key: "name",
                type: "input",
                templateOptions: {
                    label: "Name",
                    description: "A unique name for the ruleset",
                    required: true
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.NAME.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.NAME.DESCRIPTION" | translate'
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
            rest.autoRestrictionRulesetChangeName(ruleset.domain.name, ruleset.id, controller.model.name)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.CHANGENAME.SUCCESS");
                        $uibModalInstance.close(response);
                    }
                }).catch(
                errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.CHANGENAME.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);