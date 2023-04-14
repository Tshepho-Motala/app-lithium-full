'use strict';

angular.module('lithium')
	.controller('DomainView', ["domain",  "notify", "$q", "$translate", "$scope", "rest-domain" ,"GeoRest","timezoneRest",
	function(domain, notify, $q, $translate, $scope, restDomain ,geoRest, timezoneRest) {
		var controller = this;

		controller.model = domain;
		controller.modelOriginal = angular.copy(domain);
		controller.options = { formState: { readOnly: true } };

		controller.fields_personal = [{
			className: "col-xs-12",
			key: "name",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 35, disabled: true,
				onKeydown: function(value, options) {
					options.validation.show = false;
				},
				onBlur: function(value, options) {
					options.validation.show = true;
				}
			},
			modelOptions: {
				updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.DESCRIPTION" | translate'
			},
			validators: {
				pattern: {
					expression: function($viewValue, $modelValue, scope) {
						return /^[0-9a-z_\\.]+$/.test($viewValue);
					},
					message: '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.PATTERN" | translate'
				}
			},
			asyncValidators: {
				nameUnique: {
					expression: function($viewValue, $modelValue, scope) {
						if (controller.modelOriginal.name === $modelValue) {
							return $q.resolve("Same name stays");
						} else {
							scope.options.templateOptions.loading = true;
							return restDomain.findByName(encodeURIComponent($viewValue)).then(function(domain) {
								scope.options.templateOptions.loading = false;
								if (angular.isUndefined(domain) || (domain._status == 404) || (domain.length === 0)) {
									return $q.resolve("No such domain");
								} else {
									return $q.reject("The domain already exists");
								}
							});
						}
					},
					message: '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.UNIQUE" | translate'
				}
			}
		},{
			className: "col-xs-12",
			key: "displayName",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 35, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.DESCRIPTION" | translate'
			}
		},
			// {
			// 	className: "col-xs-12",
			// 	key: "timeout",
			// 	type: "input",
			// 	optionsTypes: ['editable'],
			// 	templateOptions: {
			// 		label: "", description: "", placeholder: "",
			// 		required: false, minlength: 3, maxlength: 8, disabled: false
			// 	},
			// 	expressionProperties: {
			// 		'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.TIMEOUT.NAME" | translate',
			// 		'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.TIMEOUT.NAME" | translate',
			// 		'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.TIMEOUT.DESCRIPTION" | translate'
			// 	},
			// 	validators: {
			// 		timeoutValid: {
			// 			expression: function($viewValue, $modelValue, scope) {
			// 				var value = $modelValue || $viewValue;
			// 				if(value < 200) {
			// 					return false;
			// 				}
			// 				return true;
			// 			},
			// 			message: '($viewValue != undefined && $viewValue != "")? $viewValue + " seconds is too short: Minimum is 200 Seconds" : ""'
			// 		}
			// 	}
			// },

			{
			className: "col-xs-12",
			key: "description",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false, minlength: 2, maxlength: 35
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},{
			className  : "col-xs-8",
			type : "ui-select-single",
			key : "defaultCountry",
			optionsTypes: ['editable'],
			templateOptions  : {
				label: "Country",
				optionsAttr: 'bs-options',
				description : "Default country for domain.",
				valueProp : 'iso3',
				labelProp : 'name',
				notNull: false,
				nullDisplay: 'Default country for domain.',
				placeholder : 'Select default country for domain',
				options : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.COUNTRY.FIELDS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.COUNTRY.FIELDS.COUNTRY.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.COUNTRY.FIELDS.COUNTRY.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				geoRest.countries().then(function(response) {
					$scope.options.templateOptions.options = response;
					return response;
				});
			}]
		},
			{
				key: "defaultTimezone",
				className: "col-xs-12 form-group",
				type: "ui-select-single",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "", description: "", placeholder: "", required : false,
					valueProp: 'value',
					labelProp: 'label',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.TIMEZONE.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.TIMEZONE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.TIMEZONE.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					timezoneRest.timezoneList().then(function(response) {
						$scope.to.options = response;
					});
				}]
			}
		,{
			className: "col-xs-12",
			key: "supportUrl",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.SUPPORTURL.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.SUPPORTURL.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.SUPPORTURL.DESCRIPTION" | translate'
			},
			validators: {
				urlValid: {
					expression: function($viewValue, $modelValue, scope) {
						var value = $modelValue || $viewValue;
						return /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/.test(value);
					},
					message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid URL. Make sure to enter the protocol, e.g. http:// or https://" : ""'
				}
			}
		},{
			className: "col-xs-12",
			key: "currency",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 3, maxlength: 3, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY.DESCRIPTION" | translate'
			}
		},{
			className: "col-xs-12",
			key: "currencySymbol",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 1, maxlength: 3, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY_SYMBOL.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY_SYMBOL.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY_SYMBOL.DESCRIPTION" | translate'
			}
		},{
			className: "col-xs-12",
			key: "defaultLocale",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 5, maxlength: 20, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.LOCALE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.LOCALE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.LOCALE.DESCRIPTION" | translate'
			}
		},{
			className: "col-xs-12",
			key: "supportEmail",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.SUPPORTEMAIL.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.SUPPORTEMAIL.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.SUPPORTEMAIL.DESCRIPTION" | translate'
			},
			validators: {
				emailValid: {
					expression: function($viewValue, $modelValue, scope) {
						var value = $modelValue || $viewValue;
						return /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value);
					},
					message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid email address" : ""'
				}
			}
		},{
			className: "col-xs-12",
			key: "url",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.URL.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.URL.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.URL.DESCRIPTION" | translate'
			},
			validators: {
				urlValid: {
					expression: function($viewValue, $modelValue, scope) {
						var value = $modelValue || $viewValue;
						return /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/.test(value);
					},
					message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid URL. Make sure to enter the protocol, e.g. http:// or https://" : ""'
				}
			}
		},{
				className : "col-xs-8",
				key : "preSignupAccessRule",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					placeholder : '',
					notNull: false,
					nullDisplay: 'Select Access Rule',
					options : []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.PRE_SIGNUP.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.PRE_SIGNUP.DESCRIPTION" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.PRE_SIGNUP.PLACEHOLDER" | translate',
					'templateOptions.disabled': function(viewValue, modelValue, scope) {
						return scope.formState.readOnly;
					}
				},
				controller: ['$scope', 'accessRulesRest', function($scope, accessRulesRest) {
					accessRulesRest.findByDomainName(domain.name).then(function(response) {
						$scope.to.options = response;
					});
				}]
			},{
				className : "col-xs-8",
				key : "signupAccessRule",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					placeholder : '',
					notNull: false,
					nullDisplay: 'Select Access Rule',
					options : []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.POST_SIGNUP.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.POST_SIGNUP.DESCRIPTION" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.POST_SIGNUP.PLACEHOLDER" | translate',
					'templateOptions.disabled': function(viewValue, modelValue, scope) {
						return scope.formState.readOnly;
					}
				},
				controller: ['$scope', 'accessRulesRest', function($scope, accessRulesRest) {
					accessRulesRest.findByDomainName(domain.name).then(function(response) {
						$scope.to.options = response;
					});
				}]
		},{
			className : "col-xs-8",
			key : "ipblockList",
			type : "ui-select-single",
			templateOptions : {
				label : "",
				optionsAttr: 'bs-options',
				description : "",
				valueProp : 'name',
				labelProp : 'name',
				placeholder : '',
				notNull: false,
				nullDisplay: '',
				options : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.IPBLOCK.LABEL" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.IPBLOCK.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.IPBLOCK.DESC" | translate',
				'templateOptions.disabled': function(viewValue, modelValue, scope) {
					return scope.formState.readOnly;
				}
			},
			controller: ['$scope', 'accessControlRest', function($scope, accessControlRest) {
				accessControlRest.findByDomainNameAndListTypeName(domain.name, 'ip_list', 'true').then(function(response) {
					$scope.to.options = response;
				});
			}]
		},{
			className : "col-xs-8",
			key : "failedLoginIpList",
			type : "ui-select-single",
			templateOptions : {
				label : "",
				optionsAttr: 'bs-options',
				description : "",
				valueProp : 'name',
				labelProp : 'name',
				placeholder : '',
				notNull: false,
				nullDisplay: '',
				options : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.FAILED_LOGIN_IP.LABEL" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.FAILED_LOGIN_IP.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.FAILED_LOGIN_IP.DESC" | translate',
				'templateOptions.disabled': function(viewValue, modelValue, scope) {
					return scope.formState.readOnly;
				}
			},
			controller: ['$scope', 'accessControlRest', function($scope, accessControlRest) {
				accessControlRest.findByDomainNameAndListTypeName(domain.name, 'ip_list', 'true').then(function(response) {
					$scope.to.options = response;
				});
			}]
		},{
				className: "col-xs-8",
				key: "preLoginAccessRule",
				type: "ui-select-single",
				templateOptions: {
					optionsAttr: 'bs-options',
					description: "",
					valueProp: 'name',
					labelProp: 'name',
					notNull: false,
					nullDisplay: 'Select Access Rule',
					options: []
				},
				expressionProperties: {
					'templateOptions.disabled': function (viewValue, modelValue, scope) {
						return scope.formState.readOnly;
					},
					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.ACCESS.PRE_LOGIN_RULE.LABEL" | translate',
					'templateOptions.placeholder': '"GLOBAL.ACCESSRULE.PLACEHOLDER" | translate'
				},
				controller: ['$scope', 'accessRulesRest', function ($scope, accessRulesRest) {
					accessRulesRest.findByDomainName(domain.name).then(function (response) {
						$scope.to.options = response;
					});
				}]
			}, {
			className : "col-xs-8",
			key : "loginAccessRule",
			type : "ui-select-single",
			templateOptions : {
				optionsAttr: 'bs-options',
				description : "",
				valueProp : 'name',
				labelProp : 'name',
				notNull: false,
				nullDisplay: 'Select Access Rule',
				options : []
			},
			expressionProperties: {
				'templateOptions.disabled': function(viewValue, modelValue, scope) {
					return scope.formState.readOnly;
				},
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.ACCESS.POST_LOGIN_RULE.LABEL" | translate',
				'templateOptions.placeholder': '"GLOBAL.ACCESSRULE.PLACEHOLDER" | translate'
			},
			controller: ['$scope', 'accessRulesRest', function($scope, accessRulesRest) {
				accessRulesRest.findByDomainName(domain.name).then(function(response) {
					$scope.to.options = response;
					for (var i = 0; i < response.length; i++) {
						if (!response[i].enabled) {
							$scope.to.options[i].name += " (DISABLED)";
						}
					}
				});
			}]
		}, {
				className : "col-xs-8",
				key : "userDetailsUpdateAccessRule",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					placeholder : '',
					notNull: false,
					nullDisplay: 'Select Access Rule',
					options : []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.USER_DETAILS_UPDATE.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.USER_DETAILS_UPDATE.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.USER_DETAILS_UPDATE.DESCRIPTION" | translate',
					'templateOptions.disabled': function(viewValue, modelValue, scope) {
						return scope.formState.readOnly;
					}
				},
				controller: ['$scope', 'accessRulesRest', function($scope, accessRulesRest) {
					accessRulesRest.findByDomainName(domain.name).then(function(response) {
						$scope.to.options = response;
					});
				}]
		}, {
				className : "col-xs-8",
				key : "firstDepositAccessRule",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					placeholder : '',
					notNull: false,
					nullDisplay: 'Select Access Rule',
					options : []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.FIRST_DEPOSIT_RULE.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.FIRST_DEPOSIT_RULE.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.PLAYER_DOMAIN.FIRST_DEPOSIT_RULE.DESCRIPTION" | translate',
					'templateOptions.disabled': function(viewValue, modelValue, scope) {
						return scope.formState.readOnly;
					}
				},
				controller: ['$scope', 'accessRulesRest', function($scope, accessRulesRest) {
					accessRulesRest.findByDomainName(domain.name).then(function(response) {
						$scope.to.options = response;
					});
				}]
		}, {
			className : "col-xs-8",
			type : "input",
			key : "parent.displayName",
			templateOptions : {
				label : "", disabled :true, optionsAttr: 'bs-options',
				description : "", valueProp : 'id', labelProp : 'displayName', placeholder : "",
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PARENT.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PARENT.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PARENT.DESCRIPTION" | translate'
			}
		},{
			className : "col-xs-12",
			key : "players",
			type : "checkbox2",
//			optionsTypes: ['editable'],
			templateOptions : {
				disabled : true,
				label : "",
				description : "",
				placeholder : ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYERS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYERS.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYERS.DESCRIPTION" | translate'
			}
		},{
			className : "col-xs-12",
			key : "playerDepositLimits",
			type : "checkbox2",
			optionsTypes: ['editable'],
			templateOptions : {
				disabled : true,
				label : "",
				description : "",
				placeholder : ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DEPOSITLIMITS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DEPOSITLIMITS.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DEPOSITLIMITS.DESCRIPTION" | translate'
			},
			hideExpression: function ($viewValue, $modelValue, scope) {
				console.log(controller.model.players);
				return controller.model.players !== true;
			}
		},{
            className : "col-xs-12",
            key : "playerTimeSlotLimits",
            type : "checkbox2",
            optionsTypes: ['editable'],
            templateOptions : {
                disabled : true,
                label : "",
                description : "",
                placeholder : ""
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.TIME_SLOT.VIEW.LABEL" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TIME_SLOT.VIEW.PLACEHOLDER" | translate',
                'templateOptions.description': '"UI_NETWORK_ADMIN.TIME_SLOT.VIEW.DESC" | translate'
            },
            hideExpression: function ($viewValue, $modelValue, scope) {
                return !controller.model.players // Return the opposite of this value
            }
        },{
			className : "col-xs-12",
			key : "playerBalanceLimit",
			type : "checkbox2",
			optionsTypes: ['editable'],
			templateOptions : {
				disabled : true,
				label : "",
				description : "",
				placeholder : ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.BALANCE_LIMITS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.BALANCE_LIMITS.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.BALANCE_LIMITS.DESCRIPTION" | translate'
			},
			hideExpression: function ($viewValue, $modelValue, scope) {
				console.log(controller.model.players);
				return controller.model.players !== true;
			}
		},
			{
				className : "col-xs-12",
				key : "playtimeLimit",
				type : "checkbox2",
				optionsTypes: ['editable'],
				templateOptions : {
					disabled : true,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYTIME_LIMITS.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYTIME_LIMITS.DESCRIPTION" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYTIME_LIMITS.DESCRIPTION" | translate'
				},
				hideExpression: function ($viewValue, $modelValue, scope) {
					return !controller.model.players // Return the opposite of this value
				}
			}

		];
		
		controller.fields = [{
			className: "row v-reset-row ",
			fieldGroup: [{
				className: "col-md-12",
				fieldGroup: controller.fields_personal
			}]
		}];

		controller.onEdit = function() {
			controller.options.formState.readOnly = false;
			controller.fields_personal[1].templateOptions.focus = true;
		}

		controller.onCancel = function() {
			controller.onReset();
			controller.options.formState.readOnly = true;
		}

		controller.onReset = function() {
			controller.model = angular.copy(controller.modelOriginal);
		}

		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			if (controller.form.$valid) {
				restDomain.save(domain.name, controller.model).then(function(savedDomain) {
					notify.success("UI_NETWORK_ADMIN.DOMAIN.SAVE.SUCCESS");
					controller.model = savedDomain;
					controller.modelOriginal = angular.copy(savedDomain);
					controller.options.formState.readOnly = true;
				}, function(response) {
					notify.warning("UI_NETWORK_ADMIN.DOMAIN.SAVE.FAIL");
				});
			}
		}

		controller.changelogs = {
			domainName: domain.name,
			entityId: domain.id,
			restService: restDomain,
			reload: 0
		}
	}
]);
