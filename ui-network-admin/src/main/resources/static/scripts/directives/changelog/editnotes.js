'use strict';

angular.module('lithium').controller('EditNotesModal', ['$uibModalInstance', 'domainName', 'entityId','selectedNote',
    'restService', 'notify', 'errors', 'bsLoadingOverlayService', 'ChangelogsRest', '$translate',

function ($uibModalInstance, domainName, entityId, selectedNote, restService, notify, errors, bsLoadingOverlayService, changelogsRest
, $translate) {

    const controller = this;
    controller.referenceId = 'editnotes-overlay'
    controller.submitCalled = false;
    controller.options = {removeChromeAutoComplete : true};

    controller.model = {
        category: selectedNote.category.name,
        subCategory: selectedNote.subCategory != null ? selectedNote.subCategory.name : selectedNote.subCategory,
        priority: selectedNote.priority
    };
    controller.model.comment = selectedNote.comments;

    let selectedPriority = function() {
        switch (selectedNote.priority) {
            case 0:
                return $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LOW");
                break;
            case 34:
                return $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.MEDIUM");
                break;
            case 67:
                return $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.HIGH");
                break;
        }
    }

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
                placeholder: '',
                valueProp: 'name',
                labelProp: 'name',
                optionsAttr: 'ui-options',
                ngOptions: 'ui-options',
                options: [],
                required: false
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.LABEL" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.PLACEHOLDER" | translate',
            },
            controller: [function() {
                changelogsRest.subCategories(controller.model.category).then(function (response) {
                    if (response.plain().length === 0) {
                        controller.fields[1].templateOptions.disabled = true;
                    } else {
                        controller.fields[1].templateOptions.disabled = undefined;
                        controller.fields[1].templateOptions.options = response.plain();
                    }
                });
            }]
        }, {
            className: 'col-xs-12',
            key: 'priority',
            type: 'ui-select-single',
            templateOptions: {
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
        },
        {
            className: 'col-xs-12',
            key: 'comment',
            type: 'textarea',
            templateOptions: {
                label: 'Text', maxlength: 65535, required: true
            }
        }
    ]

    controller.submit = function() {
        bsLoadingOverlayService.start({referenceId:controller.referenceId});
        if (controller.form.$invalid) {
            angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
            notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
            bsLoadingOverlayService.stop({referenceId: controller.referenceId});
            return false;
        }
        let  newNote = {
            'id' : entityId,
            'authorGuid': selectedNote.authorUser.guid,
            'categoryName' : controller.model.category == null ? selectedNote.category.name : controller.model.category,
            'subCategoryName' : controller.model.subCategory == null ? '' : controller.model.subCategory,
            'priority' : controller.model.priority == null ? selectedNote.priority : controller.model.priority,
            'comments' : controller.model.comment
        }

        restService.editNotes(newNote).then(function(response) {
            $uibModalInstance.close(response);
        }).catch(
            errors.catch('UI_NETWORK_ADMIN.DIRECTIVES.GLOBAL.CHANGE_LOG.ERROR', false)
        ).finally(function () {
            bsLoadingOverlayService.stop({referenceId:controller.referenceId});
        });
    }

    controller.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);
