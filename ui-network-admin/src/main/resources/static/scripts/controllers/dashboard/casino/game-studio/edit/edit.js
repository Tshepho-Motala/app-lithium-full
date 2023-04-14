'use strict'

angular.module('lithium').controller('GameStudioEditController', ['gameStudio', 'errors', 'notify', 'GameStudioRest', '$state',
    function(gameStudio, errors, notify, rest, $state) {
        var controller = this;
        controller.model = gameStudio;

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
                    'templateOptions.label': '"UI_NETWORK_ADMIN.GAME-STUDIO.FIELDS.DOMAIN.LABEL" | translate'
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
                    'templateOptions.label': '"UI_NETWORK_ADMIN.GAME-STUDIO.FIELDS.NAME.LABEL" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.GAME-STUDIO.FIELDS.NAME.DESCRIPTION" | translate',
                }
            }
        ]

        controller.delete = function() {
            var model = angular.copy(gameStudio);
            model.deleted = true;

            rest.update(model.domain.name, model.id, model)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.GAME-STUDIO.EDIT.DELETE.SUCCESS');
                        $state.go('dashboard.casino.game-studio.list');
                    }
                }).catch(
                    errors.catch('UI_NETWORK_ADMIN.GAME-STUDIO.EDIT.DELETE.ERROR', false)
                ).finally(function () {
                });
        }

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
                return false;
            }

            rest.update(gameStudio.domain.name, gameStudio.id, controller.model).then(function(response) {
                if (response._successful) {
                    notify.success('UI_NETWORK_ADMIN.GAME-STUDIO.EDIT.SUCCESS');
                    $state.go('dashboard.casino.game-studio.view', {
                        domainName: response.domain.name,
                        id: response.id
                    })
                } else {
                    notify.error(response._message);
                }
            }).catch(function(error) {
                errors.catch('UI_NETWORK_ADMIN.GAME-STUDIO.EDIT.ERROR', false)(error)
            });
        }
    }
]);