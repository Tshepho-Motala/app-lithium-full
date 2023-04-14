'use strict';

angular.module('lithium').directive('globalchangelog', function() {
    return {
        templateUrl: 'scripts/directives/changelog/globalchangelog.html',
        scope: {
            entryhref: '@',
            kyc: '='
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$scope','$compile','notify','$state', '$stateParams','$filter', '$dt', 'DTOptionsBuilder', 'ChangelogsRest', '$translate',
            '$uibModal','$security', '$rootScope', 'UserRest', '$userService',
            function ($scope,$compile, notify, $state, $stateParams, $filter, $dt, DTOptionsBuilder, changelogsRest, $translate, $uibModal, $security,
                      $rootScope, UserRest, $userService
            ) {
                var controller = this;
                controller.domainName = $stateParams.domainName;
                controller.userId = $stateParams.id;
                controller.selectedDomainsDisplay = $translate.instant('UI_NETWORK_ADMIN.PLAYERS.LINKS.OPTIONS.DOMAINS_SELECTED');
                controller.selectedName={};
                let subCategories = {};
                controller.domainNames=[];
                controller.isPlayersTab = function() {
                    return $state.current.name.indexOf('players') !== -1;
                }();
                controller.isKycTab = $scope.kyc ? $scope.kyc : false

                controller.priorityMap =
                    {
                        low: {from: null, to: 33, name: $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LOW')},
                        medium: {from: 34, to: 66, name: $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.MEDIUM') },
                        high: {from: 67, to: null, name: $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.HIGH')}
                    };
                if (!controller.isPlayersTab) {
                    controller.domains = $userService.playerDomainsWithAnyRole(['CHANGELOGS_GLOBAL_VIEW']);
                    const domainNames = []
                    controller.domains.forEach(el=> {
                        domainNames.push(el.name)
                    })
                    controller.domainNames = domainNames ;
                }
                controller.legendCollapsed = true;
                controller.model = {};
                controller.fields = [
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'changeDateRangeStart',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            label: '',
                            required: false,
                            datepickerOptions: {
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function () {
                                controller.fields[1].templateOptions.datepickerOptions.minDate = controller.model.changeDateRangeStart;
                            }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CHANGEDATERANGESTART" | translate'
                        }
                    }, {
                        className: 'col-md-3 col-xs-12',
                        key: 'changeDateRangeEnd',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            label: '',
                            required: false,
                            datepickerOptions: {
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function () {
                                controller.fields[0].templateOptions.datepickerOptions.maxDate = controller.model.changeDateRangeEnd;
                            }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CHANGEDATERANGEEND" | translate'
                        }
                    }, {
                        className: 'col-md-3 col-xs-12',
                        key: 'entities',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            label: "Entities",
                            placeholder: "Select entities...",
                            valueProp: 'name',
                            labelProp: 'name',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: false
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.ENTITIES.LABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.ENTITIES.PLACEHOLDER" | translate'
                        },
                        controller: ['$scope', function ($scope) {
                            changelogsRest.entities().then(function (response) {
                                $scope.to.options = response.plain();
                            });
                        }]
                    }, {
                        className: 'col-md-3 col-xs-12',
                        key: 'types',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            label: "Types",
                            placeholder: "Select types...",
                            valueProp: 'name',
                            labelProp: 'name',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: false
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.TYPES.LABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.TYPES.PLACEHOLDER" | translate'
                        },
                        controller: ['$scope', function ($scope) {
                            changelogsRest.types().then(function (response) {
                                $scope.to.options = response.plain();
                            });
                        }]
                    }, {
                        className: 'col-md-3 col-xs-12',
                        key: 'category',
                        type: 'ui-select-single',
                        templateOptions: {
                            label: "Category",
                            placeholder: "Select categories...",
                            valueProp: 'name',
                            labelProp: 'name',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: false,
                            onChange:  function ($viewValue, $model, $scope) {
                                if ($viewValue){
                                    subCategories = changelogsRest.subCategories($viewValue.name);
                                }
                            },
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
                        className: 'col-md-3 col-xs-12',
                        key: 'subCategory',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            label: "Sub Category",
                            placeholder: "Select sub category...",
                            valueProp: 'name',
                            labelProp: 'name',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: [],
                            required: false
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.LABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.PLACEHOLDER" | translate'
                        },
                        controller: ['$scope', function ($scope) {
                            $scope.$watch(function() { return controller.model.category;}, function(newVal, oldVal) {
                                changelogsRest.subCategories(newVal).then(response=>{
                                    $scope.to.options = response.plain();
                                });
                            })

                        }]
                    },{
                        className: 'col-md-3 col-xs-12',
                        key: 'priority',
                        type: 'ui-select-single',
                        templateOptions : {
                            label: '',
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [
                                {value: 'low', label: controller.priorityMap.low.name},
                                {value: 'medium', label: controller.priorityMap.medium.name},
                                {value: 'high', label: controller.priorityMap.high.name},
                            ]
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LABEL" | translate'
                        }
                    },{
                        className: 'col-md-3 col-xs-12',
                        key: "deleted",
                        type: "checkbox2",
                        templateOptions: {
                            label: '',
                            description: '',
                            placeholder: '',
                            required: false,
                            fontWeight:'bold'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.DELETED.LABEL" | translate'
                        },
                    }
                ];

                controller.formatDate = function (date) {
                    return $filter('date')(date, 'yyyy-MM-dd');
                }

                controller.toggleLegendCollapse = function () {
                    controller.legendCollapsed = !controller.legendCollapsed;
                }

                controller.addNote = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/changelog/addnote.html',
                        controller: 'AddChangeLogNoteModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            domainName: function () {
                                return controller.domainName;
                            },
                            entityId: function () {
                                return controller.userId;
                            },
                            restService: function () {
                                return UserRest;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/changelog/addnote.js']
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function(response) {
                        controller.refresh();
                        notify.success("UI_NETWORK_ADMIN.PLAYER.NOTE.ADD.SUCCESS");
                    });
                };

                controller.resetFilter = function (collapse) {
                    if (collapse) {
                        controller.toggleLegendCollapse();
                    }
                    controller.model.changeDateRangeStart = null;
                    controller.model.changeDateRangeEnd = null;
                    controller.model.entities = null;
                    controller.model.types = null;
                    controller.model.priority = null;
                    controller.model.deleted = null;
                    controller.model.category = null;
                    controller.model.subCategory = null;
                    controller.model.editComments = null;
                    controller.applyFilter(true);
                }

                controller.applyFilter = function (toggle) {
                    if (toggle === true) {
                        controller.toggleLegendCollapse();
                    }
                    controller.refresh();
                }

                controller.objArrToCommaSepStr = function (array, field) {
                    var commaSepStr = '';
                    for (var i = 0; i < array.length; i++) {
                        var obj = array[i];
                        var str = obj[field];
                        commaSepStr += (commaSepStr.length > 0) ? ',' + str : str;
                    }
                    return commaSepStr;
                }

                var baseUrl = 'services/service-changelog/backoffice/changelogs/global/table?1=1';
                var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('createdRow', function(row, data, dataIndex) {
                    if (data.pinned) {
                        $(row).addClass('bold-text');
                    }
                    // Recompiling so we can bind Angular directive to the DT
                    $compile(angular.element(row).contents())($scope);
                }).withOption('order', [0, 'desc']);

                controller.getPriority = function(priority) {
                    if (priority <= 33) return controller.priorityMap.low.name
                    else if (priority > 33 && priority <= 66) return controller.priorityMap.medium.name
                    else if (priority > 66) return controller.priorityMap.high.name;
                }

                controller.selected = {};
                controller.deleted = {};
                controller.notes = {};

                controller.noDescEntities = ['user.note'];
                controller.markedEntities = { 'user.note' : { color:'#c92d0e'}};

                controller.getTextField = function (data, type, row, meta) {
                    let text = controller.noDescEntities.indexOf(data.entity.name) === -1 ? $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS' + '.' + data.type.name.toUpperCase(), {entity: data.entity.name}) : '';
                    if (data.fieldChanges && data.fieldChanges.length > 0) {
                        if (text.length > 0) text += ' ';
                        text += $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CHANGED');
                               angular.forEach(data.fieldChanges, function (fieldChange) {
                            text += ' ' + fieldChange.field + ' ' + $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.FROM') + ': \'' + (fieldChange.fromValue && fieldChange.fromValue.length ? fieldChange.fromValue : 'empty');
                            text += '\' ' + $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.TO') + ': \'' + (fieldChange.toValue && fieldChange.toValue.length ? fieldChange.toValue : 'empty') + '\';';
                        });
                    } else if (!data.comments || data.comments.length === 0) {
                        if (text.length > 0) text += ' ';
                        text += $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.NOCHANGES');
                    }
                    if (data.comments && data.comments.length) {
                        if (text.length > 0) text += ' ';
                        text += data.comments;
                    }
                    return '<div class="limited text-wrap" ' + (controller.markedEntities[data.entity.name] ? 'style="color: ' + controller.markedEntities[data.entity.name].color + '"' : '')
                        +'title="' + text + '">' + text.substring(0, 120) + (text.length > 120 ? '...' : '') + '</div>';
                }

                controller.getChangelogBuilder = function() {
                    var builder = $dt.builder()
                        .column($dt.column('id').withTitle('ID').notVisible())
                        .column($dt.columnformatdatetime('changeDate').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.DATE')));

                        if (!controller.isPlayersTab) {
                            builder = builder.column($dt.column('entity.name').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.ENTITY')));
                                //.column($dt.column('type.name').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.TYPE')));
                        }


                        builder = builder.column($dt.column('authorUser.guid').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CREATEDBY')).renderWith(function (data, type, row, meta) {
                            if(row.authorFullName !== null) {
                                return row.authorFullName;
                            } else {
                                return row.authorUser.guid;
                            }
                        }));

                        builder = builder.column($dt.column('category.name').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CATEGORY.NAME')))
                        .column($dt.column('subCategory.name').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.NAME')))
                        .column($dt.emptycolumn($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.TEXT')).renderWith(controller.getTextField).withClass("text-wrap"))
                        .column($dt.linkscolumn('', [
                                {
                                    permission: 'changelogs_global_view',
                                    permissionType: 'any',
                                    permissionDomain: 'check-tree',
                                    title: 'GLOBAL.ACTION.OPEN',
                                    href: function (data) {
                                        return $state.href($scope.entryhref, {entryid: data.id})
                                    }
                                }
                            ]
                        ))
                        .column($dt.column('priority').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LABEL')).renderWith(function (data, type, row, meta) {
                            return controller.getPriority(data);
                        }));

                        builder = builder.column($dt.column('pinned').withTitle($translate('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PINNED.LABEL')).renderWith(function (data, type, row, meta) {
                            if ($security.hasRoleForDomain(controller.domainName ? controller.domainName : row.domain.name, 'CHANGELOGS_GLOBAL_PIN') || $security.hasAdminRole()) {
                                controller.selected[row.id] = data;
                                return '<input type="checkbox" ng-model="controller.selected[' + row.id + ']" ng-click="controller.selectOne(' + row.id + ')">';
                            }
                        }));

                        if (!controller.isPlayersTab) {
                            builder = builder.column($dt.column('domain.name').withTitle($translate('UI_NETWORK_ADMIN.BONUS.DOMAIN')));
                        }

                        builder = builder.column($dt.emptycolumn('').renderWith( function (data, type, row, meta) {
                            controller.notes[row.id] = data;
                            if((data.authorUser.guid === $rootScope.principal.guid || $security.hasRoleForDomain(controller.domainName ? controller.domainName : data.domain.name, 'PLAYER_NOTES_EDIT')) &&
                                data.authorFullName !== 'System' && data.entity.name === 'user.note') {
                                return '<button class="btn btn-info" ng-click="controller.editComments(' + row.id + ')"><i class="fa fa-pencil-square-o"></i></button>';
                            } else {
                                return;
                            }
                        }));

                        builder = builder.column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
                            controller.deleted[row.id] = data;
                            if (controller.model.deleted) {
                                return '<button class="btn btn-info" lit-if-permission="CHANGELOGS_GLOBAL_RESTORE" lit-permission-domain="{{controller.domainName}}" ng-click="controller.deleted(' + row.id + ')"><i class="fa fa-trash-o"></i></button>'
                            } else {
                                return '<button class="btn btn-danger" lit-if-permission="CHANGELOGS_GLOBAL_DELETE" lit-permission-domain="{{controller.domainName}}" ng-click="controller.deleted(' + row.id + ')"><i class="fa fa-trash-o"></i></button>'
                            }
                        }));

                        return builder;
                }

                controller.changelogsTable = controller.getChangelogBuilder()
                    .options(
                        !controller.isPlayersTab ? (controller.domainNames.length !== 0 ?
                            {
                            url: baseUrl,
                            type: 'POST',
                            data: function (d) {
                                d.domainName = [];
                                for (let dN of controller.domainNames){
                                    if(typeof dN !== 'string'){
                                        d.domainName.push(dN.name)
                                    } else {
                                        d.domainName.push(dN)
                                    }
                                }
                                d.changeDateRangeStart = (controller.model.changeDateRangeStart !== undefined && controller.model.changeDateRangeStart !== null) ? controller.formatDate(controller.model.changeDateRangeStart) : null;
                                d.changeDateRangeEnd = (controller.model.changeDateRangeEnd !== undefined && controller.model.changeDateRangeEnd !== null) ? controller.formatDate(controller.model.changeDateRangeEnd) : null;
                                d.commaSepEntities = (controller.model.entities !== undefined && controller.model.entities !== null) ? controller.objArrToCommaSepStr(controller.model.entities, 'name') : null;
                                d.commaSepTypes = (controller.model.types !== undefined && controller.model.types !== null) ? controller.objArrToCommaSepStr(controller.model.types, 'name') : null;
                                d.commaSepCategory = controller.isKycTab ? 'Account' : (controller.model.category !== undefined && controller.model.category !== null) ? controller.model.category : null;
                                d.commaSepSubCategory = controller.isKycTab ? 'KYC' :(controller.model.subCategory !== undefined && controller.model.subCategory !== null) ? controller.objArrToCommaSepStr(controller.model.subCategory, 'name') : null;
                                d.priorityFrom = (controller.model.priority !== undefined && controller.model.priority !== null) ?  controller.priorityMap[controller.model.priority].from : null;
                                d.priorityTo = (controller.model.priority !== undefined && controller.model.priority !== null) ? controller.priorityMap[controller.model.priority].to : null;
                                d.entryRecordId = (controller.isPlayersTab &&  $stateParams.id !== undefined && $stateParams.id !== null) ? $stateParams.id : null;
                                d.withChanges = true;
                                d.deleted = (controller.model.deleted !== undefined && controller.model.deleted !== null && controller.model.deleted ) ? controller.model.deleted : false;
                            }
                        } : controller.domainNames)
                            : {
                            url: baseUrl,
                            type: 'POST',
                            data: function (d) {
                                d.changeDateRangeStart = (controller.model.changeDateRangeStart !== undefined && controller.model.changeDateRangeStart !== null) ? controller.formatDate(controller.model.changeDateRangeStart) : null;
                                d.changeDateRangeEnd = (controller.model.changeDateRangeEnd !== undefined && controller.model.changeDateRangeEnd !== null) ? controller.formatDate(controller.model.changeDateRangeEnd) : null;
                                d.commaSepEntities = (controller.model.entities !== undefined && controller.model.entities !== null) ? controller.objArrToCommaSepStr(controller.model.entities, 'name') : null;
                                d.commaSepTypes = (controller.model.types !== undefined && controller.model.types !== null) ? controller.objArrToCommaSepStr(controller.model.types, 'name') : null;
                                d.commaSepCategory = controller.isKycTab ? 'Account' : (controller.model.category !== undefined && controller.model.category !== null) ? controller.model.category : null;
                                d.commaSepSubCategory = controller.isKycTab ? 'KYC' : (controller.model.subCategory !== undefined && controller.model.subCategory !== null) ? controller.objArrToCommaSepStr(controller.model.subCategory, 'name') : null;
                                d.priorityFrom = (controller.model.priority !== undefined && controller.model.priority !== null) ?  controller.priorityMap[controller.model.priority].from : null;
                                d.priorityTo = (controller.model.priority !== undefined && controller.model.priority !== null) ? controller.priorityMap[controller.model.priority].to : null;
                                d.entryRecordId = (controller.isPlayersTab &&  $stateParams.id !== undefined && $stateParams.id !== null) ? $stateParams.id : null;
                                d.pinned = false;
                                d.withChanges = true;
                                d.deleted = (controller.model.deleted !== undefined && controller.model.deleted !== null && controller.model.deleted ) ? controller.model.deleted : false;
                            }
                        },
                        null,
                        dtOptions,
                        null
                    ).build();

                    if(controller.isPlayersTab) {
                        controller.pinnedchangelogsTable = controller.getChangelogBuilder()
                            .options(
                                {
                                    url: baseUrl,
                                    type: 'POST',
                                    data: function (d) {
                                        d.changeDateRangeStart = (controller.model.changeDateRangeStart !== undefined && controller.model.changeDateRangeStart !== null) ? controller.formatDate(controller.model.changeDateRangeStart) : null;
                                        d.changeDateRangeEnd = (controller.model.changeDateRangeEnd !== undefined && controller.model.changeDateRangeEnd !== null) ? controller.formatDate(controller.model.changeDateRangeEnd) : null;
                                        d.commaSepEntities = (controller.model.entities !== undefined && controller.model.entities !== null) ? controller.objArrToCommaSepStr(controller.model.entities, 'name') : null;
                                        d.commaSepTypes = (controller.model.types !== undefined && controller.model.types !== null) ? controller.objArrToCommaSepStr(controller.model.types, 'name') : null;
                                        d.commaSepCategory = (controller.model.category !== undefined && controller.model.category !== null) ? controller.model.category : null;
                                        d.commaSepSubCategory = (controller.model.subCategory !== undefined && controller.model.subCategory !== null) ? controller.objArrToCommaSepStr(controller.model.subCategory, 'name') : null;
                                        d.priorityFrom = (controller.model.priority !== undefined && controller.model.priority !== null) ? controller.priorityMap[controller.model.priority].from : null;
                                        d.priorityTo = (controller.model.priority !== undefined && controller.model.priority !== null) ? controller.priorityMap[controller.model.priority].to : null;
                                        d.entryRecordId = (controller.isPlayersTab && $stateParams.id !== undefined && $stateParams.id !== null) ? $stateParams.id : null;
                                        d.pinned = true;
                                        d.withChanges = true;
                                        d.deleted = (controller.model.deleted !== undefined && controller.model.deleted !== null && controller.model.deleted) ? controller.model.deleted : false;
                                    }
                                },
                                null,
                                dtOptions,
                                null
                            ).build();
                    }


                    controller.selectOne = function(id)
                    {
                        changelogsRest.setPinned(id, controller.selected[id]).then(function(response) {
                            if (response._successful) {
                                controller.refresh();
                                notify.success('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PINNED.CHANGE.SUCCESS');
                            } else {
                                notify.error('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PINNED.CHANGE.ERROR');
                            }
                        }).catch(function(error) {
                            errors.catch('', false)
                        });
                    }

                    controller.editComments = function(id) {
                        let modalInstance = $uibModal.open({

                            animation: true,
                            ariaDescribedBy: 'modal-title',
                            ariaLabelledBy: 'modal-body',
                            templateUrl: 'scripts/directives/changelog/editnotes.html',
                            controller: 'EditNotesModal',
                            controllerAs: 'controller',
                            backdrop: 'static',
                            size: 'md',
                            resolve: {
                                entityId: function () {
                                    return id;
                                },
                                selectedNote: function () {
                                    return controller.notes[id];
                                },
                                domainName: function() {
                                    return controller.domainName;
                                },
                                restService: function () {
                                    return changelogsRest;
                                },
                                loadMyFiles: function ($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        name: 'lithium',
                                        files: ['scripts/directives/changelog/editnotes.js']
                                    })
                                }
                            }

                        });
                        modalInstance.result.then(function(response) {
                            controller.refresh();
                            notify.success('UI_NETWORK_ADMIN.DIRECTIVES.GLOBAL.CHANGE_LOG.SUCCESS');
                        });
                    }

                controller.deleted = function(id) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/changelog/confirmdelete.html',
                        controller: 'ConfirmNoteDeleteModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            entityId: function () {
                                return id;
                            },
                            isDelete: function () {
                                return !controller.model.deleted;
                            },
                            domainName: function() {
                                return controller.domainName;
                            },
                            restService: function () {
                                return changelogsRest;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/changelog/confirmdelete.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function(response) {
                        controller.refresh();
                        notify.success('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.DELETED.CHANGE.SUCCESS');
                    });
                };

                controller.refresh = function () {
                    if (controller.isPlayersTab) {
                        controller.pinnedchangelogsTable.instance.rerender(true);
                    }
                    controller.changelogsTable.instance.rerender(true);
                }


                // Domain select

                console.log(controller.domains)

                $rootScope.provide.dropDownMenuProvider['domainList']  = () => {
                    controller.domains.forEach(el=> {
                        el.selected = true
                    })
                    return controller.domains
                }
                $rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
                    const domainNames = []
                    data.forEach(el=> {
                        domainNames.push(el.name)
                    })
                    controller.domainNames = domainNames
                    controller.refresh();
                }

                window.VuePluginRegistry.loadByPage("DomainSelect")

            }]
    }
});
