'use strict';

angular.module('lithium')
	.controller('addMethod', ["domainName", "type", "$uibModalInstance", "rest-cashier", "notify", "errors",
	function(domainName, type, $uibModalInstance, cashierRest, notify, errors) {
		var controller = this;
		
		controller.model = {};
		controller.options = {};
		
		controller.fields = [{
			"className":"col-xs-12",
			"type":"input",
			"key":"name",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"placeholder":"name",
				"description":"name",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.NAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.NAME.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.NAME.DESCRIPTION" | translate'
			}
		},{
			"className":"col-xs-12 form-group",
			"type":"ui-select-single",
			"key":"method",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required":true,
				"optionsAttr": "bs-options",
				"valueProp": "id",
				"labelProp": "name",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				cashierRest.methods().then(function(response) {
					$scope.options.templateOptions.options = response;
					return response;
				});
			}]
		}];
		
		controller.onSubmit = function() {
			controller.model.enabled = true;
			controller.model.priority = 999;
			cashierRest.domainMethodAdd(domainName, controller.model, type).then(function(dm) {
				notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.ADD.SUCCESS");
				$uibModalInstance.close(dm);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.CASHIER.METHODS.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);