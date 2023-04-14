'use strict';

angular.module('lithium')
    .controller('ErrorMessageAdd', ["languages","subModule", "$uibModalInstance", "$userService", "notify", "errors", "UserRest","rest-translate", '$stateParams',
        function(languages, subModule, $uibModalInstance, $userService, notify, errors, userRest, translateRest, $stateParams) {
            var controller = this;

            controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "ERROR_MESSAGES_EDIT"]);

            controller.model = {
            };
            controller.options = {}

            controller.fields = [
                {
                "className":"col-xs-12",
                "type":"input",
                "key":"messageKey",
                "templateOptions":{
                    "type":"",
                    "label":"",
                    "required":true,
                    "placeholder":"",
                    "description":"",
                    "minlength" : 10,
                    "maxlength" : 60,
                    "options":[]
                },
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_KEY.TITLE" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_KEY.PLACEHOLDER" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_KEY.DESCRIPTION" | translate'
                }
            }, {
                    "className":"col-xs-12",
                    "type":"input",
                    "key":"description",
                    "templateOptions":{
                        "type":"",
                        "label":"",
                        "required":true,
                        "placeholder":"",
                        "description":"",
                        "options":[]
                    },
                    "expressionProperties": {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_DEFAULT_TRANSLATION.TITLE" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_DEFAULT_TRANSLATION.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_DEFAULT_TRANSLATION.DESCRIPTION" | translate'
                    }
                }/*, {
                "className":"col-xs-12 form-group",
                "type":"ui-select-single",
                "key":"messageLanguage",
                "templateOptions":{
                    "label":"",
                    "placeholder":"",
                    "description":"",
                    "required":true,
                    "optionsAttr": "bs-options",
                    "valueProp": "value",
                    "labelProp": "label",
                    "options": []
                },
                "expressionProperties": {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_LANGUAGE.TITLE" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_LANGUAGE.PLACEHOLDER" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.FIELDS.MESSAGE_LANGUAGE.DESCRIPTION" | translate'
                },
                    //THIS COMMENTED CODE PULLS OUT ALL LANGUAGES, FOR NOW WE ARE USING A DEFAULT LANGUAGE ON ERROR CODE CREATION WITH DEFAULT ENGLISH TRANSLATION TO FOLLOW THE EXISTING FORMAT, USER CAN ADD TRANSLATIONS FOR USING THE EDIT OPTION
                // controller: ['$scope', function($scope) {
                //     $scope.options.templateOptions.options = languages;
                // }]
                    controller: ['$scope', function ($scope) {
                        $scope.options.templateOptions.options = [
                            {value: "en", label: "English"}
                        ]
                    }]
            }*/];

            controller.onSubmit = function() {
                if (controller.form.$invalid) {
                    angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                    return false;
                }

                translateRest.addTranslationKey(controller.model, $stateParams.domain.name, subModule).then(function(translationKey) {
                    notify.success("UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.ADD.SUCCESS");
                    $uibModalInstance.close(translationKey);
                }).catch(function(error) {
                    if (error.status === 400) {
                        notify.error($translate.instant('UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.ADD.BAD_REQUEST'));
                    } else if (error.status === 409) {
                        notify.error($translate.instant('UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.ADD.CONFLICT'));
                    } else if (error.status === 550) {
                        notify.error($translate.instant('UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.ADD.DOMAIN'));
                    }
                    errors.catch("", false)(error)
                });
            }

            controller.cancel = function() {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]);