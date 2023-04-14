'use strict';

angular.module('lithium')
.controller('ecosystemAdd', ["ecosystems", "notify", "$q", "$scope", "$state", "EcosysRest", "errors", "bsLoadingOverlayService",
function(ecosystems, notify, $q, $scope, $state, EcosysRest, errors, bsLoadingOverlayService ) {
    var controller = this;
    // $scope.title = '"UI_NETWORK_ADMIN.ECOSYSTEMS.TITLE.TEXT" | translate';
    // $scope.description = '"UI_NETWORK_ADMIN.ECOSYSTEMS.SUBTEXT" | translate';

    controller.model = {
        enabled: true,
        deleted: false
    };
    controller.options = {};
    controller.fields = [{
        className: "row v-reset-row ",
        fieldGroup: [
            {

                templateOptions: {
                    label: "", description: "", placeholder: "",
                    required: true,
                    minlength: 2, maxlength: 35,
                    //focus: true, --this causes issues when cancelling form
                    onKeydown: function(value, options) {
                        options.validation.show = false;
                    },
                    onBlur: function(value, options) {
                        options.validation.show = true;
                    }
                },
                modelOptions: {
                    updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
                },
                "className": "col-xs-12",
                "key": "name",
                "type": "input",
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_NAME" | translate',
                },
                validators: {
                    pattern: {
                        expression: function($viewValue, $modelValue, scope) {
                            return /^[0-9a-z_\\.]+$/.test($viewValue);
                        },
                        message: '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.PATTERN" | translate'
                    }
                },
            },{
                "className" : "col-xs-12",
                "key" : "displayName",
                "type" : "input",
                "templateOptions" : {
                    "label" : "Display Name",
                    "description" : "",
                    "placeholder" : 'Eg. The Famous Brand',
                    "required" : true,
                    "options" : []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DISPLAY_NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DISPLAY_NAME" | translate',
                }
            },{
                "className" : "col-xs-12",
                "key" : "description",
                "type" : "input",
                "templateOptions" : {
                    "label" : "", "description" : "", "placeholder" : "",
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DESCRIPTION" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DESCRIPTION" | translate',
                },
            },{
                "className" : "col-xs-12",
                "type" : 'checkbox',
                "key" : 'enabled',
                "templateOptions" : {
                    "label": 'Enabled',
                    "type": 'hidden'
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.ENABLED.LABEL" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.ENABLED.DESCRIPTION" | translate'
                }
            }
        ]
    }];

    controller.referenceId = 'ecosystem-add-overlay';
    controller.onSubmit = function() {
        bsLoadingOverlayService.start({referenceId:controller.referenceId});
        if (controller.form.$invalid) {
            angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
            notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
            bsLoadingOverlayService.stop({referenceId:controller.referenceId});
            return false;
        }
		EcosysRest.addModifyEcosystems(controller.model).then(function() {
            notify.success("UI_NETWORK_ADMIN.ECOSYSTEMS.SUCCESS_MESSAGE");
            $state.go("dashboard.ecosystems.list");
		}).catch(
            errors.catch("UI_NETWORK_ADMIN.DOMAIN.ADD.FAIL", false)
        ).finally(function () {
            bsLoadingOverlayService.stop({referenceId:controller.referenceId});
        });
    }
    controller.onCancel = function() {
        $state.go("dashboard.ecosystems.list");
    }
}]);
