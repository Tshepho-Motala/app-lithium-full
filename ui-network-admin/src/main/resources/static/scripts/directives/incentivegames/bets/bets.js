'use strict';

angular.module('lithium').directive('incentivegamesbets', function() {
    return {
        templateUrl:'scripts/directives/incentivegames/bets/bets.html',
        scope: {
            data: "="
        },
        restrict: 'E',
        replace: true,
        controller: ['$scope', '$filter', 'errors', 'notify', 'bsLoadingOverlayService', 'UserRest', 'IncentiveGamesRest',
            function($scope, $filter, errors, notify, bsLoadingOverlayService, userRest, incentiveGamesRest) {
                $scope.allowUserSearch = ($scope.data.allowUserSearch !== undefined && $scope.data.allowUserSearch !== null) ? $scope.data.allowUserSearch : true;

                $scope.bets = [];

                $scope.model = {
                    betId: $scope.data.betId,
                    settlementId: $scope.data.settlementId,
                    competition: $scope.data.competition,
                    sport: $scope.data.sport,
                    eventName: $scope.data.eventName,
                    market: $scope.data.market,
                    selectedUser: $scope.data.user,
                    isSettled: ($scope.data.isSettled !== undefined && $scope.data.isSettled !== null) ? $scope.data.isSettled : 0,
                    betTimestampRangeStart: $scope.data.betTimestampRangeStart,
                    betTimestampRangeEnd: $scope.data.betTimestampRangeEnd,
                    settlementTimestampRangeStart: $scope.data.settlementTimestampRangeStart,
                    settlementTimestampRangeEnd: $scope.data.settlementTimestampRangeEnd
                }

                $scope.legendCollapsed = true;
                $scope.paginationLengthOpts = [
                    {value: "10"},
                    {value: "25"},
                    {value: "50"},
                    {value: "100"}
                ];
                $scope.page = 1;
                $scope.pagination = {
                    page: 1,
                    draw: 1,
                    start: 1,
                    end: 10,
                    length: "10",
                    recordsFiltered: 0,
                    recordsTotal: 0
                };

                $scope.settlementFields = [
                    {
                        className: 'col-md-4 col-xs-8',
                        key: 'isSettled',
                        type: 'ui-select-single',
                        templateOptions : {
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [
                                {value: 0, label: 'All'},
                                {value: 1, label: 'Settled'},
                                {value: 2, label: 'Not Settled'},
                            ]
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SETTLED" | translate'
                        },
                    },{
                        className: 'col-md-4 col-xs-8',
                        key: 'result',
                        type: 'ui-select-single',
                        templateOptions : {
                            valueProp: 'code',
                            labelProp: 'code',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: []
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.RESULT" | translate'
                        },
                        controller: ['$scope', function($scope) {
                            incentiveGamesRest.getSettlementResultCodes().then(function(response) {
                                $scope.to.options = response.plain();
                            });
                        }]
                    }
                ]

                $scope.idFields = [
                    {
                        className: 'col-md-4 col-xs-8',
                        key: 'betId',
                        type: 'input',
                        templateOptions : {
                            type: 'text'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.BETIDLABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.BETIDPLACEHOLDER" | translate'
                        },
                    },{
                        className: 'col-md-4 col-xs-8',
                        key: 'settlementId',
                        type: 'input',
                        templateOptions : {
                            type: 'text'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SETTLEMENTIDLABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SETTLEMENTIDPLACEHOLDER" | translate'
                        },
                    }
                ]

                $scope.categoryFields = [
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'competition',
                        type: 'input',
                        templateOptions: {
                            type: 'text'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.COMPETITIONLABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.COMPETITIONPLACEHOLDER" | translate'
                        },
                    },
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'sport',
                        type: 'input',
                        templateOptions: {
                            type: 'text'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SPORTLABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SPORTPLACEHOLDER" | translate'
                        },
                    },
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'eventName',
                        type: 'input',
                        templateOptions: {
                            type: 'text'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.EVENTNAMELABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.EVENTNAMEPLACEHOLDER" | translate'
                        },
                    },
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'market',
                        type: 'input',
                        templateOptions: {
                            type: 'text'
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.MARKETLABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.MARKETPLACEHOLDER" | translate'
                        },
                    }
                ];

                $scope.dateFields = [
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'betTimestampRangeStart',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            required: false,
                            datepickerOptions: {
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function() { $scope.dateFields[1].templateOptions.datepickerOptions.minDate = $scope.model.betTimestampRangeStart; }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.BETTIMERANGESTART" | translate'
                        },
                    },
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'betTimestampRangeEnd',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            required: false,
                            datepickerOptions: {
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function() { $scope.dateFields[0].templateOptions.datepickerOptions.maxDate = $scope.model.betTimestampRangeEnd; }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.BETTIMERANGEEND" | translate'
                        },
                    },
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'settlementTimestampRangeStart',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            required: false,
                            datepickerOptions: {
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function() { $scope.dateFields[3].templateOptions.datepickerOptions.minDate = $scope.model.settlementTimestampRangeStart; }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SETTLEMENTTIMERANGESTART" | translate'
                        },
                    },
                    {
                        className: 'col-md-3 col-xs-12',
                        key: 'settlementTimestampRangeEnd',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            required: false,
                            datepickerOptions: {
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function() { $scope.dateFields[2].templateOptions.datepickerOptions.maxDate = $scope.model.settlementTimestampRangeEnd; }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.INCENTIVEGAMES.BETS.FILTER.SETTLEMENTTIMERANGEEND" | translate'
                        },
                    }
                ];

                $scope.toggleLegendCollapse = function() {
                    $scope.legendCollapsed = !$scope.legendCollapsed;
                }

                $scope.resetFilter = function(collapse) {
                    if (collapse) {
                        $scope.toggleLegendCollapse();
                    }
                    $scope.model.betId = null;
                    $scope.model.settlementId = null;
                    $scope.model.competition = null;
                    $scope.model.sport = null;
                    $scope.model.eventName = null;
                    $scope.model.market = null;
                    $scope.model.isSettled = 0;
                    if ($scope.allowUserSearch) $scope.model.selectedUser = undefined;
                    $scope.model.betTimestampRangeStart = null;
                    $scope.model.betTimestampRangeEnd = null;
                    $scope.model.settlementTimestampRangeStart = null;
                    $scope.model.settlementTimestampRangeEnd = null;
                    $scope.model.result = null;
                    $scope.applyFilter(true);
                }

                $scope.applyFilter = function(toggle) {
                    if (toggle === true) {
                        $scope.toggleLegendCollapse();
                    }
                    $scope.pagination.start = 1;
                    $scope.page = 1;
                    $scope.pagination.page = 1;
                    $scope.refresh();
                }


                $scope.resetUserSearch = function() {
                    $scope.model.selectedUser = undefined;
                }

                $scope.searchUsers = function(search) {
                    return userRest.searchAllPlayers(search).then(function(searchResult) {
                        return searchResult.plain();
                    }).catch(function(error) {
                        errors.catch("", false)(error)
                    });
                }

                $scope.calculatePageInfo = function() {
                    if ($scope.pagination.recordsTotal === 0) {
                        $scope.pagination.start = 0;
                        $scope.pagination.end = 0;
                    } else {
                        $scope.pagination.start = ($scope.pagination.page - 1) * $scope.pagination.length + 1;
                        $scope.pagination.end = $scope.pagination.recordsTotal;
                        if ($scope.pagination.length < $scope.pagination.recordsTotal) {
                            $scope.pagination.end = $scope.pagination.length * $scope.pagination.page;
                            if ($scope.pagination.end > $scope.pagination.recordsTotal) {
                                $scope.pagination.end = $scope.pagination.recordsTotal;
                            }
                        }
                    }
                }

                $scope.paginationLengthOptChanged = function() {
                    console.debug("$scope.pagination.length", $scope.pagination.length);
                    $scope.calculatePageInfo();
                    $scope.refresh();
                }

                $scope.$watch('[page]', function (newValue, oldValue) {
                    if (newValue !== oldValue) {
                        console.debug('page', newValue);
                        $scope.pagination.page = newValue[0];
                        $scope.calculatePageInfo();
                        $scope.refresh();
                    }
                });

                $scope.formatDate = function(date) {
                    return $filter('date')(date, 'yyyy-MM-dd');
                }

                $scope.isSettled = function(isSettled) {
                    if (isSettled === undefined || isSettled === null) return null;
                    switch (isSettled) {
                        case 0:
                            return null;
                        case 1:
                            return true;
                        case 2:
                            return false;
                    }
                }

                $scope.resultCodeDescription = function() {
                    if ($scope.model.result === undefined || $scope.model.result === null) return "all";
                    switch ($scope.model.result) {
                        case "WIN": return "winning";
                        case "VOID": return "voided";
                        case "LOST": return "losing";
                    }
                }

                $scope.viewingDescription = function() {
                    var settled = $scope.isSettled($scope.model.settlement);
                    if (settled !== null && !settled) {
                        return "Viewing all open bets";
                    }
                    var resultCodeDescription = $scope.resultCodeDescription();
                    return "Viewing " + resultCodeDescription + ((settled && resultCodeDescription === 'all') ? " settled " : "") + " bets";
                }

                $scope.refresh = function() {
                    bsLoadingOverlayService.start({referenceId: "loading"});
                    incentiveGamesRest.getBetsTable(
                        $scope.pagination.draw,
                        $scope.pagination.start,
                        $scope.pagination.length,
                        ($scope.model.selectedUser !== undefined && $scope.model.selectedUser !== null) ? $scope.model.selectedUser.guid : null,
                        $scope.isSettled($scope.model.isSettled),
                        $scope.model.result,
                        ($scope.model.betTimestampRangeStart != null) ? $scope.formatDate($scope.model.betTimestampRangeStart) : null,
                        ($scope.model.betTimestampRangeEnd != null) ? $scope.formatDate($scope.model.betTimestampRangeEnd) : null,
                        ($scope.model.settlementTimestampRangeStart != null) ? $scope.formatDate($scope.model.settlementTimestampRangeStart) : null,
                        ($scope.model.settlementTimestampRangeEnd != null) ? $scope.formatDate($scope.model.settlementTimestampRangeEnd) : null,
                        ($scope.model.betId != null) ? $scope.model.betId : null,
                        ($scope.model.settlementId != null) ? $scope.model.settlementId : null,
                        ($scope.model.competition != null) ? $scope.model.competition : null,
                        ($scope.model.sport != null) ? $scope.model.sport : null,
                        ($scope.model.eventName != null) ? $scope.model.eventName : null,
                        ($scope.model.market != null) ? $scope.model.market: null
                    ).then(function (response) {
                        $scope.bets = response.data.data;
                        $scope.pagination.recordsFiltered = response.data.recordsFiltered;
                        $scope.pagination.recordsTotal = response.data.recordsTotal;
                        $scope.calculatePageInfo();
                    }).catch(
                        errors.catch("Failed to load bets.", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: "loading"});
                    });
                }

                $scope.refresh();
            }
        ]
    }
});
