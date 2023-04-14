'use strict';

angular.module('lithium')
    .controller('PlayersList', ["$translate", "$userService", "$dt", "DTOptionsBuilder", "$state", "$rootScope", "bsLoadingOverlayService", "notify", "errors", "UserRest", "$filter", "rest-cashier", "userFields", 'StatusRest', 'VerificationStatusRest', 'rest-domain',

        function ($translate, $userService, $dt, DTOptionsBuilder, $state, $rootScope, bsLoadingOverlayService, notify, errors, userRest, $filter, cashierRest, userFields, StatusRest, VerificationStatusRest, restDomain) {
            var controller = this;
            controller.referenceId = "PlayersList_" + (Math.random() * 1000);

            controller.legendCollapsed = true;
            controller.model = {};
            controller.model.isTestAccount = 0;
            controller.model.isAgeVerified = 0;
            controller.model.isAddressVerified = 0;
            controller.model.isEmailValidated = 0;
            controller.selectedDomainsDisplay = undefined;
            controller.model.isActiveRestriction = 0;
	        controller.model.documentStatus = null;
            controller.model.currentAccountBalance = {
                operator: '=',
                value: null
            };

            controller.verificationStatuses = [];
            controller.selectedAffiliate = []
            controller.findAllVerificationStatuses = function() {
                controller.verificationStatuses = [];
                bsLoadingOverlayService.start({referenceId: controller.referenceId});
                VerificationStatusRest.findAll().then(function (response) {
                    controller.verificationStatuses = response;
                }).catch(function (error) {
                    notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
                    errors.catch("", false)(error)
                }).finally(function () {
                    bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                });
            }
            controller.findAllVerificationStatuses();

            controller.filterFields = [
                {
                    className: 'col-md-3 col-xs-12',
                    key: 'username',
                    type: 'input',
                    templateOptions: {
                        label: 'Username',
                        description: '',
                        placeholder: ''
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.USERNAME.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'firstName',
                    type: 'input',
                    templateOptions: {
                        label: 'First Name Starts with',
                        description: '',
                        placeholder: ''
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.FIRST_NAME.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'lastName',
                    type: 'input',
                    templateOptions: {
                        label: 'Last Name Starts with',
                        description: '',
                        placeholder: ''
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.LAST_NAME.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'id',
                    type: 'input',
                    templateOptions: {
                        label: 'ID',
                        description: '',
                        placeholder: ''
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.ID.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'email',
                    type: 'input',
                    templateOptions: {
                        label: 'Email',
                        description: '',
                        placeholder: ''
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.EMAIL.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'mobilenumber',
                    type: 'input',
                    templateOptions: {
                        label: 'Mobile Number',
                        description: '',
                        placeholder: ''
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.MOBILENUMBER.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'dateofbirthstartdate',
                    type: 'datepicker',
                    templateOptions: {
                        label: 'Date Of Birth: Start Date',
                        description: '',
                        placeholder: '',
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[7].templateOptions.datepickerOptions.minDate = controller.model.dateofbirthstartdate;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.DATEOFBIRTHSTARTDATE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'dateofbirthenddate',
                    type: 'datepicker',
                    templateOptions: {
                        label: 'Date Of Birth: End Date',
                        description: '',
                        placeholder: '',
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[6].templateOptions.datepickerOptions.maxDate = controller.model.dateofbirthenddate;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.DATEOFBIRTHENDDATE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    key: "status",
                    className: "col-md-3 col-xs-12",
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
                        'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.NAME" | translate',
                        // 'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.DESCRIPTION" | translate'
                    },
                    controller: ['$scope', function ($scope) {
                        StatusRest.findAll().then(function (response) {
                            // console.log('StatusRest', response);
                            $scope.options.templateOptions.options = response;
                            return response;
                        });
                    }]
                }, {
                    key: "statusReason",
                    className: "col-md-3 col-xs-12",
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
                        'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.REASON.NAME" | translate',
                    },
                    controller: ['$scope', function ($scope) {
                        StatusRest.findAllStatusReasons().then(function (response) {
                            $scope.to.options = response.plain();
                        });
                    }]
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'verificationstatus',
                    type: 'ui-select-multiple',
                    templateOptions: {
                        label: 'Verification Status',
                        description: "",
                        required: false,
                        valueProp: 'id',
                        labelProp: 'code',
                        optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                        placeholder: '',
                        options: []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.VERIFICATIONSTATUS.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    },
                    controller: ['$scope', function ($scope) {
                        VerificationStatusRest.findAll().then(function (response) {
                            $scope.options.templateOptions.options = response.plain();
                            controller.verificationStatuses = response.plain();
                            return response;
                        });
                    }]
                },
                // {
                //     className: 'col-md-3 col-xs-12',
                //     key: 'accountmanagementstatus',
                //     type: 'input',
                //     templateOptions: {
                //         label: 'Account Management Status',
                //         description: '',
                //         placeholder: ''
                //     },
                //     expressionProperties: {
                //         'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.ACCOUNTMANAGEMENTSTATUS.LABEL" | translate',
                //         'templateOptions.placeholder': '',
                //         'templateOptions.description': ''
                //     }
                // },
                {
                    className: 'col-md-3 col-xs-12',
                    key: 'clienttype',
                    type: 'ui-select-multiple',
                    templateOptions : {
                        label: "Client Type",
                        description: "",
                        placeholder: "",
                        required : false,
                        optionsAttr: 'bs-options',
                        valueProp: 'guid',
                        labelProp: 'guid',
                        options: []
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.CLIENTTYPE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }, controller: ['$scope', function ($scope) {
                        restDomain.findAllProviderAuthClients(domainArray()).then(function (response) {
                            // console.log('findAllProviderAuthClients', response);
                            $scope.options.templateOptions.options = response;
                            return response;
                        });
                    }],
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'isTestAccount',
                    type: 'ui-select-single',
                    templateOptions : {
                        label: 'Is Test Account',
                        valueProp: 'value',
                        labelProp: 'label',
                        optionsAttr: 'ui-options', ngOptions: 'ui-options',
                        options: [
                            {value: 0, label: 'Both'},
                            {value: 1, label: 'Yes'},
                            {value: 2, label: 'No'},
                        ]
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.IS_TEST.LABEL" | translate'
                    }
                }, {
                    key: 'cashierTranStatus',
                    className: "col-md-3 col-xs-12",
                    type: "ui-select-single",
                    templateOptions : {
                        label: "Cashier Transaction Status",
                        description: "",
                        placeholder: "",
                        required : false,
                        optionsAttr: 'bs-options',
                        valueProp: 'code',
                        labelProp: 'code',
                        options: []
                    },
                    expressionProperties: {
                        // 'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.HASPENDINGWITHDRAWALS.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    },
                    controller: ['$scope', function ($scope) {
                        cashierRest.transactionStatuses().then(function(response) {
                            console.log('transactionStatuses', response.plain());
                            $scope.options.templateOptions.options = response.plain();
                            // return response;
                        });
                    }]
                },{
                    className: 'col-md-3 col-xs-12',
                    key: 'cashierStartDate',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'cashierStartDate Date: Range Start',
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[15].templateOptions.datepickerOptions.minDate = controller.model.cashierStartDate;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.LASTDEPOSITSTARTDATE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'cashierEndDate',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'cashierEndDate Date: Range End',
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[14].templateOptions.datepickerOptions.maxDate = controller.model.cashierEndDate;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.LASTDEPOSITENDDATE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                },
                // {
                //     className: 'col-md-3 col-xs-12',
                //     key: 'assignedaccountmanager',
                //     type: 'ui-select-single',
                //     templateOptions: {
                //         label: 'Assigned Account Manager',
                //         description: "",
                //         required: false,
                //         optionsAttr: 'bs-options',
                //         valueProp: 'name',
                //         labelProp: 'name',
                //         optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                //         placeholder: '',
                //         options: []
                //     },
                //     expressionProperties: {
                //         'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.ASSIGNEDACCOUNTMANAGER.LABEL" | translate',
                //         'templateOptions.placeholder': '',
                //         'templateOptions.description': ''
                //     },
                // },
                // {
                //     className: 'col-md-3 col-xs-12',
                //     key: 'tags',
                //     type: 'ui-select-multiple',
                //     templateOptions: {
                //         label: 'Tags',
                //         description: "",
                //         required: false,
                //         optionsAttr: 'bs-options',
                //         valueProp: 'id',
                //         labelProp: 'code',
                //         optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                //         placeholder: '',
                //         options: []
                //     },
                //     expressionProperties: {
                //         'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.TAGS.LABEL" | translate',
                //         'templateOptions.placeholder': '',
                //         'templateOptions.description': ''
                //     }, controller: ['$scope', function ($scope) {
                //         // console.log('Types', controller.types);
                //         $scope.options.templateOptions.options = controller.types;
                //         return controller.types;
                //     }],
                // },
                {
                    className: 'col-md-3 col-xs-12',
                    key: 'lastloginstartdate',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Last Login: Start Date',
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[17].templateOptions.datepickerOptions.minDate = controller.model.lastloginstartdate;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.LASTLOGINSTARTDATE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'lastloginenddate',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Last Login: End Date',
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[16].templateOptions.datepickerOptions.maxDate = controller.model.lastloginenddate;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.LASTLOGINENDDATE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'signupDateRangeStart',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Signup Date: Range Start',
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[19].templateOptions.datepickerOptions.minDate = controller.model.signupDateRangeStart;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.SIGNUP_DATE_START.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'signupDateRangeEnd',
                    type: 'datepicker',
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: 'Signup Date: Range End',
                        required: false,
                        datepickerOptions: {
                            format: 'dd/MM/yyyy'
                        },
                        onChange: function () {
                            controller.filterFields[18].templateOptions.datepickerOptions.maxDate = controller.model.signupDateRangeEnd;
                        }
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.SIGNUP_DATE_END.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'residentialAddress',
                    type: 'input',
                    templateOptions: {
                        label: 'Postal Code',
                        description: '',
                        placeholder: ''
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.POSTAL_CODE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
                    className: 'col-md-3 col-xs-12',
                    key: 'currentAccountBalance',
                    type: 'conditional-input',
                    templateOptions: {
                        label: 'Current Account Balance',
                        description: '',
                        placeholder: '',
                        options: ['>','<', "!=", '=', '>=', '<='],
                        tooltip: "Please use one of the following operators: <, >, =, <=, >=, != followed by the amount. Players with no transactions will not appear in the response"
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.CURRENT_ACCOUNT_BALANCE.LABEL" | translate',
                        'templateOptions.placeholder': '',
                        'templateOptions.description': ''
                    }
                }, {
		            className: 'col-md-3 col-xs-12',
		            key: 'isAgeVerified',
		            type: 'ui-select-single',
		            templateOptions : {
			            label: 'Is Age verified',
			            valueProp: 'value',
			            labelProp: 'label',
			            optionsAttr: 'ui-options', ngOptions: 'ui-options',
			            options: [
				            {value: 0, label: 'Both'},
				            {value: 1, label: 'Yes'},
				            {value: 2, label: 'No'},
			            ]
		            },
		            expressionProperties: {
			            'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.AGE_VERIFIED.LABEL" | translate'
		            }
	            }, {
		            className: 'col-md-3 col-xs-12',
		            key: 'isAddressVerified',
		            type: 'ui-select-single',
		            templateOptions : {
			            label: 'Is Address verified',
			            valueProp: 'value',
			            labelProp: 'label',
			            optionsAttr: 'ui-options', ngOptions: 'ui-options',
			            options: [
				            {value: 0, label: 'Both'},
				            {value: 1, label: 'Yes'},
				            {value: 2, label: 'No'},
			            ]
		            },
		            expressionProperties: {
			            'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.ADDRESS_VERIFIED.LABEL" | translate'
		            }
	            }, {
                className: 'col-md-3 col-xs-12',
                key: 'isActiveRestriction',
                type: 'ui-select-single',
                templateOptions : {
                  label: 'Is Active Restriction',
                  valueProp: 'value',
                  labelProp: 'label',
                  optionsAttr: 'ui-options', ngOptions: 'ui-options',
                  options: [
                    {value: 0, label: 'Both'},
                    {value: 1, label: 'Yes'},
                    {value: 2, label: 'No'},
                  ]
                },
                expressionProperties: {
                  'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.IS_ACTIVE_RESTRICTION.LABEL" | translate'
                }
              }, {
                className: 'col-md-3 col-xs-12',
                key: 'restrictionActiveFromDate',
                type: 'datepicker',
                optionsTypes: ['editable'],
                templateOptions: {
                  label: 'Restriction Active from Date',
                  required: false,
                  datepickerOptions: {
                    format: 'dd/MM/yyyy'
                  },
	                onChange: function () {
		                controller.filterFields[14].templateOptions.datepickerOptions.maxDate = controller.model.restrictionActiveFromDate;
	                }
                },
		            expressionProperties: {
			            'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.RESTRICTION_ACTIVE_FROM.LABEL" | translate',
			            'templateOptions.placeholder': '',
			            'templateOptions.description': ''
		            }
	            }, {
		            className: 'col-md-3 col-xs-12',
		            key: 'documentStatus',
		            type: 'ui-select-single',
		            templateOptions: {
			            label: 'Document Status',
			            valueProp: 'value',
			            labelProp: 'label',
			            optionsAttr: 'ui-options', ngOptions: 'ui-options',
			            options: [
				            {value: 0, label: 'Waiting'},
				            {value: 1, label: 'Valid'},
				            {value: 2, label: 'Invalid'},
			            ]
		            },
		            expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.USER.DOCUMENT.STATUS.LABEL" | translate'
		            }
	            }, {
		            className: 'col-md-3 col-xs-12',
		            key: 'isEmailValidated',
		            type: 'ui-select-single',
		            templateOptions : {
			            label: 'Is Email Validated',
			            valueProp: 'value',
			            labelProp: 'label',
			            optionsAttr: 'ui-options', ngOptions: 'ui-options',
			            options: [
				            {value: 0, label: 'Both'},
				            {value: 1, label: 'Yes'},
				            {value: 2, label: 'No'},
			            ]
		            },
		            expressionProperties: {
			            'templateOptions.label': '"UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.EMAIL_VALIDATED.LABEL" | translate'
		            }
	            }
            ];

            controller.types = [];

            controller.findAllTags = function () {
                controller.types = [];
                bsLoadingOverlayService.start({referenceId: controller.referenceId});
                userRest.findAllTags(domainArray()).then(function (tags) {
                    angular.forEach(tags.plain(), function (c) {
                        controller.types.push({id: c.id, name: c.name, domain: c.domain.name, selected: false});
                    });
                    controller.typeSelectNone();
                }).catch(function (error) {
                    notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
                    errors.catch("", false)(error)
                }).finally(function () {
                    bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                });
            }

            controller.typeSelect = function () {
                controller.selectedTypes = [];
                for (var d = 0; d < controller.types.length; d++) {
                    if (controller.types[d].selected)
                        controller.selectedTypes.push(controller.types[d]);
                } //for

                if (controller.selectedTypes.length == controller.types.length) {
                    controller.selectedTypesDisplay = "All Tags Selected";
                } else {
                    controller.selectedTypesDisplay = "" + controller.selectedTypes.length + " Tags Selected";
                } //if -else
            };

            controller.typeSelectAll = function () {
                for (var d = 0; d < controller.types.length; d++) controller.types[d].selected = true;
                controller.typeSelect();
            };

            controller.typeSelectNone = function () {
                for (var d = 0; d < controller.types.length; d++) controller.types[d].selected = false;
                controller.typeSelect();
            };

	        controller.restrictions = [];

	        controller.findAllRestrictions = function () {
		        controller.restrictions = [];
		        bsLoadingOverlayService.start({referenceId: controller.referenceId});
		        userRest.findRestrictions(domainArray()).then(function (restricts) {
			        angular.forEach(restricts.plain(), function (c) {
				        controller.restrictions.push({id: c.id, name: c.name, domain: c.domain.name, selected: false});
			        });
			        controller.restrictionSelectNone();
		        }).catch(function (error) {
			        notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
			        errors.catch("", false)(error)
		        }).finally(function () {
			        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
		        });
	        }

	        controller.restrictionSelect = function () {
		        controller.selectedRestrictions = [];
		        for (var d = 0; d < controller.restrictions.length; d++) {
			        if (controller.restrictions[d].selected)
				        controller.selectedRestrictions.push(controller.restrictions[d]);
		        } //for

		        if (controller.selectedRestrictions.length === controller.restrictions.length) {
			        controller.selectedRestrictionsDisplay = "All Restrictions Selected";
		        } else {
			        controller.selectedRestrictionsDisplay = controller.selectedRestrictions.length + " Restriction(s) Selected"
		        } //if - else
	        };

	        controller.restrictionSelectAll = function () {
		        for (var d = 0; d < controller.restrictions.length; d++) controller.restrictions[d].selected = true;
		        controller.restrictionSelect();
	        };

	        controller.restrictionSelectNone = function () {
		        for (var d = 0; d < controller.restrictions.length; d++) controller.restrictions[d].selected = false;
		        controller.restrictionSelect();
	        };

	        controller.restrictionSelectAll();

            function domainArray() {
                var str = "";
                angular.forEach(controller.selectedDomains, function (d) {
                    str += d.name + ",";
                });
                return str;
            }

            function arrayAsString(arr, fieldName) {
                var str = "";
                angular.forEach(arr, function (d) {
                    str += d[fieldName] + ",";
                });
                return str;
            }

            function tagArray() {
                var tags = "";
                angular.forEach(controller.selectedTypes, function (d) {
                    tags += d.id + ",";
                });
                return tags;
            }

	        function restrictionArray() {
		        var restrict = "";
		        angular.forEach(controller.selectedRestrictions, function (d) {
			        restrict += d.id + ",";
		        });
		        return restrict;

	        }
            function affiliatesArray() {
                var affiliates = "";
                angular.forEach(controller.selectedAffiliate, function (d) {
                    affiliates += d.id + ",";
                });
                return affiliates;

            }

            controller.domains = $userService.playerDomainsWithAnyRole(["PLAYER_VIEW"]);
            controller.domainSelect = function () {
                controller.selectedDomains = [];
                for (var d = 0; d < controller.domains.length; d++) {
                    if (controller.domains[d].selected)
                        controller.selectedDomains.push(controller.domains[d]);
                } //for

                if (controller.selectedDomains.length === controller.domains.length) {
                    controller.selectedDomainsDisplay = $translate.instant('UI_NETWORK_ADMIN.PLAYERS.LINKS.OPTIONS.DOMAINS_SELECTED');
                } else {
                    controller.selectedDomainsDisplay = controller.selectedDomains.length + " Domain(s) Selected"
                } //if - else
            };

            controller.domainSelectAll = function () {
                for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
                controller.domainSelect();
            };

            controller.domainSelectNone = function () {
                for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = false;
                controller.domainSelect();
            };

            controller.domainSelectAll();

            controller.formatDate = function (date) {
                if (!date) {
                    return null;
                }
                return $filter('date')(date, 'yyyy-MM-dd');
            }

            controller.resolveChoice = function(choice) {
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

	        controller.resolveDocStatusChoice = function (choice) {
		        if (choice === undefined || choice === null) return null; // None
		        switch (choice) {
			        case 0:
				        return 'Waiting';
			        case 1:
				        return 'Valid';
			        case 2:
				        return 'Invalid';
		        }
	        }

	        controller.cashierLoadTranStatus = function () {
		        cashierRest.transactionStatuses().then(function (statuses) {
			        controller.cashierTransactionStatuses = statuses.plain();
		        }).catch(function (error) {
			        errors.catch("", false)(error)
		        });
	        }
	        controller.cashierLoadTranStatus();

            var baseSearchUrl = "services/service-user-search/backoffice/players/table?1=1";
            var dtSearchOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
            controller.playerSearchTable = $dt.builder()
                .column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.ID.NAME")))
                .column($dt.column('username').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME")))
                .column($dt.linkscolumn("", [{
                    permission: "player_view",
                    permissionType: "any",
                    permissionDomain: function (data) {
                        return data.domain.name;
                    },
                    title: "GLOBAL.ACTION.OPEN",
                    target: "_blank",
                    href: function (data) {
                        return $state.href("^.player.summary", {id: data.id, domainName: data.domain.name})
                    }
                }]))
                .column(
                    $dt.labelcolumn(
                        '',
                        [{lclass: function(data) {
                                var testAccount = (data.testAccount != null) ? data.testAccount: false;
                                if (testAccount) return "danger";
                                return "";
                            },
                            text: function(data) {
                                var testAccount = (data.testAccount != null) ? data.testAccount: false;
                                if (testAccount) return "TEST";
                                return "";
                            },
                            uppercase:true
                        }]
                    )
                )
                .column($dt.columnWithClass('status.name', 'label label-lg label-default').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.NAME")))
                .column($dt.columnWithClass('statusReason.description', 'label label-lg label-default').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.REASON.NAME")))
                .column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.DOMAIN.NAME")))
                .column($dt.column('firstName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.NAME")))
                .column($dt.column(function (data) {
                    return data.lastNamePrefix ? data.lastNamePrefix + ' ' + data.lastName : data.lastName;
                }).withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.NAME")))
                .column($dt.column('email').withTitle($translate("UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.EMAIL.LABEL")))
                .column($dt.column('cellphoneNumber').withTitle($translate("UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.MOBILENUMBER.LABEL")))
                .column($dt.column('residentialAddress.postalCode').withTitle($translate("UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.POSTAL_CODE.LABEL")))
                .column($dt.columnformatdate('lastLoggedInDate').withTitle($translate("UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.LASTLOGIN.LABEL")))
                .column($dt.column('providerAuthClient').withTitle($translate("UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.CLIENTTYPE.LABEL")))
                // .column($dt.column('verificationStatus').withTitle($translate("UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.VERIFICATIONSTATUS.LABEL")))
                .column($dt.labelcolumn(
                    $translate("UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.VERIFICATIONSTATUS.LABEL"),
                    [{
                        lclass: function(data) {
                            return "default"
                        },
                        text: function(data) {
                            var vs = controller.verificationStatuses.filter(vs => vs.id == data.verificationStatus);

                            // Checking for undefined AND if the list is empty
                            // Fixes https://jira.livescore.com/browse/PLAT-4066
                            if (angular.isUndefined(vs) || vs.length <= 0) {
                                return "";
                            }
                            return vs[0].code;
                        },
                        uppercase:true
                    }]
                ).notSortable())
                .column($dt.column('userApiToken.shortGuid').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.REFERRALCODE")).notSortable())
                .column($dt.columnformatdate('createdDate').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.CREATEDDATE.NAME")))
                .column($dt.columnsize('userCategories').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.TAGS.NAME")))
                .column($dt.column('gender').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.GENDER.NAME")))
                .options(
                    {
                        url: baseSearchUrl,
                        type: 'POST',
                        data: function (d) {
                            d.requestData = {};
                            // console.log(d);
                            // d.domain = controller.model.domain;
                            d.requestData.dateofbirthstartdate = controller.formatDate(controller.model.dateofbirthstartdate);
                            d.requestData.dateofbirthenddate = controller.formatDate(controller.model.dateofbirthenddate);
                            d.requestData.email = controller.model.email;
                            d.requestData.mobilenumber = controller.model.mobilenumber;
                            d.requestData.postalcode = controller.model.residentialAddress !== undefined ? controller.model.residentialAddress : undefined;
                            d.requestData.currentAccountBalanceQuery = controller.model.currentAccountBalance.value !== null ? controller.model.currentAccountBalance.operator + controller.model.currentAccountBalance.value : '';
                            d.requestData.verificationstatus = arrayAsString(controller.model.verificationstatus, 'id');
                            d.requestData.includeexcludetestaccount = controller.model.includeexcludetestaccount;
                            // d.requestData.haspendingwithdrawals = controller.model.haspendingwithdrawals; // need to query rest-accounting.summaryAccountByOwnerGuid
                            d.requestData.accountmanagementstatus = controller.model.accountmanagementstatus;
                            d.requestData.lastloginstartdate = controller.formatDate(controller.model.lastloginstartdate);
                            d.requestData.lastloginenddate = controller.formatDate(controller.model.lastloginenddate);
                            d.requestData.clienttype = arrayAsString(controller.model.clienttype, 'guid');
                            d.requestData.assignedaccountmanager = controller.model.assignedaccountmanager;
                            d.requestData.lastdepositstartdate = controller.formatDate(controller.model.lastdepositstartdate); //
                            d.requestData.lastdepositenddate = controller.formatDate(controller.model.lastdepositenddate); //

                            d.requestData.status = arrayAsString(controller.model.status, 'name');
                            d.requestData.statusReason = arrayAsString(controller.model.statusReason, 'name');

                            d.requestData.domainNames = domainArray();
                            d.requestData.tags = tagArray();
                            d.requestData.restrictions = restrictionArray();
                            d.requestData.affiliates = affiliatesArray();

                            d.requestData.username = controller.model.username;
                            d.requestData.firstName = controller.model.firstName;
                            d.requestData.lastName = controller.model.lastName;
                            d.requestData.id = controller.model.id;
                            d.requestData.signupDateRangeStart = controller.formatDate(controller.model.signupDateRangeStart);
                            d.requestData.signupDateRangeEnd = controller.formatDate(controller.model.signupDateRangeEnd);
                            d.requestData.test = controller.resolveChoice(controller.model.isTestAccount);
                            d.requestData.ageVerified = controller.resolveChoice(controller.model.isAgeVerified);
                            d.requestData.addressVerified = controller.resolveChoice(controller.model.isAddressVerified);
                            d.requestData.emailValidated = controller.resolveChoice(controller.model.isEmailValidated);
                            d.requestData.cashierTranStatus = controller.model.cashierTranStatus;
                            d.requestData.cashierStartDate = controller.formatDate(controller.model.cashierStartDate);
                            d.requestData.cashierEndDate = controller.formatDate(controller.model.cashierEndDate);
                            d.requestData.isActiveRestriction = controller.resolveChoice(controller.model.isActiveRestriction);
	                        d.requestData.restrictionActiveFromDate = controller.formatDate(controller.model.restrictionActiveFromDate);
	                        d.requestData.documentStatus = controller.resolveDocStatusChoice(controller.model.documentStatus);
                        }
                    },
                    null,
                    dtSearchOptions,
                    null
                )
                .build();

            controller.tableLoad = function () {
                controller.playerSearchTable.instance.rerender(true);
            }

            controller.toggleLegendCollapse = function () {
                controller.legendCollapsed = !controller.legendCollapsed;
            }

            controller.resetFilter = function (collapse) {
                if (collapse) {
                    controller.toggleLegendCollapse();
                } //if

                controller.model.dateofbirthstartdate = null;
                controller.model.dateofbirthenddate = null;
                controller.model.email = null;
                controller.model.mobilenumber = null;
                controller.model.residentialAddress = null;
                controller.model.verificationstatus = null;
                controller.model.currentAccountBalance = {
                    operator: '=',
                    value: null
                };
                controller.model.includeexcludetestaccount = null;
                controller.model.haspendingwithdrawals = null;
                controller.model.accountmanagementstatus = null;
                controller.model.lastloginstartdate = null;
                controller.model.lastloginenddate = null;
                controller.model.clienttype = null;
                controller.model.assignedaccountmanager = null;
                controller.model.tags = null;
                controller.model.lastdepositstartdate = null;
                controller.model.lastdepositenddate = null;
                controller.model.cashierTranStatus = null;
                controller.model.username = null;
                controller.model.firstName = null;
                controller.model.lastName = null;
                controller.model.status = null;
                controller.model.statusReason = null;
                controller.model.id = null;
                controller.model.signupDateRangeStart = null;
                controller.model.signupDateRangeEnd = null;
                controller.model.cashierStartDate = null;
                controller.model.cashierEndDate = null;
                controller.model.isTestAccount = 0;
                controller.model.isAgeVerified = 0;
                controller.model.isAddressVerified = 0;
                controller.model.isActiveRestriction = 0;
	            controller.model.isEmailValidated = 0;
	            controller.model.restrictionActiveFromDate = null;
	            controller.model.documentStatus = null;
	            controller.applyFilter(true);
            }

            controller.applyFilter = function (toggle) {
                if (toggle === true) {
                    controller.toggleLegendCollapse();
                } //if

                controller.tableLoad();

            }
            $rootScope.provide.dropDownMenuProvider['domainList']  = () => {
                return controller.domains
            }
            $rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
                controller.selectedDomains = [...data]
                controller.findAllTags();
                controller.findAllRestrictions();
                controller.tableLoad()
            }
            $rootScope.provide.dropDownMenuProvider['tagList'] = () => {
                   return controller.types
            }
            $rootScope.provide.dropDownMenuProvider['tagsChange'] = (data) => {
                controller.selectedTypes = [...data]
                controller.tableLoad()
            }

            $rootScope.provide.dropDownMenuProvider['restrictionsList'] = () => {
                return controller.restrictions
            }

            $rootScope.provide.dropDownMenuProvider['restrictionsChange'] = (data) => {
                controller.selectedRestrictions = [...data]
                controller.tableLoad()
            }

            $rootScope.provide.dropDownMenuProvider['changeAffiliate'] = (data) => {
                controller.selectedAffiliate = [...data]
                controller.tableLoad()
            }
            window.VuePluginRegistry.loadByPage("PlayerSearchTopBar")
        }]);
