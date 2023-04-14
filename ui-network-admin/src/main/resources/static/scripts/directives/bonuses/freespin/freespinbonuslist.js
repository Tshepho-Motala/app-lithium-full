'use strict';

angular.module('lithium').directive('freespinBonusesList', function() {
    return {
        templateUrl:'scripts/directives/bonuses/freespin/freespinbonuslist.html',
        scope: {
            data: '='
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$dt', '$translate', 'rest-casino', '$scope', 'DTOptionsBuilder', '$filter', 'notify',
            function($dt, $translate, casinoRest, $scope, DTOptionsBuilder, $filter, notify) {

                console.debug("data", $scope.data);
                var controller = this;
                controller.data = $scope.data;

                controller.startDate = moment(new Date()).subtract(6, "months").format("YYYY-MM-DD");
                controller.endDate = moment(new Date()).add(1, "days").format("YYYY-MM-DD");
                controller.legendCollapsed = false;
                controller.model = {
                    bonusCodes: controller.data.bonusCodes,
                    dateRangeStart : controller.startDate,
                    dateRangeEnd: controller.endDate
                }

                controller.formatDate = function(date) {
                    return $filter('date')(date, 'yyyy-MM-dd');
                }

                controller.formatDatePicker = function(date) {
                    return $filter('date')(date, 'dd/MM/yyyy');
                }

                controller.fields = [
                    {
                        className: 'col-md-4 col-xs-12',
                        key: 'dateRangeStart',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            label: 'Date: Range Start',
                            placeholder: controller.formatDatePicker(controller.startDate),
                            required: true,
                            datepickerOptions: {
                                initDate: controller.formatDatePicker(controller.startDate),
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function() { controller.fields[1].templateOptions.datepickerOptions.minDate = controller.model.dateRangeStart; }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.FIELDS.DATERANGESTART.LABEL" | translate',
                        }
                    },{
                        className: 'col-md-4 col-xs-12',
                        key: 'dateRangeEnd',
                        type: 'datepicker',
                        optionsTypes: ['editable'],
                        templateOptions: {
                            label: 'Date: Range End',
                            placeholder: controller.formatDatePicker(controller.endDate),
                            required: true,
                            datepickerOptions: {
                                initDate: controller.formatDatePicker(controller.endDate),
                                format: 'dd/MM/yyyy'
                            },
                            onChange: function() { controller.fields[0].templateOptions.datepickerOptions.maxDate = controller.model.dateRangeEnd; }
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.FIELDS.DATERANGEEND.LABEL" | translate',
                        }
                    },{
                        className: 'col-md-4 col-xs-12',
                        key: 'bonusCodes',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            label: "Bonus Codes",
                            placeholder: "Select bonus codes...",
                            valueProp: 'bonusCode',
                            labelProp: 'bonusCode',
                            optionsAttr: 'ui-options',
                            ngOptions: 'ui-options',
                            options: controller.model.bonusCodes,
                            required: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.FIELDS.BONUSCODES.LABEL" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.FIELDS.BONUSCODES.PLACEHOLDER" | translate'
                        }
                    }
                ]

                controller.toggleLegendCollapse = function() {
                    controller.legendCollapsed = !controller.legendCollapsed;
                }

                controller.resetFilter = function (collapse) {
                    if (collapse) {
                        controller.toggleLegendCollapse();
                    }
                    controller.model = {
                        bonusCodes: controller.data.bonusCodes,
                        dateRangeStart : controller.startDate,
                        dateRangeEnd: controller.endDate
                    }
                    controller.applyFilter(true);
                }

                controller.applyFilter = function (toggle) {
                    //Make sure start date not greater than end date
                    if(moment(controller.model.dateRangeStart).isAfter(controller.model.dateRangeEnd)) {
                        notify.error("Your start date can't be after your end date");
                    } else {
                        controller.filteredBonusCodes = controller.getBonusCodes(controller.model.bonusCodes);
                        //Make sure that the user has selected at least one bonusCode
                        if(controller.filteredBonusCodes.length == 0){
                            notify.error("Your must select at least one Cash Bonus Code on the filter");
                        } else {
                            if (toggle === true) {
                                controller.toggleLegendCollapse();
                            }
                            controller.refresh();
                        }
                    }
                }

                controller.refresh = function () {
                    controller.cashBonusTable.instance.reloadData(function () {
                    }, true);
                }

                controller.getBonusCodes = function(bonusCodes) {
                    let codes = [];
                    for (let i = 0; i < bonusCodes.length; i++) {
                        codes[i] = bonusCodes[i].bonusCode;
                    }
                    return codes;
                }

                controller.paintCell = function (data, value, lclass) {

                    return '<div class="limited text-wrap ' + lclass + '" '
                        +'title="' + value + '">' + value + '</div>';
                    // return '<div class="limited text-wrap" ' + (lclass ? 'style=""' : 'style="' + lclass + '"')
                    //     +'title="' + value + '">' + value + '</div>';
                }

                controller.paintGreen = function (data, value) {

                    // return '<div class="limited text-wrap ' + lclass + '" '
                    //     +'title="' + value + '">' + value + '</div>';
                    return '<div class="limited text-wrap" style="color: green" title="' + value + '">' + value + '</div>';
                }


                var baseUrl = "services/service-casino/backoffice/" + controller.data.user.domain.name + "/bonus/FREESPIN/history?playerGuid=" + controller.data.user.guid;
                var dtOptions = DTOptionsBuilder.newOptions().withOption('order', [[0, 'desc']]).withOption('bFilter', false) ;

                controller.cashBonusTable = $dt.builder()
                    .column($dt.columnformatdatetime('startedDate').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.GRANTDATE')))
                    .column($dt.column('bonus.id').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.BONUSID')))
                    .column($dt.column('bonus.bonusCode').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.BONUSCODE')))
                    .column($dt.column('bonus.bonusName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.BONUSNAME')))
                    .column($dt.columnWithClass(function (data) {return controller.paintCell(data, $filter('cents')(data.customFreeMoneyAmountCents, controller.data.currencySymbol), "")}).withTitle($translate('UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.AMOUNT')))
                    .column($dt.labelcolumn($translate('UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.STATUS'),
                        new Array({
                            text: function (data) {
                                if(data.completed == true) {
                                    return 'UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.GRANTED';
                                } else if (data.expired == true) {
                                    return 'UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.EXPIRED';
                                } else if (data.cancelled == true) {
                                    return 'UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.CANCELLED';
                                }
                            },
                            lclass: function(data)  {
                                if(data.completed == true) {
                                    return "default label-columns bg-green";
                                } else {
                                    return "danger label-columns";
                                }
                            }
                        })))
                    .column($dt.column('description').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.TAB.CASHBONUS.DESC')))
                    .options(
                        {
                            url: baseUrl,
                            type: 'POST',
                            data: function(d) {
                                d.dateRangeStart = (controller.model.dateRangeStart !== undefined && controller.model.dateRangeStart !== null) ? controller.formatDate(controller.model.dateRangeStart) : controller.formatDate(controller.startDate);
                                d.dateRangeEnd = (controller.model.dateRangeEnd !== undefined && controller.model.dateRangeEnd !== null) ? controller.formatDate(controller.model.dateRangeEnd) : controller.formatDate(controller.endDate);
                                d.bonusCodes = controller.getBonusCodes(controller.model.bonusCodes);
                            }
                        },
                        null,
                        dtOptions,
                        null
                    ).build();
            }
        ]
    }
});
