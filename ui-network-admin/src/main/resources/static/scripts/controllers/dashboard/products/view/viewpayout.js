'use strict';

angular.module('lithium')
	.controller('PayoutView', ["$uibModalInstance", "$userService", "notify", "errors", "ProductRest", "rest-casino", "utilityFields", "payout",
	function($uibModalInstance, $userService, notify, errors, productRest, restCasino, utilityFields, payout) {
		var controller = this;
		
		controller.model = {};
		controller.model = payout;
		controller.options = {};
		
		console.log(payout);
		
		controller.fields = [{
			"className": "row v-reset-row ",
			"fieldGroup": [
				utilityFields.currencyTypeAhead("col-xs-4", false, false),
				{
					"className": "col-xs-8",
					"key":"currencyAmount",
					type: "ui-number-mask",
					optionsTypes: ['editable'],
					templateOptions : {
						label: "",
						required: false,
						decimals: 2,
						hidesep: true,
						neg: false
					},
					"expressionProperties": {
						'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.PAYOUT.AMOUNT.TITLE" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.PAYOUT.AMOUNT.PLACE" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.PAYOUT.AMOUNT.DESC" | translate'
					}
				}
			]
		},{
			className: "col-xs-12",
			template: "<div><p class='subtitle fancy'><span>or</span></p></div>"
		},{
			key: "bonusCode", 
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'bonusCode',
				labelProp: 'bonusName',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.BONUS.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.BONUS.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.BONUS.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				restCasino.findPublicBonusListV2(payout.product.domain.name, 2, 7).then(function(response) {
					$scope.to.options = response;
				});
			}]
		}];
		
		controller.save = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			console.log(controller.model);
			productRest.editPayout(controller.model).then(function(c) {
				notify.success("UI_NETWORK_ADMIN.CATALOG.PAYOUT.EDIT.SUCCESS");
				$uibModalInstance.close(c);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.PAYOUT.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.remove = function() {
			productRest.removePayout(controller.model.id).then(function(c) {
				notify.success("UI_NETWORK_ADMIN.CATALOG.PAYOUT.REMOVE.SUCCESS");
				$uibModalInstance.close(c);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.PAYOUT.REMOVE.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);