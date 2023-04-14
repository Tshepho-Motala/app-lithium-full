'use strict'

angular.module('lithium').controller('GoGameReelConfigsAddController', ['copy', '$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(copy, $translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.model = { engine: { id: null } };
		
		controller.symbols = null;
		
		controller.fields = [
			{
				key: "name",
				type: "input",
				templateOptions: {
					label: "Name",
					description: "The name of the reel configuration",
					placeholder: "",
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
//				}
			}, {
				key: "description",
				type: "textarea",
				templateOptions: {
					label: 'Description',
					description: "Provide a short description of the reel configuration",
					placeholder: ""
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
//				}
			}, {
				key: 'engine.id',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Engine',
					required: true,
					optionsAttr: 'bs-options',
					description: "",
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
			}
		];
		
		if (copy !== undefined && copy !== null) {
			var pCopy = copy.plain();
			controller.model.engine.id = pCopy.engine.id;
			
			gogameGamesRest.findEngineSymbols(controller.model.engine.id).then(function(response) {
				controller.symbols = response.plain();
				controller.initConfiguration();
				controller.configuration.symbolConfigs = pCopy.symbolConfigs;
				controller.updateReelsSymbolCount();
			}).catch(function(error) {
				notify.error('Could not load engine symbols');
				errors.catch('', false)(error)
			});
		}
		
		controller.initConfiguration = function() {
			var configuration = { symbolConfigs: [] };
			for (var i = 0; i < controller.symbols.length; i++) {
				configuration.symbolConfigs.push({symbol: controller.symbols[i].code, reelConfigs: [], stack: "1"})
				for (var k = 0; k < 5; k++) {
					configuration.symbolConfigs[i].reelConfigs.push({reelNum: k + 1, numOfAppearances: "0"});
				}
			}
			controller.configuration = configuration;
		}
		
		controller.updateReelsSymbolCount = function() {
			controller.reelsSymbolCount = [{reelNum: 1, count: 0}, {reelNum: 2, count: 0},
				{reelNum: 3, count: 0}, {reelNum: 4, count: 0}, {reelNum: 5, count: 0}];
			for (var i = 0; i < controller.configuration.symbolConfigs.length; i++) {
				var symbolConfig = controller.configuration.symbolConfigs[i];
				var stacks = symbolConfig.stack.split(',');
				for (var k = 0; k < symbolConfig.reelConfigs.length; k++) {
					var reelConfig = symbolConfig.reelConfigs[k];
					var numOfAppearances = reelConfig.numOfAppearances.split(',');
					for (var x = 0; x < numOfAppearances.length; x++) {
						controller.reelsSymbolCount[k].count = controller.reelsSymbolCount[k].count + (numOfAppearances[x] * stacks[x]);
					}
				}
			}
		}
		
		$scope.$watch(function() { return controller.model.engine.id }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				if (newValue !== undefined && newValue !== null) {
					console.log("Engine changed.")
					if (newValue !== null && newValue !== undefined && newValue !== '') {
						gogameGamesRest.findEngineSymbols(newValue).then(function(response) {
							controller.symbols = response.plain();
							controller.initConfiguration();
						}).catch(function(error) {
							notify.error('Could not load engine symbols');
							errors.catch('', false)(error)
						});
					}
				}
			}
		});
		
		$scope.$watch(function() { return controller.configuration }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				if (newValue !== undefined && newValue !== null) {
					console.log("Symbol configuration changed.")
					if (controller.validateConfiguration(false)) controller.updateReelsSymbolCount();
				}
			}
		}, true);
		
		controller.validateConfiguration = function(submitting) {
			if (controller.configuration === undefined || controller.configuration === null) return false;
			for (var i = 0; i < controller.configuration.symbolConfigs.length; i++) {
				var symbolConfig = controller.configuration.symbolConfigs[i];
				var stacks = symbolConfig.stack.split(',');
				var reelConfigs = symbolConfig.reelConfigs;
				for (var k = 0; k < reelConfigs.length; k++) {
					var reelConfig = reelConfigs[k];
					var numOfAppearances = reelConfig.numOfAppearances.split(',');
					if (numOfAppearances.length > stacks.length) return false;
					if (submitting) {
						if (numOfAppearances.length !== stacks.length) return false;
					}
					for (var x = 0; x < numOfAppearances.length; x++) {
						if (numOfAppearances[x] === undefined ||
							numOfAppearances[x] === null ||
							numOfAppearances[x] === '' ||
							numOfAppearances[x] < 0) {
								return false;
						}
					}
				}
			}
			return true;
		};
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			if (!controller.validateConfiguration(true)) {
				notify.warning("Symbol configuration validation failed. Please make sure you enter a positive value for every reel. Also ensure that you have followed the rules for defining stacks as laid out above.");
				return;
			}
			
			var config = { engineId: controller.model.engine.id, name: controller.model.name, description: controller.model.description, symbolConfigs: controller.configuration.symbolConfigs };
			
			gogameGamesRest.addReelsGenConfig(config).then(function(response) {
				if (response._status === 0) {
					notify.success('Reel Configuration added successfully');
					$state.go("dashboard.gogamegames.reelconfigs.view", { id:response.id });
				} else {
					notify.warning(response._message);
				}
				
			}).catch(function(error) {
				notify.error('Reel Configuration could not be added');
				errors.catch('', false)(error)
			});
		}
	}
]);