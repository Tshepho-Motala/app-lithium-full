'use strict';

angular.module('lithium')
    .controller('RealityCheckPlayerModal', ['user', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', '$filter', 'RealityCheckRest',
            function (user, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, $filter, realityCheckRest) {
                var controller = this;

                controller.user = user;

                controller.options = {};
                controller.model = {};

                controller.fields = [
                    {
                        key: 'newRealityCheckTime',
                        type: 'ui-select-single',
                        templateOptions : {
                            label: "Period (in minutes)",
                            description: "",
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [],
                            required: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.REALITYCHECK.FIELDS.PERIOD_IN_MINS" | translate'
                        },
                        controller: ['$scope', function($scope) {
                            realityCheckRest.optionsInMillis(user.domain.name).then(function(response) {
                                var periodsInMillis = [];
                                var opts = response.plain();
                                for (var i = 0; i < opts.length; i++) {
                                    var opt = opts[i];
                                    periodsInMillis.push({label: opt, value: opt});
                                }
                                $scope.to.options = periodsInMillis;
                            });
                        }]
                    }
                ];

                controller.referenceId = 'realitycheck-overlay';
                controller.submit = function() {
                    bsLoadingOverlayService.start({referenceId:controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        return false;
                    }

                    realityCheckRest.set(user.guid, controller.model.newRealityCheckTime, user.domain.name).then(function(response) {
                        if (response._status === 0) {
                            notify.success('Successfully set reality check time');
                            $uibModalInstance.close(response.plain());
                        } else {
                            notify.error('Failed to set reality check time');
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
