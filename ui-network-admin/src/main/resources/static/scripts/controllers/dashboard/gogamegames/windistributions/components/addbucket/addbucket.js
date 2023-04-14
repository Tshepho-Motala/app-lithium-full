'use strict';

angular.module('lithium').controller('GoGameWinDistributionsAddBucketController', ['comparators', "bucket", "errors", "$scope", "notify", "$uibModalInstance", 'GoGameGamesRest',
    function (comparators, bucket, errors, $scope, notify, $uibModalInstance, gogameGamesRest) {
        var controller = this;

        controller.model = {};
        controller.adding = true;
        if (bucket !== undefined && bucket !== null) {
            controller.model = bucket;
            controller.adding = false;
        }
        controller.fields = [
            {
                key: 'comparatorId',
                type: 'ui-select-single',
                templateOptions: {
                    label: 'Comparison Operator',
                    required: true,
                    description: "",
                    valueProp: 'id',
                    labelProp: 'operator',
                    optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                    placeholder: '',
                    options: comparators
                }
            }, {
                key: 'multiplier',
                type: 'ui-number-mask',
                optionsTypes: ['editable'],
                templateOptions: {
                    label: 'Bet/Total Play Multiplier',
                    description: "",
                    decimals: 0,
                    hidesep: true,
                    neg: true,
                    min: '-1',
                    max: '',
                    required: true
                }
            }
        ];

        controller.save = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            $uibModalInstance.close(controller.model);
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }]);