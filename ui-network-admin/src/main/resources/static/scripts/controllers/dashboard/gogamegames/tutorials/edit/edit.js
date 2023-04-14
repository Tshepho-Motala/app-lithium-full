'use strict'

angular.module('lithium').controller('GoGameTutorialsEditController', ['tutorial', '$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(tutorial, $translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		console.log(tutorial.mathModelRevision);
		
		controller.model = { domainName: tutorial.domain.name, engineId: tutorial.engine.id, mathModelId: null, pbCents: tutorial.pbCents, winCents: tutorial.winCents, result: typeof(tutorial.result) !== 'string' ? JSON.stringify(tutorial.result) : tutorial.result };
		
		controller.fields = [
			{
				key : "domainName",
				type : "ui-select-single",
				templateOptions : {
					label : "Domain",
					required : true,
					optionsAttr: 'bs-options',
					description : "The domain that you are creating the tutorial for",
					valueProp : 'name',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : [],
					focus: true
				},
//					expressionProperties: {
//						'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//						'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//					},
				controller: ['$scope', function($scope) {
					$scope.to.options = $userService.domainsWithRole("GOGAMEGAMES_TUTORIALS_*");
				}]
			},
			{
				key: 'engineId',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Engine',
					description: 'Choose the engine that you\'re creating the tutorial for',
					required: true,
					optionsAttr: 'bs-options',
					valueProp: 'id',
					labelProp: 'id',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: []
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//				},
				controller: ['$scope', function($scope) {
					gogameGamesRest.findAllEngines().then(function(response) {
						$scope.to.options = response.plain();
					});
				}]
			}, {
				key: 'mathModelId',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Math Model',
					description: 'Choose the math model. The purpose of this is to populate the tutorial with appropriate reels setup and other configuration properties',
					optionsAttr: 'bs-options',
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: [],
					focus: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//				},
				controller: ['$scope', function($scope) {
					var options = [];
					gogameGamesRest.findAllMathModels().then(function(response) {
						var r = response.plain();
						for (var i = 0; i < r.length; i++) {
							options.push({ id: r[i].id, name: r[i].current.name});
						}
						$scope.to.options = options;
					});
				}]
			}, {
				key: "totalPlay",
				type: "ui-money-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "Total Play",
					description: "",
					addFormControlClass: true,
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.CURRENCY.MINAMOUNT.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.CURRENCY.MINAMOUNT.DESCRIPTION" | translate'
//				}
			}, {
				key: 'pbCents',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Player Balance Cents',
					description: "The balance displayed in game, before the player hit's take it",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: '',
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//				}
			}, {
				key: 'winCents',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Win Cents',
					description: "Total win cents in the result below. This will be added to player balance cents above, after the player hit's take it",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: '',
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//				}
			}, {
				key: "result",
				type: "textarea",
				templateOptions: {
					label: 'Result',
					description: "The JSON response that needs to be passed to the game",
					placeholder: "",
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
//				}
			}
		];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			gogameGamesRest.editTutorial(tutorial.id, controller.model).then(function(response) {
				console.log(response);
				if (response._status === 0) {
					notify.success('Successfully edited tutorial');
					$state.go("dashboard.gogamegames.tutorials.tutorial", { id:response.id }, { reload: true });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("Could not save tutorial");
				errors.catch('', false)(error)
			});
		}
		
		controller.back = function() {
			$state.go("dashboard.gogamegames.tutorials.tutorial", { id:tutorial.id });
		}
	}
]);