'use strict';

angular.module('lithium')
.controller('GameTypesAddController', ['domainName', '$scope', '$stateParams', '$state', 'errors', 'notify', 'GameTypesRest',
    function(domainName, $scope, $stateParams, $state, errors, notify, rest) {
        var controller = this;
        $scope.setDescription('UI_NETWORK_ADMIN.GAME-TYPES.ADD.TITLE');

        controller.model = {domain: {name: domainName}};

        controller.fields = [
            {
                className: 'col-xs-12 col-md-6',
                key: "domain.name",
                type: "readonly-input",
                templateOptions: {
                    label: "",
                    description: "",
                    required: true,

                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.GAME-TYPES.FIELDS.DOMAIN.LABEL" | translate'
                }
            }, {
                className: 'col-xs-12 col-md-6',
                key: "name",
                type: "input",
                templateOptions: {
                    label: "",
                    description: "",
                    required: true,

                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.GAME-TYPES.FIELDS.NAME.LABEL" | translate',
                }
            },
            {
                "className": "col-xs-12 col-md-6",
                "type": "ui-select-single",
                "key": "type",
                "templateOptions": {
                    "label": '',
                    "optionsAttr": 'bs-options',
                    "description": "",
                    "valueProp": 'value',
                    "labelProp": 'value',
                    "placeholder": '',
                    "ngOptions": 'ui-options',
                    "options": [],
                    "required": true
                },
                controller: ['$scope', function ($scope) {
                    $scope.to.options = [
                        {value: "primary"},
                        {value: "secondary"}
                    ]
                }],
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.GAME-TYPES.FIELDS.TYPE.LABEL" | translate',
                }
            }
        ]

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
                return false;
            }

            rest.add(domainName, controller.model).then(function(response) {
                if (response._successful) {
                    notify.success('UI_NETWORK_ADMIN.GAME-TYPES.ADD.SUCCESS');
                    $state.go('dashboard.casino.game-types.view', {
                        domainName: response.domain.name,
                        id: response.id
                    })
                } else {
                    notify.error(response._message);
                }
            }).catch(function(error) {
                errors.catch('UI_NETWORK_ADMIN.GAME-TYPES.ADD.ERROR', false)(error)
            });
        }
    }
]);
