'use strict';

angular.module('lithium')
	.controller('LocalCurrencyView', ["$uibModalInstance", "$userService", "notify", "errors", "ProductRest", "userFields", "utilityFields", "localCurrency",
	function($uibModalInstance, $userService, notify, errors, productRest, userFields, utilityFields, localCurrency) {
		var controller = this;
		
		controller.model = {};
		controller.model = localCurrency;
		controller.options = {};
		
		console.log(localCurrency);
		
		controller.fields = [
			userFields.countryTypeAhead('', 'countryCode', false, true),
			utilityFields.currencyTypeAhead("col-xs-12", true, false),
			{
				"className": "col-xs-12",
				"key":"currencyAmount",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 2,
					hidesep: true,
					neg: false
				},
				"expressionProperties": {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.LOCALCURRENCY.TITLE" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.LOCALCURRENCY.PLACE" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.LOCALCURRENCY.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = controller.availableDomains;
				}]
			}
		];
		
		controller.save = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			console.log(controller.model);
			productRest.editLocalCurrency(controller.model).then(function(c) {
				notify.success("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.EDIT.SUCCESS");
				$uibModalInstance.close(c);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.remove = function() {
			productRest.removeLocalCurrency(controller.model.id).then(function(c) {
				notify.success("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.REMOVE.SUCCESS");
				$uibModalInstance.close(c);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.REMOVE.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);