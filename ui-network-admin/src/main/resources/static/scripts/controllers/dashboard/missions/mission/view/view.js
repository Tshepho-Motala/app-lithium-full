'use strict'

angular.module('lithium').controller('MissionViewController', ['mission', 'missionRevision', 'gamesList', '$translate', 'notify', 'Lightbox',
	function(mission, missionRevision, gamesList, $translate, notify, Lightbox) {
		var controller = this;
		
		controller.model = mission.plain();
		controller.missionRevision = missionRevision;
		
		controller.setMissionChallengeIconUrls = function() {
			for (var i = 0; controller.missionRevision.challenges && i < controller.missionRevision.challenges.length; i++) {
				var iconUrl = 'services/service-promo/backoffice/promotion/'+mission.id+'/challenge/'+controller.missionRevision.challenges[i].id+'/getIcon';
				controller.missionRevision.challenges[i].iconUrl = iconUrl;
			}
		}
		
		controller.setMissionChallengeIconUrls();
		
		controller.collapseChallenge = function(challenge) {
			challenge.collapsed = !challenge.collapsed;
		}
		
		controller.openLightBox = function(src) {
			var image = [{
				'url': src
			}]
			Lightbox.openModal(image, 0);
		}
		
		controller.getGameName = function(gameGuid) {
			var gameName = "";
			if (gamesList !== undefined && gameGuid !== undefined) {
				for (var i = 0; i < gamesList.length; i++) {
					if (gamesList[i].guid == gameGuid) {
						gameName = gamesList[i].name + ' - ' + gamesList[i].labels.os.value;
						break;
					}
				}
			}
			return gameName;
		}

		switch (controller.missionRevision.maxRedeemableGranularity) {
			case 1:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_YEAR';
				break;
			case 2:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_MONTH';
				break;
			case 3:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_DAY';
				break;
			case 4:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_WEEK';
				break;
			case 5:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_TOTAL';
				break;
			default:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_TOTAL';
				break;
		}
		
		controller.getChallengeRuleExplanation = function(type, action, identifier, val) {
			
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
					} else if (action === 'bonus_hourly') {
						return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.CASINO.BONUSHOURLY';
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
						return 'UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGERULES.RULE.EXPLAIN.AVATAR.UPDATE'
					}
					break;
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

		controller.getDurationExplanation = () => {

			if(angular.isUndefinedOrNull(controller.missionRevision.maxRedeemableGranularity) || angular.isUndefinedOrNull(controller.missionRevision.duration)) {
				return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.NOT_SET");
			}

			return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DURATION.EXPLAIN." +controller.missionRevision.maxRedeemableGranularity, {
				val: controller.missionRevision.duration
			})
		}

		controller.getOffsetExplanation = () => {
			return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.GRANULARITY_OFFSET.EXPLAIN")
		}

		controller.getBreakExplanation = () => {
			return $translate.instant(`UI_NETWORK_ADMIN.MISSIONS.FIELDS.GRANULARITY_BREAK.EXPLAIN.${controller.missionRevision.granularityBreak}`.toUpperCase())
		}
	}
]);
