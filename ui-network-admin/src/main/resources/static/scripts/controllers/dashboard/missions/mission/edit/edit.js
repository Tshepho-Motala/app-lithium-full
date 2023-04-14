'use strict'

angular.module('lithium').controller('MissionEditController', ['mission', 'gamesList', 'bonusList', '$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'Lightbox', 'PromotionRest', 'rest-casino', 'rest-games',
	function(mission, gamesList, bonusList, $translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, Lightbox, PromotionRest, casinoRest, gamesRest) {
		var controller = this;
		
		controller.model = mission;
		if (controller.model.edit.startDate === undefined || controller.model.edit.startDate === null) {
			var sdate = new Date();
			sdate.setUTCHours(0,0,0,0);
			controller.model.edit.startDate = sdate;
		}
		if (controller.model.edit.endDate === undefined || controller.model.edit.endDate === null) {
			var edate = new Date();
			edate.setUTCHours(23,59,59,59);
			controller.model.edit.endDate = edate;
		}

		
		controller.setMissionChallengeIconUrls = function() {
			for (var i = 0; i < controller.model.edit.challenges.length; i++) {
				var iconUrl = 'services/service-promo/backoffice/promotion/'+mission.id+'/challenge/'+controller.model.edit.challenges[i].id+'/getIcon';
				controller.model.edit.challenges[i].iconUrl = iconUrl;
			}
		}
		
		controller.setMissionChallengeIconUrls();
		
		controller.setDomainGamesList = function() {
			controller.domainGamesList = [];
			for (var i = 0; i < gamesList.length; i++) {
				var os = (gamesList[i].labels.os)?gamesList[i].labels.os.value:"";
				controller.domainGamesList.push({ label: gamesList[i].name + ' - ' + os, value: gamesList[i].guid });
			}
		}
		
		controller.setDomainGamesList();
		
		controller.fields = [
			{
				className : 'col-xs-12',
				key : "edit.domain.name",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : [],
					disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.to.options = $userService.domainsWithRole("MISSIONS_*");
				}]
			}, {
				className : 'col-xs-12',
				key: "edit.name",
				type: "input",
				templateOptions: {
					label: "",
					description: "",
					placeholder: "",
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.NAME.DESCRIPTION" | translate'
				}
			}, {
				className : 'col-xs-12',
				key: "edit.description",
				type: "textarea",
				templateOptions: {
					label: "",
					description: "",
					placeholder: "",
					maxlength: 255
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
				}
			// }, {
			// 	className : 'col-xs-12',
			// 	key: "edit.type",
			// 	type: "uib-btn-radio",
			// 	templateOptions : {
			// 		label : "",
			// 		required : true,
			// 		btnclass: 'default',
			// 		showicons: true,
			// 		description : "",
			// 		valueProp : 'value',
			// 		labelProp : 'name',
			// 		optionsAttr: 'ui-options', "ngOptions": 'ui-options',
			// 		placeholder : '',
			// 		options : [
			// 			{ name: "Sequential", value: 1 },
			// 			{ name: "Date Driven", value: 2 }
			// 		]
			// 	},
			// 	expressionProperties: {
			// 		'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.TYPE.NAME" | translate',
			// 		'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.TYPE.DESCRIPTION" | translate'
			// 	}
			}, {
				className : 'col-xs-12',
				key: "edit.sequenceNumber",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
					max: ''
				},
				// hideExpression: function($viewValue, $modelValue, scope) {
				// 	var hide = false;
				// 	if (angular.isDefined(scope.model.edit.type) && scope.model.edit.type != 1) hide = true;
				// 	return hide;
				// },
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.SEQUENCENUMBER.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.SEQUENCENUMBER.DESCRIPTION" | translate'
				}
			}, {
				className: "col-xs-12",
				key : "edit.startDate",
				type : "datepicker",
				templateOptions : {
					label : "",
					required : false,
					description : "",
					placeholder : '',
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.STARTDATE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.STARTDATE.DESCRIPTION" | translate'
				},
				// hideExpression: function($viewValue, $modelValue, scope) {
				// 	var hide = false;
				// 	if (angular.isDefined(scope.model.edit.type) && scope.model.edit.type != 2) hide = true;
				// 	return hide;
				// }
			}, {
				className: "col-xs-12",
				key : "edit.endDate",
				type : "datepicker",
				templateOptions : {
					label : "",
					required : false,
					description : "",
					placeholder : '',
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.ENDDATE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.ENDDATE.DESCRIPTION" | translate'
				},
				// hideExpression: function($viewValue, $modelValue, scope) {
				// 	var hide = false;
				// 	if (angular.isDefined(scope.model.edit.type) && scope.model.edit.type != 2) hide = true;
				// 	return hide;
				// }
			}, {
				className : 'col-xs-12',
				key: "edit.xpLevel",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
				}
			}, {
				className : 'col-xs-12',
				key: "edit.maxRedeemable",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PROMOTIONS.FIELDS.MAXREDEEMABLE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PROMOTIONS.FIELDS.MAXREDEEMABLE.DESCRIPTION" | translate'
				}
			}, {
				className : 'col-xs-12',
				key: "edit.maxRedeemableGranularity",
				type: "granularity",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false
				}
			}, {
				className : 'col-xs-12',
				key: "edit.duration",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
					max: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PROMOTIONS.FIELDS.DURATION.NAME" | translate',
					'templateOptions.description': function() {

						if(angular.isUndefinedOrNull(controller.model.edit.maxRedeemableGranularity)) {
							return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DURATION.DESCRIPTION") +","+ $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DURATION.DETAIL");
						}

						return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DURATION.EXPLAIN." +controller.model.edit.maxRedeemableGranularity, {
							val: controller.model.edit.duration
						})
					}
				}
			}, {
				className : 'col-xs-12',
				key: "edit.granularityStartOffset",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PROMOTIONS.FIELDS.GRANULARITYSTARTOFFSET.NAME" | translate',
					'templateOptions.description': () => {
						return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.GRANULARITY_OFFSET.DESCRIPTION") + $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.GRANULARITY_OFFSET.EXPLAIN");
					}
				}
			}, {
				className: "col-xs-12",
				key: "edit.granularityBreak",
				type: "checkbox2",
				templateOptions: {
					label: '',
					description: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PROMOTIONS.FIELDS.GRANULARITYBREAK.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PROMOTIONS.FIELDS.GRANULARITYBREAK.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PROMOTIONS.FIELDS.GRANULARITY_BREAK.DESCRIPTION" | translate'
				}
			}
		];
		
		controller.missionRewardFields = [
			{
				className: "col-xs-12",
				key: "edit.reward.bonusCode", 
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
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.MISSIONREWARD.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.MISSIONREWARD.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.to.options = bonusList;
				}]
			}
		];
		
		controller.addChallenge = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/missions/addchallenge/addchallenge.html',
				controller: 'MissionsAddChallengeModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					mission: function() { return mission; },
					challenge: function() { return null; },
					domainName: function() { return controller.model.edit.domain.name; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/missions/addchallenge/addchallenge.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				if (response) {
					mission = response.plain();
					controller.model = response.plain();
					controller.setMissionChallengeIconUrls();
				}
			});
		}
		
		controller.modifyChallenge = function(challenge) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/missions/addchallenge/addchallenge.html',
				controller: 'MissionsAddChallengeModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					mission: function() { return mission; },
					challenge: function() { return angular.copy(challenge); },
					domainName: function() { return controller.model.edit.domain.name; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/missions/addchallenge/addchallenge.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				if (response) {
					mission = response.plain();
					controller.model = response.plain();
					controller.setMissionChallengeIconUrls();
				}
			});
		}
		
		controller.removeChallenge = function(challenge) {
			PromotionRest.removeChallenge(controller.model.id, challenge.id).then(function(response) {
				if (response._status === 0) {
					controller.model = response.plain();
					controller.setMissionChallengeIconUrls();
					notify.success("UI_NETWORK_ADMIN.MISSIONS.CHALLENGE.REMOVE.SUCCESS");
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.CHALLENGE.REMOVE.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.collapseChallenge = function(challenge) {
			challenge.collapsed = !challenge.collapsed;
		}
		
		controller.addChallengeRule = function(challenge) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/missions/addchallengerule/addchallengerule.html',
				controller: 'MissionsAddChallengeRuleModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domainName: function() { return controller.model.edit.domain.name; },
					mission: function() { return mission },
					challenge: function() { return challenge; },
					rule: function() { return null },
					domainGamesList: function() { return controller.domainGamesList; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/missions/addchallengerule/addchallengerule.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				if (response) {
					mission = response.plain();
					controller.model = response.plain();
					controller.setMissionChallengeIconUrls();
					for (var i = 0; i < controller.model.edit.challenges.length; i++) {
						if (controller.model.edit.challenges[i].id === challenge.id) {
							controller.model.edit.challenges[i].collapsed = true;
							break;
						}
					}
				}
			});
		}
		
		controller.modifyChallengeRule = function(challenge, rule) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/missions/addchallengerule/addchallengerule.html',
				controller: 'MissionsAddChallengeRuleModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domainName: function() { return controller.model.edit.domain.name; },
					mission: function() { return mission; },
					challenge: function() { return challenge; },
					rule: function() { return angular.copy(rule) },
					domainGamesList: function() { return controller.domainGamesList; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/missions/addchallengerule/addchallengerule.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				if (response) {
					mission = response.plain();
					controller.model = response.plain();
					controller.setMissionChallengeIconUrls();
					for (var i = 0; i < controller.model.edit.challenges.length; i++) {
						if (controller.model.edit.challenges[i].id === challenge.id) {
							controller.model.edit.challenges[i].collapsed = true;
							break;
						}
					}
				}
			});
		}
		
		controller.removeChallengeRule = function(challenge, rule) {
			PromotionRest.removeChallengeRule(controller.model.id, challenge.id, rule.id).then(function(response) {
				if (response._status === 0) {
					controller.model = response.plain();
					controller.setMissionChallengeIconUrls();
					for (var i = 0; i < controller.model.edit.challenges.length; i++) {
						if (controller.model.edit.challenges[i].id === challenge.id) {
							controller.model.edit.challenges[i].collapsed = true;
							break;
						}
					}
					notify.success("UI_NETWORK_ADMIN.MISSIONS.CHALLENGERULE.REMOVE.SUCCESS");
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.CHALLENGERULE.REMOVE.ERROR");
				errors.catch("", false)(error)
			});
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
		
		controller.openLightBox = function(src) {
			var image = [{
				'url': src
			}]
			Lightbox.openModal(image, 0);
		}
		
		controller.onContinue = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			PromotionRest.editPost(controller.model.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.MISSIONS.EDIT.SUCCESS");
					$state.go("dashboard.missions.mission.view", { id:response.id, missionRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			PromotionRest.editAndSaveCurrent(controller.model.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.MISSIONS.EDIT.SUCCESS");
					$state.go("dashboard.missions.mission.view", { id:response.id, missionRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);
