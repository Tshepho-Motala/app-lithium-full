'use strict';

angular.module('lithium')
	.controller('CashierDomainProfileEditController', ["domainName", "errors", "profile", "rest-cashier", "notify", "$scope",
	function(domainName, errors, profile, cashierRest, notify, $scope) {
		var controller = this;
		$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.HEADER.PROFILEEDIT");
		
		controller.model = profile;
		controller.options = {};
		
		controller.fields = [{
			"className" : "col-xs-12",
			"type" : "input",
			"key" : "code",
			"templateOptions" : {
				"type" : "",
				"label" : "",
				"required" : true,
				"placeholder" : "",
				"description" : "",
				"options" : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.DESCRIPTION" | translate'
			}
		},{
			"className" : "col-xs-12",
			"type" : "input",
			"key" : "name",
			"templateOptions" : {
				"type" : "",
				"label" : "",
				"required" : true,
				"placeholder" : "",
				"description" : "",
				"options" : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.DESCRIPTION" | translate'
			}
		},{
			"className" : "col-xs-12",
			"type" : "input",
			"key" : "description",
			"templateOptions" : {
				"type" : "",
				"label" : "",
				"required" : false,
				"placeholder" : "",
				"description" : "",
				"options" : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.DESCRIPTION" | translate'
			}
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				notify.warning("UI_NETWORK_ADMIN.CASHIER.PROFILES.EDIT.ERROR");
			} else {
				var saveModel = cashierRest.copy(controller.model);
				cashierRest.profileSave(saveModel).then(function(p) {
					controller.model = p;
					notify.success("UI_NETWORK_ADMIN.CASHIER.PROFILES.EDIT.SUCCESS");
				}).catch(function(error) {
					notify.warning("UI_NETWORK_ADMIN.CASHIER.PROFILES.EDIT.ERROR");
					errors.catch("", false)(error)
				});
			}
		}
	}
]);