'use strict';

angular.module('lithium')
.controller('CasinoLobbiesAddController', ['domainName', '$scope', '$stateParams', '$state', 'errors', 'notify', 'CasinoCMSRest',
    function(domainName, $scope, $stateParams, $state, errors, notify, rest) {
        var controller = this;
        $scope.setDescription('UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.TITLE');

        controller.model = {};

        controller.fields = [
            {
                key: 'description',
                type: 'textarea',
                templateOptions: {
                    required: true,
                    cols: 5,
                    rows: 5,
                    maxlength: 2000
                },
                optionsTypes: ['editable'],
                modelOptions: {
                    updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.DESCRIPTION.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
                }
            }, {
                key: 'json',
                type: 'json-editor',
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.JSON.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.JSON.DESCRIPTION" | translate'
                },
                templateOptions: {
                    required: true,
                    height: '400px'
                }
            }
        ]

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
                return false;
            }

            try {
                JSON.parse(controller.model.json);
            } catch (e) {
                notify.warning('UI_NETWORK_ADMIN.CASINO.LOBBIES.INVALID_JSON');
                return false;
            }

            rest.add(domainName, controller.model).then(function(response) {
                notify.success('UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.SUCCESS');
                $state.go('dashboard.casino.lobbies.lobby.view', {domainName: response.domain.name, lobbyId: response.id, lobbyRevisionId: response.current.id})
            }).catch(function(error) {
                errors.catch('UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.ERROR', false)(error)
            });
        }
    }
]);
