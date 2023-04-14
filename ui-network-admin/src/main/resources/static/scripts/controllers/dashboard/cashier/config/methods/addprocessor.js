'use strict';

angular.module('lithium')
	.controller('addProcessor', ["type", "domainName", "domainMethod", "$uibModalInstance", "rest-cashier", "notify", "errors",
	function(type, domainName, domainMethod, $uibModalInstance, cashierRest, notify, errors) {
		var controller = this;

		controller.model = {};
		controller.options = {};
		
		controller.model.domainMethod = domainMethod;
		
		controller.fields = [{
			"className":"col-xs-12 form-group",
			"type":"input",
			"key":"domainMethod.method.name",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required": false,
				"disabled": true,
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.METHOD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.METHOD.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.METHOD.DESCRIPTION" | translate'
			}
		},{
			"className":"col-xs-12 form-group",
			"type":"input",
			"key":"description",
			"templateOptions": {
				"label": "",
				"placeholder": "",
				"description": "",
				"required": false,
				"disabled": false,
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},{
			"className":"col-xs-12 form-group",
			"type":"ui-select-single",
			"key":"processor.id",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required": true,
				"optionsAttr": "bs-options",
				"valueProp": "id",
				"labelProp": "name",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.DESCRIPTION" | translate'
			},
			"controller": ['$scope', function($scope) {
				cashierRest.processors(domainMethod.method.id, type).then(function(response) {
					$scope.options.templateOptions.options = response;
					return response;
				});
			}]
		}, {
			className: "pull-left",
			type: "checkbox",
			key: "reserveFundsOnWithdrawal",
			templateOptions: {
				label: "Reserve funds when withdrawal is requested"
			},
			hideExpression: function($viewValue, $modelValue, scope) {
				return (type !== 'withdraw');
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.RESERVEFUNDSONWITHDRAWAL.NAME" | translate'
			}
		}];
		
		controller.onSubmit = function() {
			controller.model.enabled = true;
			controller.model.weight = 0;
			cashierRest.domainMethodProcessorAdd(controller.model).then(function(dmp) {
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.ADD.SUCCESS");
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.ADD.SUCCESSSHOWPROP", {ttl:15000});
				$uibModalInstance.close(dmp);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);