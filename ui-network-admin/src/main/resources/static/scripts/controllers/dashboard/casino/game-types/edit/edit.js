'use strict'

angular.module('lithium').controller('GameTypesEditController', ['gameType', 'errors', 'notify', 'GameTypesRest', '$state',
    function(gameType, errors, notify, rest, $state) {
        var controller = this;
        controller.model = gameType;

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
                    'templateOptions.description': '"UI_NETWORK_ADMIN.GAME-TYPES.FIELDS.NAME.DESCRIPTION" | translate',
                }
            },
            {
                "className": "col-xs-12 col-md-6",
                "type": "ui-select-single",
                "key": "type",
                "templateOptions": {
                    "label": '',
                    "required": true,
                    "optionsAttr": 'bs-options',
                    "description": "",
                    "valueProp": 'value',
                    "labelProp": 'value',
                    "placeholder": '',
                    "ngOptions": 'ui-options',
                    "options": []
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

        controller.delete = function() {
            var model = angular.copy(gameType);
            model.deleted = true;

            rest.delete(model.domain.name, model.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.GAME-TYPES.EDIT.DELETE.SUCCESS');
                        $state.go('dashboard.casino.game-types.list');
                    }
                }).catch(
                    errors.catch('UI_NETWORK_ADMIN.GAME-TYPES.EDIT.DELETE.ERROR', false)
                ).finally(function () {
                });
        }

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
                return false;
            }

            rest.update(gameType.domain.name, gameType.id, controller.model).then(function(response) {
                if (response._successful) {
                    notify.success('UI_NETWORK_ADMIN.GAME-TYPES.EDIT.SUCCESS');
                    $state.go('dashboard.casino.game-types.view', {
                        domainName: response.domain.name,
                        id: response.id
                    })
                } else {
                    notify.error(response._message);
                }
            }).catch(function(error) {
                errors.catch('UI_NETWORK_ADMIN.GAME-TYPES.EDIT.ERROR', false)(error)
            });
        }
    }
]);