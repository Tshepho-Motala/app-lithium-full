'use strict';

angular.module('lithium')
    .controller('adduserLinkModal', ['user', 'isModify', 'updatePlayerLinkData', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'UserRestrictionsRest', 'UserRest',
            function (user, isModify, updatePlayerLinkData, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, rest, userRest) {
                var controller = this;
                controller.isModified = isModify;

                $scope.title = "UI_NETWORK_ADMIN.ECOSYSTEMS.MODAL.ADD";
                // $scope.titleUpdate = "Update Player Link";

                controller.options = {};
                controller.model = {
                    "primaryUser": user.username
                };

                if(updatePlayerLinkData && updatePlayerLinkData.userLinkType) {
                    controller.model['userLinkType'] = updatePlayerLinkData.userLinkType.code
                }
                
                controller.fields = [
                    {
                        className: 'col-xs-12',
                        key: 'userLinkType',
                        type: 'ui-select-single',
                        templateOptions : {
                            required: true,
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                            placeholder: '',
                            options: []
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.MODAL.LABEL.TYPE" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.ECOSYSTEMS.MODAL.DESCRIPTION.TYPE" | translate'
                        },            
                        controller: ['$scope', function($scope) {
                            userRest.userLinkTypeList().then(function(response) {
                                var options = [];
                                var response = response.plain();
                                for (var i = 0; i < response.length; i++) {
                                    var opt = response[i];
                                    if(opt.code !== 'CROSS_DOMAIN_LINK'){
                                        options.push({label: opt.code, value: opt.code});
                                    }
                                }
                                $scope.to.options = options;
                            });
                        }]
                    },
                    {
                        className: "col-xs-12",
                        key: "linkNote",
                        type: "input",
                        optionsTypes: ['editable'],
                        templateOptions: {
                            label: "", description: "", placeholder: "",
                            required: true, minlength: 5, maxlength: 1000
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.MODAL.LABEL.COMMENT" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ECOSYSTEMS.MODAL.PLACEHOLDER.COMMENT" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.ECOSYSTEMS.MODAL.DESCRIPTION.COMMENT" | translate'
                        }
                    }
                ];

                controller.referenceId = 'adduserlinkmodal-overlay';

                if (!controller.isModified) {
                    controller.model.linkNote = updatePlayerLinkData.linkNote;
                }
                controller.secondaryUserLinkData = {};
                controller.isSecondaryData = false;
                if (updatePlayerLinkData !== null && updatePlayerLinkData.secondaryUser !== undefined) {
                    userRest.playerLinks(updatePlayerLinkData.secondaryUser.guid).then(function(response){
                        if (response !== undefined ) {
                            controller.isSecondaryData = true;
                            controller.secondaryUserLinkData = response.plain();
                        }
                    });
                }
                controller.submit = function() {
                    bsLoadingOverlayService.start({referenceId:controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        return false;
                    }
                    var primaryUserGuid = `${user.domain.name}/${controller.model.primaryUser}`;
                    var secondaryUserGuid = `${user.domain.name}/${controller.secondaryUser}`;
                    
                    if (secondaryUserGuid != primaryUserGuid) {
                        if (!controller.isModified) {
                            userRest.modifyUserLink(updatePlayerLinkData.id, controller.model.userLinkType, controller.model.linkNote).then(function (response) {
                                notify.success("UI_NETWORK_ADMIN.ECOSYSTEMS.NOTIFY.UPDATE");
                                $uibModalInstance.close(response);
                            }).catch(function() {
                                errors.catch('', false);
                            }).finally(function() {
                                bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                            });
                        } else {
                            userRest.addUserLink(primaryUserGuid, secondaryUserGuid, controller.model.userLinkType, controller.model.linkNote).then(function(response) {
                                notify.success("UI_NETWORK_ADMIN.ECOSYSTEMS.NOTIFY.ADD");
                                $uibModalInstance.close(response);
                            }).catch(function() {
                                errors.catch('', false);
                            }).finally(function() {
                                bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                            });
                        }
                    } else {
                        notify.error("UI_NETWORK_ADMIN.ECOSYSTEMS.FORM_ERROR.LABEL");
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        return false;

                    }
                };

                controller.searchPlayer = function(searchValue) {
                    return userRest.search(user.domain.name, searchValue).then(function(response) {
                        return response.plain();
                    });
                }

                controller.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            }
        ]
    );
