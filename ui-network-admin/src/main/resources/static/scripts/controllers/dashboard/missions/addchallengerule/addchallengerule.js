'use strict';

angular.module('lithium').controller('MissionsAddChallengeRuleModal', ["domainName", "domainGamesList", "challenge", "mission", "rule", "$translate", "errors", "$scope", "notify", "$uibModalInstance", "PromotionRest",
function (domainName, domainGamesList, challenge, mission, rule, $translate, errors, $scope, notify, $uibModalInstance, PromotionRest) {
	var controller = this;
	
	controller.fields = [
		{
			className: "col-xs-12",
			key: "type", 
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [
					{ value: 'sports', label: 'Sports' },
					{ value: 'casino', label: 'Casino' },
					{ value: 'xp', label: 'XP' },
					{ value: 'user', label: 'User' },
					{ value: 'raf', label: 'Refer a Friend' },
					{ value: 'avatar', label: "Avatar" }
				],
				required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.TYPE" | translate'
			}
		}, {
			className: "col-xs-12",
			key: "action", 
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [],
				required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.ACTION" | translate'
			}
		}, {
			className : 'col-xs-12',
			key : "identifier",
			type : "ui-select-single",
			templateOptions : {
				label : "Game",
				optionsAttr: 'bs-options',
				description : "",
				valueProp : 'value',
				labelProp : 'label',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder : '',
				options : []
			},
			hideExpression: function($viewValue, $modelValue, scope) {
				if ((controller.model.type === 'casino') && (controller.model.action !== 'bonus_hourly')) return false;
				return true;
			},
			controller: ['$scope', function($scope) {
				$scope.to.options = domainGamesList;
			}]
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.IDENTIFIER" | translate'
//			}
		}, {
			className : 'col-xs-12',
			key: "value",
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
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.VALUE" | translate'
			}
		}, {
			className: 'col-xs-12',
			key: "explanation",
			type: "examplewell",
			templateOptions: {
				label: "",
				explain: ""
			}, 
			hideExpression: function($viewValue, $modelValue, scope) {
				if (controller.model.type !== undefined && controller.model.type !== null && controller.model.type !== ''
					&& controller.model.action !== undefined && controller.model.action !== null && controller.model.action !== '') return false;
				return true;
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLANATION" | translate',
				'templateOptions.explain': function(viewValue, modelValue, $scope) {
					var gameName = "";
					if (domainGamesList !== undefined && $scope.model.identifier !== undefined) {
						for (var i = 0; i < domainGamesList.length; i++) {
							if (domainGamesList[i].value === $scope.model.identifier) {
								gameName = domainGamesList[i].label;
								break;
							}
						}
					}
					switch ($scope.model.type) {
						case 'casino':
							if ($scope.model.action === 'spin') {
								if ($scope.model.identifier !== undefined && $scope.model.identifier !== null && $scope.model.identifier !== '') {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SPIN.GAME", {val: $scope.model.value, gameName: gameName}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								} else {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SPIN.ANY", {val: $scope.model.value}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								}
							} else if ($scope.model.action === 'win') {
								if ($scope.model.identifier !== undefined && $scope.model.identifier !== null && $scope.model.identifier !== '') {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WIN.GAME", {val: $scope.model.value, gameName: gameName}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								} else {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WIN.ANY", {val: $scope.model.value}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								}
							} else if ($scope.model.action === 'play') {
								if ($scope.model.identifier !== undefined && $scope.model.identifier !== null && $scope.model.identifier !== '') {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.PLAY.GAME", {val: $scope.model.value, gameName: gameName}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								} else {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.PLAY.ANY", {val: $scope.model.value}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								}
							} else if ($scope.model.action === 'bonusround') {
								if ($scope.model.identifier !== undefined && $scope.model.identifier !== null && $scope.model.identifier !== '') {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BONUSROUND.GAME", {val: $scope.model.value, gameName: gameName}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								} else {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BONUSROUND.ANY", {val: $scope.model.value}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								}
							} else if ($scope.model.action === 'bonus_hourly') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BONUSHOURLY", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							} else if ($scope.model.action === 'seeit') {
								if ($scope.model.identifier !== undefined && $scope.model.identifier !== null && $scope.model.identifier !== '') {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SEEIT.GAME", {val: $scope.model.value, gameName: gameName}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								} else {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SEEIT.ANY", {val: $scope.model.value}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								}
							} else if ($scope.model.action === 'bet_accumulator') {
								if ($scope.model.identifier !== undefined
										&& $scope.model.identifier !== null
										&& $scope.model.identifier !== '') {
									$translate(
											"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER.GAME",
											{val: $scope.model.value, gameName: gameName}).then(
											function (translation) {
												$scope.options.templateOptions.explain = translation;
											});
								} else {
									$translate(
											"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER.ANY",
											{val: $scope.model.value}).then(function (translation) {
										$scope.options.templateOptions.explain = translation;
									});
								}
							} else if ($scope.model.action === 'bet_onceoff') {
								
								if ($scope.model.identifier !== undefined && $scope.model.identifier !== null && $scope.model.identifier !== '') {
									$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER_ONCE_OFF.GAME", {val: $scope.model.value, gameName: gameName}).then(function(translation) {
										$scope.options.templateOptions.explain = translation;
									});
								} else {
									// TODO: update translation here
									$translate(
										"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER_ONCE_OFF.ANY",
										{val: $scope.model.value}
									).then(
										function (translation) {
											$scope.options.templateOptions.explain = translation;
										}
									);
								}
								console.log($scope.options.templateOptions.explain);
							}
							break;
						case 'xp':
							if ($scope.model.action === 'level') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.XP.LEVEL", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							} else if ($scope.model.action === 'points') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.XP.POINTS", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							}
							break;
						case 'user':
							if ($scope.model.action === 'login') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.USER.LOGIN", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							}
							break;
						case 'raf':
							if ($scope.model.action === 'referral') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.RAF.REFERRAL", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							} else if (action === 'conversion') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.RAF.CONVERSION", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							}
							break;
						case 'avatar':
							if ($scope.model.action === 'update') {
								console.log('Avatar update');
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.AVATAR.UPDATE", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							}
							break;
						case 'sports':
							if($scope.model.action === 'win') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.WIN", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							}
							else if($scope.model.action === 'bet_onceoff') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.BET_TOTAL", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							}
							else if($scope.model.action === 'bet_accumulator') {
								$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.BET_ACCUMULATOR", {val: $scope.model.value}).then(function(translation) {
									$scope.options.templateOptions.explain = translation;
								});
							}
							
							break;	
						default: $scope.options.templateOptions.explain = ""; 
					}
				}
			}
		}
	];
	
	controller.setActions = function() {
		var actions = [];
		switch (controller.model.type) {
			case 'casino':
				actions = [
					{ value: "spin", label: "Spins" },
					{ value: "win", label: "Wins" },
					// { value: "play", label: "Play" },
					// { value: "bonusround", label: "Bonus Rounds" },
					// { value: "bonus_hourly", label: "Hourly Bonus" },
					// { value: "seeit", label: "See It" },
					{ value: "bet_accumulator", label: "Wager (Total)" },
					{ value: "bet_onceoff", label: "Wager (Once Off)" }
				];
				break;
			case 'sports':
				actions = [
					// { value: "spin", label: "Spins" },
					{ value: "win", label: "Wins" },
					// { value: "play", label: "Play" },
					// { value: "bonusround", label: "Bonus Rounds" },
					// { value: "bonus_hourly", label: "Hourly Bonus" },
					// { value: "seeit", label: "See It" },
					{ value: "bet_accumulator", label: "Bet (Total)" },
					{ value: "bet_onceoff", label: "Bet (Once Off)" }
				];
				break;
			case 'xp':
				actions = [
					{ value: "level", label: "Levels" },
					{ value: "points", label: "Points" }
				];
				break;
			case 'user':
				actions = [
					{ value: "login", label: "Logins"}
				]
				break;
			case 'raf':
				actions = [
					{ value: "referral", label: "Referrals" },
					{ value: "conversion", label: "Conversions" }
				];
				break;
			case 'avatar':
				actions = [
					{ value: "update", label: "Update"}
				];
			default:; 
		}
		controller.fields[1].templateOptions.options = actions;
	}
	
	if (rule !== null) {
		controller.model = rule;
		controller.setActions();
	} else {
		controller.model = { type: "", action: "", value: 1 };
	}
	
	$scope.$watch(function() { return controller.model }, function(newValue, oldValue) {
		if (newValue != oldValue) {
			if (newValue.type !== oldValue.type) {
				controller.model.action = "";
			}
			controller.setActions();
		}
	}, true);
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		if (mission !== null && challenge !== null && rule !== null) {
			PromotionRest.modifyChallengeRule(mission.id, challenge.id, rule.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.MISSIONS.CHALLENGERULE.MODIFY.SUCCESS");
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.CHALLENGERULE.MODIFY.ERROR");
				errors.catch("", false)(error)
			});
		} else if (mission !== null && challenge !== null) {
			PromotionRest.addChallengeRule(mission.id, challenge.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.MISSIONS.CHALLENGERULE.ADD.SUCCESS");
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.CHALLENGERULE.ADD.ERROR");
				errors.catch("", false)(error)
			});
		} else {
			$uibModalInstance.close(controller.model);
		}
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);