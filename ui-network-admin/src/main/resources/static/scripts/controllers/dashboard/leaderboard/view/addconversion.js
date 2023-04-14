'use strict';

angular.module('lithium')
	.controller('AddConversion', ["$uibModalInstance", "$userService", "notify", "errors", "LeaderboardRest", "$translate", "utilityFields", "leaderboard",
	function($uibModalInstance, $userService, notify, errors, leaderboardRest, $translate, utilityFields, leaderboard) {
		var controller = this;
		
		controller.model = {};
		controller.model.leaderboard = leaderboard;
		controller.options = {};
		
		console.log(leaderboard);
		
		controller.types = [];
		
		var triggerTranslations = [
			'UI_NETWORK_ADMIN.LEADERBOARD.TYPE.1',
			'UI_NETWORK_ADMIN.LEADERBOARD.TYPE.2',
			'UI_NETWORK_ADMIN.LEADERBOARD.TYPE.3'
		];
		
		$translate(triggerTranslations).then(function(translations) {
			angular.forEach(translations, function(v,k) {
				this.push({id:k.slice(-1), name:v});
			}, controller.types);
		});
		
		controller.fields = [{
			key: "type", 
			className: "col-xs-12",
			type: "ui-select-single",
			"optionsTypes": ['editable'],
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				dataAllowClear: false,
				disabled: true,
				optionsAttr: 'bs-options',
				valueProp: 'id',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.TYPE.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.TYPE.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.TYPE.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.to.options = controller.types;
			}]
		},{
			"className": "col-xs-12",
			"key":"conversion",
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.CONVERSION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.CONVERSION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.CONVERSION.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		},{
			key: "conversionExample",
			type: "examplewell",
			templateOptions: {
				label: "",
				explain: ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.EXAMPLE" | translate',
				'templateOptions.explain': function(viewValue, modelValue, $scope) {
					console.log($scope.model, ($scope.model.conversion*10000));
					$translate("UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.EXPLAIN", {
						conversion: ($scope.model.conversion)?($scope.model.conversion):0,
						score: ($scope.model.conversion)?($scope.model.conversion*10000):0
					}).then(function success(translate) {
						$scope.options.templateOptions.explain = translate;
					});
				}
			}
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			console.log(controller.model);
			leaderboardRest.addConversion(controller.model.leaderboard.id, controller.model.conversion, controller.model.type).then(function(c) {
				notify.success("UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.ADD.SUCCESS");
				$uibModalInstance.close(c);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);