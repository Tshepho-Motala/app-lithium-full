'use strict';

angular.module('lithium')
.controller('BonusAddController', ["domains", "types", "rest-casino", 'notify', "$scope", "$state", "$translate", 
function (domains, types, casinoRest, notify, $scope, $state, $translate) {
	var controller = this;
	
	controller.options = {formState: {}};
	controller.model = {};
	controller.triggerTypes = [];
	
//	var triggerTranslations = [
//		'GLOBAL.BONUS.TYPE.TRIGGER.0',
//		'GLOBAL.BONUS.TYPE.TRIGGER.1',
//		'GLOBAL.BONUS.TYPE.TRIGGER.2',
//		'GLOBAL.BONUS.TYPE.TRIGGER.3',
//		'GLOBAL.BONUS.TYPE.TRIGGER.4',
//		'GLOBAL.BONUS.TYPE.TRIGGER.5',
//		'GLOBAL.BONUS.TYPE.TRIGGER.6'
//	];
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.0').then(function (translations) {
		controller.triggerTypes.push({id:0, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.1').then(function (translations) {
		controller.triggerTypes.push({id:1, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.2').then(function (translations) {
		controller.triggerTypes.push({id:2, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.3').then(function (translations) {
		controller.triggerTypes.push({id:3, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.4').then(function (translations) {
		controller.triggerTypes.push({id:4, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.5').then(function (translations) {
		controller.triggerTypes.push({id:5, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.6').then(function (translations) {
		controller.triggerTypes.push({id:6, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.7').then(function (translations) {
		controller.triggerTypes.push({id:7, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.8').then(function (translations) {
		controller.triggerTypes.push({id:8, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.9').then(function (translations) {
		controller.triggerTypes.push({id:9, name:translations});
	});
	$translate('GLOBAL.BONUS.TYPE.TRIGGER.10').then(function (translations) {
		controller.triggerTypes.push({id:10, name:translations});
	});
	
	controller.fields = [{
		key : "domainName",
		type : "ui-select-single",
		templateOptions : {
			label : "",
			required : true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'name',
			labelProp : 'displayName',
			optionsAttr: 'ui-options', "ngOptions": 'ui-options',
			placeholder : 'Select Parent Domain',
			options : []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.DOMAIN.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.DOMAIN.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.DOMAIN.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.to.options = domains;
		}]
	},{
		key: "bonusType",
		type: "uib-btn-radio",
		templateOptions : {
			label : "Type",
			required : true,
			btnclass: 'default',
			showicons: true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'id',
			labelProp : 'name',
			optionsAttr: 'ui-options', "ngOptions": 'ui-options',
			placeholder : '',
			options : []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.TYPE.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.TYPE.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.TYPE.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
//			controller.model.bonusType = 0;
			$scope.to.options = types;
		}]
	},{
		key: "bonusTriggerType",
		type: "uib-btn-radio",
		templateOptions : {
			label : "Trigger Type",
			required : true,
			btnclass: 'default',
			showicons: true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'id',
			labelProp : 'name',
			optionsAttr: 'ui-options', "ngOptions": 'ui-options',
			placeholder : '',
			options : []
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if (controller.model.bonusType === 2) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.TRIGGERTYPE.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.TRIGGERTYPE.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.TRIGGERTYPE.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			console.log(controller.triggerTypes);
			$scope.to.options = controller.triggerTypes;
		}]
	},{
		key : "triggerTypeAny",
		type : "checkbox2",
		templateOptions : {
			label : "",
			required : true,
			description : "",
			fontWeight: 'bold'
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if ((controller.model.bonusType === 2) && (controller.model.bonusTriggerType === 1)) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.TRIGGER.ANY" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.TRIGGER.ANYEXPLAIN" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.model.triggerTypeAny = true;
		}]
	},{
		key: "triggerAmount",
		type: "ui-number-mask",
		optionsTypes: ['editable'],
		templateOptions : {
			label: "",
			required: true,
			decimals: 0,
			hidesep: true,
			neg: false,
			min: '1',
			max: ''
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if (((controller.model.bonusTriggerType === 1) && (!controller.model.triggerTypeAny)) || (controller.model.bonusTriggerType === 2)) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.TRIGGERAMOUNT.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.TRIGGERAMOUNT.DESCRIPTION" | translate'
		}
	},{
		key: "triggerAmount",
		type: "ui-number-mask",
		optionsTypes: ['editable'],
		templateOptions : {
			label: "",
			required: true,
			decimals: 0,
			hidesep: true,
			neg: false,
			min: 2,
			max: ''
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if (controller.model.bonusTriggerType === 6) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.CONSECUTIVEAMOUNT.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.CONSECUTIVEAMOUNT.DESCRIPTION" | translate'
		}
	},{
		key: "triggerAmount",
		type: "ui-number-mask",
		optionsTypes: ['editable'],
		templateOptions : {
			label: "",
			required: true,
			decimals: 0,
			hidesep: true,
			neg: false,
			min: 2,
			max: ''
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if (controller.model.bonusTriggerType === 9) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.HOURLY.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.HOURLY.DESCRIPTION" | translate'
		}
	},{
		key: "triggerGranularity",
		type: "granularityfor",
		optionsTypes: ['editable'],
		templateOptions : {
			label: "",
			required: false,
			showicons: true
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if (((controller.model.bonusTriggerType === 1) && (!controller.model.triggerTypeAny)) || (controller.model.bonusTriggerType === 2)) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.TRIGGERGRANULARITY.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.TRIGGERGRANULARITY.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.model.triggerGranularity = 3;
		}]
	},{
		key: "triggerGranularity",
		type: "granularities",
		optionsTypes: ['editable'],
		templateOptions : {
			label: "",
			required: false,
			showicons: true,
			include: [3]
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if (controller.model.bonusTriggerType === 6) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.TRIGGERGRANULARITY.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.TRIGGERGRANULARITY.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.model.triggerGranularity = 3;
		}]
	},{
		key: "triggerExample",
		type: "examplewell",
		templateOptions: {
			label: "",
			explain: ""
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			if (((controller.model.bonusTriggerType === 1) && (!controller.model.triggerTypeAny)) || (controller.model.bonusTriggerType === 2)) return false;
			return true;
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EXAMPLE.LABEL" | translate',
			'templateOptions.explain': function(viewValue, modelValue, $scope) {
				if ((controller.model.bonusTriggerType === 1) || (controller.model.bonusTriggerType === 2)) {
					$translate("UI_NETWORK_ADMIN.BONUS.TRIGGER.EXPLAIN."+controller.model.triggerGranularity, {
						type: (controller.triggerTypes[controller.model.bonusTriggerType].name).toLowerCase(),
						amount: controller.model.triggerAmount
					}).then(function success(translate) {
						$scope.options.templateOptions.explain = translate;
					});
				} else {
					$scope.options.templateOptions.explain = '';
				}
			}
		}
	},{
		key: "bonusCode",
		type: "input",
		templateOptions: {
			label: "Code", description: "", placeholder: "", required: false,
			minlength: 2, maxlength: 35
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.CODE.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.CODE.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.CODE.DESCRIPTION" | translate'
		}
	},{
		key: "bonusName",
		type: "input",
		templateOptions: {
			label: "Name", description: "", placeholder: "", required: true,
			minlength: 2, maxlength: 35
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.NAME.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.NAME.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.NAME.DESCRIPTION" | translate'
		}
	}];
	
	controller.proceed = function(bonusId) {
//		console.log(bonusId);
		createBonusAndRedirect(bonusId);
	}
	
	function createBonusAndRedirect(bonusId) {
		if (bonusId === -1) {
//			console.log(controller.model);
			casinoRest.createNewBonus(controller.model).then(function(response) {
//				console.log(response.plain());
				if (angular.isDefined(response.id)) {
					$scope.newBonus = response.plain();
					$state.go("dashboard.bonuses.edit", {bonusId:response.id, bonusRevisionId:response.edit.id});
				}
			}).catch(function() {
				controller.submitCalled = undefined;
			}).finally(function () {

			});
		} else {
			casinoRest.copyBonusRevision(bonusId).then(function(response) {
//				console.log(response.plain());
				if (angular.isDefined(response.id)) {
					$scope.newBonus = response.plain();
					$state.go("dashboard.bonuses.edit", {bonusId:response.id, bonusRevisionId:response.edit.id});
				}
			}).catch(function() {
				controller.submitCalled = undefined;
			}).finally(function () {

			});
		}
	}
	
	controller.save = function() {
		controller.submitCalled = true;
//		console.log(controller.form.$invalid);
//		console.log(controller.model);
		if (!controller.form.$invalid) {
			if (angular.isUndefined(controller.model.bonusCode)) {
				controller.model.bonusCode = '';
//				console.log(controller.model);
			}
			
			casinoRest.findLastBonusRevision(controller.model.bonusCode, controller.model.domainName, controller.model.bonusType).then(function(response) {
//				console.log(response.plain());
				if (angular.isDefined(response.id)) {
					$scope.existingBonus = response.plain();
				}
//				console.log($scope.existingBonus);
				if (angular.isUndefined($scope.existingBonus)) {
					createBonusAndRedirect(-1);
				}
			}).catch(function() {
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
				controller.submitCalled = undefined;
			}).finally(function () {
			
			});
		}
	}
}]);