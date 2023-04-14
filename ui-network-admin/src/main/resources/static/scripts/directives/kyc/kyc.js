'use strict';

angular.module('lithium').directive('kyc', function () {

        return {
            templateUrl: 'scripts/directives/kyc/kyc.html',

            scope: {
                inputPlayer: "=?",
            },
            restrict: 'E',
            replace: true,
            controllerAs: 'controller',
            controller: [
                '$state',  '$filter', '$dt', '$translate', '$compile', 'DTOptionsBuilder', '$uibModal', '$scope', '$rootScope',
                function ($state, $filter, $dt, $translate, $compile, DTOptionsBuilder, $uibModal, $scope, $rootScope) {
                    var controller = this;

                    controller.model = {};
                    controller.fields = [];
                    controller.selectedUser = $scope.inputPlayer;
                    $scope.showVendorModal = false

                    controller.uploadAndSubmitDocument = function () {
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/directives/player/document-verification/upload-and-submit.html',
                            controller: 'DocumentVerificationModal',
                            controllerAs: 'controller',
                            backdrop: 'static',
                            size: 'md',
                            resolve: {
                                user: function () {
                                    return $scope.inputPlayer;
                                },
                                loadMyFiles: function ($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        name: 'lithium',
                                        files: ['scripts/directives/player/document-verification/upload-and-submit.js']
                                    })
                                }
                            }
                        });

                        modalInstance.result.then(function (result) {
                            console.log('on documentation verification modal close')
                            // $scope.data.verificationStatus = result;
                        });
                    }


                    var baseUrl = "services/service-kyc/backoffice/kyc/table?1=1";
                    var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('createdRow', function (row, data, dataIndex) {
                        $compile(angular.element(row).contents())($scope);
                    }).withOption('order', [0, 'desc']);

                    var dtBuilder = $dt.builder();

                    dtBuilder
	                    .column($dt.column('success').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.IS_SUCCESS.LABEL')).notSortable())
	                    .column($dt.column('reasonValue').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.REASON.LABEL')).notSortable())
                        .column($dt.linkscolumn("Vendor data", [{
                            title: function (data) {
                                if(data.vendorsData && data.vendorsData.length) {
                                    return 'Info'
                                } else {
                                    return  ''
                                }
                            },
                            permission: 'PLAYER_KYC_RESULTS_VIEW',
                            permissionType: 'any',

                            click: function (data) {
                                $scope.showVendorModal  = !$scope.showVendorModal
                                if($scope.showVendorModal === true) {
                                    if(data.vendorsData && data.vendorsData.length) {
                                        $rootScope.provide.playerKYCProvider['vendorData'] = data.vendorsData
                                    } else {
                                        $rootScope.provide.playerKYCProvider['vendorData'] = []
                                    }
                                    window.VuePluginRegistry.loadByPage("PlayerKYCVendorModal")
                                }
                            }
                        }]))
                        .column($dt.column('providerRequestId').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.KYC_RECORD_ID.LABEL')).notSortable())
	                    .column($dt.column('resultMessage.description').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.PROVIDER_MSG.LABEL')).notSortable())
                        .column($dt.columnformatdatetime('createdOn').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.DATE.LABEL')).notSortable())
                        .column($dt.column('provider.guid').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.PROVIDER.LABEL')).notSortable())
                        .column($dt.column('methodType.name').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.METHOD.LABEL')).notSortable())
                        .column($dt.column('legalLastName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.LEGAL_NAME.LABEL')).notSortable())
	                    .column($dt.column('fullName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.FULL_NAME.LABEL')).notSortable())
                        .column($dt.column('dob').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.DOB.LABEL')).notSortable())
                        .column($dt.column('address').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.ADDRESS.LABEL')).notSortable())
                        .column($dt.column('countryOfBirth').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.COUNTRY_OF_BIRTH.LABEL')).notSortable())
                        .column($dt.column('phoneNumber').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.PHONE_NUMBER.LABEL')).notSortable())
                        .column($dt.column('nationality').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.NATIONALITY.LABEL')).notSortable())
                        .column($dt.column('methodTypeUid').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.ID_NUMBER.LABEL')).notSortable())
	                    .column($dt.column('bvnUid').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.BVN.LABEL')).notSortable())
                        .column($dt.linkscolumn("Photo", [{
                            title: "GLOBAL.ACTION.OPEN",
                            permission: 'PLAYER_KYC_RESULTS_VIEW',
                            permissionType: 'any',
                            click: function (data) {
                                if(data.document) {
                                    let base64Img = 'data:image/png;base64,' + data.document.body;
                                    let w = window.open('about:blank');
                                    let image = new Image();
                                    image.src = base64Img;
                                    setTimeout(function () {
                                        w.document.write(image.outerHTML);
                                    }, 0)
                                }

                            }
                        }]))

                        .column($dt.column('documentDecision').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.DOCUMENT_DECISION.LABEL')).notSortable())
                        .column($dt.column('addressDecision').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.KYC.TAB.FIELDS.ADDRESS_DECISION.LABEL')).notSortable())
                        .options({
                            url: baseUrl, type: 'POST', data: function (d) {
                                d.userGuid = controller.selectedUser.guid;
                            }
                        }, null, dtOptions, null);
                    controller.verificationsTable = dtBuilder.build();
                    controller.refresh = function () {
                        controller.verificationsTable.instance.reloadData(function () {
                        }, false);
                    }

                    $rootScope.provide.playerKYCProvider['closeModal'] = () => {
                        $scope.showVendorModal = false
                    }
                }
            ]
        };
    }
);
