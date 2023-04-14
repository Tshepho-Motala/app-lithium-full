'use strict'

angular.module('lithium')
.controller('RAFSettingsController',
["config", "$scope", "$stateParams", "$uibModal", "notify", "errors", "$filter", "rest-casino", "NotificationRest", "RAFRest", "bsLoadingOverlayService",
function(config, $scope, $stateParams, $uibModal, notify, errors, $filter, restCasino, notificationRest, restRAF, bsLoadingOverlayService) {
	var controller = this;
	controller.referenceId = "RAFSettingsController_"+(Math.random()*1000);
	controller.model = config;
	
	controller.submitCalled = false;
	controller.options = {removeChromeAutoComplete:true};
	
	controller.fields = [
		{
			key: "conversionType",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'id',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [
					{ id: 0, name: 'Deposit'},
					{ id: 1, name: 'XP Level'}
				]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.CONVERSIONTYPE.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.CONVERSIONTYPE.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.CONVERSIONTYPE.DESC" | translate'
			}
		},
		{
			key: "conversionXpLevel",
			className: "col-xs-12 form-group",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.CONVERSIONXPLEVEL.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.CONVERSIONXPLEVEL.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.CONVERSIONXPLEVEL.DESC" | translate'
			},
			hideExpression: function ($viewValue, $modelValue, scope) {
				if (controller.model.conversionType === 1) return false;
				return true;
			}
		},
		{
			className:"col-xs-12",
			type:"ui-select-single",
			key:"referrerBonusCode",
			templateOptions:{
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'bonusCode',
				labelProp: 'bonusName',
				optionsAttr: 'ui-options',
				ngOptions: 'ui-options',
				options: []
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFERRERBONUSCODE.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFERRERBONUSCODE.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFERRERBONUSCODE.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				restCasino.findPublicBonusListV2(config.domain.name, 2, 3).then(function(response) {
					$scope.to.options = response;
				});
			}]
		},
		{
			key: "refereeBonusCode",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'bonusCode',
				labelProp: 'bonusName',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFEREEBONUSCODE.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFEREEBONUSCODE.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFEREEBONUSCODE.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				restCasino.findPublicBonusListV2(config.domain.name, 2, 3).then(function(response) {
					$scope.to.options = response;
				});
			}]
		},
		{
			key: "referralNotification",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'name',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFERRALNOTIFICATION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFERRALNOTIFICATION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.REFERRALNOTIFICATION.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				notificationRest.findByDomainName(config.domain.name).then(function(notifications) {
					$scope.to.options = notifications.plain();
				}).catch(function(error) {
					notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
					errors.catch("", false)(error)
				}).finally(function() {
				});
			}]
		},
		{
			key: "autoConvertPlayer",
			className: "col-xs-12 form-group",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'name',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [
					{ name: 'ENABLED'},
					{ name: 'DISABLED'}
				]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.AUTO_CONVERT_PLAYER.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.AUTO_CONVERT_PLAYER.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RAF.CONFIG.FIELDS.AUTO_CONVERT_PLAYER.DESC" | translate'
			},
		},
	];
	
	controller.onSubmit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		controller.submitCalled = true;
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		restRAF.modifyConfiguration(config.domain.name, controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.RAF.CONFIG.REFERRALBONUSES.MODIFY.SUCCESS");
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.RAF.CONFIG.REFERRALBONUSES.MODIFY.ERROR");
			errors.catch("", false)(error)
		}).finally(function() {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}
}]);