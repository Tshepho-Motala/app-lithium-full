'use strict';

angular.module('lithium').controller('RestrictionsRestrictionModal', ["set", "restriction", "restrictionTypes", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "RestrictionsRest",
    function (set, restriction, restrictionTypes, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest) {
        var controller = this;

        console.debug("restriction", restriction);

        controller.model = restriction;

        controller.fields = [
            {
                className: 'col-xs-12',
                key : "restriction.code",
                type : "ui-select-single",
                templateOptions : {
                    label : "Restriction",
                    description : "Select the restriction to be applied",
                    placeholder : 'Select Restriction',
                    required : true,
                    optionsAttr: 'bs-options',
                    valueProp : 'code',
                    labelProp : 'name',
                    optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                    options: []
                },
                controller: ['$scope', function($scope) {
                    $scope.to.options = restrictionTypes;
                }],
                expressionProperties: {
                    'templateOptions.disabled': function(viewValue, modelValue, scope) {
                        return (restriction !== null);
                    },
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.RESTRICTION.RESTRICTION.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.RESTRICTION.RESTRICTION.DESCRIPTION" | translate'
                }
            }, {
                className: 'col-xs-12 col-md-6',
                type: 'checkbox2',
                key: 'enabled',
                templateOptions: {
                    label: 'Enabled',
                    fontWeight:'bold',
                    description: 'Enable this restriction in the set?',
                    required: true
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.RESTRICTION.ENABLED.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.RESTRICTION.ENABLED.DESCRIPTION" | translate'
                }
            }
        ];

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            if (set !== undefined && set !== null) {
                // We're editing
                if (restriction !== undefined && restriction !== null) {
                    // Modifying an existing restriction
                    bsLoadingOverlayService.start({referenceId: "loading"});
                    rest.domainRestrictionSetRestrictionUpdate(set.id, restriction.id, controller.model)
                        .then(function (response) {
                            if (response._status !== 0) {
                                notify.error(response._message);
                            } else {
                                notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.RESTRICTION.UPDATE.SUCCESS");
                                $uibModalInstance.close(response);
                            }
                        }).catch(
                        errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.RESTRICTION.UPDATE.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: "loading"});
                    });
                } else {
                    // Adding a new restriction
                    rest.domainRestrictionSetRestrictionAdd(set.id, controller.model)
                        .then(function (response) {
                            if (response._status !== 0) {
                                notify.error(response._message);
                            } else {
                                notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.RESTRICTION.UPDATE.SUCCESS");
                                $uibModalInstance.close(response);
                            }
                        }).catch(
                        errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.RESTRICTION.UPDATE.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: "loading"});
                    });
                }
            } else {
                // We're adding
                $uibModalInstance.close(controller.model);
            }
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);