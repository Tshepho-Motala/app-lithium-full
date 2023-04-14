'use strict';

angular.module('lithium')
    .controller('SelfExcludePlayerModal', ['user', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', '$filter', 'ExclusionRest',
        function (user, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, $filter, exclusionRest) {
            var controller = this;

            controller.user = user;

            controller.options = {};
            controller.model = {};

            controller.fields = [
                {
                    key: "permanent",
                    type: "checkbox",
                    templateOptions: {
                        label: "Permanent",
                        description: 'Exclude player from transacting permanently'
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.SELFEXCLUSION.FIELDS.PERMANENT.NAME" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.SELFEXCLUSION.FIELDS.PERMANENT.NAME" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.SELFEXCLUSION.FIELDS.PERMANENT.DESCRIPTION" | translate'
                    }
                }, {
                    className: "col-xs-12",
                    key: 'periodInMonths',
                    type: 'ui-select-single',
                    hideExpression: function($viewValue, $modelValue, scope) {
                        return (scope.model.permanent !== undefined && scope.model.permanent !== null && scope.model.permanent === true);
                    },
                    templateOptions : {
                        label: "Period (in months)",
                        description: "",
                        valueProp: 'value',
                        labelProp: 'label',
                        optionsAttr: 'ui-options', ngOptions: 'ui-options',
                        options: [],
                        required: false
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.EXCLUSION.FIELDS.PERIODINMONTHS" | translate'
                    },
                    controller: ['$scope', function($scope) {
                        exclusionRest.optionsInMonths(user.domain.name).then(function(response) {
                            var periodsInMonths = [];
                            var opts = response.plain();
                            for (var i = 0; i < opts.length; i++) {
                                var opt = opts[i];
                                periodsInMonths.push({label: opt, value: opt});
                            }
                            $scope.to.options = periodsInMonths;
                        });
                    }]
                },

            ];

            controller.referenceId = 'changeexclusion-overlay';
            controller.submit = function() {
                bsLoadingOverlayService.start({referenceId:controller.referenceId});
                if (controller.form.$invalid) {
                    angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                    bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    return false;
                }

                if (controller.model.permanent === undefined || controller.model.permanent === null || !controller.model.permanent) {
                    if (controller.model.periodInMonths === undefined || controller.model.periodInMonths === null) {
                        notify.warning("Either check the permanent box, or choose an exclusion period.");
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        return false;
                    }
                }

                var isPermanent = (controller.model.permanent !== undefined && controller.model.permanent !== null && controller.model.permanent === true);

                exclusionRest.set(user.guid, (isPermanent) ? null: controller.model.periodInMonths, user.domain.name).then(function(response) {
                    if (response._status === 0) {
                        notify.success('UI_NETWORK_ADMIN.PLAYER.EXCLUSION.EXCLUDE.SUCCESS');
                        $uibModalInstance.close(response.plain());
                    } else {
                        notify.error('UI_NETWORK_ADMIN.PLAYER.EXCLUSION.EXCLUDE.ERROR');
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
