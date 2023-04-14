'use strict';

angular.module('lithium').controller('AutoRestrictionRulesetOutcomeModal', ["ruleset", "outcomes", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "AutoRestrictionRulesetRest",
    function (ruleset, outcomes, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest) {
        var controller = this;

        controller.model = {outcome: ruleset.outcome};

        controller.fields = [
            {
                className: 'col-xs-12',
                key: "outcome",
                type: "ui-select-single",
                templateOptions: {
                    label: "Outcome",
                    description: "Choose the outcome of the ruleset",
                    required: true,
                    optionsAttr: 'bs-options',
                    valueProp: 'id',
                    labelProp: 'displayName',
                    optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                    placeholder: '',
                    options: []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.OUTCOME.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.OUTCOME.DESCRIPTION" | translate'
                },
                controller: ['$scope', function($scope) {
                    $scope.to.options = outcomes;
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
            rest.autoRestrictionRulesetChangeOutcome(ruleset.domain.name, ruleset.id, controller.model.outcome)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.CHANGEOUTCOME.SUCCESS");
                        $uibModalInstance.close(response);
                    }
                }).catch(
                errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.CHANGEOUTCOME.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);