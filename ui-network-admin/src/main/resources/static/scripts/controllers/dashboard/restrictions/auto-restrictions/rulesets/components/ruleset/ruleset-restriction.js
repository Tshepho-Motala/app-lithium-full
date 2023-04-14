'use strict';

angular.module('lithium').controller('AutoRestrictionRulesetRestrictionModal', ["ruleset", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "RestrictionsRest", "AutoRestrictionRulesetRest",
    function (ruleset, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, restrictionsRest, rest) {
        var controller = this;

        controller.model = {restrictionSet: {id: ruleset.restrictionSet.id} };

        controller.fields = [
            {
                className: 'col-xs-12',
                key: "restrictionSet.id",
                type: "ui-select-single",
                templateOptions: {
                    label: "Restriction",
                    description: "Choose the restriction for the auto-restriction outcome",
                    required: true,
                    optionsAttr: 'bs-options',
                    valueProp: 'id',
                    labelProp: 'name',
                    optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                    placeholder: '',
                    options: []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.RESTRICTION.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.RESTRICTION.DESCRIPTION" | translate'
                },
                controller: ['$scope', function($scope) {
                    restrictionsRest.domainRestrictionSets(ruleset.domain.name).then(function (response) {
                       $scope.to.options = response.plain();
                    });
                }]
            }
        ];

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoRestrictionRulesetChangeRestriction(ruleset.domain.name, ruleset.id, controller.model.restrictionSet.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.CHANGERESTRICTION.SUCCESS");
                        $uibModalInstance.close(response);
                    }
                }).catch(
                errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.CHANGERESTRICTION.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);