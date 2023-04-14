'use strict';

angular.module('lithium')
.controller('GameSuppliersAddController', ['domainName', '$scope', '$stateParams', '$state', 'errors', 'notify', 'GameSuppliersRest',
    function(domainName, $scope, $stateParams, $state, errors, notify, rest) {
        var controller = this;
        $scope.setDescription('UI_NETWORK_ADMIN.GAME-SUPPLIERS.ADD.TITLE');

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
                    'templateOptions.label': '"UI_NETWORK_ADMIN.GAME-SUPPLIERS.FIELDS.DOMAIN.LABEL" | translate'
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
                    'templateOptions.label': '"UI_NETWORK_ADMIN.GAME-SUPPLIERS.FIELDS.NAME.LABEL" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.GAME-SUPPLIERS.FIELDS.NAME.DESCRIPTION" | translate',
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
                    notify.success('UI_NETWORK_ADMIN.GAME-SUPPLIERS.ADD.SUCCESS');
                    $state.go('dashboard.casino.game-suppliers.view', {
                        domainName: response.domain.name,
                        id: response.id
                    })
                } else {
                    notify.error(response._message);
                }
            }).catch(function(error) {
                errors.catch('UI_NETWORK_ADMIN.GAME-SUPPLIERS.ADD.ERROR', false)(error)
            });
        }
    }
]);
