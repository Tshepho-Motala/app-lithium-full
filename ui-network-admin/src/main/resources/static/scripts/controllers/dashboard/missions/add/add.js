'use strict'

angular.module('lithium').controller('MissionsAddController', ['$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'Lightbox', 'PromotionRest', 'rest-casino', 'rest-games',
	function($translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, Lightbox, PromotionRest, casinoRest, gamesRest) {
		var controller = this;
		
		var sdate = new Date();
		sdate.setUTCHours(0,0,0,0);
		var edate = new Date();
		edate.setUTCHours(23,59,59,59);

		controller.model = { current: { type: 1, startDate: sdate, endDate: edate, domain: {name: ""}, challenges: [], userCategories: [] }};
		controller.domainGameList = [];
		
		controller.fields = [
			{
				className : 'col-xs-12',
				key : "current.domain.name",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					required : true,
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : []
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
				key: "current.name",
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
				key: "current.description",
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
			// 	key: "current.type",
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
				key: "current.sequenceNumber",
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
				// 	if (angular.isDefined(scope.model.current.type) && scope.model.current.type != 1) hide = true;
				// 	return hide;
				// },
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.SEQUENCENUMBER.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.SEQUENCENUMBER.DESCRIPTION" | translate'
				}
			}, {
				className: "col-xs-12",
				key : "current.startDate",
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
				// 	if (angular.isDefined(scope.model.current.type) && scope.model.current.type != 2) hide = true;
				// 	return hide;
				// }
			}, {
				className: "col-xs-12",
				key : "current.endDate",
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
				// 	if (angular.isDefined(scope.model.current.type) && scope.model.current.type != 2) hide = true;
				// 	return hide;
				// }
			}, {
				className : 'col-xs-12',
				key: "current.xpLevel",
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
				key: "current.maxRedeemable",
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
				key: "current.maxRedeemableGranularity",
				type: "granularity",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false
				}
			}, {
				className : 'col-xs-12',
				key: "current.duration",
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
					'templateOptions.description': function(viewValue, modelValue, $scope) {

						if(angular.isUndefinedOrNull(controller.model.current.maxRedeemableGranularity)) {
							return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DURATION.DESCRIPTION") + $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DURATION.DETAIL");
						}
						
						return $translate.instant("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DURATION.EXPLAIN." +controller.model.current.maxRedeemableGranularity, {
							val: controller.model.current.duration
						})
					}
				}
			}, {
				className : 'col-xs-12',
				key: "current.granularityStartOffset",
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
				key: "current.granularityBreak",
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
				key: "current.reward.bonusCode", 
				type: "ui-select-single",
				templateOptions : {
					label: "",
					description: "",
					placeholder: "",
					optionsAttr: 'bs-options',
					valueProp: 'bonusCode',
					labelProp: 'bonusName',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.MISSIONREWARD.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.MISSIONREWARD.DESCRIPTION" | translate'
				}
			}
		];

		controller.isDomainSelected = () => {
			return !angular.isUndefinedOrNull(controller.model.current.domain.name) && controller.model.current.domain.name.length > 0;
		}
		
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
					mission: function() { return null; },
					challenge: function() { return null; },
					domainName: function() { return controller.model.current.domain.name; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/missions/addchallenge/addchallenge.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(challenge) {
				challenge.uid = controller.model.current.challenges.length + 1;
				if (challenge) controller.model.current.challenges.push(challenge);
			});
		}

		controller.addCategories = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/missions/addcategories/index.html',
				controller: 'MissionsAddCategoryModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domainName: function() { return controller.model.current.domain.name; },
					selectedCategories: () => controller.model.current.userCategories,
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/missions/addcategories/index.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(categories) {
				console.log(categories)
				if(!angular.isUndefinedOrNull(categories) && categories.length > 0) {
					controller.model.current.userCategories = categories
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
					mission: function() { return null; },
					challenge: function() { return angular.copy(challenge); },
					domainName: function() { return controller.model.current.domain.name; },
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
					for (var i = 0; i < controller.model.current.challenges.length; i++) {
						if (controller.model.current.challenges[i].uid === challenge.uid) {
							controller.model.current.challenges[i] = response;
							break;
						}
					}
				}
			});
		}
		
		controller.removeChallenge = function(challenge) {
			for (var i = 0; controller.model.current.challenges.length; i++) {
				if (controller.model.current.challenges[i] === challenge) {
					controller.model.current.challenges.splice(i, 1);
					break;
				}
			}
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
					domainName: function() { return controller.model.current.domain.name; },
					mission: function() { return null; },
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
			
			modalInstance.result.then(function(rule) {
				if (angular.isUndefined(challenge.rules)) challenge.rules = [];
				console.log(rule);
				rule.uid = challenge.rules.length + 1;
				challenge.rules.push(rule);
				if (!challenge.collapsed) challenge.collapsed = true;
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
					domainName: function() { return controller.model.current.domain.name; },
					mission: function() { return null; },
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
					for (var i = 0; i < controller.model.current.challenges.length; i++) {
						if (controller.model.current.challenges[i].uid === challenge.uid) {
							for (var k = 0; k < controller.model.current.challenges[i].rules.length; k++) {
								if (controller.model.current.challenges[i].rules[k].uid === rule.uid) {
									controller.model.current.challenges[i].rules[k] = response;
									break;
								}
							}
							break;
						}
					}
				}
			});
		}
		
		controller.removeChallengeRule = function(challenge, rule) {
			for (var i = 0; i < challenge.rules.length; i++) {
				if (challenge.rules[i] === rule) challenge.rules.splice(i, 1);
			}
		}
		
		controller.getGameName = function(gameGuid) {
			var gameName = "";
			if (controller.domainGamesList !== undefined && gameGuid !== undefined) {
				for (var i = 0; i < controller.domainGamesList.length; i++) {
					if (controller.domainGamesList[i].value === gameGuid) {
						gameName = controller.domainGamesList[i].label;
						break;
					}
				}
			}
			return gameName;
		}
		
		controller.getChallengeRuleExplanation = function(type, action, identifier, val) {
			console.log("type: " + type + ", action: " + action + ", identifier: " + identifier + ", val: " + val);
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
					} else if (action === 'bet_onceoff') {
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
		
		$scope.$watch(function() { return controller.model.current.domain.name }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				if (oldValue !== undefined && oldValue !== null && oldValue !== '') {
					notify.warning("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.CHANGED");
					controller.model.current.reward = null;
					if (controller.model.current.challenges > 0) {
						for (var i = 0; i < controller.model.current.challenges.length; i++) {
							var challenge = controller.model.current.challenges[i];
							challenge.reward = null;
							challenge.rules = [];
						}
					}
				}
				if (newValue !== undefined && newValue !== null && newValue !== '') {
					casinoRest.findPublicBonusListV2(controller.model.current.domain.name, 2, 5).then(function(response) {
						controller.missionRewardFields[0].templateOptions.options = response;
					});
					gamesRest.list(controller.model.current.domain.name).then(function(response) {
						var games = response.plain();
						controller.domainGamesList = [];
						for (var i = 0; i < games.length; i++) {
							if (!games[i].labels.os) {
								controller.domainGamesList.push({ label: games[i].name + '', value: games[i].guid });
							} else {
								controller.domainGamesList.push({ label: games[i].name + ' - ' + games[i].labels.os.value, value: games[i].guid });
							}
						}
					});
				}
			}
		});
		
		controller.openLightBox = function(src) {
			var image = [{
				'url': src
			}]
			Lightbox.openModal(image, 0);
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			if (controller.model.current.startDate !== undefined && controller.model.current.startDate !== null) {
				controller.model.current.startDate.setUTCHours(0,0,0,0);
			}
			if (controller.model.current.endDate !== undefined && controller.model.current.endDate !== null) {
				controller.model.current.endDate.setUTCHours(23,59,59,59);
			}
			
			PromotionRest.create(controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.MISSIONS.ADD.SUCCESS");
					$state.go("dashboard.missions.mission.view", { id:response.id, missionRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);
