'use strict';

angular.module('lithium').directive('sportsFreeBetsList', function () {
    return {
        templateUrl: 'scripts/directives/bonuses/sportsfreebets/sportsfreebetslist.html',
        scope: {
            data: '='
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$dt', '$translate', '$scope', 'DTOptionsBuilder', '$filter', 'notify', '$stateParams',
        function ($dt, $translate, $scope, DTOptionsBuilder, $filter, notify, $stateParams) {
            let controller = this;
            $scope.options = {removeChromeAutoComplete: true}
            $scope.startDate = moment(new Date()).subtract(6, "months").format("YYYY-MM-DD");
            $scope.endDate = moment(new Date()).add(1, "days").format("YYYY-MM-DD");
            $scope.model = {
                sportsFreeBetListDateRangeStart: $scope.startDate,
                sportsFreeBetListDateRangeEnd: $scope.endDate,
                sportsFreeBetActiveBonusStatusDropDown: null
            }

            $scope.formatDate = function(date) {
                return $filter('date')(date, 'yyyy-MM-dd');
            }

            $scope.formatDatePicker = function(date) {
                return $filter('date')(date, 'dd/MM/yyyy');
            }

            $scope.fields = [
                {
                    className: 'col-md-4 col-xs-12',
                    key: 'sportsFreeBetListDateRangeStart',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Granted Date: Range Start',
                        placeholder: $scope.formatDatePicker($scope.startDate),
                        required: true,
                        datepickerOptions: {
                            initDate: $scope.formatDatePicker($scope.startDate),
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function() { $scope.fields[1].templateOptions.datepickerOptions.minDate = $scope.model.dateRangeStart; }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.FIELDS.LABEL.DATE_RANGE_START" | translate',
                    }
                },
                {
                    className: 'col-md-4 col-xs-12',
                    key: 'sportsFreeBetListDateRangeEnd',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Granted Date: Range End',
                        placeholder: $scope.formatDatePicker($scope.endDate),
                        required: true,
                        datepickerOptions: {
                            initDate: $scope.formatDatePicker($scope.endDate),
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function() { $scope.fields[0].templateOptions.datepickerOptions.maxDate = $scope.model.dateRangeEnd; }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.FIELDS.LABEL.DATE_RANGE_END" | translate',
                    }
                },
                {
                    className: 'col-md-2 col-xs-12',
                    key: 'sportsFreeBetActiveBonusStatusDropDown',
                    type: 'ui-select-single',
                    templateOptions : {
                        placeholder: $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.PLACEHOLDER'),
                        valueProp: 'value',
                        labelProp: 'label',
                        optionsAttr: 'ui-options', ngOptions: 'ui-options',
                        options: [
                            {value: 0, label: $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.GRANTED')},
                            {value: 1, label: $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.OPENED')},
                            {value: 2, label: $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.EXPIRED')},
                            {value: 3, label: $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.CANCELLED')}
                        ]
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.FIELDS.LABEL.STATUS" | translate'
                    }
                }

            ];

            $scope.refresh = function() {
                $scope.sportsFreeBetTable.instance.reloadData(() => {}, true);
            }


            $scope.applyFilter = function() {
                if(moment($scope.model.sportsFreeBetListDateRangeStart).isAfter($scope.model.sportsFreeBetListDateRangeEnd)) {
                    notify.error('"UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.ERRORS.DATE_FILTER" | translate');
                } else {
                    $scope.refresh();
                }
            }

            $scope.resetFilter = function () {
                $scope.model = {
                    sportsFreeBetListDateRangeStart: $scope.startDate,
                    sportsFreeBetListDateRangeEnd: $scope.endDate
                }
                $scope.applyFilter();
            }

            $scope.status = function(data) {
                if(data === 0) {
                    return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.GRANTED');
                } else if(data == 1) {
                    return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.OPENED');
                } else if( data == 2) {
                    return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.EXPIRED');
                } else if(data == 3) {
                    return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.LABEL.CANCELLED');
                }
            }

            const activeBonusesUrl  = 'services/service-casino-provider-sportsbook/backoffice/bet/' +$stateParams.domainName+'/freebets/history';
            let dtOptions = DTOptionsBuilder.newOptions().withOption('order', [[0, 'desc']]).withOption('bFilter', false);
            controller.paintCell = function (data, value, lclass) {
                return '<div class="limited text-wrap ' + lclass + '" '
                    +'title="' + value + '">' + value + '</div>';
            }

            $scope.sportsFreeBetTable  = $dt.builder()
                .column($dt.columnformatdatetime('dateGiven').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.TABLE.GRAND_DATE')))
                .column($dt.column('bonusId').withTitle( $translate('UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.TABLE.BONUS_ID')).notSortable())
                .column($dt.column('bonusName').withTitle( $translate('UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.TABLE.BONUS_NAME')).notSortable())
                .column($dt.column('bonusCode').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.TABLE.BONUS_CODE')).notSortable())
                .column($dt.columncurrencysymbolcents('amountGiven', 'currencySymbol', 2)
                    .withTitle($translate("UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.TABLE.AMOUNT_GIVEN")).notSortable())
                .column($dt.labelcolumn($translate('UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.TABLE.STATUS'),
                    new Array({
                        text: function (data) {
                            if(data.status === 'completed') {
                                return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.BONUS.STATUS.GRANTED');
                            } else if (data.status === 'expired') {
                                return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.BONUS.STATUS.EXPIRED');
                            } else if (data.status === 'cancelled') {
                                return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.BONUS.STATUS.CANCELLED');
                            } else if (data.status === 'opened') {
                                return $translate.instant('UI_NETWORK_ADMIN.GLOBAL.BONUS.STATUS.OPENED');
                            }
                        },
                        lclass: function(data)  {
                            if(data.status === 'completed') {
                                return "default label-columns bg-green";
                            } else if(data.status === 'expired') {
                                return "danger label-columns";
                            }
                        }
                    })).notSortable())
                .options(
                    {
                    url: activeBonusesUrl,
                    type: 'GET',
                    data: function (s) {
                        s.playerId = $stateParams.id;
                            s.dateRangeFrom = ($scope.model.sportsFreeBetListDateRangeStart !== undefined && $scope.model.sportsFreeBetListDateRangeStart !== null)? $scope.formatDate($scope.model.sportsFreeBetListDateRangeStart)
                            : $scope.formatDate($scope.startDate);
                        s.dateRangeTo = ($scope.model.sportsFreeBetListDateRangeEnd !== undefined && $scope.model.sportsFreeBetListDateRangeEnd !== null) ? $scope.formatDate($scope.model.sportsFreeBetListDateRangeEnd)
                            : $scope.formatDate($scope.endDate);
                        s.status = s.status = ($scope.model.sportsFreeBetActiveBonusStatusDropDown !== undefined && $scope.model.sportsFreeBetActiveBonusStatusDropDown !== null)
                            ? $scope.status($scope.model.sportsFreeBetActiveBonusStatusDropDown) : null;
                    }
                }
                , null, dtOptions, null).build();
        }]
    }
})