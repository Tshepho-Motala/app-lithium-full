'use strict'

angular.module('lithium').controller('GameSuppliersEditController', ['gameSupplier', 'errors', 'notify', 'GameSuppliersRest', '$state',
    function(gameSupplier, errors, notify, rest, $state) {
        var controller = this;
        controller.model = gameSupplier;

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

        controller.delete = function() {
            var model = angular.copy(gameSupplier);
            model.deleted = true;

            rest.update(model.domain.name, model.id, model)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.GAME-SUPPLIERS.EDIT.DELETE.SUCCESS');
                        $state.go('dashboard.casino.game-suppliers.list');
                    }
                }).catch(
                    errors.catch('UI_NETWORK_ADMIN.GAME-SUPPLIERS.EDIT.DELETE.ERROR', false)
                ).finally(function () {
                });
        }

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
                return false;
            }

            rest.update(gameSupplier.domain.name, gameSupplier.id, controller.model).then(function(response) {
                if (response._successful) {
                    notify.success('UI_NETWORK_ADMIN.GAME-SUPPLIERS.EDIT.SUCCESS');
                    $state.go('dashboard.casino.game-suppliers.view', {
                        domainName: response.domain.name,
                        id: response.id
                    })
                } else {
                    notify.error(response._message);
                }
            }).catch(function(error) {
                errors.catch('UI_NETWORK_ADMIN.GAME-SUPPLIERS.EDIT.ERROR', false)(error)
            });
        }
    }
]);