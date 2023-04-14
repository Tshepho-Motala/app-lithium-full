'use strict';

angular.module('lithium').controller('AutoWithdrawalRulesetDelayModal', ["ruleset", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "AutoWithdrawalRulesetRest",
    function (ruleset, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest) {
        var controller = this;

        controller.model = {delay: ruleset.delay, delayedStart: ruleset.delayedStart};


        controller.setupFields = function () {
            controller.fields = [
                {
                    className: 'top-space-15 col-xs-12 col-md-6',
                    type: 'checkbox2',
                    key: 'delayedStart',
                    templateOptions: {
                        label: 'delayedStart',
                        fontWeight: 'bold',
                        description: 'Should this ruleset be delay?',
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DELAYED_START.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.ENABLED.DESCRIPTION" | translate'
                    }
                }
            ];

            if (controller.model.delayedStart === true) {
                controller.fields.push(
                {
                    className: 'col-xs-12',
                    key: "delay",
                    type: 'ui-number-mask',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Delay',
                        description: "The withdraw processing delay in ms after approve.",
                        decimals: 0,
                        hidesep: true,
                        neg: false,
                        min: '0',
                        max: '',
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DELAY.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.FIELDS.DELAY.DESCRIPTION" | translate'
                    }
                });
            } else {
                controller.model.delay = null;
            }
        }

        controller.setupFields();

        $scope.$watch('[controller.model.delayedStart]', function (newValue, oldValue) {
            if (newValue != oldValue) {
                controller.setupFields();
            }
        });

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[delay='" + controller.form.$delay + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.autoWithdrawalRulesetChangeDelay(ruleset.domain.name, ruleset.id, controller.model.delay, controller.model.delayedStart)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.CHANGEDELAY.SUCCESS");
                        $uibModalInstance.close(response);
                    }
                }).catch(
                errors.catch("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.CHANGEDELAY.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);
