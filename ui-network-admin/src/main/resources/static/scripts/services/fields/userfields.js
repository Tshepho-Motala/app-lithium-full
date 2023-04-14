'use strict';

angular.module('lithium')
.factory('userFields', [ 'UserRest','RestrictionsRest', 'GeoRest', 'StatusRest', '$q','timezoneRest', 'formlyValidators', '$translate', 'VerificationStatusRest', 'ChangelogsRest', 'accessRulesRest', 'BiometricsStatusRest',
	function(UserRest,RestrictionsRest, GeoRest, StatusRest, $q,timezoneRest, formlyValidators, $translate, VerificationStatusRest, changelogsRest, accessRulesRest, biometricsStatusRest) {
		var service = {};
		let currentDate = moment.now();
		let minDate = moment([moment().year() - 120 , moment().month(), moment().day()]);
		function getMessageForInvalidIban(input) {
			var CODE_LENGTHS = {
				AD: 24, AE: 23, AT: 20, AZ: 28, BA: 20, BE: 16, BG: 22, BH: 22, BR: 29,
				CH: 21, CR: 21, CY: 28, CZ: 24, DE: 22, DK: 18, DO: 28, EE: 20, ES: 24,
				FI: 18, FO: 18, FR: 27, GB: 22, GI: 23, GL: 18, GR: 27, GT: 28, HR: 21,
				HU: 28, IE: 22, IL: 23, IS: 26, IT: 27, JO: 30, KW: 30, KZ: 20, LB: 28,
				LI: 21, LT: 20, LU: 20, LV: 21, MC: 27, MD: 24, ME: 22, MK: 19, MR: 27,
				MT: 31, MU: 30, NL: 18, NO: 15, PK: 24, PL: 28, PS: 29, PT: 25, QA: 29,
				RO: 24, RS: 22, SA: 24, SE: 24, SI: 19, SK: 24, SM: 27, TN: 24, TR: 26,   
				AL: 28, BY: 28, CR: 22, EG: 29, GE: 22, IQ: 23, LC: 32, SC: 31, ST: 25,
				SV: 28, TL: 23, UA: 29, VA: 22, VG: 24, XK: 20
			};
			var iban = String(input).toUpperCase().replace(/[^A-Z0-9]/g, ''), // keep only alphanumeric characters
					code = iban.match(/^([A-Z]{2})(\d{2})([A-Z\d]+)$/), // match and capture (1) the country code, (2) the check digits, and (3) the rest
					digits;

			//check if iban is less than the required chars
			if (!code ||iban.length < CODE_LENGTHS[code[1]]) {
				return  "UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.SHORT_ERROR";
			}

			//check if iban is more than the required chars
			if (iban.length > CODE_LENGTHS[code[1]]) {
				return "UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.LONG_ERROR";
			}

			// rearrange country code and check digits, and convert chars to ints
			digits = (code[3] + code[1] + code[2]).replace(/[A-Z]/g, function (letter) {
				return letter.charCodeAt(0) - 55;
			});
			
			// final check
			return mod97(digits) === 1 ? null: "UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.ERROR";
		}
		
		function mod97(string) {
			var checksum = string.slice(0, 2), fragment;
			for (var offset = 2; offset < string.length; offset += 7) {
				fragment = String(checksum) + string.substring(offset, offset + 7);
				checksum = parseInt(fragment, 10) % 97;
			}
			return checksum;
		}

		service.username = function(domain) {
			return {
				className: "col-xs-12",
				key: "username",
				type: "input",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true,
					minlength: 2, maxlength: 35,
					focus: true
				},
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},
				
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.DESCRIPTION" | translate'
				},
				validators: {
					pattern: {
						expression: function($viewValue, $modelValue, scope) {
							return /^[0-9A-Za-z_\\.]+$/.test($viewValue);
						},
						message: '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.PATTERN" | translate'
					}
				},
				asyncValidators: {
					machineNameUnique: {
						expression: function($viewValue, $modelValue, scope) {
							return UserRest.isUnique(domain.name, $viewValue)
								.then(function(result) {
									if (result) return $q.resolve("Check"); 
									return $q.reject("Not check");
								});
						},
						message: '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.UNIQUE" | translate'
					}
				}
			}
		};

		service.editUsername = function(user, disabled) {
			return {
				className: "col-xs-12",
				key: "username",
				type: "input",
				templateOptions: {
					disabled: !disabled,
					required: true,
					minlength: 2, maxlength: 35,
					focus: true
				},
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.DESCRIPTION" | translate'
				},
				validators: {
					pattern: {
						expression: function($viewValue, $modelValue, scope) {
							return /^[0-9A-Za-z_\\.]+$/.test($viewValue);
						},
						message: '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.PATTERN" | translate'
					}
				},
				asyncValidators: {
					machineNameUnique: {
						expression: function($viewValue, $modelValue, scope) {
							return UserRest.isUnique(user.domain.name, $viewValue, user.id)
								.then(function(result) {
									if (result) return $q.resolve("Check");
									return $q.reject("Not check");
								});
						},
						message: '"UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.UNIQUE" | translate'
					}
				}
			}
		};

		service.firstName = {
			className: "col-xs-12",
			key: "firstName",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 255, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.DESCRIPTION" | translate'
			},
			validators: {
				pattern: formlyValidators.firstName("UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.PATTERN")
			}
		};

		service.lastNamePrefixTypeHead = function (domainSettings) {
			let field = {
				className: "col-xs-12",
				key: "lastNamePrefix",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: ""
				},
				hideExpression: domainSettings['lastNamePrefix'] == "show" ? "false" : "true",
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.LAST_NAME_PREFIX.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.LAST_NAME_PREFIX.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.LAST_NAME_PREFIX.DESCRIPTION" | translate'
				},
				validators: {
					pattern: formlyValidators.lastNamePrefix("UI_NETWORK_ADMIN.USER.FIELDS.LAST_NAME_PREFIX.PATTERN")
				}
			};
			return field;
		}
		
		service.lastName = {
			className: "col-xs-12",
			key: "lastName",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 255
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.DESCRIPTION" | translate'
			},
			validators: {
				pattern: formlyValidators.lastName("UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.PATTERN")
			}
		};
		
		service.newPassword = {
			className: "col-md-6", key: "newPassword", type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "", type: "password", required: true, minlength: 6, maxlength: 35
			},
			validators: { pattern: formlyValidators.password() },
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.CHANGEPASSWORD.NEW.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.CHANGEPASSWORD.NEW.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.CHANGEPASSWORD.NEW.DESCRIPTION" | translate'
			}
		};
		
		service.confirmPassword = {
			className: "col-md-6", key: "confirmPassword", type: "input", optionsTypes: ['matchField'],
			templateOptions: {
				label: "", description: "", placeholder: "", type: "password", required: true, minlength: 6, maxlength: 35
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.CHANGEPASSWORD.CONFIRM.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.CHANGEPASSWORD.CONFIRM.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.CHANGEPASSWORD.CONFIRM.DESCRIPTION" | translate'
			},
			data: {
				fieldToMatch: 'newPassword',
				matchFieldMessage: "'UI_NETWORK_ADMIN.USER.FIELDS.CHANGEPASSWORD.CONFIRM.MATCH' | translate"
			}
		};
		
		service.telephoneNumber = {
			className: "col-xs-12",
			key: "telephoneNumber",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false, minlength: 2, maxlength: 35
			},
			expressionProperties: {
				'templateOptions.label': '"GLOBAL.FIELDS.TELEPHONENUMBER" | translate',
				'templateOptions.placeholder': '"GLOBAL.FIELDS.TELEPHONENUMBER" | translate',
				'templateOptions.description': '"GLOBAL.FIELDS.TELEPHONE.DESCRIPTION" | translate'
			},
			validators: {
				pattern: formlyValidators.telephone()
			}
		};
		
		service.cellphoneNumber = {
			className: "col-xs-12",
			key: "cellphoneNumber",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false, minlength: 2, maxlength: 35
			},
			expressionProperties: {
				'templateOptions.label': '"GLOBAL.FIELDS.CELLPHONENUMBER" | translate',
				'templateOptions.placeholder': '"GLOBAL.FIELDS.CELLPHONENUMBER" | translate',
				'templateOptions.description': '"GLOBAL.FIELDS.TELEPHONE.DESCRIPTION" | translate'
			},
			validators: {
				pattern: formlyValidators.telephone()
			}
		};

		service.tagnameMultiSelect = function(key, label, placeholder, description, domainName) {
			return {
				className: 'col-md-4 col-xs-12',
				key: key,
				type: 'ui-select-multiple',
				templateOptions: {
					label: label,
					placeholder: placeholder,
					description: description,
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					options: [],
					required: true
				},
				controller: ['$scope', function ($scope) {
					UserRest.findAllTags([domainName]).then(function(response) {
						$scope.options.templateOptions.options = response.plain();
						return response.plain();
					})
				}]
			}
		}

		service.tagnameSingleSelect = function(key, label, placeholder, description, domainName) {
			return {
				className: 'col-md-4 col-xs-12',
				key: key,
				type: 'ui-select-single',
				templateOptions: {
					label: label,
					placeholder: placeholder,
					description: description,
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					options: [],
					required: true
				},
				controller: ['$scope', function ($scope) {
					UserRest.findAllTags([domainName]).then(function(response) {
						$scope.options.templateOptions.options = response.plain();
						return response.plain();
					})
				}]
			}
		}

		service.radio = function(key, label, defaultValue, options, order=0) {
			return {
				className: 'col-md-4 col-xs-12',
				key: key,
				type: 'radio',
				defaultValue: defaultValue,
				templateOptions: {
					type: 'radio',
					label: label,
					required: true,
					name: 'gender',
					valueProp: 'key',
					labelProp: 'value',
					options: options,
					order
				}
			}
		}

		service.testPlayerSelect = {
			key: "testPlayer",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions: {
				label: "", description: "", placeholder: "", required: true,
				optionsAttr: 'bs-options',
				valueProp: 'id',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [{id: false, name: false}, {id: true, name: true}]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.TEST_PLAYER.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.TEST_PLAYER.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.TEST_PLAYER.DESCRIPTION" | translate'
			}
		};

		service.accessRule =  {
				key: "accessRule",
				className: "col-xs-12 form-group",
				type: "ui-select-single",
				templateOptions: {
					label: "", description: "", placeholder: "", required: true,
					optionsAttr: 'bs-options',
					valueProp: 'name',
					labelProp: 'name',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ACTIONS.PROCESS_ACCESS_RULE.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ACTIONS.PROCESS_ACCESS_RULE.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.MASS_UPDATE.ACTIONS.PROCESS_ACCESS_RULE.DESCRIPTION" | translate'
				},
				controller: ['$scope', function ($scope) {
					accessRulesRest.findByDomainName($scope.model.domainName).then(function (response) {
						$scope.options.templateOptions.options = response;
						return response;
					});
				}]
			}


		service.status = {
			key: "status",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions: {
				label: "", description: "", placeholder: "", required: true,
				optionsAttr: 'bs-options',
				valueProp: 'id',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
//				onChange: function() {console.log(this);},
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.DESCRIPTION" | translate'
			},
			controller: ['$scope', function ($scope) {
				StatusRest.findAll().then(function (response) {
					$scope.options.templateOptions.options = response;
					return response;
				});
			}]
		};

		service.biometricsStatus = {
			key: "biometricsStatus",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions: {
				label: "Biometrics Status", description: "", placeholder: "", required: true,
				optionsAttr: 'bs-options',
				valueProp: 'value',
				labelProp: 'value',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
			 	options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.BIOMETRICS_STATUS.STATUS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.BIOMETRICS_STATUS.STATUS.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.BIOMETRICS_STATUS.STATUS.DESCRIPTION" | translate'
			},
			controller: ['$scope', function ($scope) {
				biometricsStatusRest.findAll().then(function (response) {
					let list = []
					response.plain().forEach(item => {
						list.push({value: item})
					})
					$scope.options.templateOptions.options = list
					return response;
				});
			}]
		};

		service.comment = function (key, label, placeholder, description, required, order=0) {
			return {
				className: "col-xs-12",
				key: key,
				type: "textarea",
				templateOptions: {
					label: label, description: description, placeholder: placeholder,
					minlength: 5, maxlength: 65535, required: required,
					order
				}
			}
		}

		service.statusReason = function (status, domainName, statuses, valueProp, excludeReasons) {
			return {
				className: 'col-xs-12',
				key: 'statusReason',
				type: 'ui-select-single',
				templateOptions : {
					label: "Status Reason",
					description: "",
					valueProp: valueProp,
					labelProp: 'description',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: [],
					required: true
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					var statusId = undefined;
					if (scope.model.status && scope.model.status.id) {
						statusId = scope.model.status.id;
					} else {
						statusId = scope.model.status;
					}
					for (var i = 0; i < statuses.length; i++) {
						var s = statuses[i];
						if (s.id === statusId) {
							return s.userEnabled;
						}
					}
					return true;
				},
				controller: ['$scope', function($scope) {

					if ((status)) {
						StatusRest.findReasonsByStatus(domainName, status).then(function (statusReasons) {
							for (var k = 0; k < excludeReasons.length; k++) {
								for (var i = 0; i < statusReasons.length; i++) {
									/* ToDo: We might need to revisit this, since there could be additional cases in future for certain exclusion types
                                        that should not be allowed to be set by LBO users but only by system.
                                        So a retrieval and config system for this might be a good way to allow different brands
                                        to do different things with their allowed exclusion selections. */
									if (statusReasons[i].name === excludeReasons[k]) {
										statusReasons.splice(i, 1);
										break;
									}
								}
							}
							$scope.to.options = statusReasons;
						});
					}
				}],
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.REASON.NAME" | translate'
				}
			}
		}

		service.addNote = [
			{
				className: 'col-xs-12',
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
					required: true
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
			},
			{
				className: 'col-xs-12',
				key: 'subCategory',
				type: 'ui-select-single',
				templateOptions: {
					label: "Sub Category",
					placeholder: "Select sub category...",
					valueProp: 'name',
					labelProp: 'name',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					options: [],
					required: false,
					disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.PLACEHOLDER" | translate',
				},
			},
			{
				className: 'col-xs-12',
				key: 'priority',
				type: 'ui-select-single',
				templateOptions: {
					label: '',
					valueProp: 'value',
					labelProp: 'label',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: [
						{
							value: 0,
							label: $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LOW")
						},
						{
							value: 34,
							label: $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.MEDIUM")
						},
						{
							value: 67,
							label: $translate.instant("UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.HIGH")
						}
					],
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.PLACEHOLDER" | translate',

				}
			},
			service.comment('text',
				$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.NOTE.COMMENT.NAME"),
				$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.NOTE.COMMENT.PLACEHOLDER"), '', false)
		];



        service.verificationStatus = {
                key: "verificationStatus",
                className: "col-xs-12 form-group",
                type: "ui-select-single",
                templateOptions: {
                    label: "", description: "", placeholder: "", required: false,
                    optionsAttr: 'bs-options',
                    valueProp: 'id',
                    labelProp: 'code',
                    optionsAttr: 'ui-options', ngOptions: 'ui-options',
                    options: []
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATION_STATUS.STATUS.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATION_STATUS.STATUS.PLACEHOLDER" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATION_STATUS.STATUS.DESCRIPTION" | translate'
                },
                controller: ['$scope', function ($scope) {
                    VerificationStatusRest.findAll().then(function (response) {
                        $scope.options.templateOptions.options = response;
                        return response;
                    });
                }]
        };

		service.ageVerified = {
			key: "ageVerified",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions: {
				label: "", description: "", placeholder: "", required: false,
				valueProp: 'value',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [
					{
						value: null,
						name: 'unchanged'
					},
					{
						value: true,
						name: 'true'
					},
					{
						value: false,
						name: 'false'
					}
				]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.IS_AGE_VERIFIED.STATUS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.IS_AGE_VERIFIED.STATUS.PLACEHOLDER" | translate',
			},
		};

		service.addressVerified = {
			key: "addressVerified",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions: {
				label: "", description: "", placeholder: "", required: false,
				valueProp: 'value',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [
					{
						value: null,
						name: 'unchanged'
					},
					{
						value: true,
						name: 'true'
					},
					{
						value: false,
						name: 'false'
					}
				]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.IS_ADDRESS_VERIFIED.STATUS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.IS_ADDRESS_VERIFIED.STATUS.PLACEHOLDER" | translate',
			},
		};
		
		service.timezone = {
		key: "timezone", 
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "", description: "", placeholder: "", required : false,
				optionsAttr: 'bs-options',
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
		service.gender = {
				key: "gender", 
					className: "col-xs-12 form-group",
					type: "ui-select-single",
 					templateOptions : {
						label: "", description: "", placeholder: "", required : false,
						optionsAttr: 'bs-options',
						valueProp: 'value',
						labelProp: 'label',
						// optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: []
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.GENDER.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.GENDER.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.GENDER.DESCRIPTION" | translate'
					} ,
					controller: ['$scope', function($scope) {
						var options = [];
						$translate('UI_NETWORK_ADMIN.USER.FIELDS.GENDER.GENDERS.MALE').then(function (male) {
							options.push({label:male, value:'Male'});
						}) 
						$translate('UI_NETWORK_ADMIN.USER.FIELDS.GENDER.GENDERS.FEMALE').then(function (female) {
							options.push({label:female, value:'Female'});
						})
						$scope.options.templateOptions.options = options;
 					}]
		}
		
		service.dateOfBirth = {
			className: "col-xs-12",
			key: "dateOfBirth",
			type: "datepicker",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "",
				required: false,
				datepickerOptions: {
					format: 'dd/MM/yyyy'
				}
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.DATEOFBIRTH.NAME" | translate'
			}
		}
		
		service.country = {
			key: "countryCode", 
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "Lets do this", description: "", placeholder: "", required : false,
				optionsAttr: 'bs-options',
				valueProp: 'code',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.COUNTRY.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.COUNTRY.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.COUNTRY.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				GeoRest.countries().then(function(response) {
					$scope.to.options = response;
				});
			}]
		};
		
		service.cityTypeAhead = function(key, required, displayOnly) {
			var field = {
				key: key+".city",
				type: "uib-typeahead",
				className: "col-xs-12",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					placeholder: "",
					required : required,
					valueProp: 'code',
					labelProp: 'name',
					displayProp: 'cityWithCountry',
					displayOnly: displayOnly,
					editable: true
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.ADDRESS.CITY.NAME" | translate',
					'templateOptions.placeholder': '"GLOBAL.ADDRESS.CITY.PLACEHOLDER" | translate'
//					'templateOptions.description': '"GLOBAL.ADDRESS.CITY.DESCRIPTION" | translate'
//						,'templateOptions.disabled': function($viewValue, $modelValue, scope) {return (!!$modelValue);}
				},
				controller: ['$scope', function($scope) {
					$scope.searchTypeAhead = function(searchValue) {
						var postalCode = $.grep($scope.fields, function(e){return e.key === (key+'.postalCode'); });
						postalCode[0].templateOptions.mask = null;
						if (angular.isUndefinedOrNull($scope.model[key].adminLevel1Code)) {
							if (angular.isUndefinedOrNull($scope.model[key].countryCode)) {
//								console.log('no country, no state specified, do global search : '+searchValue);
								return GeoRest.cities(searchValue).then(function(response) {
									$scope.to.options = response;
									return response;
								});
							} else {
//								console.log('no state specified, but have country, use that to search : '+searchValue);
								return GeoRest.countryCitiesSearch($scope.model[key].countryCode, searchValue).then(function(response) {
									$scope.to.options = response;
									return response;
								});
							}
						} else {
//							console.log('state specified, use that to search : '+searchValue);
							return GeoRest.level1CitiesSearch($scope.model[key].countryCode, $scope.model[key].adminLevel1Code, searchValue).then(function(response) {
								$scope.to.options = response;
								return response;
							});
						}
					}
					$scope.resetTypeAhead = function() {
						$scope.model[key].city = null;
						$scope.model[key].cityCode = null;
					}
					$scope.selectTypeAhead = function($item, $model, $label, $event) {
						var postalCode = $.grep($scope.fields, function(e){return e.key === (key+'.postalCode'); });
						//postalCode[0].templateOptions.mask = $item.postalCodeFormat;
//						var adminLevel1Code = $.grep($scope.fields, function(e){return e.key === (key+'.adminLevel1'); });
//						var countryCode = $.grep($scope.fields, function(e){return e.key === (key+'.country'); });
						$scope.model[key].city = $item.name;
						$scope.model[key].cityCode = $item.code;
//						$scope.model[key].adminLevel1 = $item.level1;
//						$scope.model[key].adminLevel1Code = $item.level1Code;
//						$scope.model[key].country = $item.country;
//						$scope.model[key].countryCode = $item.countryCode;
					}
				}]
			}
			return field;
		}
		
		service.adminLevel1TypeAhead = function(key, required, displayOnly) {
			var field = {
				key: key+".adminLevel1",
				type: "uib-typeahead",
				className: "col-xs-12",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					placeholder: "",
					required : required,
					valueProp: 'code',
					labelProp: 'name',
					displayProp: 'adminLevel1WithCountry',
					displayOnly: displayOnly,
					editable: true
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.ADDRESS.ADMINLEVEL1.NAME" | translate',
					'templateOptions.placeholder': '"GLOBAL.ADDRESS.ADMINLEVEL1.PLACEHOLDER" | translate'
//					'templateOptions.description': '"GLOBAL.ADDRESS.ADMINLEVEL1.DESCRIPTION" | translate'
//					,'templateOptions.disabled': function($viewValue, $modelValue, scope) {return (!!$modelValue);}
				},
				controller: ['$scope', function($scope) {
					$scope.searchTypeAhead = function(searchValue) {
						if (angular.isUndefinedOrNull($scope.model[key].countryCode)) {
							console.debug("no country specified, doing global adminlevel1 search");
							return GeoRest.level1s(searchValue).then(function(response) {
								$scope.to.options = response;
								return response;
							});
						} else {
							console.debug("using country for adminlevel1 search");
							return GeoRest.countryLevel1sSearch($scope.model[key].countryCode, searchValue).then(function(response) {
								$scope.to.options = response;
								return response;
							});
						}
					}
					$scope.resetTypeAhead = function() {
//						$scope.model[key].city = null;
//						$scope.model[key].cityCode = null;
						$scope.model[key].adminLevel1 = null;
						$scope.model[key].adminLevel1Code = null;
					}
					$scope.selectTypeAhead = function($item, $model, $label, $event) {
						var postalCode = $.grep($scope.fields, function(e){return e.key === (key+'.postalCode'); });
						//postalCode[0].templateOptions.mask = $item.postalCodeFormat;
//						var cityCode = $.grep($scope.fields, function(e){return e.key === (key+'.city'); });
//						var countryCode = $.grep($scope.fields, function(e){return e.key === (key+'.country'); });
//						cityCode[0].templateOptions.options = [];
//						$scope.model[key].city = null;
//						$scope.model[key].cityCode = null;
						$scope.model[key].adminLevel1 = $item.name;
						$scope.model[key].adminLevel1Code = $item.code;
//						$scope.model[key].country = $item.country;
//						$scope.model[key].countryCode = $item.countryCode;
					}
				}]
			}
			return field;
		}
		
		service.countryTypeAhead = function(key, field, displayOnly, standAlone) {
			var field = {
				key: (key+field),
				type: "uib-typeahead",
				className: "col-xs-12",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					placeholder: "",
					required : true,
					valueProp: 'code',
					labelProp: 'name',
					displayProp: 'name',
					disabled: false,
					displayOnly:displayOnly,
					editable: true
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.ADDRESS.COUNTRY.NAME" | translate',
					'templateOptions.placeholder': '"GLOBAL.ADDRESS.COUNTRY.PLACEHOLDER" | translate'
//					'templateOptions.description': '"GLOBAL.ADDRESS.COUNTRY.DESCRIPTION" | translate'
//					,'templateOptions.disabled': function($viewValue, $modelValue, scope) {return (!!$modelValue);}
				},
				controller: ['$scope', function($scope) {
					$scope.searchTypeAhead = function(searchValue) {
						return GeoRest.countriesSearch(searchValue).then(function(response) {
							$scope.to.options = response;
							return response;
						});
					}
					$scope.resetTypeAhead = function() {
						if (!standAlone) {
							$scope.model[key].city = null;
							$scope.model[key].cityCode = null;
							$scope.model[key].adminLevel1 = null;
							$scope.model[key].adminLevel1Code = null;
							$scope.model[key].postalCode = null;
						}
						if (key === '') {
							$scope.model.country = null;
							$scope.model.countryCode = null;
						} else {
							$scope.model[key].country = null;
							$scope.model[key].countryCode = null;
						}
					}
					$scope.selectTypeAhead = function($item, $model, $label, $event) {
						if (!standAlone) {
							var postalCode = $.grep($scope.fields, function(e){return e.key === (key+'.postalCode'); });
							//postalCode[0].templateOptions.mask = $item.postalCodeFormat;
							$scope.model[key].city = null;
							$scope.model[key].cityCode = null;
							$scope.model[key].adminLevel1 = null;
							$scope.model[key].adminLevel1Code = null;
						}
						if (key === '') {
							$scope.model.country = $item.name;
							$scope.model.countryCode = $item.code;
						} else {
							$scope.model[key].country = $item.name;
							$scope.model[key].countryCode = $item.code;
						}
					}
				}]
			}
			return field;
		}

		service.placeOfBirthInput = function(key, domainSettings) {
			var field = {
				key: (key),
				type: "input",
				className: "col-xs-12",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					placeholder: ""
				},
				hideExpression: domainSettings['placeOfBirth'] == "show" ? "false" : "true",
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.PLACE_OF_BIRTH.NAME" | translate',
					'templateOptions.placeholder': '"GLOBAL.PLACE_OF_BIRTH.PLACEHOLDER" | translate'
				}
			}
			return field;
		}

		service.ibanTypeHead = function (domainSettings, authorised) {
			let field = {
				className: "col-xs-12",
				key: "additionalData['iban']",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: ""
				},
				hideExpression: !authorised ? "true" : (domainSettings['iban'] == "show" ? "false" : "true"),
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.IBAN.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.IBAN.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.IBAN.DESCRIPTION" | translate'
				}
			};
			return field;
		}
		
		service.postalCode = function(key, label, placeholder, description, required, mask, placeholderChar) {
			var field = {
				className: "col-xs-12",
				key: key,
				type: "masked-input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "",
					description: "",
					placeholder: "",
					required: required,
					mask: mask,
					placeholderChar: placeholderChar
				},
				expressionProperties: {
					'templateOptions.label': '"'+label+'" | translate',
					'templateOptions.placeholder': '"'+placeholder+'" | translate',
					'templateOptions.description': '"'+description+'" | translate'
				}
			};
			return field;
		}
		
		service.email = {
			key: "email", type: "input", className: "col-xs-12",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				type: "email", required: true, minlength: 2,
				addonRight: { class: "fa fa-envelope" }
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.EMAIL.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.EMAIL.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.EMAIL.DESCRIPTION" | translate'
			},
			validators: {
				pattern: formlyValidators.email()
			}
		};
		
		var textField = function(key, label, placeholder, description, pattern, message) {
			return textFieldOpt(key, label, placeholder, description, pattern, message, true);
		}
		
		var textFieldOpt = function(key, label, placeholder, description, pattern, message, required) {
			var field = {
				className: "col-xs-12",
				key: key,
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "",
					description: "",
					placeholder: "",
					required: required,
					minlength: 2,
					maxlength: 35
				},
				expressionProperties: {
					'templateOptions.label': '"'+label+'" | translate',
					'templateOptions.placeholder': '"'+placeholder+'" | translate',
					'templateOptions.description': '"'+description+'" | translate'
				},
				validators: {
					pattern: formlyValidators.pattern(pattern, message)
				}
			};
			return field;
		};
		
		service.group = function(heading, fields) {
			var newFields;
			if (heading.length > 0) {
				newFields = [{
					className: "col-xs-12",
					template: "<h4>{{ '"+heading+"' | translate }}</h4><hr/>"
				}];
			} else {
				newFields = [{
					className: "col-xs-12",
					template: ""
				}];
			}
			return {
				fieldGroup: newFields.concat(fields)
			}
		}
		
		service.address = function(key, heading, displayOnly) {
			return service.group(heading, [
//				(key, label, placeholder, description, pattern, message) {
				textField(key + ".addressLine1", "GLOBAL.ADDRESS.LINE1.NAME", "GLOBAL.ADDRESS.LINE1.PLACEHOLDER", "", /^[A-Za-z0-9\.\, +]*$/, "GLOBAL.ADDRESS.LINE1.MESSAGE"),
				textFieldOpt(key + ".addressLine2", "GLOBAL.ADDRESS.LINE2.NAME", "GLOBAL.ADDRESS.LINE2.PLACEHOLDER", "", /^[A-Za-z0-9\.\, +]*$/, "GLOBAL.ADDRESS.LINE2.MESSAGE", false),
				textFieldOpt(key + ".addressLine3", "GLOBAL.ADDRESS.LINE3.NAME", "GLOBAL.ADDRESS.LINE3.PLACEHOLDER", "", /^[A-Za-z0-9\.\, +]*$/, "GLOBAL.ADDRESS.LINE3.MESSAGE", false),
				service.cityTypeAhead(key, true, displayOnly),
				service.adminLevel1TypeAhead(key, true, displayOnly),
				service.countryTypeAhead(key, '.country', displayOnly, true),
				service.postalCode(key + ".postalCode", "GLOBAL.ADDRESS.POSTALCODE.NAME", "GLOBAL.ADDRESS.POSTALCODE.PLACEHOLDER", "", true, '', '_'),
			]);
		}

		service.iban = function() {
			let errorMessage = null;

			return [
				{
					className: "col-xs-12",
					key: "iban",
					type: "input",
					validators: {
						iban: {
							expression: (viewValue, modelValue) => {
								errorMessage = getMessageForInvalidIban(viewValue || modelValue);

								return  errorMessage === null
							}
						}
					},

					validation: {
						messages: {
							iban: function(viewValue, modelValue, scope) {
								return errorMessage
							}
						}
					},
					templateOptions: {
						label: "",
						description: "",
						placeholder: ""
						
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.IBAN.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.IBAN.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.IBAN.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "reason",
					type: "textarea",
					templateOptions: {
						label: "",
						description: "",
						placeholder: "",
						required: true,
						minlength: 4,
						maxlength: 65535,
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.LABEL" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION" | translate'
					}
				}
			];
		}

		service.restrictionMultiSelect = (key,label, placeholder, description, domainName, order= 0) =>  {
			return {
					className: 'col-md-12 col-xs-12',
					key: key,
					type: 'ui-select-single',
					templateOptions: {
						label: label,
						placeholder: placeholder,
						description: description,
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options',
						ngOptions: 'ui-options',
						options: [],
						required: true,
						order
					},
					controller: ['$scope', function ($scope) {
						RestrictionsRest.list([domainName], true).then(function(response) {
							let options = []
							for(let option of response.plain()) {
								options.push({label: option.name, value: option});
							}

							$scope.options.templateOptions.options = options;
							console.log("Checking Options:", options);
							return options
						})
					}]
				}
		}

		service.restrictionSingleSelect = function (restrictionsRest, domainName) {
			return [{
				key: "restrictionSingleSelect",
				className: "col-xs-12 form-group",
				type: "ui-select-single",
				templateOptions: {
					label: "",
					description: "",
					placeholder: "",
					required: true,
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.LIFT_RESTRICTIONS.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.LIFT_RESTRICTIONS.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.LIFT_RESTRICTIONS.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					return restrictionsRest.list([domainName], true).then((data) => {
						$scope.options.templateOptions.options = data.plain();
						return data.plain()
					}).catch((e) => {
						return []
					})
				}]
				},
			service.comment('restrictionReason',
				$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.NOTE.COMMENT.NAME"),
				$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.NOTE.COMMENT.PLACEHOLDER"), '', true)
			]
		}

		return service;
	}


]);
