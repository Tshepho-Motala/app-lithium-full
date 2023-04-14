'use strict';

angular.module('lithium')
	.controller('EditMethodController', ["errors", "rest-cashier", "notify", "domainMethod", "$uibModalInstance", 
	function(errors, cashierRest, notify, domainMethod, $uibModalInstance) {
		var controller = this;
		
		controller.model = domainMethod;
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.onSubmit = function() {
			if (controller.form.$invalid === false) {
				var saveModel = cashierRest.copy(controller.model);
				cashierRest.domainMethodUpdate(saveModel).then(function(dm) {
					notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.SUCCESS");
					$uibModalInstance.close(dm);
				}).catch(function(error) {
					notify.error("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ERROR");
					errors.catch("", false)(error)
				});
			}
		}
		
		controller.fields = [{
			"className":"col-xs-12 form-group",
			"type":"input",
			"key":"method.name",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required":true,
				"disabled": true,
				"optionsAttr": "bs-options",
				"valueProp": "id",
				"labelProp": "name",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.DESCRIPTION" | translate'
			}
		},{
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
		},
	 {
			"className":"col-xs-12",
			"type":"input",
			"key":"description",
			"templateOptions":{
				"type":"",
				"label":"",
 				"placeholder":"method description",
				"description":"description",
				"options":[]
			},
		 	"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PROCESOR_DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PROCESOR_DESCRIPTION.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.METHOD.PROCESOR_DESCRIPTION.DESCRIPTION" | translate'
			}  
		}, 
		{
			"className":"col-xs-12",
			"type":"image-upload",
			"key":"image",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":false,
				"description":"",
				"maxsize": 500,  //Maximum file size in kilobytes (KB)
				"minsize": 1,
				"accept": "image/*",
				"preview": true
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.IMAGE.NAME" | translate',
				'templateOptions.description': '' //'"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.IMAGE.DESCRIPTION" | translate'
			}
		},{
			"className":"col-xs-12",
			"type": "accessRule",
			"key": "accessRule",
			"templateOptions": {
				"required": false,
				"labelExtra": "(Evaluated for method display to user)"
			}
		},{
			"className":"col-xs-12",
			"type": "accessRule",
			"key": "accessRuleOnTranInit",
			"templateOptions": {
				"required": false,
				"labelExtra": "(Evaluated prior to transaction attempt from user)"
			}
		}
		];
	}
]);
