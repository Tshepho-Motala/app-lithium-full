'use strict';

angular.module('lithium').controller('ActiveBonusesController', ['$filter', '$dt', '$translate', 'DTOptionsBuilder','notify', 'rest-domain',
    'rest-casino', '$scope', 'errors', '$state', '$userService', 'UserRest',
    function ($filter, $dt, $translate, DTOptionsBuilder, notify, restDomain, restCasino, $scope, errors, $state, $userService, userRest) {
        let controller = this;
        let bonusCodesArray = [];
        $scope.options = {removeChromeAutoComplete: true};
        $scope.startDate = moment(new Date()).subtract(6, "months").format("YYYY-MM-DD");
        $scope.endDate = moment(new Date()).add(1, "days").format("YYYY-MM-DD");
        controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "BONUS_VIEW"]);

        $scope.model = {
            dateRangeFrom: $scope.startDate,
            dateRangeTo: $scope.endDate,
            bonusNameAndCodeDropDown: bonusCodesArray,
            brandDropDown: null
        };

        $scope.formatDate = function (date) {
            return $filter('date')(date, 'yyyy-MM-dd');
        }

        $scope.formatDatePicker = function (date) {
            return $filter('date')(date, 'dd/MM/yyyy');
        }

        $scope.statusTranslate = function(data) {
            console.log(data);
            if (data === 0) {
                return 'GRANTED';
            } else if (data == 1) {
                return 'EXPIRED';
            } else if (data == 2) {
                return 'CANCELLED';
            } else if (data == 3) {
                return 'ACTIVE';
            }
        }

        $scope.fields = [
            {
                className: "col-md-2 col-xs-12",
                key: "brandDropDown",
                type: "ui-select-single",
                templateOptions: {
                    placeholder: "Select brand...",
                    valueProp: 'name',
                    labelProp: 'name',
                    optionsAttr: 'ui-options',
                    ngOptions: 'ui-options',
                    ngHide: false,
                    options: controller.domains
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.FIELDS.TAB.DOMAIN_DROPDOWN.LABEL"| translate'
                }
            },
            {
                className: 'col-md-2 col-xs-12',
                    key: 'dateRangeFrom',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                    label: '',
                        placeholder: $scope.formatDatePicker($scope.startDate),
                        required: true,
                        datepickerOptions: {
                        initDate: $scope.formatDatePicker($scope.startDate),
                            format: 'dd/MM/yyyy'
                    },
                    onChange: function () {
                        $scope.fields[2].templateOptions.datepickerOptions.minDate = $scope.model.dateRangeFrom;
                    }
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.FIELDS.TAB.DATE_FROM.LABEL" | translate',
                }
            },
            {
                className: 'col-md-2 col-xs-12',
                key: 'dateRangeTo',
                type: 'datepicker',
                optionsTypes: ['editable'],
                templateOptions: {
                    label: '',
                    placeholder: $scope.formatDatePicker($scope.endDate),
                    required: true,
                    datepickerOptions: {
                        initDate: $scope.formatDatePicker($scope.endDate),
                        format: 'dd/MM/yyyy'
                    },
                    onChange: function () {
                        $scope.fields[2].templateOptions.datepickerOptions.minDate = $scope.model.dateRangeTo;
                    }
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.FIELDS.TAB.DATE_TO.LABEL" | translate',
                }
            },
            {
                className: 'col-md-2 col-xs-12',
                key: 'activeBonusStatusDropDown',
                type: 'ui-select-single',
                templateOptions : {
                    placeholder: "Select status...",
                    valueProp: 'value',
                    labelProp: 'label',
                    optionsAttr: 'ui-options', ngOptions: 'ui-options',
                    options: [
                        {value: 0, label: 'Granted'},
                        {value: 1, label: 'Expired'},
                        {value: 2, label: 'Cancelled'},
                        {value: 3, label: 'Active'},
                    ]
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.FIELDS.TAB.STATUS.LABEL" | translate'
                }
            },
            {
                className: 'col-md-3 col-xs-12',
                key: 'bonusNameAndCodeDropDown',
                type: 'ui-select-multiple',
                templateOptions: {
                    label: '',
                    placeholder: "Select bonus codes...",
                    valueProp: 'value',
                    labelProp: 'label',
                    optionsAttr: 'ui-options',
                    ngOptions: 'ui-options',
                    options: [],
                    required: true

                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.FIELDS.TAB.BONUS_NAME_AND_CODE.LABEL" | translate'
                },
                controller: ['$scope', function($scope) {
                    let playerDomains = [];
                    for(let n = 0 ; n < controller.domains.length; n++) {
                        playerDomains[n] = controller.domains[n].name;
                    }
                    restCasino.activeBonusCodes(playerDomains).then(function (response) {
                        let dataResponse = response;
                        for(let s = 0; s < dataResponse.length; s++) {
                            let bonusCodes = {};
                            bonusCodes.value = s;
                            bonusCodes.label = dataResponse[s];
                            bonusCodesArray.push(bonusCodes);
                        }
                        $scope.options.templateOptions.options = bonusCodesArray;
                        return bonusCodesArray;
                    });
                }]
            }
        ];

        $scope.$watch(function() { return $scope.model.brandDropDown;}, function(newValue, oldValue) {
            if (newValue) {
                restCasino.activeBonusCodes(newValue).then(function (response) {
                    let dataResponse = response; let bonusCodesArray = [];
                    for (let s = 0; s < dataResponse.length; s++) {
                        let bonusCodes = {};
                        bonusCodes.value = s;
                        bonusCodes.label = dataResponse[s];
                        bonusCodesArray.push(bonusCodes);
                    }
                    $scope.fields[4].templateOptions.options = bonusCodesArray; return bonusCodesArray;
                });
            }
        });

        $scope.refresh = function() {
            $scope.activeBonusesTable.instance.reloadData(() => {}, true);
        }

        $scope.applyFilter = function() {
            if(moment($scope.model.dateRangeFrom).isAfter($scope.model.dateRangeTo)) {
                notify.error('"UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.ERRORS.DATE_FILTER" | translate');
            } else {
                $scope.refresh();
            }
        }

        $scope.selectedBonusCodes = function(data) {
            let bonusCodes = [];
           for(let i = 0; i < data.length; i++) {
               bonusCodes[i] = data[i].label;
           }
           return bonusCodes;
        }

        $scope.getDomainNames = function(domains) {
            let domainNames = []
            if(Array.isArray(domains)) {
                for (let i = 0; i < domains.length; i++) {
                    domainNames.push(domains[i].name)
                }
            } else {
                domainNames[0] = domains;
            }
            return domainNames;
        }

        $scope.resetFilter = function () {
            $scope.model = {
                dateRangeFrom: $scope.startDate,
                dateRangeTo: $scope.endDate,
                bonusNameAndCodeDropDown: bonusCodesArray
            }
            $scope.applyFilter();
        }

        const activeBonusesUrl  = 'services/service-casino/casino/bonus/find/bonus-token/active/table';
        let dtOptions = DTOptionsBuilder.newOptions().withOption('order', [[0, 'desc']]).withOption('bFilter', false);
        $scope.paintCell = function (data, value, lclass) {
            return '<div class="limited text-wrap ' + lclass + '" '
                +'title="' + value + '">' + value + '</div>';
        }

        $scope.activeBonusesTable  = $dt.builder()
            .column($dt.columnformatdatetime('grantDate').withTitle($translate('UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.TABLE.GRAND_DATE')).notSortable())
            .column($dt.linkscolumn($translate('UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.TABLE.PLAYER_GUID'), [{
                permission: "player_view",
                permissionType: "any",
                permissionDomain: function (data) {
                    return data.playerGuid.split('/')[0];
                },
                title: function (data) {
                    return data.playerGuid;
                },
                target: "_blank",
                href: function (data) {
                    return $state.href("dashboard.players.guidredirect", {domainName: data.playerGuid.split('/')[0], usernameOrId: data.playerGuid.split('/')[1]})
                }
            }]))
            .column($dt.column('bonusRevisionId').withTitle( $translate('UI_NETWORK_ADMIN.PLAYER.BONUS.REVISION_CODE')).notSortable())
            .column($dt.column('bonusId').withTitle( $translate('UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.TABLE.BONUS_ID')).notSortable())
            .column($dt.column('bonusCode').withTitle($translate('UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.TABLE.BONUS_CODE')).notSortable())
            .column($dt.column('bonusName').withTitle( $translate('UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.TABLE.BONUS_NAME')).notSortable())
            .column($dt.columncurrencysymbolcents('amount', 'currencySymbol', 2)
                .withTitle($translate("UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.TABLE.CASH_AMOUNT")))
            .column($dt.labelcolumn($translate('UI_NETWORK_ADMIN.ACTIVE_BONUS.PAGE.TABLE.STATUS'),
                new Array({
                    text: function (data) {
                        if(data.completed == true) {
                            return 'GRANTED';
                        } else if (data.expired == true) {
                            return 'EXPIRED';
                        } else if (data.cancelled == true) {
                            return 'CANCELLED';
                        } else if (!data.completed && !data.expired && !data.cancelled) {
                            return "ACTIVE";
                        } else {
                            return "UNKNOWN"
                        }
                    },
                    lclass: function(data)  {
                        if (data.completed == true) {
                            return "default label-columns bg-green";
                        } if (!data.completed && !data.expired && !data.cancelled) {
                            return "default label-columns bg-teal";
                        } else {
                            return "danger label-columns";
                        }
                    }
                })))
            .options({
            url: activeBonusesUrl,
            type: 'POST',
            data: function (s) {
                s.domains = ($scope.model.brandDropDown !== null && $scope.model.brandDropDown !== undefined) ? $scope.getDomainNames($scope.model.brandDropDown):
                    $scope.getDomainNames(controller.domains);
                s.bonusCodes =  ($scope.model.bonusNameAndCodeDropDown !== undefined && $scope.model.bonusNameAndCodeDropDown !== null) ?
                    $scope.selectedBonusCodes($scope.model.bonusNameAndCodeDropDown) : null;
                s.status = ($scope.model.activeBonusStatusDropDown !== undefined && $scope.model.activeBonusStatusDropDown !== null) ? $scope.statusTranslate($scope.model.activeBonusStatusDropDown) : null;
                s.dateRangeFrom = ($scope.model.dateRangeFrom !== undefined && $scope.model.dateRangeFrom !== null)? $scope.formatDate($scope.model.dateRangeFrom)
                    : $scope.formatDate($scope.startDate);
                s.dateRangeTo = ($scope.model.dateRangeTo !== undefined && $scope.model.dateRangeTo !== null) ? $scope.formatDate($scope.model.dateRangeTo)
                    : $scope.formatDate($scope.endDate);
            }
        }, null, dtOptions, null).build();
    }
]);
