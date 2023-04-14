'use strict';

angular.module('lithium')
.controller('domainAdd', ["domains","GeoRest", "notify", "$q", "$scope", "$state", "rest-domain", "errors", "bsLoadingOverlayService", 
function(domains,geoRest, notify, $q, $scope, $state, restDomain, errors, bsLoadingOverlayService ) {
	var controller = this;
	$scope.title = 'UI_NETWORK_ADMIN.DOMAIN.ADD.TITLE';
	$scope.description = 'UI_NETWORK_ADMIN.DOMAIN.ADD.DESCRIPTION';
	
	controller.model = {};
	controller.options = {};
	controller.fields = [{
		className: "row v-reset-row ",
		fieldGroup: [
			{
			className: "col-xs-8",
			key: "name",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true,
				minlength: 2, maxlength: 35,
				//focus: true, --this causes issues when cancelling form
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
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.PLACEHOLDER" | translate',
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
						var success = false;
						return restDomain.findByName(encodeURIComponent($viewValue)).then(function(domain) {
							if (angular.isUndefined(domain) || (domain._status == 404) || (domain.length === 0)) {
								success = true;
							}
						}).catch(function() {
							scope.options.validation.show = true;
							errors.catch("UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.UNIQUE", false);
						}).finally(function () {
							scope.options.templateOptions.loading = false;
							if (success) {
								return $q.resolve("No such domain");
							} else {
								return $q.reject("The domain already exists");
							}
						});
					},
					message: '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.NAME.UNIQUE" | translate'
				}
			}
		},{
			"className" : "col-xs-12",
			"key" : "displayName",
			"type" : "input",
			"templateOptions" : {
				"label" : "Display Name",
				"description" : "",
				"placeholder" : 'Eg. The Famous Brand',
				"required" : true,
				"options" : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.DESCRIPTION" | translate'
			}
		},{
			"className" : "col-xs-12",
			"key" : "description",
			"type" : "input",
			"templateOptions" : {
				"label" : "", "description" : "", "placeholder" : "",
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},{
			"className" : "col-xs-8",
			"type" : "ui-select-single",
			"key" : "defaultCountry",
			"templateOptions" : {
				"label" : "Country",
				"required" : false,
				"optionsAttr": 'bs-options',
				"description" : "Default country for domain.",
				"valueProp" : 'iso3',
				"labelProp" : 'name',
				"optionsAttr": 'ui-options', "ngOptions": 'ui-options',
				"placeholder" : 'Select default country for domain',
				"options" : []
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
		}
		,{
			className: "col-xs-12",
			key: "supportUrl",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, disabled: false
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
			key: "url",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, disabled: false
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
			className: "col-xs-12",
			key: "supportEmail",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, disabled: false
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
			key: "currency",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 3, maxlength: 3, disabled: false
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.CURRENCY.DESCRIPTION" | translate'
			}
		},
			// {
			// 	className: "col-xs-12",
			// 	key: "timeout",
			// 	type: "input",
			// 	optionsTypes: ['editable'],
			// 	templateOptions: {
			// 		label: "", description: "", placeholder: "",
			// 		required: false, minlength: 3, maxlength: 8, disabled: false,
			// 	},
			// 	expressionProperties: {
			// 		'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.TIMEOUT.NAME" | translate',
			// 		'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.TIMEOUT.NAME" | translate',
			// 		'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.TIMEOUT.DESCRIPTION" | translate'
			// 	}
			// }
			{
			className: "col-xs-12",
			key: "currencySymbol",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 1, maxlength: 3, disabled: false
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
				required: true, minlength: 5, maxlength: 20, disabled: false
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.LOCALE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.LOCALE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.LOCALE.DESCRIPTION" | translate'
			}
		},{
			"className" : "col-xs-8",
			"type" : "ui-select-single",
			"key" : "parentId",
			"templateOptions" : {
				"label" : "Parent Domain",
				"required" : true,
				"optionsAttr": 'bs-options',
				"description" : "The language that you would like to provide translations for.",
				"valueProp" : 'id',
				"labelProp" : 'displayName',
				"optionsAttr": 'ui-options', "ngOptions": 'ui-options',
				"placeholder" : 'Select Parent Domain',
				"options" : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PARENT.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PARENT.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PARENT.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.to.options = domains;
			}]
		},{
			"className" : "col-xs-12",
			"key" : "players",
			"type" : "checkbox2",
			"templateOptions" : {
				"label" : "", "description" : "", "placeholder" : ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYERS.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYERS.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.PLAYERS.DESCRIPTION" | translate'
			}
		}]
	}];
	
	controller.referenceId = 'domain-add-overlay';
	controller.onSubmit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}
		restDomain.add(controller.model).then(function() {
			notify.success("UI_NETWORK_ADMIN.DOMAIN.ADD.SUCCESS");
			$state.go("dashboard.domains.list");
		}).catch(
			errors.catch("UI_NETWORK_ADMIN.DOMAIN.ADD.FAIL", false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}
	controller.onCancel = function() {
		$state.go("dashboard.domains.list");
	}
}]);
