'use strict';

angular.module('lithium').directive('errorMessages', function() {
    return {
        templateUrl:'scripts/directives/errormessages/errormessages.html',
        scope: {
            data: '=',
            subModule: '=',
            domainSpecific: '=',
            reload: '=ngModel'
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$dt', '$translate', '$scope', 'DTOptionsBuilder', 'notify', '$filter', '$compile', '$uibModal', 'rest-translate', '$userService',
            function($dt, $translate, $scope, DTOptionsBuilder, notify, $filter, $compile, $uibModal, translateRest, $userService) {

                var controller = this;
                controller.referenceId = "controller_"+(Math.random()*1000);
                controller.changelogs = null;
                controller.data = {
                    domainName: $scope.data.domainName,
                    subModule: $scope.subModule,
                    domainSpecific: $scope.domainSpecific
                }

                controller.getErrorMessageBuilder = function() {
                    return $dt.builder()
                        .column($dt.column(function (data) {
                            return data.code;
                        }).withTitle($translate('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TABLE.ERROR_CODE')))
                        .column($dt.column(function (data) {
                            let newString = controller.stringWrap(data.value, 100, true, []);
                            return newString.join('<br/>');
                        }).withTitle($translate('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TABLE.ERROR_MESSAGE')))
                        .column($dt.column(function (data) {
                            let languages = data.languages;
                            let locale = '';
                            for (let i = 0; i < languages.length; i++) {
                                locale = i == 0 ? languages[i] : locale + ', ' + languages[i];
                            }
                            return locale.trim();
                        }).withTitle($translate('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TABLE.LOCALE')))
                        .column($dt.linkscolumn("",
                            [
                                {
                                    permission: "error_messages_view",
                                    permissionType:"any",
                                    permissionDomain: controller.data.domainName,
                                    title: $userService.domainsWithAnyRole(["ADMIN", "ERROR_MESSAGES_EDIT"]).length > 0 ? $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TABLE.EDIT') : $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TABLE.VIEW'),
                                    click: function(data){
                                        controller.editTranslation(data)
                                    }
                                }]))
                        .column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                            if (data.userDefined === "true") {
                                return '<button class="btn btn-danger" ng-disabled="false" ng-click="controller.deleteErrorMessage(' + row.id + ')"><i class="fa fa-trash-o"></i></button>';
                            } else {
                                return '<button class="btn btn-danger" ng-disabled="true"><i class="fa fa-trash-o"></i></button>';
                            }
                        }))
                }

                controller.stringWrap = function (string, length, useSpaces, array) {
                    array = array || [];
                    if (string.length <= length) {
                        array.push(string);
                        return array;
                    }
                    
                    var line = string.substring(0, length);
                    if (! useSpaces) { // insert newlines
                        array.push(line);
                        return controller.stringWrap(string.substring(length), length, useSpaces, array);
                    }
                    else { // insert newlines after whitespace
                        var lastSpaceRgx = /\s(?!.*\s)/;
                        var idx = line.search(lastSpaceRgx);
                        var nextIdx = length;
                        if (idx > 0) {
                            line = line.substring(0, idx);
                            nextIdx = idx;
                        }
                        array.push(line);
                        return controller.stringWrap(string.substring(nextIdx), length, useSpaces, array);
                    }
                }

                controller.editTranslation = function (data) {
                    let modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/brandConfig/errormessages/editerrormessage.html',
                        controller: 'EditErrorMessageModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            domainName: function () {
                                return controller.data.domainName;
                            },
                            translationData: function () {
                                return data;
                            },
                            domainSpecific: function () {
                                return controller.data.domainSpecific;
                            },
                            toggleErrorMessage: function() {
                                return {
                                    enabled: false,
                                }
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/controllers/dashboard/brandConfig/errormessages/editerrormessage.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        controller.refresh();
                    });
                };

                controller.deleteErrorMessage = function(id) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/brandConfig/errormessages/confirmdelete.html',
                        controller: 'ConfirmErrorMessageNoteDeleteModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            entityId: function () {
                                return id;
                            },
                            restService: function () {
                                return translateRest;
                            },
                            domainSelected : function () {
                                return controller.data.domainName;
                            },

                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/controllers/dashboard/brandConfig/errormessages/confirmdelete.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (response) {
                        controller.refresh();
                        notify.success('UI_NETWORK_ADMIN.BRAND_CONFIG.ERROR_MESSAGES.DELETE.SUCCESS');
                    });
                };

                controller.addErrorMessage = function() {
                    let modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/brandConfig/errormessages/adderrormessage.html',
                        controller: 'ErrorMessageAdd',
                        controllerAs: 'controller',
                        size: 'md cascading-modal',
                        backdrop: 'static',
                        resolve: {
                            languages: function () {
                                return controller.data.languages;
                            },
                            subModule: function (){
                                return controller.data.subModule;
                            },
                            loadMyFiles:function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [
                                        'scripts/controllers/dashboard/brandConfig/errormessages/adderrormessage.js'
                                    ]
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function(translationKey) {
                        controller.refresh();
                    });
                }

                var baseUrl = "services/service-translate/apiv2/translations/" + controller.data.domainName + "/" + controller.data.subModule + "/list?domainSpecific=" + controller.data.domainSpecific;
                var dtOptions = DTOptionsBuilder.newOptions().withOption('bFilter', false).withOption('createdRow', function(row, data, dataIndex) {
                // Recompiling so we can bind Angular directive to the DT
                $compile(angular.element(row).contents())($scope);
                });

                controller.errorMessageTable = controller.getErrorMessageBuilder()
                    .options(
                        {
                            url: baseUrl,
                            type: 'GET',
                            data: function (d) {}
                        },
                        null,
                        dtOptions,
                        null).build();

                controller.refresh = function () {
                    controller.errorMessageTable.instance.reloadData(function () {
                    }, false);
                    $scope.data.reload();
                };
                $scope.data.reload();
            }
        ]
    }
});