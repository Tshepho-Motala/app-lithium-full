'use strict';

angular.module('lithium').directive('massGenericBonuses', function() {
    return {
        templateUrl:'scripts/directives/bonuses/generic/massgenericbonuses.html',
        scope: {
            data: '='
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$dt', '$translate', 'rest-user-mass-action', '$scope', 'DTOptionsBuilder', 'notify', '$filter', '$compile', '$state',
            function($dt, $translate, userMassActionRest, $scope, DTOptionsBuilder, notify, $filter, $compile, $state) {

                var controller = this;
                controller.start = true;

                controller.selectFile = function (file) {
                    controller.selectedFile = null;
                    controller.model.progress = 0;

                    if (file != null && controller.model.bonusCode !== undefined  && controller.model.bonusCode !== '') {
                        controller.selectedFile = file;
                        controller.model.fileName = controller.selectedFile.name;
                        controller.upload();
                    } else {
                        if (file != null && (controller.model.bonusCode === undefined || controller.model.bonusCode === ''))  {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.NO_BONUS_CODE_SELECTED'));
                        }
                    }
                };

                controller.init = function(view, actionData) {
                    const action = actionData || {
                        id: -1,
                        uploadType: $scope.data.tabName
                    }

                    controller.model = {
                        action,
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
                        progress: 0,
                        uploadSummary: {}
                    }
                }
                controller.init('history');

                controller.getUploadStatus = async function() {
                    if (controller.model.action.id === -1) {
                        controller.model.bonusCode = null;
                        controller.model.defaultAmount = 0;
                        controller.model.bonusDescription = '';
                        controller.model.allowDuplicates = false;
                        controller.model.fileName = '';
                        controller.waitForProcessingStart = false;
                        controller.start = true;
                        controller.uploading = false;
                        controller.failed = false;
                        controller.done = false;

                    } else {

                        // This gets the current "actions" data
                        const fileUploadInfo = await controller.getFileUploadInfoAsync()
                        if(!fileUploadInfo || !fileUploadInfo.id) {
                            return
                        }
                        controller.model.action = fileUploadInfo

                        await controller.delayCreateCheckedTable();

                        //When you are resuming a state, prepopulate the bonus code, default bonus amount and description
                        controller.model.bonusCode = fileUploadInfo.massActionMeta.bonusCode;
                        controller.model.defaultAmount = fileUploadInfo.massActionMeta.defaultBonusAmount;
                        controller.model.bonusDescription = fileUploadInfo.massActionMeta.bonusDescription;
                        controller.model.allowDuplicates = fileUploadInfo.massActionMeta.allowDuplicates;
                        controller.model.fileName = fileUploadInfo.file == null ? '' : fileUploadInfo.file.fileName;

                        // Update properties
                        controller.resetProperties()

                        if (fileUploadInfo.uploadStatus === 'UPLOADED') {
                            controller.uploading = true;
                            await controller.getUploadSummaryAsync();
                            await controller.monitorBonusCsvFileUploadProgress('user-verification');
                        } else if (fileUploadInfo.uploadStatus === 'CHECKING') {
                            controller.checking = true;
                            await controller.getUploadSummaryAsync();
                            await controller.monitorBonusCsvFileUploadProgress('user-verification');
                        } else if (fileUploadInfo.uploadStatus === 'CHECKED') {
                            controller.model.progress = 100;
                            controller.checked = true;
                            controller.processBtn = true;
                            await controller.getUploadSummaryAsync();
                        } else if (fileUploadInfo.uploadStatus === 'PROCESSING') {
                            controller.processing = true;
                            controller.waitForProcessingStart = true;
                            await controller.getUploadSummaryAsync();
                            await controller.monitorBonusCsvFileUploadProgress('bonus-grant');
                        } else if (fileUploadInfo.uploadStatus === 'DONE') {
                            controller.checking = true;
                            controller.done = true;
                            controller.model.progress = 100;
                            await controller.getUploadSummaryAsync();
                        } else if (fileUploadInfo.uploadStatus === 'FAILED_STAGE_1' || fileUploadInfo.uploadStatus === 'FAILED_STAGE_2') {
                            controller.checking = true;
                            controller.failed = true;
                            controller.model.bonusCode = '';
                            controller.model.defaultAmount = 0;
                            controller.model.bonusDescription = '';
                            controller.model.allowDuplicates = false;
                            controller.model.fileName = '';
                            await controller.getUploadSummaryAsync();
                        }
                    }
                }

                /**
                 * Gets only the summary of the upload to display at the bottom of the table
                 */
                controller.getUploadSummaryAsync = async function() {
                    const response = await userMassActionRest.getFileUploadSummary(controller.model)
                    controller.model.uploadSummary = response;
                }

                /**
                 * Gets all the data for this action
                 */
                controller.getFileUploadInfoAsync = async function () {
                    const query = await userMassActionRest.getFileUploadStatus(controller.model)
                    return query.plain()
                }

                controller.resetProperties = function () {
                    controller.start = false;
                    controller.uploading = false;
                    controller.checked = false;
                    controller.processing = false;
                    controller.processBtn = false;
                    controller.waitForProcessingStart = false;
                    controller.failed = false;
                    controller.done = false;
                }

                controller.upload = async function() {
                    const response = await userMassActionRest.uploadCsv(controller.model, controller.selectedFile)

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
                        notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.FREESPIN_BONUSES.ERRORS.FILE_UPLOAD_FAILED'));
                    } else {
                        await controller.getUploadStatus();
                    }
                }

                controller.monitorBonusCsvFileUploadProgress = async function(stageName) {
                    const response = await userMassActionRest.monitorFileUploadProgress(controller.model, stageName)
                    controller.model.progress = response.percentile;

                    if (!controller.start && (response.uploadStatus === 'FAILED_STAGE_1' || response.uploadStatus === 'FAILED_STAGE_2')) {
                        notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.FREESPIN_BONUSES.ERRORS.FILE_UPLOAD_FAILED'));
                        controller.failed = true;
                    }

                    if (controller.uploading && controller.model.progress > 0) {
                        controller.checking = true
                        controller.uploading = false;
                    } else if (controller.processing && controller.model.progress > 0) {
                        controller.waitForProcessingStart = false;
                    }
                    if(controller.model.progress === undefined || controller.model.progress === 0 || controller.model.progress < 100) {
                        if (controller.model.action.id === -1) { return; } // When the user clicks the back button to history view

                        // Do a recheck after a timeout to prevent spamming the server
                        setTimeout(async function () {
                            if (controller.processing) {
                                await controller.getUploadStatus();
                                await controller.refreshCheckedTable();
                            }
                            controller.monitorBonusCsvFileUploadProgress(stageName);
                        }, 1000);

                    } else if (controller.model.progress === 100) {
                        // When our progress is at 100, do a full analysis of the upload to get the
                        // most accurate data
                        await controller.getUploadStatus();
                    } else {
                        await controller.getUploadSummaryAsync();
                        if (stageName === 'user-verification') {
                            controller.checked = true;
                            controller.processing = false;
                            controller.processBtn = true;
                        } else {
                            controller.checked = false;
                            controller.processing = true;
                        }

                        return;
                    }
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
                    }, {
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

                controller.removeRecord = async function(id) {
                    await userMassActionRest.removeFileDataRecord(controller.model, controller.deleted[id].rowNumber)
                    await controller.refreshCheckedTable();
                    await controller.getUploadSummaryAsync();
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

                controller.submit = async function() {
                    const hasExistingPlayers = controller.model.uploadSummary?.existingPlayers > 0 || false
                    if (controller.model.bonusCode !== undefined && hasExistingPlayers && controller.model.uploadSummary.existingNotFailedPlayers > 0) {
                        controller.processing = true;
                        controller.model.progress = 0;
                        try {
                            await userMassActionRest.processFileUpload(controller.model)
                            await controller.getUploadStatus();
                        } catch(e) {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.ERRORS.INVALID_AMOUNT'))
                        }

                    } else {
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
                    controller.resetProperties()
                    controller.checkedTable = null
                    controller.init('history');
                }

                controller.addAction = async function() {
                    controller.init('action');
                    await controller.getUploadStatus();
                }

                controller.openAction = async function(id) {
                    controller.init('action');
                    controller.model.action = controller.actions[id];
                    controller.getUploadStatus();
                }

                controller.delayCreateCheckedTable = function() {
                    return new Promise(function (res) {
                        setTimeout(function() {
                            controller.createCheckedTable()
                            res()
                        }, 500);
                    })
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
                                data: function () {}
                            },
                            null,
                            dtCheckedTableOptions,
                            null).build();
                }

                controller.refreshCheckedTable = function () {
                    return new Promise(async function (res, rej) {
                        // Ensure the existence of not only instance,
                        // but the reloadData function as well (which sometimes gets lost)
                        // after a status refresh.
                        const hasInstance = controller.checkedTable.instance
                            && controller.checkedTable.instance.reloadData

                        if (hasInstance) {
                            controller.checkedTable.instance.reloadData(function() {
                                res()
                            }, false);
                        } else {
                            controller.createCheckedTable()
                            res()
                        }
                    })
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
