'use strict';

angular.module('lithium')
	.controller('EditProcessorController', ["dmp", "rest-cashier", "notify", "errors", "$uibModalInstance",
	function(dmp, cashierRest, notify, errors, $uibModalInstance) {
		var controller = this;
		
		controller.model = {};
		controller.options = {};
		
//		controller.model.method = domainMethod.method;
		controller.model = dmp;
		
		controller.fields = [{
			"className":"col-xs-12",
			"type":"input",
			"key":"domainMethod.method.name",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required": true,
				"disabled": true,
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.METHOD.NAME" | translate'
			}
		},{
			"className":"col-xs-12",
			"type":"input",
			"key":"processor.name",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required": true,
				"disabled": true,
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.PROCESSOR.NAME" | translate'
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
			"className":"col-xs-12",
			"type": "accessRule",
			"key": "accessRule",
			"templateOptions": {
				"required": false,
				"labelExtra": "(Evaluated for processor display to user)"
			}
		},{
			"className":"col-xs-12",
			"type": "accessRule",
			"key": "accessRuleOnTranInit",
			"templateOptions": {
				"required": false,
				"labelExtra": "(Evaluated prior to transaction attempt from user)"
			}
		},{
			className: "pull-left",
			type: "checkbox",
			key: "reserveFundsOnWithdrawal",
			templateOptions: {
				label: "Reserve funds when withdrawal is requested"
			},
			hideExpression: function($viewValue, $modelValue, scope) {
				return (dmp.domainMethod.deposit === true);
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.RESERVEFUNDSONWITHDRAWAL.NAME" | translate'
			}
		}
		];
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.onSubmit = function() {
			cashierRest.domainMethodProcessorUpdate(controller.model).then(function(dmp) {
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.SUCCESS");
				$uibModalInstance.close(dmp);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);
