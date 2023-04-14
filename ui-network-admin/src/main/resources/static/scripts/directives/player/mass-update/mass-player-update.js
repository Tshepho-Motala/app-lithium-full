'use strict';

angular.module('lithium').directive('massPlayerUpdate', function() {
    return {
        templateUrl:'scripts/directives/player/mass-update/mass-player-update.html',
        scope: {
            data: '='
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$dt', '$translate', 'rest-user-mass-action', '$scope', 'DTOptionsBuilder', 'notify', '$filter', '$compile', '$state', 'userFields', 'accountingFields', 'ChangelogsRest', 'RestrictionsRest',
            function($dt, $translate, userMassActionRest, $scope, DTOptionsBuilder, notify, $filter, $compile, $state, userFields, accountingFields, changelogsRest, restrictionsRest) {

                var controller = this;

                controller.init = function (view) {
                    controller.start = true;
                    controller.done = false;
                    controller.model = {};

                    controller.model = {
                        action: {
                            id: -1,
                            uploadType: "PLAYER_INFO"
                        },
                        view: view,
                        statuses: $scope.data.statuses,
                        statusReasons: $scope.data.statusReasons,
                        excludeStatusReasons: $scope.data.excludeStatusReasons,
                        tags: $scope.data.tags,
                        domainName: $scope.data.domainName,
                        domainSettings: $scope.data.domainSettings,
                        adjustment: 0,
                        fileName: '',
                        progress: 0,
                        options: {
                            changeStatus: true,
                            changeVerificationStatus: true,
                            changeBiometricsStatus: true,
                            processAccessRule: true,
                            masAsTestPlayer: true,
                            playerTags: true,
                            addNote: true,
                            balanceAdjust: true,
                            playerRestrictions: true
                        },

                        restrictions: null,
                        restrictionReason: ''
                    };

                    controller.validatedRecordsBaseUrl = () => {
                        return "services/service-user-mass-action/backoffice/" + controller.model.domainName + "/" + controller.model.action.uploadType + "/table?id=" + controller.model.action.id;
                    }

                    controller.expand = function() {
                        controller.model.options = {
                            changeStatus: true,
                            changeVerificationStatus: true,
                            changeBiometricsStatus: true,
                            processAccessRule: true,
                            masAsTestPlayer: true,
                            playerTags: true,
                            addNote: true,
                            balanceAdjust: true,
                            playerRestrictions: true
                        }
                    }

                    controller.collapse = function() {
                        controller.model.options = {
                            changeStatus: false,
                            changeVerificationStatus: false,
                            changeBiometricsStatus: false,
                            processAccessRule: false,
                            masAsTestPlayer: false,
                            playerTags: false,
                            addNote: false,
                            balanceAdjust: false,
                            playerRestrictions: false
                        }
                    }
                    controller.expand();


                }
                controller.init('history')

                controller.fields = {
                    playerRestrictionFields: [
                        userFields.radio('restrictionAction','', 'place', [
                            { value: $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_RESTRICTIONS.RADIO.PLACE.NAME"), key: 'place' },
                            { value: $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_RESTRICTIONS.RADIO.LIFT.NAME"), key: 'lift' }
                        ])
                    ],
                    status: [
                        userFields.status,
                        userFields.statusReason(controller.model.status, controller.model.domainName, controller.model.statuses, 'name', controller.model.excludeStatusReasons),
                        userFields.comment("statusComment",
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION"),
                            false)
                    ],
                    verificationStatus: [
                        userFields.verificationStatus,
                        userFields.ageVerified,
                        userFields.addressVerified,
                        userFields.comment("verificationStatusComment",
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATION_STATUS.COMMENT.NAME"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATION_STATUS.COMMENT.PLACEHOLDER"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATION_STATUS.COMMENT.DESCRIPTION"),
                            true)
                    ],
                    biometricsStatus: [
                        userFields.biometricsStatus,
                        userFields.comment("biometricsStatusComment",
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION"),
                            true)
                    ],
                    accessRule: [
                        userFields.accessRule,
                    ],
                    testPlayer: [
                        userFields.testPlayerSelect
                    ],
                    tagnames: [
                        userFields.radio("tagOptions", '', 'add', [
                            { value: $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.ADD_TAG.RADIO_LABEL"), key: 'add' },
                            { value: $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REPLACE_TAG.RADIO_LABEL"), key: 'replace' },
                            { value: $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REMOVE_ALL_TAGS.RADIO_LABEL"), key: 'remove-all' },
                            { value: $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REMOVE_SPECIFIC_TAGS.RADIO_LABEL"), key: 'remove' }
                        ])
                    ],
                    note: userFields.addNote,
                    adjust: accountingFields.balanceAdjust(controller.model.domainName, {
                        comment: 'adjustComment'
                    }),
                }

                controller.fields.status[0].templateOptions.onChange =  function() {
                    controller.statusReason = undefined;
                    controller.fields.status[1] = userFields.statusReason(controller.model.status, controller.model.domainName, controller.model.statuses, 'name', controller.model.excludeStatusReasons);
                }

                controller.fields.verificationStatus[0].templateOptions.onChange = function () {
                    if (controller.model.verificationStatus !== null && controller.model.verificationStatus !== 2 && controller.model.verificationStatus !== 3) {
                        controller.fields.verificationStatus[1].templateOptions.disabled = true;
                        controller.fields.verificationStatus[2].templateOptions.disabled = true;
                    } else if (controller.model.verificationStatus === null) {
                        controller.fields.verificationStatus[1].templateOptions.disabled = undefined;
                        controller.fields.verificationStatus[2].templateOptions.disabled = undefined;
                    }
                }

                controller.fields.verificationStatus[1].templateOptions.onChange = function () {
                    if (controller.model.ageVerified !== null) {
                        controller.fields.verificationStatus[0].templateOptions.disabled = true;
                    } else if (controller.model.addressVerified === null) {
                        controller.fields.verificationStatus[0].templateOptions.disabled = undefined;
                    }
                }

                controller.fields.verificationStatus[2].templateOptions.onChange = function () {
                    if (controller.model.addressVerified !== null) {
                        controller.fields.verificationStatus[0].templateOptions.disabled = true;
                    } else if (controller.model.ageVerified === null) {
                        controller.fields.verificationStatus[0].templateOptions.disabled = undefined;
                    }
                }

                controller.getRestrictionFieldsOrdered = () => {

                    return controller.fields.playerRestrictionFields.sort(function compare( a, b ) {
                            if ( a.templateOptions.order < b.templateOptions.order ){
                                return -1;
                            }
                            if ( a.templateOptions.order > b.templateOptions.order ){
                                return 1;
                            }
                            return 0;
                        }
                    )
                }
                controller.getSubordinates = (count) => {
                    return angular.range(1, count).map(altCount => ({ label: $translate.instant("UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.SUBORDINATE.MESSAGE."+altCount), value: altCount  }))
                }
                controller.addSubordinateControl = () => {
                    controller.fields.playerRestrictionFields.push(
                        {
                            className: 'col-xs-12',
                            key: "subordinate",
                            type: "ui-select-single",
                            templateOptions: {
                                label: "",
                                description: "",
                                required: true,
                                optionsAttr: 'bs-options',
                                valueProp: 'value',
                                labelProp: 'label',
                                "ngOptions": 'ui-options',
                                placeholder: '',
                                options: [],
                                order:2
                            },
                            expressionProperties: {
                                'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTION_SUBORDINATE.NAME" | translate',
                                'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTION_SUBORDINATE.DESCRIPTION" | translate'
                            },

                        })
                }
                controller.isInterventionCompsBlock = (domainRestrictionSet) => {

                    if(!domainRestrictionSet) {
                        return false;
                    }

                    let name = ''
                    if (domainRestrictionSet.name && typeof domainRestrictionSet.name === 'string') {
                        name = domainRestrictionSet.name.toUpperCase().replaceAll(new RegExp(/\s+/,"gm"), "_");
                    }
                    return name === "INTERVENTION_COMPS_BLOCK";
                }

                $scope.$watch(() => controller.model.restrictionAction, (newVal, oldValue) => {
                    const restrictions = [controller.fields.playerRestrictionFields[0]]

                    if(typeof newVal === 'string' && newVal.length > 0) {
                        const label = newVal.toUpperCase()
                        restrictions.push(userFields.restrictionMultiSelect('restrictions',
                            $translate.instant(`UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_RESTRICTIONS.OPTIONS.${label}.NAME`),
                            $translate.instant(`UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_RESTRICTIONS.OPTIONS.${label}.PLACEHOLDER`),
                            $translate.instant(`UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_RESTRICTIONS.OPTIONS.${label}.DESCRIPTION`),
                            controller.model.domainName));
                    }

                    restrictions.push(userFields.comment("restrictionReason",
                        $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.NOTE.COMMENT.NAME"),
                        $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.NOTE.COMMENT.PLACEHOLDER"),
                        '',true,3))

                    controller.fields.playerRestrictionFields = restrictions;
                });

                $scope.$watch(() => controller.model.restrictions,(newVal, oldValue) => {
                    const wantedVal = newVal ? newVal[0] || newVal:null; //TODO Future updates we need to ensure we check the entire array when change to multiple restrictions. Currently we only cter for one.
                    controller.fields.playerRestrictionFields = controller.fields.playerRestrictionFields.filter(f => f.templateOptions.order !== 2);
                   if(!angular.isUndefinedOrNull(wantedVal)) {
                        if(controller.isInterventionCompsBlock(wantedVal)) {
                            controller.addSubordinateControl();
                            controller.fields.playerRestrictionFields[3].templateOptions.options = controller.getSubordinates(wantedVal.altMessageCount);
                        }
                   }
                });

                $scope.$watch(function() { return controller.model.tagOptions }, function(newValue, oldValue) {
                    let tagnames = [controller.fields.tagnames[0]];
                    if (newValue === 'add') {
                        tagnames.push(userFields.tagnameMultiSelect('add',
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.ADD_TAG.ADD.NAME"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.ADD_TAG.ADD.PLACEHOLDER"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.ADD_TAG.ADD.DESCRIPTION"),
                            controller.model.domainName));
                    } else if (newValue === 'replace') {
                        tagnames.push(userFields.tagnameSingleSelect('replaceFrom',
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REPLACE_TAG.FROM.NAME"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REPLACE_TAG.FROM.PLACEHOLDER"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REPLACE_TAG.FROM.DESCRIPTION"),
                            controller.model.domainName));
                        tagnames.push(userFields.tagnameSingleSelect('replaceTo',
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REPLACE_TAG.TO.NAME"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REPLACE_TAG.TO.PLACEHOLDER"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REPLACE_TAG.TO.DESCRIPTION"),
                            controller.model.domainName));
                    } else if (newValue === 'remove-all') {
                        //do nothing
                    } else if (newValue === 'remove') {
                        tagnames.push(userFields.tagnameMultiSelect('remove',
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REMOVE_SPECIFIC_TAGS.REMOVE.NAME"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REMOVE_SPECIFIC_TAGS.REMOVE.PLACEHOLDER"),
                            $translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.PLAYER_TAGS.OPTIONS.REMOVE_SPECIFIC_TAGS.REMOVE.DESCRIPTION"),
                            controller.model.domainName));
                    }
                    controller.fields.tagnames = tagnames;
                });

                $scope.$watch(function() { return controller.model.category;}, function ($viewValue, $model, $scope) {
                    if ($viewValue) {
                        changelogsRest.subCategories($viewValue).then(function (response) {
                            if (response.plain().length === 0) {
                                controller.fields.note[1].templateOptions.disabled = true;
                            } else {
                                controller.fields.note[1].templateOptions.disabled = undefined;
                                controller.fields.note[1].templateOptions.options = response.plain();
                            }
                        });
                    }
                    controller.model.subCategory = undefined;
                });

                controller.selectFile = function (file) {
                    controller.selectedFile = null;
                    if (file != null) {
                        controller.selectedFile = file;
                        controller.model.fileName = file.name;
                        controller.model.fileSize = controller.fileSize(file.size);
                        controller.upload();
                    }
                };

                controller.getUploadStatus = function() {
                    if (controller.model.action.id === -1) {
                        controller.start = true;
                        controller.uploading = false;
                        controller.checked = false;
                        controller.processing = false;
                        controller.processBtn = false;
                        controller.waitForProcessingStart = false;
                        controller.done = false;
                        controller.failed = false;
                        controller.model.fileName = null;
                        controller.model.fileSize = null;
                        controller.resetActionValidationErrors();
                    } else {
                        userMassActionRest.getFileUploadStatus(controller.model).then(function(response) {
                            controller.model.action = response.plain();

                            if (response !== null && response.id !== null ) {

                                controller.populateActions(response.massActionMeta);
                                controller.model.fileUpload = response;
                                controller.model.fileName = response.file == null ? '' : response.file.fileName;
                                //controller.model.fileSize = FIXME
                                if (controller.model.action.uploadStatus === 'UPLOADED') {
                                    controller.start = false;
                                    controller.uploading = true;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.done = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.monitorPlayerCsvFileUploadProgress('user-verification');
                                } else if (controller.model.action.uploadStatus === 'UPLOADING') {
                                    controller.start = false;
                                    controller.uploading = true;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.done = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.monitorPlayerCsvFileUploadProgress('user-verification');
                                } else if (controller.model.action.uploadStatus === 'CHECKING') {
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
                                    controller.monitorPlayerCsvFileUploadProgress('user-verification');
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
                                    controller.checking = false
                                    controller.getUploadSummary();
                                    controller.refreshCheckedTable();
                                } else if (controller.model.action.uploadStatus === 'PROCESSING') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.checked = false;
                                    controller.processing = true;
                                    controller.done = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = true;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.monitorPlayerCsvFileUploadProgress('player-update');
                                } else if (controller.model.action.uploadStatus === 'DONE') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.checking = false;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.done = true;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.model.progress = 100;
                                    controller.failed = false;
                                    controller.getUploadSummary();
                                    controller.refreshCheckedTable();
                                } else if (controller.model.action.uploadStatus === 'FAILED_STAGE_1' || controller.model.fileUpload.uploadStatus === 'FAILED_STAGE_2') {
                                    controller.start = false;
                                    controller.uploading = false;
                                    controller.checking = true;
                                    controller.checked = false;
                                    controller.processing = false;
                                    controller.done = false;
                                    controller.processBtn = false;
                                    controller.waitForProcessingStart = false;
                                    controller.failed = true;
                                    controller.model.fileName = '';
                                    controller.getUploadSummary();
                                }
                            }
                        });
                    }
                }

                controller.populateActions = function(massActionMeta) {
                    if (massActionMeta) {
                        for (let action of massActionMeta.actions) {
                            switch (action.name) {
                                case "CHANGE_STATUS": {
                                    let status = $scope.data.statuses.find(status => status.name === massActionMeta.status);
                                    let statusReason = $scope.data.statusReasons.find(statusReason => statusReason.name === massActionMeta.statusReason);

                                    controller.model.status = status ? status.id : status;
                                    controller.fields.status[1] = userFields.statusReason(controller.model.status, controller.model.domainName, controller.model.statuses, 'name', []);
                                    controller.model.statusReason = statusReason ? statusReason.name : statusReason;
                                    controller.model.statusComment = massActionMeta.statusComment;
                                    break;
                                }
                                case "CHANGE_VERIFICATION_STATUS": {
                                    controller.model.verificationStatus = massActionMeta.verificationStatusId;
                                    controller.model.ageVerified = massActionMeta.ageVerified;
                                    controller.model.addressVerified = massActionMeta.addressVerified;
                                    controller.model.verificationStatusComment = massActionMeta.verificationStatusComment;
                                    break;
                                }
                                case "CHANGE_BIOMETRICS_STATUS": {
                                    controller.model.biometricsStatus = massActionMeta.biometricsStatus;
                                    controller.model.biometricsStatusComment = massActionMeta.biometricsStatusComment;
                                    break;
                                }
                                case "MARK_AS_TEST_PLAYER": {
                                    controller.model.testPlayer = massActionMeta.testPlayer;
                                    break;
                                }
                                case "PROCESS_ACCESS_RULE": {
                                    controller.model.accessRule =  massActionMeta.accessRule;
                                    break;
                                }
                                case "ADD_PLAYER_TAGS": {
                                    let tagIds = massActionMeta.addTags ? massActionMeta.addTags.split(",").map(item => item.trim()) : [];
                                    controller.model.tagOptions = 'add';
                                    controller.model.add = [];
                                    for (let tagId of tagIds) {
                                        let obj = $scope.data.tags.find(tag => tag.id == tagId);
                                        if (obj) {controller.model.add.push(obj);}
                                    }
                                    break;
                                }
                                case "REPLACE_PLAYER_TAGS": {
                                    controller.model.tagOptions = 'replace';
                                    controller.model.replaceFrom = massActionMeta.replaceTagFrom;
                                    controller.model.replaceTo = massActionMeta.replaceTagTo;
                                    break;
                                }
                                case "REMOVE_ALL_PLAYER_TAGS": {
                                    controller.model.tagOptions = 'remove-all';
                                    break;
                                }
                                case "REMOVE_PLAYER_TAGS": {
                                    let tagIds = massActionMeta.removeTags ? massActionMeta.removeTags.split(",").map(item => item.trim()) : [];
                                    controller.model.tagOptions = 'remove';
                                    controller.model.remove = [];
                                    for (let tagId of tagIds) {
                                        let obj = $scope.data.tags.find(tag => tag.id == tagId);
                                        if (obj) {controller.model.remove.push(obj);}
                                    }
                                    break;
                                }
                                case "ADD_NOTE": {
                                    controller.model.category = massActionMeta.noteCategory;
                                    controller.model.subCategory = massActionMeta.noteSubCategory;
                                    controller.model.priority = massActionMeta.notePriority;
                                    controller.model.text = massActionMeta.noteComment;
                                    break;
                                }
                                case "BALANCE_ADJUSTMENT": {
                                    controller.model.trantypeacct = massActionMeta.adjustmentTransactionTypeCode;
                                    controller.model.adjustComment = massActionMeta.adjustmentComment;
                                    break;
                                }
                            }
                        }
                    }
                }

                controller.getUploadSummary = function() {
                    userMassActionRest.getFileUploadSummary(controller.model).then(function(response) {
                        controller.model.uploadSummary = response;
                    });
                }

                controller.upload = function() {
                    userMassActionRest.uploadCsv(controller.model, controller.selectedFile).then(function(response) {

                        controller.model.action = response.plain();
                        if (response.uploadStatus === 'FAILED_STAGE_1' || response.uploadStatus === 'FAILED_STAGE_2') {
                            controller.uploading = false;
                            controller.checking = false;
                            controller.checked = false;
                            controller.processing = false;
                            controller.processBtn = false;
                            controller.model.fileName = '';
                            controller.failed = true;
                            notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ERRORS.FILE_UPLOAD_FAILED'));
                        } else {
                            controller.getUploadStatus();
                        }
                    });
                }

                controller.monitorPlayerCsvFileUploadProgress = function(stageName) {
                    userMassActionRest.monitorFileUploadProgress(controller.model, stageName).then(function(response) {
                        controller.model.progress = response.percentile;
                        if (!controller.start && (response.uploadStatus === 'FAILED_STAGE_1' || response.uploadStatus === 'FAILED_STAGE_2')) {
                            notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ERRORS.FILE_UPLOAD_FAILED'));
                            controller.failed = true;
                        }
                        if (controller.uploading && controller.model.progress > 0) {
                            controller.checking = true
                            controller.uploading = false;
                        } else if (controller.processing && controller.model.progress > 0) {
                            controller.waitForProcessingStart = false;
                        }
                        if(controller.model.progress === undefined || controller.model.progress === 0 || controller.model.progress < 100) {
                            setTimeout(function(){
                                if (controller.model.action.id === -1) { return; } // When the user clicks the back button to history view
                                if (controller.processing) { 
                                    controller.refreshCheckedTable();
                                 }

                                controller.monitorPlayerCsvFileUploadProgress(stageName);
                            }, 1000);
                        } else {
                            if (stageName === 'user-verification') {
                                controller.checked = true;
                                controller.processing = false;
                                controller.processBtn = true;
                            } else {
                                controller.checked = false;
                                controller.processing = true;
                            }

                            controller.getUploadStatus()
                            return;
                        }
                    });
                }

                controller.paintCell = function (data, value) {
                    let errorDetected = false;
                    if (data.dataError != null || !data.userStatus) {
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
                    if (data.uploadStatus === 'FAILED_STAGE_1' || data.uploadStatus === 'FAILED_STAGE_2' || data.userStatus === 'UNKNOWN') {
                        return '<div class="limited"><i class="fa fa-times fa-lg" style="color:red"></i></div>';
                    } else if (data.uploadStatus === 'DONE') {
                        return '<div class="limited"><i class="fa fa-check fa-lg" style="color:green"></i></div>';
                    } else if (controller.processing){
                        if (data.uploadStatus === 'CHECKED') {
                            if (!controller.model.allowDuplicates) {
                                if (data.duplicate) {
                                    return '<div class="limited"><i class="fa fa-fast-forward fa-lg"></i></div>';
                                } else {
                                    return '<div class="limited"><i class="fa fa-spinner fa-pulse fa-spin"></i></div>';
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

                controller.getCheckedBuilder = function () {
                    var builder = $dt.builder()
                        .column($dt.columnWithClass(function (data) {
                            return controller.paintCell(data, data.rowNumber)
                        }).withTitle($translate('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.TABLE.ROW_NUMBER.LABEL')))

                        .column($dt.linkscolumn("",
                            [{
                                permission: "MASS_PLAYER_UPDATE_VIEW",
                                permissionType: "any",
                                permissionDomain: function (data) {
                                    return controller.model.domainName;
                                },
                                title: function (data) {
                                    return data.uploadedPlayerId;
                                },
                                href: function (data) {
                                    return $state.href("dashboard.players.player.summary", {
                                        id: data.uploadedPlayerId,
                                        domainName: controller.model.domainName
                                    })
                                }
                            }]).withTitle($translate('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.TABLE.PLAYER_ID.LABEL')))

                        .column($dt.labelcolumn($translate('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.TABLE.ACCOUNT_STATUS.LABEL'),
                            [{
                                text: function (data) {
                                    return data.userStatus ? data.userStatus : 'UNKNOWN';
                                },
                                lclass: function (data) {
                                    let errorDetected = false;
                                    if (data.dataError != null || !data.userStatus) {
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
                                    return data.userStatusReason ? data.userStatusReason : '';
                                },
                                lclass: function (data) {
                                    if (data.userStatusReason === undefined || data.userStatusReason == null || data.userStatusReason === '')
                                        return "default hide";
                                    else
                                        return "default label-columns";
                                }
                            }]))

                    if (controller.model.action.massActionMeta != null && controller.model.action.massActionMeta.accessRule != null) {
                        builder = builder.column($dt.labelcolumn($translate('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ACTIONS.PROCESS_ACCESS_RULE.TABLE_LABEL'),
                            [{
                                text: function (data) {
                                    return data.ruleSetResultSuccess === true ? "SUCCESS" : 'FAIL';
                                },
                                lclass: function (data) {

                                    if (data.ruleSetResultSuccess === undefined || data.ruleSetResultSuccess == null) {
                                        return "default hide";
                                    }
                                    if (data.ruleSetResultSuccess) {
                                        return "default label-columns bg-green";
                                    } else
                                        return "default label-columns bg-red";
                                }
                            }]))
                            .column($dt.emptycolumn('').renderWith(function(data) {
                                if (data.ruleSetResultMessage === undefined || data.ruleSetResultMessage == null) {
                                    return "";
                                } else
                                    return data.ruleSetResultMessage
                            }))
                    }

                    builder = builder
                        .column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                            return controller.processingStatus(data)
                        }))
                        .column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                            if (data.comment === undefined || data.comment == null) {
                                return "";
                            } else {
                                return data.comment
                            }
                        }))
                        .column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                        controller.deleted[row.id] = data;
                        if (controller.checked)
                            return '<button class="btn btn-danger" ng-click="controller.removeRecord(' + row.id + ')"><i class="fa fa-trash-o"></i></button>';
                        else
                            return '<button class="btn btn-danger" ng-click="controller.removeRecord(' + row.id + ')" disabled><i class="fa fa-trash-o"></i></button>';
                    }));
                    return builder
                }

                controller.submit = function() {
                    if (controller.hasValidFormActions()) {
                        controller.processing = true;
                        controller.model.progress = 0;
                        userMassActionRest.processFileUpload(controller.model).then(function(response) {
                            console.debug("Processing file -> {}", response);
                            controller.getUploadStatus();
                        }).catch(function(error) {
                            console.error("Error : -> {}", error);
                            notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ERRORS.UNABLE_TO_PROCESS_UPLOAD'))
                        });
                    } else {
                        notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ERRORS.ACTIONS_VALIDATION'))
                    }
                }

                controller.resetActionValidationErrors = function() {
                    if (controller.fields.note[0].validation) {
                        controller.fields.note[0].validation.errorExistsAndShouldBeVisible = false;
                        controller.fields.note[0].validation.show = false;
                    }
                    if (controller.fields.note[2].validation) {
                        controller.fields.note[2].validation.errorExistsAndShouldBeVisible = false;
                        controller.fields.note[2].validation.show = false;
                    }
                    if (controller.fields.adjust[0].validation) {
                        controller.fields.adjust[0].validation.errorExistsAndShouldBeVisible = false;
                        controller.fields.adjust[0].validation.show = false;
                    }
                    if (controller.fields.adjust[1].validation) {
                        controller.fields.adjust[1].validation.errorExistsAndShouldBeVisible = false;
                        controller.fields.adjust[1].validation.show = false;
                    }
                    if (controller.fields.verificationStatus[3].validation) {
                        controller.fields.verificationStatus[3].validation.errorExistsAndShouldBeVisible = false;
                        controller.fields.verificationStatus[3].validation.show = false;
                    }
                }

                controller.hasValidFormActions = function() {
                    let valid = true;
                    controller.resetActionValidationErrors();
                    controller.actionsRequest = userMassActionRest.buildActionsRequest(controller.model);

                    for (let actionsType = 0; actionsType < controller.actionsRequest.actions.length; actionsType++) {
                        switch (controller.actionsRequest.actions[actionsType]) {
                            case "CHANGE_STATUS": {
                                if (!controller.fields.status[1].hide && !controller.model.statusReason) {
                                    controller.fields.status[1].validation.show = true;
                                    valid = false;
                                }
                                break;
                            }
                            case "CHANGE_VERIFICATION_STATUS": {
                                if(!controller.model.verificationStatusComment) {
                                    controller.fields.verificationStatus[3].validation.errorExistsAndShouldBeVisible = true;
                                    controller.fields.verificationStatus[3].validation.show = true;
                                    valid = false
                                }
                                if( controller.model.verificationStatus === 3) {
                                    valid = false
                                }
                                break;
                            }
                            case "CHANGE_BIOMETRICS_STATUS": {
                                if(!controller.model.biometricsStatusComment) {
                                    controller.fields.biometricsStatus[1].validation.errorExistsAndShouldBeVisible = true;
                                    controller.fields.biometricsStatus[1].validation.show = true;
                                    valid = false
                                }
                                break;
                            }
                            case "ADD_NOTE": {
                                if (!controller.model.category) {
                                    controller.fields.note[0].validation.errorExistsAndShouldBeVisible = true;
                                    controller.fields.note[0].validation.show = true;
                                    valid = false
                                }
                                if (controller.model.priority >= 0) { break; }
                                else {
                                    controller.fields.note[2].validation.errorExistsAndShouldBeVisible = true;
                                    controller.fields.note[2].validation.show = true;
                                    valid = false
                                }
                                break;
                            }
                            case "BALANCE_ADJUSTMENT": {
                                if (!controller.model.trantypeacct) {
                                    controller.fields.adjust[2].validation.errorExistsAndShouldBeVisible = true;
                                    controller.fields.adjust[2].validation.show = true;
                                    valid = false;
                                }
                                if (!controller.model.adjustComment) {
                                    controller.fields.adjust[3].validation.errorExistsAndShouldBeVisible = true;
                                    controller.fields.adjust[3].validation.show = true;
                                    valid = false;
                                }
                                break;
                            }
                            default:;
                        }
                    }
                    return valid;
                }

                controller.fileSize = function(bytes) {
                    //Supports up to MB
                    if (bytes <= 1024) {
                        return bytes + " bytes"
                    } else if ((bytes / 1024) < 1024) {
                        return (bytes / 1024).toFixed(2) + " KB";
                    }
                    return (bytes / (1024*1024)).toFixed(2) + " MB";
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
                    var dtCheckedTableOptions = DTOptionsBuilder.newOptions().withOption('bFilter', false).withOption('createdRow', function(row, data, dataIndex) {
                        // Recompiling so we can bind Angular directive to the DT
                        $compile(angular.element(row).contents())($scope);
                    });

                    controller.checkedTable = controller.getCheckedBuilder()
                        .options(
                            {
                                url: controller.validatedRecordsBaseUrl(),
                                type: 'GET',
                                data: function (d) {}
                            },
                            null,
                            dtCheckedTableOptions,
                            null)
                        .build();
                }

                controller.refreshCheckedTable = function () {
                    if (controller.checkedTable.instance && controller.checkedTable.instance.DataTable !== undefined) {
                        controller.checkedTable.instance.DataTable.ajax.url(controller.validatedRecordsBaseUrl()).load();
                    } else {
                        controller.createCheckedTable();
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

                //FIXME: Need to find a new endpoint that can be used by all
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

                controller.disableSubmit = function() {
                    let {verificationStatus, addressVerified, ageVerified} = controller.model

                    if (addressVerified != null || ageVerified != null) {
                        return false
                    }

                    switch (verificationStatus) {
                        case 3: // if user set EXTERNALLY_VERIFIED
                            return true
                        case 2: // if user set MANUALLY_VERIFIED
                            if(addressVerified || ageVerified) return false // if status verified via address or age
                            return true
                        default:
                            return false // if user doesn't set EXTERNALLY_VERIFIED or MANUALLY_VERIFIED which alone is prohibited
                    }
                }

            }
        ]
    }
});
