'use strict';

angular.module('lithium')
    .controller('CooloffPlayerModal', ['user', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', '$filter', 'CoolOffRest',
        function (user, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, $filter, coolOffRest) {
            var controller = this;

            controller.user = user;

            controller.options = {};
            controller.model = {};

            controller.fields = [
                {
                    key: 'periodInDays',
                    type: 'ui-select-single',
                    templateOptions : {
                        label: "Period (in days)",
                        description: "",
                        valueProp: 'value',
                        labelProp: 'label',
                        optionsAttr: 'ui-options', ngOptions: 'ui-options',
                        options: [],
                        required: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.COOLOFF.FIELDS.PERIODINDAYS" | translate'
                    },
                    controller: ['$scope', function($scope) {
                        coolOffRest.optionsInDays(user.domain.name).then(function(response) {
                            var periodsInDays = [];
                            var opts = response.plain();
                            for (var i = 0; i < opts.length; i++) {
                                var opt = opts[i];
                                periodsInDays.push({label: opt, value: opt});
                            }
                            $scope.to.options = periodsInDays;
                        });
                    }]
                }
            ];

            controller.referenceId = 'cooloff-overlay';
            controller.submit = function() {
                bsLoadingOverlayService.start({referenceId:controller.referenceId});
                if (controller.form.$invalid) {
                    angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    return false;
                }

                coolOffRest.set(user.guid, controller.model.periodInDays, user.domain.name).then(function(response) {
                    if (response._status === 0) {
                        notify.success('Successfully cooled off player');
                        $uibModalInstance.close(response.plain());
                    } else {
                        notify.error('Failed to cool off player');
                    }
                }).catch(function() {
                    errors.catch('', false);
                }).finally(function() {
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                });
            };

            controller.cancel = function() {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]
);
