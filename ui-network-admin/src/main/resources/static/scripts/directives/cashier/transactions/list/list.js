'use strict';

angular.module('lithium').directive('cashiertranlist', function() {
    return {
        templateUrl:'scripts/directives/cashier/transactions/list/list.html',
        scope: {
            data: '=',
            reset: '='
        },
        restrict: 'E',
        replace: true,
        controller: ['$dt', '$translate', 'rest-cashier', 'UserRest', '$state', '$scope', '$stateParams', '$uibModal',
                     'notify', 'errors', '$rootScope', '$http', '$interval', 'DocumentGenerationRest', 'StatusRest',
            function($dt, $translate, cashierRest, userRest, $state, $scope, $stateParams, $uibModal, notify, errors,
                     $rootScope, $http, $interval, documentRest, statusRest) {
                console.debug("data", $scope.data);

                $scope.allowUserSearch = ($scope.data.allowUserSearch !== undefined && $scope.data.allowUserSearch !== null) ? $scope.data.allowUserSearch : true;
                $scope.allowAddManualTran = ($scope.data.allowAddManualTran !== undefined && $scope.data.allowAddManualTran !== null) ? $scope.data.allowAddManualTran : false;

                $scope.types = [
                    {type:"", translate:"...", tran:""},
                    {type:"deposit", translate:"UI_NETWORK_ADMIN.CASHIER.TYPES.DEPOSIT", tran:"DEPOSIT"},
                    {type:"withdraw", translate:"UI_NETWORK_ADMIN.CASHIER.TYPES.WITHDRAWAL", tran:"WITHDRAWAL"}
                ];
                $scope.model = {
                    autoApproved: 0,
	                testAccount: 0,
                    selectedType: $scope.types[0],
                    selectedMethod: undefined,
                    selectedProcessor: undefined,
                    selectedUser: $scope.data.selectedUser,
                    selectedPaymentType: undefined,
                    selectedStatus: undefined,
                    lastFourDigits: undefined,
                    createdDateRangeStart: undefined,
                    createdDateRangeEnd: new Date(),
                    updatedDateRangeStart: undefined,
                    updatedDateRangeEnd: undefined,
                    registrationDateRangeStart: undefined,
                    registrationDateRangeEnd: undefined,
                    transactionId: undefined,
                    transactionRuntimeQuery: {
                        operator: '=',
                        value: undefined
                    },
                    dateStartAfterFilter: '',
                    dateEndAfterFilter: '',
                    transactionTags: undefined,
	                userTags: undefined,
	                userStatuses: undefined,
	                depositCount: {
		                operator: '=',
		                value: undefined
	                },
	                daysSinceFirstDeposit: {
		                operator: '=',
		                value: undefined
	                },
	                transactionAmount: {
		                operator: '=',
		                value: undefined
	                },
	                activePaymentMethodCount: {
		                operator: '=',
		                value: undefined
	                }
                };

                if ($scope.reset) {
                    $scope.model.createdDateRangeStart = new Date(new Date().getTime() - 86400000)
                }

                $scope.legendCollapsed = true;
                $scope.hideTranTable = false;

                var transactionType = $scope.model.selectedType;
                var dmpId = -1;
                var guid = "";
                var domainMethodId = -1;
                var baseUrl = "services/service-cashier/cashier/transaction/table?dm=&domain="+$stateParams.domainName;
                if ($scope.model.selectedUser !== undefined && $scope.model.selectedUser !== null &&
                        $scope.model.selectedUser.guid !== undefined && $scope.model.selectedUser.guid !== null) {
                    baseUrl += "&guid=" + encodeURIComponent($scope.model.selectedUser.guid);
                } else {
                    baseUrl += "&guid=";
                }
                if ($scope.model.createdDateRangeStart) {
                    const date = $scope.model.createdDateRangeStart
                    const creStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
                    baseUrl += "&cresd=" + creStart;
                }
                if ($scope.model.createdDateRangeEnd) {
                    const date = $scope.model.createdDateRangeEnd
                    const creEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
                    baseUrl += "&creed=" + creEnd;
                }
                if ($scope.model.updatedDateRangeStart) {
                    const date = $scope.model.updatedDateRangeStart
                    const updStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
                    baseUrl += "&updsd=" + updStart;

                }
                if ($scope.model.updatedDateRangeEnd) {
                    const date = $scope.model.updatedDateRangeEnd
                    const updEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
                    baseUrl += "&upded=" + updEnd;
                }
	            if ($scope.model.registrationDateRangeStart) {
		            const date = $scope.model.registrationDateRangeStart
		            const registrationStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
		            baseUrl += "&registrationStart=" + registrationStart;

	            }
	            if ($scope.model.registrationDateRangeEnd) {
		            const date = $scope.model.registrationDateRangeEnd
		            const registrationEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
		            baseUrl += "&registrationEnd=" + registrationEnd;
	            }
                console.debug("baseUrl", baseUrl);

                console.debug("$stateParams.granularity", $stateParams.granularity);

                if (!angular.isUndefined($stateParams.granularity)) {
                    if (!angular.isUndefined($stateParams.status)) {
                        baseUrl += "&status="+$stateParams.status;
                        $scope.model.selectedStatus = [];
                        $scope.model.selectedStatus[0] = { code: $stateParams.status };
                    }
                    if (!angular.isUndefined($stateParams.paymentType)) {
                        baseUrl += "&paymentType=" + $stateParams.paymentType;
                        $scope.model.selectedPaymentType = {};
                        $scope.model.selectedPaymentType.paymentType = $stateParams.paymentType;
                    }
                    if ($stateParams.offset === -1) {
                        baseUrl += "&cresd=-1&creed=-1&updsd=-1&upded=-1@&registrationStart=-1&registrationEnd=-1";
                    } else {
                        var creStart = luxon.DateTime.local();
                        var creEnd = luxon.DateTime.local();
                        var updStart = luxon.DateTime.local();
                        var updEnd = luxon.DateTime.local();
                        var registrationStart = luxon.DateTime.local();
                        var registrationEnd = luxon.DateTime.local();

                        switch ($stateParams.granularity) {
                            case 1: { // yearly
                                creStart = luxon.DateTime.local().startOf('year').minus({year: $stateParams.offset});
                                creEnd = luxon.DateTime.local().endOf('year').minus({year: $stateParams.offset});
                                updStart = luxon.DateTime.local().startOf('year').minus({year: $stateParams.offset});
                                updEnd = luxon.DateTime.local().endOf('year').minus({year: $stateParams.offset});
	                            registrationStart = luxon.DateTime.local().startOf('year').minus({year: $stateParams.offset});
	                            registrationEnd = luxon.DateTime.local().endOf('year').minus({year: $stateParams.offset});
                                baseUrl += "&cresd=" + creStart.ts + "&creed=" + creEnd.ts + "&updsd=" + updStart.ts + "&upded" + updEnd.ts + "&registrationStart" + registrationStart.ts + "&registrationEnd" + registrationEnd.ts;
                                break;
                            }
                            case 2: { // monthly
                                creStart = luxon.DateTime.local().startOf('month').minus({month: $stateParams.offset});
                                creEnd = luxon.DateTime.local().endOf('month').minus({month: $stateParams.offset});
                                updStart = luxon.DateTime.local().startOf('month').minus({month: $stateParams.offset});
                                updEnd = luxon.DateTime.local().endOf('month').minus({month: $stateParams.offset});
	                            registrationStart = luxon.DateTime.local().startOf('month').minus({month: $stateParams.offset});
	                            registrationEnd = luxon.DateTime.local().endOf('month').minus({month: $stateParams.offset});
                                baseUrl += "&cresd=" + creStart.ts + "&creed=" + creEnd.ts + "&updsd=" + updStart.ts + "&upded" + updEnd.ts + "&registrationStart" + registrationStart.ts + "&registrationEnd" + registrationEnd.ts;
                                break;
                            }
                            case 3: { // daily
                                creStart = luxon.DateTime.local().startOf('day').minus({day: $stateParams.offset});
                                creEnd = luxon.DateTime.local().endOf('day').minus({day: $stateParams.offset});
                                updStart = luxon.DateTime.local().startOf('day').minus({day: $stateParams.offset});
                                updEnd = luxon.DateTime.local().endOf('day').minus({day: $stateParams.offset});
	                            registrationStart = luxon.DateTime.local().startOf('day').minus({day: $stateParams.offset});
	                            registrationEnd = luxon.DateTime.local().endOf('day').minus({day: $stateParams.offset});
                                baseUrl += "&cresd=" + creStart.ts + "&creed=" + creEnd.ts + "&updsd=" + updStart.ts + "&upded" + updEnd.ts + "&registrationStart" +registrationStart.ts + "&registrationEnd" + registrationEnd.ts;
                                break;
                            }
                            case 4: { // weekly
                                creStart = luxon.DateTime.local().startOf('week').minus({week: $stateParams.offset});
                                creEnd = luxon.DateTime.local().endOf('week').minus({week: $stateParams.offset});
                                updStart = luxon.DateTime.local().startOf('week').minus({week: $stateParams.offset});
                                updEnd = luxon.DateTime.local().endOf('week').minus({week: $stateParams.offset});
	                            registrationStart = luxon.DateTime.local().startOf('week').minus({week: $stateParams.offset});
	                            registrationEnd = luxon.DateTime.local().endOf('week').minus({week: $stateParams.offset});
                                baseUrl += "&cresd=" + creStart.ts + "&creed=" + creEnd.ts + "&updsd=" + updStart.ts + "&upded" + updEnd.ts + "&registrationStart" +registrationStart.ts + "&registrationEnd" + registrationEnd.ts;
                                break;
                            }
                        }
                        $scope.model.createdDateRangeStart = creStart.toISO({ includeOffset: false, suppressMilliseconds:true });
                        $scope.model.createdDateRangeEnd = creEnd.toISO({ includeOffset: false, suppressMilliseconds:true });
                        $scope.model.updatedDateRangeStart = updStart.toISO({ includeOffset: false, suppressMilliseconds:true });
                        $scope.model.updatedDateRangeEnd = updEnd.toISO({ includeOffset: false, suppressMilliseconds:true });
	                    $scope.model.registrationDateRangeStart = registrationStart.toISO({ includeOffset: false, suppressMilliseconds:true });
	                    $scope.model.registrationDateRangeEnd = registrationEnd.toISO({ includeOffset: false, suppressMilliseconds:true });

                    }
                } else {
                    baseUrl += "&cresd=-1&creed=-1&updsd=-1&upded=-1&registrationStart=-1&registrationEnd=-1";
                }

                $scope.findDomainMethods = function(type) {
                    $scope.model.selectedMethod = undefined;
                    $scope.domainMethods = undefined;
                    $scope.model.selectedProcessor = undefined;
                    $scope.domainMethodProcessors = undefined;
                    dmpId = -1;
                    domainMethodId = -1;
                    if (type !== '') {
                        cashierRest.domainMethods($stateParams.domainName, type).then(function(dm) {
                            $scope.domainMethods = dm.plain();
                            $scope.domainMethods.unshift({id:-1, name:'...'});
                        }).catch(function(error) {
                            errors.catch("", false)(error)
                        });
                    }
                }

                $scope.findDomainMethodProcessors = function(dm) {
                    $scope.model.selectedProcessor = undefined;
                    $scope.domainMethodProcessors = undefined;
                    dmpId = -1;
                    if (dm !== '') {
                        cashierRest.domainMethodProcessors(dm).then(function(dmps) {
                            $scope.domainMethodProcessors = dmps.plain();
                            $scope.domainMethodProcessors.unshift({id:-1, name:'...'});
                        }).catch(function(error) {
                            errors.catch("", false)(error)
                        });
                    }
                }

                $scope.fields = [{
                    className: 'col-xs-6',
                    key: 'createdDateRangeStart',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: $translate.instant("UI_NETWORK_ADMIN.CASHIER.TRANS.FILTER.CREATED_START"),
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function() { $scope.fields[1].templateOptions.datepickerOptions.minDate = $scope.model.createdDateRangeStart; }
                    }
                },{
                    className: 'col-xs-6',
                    key: 'createdDateRangeEnd',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: $translate.instant("UI_NETWORK_ADMIN.CASHIER.TRANS.FILTER.CREATED_END"),
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function() { $scope.fields[0].templateOptions.datepickerOptions.maxDate = $scope.model.createdDateRangeEnd; }
                    }
                },{
                    className: 'col-xs-6',
                    key: 'updatedDateRangeStart',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: $translate.instant("UI_NETWORK_ADMIN.CASHIER.TRANS.FILTER.UPDATED_START"),
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function() { $scope.fields[3].templateOptions.datepickerOptions.minDate = $scope.model.updatedDateRangeStart; }
                    }
                },{
                    className: 'col-xs-6',
                    key: 'updatedDateRangeEnd',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: $translate.instant("UI_NETWORK_ADMIN.CASHIER.TRANS.FILTER.UPDATED_END"),
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function() { $scope.fields[2].templateOptions.datepickerOptions.maxDate = $scope.model.updatedDateRangeEnd; }
                    }
                },
               {
                   className: 'col-xs-6',
                   key: 'registrationDateRangeStart',
                   type: 'datepicker',
                   optionsTypes: ['editable'],
                   templateOptions: {
                       label: $translate.instant("UI_NETWORK_ADMIN.CASHIER.TRANS.FILTER.REGISTRATION_START"),
                       required: false,
                       datepickerOptions: {
                               format: 'dd/MM/yyyy'
                       },
                       onChange: function() { $scope.fields[3].templateOptions.datepickerOptions.minDate = $scope.model.registrationDateRangeStart; }
                      }
               },{
                    className: 'col-xs-6',
                    key: 'registrationDateRangeEnd',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: $translate.instant("UI_NETWORK_ADMIN.CASHIER.TRANS.FILTER.REGISTRATION_END"),
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function() { $scope.fields[2].templateOptions.datepickerOptions.maxDate = $scope.model.registrationDateRangeEnd; }
                        }
               },{
                    className: 'col-xs-6',
                    key: "processorReference",
                    type: "input",
                    templateOptions: {
                        label: "Processor Reference",
                        description: "",
                        placeholder: ""
                    }
               },{
                    className: 'col-xs-6',
                    key: "lastFourDigits",
                    type: "input",
                    templateOptions: {
                        label: "Descriptor",
                        description: "",
                        placeholder: ""
                    }
                },{
                    className: 'col-xs-6',
                    key: "transactionId",
                    type: "input",
                    templateOptions: {
                        label: "ID",
                        type: "number",
                        description: "",
                        placeholder: $translate.instant("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.LIST.FIELDS.ID.DESCRIPTION"),
                        min:100
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.TRANSACTION.LIST.FIELDS.ID.LABEL" | translate',
                        'templateOptions.description': ''
                    }
                },{
                    className: 'col-xs-6',
                    key: 'transactionRuntimeQuery',
                    type: 'conditional-input',
                    templateOptions: {
                        label: 'Transaction runtime (sec)',
                        description: '',
                        placeholder: '',
                        options: ['>','<', "!=", '=', '>=', '<='],
                        tooltip: 'Please use one of the following operators: <, >, =, <=, >=, != followed by the duration in seconds'
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.TRANSACTION_RUNTIME.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                        className: 'col-xs-6',
                        key: 'transactionTags',
                        type: 'ui-select-multiple',
                        templateOptions: {
                            label: 'Select Tags',
                            required: false,
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [],
                        },
                        controller: ['$scope', function ($scope) {
                            cashierRest.transactionTags().then(function (response) {
                                var list = [];
                                for (var i = 0; i < response.plain().length; i++) {
                                    list.push({
                                        value: i,
                                        include: true,
                                        val: response.plain()[i],
                                        label: response.plain()[i]
                                    });
                                }
                                for (var b = 0; b < response.plain().length; b++) {
                                    list.push({
                                        value: b + response.plain().length,
                                        include: false,
                                        val: response.plain()[b],
                                        label: "NOT_" + response.plain()[b]
                                    });
                                }
                                $scope.options.templateOptions.options = list;
                                return list;
                            });
                        }],
                    }, {
                        className: 'col-xs-6',
                        key: 'testAccount',
                        type: 'ui-select-single',
                        templateOptions: {
                            label: 'Is Test Account',
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            options: [
                                {value: 0, label: 'Both'},
                                {value: 1, label: 'Yes'},
                                {value: 2, label: 'No'},
                            ]
                        }
                    },, {
		                className: 'col-xs-6',
		                key: 'depositCount',
		                type: 'conditional-input',
		                templateOptions: {
			                label: 'Deposit count',
			                description: '',
			                placeholder: '',
			                options: ['>','<', "!=", '=', '>=', '<='],
			                tooltip: 'Please use one of the following operators: <, >, =, <=, >=, != followed by the deposit count'
		                },
		                expressionProperties: {
			                'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.DEPOSIT_COUNT.LABEL" | translate',
			                'templateOptions.placeholder': '',
			                'templateOptions.description': ''
		                }
	                } , {
		                className: 'col-xs-6',
		                key: 'daysSinceFirstDeposit',
		                type: 'conditional-input',
		                templateOptions: {
			                label: 'Days since First deposit',
			                description: '',
			                placeholder: '',
			                options: ['>','<', "!=", '=', '>=', '<='],
			                tooltip: 'Please use one of the following operators: <, >, =, <=, >=, != followed by the days since first deposit'
		                },
		                expressionProperties: {
			                'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.DAYS_SINCE_FIRST_DEPOSIT.LABEL" | translate',
			                'templateOptions.placeholder': '',
			                'templateOptions.description': ''
		                }
	                }, {
		                className: 'col-xs-6',
		                key: 'transactionAmount',
		                type: 'conditional-input',
		                templateOptions: {
			                label: 'Transaction Amount',
			                description: '',
			                placeholder: '',
			                options: ['>','<', "!=", '=', '>=', '<='],
			                tooltip: 'Please use one of the following operators: <, >, =, <=, >=, != followed by the transaction amount'
		                },
		                expressionProperties: {
			                'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.TRANSACTION_AMOUNT.LABEL" | translate',
			                'templateOptions.placeholder': '',
			                'templateOptions.description': ''
		                }
	                }, {
		                className: 'col-xs-6',
		                key: 'activePaymentMethodCount',
		                type: 'conditional-input',
		                templateOptions: {
			                label: 'Active payment method count',
			                description: '',
			                placeholder: '',
			                options: ['>','<', "!=", '=', '>=', '<='],
			                tooltip: 'Please use one of the following operators: <, >, =, <=, >=, != followed by the active payment method count'
		                },
		                expressionProperties: {
			                'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.ACTIVE_PAYMENT_METHOD.LABEL" | translate',
			                'templateOptions.placeholder': '',
			                'templateOptions.description': ''
		                }
	                }, {
		                key: "userStatuses",
		                className: "col-xs-6",
		                type: "ui-select-multiple",
		                templateOptions : {
			                label: "",
			                description: "",
			                placeholder: "",
			                required : false,
			                optionsAttr: 'bs-options',
			                valueProp: 'id',
			                labelProp: 'name',
			                options: []
		                },
		                expressionProperties: {
			                'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.PLAYER_STATUS.LABEL" | translate',
		                },
		                controller: ['$scope', function ($scope) {
			                statusRest.findAll().then(function (response) {
 				                $scope.options.templateOptions.options = response;
				                return response;
			                });
		                }]
	                }, {
		                key: "userTags",
		                className: "col-xs-6",
		                type: "ui-select-multiple",
		                templateOptions : {
			                label: "",
			                description: "",
			                placeholder: "",
			                required : false,
			                optionsAttr: 'bs-options',
			                valueProp: 'id',
			                labelProp: 'name',
			                options: []
		                },
		                expressionProperties: {
			                'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.PLAYER_TAGS.LABEL" | translate',
		                },
		                controller: ['$scope', function ($scope) {
			                userRest.findAllTags($stateParams.domainName+',').then(function (response) {
				                $scope.options.templateOptions.options = response;
								return response;
			                });
		                }]
	                }

                ];

                if (!angular.isUndefined($stateParams.tranType)) {
                    if ($stateParams.tranType === 'DEPOSIT') $scope.model.selectedType = $scope.types[1];
                    if ($stateParams.tranType === 'CASHIER_DEPOSIT') $scope.model.selectedType = $scope.types[1];
                    if ($stateParams.tranType === 'WITHDRAWAL') $scope.model.selectedType = $scope.types[2];
                    if ($stateParams.tranType === 'CASHIER_PAYOUT') $scope.model.selectedType = $scope.types[2];
                    baseUrl += "&transactionType="+$scope.model.selectedType.type;
                    $scope.findDomainMethods($scope.model.selectedType.type);
                }

                $scope.viewTran = function(data) {
                    $scope.hideTranTable = true;
                    console.debug("controller.viewTran", data, $scope.hideTranTable);
                    $scope.selectedTran = data;
                    $state.go('dashboard.cashier.transactions.list.tran', {
                        tranId: data.id,
                        domainName: $stateParams.domainName
                    });
                    $scope.$apply();
                }

	            $scope.resolveChoice = function(choice) {
		            if (choice === undefined || choice === null) return null; // Both
		            switch (choice) {
			            case 0:
				            return null; // Both
			            case 1:
				            return true; // Auto approved only
			            case 2:
				            return false; // Not auto approved only
		            }
	            }

                $scope.rowClickHandler = function(data) {
                    console.debug(data);
                    $state.go('dashboard.cashier.transactions.list.tran', {
                        tranId: data.id,
                        domainName: $stateParams.domainName
                    });
                }
                $scope.transTable = $dt.builder()
                    .column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.ID")))
                    .column($dt.linkscolumn("", [{
                        permission: "cashier_transactions",
                        permissionType:"any",
                        permissionDomain: function(data) { return data.domainMethod.domain.name; },
                        title: "GLOBAL.ACTION.OPEN",
//                      click: function(data) {
//                          controller.viewTran(data);
//                      },
                        target: "_blank",
                        href: function(data) {
                            return $state.href("dashboard.cashier.transaction", {
                                tranId: data.id,
                                domainName: $stateParams.domainName
                            });
                        }
                    }]))
                    .column($dt.iconcolumn("", {
                        icon: "comments",
                        condition: function (data) {
                            return data.hasRemarks;
                        }
                    }))
                    .column($dt.columnformatdatetimems('createdOn').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.CREATED")))
                    .column($dt.columnformatdatetimems('current.timestamp').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.UPDATED")))
                    .column($dt.columnWithTransactionStatus('current.status.code').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.STATUS")))
                    .column($dt.columnlength('transactionType').withTitle("Type"))
                    .column($dt.columncurrencysymbolcents('amountCents', "currencyCode", 2).withTitle("Amount"))
                    .column($dt.columnlength('current.processor.description').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.PROCESSOR")))
                    .column($dt.column('domainMethod.name').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.METHOD")))
                    .column($dt.column('transactionPaymentType.paymentType').withTitle("Payment Type").withOption('defaultContent', ''))
                    .column($dt.column('user.guid').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.PLAYER")).withOption('defaultContent', ''))
                    .column($dt.column('paymentMethod.lastFourDigits').withTitle("Descriptor").withOption('defaultContent', 'N/A'))
                    .column($dt.column('declineReason').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.DECLINE_REASON"))
                        .renderWith(function (data, type, row, meta) {
                            if (data !== null && data.length > 0) {
                                data = data.replaceAll("\"", "&quot;")
                                return '<div title="' + data + '">' + data.substring(0, 120) + (data.length > 120 ? '...' : '') + '</div>';
                            }
                        }))
                    .column($dt.column('processorReference').withTitle("Processor Ref").withOption('defaultContent', ''))
                    .column($dt.column('additionalReference').withTitle("Additional Ref").withOption('defaultContent', ''))
                    .column($dt.labelcolumn('Tags', [
                        {
                            lclass: function (data) {
                                if (data.tags) return 'success';
                                return '';
                            },
                            text: function (data) {
                                if (data.tags) return data.tags.toString();
                                return '';
                            },
                            uppercase: true
                        }]))
	                .column($dt.column('user.testAccount').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.IS_TEST_ACCOUNT")))
                    .column($dt.column('autoApproved').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.AUTO_APPROVED")))
	                .column($dt.column('reviewedById').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.REVIEWED_BY")).renderWith(function (data, type, row, meta) {
                        return row.reviewedByFullName;
                    }))
                    .column($dt.column('directWithdrawal').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.DIRECT_WITHDRAWAL")))
                    .column($dt.column('initiationAuthorFullName').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.INITIATED_BY")).notSortable())
                    .column($dt.column('runtime').withTitle("Runtime").notSortable())
                    .column($dt.columnobscurecc('accountInfo').withTitle("Account Info"))
                    .column($dt.columnlength('bonusCode').withTitle("Bonus Code"))
                    .column($dt.column('bonusId').withTitle("Bonus ID"))
                    //	.column($dt.linkscolumn("", [{   // TODO: find player id, or way to use guid to redirect.
                    //		permission: "player_view",
                    //		permissionType: "any",
                    //		permissionDomain: function(data) { return data.domainName; },
                    //		title: '<span class=\"fa fa-eye\"></span>',
                    //		href: function(data) {
                    //			console.log(data);
                    //			return $state.href('dashboard.players.player', {
                    //				id:data.user.id,
                    //				domainName:data.domainMethod.domain.name
                    //			})
                    //		}
                    //	}]))
                    //	.column($dt.column('current.author.guid').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.AUTHOR")))
                    .options(baseUrl) //, controller.rowClickHandler)
                    .nosearch()
                    //	.order([[5, 'desc'],[2, 'desc']])
                    .order([0, 'desc'])
                    //	.nosearch()
                    .build();

                cashierRest.transactionPaymentTypes().then(function(paymentTypes) {
                    $scope.transactionPaymentTypes = paymentTypes.plain();
                }).catch(function(error) {
                    errors.catch("", false)(error)
                });

                cashierRest.transactionStatuses().then(function(statuses) {
                    $scope.transactionStatuses = statuses.plain();
                }).catch(function(error) {
                    errors.catch("", false)(error)
                });

                $scope.resetUserSearch = function() {
                    $scope.model.selectedUser = undefined;
                    guid = "";
                }
                $scope.searchUsers = function(userGuid) {
                    return userRest.search($stateParams.domainName, userGuid).then(function(searchResult) {
                        return searchResult.plain();
                    }).catch(function(error) {
                        errors.catch("", false)(error)
                    });
                };

                $scope.toggleLegendCollapse = function() {
                    $scope.legendCollapsed = !$scope.legendCollapsed;
                }

                $scope.resetFilter = function(collapse) {
                    if (collapse) {
                        $scope.toggleLegendCollapse();
                    }
                    guid = "";
                    dmpId = -1;
                    domainMethodId = -1;
                    $scope.model.selectedProcessor = undefined;
                    $scope.domainMethodProcessors = undefined;
                    $scope.model.selectedPaymentType = undefined;
                    $scope.model.selectedStatus = undefined;
                    $scope.model.selectedMethod = undefined;
                    if ($scope.data.selectedUser === undefined) $scope.model.selectedUser = undefined;
                    $scope.model.createdDateRangeStart = undefined;
                    $scope.model.createdDateRangeEnd = undefined;
                    $scope.model.updatedDateRangeStart = undefined;
                    $scope.model.updatedDateRangeEnd = undefined;
	                $scope.model.registrationDateRangeStart = undefined;
	                $scope.model.registrationDateRangeEnd = undefined;
                    $scope.model.selectedType = $scope.types[0];
	                $scope.model.testAccount = 0;
                    $scope.hideTranTable = false;
                    $scope.model.lastFourDigits = undefined;
                    $scope.model.transactionId = undefined;
                    $scope.model.transactionRuntimeQuery = {
                        operator: '=',
                        value: undefined
                    };
                    $scope.model.processorReference = undefined;
                    $scope.model.transactionTags = undefined;
	                $scope.model.userTags = undefined;
	                $scope.model.userStatuses = undefined;
					$scope.model.depositCount = {
						operator: '=',
						value: undefined
					};
	                $scope.model.daysSinceFirstDeposit = {
		                operator: '=',
		                value: undefined
	                };
	                $scope.model.transactionAmount = {
		                operator: '=',
		                value: undefined
	                };
	                $scope.model.activePaymentMethodCount = {
		                operator: '=',
		                value: undefined
	                };

	                $scope.applyFilter(true);
                }


                $scope.applyFilter = function(toggle) {

                    if ($scope.form.$invalid) {
                        angular.element("[name='" + $scope.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        return false;
                    }

                    if (toggle === true) {
                        $scope.toggleLegendCollapse();
                    }
                    $scope.populateTransactionTable();
                    baseUrl = "services/service-cashier/cashier/transaction/table?dmp="+dmpId+"&dm="+domainMethodId+"&guid="+guid+"&domain="+$stateParams.domainName;

                    if ((!angular.isUndefined($scope.model.selectedType)) && ($scope.model.selectedType.type !== '')) {
                        baseUrl += "&transactionType="+$scope.model.selectedType.type;
                    }
                    // if (!angular.isUndefined($scope.model.selectedStatus)) {
                    //     baseUrl += "&status="+$scope.model.selectedStatus.code;
                    // }
                    if (!angular.isUndefined($scope.model.selectedStatus)) {
                        if($scope.model.selectedStatus.length){
                            let tempUrl =  $scope.model.selectedStatus.map( item => item.code ).join(',');
                            baseUrl += "&statuses="+tempUrl;
                        }
                    }
                    if ($scope.model.selectedPaymentType && !angular.isUndefined($scope.model.selectedPaymentType)) {
                        baseUrl += "&paymentType=" + $scope.model.selectedPaymentType.paymentType;
                    }
                    if ($scope.model.createdDateRangeStart) {
                        const date = $scope.model.createdDateRangeStart
                        const creStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
                        baseUrl += "&cresd=" + creStart;
                    }
                    if ($scope.model.createdDateRangeEnd) {
                        const date = $scope.model.createdDateRangeEnd
                        const creEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
                        baseUrl += "&creed=" + creEnd;
                    }
                    if ($scope.model.updatedDateRangeStart) {
                        const date = $scope.model.updatedDateRangeStart
                        const updStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
                        baseUrl += "&updsd=" + updStart;

                    }
                    if ($scope.model.updatedDateRangeEnd) {
                        const date = $scope.model.updatedDateRangeEnd
                        const updEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
                        baseUrl += "&upded=" + updEnd;
                    }
	                if ($scope.model.registrationDateRangeStart) {
		                const date = $scope.model.registrationDateRangeStart
		                const registrationStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
		                baseUrl += "&registrationStart=" + registrationStart;
	                }
	                if ($scope.model.registrationDateRangeEnd) {
		                const date = $scope.model.registrationDateRangeEnd
		                const registrationEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
		                baseUrl += "&registrationEnd=" + registrationEnd;
	                }
                    if ($scope.model.processorReference !== undefined && $scope.model.processorReference !== null && $scope.model.processorReference !== '') {
                        baseUrl += "&processorReference="+$scope.model.processorReference;
                    }
                    if ($scope.model.lastFourDigits !== undefined && $scope.model.lastFourDigits !== null && $scope.model.lastFourDigits !== '') {
                        baseUrl += "&lastFourDigits="+$scope.model.lastFourDigits;
                    }

                    if ($scope.model.transactionId !== undefined && $scope.model.transactionId !== null && $scope.model.transactionId !== '') {
                        baseUrl += "&id="+$scope.model.transactionId;
                    }

                    if ($scope.model.transactionRuntimeQuery.value  !== undefined) {
                        baseUrl += "&transactionRuntimeQuery=" + $scope.model.transactionRuntimeQuery.operator + $scope.model.transactionRuntimeQuery.value;
                    }

	                var testAccount = $scope.resolveChoice($scope.model.testAccount);
	                if (testAccount !== null) {
		                baseUrl += "&testAccount=" + testAccount;
	                }

	                if ($scope.model.depositCount.value  !== undefined) {
		                baseUrl += "&depositCount=" + $scope.model.depositCount.operator + $scope.model.depositCount.value;
	                }

	                if ($scope.model.daysSinceFirstDeposit.value  !== undefined) {
		                baseUrl += "&daysSinceFirstDeposit=" + $scope.model.daysSinceFirstDeposit.operator + $scope.model.daysSinceFirstDeposit.value;
	                }

	                if ($scope.model.transactionAmount.value  !== undefined) {
		                baseUrl += "&transactionAmount=" + $scope.model.transactionAmount.operator + $scope.model.transactionAmount.value;
	                }

	                if ($scope.model.activePaymentMethodCount.value  !== undefined) {
		                baseUrl += "&activePaymentMethodCount=" + $scope.model.activePaymentMethodCount.operator + $scope.model.activePaymentMethodCount.value;
	                }

                    if (!angular.isUndefined($scope.model.transactionTags)) {
                        if ($scope.model.transactionTags.length) {
                            let includedTransactionTags = $scope.model.transactionTags.filter(item => item.include === true);
                            let excludedTransactionTags = $scope.model.transactionTags.filter(item => item.include === false);

                             let includedTempUrl = includedTransactionTags.map(item => item.val).join(',');
                             let excludedTempUrl = excludedTransactionTags.map(item => item.val).join(',');
                             baseUrl += "&includedTransactionTagsNames=" + includedTempUrl;
                             baseUrl += "&excludedTransactionTagsNames=" + excludedTempUrl;
                        }
                    }

	                if (!angular.isUndefined($scope.model.userTags)) {
		                if ($scope.model.userTags.length) {
			                let userTagsTempUrl = $scope.model.userTags.map(item => item.id).join(',');
			                baseUrl += "&userTagIds=" + userTagsTempUrl;
		                }
	                }

	                if (!angular.isUndefined($scope.model.userStatuses)) {
		                if ($scope.model.userStatuses.length) {

			                let statusesUrl = $scope.model.userStatuses.map(item => item.id).join(',');
			                baseUrl += "&userStatusIds=" + statusesUrl;
		                }
	                }

                    console.debug("baseUrl", baseUrl);

                    let changeDate = (date) => {
                        let changedDate = new Date(date)
                        return `${changedDate.getDate()}/${changedDate.getMonth() + 1}/${changedDate.getFullYear()}`
                    }

                    if ($scope.model.createdDateRangeStart) $scope.model.dateStartAfterFilter = luxon.DateTime.fromISO($scope.model.createdDateRangeStart.toISOString()).ts
                    else $scope.model.dateStartAfterFilter = ''
                    if ($scope.model.createdDateRangeEnd) $scope.model.dateEndAfterFilter = luxon.DateTime.fromISO($scope.model.createdDateRangeEnd.toISOString()).ts
                    else $scope.model.dateEndAfterFilter = ''

                    $scope.transTable.instance._renderer.options.ajax = baseUrl;
                    $scope.transTable.instance.rerender();
                    $scope.hideTranTable = false;
                }

                $scope.populateTransactionTable = function() {
                    if (!angular.isUndefined($scope.model.selectedProcessor)) dmpId = $scope.model.selectedProcessor.id;
                    if (!angular.isUndefined($scope.model.selectedMethod)) domainMethodId = $scope.model.selectedMethod.id;
                    if (!angular.isUndefined($scope.model.selectedUser)) guid = $scope.model.selectedUser.guid;
                }
                $scope.populateTransactionTable();

                $scope.add = function() {
                    $state.go('dashboard.cashier.transactions.add', {
                        domainName: $stateParams.domainName
                    });
                };

                $scope.refreshList = function() {
                    $scope.transTable.instance.reloadData(function(){}, false);
                }

                if ($rootScope.settings !== undefined && $rootScope.settings !== null &&
                    $rootScope.settings.transactionListUpdateSeconds !== undefined && $rootScope.settings.transactionListUpdateSettings !== null) {
                    var intervalPromise = null;
                    intervalPromise = $interval(function() {
                        $scope.refreshList()
                    }, $rootScope.settings.transactionListUpdateSeconds * 1000);
                    $scope.$on('$destroy',function(){
                        if(intervalPromise)
                            $interval.cancel(intervalPromise);
                    });
                }

                $scope.reference = () => {
                    if ($scope.model?.selectedUser?.guid){
                        const reference = localStorage.getItem(`export_reference_transactions_${$scope.model.selectedUser.guid}`)

                        if(angular.isUndefinedOrNull(reference)) {
                            return null;
                        }

                        return reference.replace('export_reference_transactions_', '')
                    }

                    const reference = localStorage.getItem(`export_reference_transactions_list`)

                    if(angular.isUndefinedOrNull(reference)) {
                        return null;
                    }

                    return reference.replace('export_reference_transactions_list', '')

                }
                $rootScope.provide['csvGeneratorProvider'] = {}

                $rootScope.provide.csvGeneratorProvider.generate = async (config) => {
                     const response = await documentRest.generateDocument({
                         ...config,
                     });
                    if ($scope.model?.selectedUser?.guid){
                        localStorage.setItem(`export_reference_transactions_${$scope.model.selectedUser.guid}`, response.reference);
                    } else{
                        localStorage.setItem(`export_reference_transactions_list`, response.reference);
                    }


                    return response
                }

                $rootScope.provide.csvGeneratorProvider.progress = async (config) => {
                    return documentRest.documentStatus($scope.reference());
                }

                $rootScope.provide.csvGeneratorProvider.cancelGeneration = async (reference) => {
                    return documentRest.documentCancel(reference);
                }

                $rootScope.provide.csvGeneratorProvider.download = (reference) => {
                    const a = document.createElement("a")
                    const url = `services/service-document-generation/document/${reference}/download`;
                    a.href = url;
                    a.setAttribute('download', reference)
                    document.body.appendChild(a);
                    a.click();

                    setTimeout(() => document.body.removeChild(a), 1500)
                }
                $scope.apiParams = () => {
                    const params = {};

                    params.dmp = dmpId;
                    params.dm = domainMethodId;
                    params.domain = $stateParams.domainName;
                    if (guid) params.guid = guid;
                    if ((!angular.isUndefined($scope.model.selectedType)) && ($scope.model.selectedType.type !== '')) params.transactionType = $scope.model.selectedType.type;
                    if ($scope.model.selectedStatus !== undefined && Object.keys($scope.model.selectedStatus).length) params.statuses = $scope.model.selectedStatus.map( item => item.code ).join(',');
                    if ($scope.model.selectedPaymentType) params.paymentType = $scope.model.selectedPaymentType.paymentType;
                    if ($scope.transTable.instance.DataTable) params.search = $scope.transTable.instance.DataTable.search();
                    if ($scope.model.createdDateRangeStart) {
                        const date = $scope.model.createdDateRangeStart
                        params.cresd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());

                    }
                    if ($scope.model.createdDateRangeEnd) {
                        const date = $scope.model.createdDateRangeEnd
                        params.creed = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);
                    }
                    if ($scope.model.updatedDateRangeStart) {
                        const date = $scope.model.updatedDateRangeStart
                        params.updsd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
                    }
                    if ($scope.model.updatedDateRangeEnd) {
                        const date = $scope.model.updatedDateRangeEnd
                        params.upded = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);

                    }
	                if ($scope.model.registrationDateRangeStart) {
		                const date = $scope.model.registrationDateRangeStart
		                params.registrationStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
	                }
	                if ($scope.model.registrationDateRangeEnd) {
		                const date = $scope.model.registrationDateRangeEnd
		                params.registrationEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23,59,59,999);

	                }
                    if ($scope.model.processorReference) params.processorReference = $scope.model.processorReference;
                    if ($scope.model.lastFourDigits) params.lastFourDigits = $scope.model.lastFourDigits;
                    if ($scope.model.transactionId) params.id = $scope.model.transactionId;
                    if ($scope.model.transactionRuntimeQuery.value && $scope.model.transactionRuntimeQuery.operator) params.transactionRuntimeQuery = $scope.model.transactionRuntimeQuery.operator + $scope.model.transactionRuntimeQuery.value;

                    var autoApproved = $scope.resolveChoice($scope.model.autoApproved);
                    if (autoApproved !== null) {
                        params.autoApproved = autoApproved;
                    }

                    var testAccount = $scope.resolveChoice($scope.model.testAccount);
                    if (testAccount !== null) {
                        params.testAccount = testAccount;
                    }

                    if ($scope.model.transactionTags !== undefined && Object.keys($scope.model.transactionTags).length) {
                        let includedTransactionTags = $scope.model.transactionTags.filter(item => item.include === true);
                        let excludedTransactionTags = $scope.model.transactionTags.filter(item => item.include === false);

                        params.includedTransactionTagsNames = includedTransactionTags.map(item => item.val).join(',');
                        params.excludedTransactionTagsNames = excludedTransactionTags.map(item => item.val).join(',');
                    } else{
                        params.includedTransactionTagsNames = '';
                        params.excludedTransactionTagsNames = '';
					}

	                if ($scope.model.depositCount.value && $scope.model.depositCount.operator) params.depositCount = $scope.model.depositCount.operator + $scope.model.depositCount.value;

	                if ($scope.model.daysSinceFirstDeposit.value && $scope.model.daysSinceFirstDeposit.operator) params.daysSinceFirstDeposit = $scope.model.daysSinceFirstDeposit.operator + $scope.model.daysSinceFirstDeposit.value;

	                if ($scope.model.transactionAmount.value && $scope.model.transactionAmount.operator) params.transactionAmount = $scope.model.transactionAmount.operator + $scope.model.transactionAmount.value;

	                if ($scope.model.activePaymentMethodCount.value && $scope.model.activePaymentMethodCount.operator) params.activePaymentMethodCount = $scope.model.activePaymentMethodCount.operator + $scope.model.activePaymentMethodCount.value;

	                if ($scope.model.userStatuses !== undefined && Object.keys($scope.model.userStatuses).length) {
		                params.userStatuses = $scope.model.userStatuses.map(item => item.id).join(',');
	                } else {
		                params.userStatuses = '';
	                }

	                if ($scope.model.userTags !== undefined && Object.keys($scope.model.userTags).length) {
		                params.userTags = $scope.model.userTags.map(item => item.id).join(',');
	                } else {
		                params.userTags = '';
	                }

                    return params
                }
                $rootScope.provide.csvGeneratorProvider.getConfig =  () => {
                    const config =  {
                        domain: $stateParams.domainName,
                        provider: 'service-csv-provider-cashier-transactions',
                        page: 0,
                        size: 10,
                        parameters: $scope.apiParams(),
                        reference:  $scope.reference()
                    };
                    if ($scope.model?.selectedUser?.guid) config.userGuid = $scope.model?.selectedUser?.guid;
                    return config
                }

                window.VuePluginRegistry.loadByPage('dashboard/csv-export')

                // BULK ACTION
                $rootScope.provide['bulkTransactionProvider'] = {}
                if ($scope.model.selectedUser !== undefined && $scope.model.selectedUser !== null &&
                    $scope.model.selectedUser.guid !== undefined && $scope.model.selectedUser.guid !== null) {
                    $rootScope.provide.bulkTransactionProvider.selectedUser =  $scope.model.selectedUser
                } else {
                    $rootScope.provide.bulkTransactionProvider.selectedUser =  null
                }
                $rootScope.provide.bulkTransactionProvider.getParams =  () => { return  $scope.apiParams() }
                window.VuePluginRegistry.loadByPage("WithdrawalBulk")

            }
        ]
    }
});
