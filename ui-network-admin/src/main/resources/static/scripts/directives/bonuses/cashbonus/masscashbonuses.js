'use strict';

angular.module('lithium').directive('massCashBonuses', function() {
    return {
        templateUrl:'scripts/directives/bonuses/cashbonus/masscashbonuses.html',
        scope: {
            data: '='
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$dt', '$translate', 'rest-user-mass-action', '$scope', 'DTOptionsBuilder', 'notify', '$filter', '$compile', '$state', 'rest-domain',
            function($dt, $translate, userMassActionRest, $scope, DTOptionsBuilder, notify, $filter, $compile, $state, domainRest) {

                var controller = this;
                controller.start = true;
                controller.maxPayout = 125000;
                controller.invalidAmountMessage = "";

                domainRest.findCurrentDomainSetting($scope.data.domainName, "maximum_bonus_payout")
                    .then(function (response) {
                        if (response !== undefined && response !== null && response._status === 0) {
                            if (response.labelValue !== undefined && response.labelValue !== null && response.labelValue.value != null && response.labelValue.value !== "") {
                                controller.maxPayout = response.labelValue.value;
                                controller.invalidAmountMessage = $translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.AMOUNT.INVALID_AGAINST_DOMAIN') + response.labelValue.value;
                            } else {
                                controller.invalidAmountMessage = $translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.AMOUNT.INVALID') + controller.maxPayout;
                            }
                        }
                    });

                controller.selectFile = function (file) {
                    controller.selectedFile = null;
                    let amountValid = controller.model.defaultAmount > 0 && controller.model.defaultAmount <= controller.maxPayout;
                    if (file != null && controller.model.bonusCode !== undefined  && controller.model.bonusCode !== '' && amountValid) {
                        controller.selectedFile = file;
                        controller.model.fileName = controller.selectedFile.name;
                        controller.upload();
                    } else {
                        if (file != null && !amountValid) {
                            notify.error(controller.invalidAmountMessage);
                        }
                        if (file != null && (controller.model.bonusCode === undefined || controller.model.bonusCode === ''))  {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.NO_BONUS_CODE_SELECTED'));
                        }
                    }
                };

                controller.init = function(view) {
                    controller.model = {
                        action: {
                            id: -1,
                            uploadType: "BONUS_CASH"
                        },
                        view: view,
                        bonusCodes: $scope.data.bonusCodes,
                        defaultAmount: 0,
                        domainName: $scope.data.domainName,
                        bonusDescription: '',
                        allowDuplicates: false,
                        fileName: '',
                        uploadType: [
                            { name: 'CSV File', value: 'csv' , checked: 'checked'},
                            { name: 'Segment', value: 'segment', disabled: 'disabled' }
                        ],
                        progress: 0
                    }
                }
                controller.init('history');

                controller.getUploadStatus = function() {
                    if (controller.model.action.id === -1) {
                        controller.model.bonusCode = null;
                        controller.model.defaultAmount = 0;
                        controller.model.bonusDescription = '';
                        controller.model.allowDuplicates = false;
                        controller.model.bonusFileUpload = {};
                        controller.model.fileName = '';
                        controller.waitForProcessingStart = false;
                        controller.start = true;
                        controller.uploading = false;
                        controller.failed = false;
                        controller.done = false;
                    } else {
                        userMassActionRest.getFileUploadStatus(controller.model).then(function (response) {
                            controller.model.action = response.plain();
                            if (response !== null && response.id !== null) {
                                //When you are resuming a state, prepopulate the bonus code, default bonus amount and description
                                controller.model.bonusCode = response.massActionMeta.bonusCode;
                                controller.model.defaultAmount = response.massActionMeta.defaultBonusAmount;
                                controller.model.bonusDescription = response.massActionMeta.bonusDescription;
                                controller.model.allowDuplicates = response.massActionMeta.allowDuplicates;
                                controller.model.bonusFileUpload = response;
                                controller.model.fileName = response.file == null ? '' : response.file.fileName;
                                if (controller.model.bonusFileUpload.uploadStatus === 'UPLOADED') {
                                    controller.start = false;
                                    controller.uploading = true;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.createCheckedTable();
                                    controller.monitorBonusCsvFileUploadProgress('user-verification');
                                } else if (controller.model.bonusFileUpload.uploadStatus === 'CHECKING') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.checking = true;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.done = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.monitorBonusCsvFileUploadProgress('user-verification');
                                } if (response.uploadStatus === 'CHECKED') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.model.progress = 100;
                                    controller.checked = true;
                                    controller.processing = false;
                                    controller.done = false;
                                    controller.processBtn = true;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.monitorBonusCsvFileUploadProgress('bonus-grant');
                                } else if (controller.model.bonusFileUpload.uploadStatus === 'PROCESSING') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.checked = false;
                                    controller.processing = true;
                                    controller.done = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = true;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.monitorBonusCsvFileUploadProgress('bonus-grant');
                                } else if (controller.model.bonusFileUpload.uploadStatus === 'DONE') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.checking = true;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.done = true;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = false;
                                    controller.model.progress = 100;
                                    controller.getUploadSummary();
                                } else if (controller.model.bonusFileUpload.uploadStatus === 'FAILED_STAGE_1' || controller.model.bonusFileUpload.uploadStatus === 'FAILED_STAGE_2') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.checking = true;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.done = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = true;
                                    controller.model.bonusCode = '';
                                    controller.model.defaultAmount = 0;
                                    controller.model.bonusDescription = '';
                                    controller.model.allowDuplicates = false;
                                    controller.model.fileName = '';
                                    controller.getUploadSummary();
                                }
                            }
                        });
                    }
                }
                controller.getUploadSummary = function() {
                    userMassActionRest.getFileUploadSummary(controller.model).then(function(response) {
                        controller.model.uploadSummary = response;
                    });
                }

                controller.upload = function() {
                    userMassActionRest.uploadCsv(controller.model, controller.selectedFile).then(function(response) {

                        controller.model.action = response;
                        if (response.uploadStatus === 'FAILED_STAGE_1' || response.uploadStatus === 'FAILED_STAGE_2') {
                            controller.uploading = false;
                            controller.checking = false;
                            controller.checked = false;
                            controller.processing = false;
                            controller.processBtn = false;
                            controller.model.bonusCode = '';
                            controller.model.defaultAmount = 0;
                            controller.model.bonusDescription = '';
                            controller.model.allowDuplicates = false;
                            controller.model.fileName = '';
                            controller.failed = true;
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.FILE_UPLOAD_FAILED'));
                        } else {
                            controller.getUploadStatus();
                        }
                    });
                    
                    // Quality of life update to hasten the initial status check
                    setTimeout(() => {
                        controller.getUploadStatus();
                    }, 400)
                }

                controller.monitorBonusCsvFileUploadProgress = function(stageName) {
                    userMassActionRest.monitorFileUploadProgress(controller.model, stageName).then(function(response) {
                        controller.model.progress = response.percentile;
                        if (!controller.start && (response.uploadStatus === 'FAILED_STAGE_1' || response.uploadStatus === 'FAILED_STAGE_2')) {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.FILE_UPLOAD_FAILED'));
                            controller.failed = true;
                        }
                        if (controller.uploading && controller.model.progress > 0) {
                            controller.checking = true
                            controller.uploading = false;
                        } else if (controller.processing && controller.model.progress > 0) {
                            controller.waitForProcessingStart = false;
                        }
                        if(controller.model.progress === undefined || controller.model.progress === 0 || controller.model.progress < 100) {
                            setTimeout( async function(){
                                if (controller.model.action.id === -1) { return; } // When the user clicks the back button to history view
                                if (controller.processing) {
                                    await controller.getUploadStatus();
                                    await controller.refreshCheckedTable();
                                }
                                controller.monitorBonusCsvFileUploadProgress(stageName);
                            }, 3000);
                        } else {
                            // Get the data first
                            controller.getUploadSummary();

                            // Then ensure we've created the table
                            // Calling this multiple times has no negative impact
                            controller.createCheckedTable();

                            // Then, after a small cool-down, display the table with the data
                            setTimeout(() => {
                                if (stageName === 'user-verification') {
                                    controller.checked = true;
                                    controller.processing = false;
                                    controller.processBtn = true;
                                } else {
                                    controller.checked = false;
                                    controller.processing = true;
                                }
                            }, 500)

                            return;
                        }
                    });
                }

                controller.fields = [
                    {
                        className: 'col-xs-12',
                        key: 'bonusCode',
                        type: 'select',
                        templateOptions: {
                            label: "Bonus Codes",
                            placeholder: "Select bonus codes...",
                            options: controller.model.bonusCodes,
                            required: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.BONUS_CODES.LABEL" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.BONUS_CODES.DESC" | translate'
                        }
                    },
                    {
                        className: 'col-xs-12',
                        key: 'defaultAmount',
                        type: 'ui-money-mask',
                        templateOptions: {
                            label: 'Amount',
                            description: 'Enter the amount',
                            required: false,
                            symbol: $scope.data.currencySymbol
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.AMOUNT.LABEL" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.AMOUNT.DESC" | translate'
                        },
                        validators: {
                            pattern: {
                                expression: function($viewValue, $modelValue, scope) {
                                    return $modelValue <= 0 ? false : $modelValue <= controller.maxPayout;
                                },
                                message: function () {
                                    return controller.invalidAmountMessage;
                                }
                            }
                        }
                    },
                    {
                        className: 'col-xs-12',
                        key: 'bonusDescription',
                        type: 'input',
                        templateOptions: {
                            label: 'Description',
                            description: 'Enter the description',
                            required: false
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.DESC.LABEL" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.DESC.DESC" | translate'
                        }
                    }
                ]

                controller.paintCell = function (data, value) {
                    let errorDetected = false;
                    if (data.userStatus === 'UNKNOWN') {
                        errorDetected = true;
                    } else if (data.dataError != null) {
                        errorDetected = true;
                    } else if (data.userStatus !== 'OPEN') {
                        errorDetected = true;
                    }

                    if (data.duplicate === false) {
                        return '<div class="limited text-wrap" ' + (errorDetected ? 'style="color: red"' : 'style="color: green"')
                            +'title="' + value + '">' + value + '</div>';
                    } else {
                        return '<div class="limited text-wrap" ' + (errorDetected ? 'style="color: red"' : 'style="color: orange"')
                            +'title="' + value + '">' + value + '</div>';
                    }
                }

                controller.processingStatus = function (data) {
                    if (data.uploadStatus === 'FAILED_STAGE_1' || data.uploadStatus === 'FAILED_STAGE_2' || data.userStatus !== 'OPEN') {
                        return '<div class="limited text-wrap"><i class="fa fa-times fa-lg" style="color:red"></i></div>';
                    } else if (data.uploadStatus === 'DONE' && data.dataError === 'UNABLE_TO_GRANT_BONUS') {
                        return '<div class="limited text-wrap"><i class="fa fa-chain-broken fa-lg" style="color:red"></i></div>';
                    } else if (data.uploadStatus === 'DONE') {
                        return '<div class="limited text-wrap"><i class="fa fa-check fa-lg" style="color:green"></i></div>';
                    } else if (controller.processing){
                        if (data.uploadStatus === 'CHECKED') {
                            if (!controller.model.allowDuplicates) {
                                if (data.duplicate) {
                                    return '<div class="limited text-wrap"><i class="fa fa-fast-forward fa-lg"></i></div>';
                                } else {
                                    return '<div class="limited text-wrap"><i class="fa fa-spinner fa-pulse fa-spin"></i></div>';
                                }
                            } {
                                return '<div class="limited text-wrap"><i class="fa fa-spinner fa-pulse fa-spin"></i></div>';
                            }
                            return '<div class="limited text-wrap"><i class="fa fa-check fa-lg" style="color:green"></i></div>';
                        }
                    } else {
                        return '<div class="limited text-wrap"></div>';
                    }
                }

                controller.removeRecord = function(id) {
                    userMassActionRest.removeFileDataRecord(controller.model, controller.deleted[id].rowNumber).then(function(response) {
                        controller.refreshCheckedTable();
                        controller.getUploadSummary();
                    });
                }
                controller.deleted = [];
                controller.getCheckedBuilder = function() {
                    return $dt.builder()
                        .column($dt.columnWithClass(function (data) {
                            return controller.paintCell(data, data.rowNumber)
                        }).withTitle($translate('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.TABLE.ROW_NUMBER.LABEL')))
                        .column($dt.linkscolumn("",
                            [{
                                permission: "MASS_BONUS_ALLOCATION_VIEW",
                                permissionType:"any",
                                permissionDomain: function(data) {
                                    return controller.model.domainName;
                                },
                                title: function(data) {
                                    return data.uploadedPlayerId;
                                },
                                href: function(data) {
                                    return $state.href("dashboard.players.player.summary", {
                                        id: data.uploadedPlayerId,
                                        domainName: controller.model.domainName
                                    })
                                }
                            }]).withTitle($translate('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.TABLE.PLAYER_ID.LABEL')))
                        .column($dt.labelcolumn($translate('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.TABLE.ACCOUNT_STATUS.LABEL'),
                            [{
                                text: function (data) {
                                    return data.userStatus ? data.userStatus : 'UNKNOWN';
                                },
                                lclass: function (data) {
                                    let errorDetected = false;
                                    if (data.dataError != null) {
                                        errorDetected = true;
                                    } else if (data.userStatus !== 'OPEN') {
                                        errorDetected = true;
                                    }

                                    if (data.duplicate === false) {
                                        return errorDetected ? "danger label-columns" : "default label-columns bg-green";
                                    } else {
                                        return errorDetected ? "danger label-columns" : "default label-columns bg-orange";
                                    }
                                }
                            }, {
                                text: function (data) {
                                    return data.userStatusReason;
                                },
                                lclass: function (data) {
                                    if (data.userStatusReason === undefined || data.userStatusReason == null || data.userStatusReason === '')
                                        return "default hide";
                                    else
                                        return "default label-columns";
                                }
                            }]))
                        .column($dt.columnWithClass(function (data) {
                            return controller.paintCell(data, $filter('cents')(data.amount * 100, $scope.data.currencySymbol))
                        }).withTitle($translate('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.TABLE.AMOUNT.LABEL')))
                        .column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                            return controller.processingStatus(data)
                        }))
                        .column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                            controller.deleted[row.id] = data;
                            if (controller.checked)
                                return '<button class="btn btn-danger" ng-click="controller.removeRecord(' + row.id + ')"><i class="fa fa-trash-o"></i></button>';
                            else
                                return '<button class="btn btn-danger" ng-click="controller.removeRecord(' + row.id + ')" disabled><i class="fa fa-trash-o"></i></button>';
                        }));
                }

                controller.submit = function() {
                    let amountValid = controller.model.defaultAmount > 0 && controller.model.defaultAmount <= controller.maxPayout;
                    if (controller.model.bonusCode !== undefined && amountValid && controller.model.uploadSummary.existingPlayers > 0 && (controller.model.uploadSummary.existingNotFailedPlayers > 0 || controller.model.defaultAmount > 0)) {
                        controller.processing = true;
                        controller.model.progress = 0;
                        userMassActionRest.processFileUpload(controller.model).then(function(response) {
                            console.debug("Successfully uploaded file -> {}", response);
                            controller.getUploadStatus();
                        }).catch(function(error) {
                            console.error("Error : -> {}", error);
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.INVALID_AMOUNT'))
                        });
                    } else {
                        if (!amountValid) {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.INVALID_AMOUNT'));
                        }
                        if (controller.model.bonusCode === undefined)  {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.NO_BONUS_CODE_SELECTED'));
                        }
                        if (controller.model.uploadSummary.existingPlayers === 0)  {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.NO_VALID_PLAYERS'));
                        } else if (controller.model.uploadSummary.existingNotFailedPlayers === 0) {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.DEFAULT_AMOUNT_NOT_SPECIFIED'));
                        }
                    }
                }

                controller.back = function() {
                    controller.init('history');
                }

                controller.addAction = function() {
                    controller.init('action');
                    controller.getUploadStatus();
                    controller.createCheckedTable();
                }

                controller.createCheckedTable = function() {
                    var checkedBaseUrl = "services/service-user-mass-action/backoffice/" + controller.model.domainName + "/" + controller.model.action.uploadType + "/table?id=" + controller.model.action.id;
                    var dtCheckedTableOptions = DTOptionsBuilder.newOptions().withOption('bFilter', false).withOption('createdRow', function(row, data, dataIndex) {
                        // Recompiling so we can bind Angular directive to the DT
                        $compile(angular.element(row).contents())($scope);
                    });
                    controller.checkedTable = controller.getCheckedBuilder()
                        .options(
                            {
                                url: checkedBaseUrl,
                                type: 'GET',
                                data: function (d) {}
                            },
                            null,
                            dtCheckedTableOptions,
                            null).build();

                    controller.refreshCheckedTable = function () {
                        if (controller.checkedTable.instance) {
                            controller.checkedTable.instance.reloadData(function () {
                            }, false);
                        } else {
                            controller.createCheckedTable();
                        }
                    }
                }

                controller.openAction = function(id) {
                    controller.init('action');
                    controller.model.action = controller.actions[id];
                    controller.getUploadStatus();
                    controller.createCheckedTable();
                }

                controller.actions = [];
                controller.getHistoryCheckedBuilder = function() {
                    return $dt.builder()
                        .column($dt.columnformatdatetime('uploadDate').withTitle($translate('UI_NETWORK_ADMIN.MASS_UPLOAD_HISTORY.TABLE.UPLOAD_DATE')))
                        .column($dt.column('uploadStatus').withTitle($translate('UI_NETWORK_ADMIN.MASS_UPLOAD_HISTORY.TABLE.UPLOAD_STATUS')))
                        .column($dt.column('recordsFound').withTitle($translate('UI_NETWORK_ADMIN.MASS_UPLOAD_HISTORY.TABLE.RECORDS_FOUND')))
                        .column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                            controller.actions[row.id] = data;
                            return '<button class="btn btn-primary" ng-click="controller.openAction(' + row.id + ')"><i class="fa fa-pencil-square-o"></i></button>';
                        }));
                }

                var massActionHistoryTableOptionsBaseUrl = "services/service-user-mass-action/backoffice/" + controller.model.domainName + "/" + controller.model.action.uploadType + "/history/table";
                var dtMassActionHistoryTableOptions = DTOptionsBuilder.newOptions().withOption('bFilter', false).withOption('order', [[0, 'desc']]).withOption('createdRow', function(row, data, dataIndex) {
                    // Recompiling so we can bind Angular directive to the DT
                    $compile(angular.element(row).contents())($scope);
                });

                controller.userMassActionHistoryTable = controller.getHistoryCheckedBuilder()
                    .options(
                        {
                            url: massActionHistoryTableOptionsBaseUrl,
                            type: 'GET',
                            data: function (d) {}
                        },
                        null,
                        dtMassActionHistoryTableOptions,
                        null).build();

                controller.refreshHistoryTable = function () {
                    controller.userMassActionHistoryTable.instance.reloadData(function () {
                    }, false);
                }
            }
        ]
    }
});
