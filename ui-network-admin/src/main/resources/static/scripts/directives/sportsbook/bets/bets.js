'use strict';

angular.module('lithium').directive('sportbookbets', function () {
    return {
        templateUrl: 'scripts/directives/sportsbook/bets/bets.html',
        scope: {
            data: '='
        },
        restrict: 'E',
        replace: true,
        controller: ['$state', '$scope', '$filter', 'errors', 'notify', 'bsLoadingOverlayService', 'rest-domain', 'UserRest', 'SportsbookRest', '$dt', '$translate', 'DTOptionsBuilder', '$compile', '$uibModal',
            function ($state, $scope, $filter, errors, notify, bsLoadingOverlayService, domainRest, userRest, sportsBookRest, $dt, $translate, DTOptionsBuilder, $compile, $uibModal) {
                let domainName = $scope.data.domainName;
                let playerOffset = $scope.data.playerOffset;
                const controller = this;
                $scope.model = {};
                if ($scope.data.user !== undefined) {
                    domainName = $scope.data.user.domain.name;
                    $scope.model.selectedUser = $scope.data.user;
                }
                $scope.marketLeagueVisible = false;
                $scope.tableVisible = true;
                let marketList = {};
                let leagueList = {};
                let eventsList = {};

                let today = new Date().toISOString();
                let yesterday = new Date(new Date().getTime() - (24 * 60 * 60 * 1000)).toISOString();

                let fromDate = moment().subtract(1, 'd').format('YYYY-MM-DD');
                let toDate = moment().format('YYYY-MM-DD');

                $scope.allowUserSearch = ($scope.data.allowUserSearch !== undefined && $scope.data.allowUserSearch !== null) ? $scope.data.allowUserSearch : true;
                $scope.legendCollapsed = true;
                $scope.formatDatePicker = function(date) {
                    return $filter('date')(date, 'dd/MM/yyyy');
                }
                const baseUrl = 'services/service-casino-provider-sportsbook/backoffice/bet/'.concat(domainName).concat('/search/table');
                const dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('bFilter', false).withOption('order', []).withOption('createdRow', function (row, data, dataIndex) {
                    $compile(angular.element(row).contents())($scope);
                });

                let sportsList = sportsBookRest.sportsByList(domainName).then(function (searchResult) {
                    return searchResult.plain();
                }).catch(function (error) {
                    errors.catch("", false)(error)
                });

                $scope.status = ['Opened'];
                $scope.tabs = [
                    {
                        name: 'Open',
                        title: 'GLOBAL.ACTION.OPEN',
                    },
                    {
                        name: 'Settled',
                        title: 'UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SETTLED',
                    }
                ];

                $scope.settlementFields = [
                    {
                        className: 'col-md-6 col-xs-12',
                        key: 'betType',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [
                                {value: "Single", label: 'Single'},
                                {value: "Combo", label: 'Combo'},
                                {value: "Forecast", label: 'Forecast'},
                                {value: "Tricast", label: 'Tricast'},
                                {value: "System", label: 'System'},
                                {value: "YourBet", label: 'Your Bet'}
                            ]
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.SPORTSBOOK.BETS.FILTER.BET_TYPE" | translate'
                        }
                    },
                    {
                        className: 'col-md-6 col-xs-12',
                        key: 'matchType',
                        type: 'ui-select-single',
                        templateOptions: {
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [
                                {value: "Prematch", label: 'Prematch'},
                                {value: "Live", label: 'Live'},
                                {value: "All", label: 'All '},
                            ]
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.SPORTSBOOK.BETS.FILTER.MATCH_TYPE" | translate'
                        }
                    }
                ];

                $scope.bySportFilters = [
                    {
                        className: 'col-md-6 col-xs-12',
                        key: 'leaguesBySport',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            valueProp: 'id',
                            labelProp: 'name',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [],
                            onChange: function () {
                                eventsList = sportsBookRest.events(domainName, jsonToIntArray($scope.model.leaguesBySport));
                            }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.SPORTBOOK.BETS.HISTORY.LEAGUE" | translate'
                        },
                        controller: ['$scope', function ($scope) {
                            leagueList.then(function (response) {
                                $scope.options.templateOptions.options = response.plain();
                                return response;
                            });
                        }]
                    },
                    {
                        className: 'col-md-6 col-xs-12',
                        key: 'marketTypesBySport',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            valueProp: 'id',
                            labelProp: 'name',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: []
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.SPORTSBOOK.BETS.LABEL.MARKET_TYPE" | translate'
                        },
                        controller: ['$scope', function ($scope) {
                            marketList.then(function (response) {
                                $scope.options.templateOptions.options = response.plain();
                                return response;
                            });
                        }]
                    }
                ];

                $scope.byLeagueFilters = [
                    {
                        className: 'col-md-12 col-xs-12',
                        key: 'eventsByLeague',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            valueProp: 'id',
                            labelProp: 'name',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: []
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.TAB.EVENTS" | translate'
                        },
                        controller: ['$scope', function ($scope) {
                            eventsList.then(function (response) {
                                $scope.options.templateOptions.options = response.plain();
                                return response;
                            });
                        }]
                    }
                ];

                $scope.idFields = [
                    {
                        className: 'col-md-6 col-xs-12',
                        key: 'betId',
                        type: 'input',
                        templateOptions: {
                            type: 'text'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.SPORTSBOOK.BETS.FILTER.BETIDLABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.SPORTSBOOK.BETS.FILTER.BETIDPLACEHOLDER" | translate'
                        },
                    }
                ];

                $scope.dateFields = [
                    {
                        className: 'col-md-6 col-xs-12',
                        key: 'betTimestampRangeStart',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            placeholder: $scope.formatDatePicker(yesterday),
                            required: false,
                            datepickerOptions: {
                                format: 'dd/MM/yyyy',
                                initDate: $scope.formatDatePicker(yesterday),
                            },
                            onChange: function () {
                                $scope.dateFields[1].templateOptions.datepickerOptions.minDate = $scope.model.betTimestampRangeStart;
                            }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.DATERANGESTART.LABEL" | translate'
                        },
                    },
                    {
                        className: 'col-md-6 col-xs-12',
                        key: 'betTimestampRangeEnd',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            required: false,
                            placeholder: $scope.formatDatePicker(today),
                            datepickerOptions: {
                                format: 'dd/MM/yyyy',
                                initDate: $scope.formatDatePicker(today),
                            },
                            onChange: function () {
                                $scope.dateFields[0].templateOptions.datepickerOptions.maxDate = $scope.model.betTimestampRangeEnd;
                            }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.DATERANGEEND.LABEL" | translate'
                        },
                    },
                    {
                        className: 'col-md-12 col-xs-12',
                        key: 'selectedSport',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            label: 'Sports',
                            description: "",
                            required: false,
                            valueProp: 'id',
                            labelProp: 'name',
                            optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                            placeholder: '',
                            options: [],
                            onChange: function () {
                                marketList = sportsBookRest.markets(domainName, jsonToIntArray($scope.model.selectedSport));
                                leagueList = sportsBookRest.leagues(domainName, jsonToIntArray($scope.model.selectedSport));
                            }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SPORTLABEL" | translate',
                            'templateOptions.placeholder': '',
                            'templateOptions.description': ''
                        },
                        controller: ['$scope', function ($scope) {
                            sportsList.then(function (response) {
                                $scope.options.templateOptions.options = response;
                                return response;
                            });
                        }]
                    }
                ];

                $scope.toggleLegendCollapse = function () {
                    $scope.legendCollapsed = !$scope.legendCollapsed;
                }

                $scope.applyFilter = function (toggle) {
                    if (toggle === true) {
                        $scope.toggleLegendCollapse();
                    }
                    $scope.rowData = [];
                    $scope.refresh();
                }

                $scope.resetFilter = function (collapse) {
                    if (collapse) {
                        $scope.toggleLegendCollapse();
                    }
                    $scope.rowData = [];
                    $scope.model.betId = null;
                    if ($scope.allowUserSearch) $scope.model.selectedUser = undefined;
                    $scope.model.selectedSport = undefined;
                    $scope.model.betType = null;
                    $scope.model.betAmountType = null;
                    $scope.model.matchType = null;
                    $scope.model.betTimestampRangeStart = undefined;
                    $scope.model.betTimestampRangeEnd  = undefined;
                    $scope.model.result = null;
                    $scope.model.leaguesBySport = null;
                    $scope.model.marketTypesBySport = null;
                    $scope.model.eventsByLeague = null;
                    $scope.applyFilter(true);
                }

                $scope.resetUserSearch = function () {
                    $scope.model.selectedUser = undefined;
                }

                $scope.searchUsers = function (search) {
                    return userRest.searchAllPlayers(search).then(function (searchResult) {
                        return searchResult.plain();
                    }).catch(function (error) {
                        errors.catch("", false)(error)
                    });
                }

                $scope.tableBuilder = function () {

                    return $dt.builder()
                        .column($dt.column('').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETID.LABEL'))
                            .renderWith(function (data, type, row, meta) {
                            //this was the fix for the rounding id error
                            $scope.rowData.push(row);
                            return ` <a ng-click="moreInfo(${meta.row})">${row.betId}</a>`;
                        }))
                        .column($dt.column('betName').withTitle($translate('UI_NETWORK_ADMIN.SPORTBOOK.BETS.HISTORY.BET_TYPE')))
                        .column($scope.allowUserSearch ? $dt.linkscolumn($translate('UI_NETWORK_ADMIN.GAME.PLAYERSDT.GUID'), [{
                            permission: "player_view",
                            permissionType: "any",
                            permissionDomain: function () {
                                return domainName;
                            },
                            title: function (data) {
                                let playerId = data.customerId;
                                if (playerOffset && playerOffset > 0) {
                                    playerId = data.customerId - playerOffset;
                                }
                                return domainName.concat('/').concat(playerId);
                            },
                            target: '_blank',
                            href: function (data) {
                                let playerId = data.customerId;

                                if (playerOffset && playerOffset > 0) {
                                    playerId = data.customerId - playerOffset;
                                }
                                return $state.href("dashboard.players.player.summary", {id: playerId, domainName: domainName})
                            }
                        }]).notSortable() : undefined)
                        .column($dt.columnformatnotimezone('betDate')
                            .withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETDATE.LABEL')))
                        .column($dt.columnsize('selections').withTitle($translate('UI_NETWORK_ADMIN.SPORTBOOK.BETS.HISTORY.SELECTIONS')).notSortable())
                        .column($dt.column('stake')
                            .withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.STAKE.LABEL')).notSortable())
                        .column($dt.column('return')
                            .withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.RETURN.LABEL')).notSortable())
                        .column($dt.column('betStatus')
                            .withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETSTATUS.LABEL')))
                        .column($dt.labelcolumn(
                            '',
                            [{
                                text: function (data) {
                                    return data['return'] - data['stake'];
                                },
                            }]
                        ).withTitle($translate('UI_NETWORK_ADMIN.SPORTBOOK.BETS.HISTORY.WIN_LOSS')).notSortable())
                        .column($dt.columnformatdate('betSettledDate')
                            .withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETSETTLEDDATE.LABEL')).withOption('visible', false)
                        )
                        .options({
                                url: baseUrl,
                                type: 'POST',
                                data: function (request) {
                                    $scope.rowData = [];
                                    fromDate = $scope.formatDate($scope.model.betTimestampRangeStart);
                                    toDate = $scope.formatDate($scope.model.betTimestampRangeEnd);
                                    request.dateType = $scope.status.length === 1 ? 'BetPlacement' : 'BetSettledDate';
                                    request.from = fromDate === undefined ? yesterday : fromDate;
                                    request.to = toDate === undefined ? today : toDate;
                                    request.status = $scope.status;
                                    request.matchType = $scope.model.matchType;
                                    $scope.model.betType ? request.betType = jsonToArray($scope.model.betType) : undefined;
                                    $scope.model.betId ? request.betId = $scope.model.betId : undefined;
                                    $scope.model.selectedSport ? request.sport = jsonToIntArray($scope.model.selectedSport) : undefined;
                                    $scope.model.leaguesBySport ? request.leaguesBySport = jsonToIntArray($scope.model.leaguesBySport) : undefined;
                                    $scope.model.marketTypesBySport ? request.marketTypesBySport = jsonToIntArray($scope.model.marketTypesBySport) : undefined;
                                    $scope.model.eventsByLeague ? request.eventsByLeague = jsonToIntArray($scope.model.eventsByLeague) : undefined;
                                    $scope.model.selectedUser ? request.customerId = $scope.model.selectedUser.id : undefined;
                                },
                                error: function (xhr, error, thrown) {
                                    notify.error('UI_NETWORK_ADMIN.DOMAIN.PROVIDERS.ERRORS.ERROR_ON_LOAD');
                                }
                            }, null, dtOptions, null
                        );
                }

                $scope.betsTable = $scope.tableBuilder().build();

                $scope.moreInfo = function (id) {
                    let modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/sportsbook/bets/info/info.html',
                        controller: 'betInfo',
                        controllerAs: 'controller',
                        size: 'md cascading-modal',
                        backdrop: 'static',
                        resolve: {
                            betData: function () {
                                return $scope.rowData[id];
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: [
                                        'scripts/controllers/dashboard/sportsbook/bets/info/info.js'
                                     ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (dm) {
                        controller.reload();
                    });
                };

                $scope.setTab = function (tabs) {
                    $scope.activeTab = tabs;
                    switch (tabs.name) {
                        case 'Open':
                            $scope.status = ['Opened'];
                            $scope.betsTable.columns.$$state.value[9].bVisible = false;
                            break;
                        default:
                            $scope.status = ['Lost', 'Won', 'Draw', 'Cancelled', 'HalfLost', 'HalfWon', 'CashOut'];
                            $scope.betsTable.columns.$$state.value[9].bVisible = true;
                    }
                    $scope.tableVisible = false;
                    $scope.$watch(function() { return $scope.betsTable.columns.$$state.value[9].bVisible; }, function(newValue, oldValue) {
                        //LSPLAT-4011 - this variable is to make sure the table is rebuilt when a tab is changed
                        $scope.tableVisible = true;

                        if (newValue !== oldValue) {
                           $scope.refresh();
                        }
                    });
                }

                angular.forEach($scope.tabs, function (tab) {
                    if ($state.includes(tab.name)) $scope.activeTab = tab;
                });

                $scope.formatDate = function (date) {
                    return $filter('date')(date, 'yyyy-MM-dd');
                }

                $scope.refresh = function () {
                    $scope.betsTable.instance.reloadData(function () {
                    }, false);
                }

                function jsonToIntArray(selected) {
                    let array = [];
                    selected.forEach(item => array.push(item.id));
                    return array;
                }

                function jsonToArray(selected) {
                    let array = [];
                    selected.forEach(item => array.push(item.value));
                    return array;
                }
            }
        ]
    }
});
