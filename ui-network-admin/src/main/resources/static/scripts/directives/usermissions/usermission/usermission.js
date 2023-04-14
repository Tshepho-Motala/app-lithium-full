'use strict';

angular.module('lithium').directive('usermission', function() {
	return {
		templateUrl:'scripts/directives/usermissions/usermission/usermission.html',
		scope: {
			data: "=",
			gamesList: "="
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal','$translate', '$scope', 'notify', 'errors', 'Lightbox',
		function($q, $uibModal,$translate, $scope, notify, errors, Lightbox) {
			
			$scope.setup = function() {
				for (var i = 0; i < $scope.data.challenges.length; i++) {
					var iconUrl = 'services/service-promo/backoffice/promotion/'+$scope.data.missionRevision.id+'/challenge/'+$scope.data.challenges[i].id+'/getIcon';
					$scope.data.challenges[i].iconUrl = iconUrl;
				}
			}
			
			$scope.setup();
			
			$scope.collapseChallenge = function(challenge) {
				challenge.collapsed = !challenge.collapsed;
			}
			
			$scope.openLightBox = function(src) {
				var image = [{
					'url': src
				}]
				Lightbox.openModal(image, 0);
			}
			
			$scope.getGameName = function(gameGuid) {
				var gameName = "";
				if ($scope.gamesList !== undefined && gameGuid !== undefined) {
					for (var i = 0; i < $scope.gamesList.length; i++) {
						if ($scope.gamesList[i].guid == gameGuid) {
							gameName = $scope.gamesList[i].name + ' - ' + $scope.gamesList[i].labels.os.value;
							break;
						}
					}
				}
				return gameName;
			}
			
			$scope.getChallengeRuleExplanation = function(type, action, identifier, val) {
				switch (type) {
					case 'casino':
						if (action === 'spin') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SPIN.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SPIN.ANY';
							}
						} else if (action === 'win') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WIN.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WIN.ANY';
							}
						} else if (action === 'play') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.PLAY.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.PLAY.ANY';
							}
						} else if (action === 'bonusround') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BONUSROUND.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BONUSROUND.ANY';
							}
						} else if (action === 'seeit') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SEEIT.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SEEIT.ANY';
							}
						} else if (action === 'bet') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BET.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BET.ANY';
							}
						}
						else if (action === 'bet_onceoff') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER_ONCE_OFF.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER_ONCE_OFF.ANY';
							}
						}
						else if (action === 'bet_accumulator') {
							if (typeof identifier === 'string' && identifier !== '') {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER.GAME';
							} else {
								return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER.ANY';
							}
						}
						break;
					case 'xp':
						if (action === 'level') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.XP.LEVEL';
						} else if (action === 'points') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.XP.POINTS';
						}
						break;
					case 'user':
						if (action === 'login') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.USER.LOGIN';
						}
						break;
					case 'raf':
						if (action === 'referral') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.RAF.REFERRAL';
						} else if (action === 'conversion') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.RAF.CONVERSION';
						}
						break;
					case 'avatar':
						if (action === 'update') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.AVATAR.UPDATE';
						}
					case 'sports':
						if(action === 'win') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.WIN';
						}
						else if(action === 'bet_onceoff') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.BET_TOTAL';
						}
						else if(action === 'bet_accumulator') {
							return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.BET_ACCUMULATOR';
						}
						break;
					default: return ''; 
				}
			}

			$scope.getRuleProgressExplanation = (rule, challenge) => {
				let action = rule.rule.action;
				let type = rule.rule.type;

				if(angular.isUndefinedOrNull(rule.missionStat)) {
					return ''
				}

				let statValue = rule.missionStat.missionStatSummary[rule.missionStat.missionStatSummary.length - 1] || {value: 0}

				switch(type) {
					case 'casino':
						

						let completed = !angular.isUndefinedOrNull(rule.completed) ? "COMPLETE": 'INCOMPLETE'
						let game = (typeof rule.rule.identifier === 'string' && rule.rule.identifier !== '') ? 'GAME': 'ANY';
						

						if(action === 'bet_onceoff') {
							
							let key = `UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER_ONCE_OFF.${completed}_${game}`;

							return $translate.instant(key, {
								amount: statValue.value,
								required_amount: rule.rule.value,
								gameName: $scope.getGameName(rule.rule.identifier)
							})
						} else if(action === 'bet_accumulator') {
							let key = `UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WAGER.${completed}_${game}`;

							return $translate.instant(key, {
								amount: statValue.value,
								short_amount: (rule.rule.value - statValue.value),
								gameName: $scope.getGameName(rule.rule.identifier)
							})
						}
						else if(action === 'win') {
							let key = `UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.WIN.${completed}_${game}`;

							return $translate.instant(key, {
								amount: statValue.value,
								short_amount: (rule.rule.value - statValue.value),
								gameName: $scope.getGameName(rule.rule.identifier)
							})
						}
						else if(action === 'spin') {
							let key = `UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.SPIN.${completed}_${game}`;

							return $translate.instant(key, {
								spins: statValue.value,
								gameName: $scope.getGameName(rule.rule.identifier),
								short_spins: (rule.rule.value - statValue.value)
							})
						}

						return '';
					
					case 'sports': 

						if(angular.isUndefinedOrNull(rule.missionStat)) {
							return ''
						}

						let progress= !angular.isUndefinedOrNull(rule.completed) ? 'COMPLETE':'INCOMPLETE';
					
						if(action === 'bet_onceoff') {
							let key = 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.BET_TOTAL_' + progress;

							return $translate.instant(key, {
								amount: statValue.value,
								required_amount: rule.rule.value
							})
						} else if(action === 'bet_accumulator') {
							let key = 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.BET_ACCUMULATOR_' + progress;

							return $translate.instant(key, {
								amount: statValue.value,
								short_amount: (rule.rule.value - statValue.value)
							})
						}
						else if(action === 'win') {
							let key = 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.SPORTS.WIN_' + progress;

							return $translate.instant(key, {
								amount: statValue.value,
								short_amount: (rule.rule.value - statValue.value)
							})
						}
					
				}
			}
			
			
		}]
	}
});