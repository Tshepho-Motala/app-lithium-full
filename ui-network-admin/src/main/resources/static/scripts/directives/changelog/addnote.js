'use strict';

angular.module('lithium')
    .controller('AddChangeLogNoteModal',
        ['$uibModalInstance', 'domainName', 'entityId', 'notify', 'errors', 'bsLoadingOverlayService', 'ChangelogsRest', '$translate',
            function ($uibModalInstance, domainName, entityId, notify, errors, bsLoadingOverlayService, changelogsRest, $translate) {
                let controller = this;

                controller.options = {};
                controller.model = {};
                controller.fields = [
                    {
                        className: 'col-xs-12',
                        key: 'category',
                        type: 'ui-select-single',
                        templateOptions: {
                            onChange: function ($viewValue, $model, $scope) {
                                changelogsRest.subCategories($viewValue).then(function (response) {
                                    if (response.plain().length === 0) {
                                        controller.fields[1].templateOptions.disabled = true;
                                    } else {
                                        controller.fields[1].templateOptions.disabled = undefined;
                                        controller.fields[1].templateOptions.options = response.plain();
                                    }
                                });
                                controller.model.subCategory = undefined;
                            },
                            label: 'Category',
                            placeholder: 'Select categories...',
                            valueProp: 'name',
                            labelProp: 'name',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: true
                        },

                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CATEGORY.LABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CATEGORY.PLACEHOLDER" | translate'
                        },
                        controller: ['$scope', function ($scope) {
                            changelogsRest.categories().then(function (response) {
                                $scope.to.options = response.plain();
                            });
                        }]
                    }, {
                        className: 'col-xs-12',
                        key: 'subCategory',
                        type: 'ui-select-single',
                        templateOptions: {
                            label: 'Sub Category',
                            placeholder: 'Select sub category...',
                            valueProp: 'name',
                            labelProp: 'name',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: false,
                            disabled: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.LABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.PLACEHOLDER" | translate',
                        },

                    }, {
                        className: 'col-xs-12',
                        key: 'priority',
                        type: 'ui-select-single',
                        templateOptions: {
                            label: '',
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [
                                {
                                    value: 0,
                                    label: $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LOW")
                                },
                                {
                                    value: 34,
                                    label: $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.MEDIUM")
                                },
                                {
                                    value: 67,
                                    label: $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.HIGH")
                                }
                            ],
                            required: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.PLACEHOLDER" | translate'
                        }
                    }, {
                        key: "text",
                        type: "textarea",
                        className: "col-xs-12",
                        templateOptions: {
                            label: "Text",
                            required: true,
                            maxlength: 65535
                        }
                    }
                ];

                controller.referenceId = 'addnote-overlay';
                controller.submit = function () {
                    bsLoadingOverlayService.start({referenceId: controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        return false;
                    }
                    changelogsRest.addNote(domainName, entityId, controller.model).then(function (response) {
                        $uibModalInstance.close(response);
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.PLAYER.NOTE.ADD.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                    });
                };

                controller.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            }
        ]
    );
