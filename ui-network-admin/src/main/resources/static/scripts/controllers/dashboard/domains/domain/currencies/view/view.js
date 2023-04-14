'use strict';

angular.module('lithium')
	.controller('DomainCurrencyViewController', ["domainCurrency", "domain", "$log", "$scope", "$translate", "$state", "notify", "rest-accounting-internal",
	function(domainCurrency, domain, $log, $scope, $translate, $state , notify, restAccountingInternal) {
		var controller = this;
		controller.model = domainCurrency;
		controller.model.code = domainCurrency.currency.code;
		controller.modelOriginal = angular.copy(domainCurrency);
		controller.options = { formState: { readOnly: true } };
		
		controller.fields = [
			{
				className: "col-xs-12",
				key: "code",
				type: "input",
				optionsTypes: [],
				templateOptions: {
					label: "Code", description: "", placeholder: "",
					required: true, minlength: 2, maxlength: 35, disabled: true
				},
				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.DESCRIPTION" | translate'
				}
			},
			{
				className: "col-xs-12",
				key: "name",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "Name", description: "", placeholder: "",
					required: true, minlength: 2, maxlength: 35, disabled: true
				},
				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.DESCRIPTION" | translate'
				}
			},
			{
				className: "col-xs-12",
				key: "symbol",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "Symbol", description: "", placeholder: "",
					required: true, minlength: 1, maxlength: 35, disabled: true
				},
				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.DESCRIPTION" | translate'
				}
			},
			{
				className: "col-xs-12",
				key: 'divisor',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Divisor',
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: '',
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//				}
			},
			{
				className: "col-xs-12",
				key: "description",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "Description", description: "", placeholder: "",
					required: false, minlength: 2, maxlength: 35, disabled: true
				},
				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.FIELDS.DISPLAYNAME.DESCRIPTION" | translate'
				}
			}
		];
		
		controller.onEdit = function() {
			controller.options.formState.readOnly = false;
		}
		
		controller.onCancel = function() {
			controller.onReset();
			controller.options.formState.readOnly = true;
		}
		controller.onReset = function() {
			controller.model = angular.copy(controller.modelOriginal);
			controller.model.code = domainCurrency.currency.code;
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			restAccountingInternal.saveDomainCurrency(domain.name, controller.model).then(function(response) {
				console.log(response.plain());
				notify.success("Saved Succesfully.");
				controller.model = response.plain();
				controller.model.code = response.currency.code;
				controller.modelOriginal = angular.copy(response.plain());
				controller.options.formState.readOnly = true;
			});
		}
		
		controller.setAsDefault = function(domainCurrency) {
			$translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.CONFIRM').then(function(response) {
				if (window.confirm(response)) {
					restAccountingInternal.setAsDefault(domain.name, controller.model.id).then(function(response) {
						if (response._status !== 0) {
							notify.error('Could not set as default');
						} else {
							notify.success('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.SUCCESS');
							$state.go("dashboard.domains.domain.currencies")
						}
					}
					).catch(function() {
						errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.ERROR', false);
					}).finally(function() {
					});
				}
			}).catch(function() {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.ERROR');
			});
		}
		
		controller.deleteDomainCurrency = function(domainCurrency) {
			$translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.CONFIRM').then(function(response) {
				if (window.confirm(response)) {
					restAccountingInternal.deleteDomainCurrency(domain.name, controller.model.id).then(function(response) {
						if (response._status !== 0) {
							notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR');
						} else {
							notify.success('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.SUCCESS');
							$state.go("dashboard.domains.domain.currencies")
						}
					}).catch(function() {
						errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR', false);
					}).finally(function() {
					});
				}
			}).catch(function() {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR');
			});
		}
	}
]);