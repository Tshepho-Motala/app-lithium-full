'use strict';

angular.module('lithium')
    .directive('accountinghistory', function () {

            return {
                templateUrl: 'scripts/directives/accounting/history/history.html',
                scope: {
                    inputPlayer: "=?",
                    inputSportsbook: "=?"
                },
                restrict: 'E',
                replace: true,
                controllerAs: 'controller',
                controller: [
                    '$state', '$filter', '$dt', 'rest-domain', 'UserRest', 'rest-casino', 'rest-accounting-internal', '$translate', '$compile', 'DTOptionsBuilder', '$scope', "notify", "$stateParams",
                    function ($state, $filter, $dt, domainRest, userRest, casinoRest, accountInternalRest, $translate, $compile, DTOptionsBuilder, $scope, notify, $stateParams) {
                        var controller = this;
                        var filterApplied = true;

                        controller.startDate = moment(new Date()).subtract(6, "months").format("YYYY-MM-DD");
                        controller.endDate = moment(new Date()).add(1, "days").format("YYYY-MM-DD");
                        controller.domainName = $stateParams.domainName;
                        controller.orderAsc = [];
                        controller.orderDesc = [];

                        controller.formatDate = function (date) {
                            return $filter('date')(date, 'yyyy-MM-dd');
                        }

                        controller.formatDatePicker = function (date) {
                            return $filter('date')(date, 'dd/MM/yyyy');
                        }

                        controller.playerSpecificScreen = !!$scope.inputPlayer;
                        controller.sportbookSpecificScreen = !!$scope.inputSportsbook;
                        controller.legendCollapsed = false;
                        controller.model = {};
                        controller.fields = [
                            {
                                className: 'col-md-4 col-xs-12',
                                key: 'transactionId',
                                type: 'input',
                                templateOptions: {
                                    label: 'Tran ID',
                                    description: '',
                                    placeholder: ''
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.TRANID.LABEL" | translate',
                                }
                            }, {
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
                                    onChange: function () {
                                        controller.fields[2].templateOptions.datepickerOptions.minDate = controller.model.dateRangeStart;
                                    }
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.DATERANGESTART.LABEL" | translate',
                                }
                            }, {
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
                                    onChange: function () {
                                        controller.fields[1].templateOptions.datepickerOptions.maxDate = controller.model.dateRangeEnd;
                                    }
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.DATERANGEEND.LABEL" | translate',
                                }
                            }, {
                                className: 'col-md-4 col-xs-12',
                                key: 'providerGuid',
                                type: "ui-select-single",
                                templateOptions: {
                                    label: 'Provider',
                                    description: '',
                                    placeholder: '',
                                    valueProp: 'value',
                                    labelProp: 'value',
                                    optionsAttr: 'ui-options',
                                    ngOptions: 'ui-options',
                                    options: []
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.PROVIDER.LABEL" | translate',
                                },
                                controller: ['$scope', function ($scope) {
                                    accountInternalRest.labelValuesByLabelName('provider_guid', 10000, 0).then(function (response) {
                                        var result = [];
                                        for (var i = 0; i < response.content.length; i++) {
                                            var obj = response.content[i];
                                            if ((!!obj['value']) && (typeof obj['value'] == 'string')) {
                                                if (obj['value'].includes('/')) {
                                                    result.push(obj);
                                                }
                                            }
                                        }
                                        $scope.to.options = result;
                                    });
                                }]
                            }, {
                                className: 'col-md-4 col-xs-12',
                                key: 'providerTransId',
                                type: 'input',
                                templateOptions: {
                                    label: 'Provider Tran ID',
                                    description: '',
                                    placeholder: ''
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.PROVIDERTRANID.LABEL" | translate',
                                }
                            }, {
                                className: 'col-md-4 col-xs-12',
                                key: 'additionalTransId',
                                type: 'input',
                                templateOptions: {
                                    label: 'Additional Tran ID',
                                    description: '',
                                    placeholder: ''
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ADDITIONALTRANID.LABEL" | translate',
                                }
                            },{
                                className: 'col-md-4 col-xs-12',
                                key: 'roundId',
                                type: 'input',
                                templateOptions: {
                                    label: 'Round ID',
                                    description: '',
                                    placeholder: ''
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ROUNDID.LABEL" | translate',
                                }
                            },
                            {
                                className: 'col-md-4 col-xs-12',
                                key: 'accountCode',
                                type: 'ui-select-multiple',
                                templateOptions: {
                                    valueProp: 'code',
                                    labelProp: 'code',
                                    optionsAttr: 'ui-options', ngOptions: 'ui-options',
                                    options: []
                                },
                                expressionProperties: {
                                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ACCOUNTCODE.LABEL" | translate'
                                },
                                controller: ['$scope', function ($scope) {
                                    accountInternalRest.fetchAccountCodes().then(function (response) {
                                        $scope.options.templateOptions.options = response;
                                        return response;
                                    });
                                }]
                            }

                        ];

                        //Set the default values for the first page load
                        controller.model.dateRangeStart = controller.startDate;
                        controller.model.dateRangeEnd = controller.endDate;

                        controller.toggleLegendCollapse = function () {
                            controller.legendCollapsed = !controller.legendCollapsed;
                        }

                        controller.resetUserSearch = function () {
                            if (controller.playerSpecificScreen) {
                                controller.selectedUser = $scope.inputPlayer;
                            } else {
                                controller.selectedUser = undefined;
                            }
                        }

                        controller.searchUsers = function (search) {
                            return userRest.searchAllPlayers(search).then(function (searchResult) {
                                return searchResult.plain();
                            }).catch(function (error) {
                                errors.catch("", false)(error)
                            });
                        }

                        controller.resetFilter = function (collapse) {
                            if (collapse) {
                                controller.toggleLegendCollapse();
                            }
                            if (controller.playerSpecificScreen) {
                                controller.selectedUser = $scope.inputPlayer;
                            } else {
                                controller.selectedUser = undefined;
                            }
                            controller.model.transactionId = null;
                            controller.model.dateRangeStart = controller.startDate;
                            controller.model.dateRangeEnd = controller.endDate;
                            controller.model.providerGuid = null;
                            controller.model.providerTransId = null;
                            controller.model.roundId = null;
                            controller.model.additionalTransId = null;
                            controller.model.accountCode = null;
                            controller.applyFilter(true);
                        }

                        controller.applyFilter = function (toggle) {
                            //Check if dates are filled, if not then populate them.
                            if (controller.model.dateRangeStart === undefined || controller.model.dateRangeStart === null) {
                                controller.fields[0].value = controller.formatDatePicker(controller.startDate);
                                controller.model.dateRangeStart = controller.formatDate(controller.startDate);
                            }

                            if (controller.model.dateRangeEnd === undefined || controller.model.dateRangeEnd === null) {
                                controller.fields[1].value = controller.formatDatePicker(controller.endDate);
                                controller.model.dateRangeEnd = controller.formatDate(controller.endDate);
                            }

                            //Make sure start date not greater than end date
                            if (moment(controller.model.dateRangeStart).isAfter(controller.model.dateRangeEnd)) {
                                notify.error("Your start date can't be after your end date");
                            } else {
                                if (toggle === true) {
                                    controller.toggleLegendCollapse();
                                }
                                filterApplied = true;
                                controller.refresh();
                            }
                        }

                        controller.addOrderingField = function (order, field){
                            //Check to see if label value is linked to a valid table field
                            if (controller.transactionsTable.columns.$$state.value.filter(col => col.mData === field).length > 0) {
                                if (order === "asc") {
                                    controller.orderAsc.push(field);
                                } else if (order == "desc") {
                                    controller.orderDesc.push(field);
                                }
                            }
                        }
                        controller.transactionOrdering = async function () {
                            await domainRest.view($stateParams.domainName).then(function (response) {
                                let labelValues = response.current ? response.current.labelValueList : [];
                                for (let i = 0; i < labelValues.length; i++) {
                                    if (labelValues[i].label.name == 'accountingTransactionOrderingFieldsDesc' || labelValues[i].label.name == 'accountingTransactionOrderingFieldsAsc') {
                                        let fields = labelValues[i].labelValue.value ? labelValues[i].labelValue.value.split(",") : [];
                                        for (let j = 0; j < fields.length; j++) {
                                            if (labelValues[i].label.name == 'accountingTransactionOrderingFieldsDesc') {
                                                controller.addOrderingField('desc', fields[j]);
                                            } else {
                                                controller.addOrderingField('asc', fields[j]);
                                            }
                                        }
                                    }
                                }

                                if (controller.orderDesc.length > 0 || controller.orderAsc.length > 0) {
                                    let cols = controller.transactionsTable.columns.$$state.value;
                                    //Resetting any ordering that comes default with datatables
                                    controller.transactionsTable.instance.DataTable.order([]);
                                    for (let i = 0; i < cols.length; i++) {
                                        let col = (cols[i].mData ? cols[i].mData : "");
                                        if (controller.orderDesc.includes(col) || controller.orderAsc.includes(col)) {
                                            if (controller.orderDesc.includes(col)) {
                                                controller.transactionsTable.instance.DataTable.order().push([i, 'desc']);
                                            } else {
                                                controller.transactionsTable.instance.DataTable.order().push([i, 'asc']);
                                            }
                                        }
                                    }
                                } else {
                                    //Defaults to ordering transactions by transactionId
                                    controller.transactionsTable.instance.DataTable.order([0, 'desc']);
                                }
                                filterApplied = true;
                                controller.refresh();
                            });
                        }

                        var baseUrl = "services/service-accounting-history/admin/transactions/table?1=1";
                        var dtOptions = DTOptionsBuilder.newOptions().withOption('bFilter', false).withOption('order', [[0, 'desc']]).withOption('stateSave', false).withOption('createdRow', function (row, data, dataIndex) {
                            // Recompiling so we can bind Angular directive to the DT
                            // TODO NB - look at
                            // https://l-lin.github.io/angular-datatables/archives/#!/bindAngularDirective
                            // https://gitlab.com/playsafe/lithium/app-lithium-full/-/merge_requests/899/diffs?diff_id=105694473#8fed9f8d2f4c2706dcb6a9d8d739ef9d9def8e51
                            // https://gitlab.com/playsafe/lithium/app-lithium-full/-/merge_requests/899/diffs?diff_id=105694473#bf0f178108a7357710f047974c182764b7047991
                            $compile(angular.element(row).contents())($scope);
                        });
                        controller.transactionOrdering();
                        var dtBuilder = $dt.builder();

                        dtBuilder
                            .column($dt.column('transaction.id').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.TRANID.LABEL')));

                        if (!controller.playerSpecificScreen) {

                            dtBuilder.column($dt.linkscolumn($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.PLAYER.LABEL'), [{
                                permission: "player_*",
                                permissionType: "any",
                                permissionDomain: function (data) {
                                    return data.account.owner.guid.split('/')[0];
                                },
                                title: function (d) {
                                    return d.account.owner.guid;
                                },
                                target: "_blank",
                                href: function (data) {
                                    return $state.href('dashboard.players.guidredirect', {
                                        domainName: data.account.owner.guid.split('/')[0],
                                        usernameOrId: data.account.owner.guid.split('/')[1]
                                    });

                                }
                            }]));
                        }

                        dtBuilder.column($dt.columnformatdatetime('date').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.DATE.LABEL')).notSortable())
                            .column($dt.column('transaction.transactionType.code').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.TRANTYPE.LABEL')).notSortable())
                            .column($dt.column('account.currency.code').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.TRANCURRENCY.LABEL')).notSortable())
                            .column($dt.columncurrencypos('amountCents', '').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.AMOUNTPOS.LABEL')).notSortable())
                            .column($dt.columncurrencyneg('amountCents', '').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.AMOUNTNEG.LABEL')).notSortable())
                            .column($dt.column('account.accountCode.code').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ACCOUNTCODE.LABEL')).notSortable())
                            .column($dt.columncurrency('postEntryAccountBalanceCents', '').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ACCOUNTBALANCE.LABEL')).notSortable())
                            .column($dt.columnprovider('details.providerGuid').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.PROVIDER.LABEL')).notSortable())
                            .column($dt.linkscolumn($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.PROVIDERTRANID.LABEL'),
                                [{
                                    title: function (data) {
                                        return data.details.externalTranId;
                                    },
                                    href: function (data) {
                                        var res = data.details.externalTransactionDetailUrl
                                            ? data.details.externalTransactionDetailUrl
                                            : null;
                                        return res;
                                    },
                                    target: "_blank",
                                    condition: function (data) {
                                        var res = !!(data.details.externalTranId &&
                                            (data.details.externalTransactionDetailUrl &&
                                                data.details.externalTransactionDetailUrl.length > 0));
                                        return res;
                                    },
                                    fallback: function (data) {
                                        var res = data.details.externalTranId ? data.details.externalTranId : '&nbsp;';
                                        return res;
                                    },
                                    permission: "player_accounting_sportsbook_history_view,player_accounting_history_view,global_accounting_view,player_view,player_edit",
                                    permissionType: "any",
                                    permissionDomain: function () {
                                        // TODO: When global accounting history is switched back this must be rechecked
                                        return controller.domainName;
                                    },
                                }
                                ]))
                            .column($dt.linkscolumn($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ROUNDID.LABEL'),
                                [ {
                                    title: function (data) {
                                        return data.details.roundId;
                                    },
                                    click: function (event){
                                        let domainName = event.account.domain.name;
                                        let providerId, gameKey, roundId, playerId = "";
                                        if(event.details.providerGuid.split("/").length>0){
                                            providerId =event.details.providerGuid.split("/")[1];
                                        }
                                        if(event.details.gameGuid.lastIndexOf("_") > -1)
                                        {
                                            gameKey = event.details.gameGuid.substring(event.details.gameGuid.lastIndexOf("_") + 1);
                                        }
                                        roundId = event.details.roundId;
                                        if(event.account.owner.guid.split("/").length > 0){
                                            playerId = event.account.owner.guid.split("/")[1];
                                        }
                                        casinoRest.getRoundReplay(domainName, providerId, gameKey, roundId, playerId).then(function (response) {
                                            window.open(response.replayUrl?response.replayUrl:"", "_blank");
                                        });
                                    },
                                    target: "_blank",
                                    condition: function(data) {
                                        var res = !!(data.details.roundId &&
                                            (data.details.roundId &&
                                                data.details.roundId.length > 0));
                                        return res;
                                    },
                                    fallback: function(data) {
                                        var res = data.details.roundId ? data.details.roundId : '&nbsp;';
                                        return res;
                                    },
                                    permission: "player_accounting_sportsbook_history_view,player_accounting_history_view,global_accounting_view,player_view,player_edit",
                                    permissionType: "any"
                                }
                                ]))
                            .column($dt.columnformatdatetime('details.externalTimestamp').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.EXTERNALTIMESTAMP.LABEL')).notSortable())
                            .column($dt.column('details.additionalTranId').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ADDITIONALTRANID.LABEL')).notSortable())
                            .column($dt.column('details.gameName').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.GAMENAME.LABEL')).notSortable())
                            .column($dt.column('details.bonusRevisionId').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.BONUSREVISION.LABEL')).notSortable())
                            .column($dt.column('details.bonusName').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.BONUSNAME.LABEL')).notSortable())
                            .column($dt.column('details.bonusCode').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.BONUSCODE.LABEL')).notSortable());

                        if (!controller.sportbookSpecificScreen) {
                            dtBuilder.column($dt.column('details.processingMethod').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.PROCESSINGMETHOD.LABEL')).notSortable());
                        }

                        dtBuilder.column($dt.column('details.accountingClientTranId').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ACC_CLIENT_TRANID.LABEL')).notSortable())
                            .column($dt.column('details.accountingClientExternalId').withTitle($translate('UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.ACC_CLIENT_EXTID.LABEL')).notSortable())
                            .options(
                                {
                                    url: baseUrl,
                                    type: 'POST',
                                    data: function (d) {
                                        if (controller.playerSpecificScreen) {
                                            d.userGuid = $scope.inputPlayer.guid;
                                        } else if (!!controller.selectedUser) {
                                            // only visible if player is not defined on screen
                                            d.userGuid = controller.selectedUser.guid;
                                        }

                                        if (filterApplied) {
                                            d.start = 0;
                                            filterApplied = false;
                                        }

                                        d.transactionId = controller.model.transactionId;
                                        d.dateRangeStart = (controller.model.dateRangeStart !== undefined && controller.model.dateRangeStart !== null) ? controller.formatDate(controller.model.dateRangeStart) : controller.formatDate(controller.startDate);
                                        d.dateRangeEnd = (controller.model.dateRangeEnd !== undefined && controller.model.dateRangeEnd !== null) ? controller.formatDate(controller.model.dateRangeEnd) : controller.formatDate(controller.endDate);
                                        d.providerGuid = (controller.model.providerGuid !== undefined && controller.model.providerGuid !== null) ? controller.model.providerGuid : null;
                                        d.providerTransId = (controller.model.providerTransId !== undefined && controller.model.providerTransId !== null) ? controller.model.providerTransId : null;
                                        d.roundId = (controller.model.roundId !== undefined && controller.model.roundId !== null) ? controller.model.roundId : null;
                                        d.additionalTransId = (controller.model.additionalTransId !== undefined && controller.model.additionalTransId !== null) ? controller.model.additionalTransId : null;
                                        d.accountCode =  arrayAsString(controller.model.accountCode, 'code');
                                        d.domainName = controller.domainName;

                                        if (controller.sportbookSpecificScreen) {
                                            d.transactionType = 'SPORTS_BET,SPORTS_RESERVE,SPORTS_WIN,SPORTS_LOSS,SPORTS_RESETTLEMENT,SPORTS_FREE_BET,SPORTS_FREE_WIN,SPORTS_FREE_LOSS,SPORTS_FREE_RESETTLEMENT,SPORTS_DEBIT,SPORTS_RESERVE,SPORTS_RESERVE_CANCEL,SPORTS_RESERVE_COMMIT';
                                        }
                                    }
                                },
                                null,
                                dtOptions,
                                null
                            );

                        controller.transactionsTable = dtBuilder.build();

                        controller.refresh = function () {
                            controller.transactionsTable.instance.reloadData(function () {
                            }, false);
                        }

                        function arrayAsString(arr, fieldName) {
                            let str = "";
                            angular.forEach(arr, function (d) {
                                str += d[fieldName] + ",";
                            });
                            return str;
                        }
                    }
                ]
            };
        }
    );
