'use strict';

angular.module('lithium')
	.controller('ViewConversion', ["$uibModalInstance", "$userService", "notify", "errors", "LeaderboardRest", "$translate", "utilityFields", "conversion",
	function($uibModalInstance, $userService, notify, errors, leaderboardRest, $translate, utilityFields, conversion) {
		var controller = this;
		
		controller.model = {};
		controller.model = conversion;
		controller.options = {};
		
		
		console.log(conversion);
		
		controller.fields = [{
			key: "typeDisplay",
			className: "col-xs-12",
			type: "readonly-input",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				readOnly: true,
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.TYPE.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.TYPE.'+conversion.type+'" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.TYPE.DESC" | translate'
			}
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
					$translate("UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.EXPLAIN."+conversion.type, {
						conversion: ($scope.model.conversion)?($scope.model.conversion):0,
						score: ($scope.model.conversion)?Math.floor($scope.model.conversion*100):0
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
			leaderboardRest.editConversion(controller.model.id, controller.model.conversion).then(function(c) {
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