'use strict';

angular.module('lithium')
	.controller('LocalCurrencyAdd', ["$uibModalInstance", "$userService", "notify", "errors", "ProductRest", "userFields", "utilityFields", "product",
	function($uibModalInstance, $userService, notify, errors, productRest, userFields, utilityFields, product) {
		var controller = this;
		
		controller.model = {};
		controller.model.product = product;
		controller.options = {};
		
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
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			console.log(controller.model);
			productRest.addLocalCurrency(controller.model).then(function(c) {
				notify.success("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.ADD.SUCCESS");
				$uibModalInstance.close(c);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);