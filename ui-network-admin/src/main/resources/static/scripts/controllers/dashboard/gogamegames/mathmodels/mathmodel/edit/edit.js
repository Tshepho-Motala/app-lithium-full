'use strict'

angular.module('lithium').controller('GoGameMathModelEditController', ['mathModel', '$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(mathModel, $translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.model = mathModel;
		controller.mathModelRevision = mathModel.edit;
		controller.engineFiveData = { totalPicks: controller.mathModelRevision.totalPicks, totalShuffles: controller.mathModelRevision.totalShuffles, totalHints: controller.mathModelRevision.totalHints, totalFuturePicks: controller.mathModelRevision.totalFuturePicks, totalFields: controller.mathModelRevision.totalFields };
		controller.reelSets = [];
		
		controller.parseJSONStringsToObjs = function() {
			if (controller.mathModelRevision.stops !== undefined && controller.mathModelRevision.stops !== null) {
				if (typeof controller.mathModelRevision.stops === 'string') {
					controller.mathModelRevision.stops = JSON.parse(controller.mathModelRevision.stops);
				}
			} else {
				controller.mathModelRevision.stops = [];
			}
			if (controller.mathModelRevision.lines !== undefined && controller.mathModelRevision.lines !== null) {
				if (typeof controller.mathModelRevision.lines === 'string') {
					controller.mathModelRevision.lines = JSON.parse(controller.mathModelRevision.lines);
				}
			} else {
				controller.mathModelRevision.lines = [];
			}
			if (controller.mathModelRevision.paytables !== undefined && controller.mathModelRevision.paytables !== null) {
				if (typeof controller.mathModelRevision.paytables === 'string') {
					controller.mathModelRevision.paytables = JSON.parse(controller.mathModelRevision.paytables);
				}
			} else {
				controller.mathModelRevision.paytables = [];
			}
			if (controller.mathModelRevision.featureConfigs !== undefined && controller.mathModelRevision.featureConfigs !== null) {
				if (typeof controller.mathModelRevision.featureConfigs === 'string') {
					controller.mathModelRevision.featureConfigs = JSON.parse(controller.mathModelRevision.featureConfigs);
				}
			} else {
				controller.mathModelRevision.featureConfigs = {};
			}
			if (controller.mathModelRevision.reels !== undefined && controller.mathModelRevision.reels !== null) {
				if (typeof controller.mathModelRevision.reels === 'string') {
					controller.mathModelRevision.reels = JSON.parse(controller.mathModelRevision.reels);
				}
			} else {
				controller.mathModelRevision.reels = [];
			}
			if (controller.mathModelRevision.reelsProbabilities !== undefined && controller.mathModelRevision.reelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.reelsProbabilities === 'string') {
					controller.mathModelRevision.reelsProbabilities = JSON.parse(controller.mathModelRevision.reelsProbabilities);
				}
			} else {
				controller.mathModelRevision.reelsProbabilities = [];
			}
			if (controller.mathModelRevision.fsReels !== undefined && controller.mathModelRevision.fsReels !== null) {
				if (typeof controller.mathModelRevision.fsReels === 'string') {
					controller.mathModelRevision.fsReels = JSON.parse(controller.mathModelRevision.fsReels);
				}
			} else {
				controller.mathModelRevision.fsReels = [];
			}
			if (controller.mathModelRevision.fsReelsProbabilities !== undefined && controller.mathModelRevision.fsReelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.fsReelsProbabilities === 'string') {
					controller.mathModelRevision.fsReelsProbabilities = JSON.parse(controller.mathModelRevision.fsReelsProbabilities);
				}
			} else {
				controller.mathModelRevision.fsReelsProbabilities = [];
			}
			if (controller.mathModelRevision.rtReels !== undefined && controller.mathModelRevision.rtReels !== null) {
				if (typeof controller.mathModelRevision.rtReels === 'string') {
					controller.mathModelRevision.rtReels = JSON.parse(controller.mathModelRevision.rtReels);
				}
			} else {
				controller.mathModelRevision.rtReels = [];
			}
			if (controller.mathModelRevision.rtReelsProbabilities !== undefined && controller.mathModelRevision.rtReelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.rtReelsProbabilities === 'string') {
					controller.mathModelRevision.rtReelsProbabilities = JSON.parse(controller.mathModelRevision.rtReelsProbabilities);
				}
			} else {
				controller.mathModelRevision.rtReelsProbabilities = [];
			}
			if (controller.mathModelRevision.mlReels !== undefined && controller.mathModelRevision.mlReels !== null) {
				if (typeof controller.mathModelRevision.mlReels === 'string') {
					controller.mathModelRevision.mlReels = JSON.parse(controller.mathModelRevision.mlReels);
				}
			} else {
				controller.mathModelRevision.mlReels = [];
			}
			if (controller.mathModelRevision.mlReelsProbabilities !== undefined && controller.mathModelRevision.mlReelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.mlReelsProbabilities === 'string') {
					controller.mathModelRevision.mlReelsProbabilities = JSON.parse(controller.mathModelRevision.mlReelsProbabilities);
				}
			} else {
				controller.mathModelRevision.mlReelsProbabilities = [];
			}
		}
		
		controller.setupReelsBaseGame = function() {
			controller.reelPositionsBG = [];
			controller.maxReelLenBG = [];
			
			for (var k = 0; k < controller.model.edit.reels.length; k++) {
				controller.maxReelLenBG.push(0);
				var reelSet = controller.model.edit.reels[k];
				for (var i = 0; i < 5; i++) {
					controller.reelPositionsBG.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenBG[k])
						controller.maxReelLenBG[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenBG[k]; i++) {
					controller.reelPositionsBG[k].push(i + 1);
				}
			}
		}
		
		controller.setupReelsFreespin = function() {
			controller.reelPositionsFS = [];
			controller.maxReelLenFS = [];
			
			for (var k = 0; k < controller.model.edit.fsReels.length; k++) {
				controller.maxReelLenFS.push(0);
				var reelSet = controller.model.edit.fsReels[k];
				for (var i = 0; i < 5; i++) {
					controller.reelPositionsFS.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenFS[k])
						controller.maxReelLenFS[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenFS[k]; i++) {
					controller.reelPositionsFS[k].push(i + 1);
				}
			}
		}
		
		controller.setupReelsRetrigger = function() {
			controller.reelPositionsRT = [];
			controller.maxReelLenRT = [];
			
			for (var k = 0; k < controller.model.edit.rtReels.length; k++) {
				controller.maxReelLenRT.push(0);
				var reelSet = controller.model.edit.rtReels[k];
				for (var i = 0; i < 5; i++) {
					controller.reelPositionsRT.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenRT[k])
						controller.maxReelLenRT[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenRT[k]; i++) {
					controller.reelPositionsRT[k].push(i + 1);
				}
			}
		}

		controller.setupReelsMegalink = function() {
			controller.reelPositionsML = [];
			controller.maxReelLenML = [];

			for (var k = 0; k < controller.model.edit.mlReels.length; k++) {
				controller.maxReelLenML.push(0);
				var reelSet = controller.model.edit.mlReels[k];
				for (var i = 0; i < reelSet.length; i++) {
					controller.reelPositionsML.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenML[k])
						controller.maxReelLenML[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenML[k]; i++) {
					controller.reelPositionsML[k].push(i + 1);
				}
			}
		}
		
		controller.setReelSets = function() {
			gogameGamesRest.findReelSetsByEngine(controller.model.engine.id).then(function(response) {
				controller.reelSets = response;
			}).catch(function(error) {
				notify.error('Unable to load reel sets');
				errors.catch('', false)(error)
			});
		}
		
		controller.parseJSONStringsToObjs();
		controller.setupReelsBaseGame();
		controller.setupReelsFreespin();
		controller.setupReelsRetrigger();
		controller.setupReelsMegalink();
		controller.setReelSets();
		
		controller.fieldsGeneral = [
			{
				key: "edit.name",
				type: "input",
				templateOptions: {
					label: "Name",
					description: "The name of the math model",
					placeholder: "",
					required: true,
					disabled: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
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
					options: [],
					disabled: true
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
		
		controller.fieldsEngineFive = [
			{
				key: 'totalPicks',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Total Picks',
					description: "",
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
				key: 'totalShuffles',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Total Shuffles',
					description: "",
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
				key: 'totalHints',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Total Hints',
					description: "",
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
				key: 'totalFuturePicks',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Total Future Picks',
					description: "",
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
				key: 'totalFields',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Total Fields',
					description: "",
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
			}
		];
		
		controller.addStop = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/stops/stops.html',
				controller: 'GoGameMathModelsAddStopsController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/stops/stops.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
//				console.log(response);
				controller.model.edit.stops.push(response.stop);
			});
		}
		
		controller.removeStop = function(stop, $index) {
			for (var i = 0; i < controller.model.edit.stops.length; i++) {
				if (i === $index) {
					controller.model.edit.stops.splice(i, 1);
					break;
				}
			}
		}
		
		controller.addLine = function() {
			controller.model.edit.lines[controller.model.edit.lines.length] = { positions: []};
		}
		
		controller.removeLine = function(line, $index) {
			for (var i = 0; i < controller.model.edit.lines.length; i++) {
				if (i === $index) {
					controller.model.edit.lines.splice(i, 1);
					break;
				}
			}
		}
		
		controller.addLinePosition = function(line) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/lines/lines.html',
				controller: 'GoGameMathModelsAddLinesController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/lines/lines.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
//				console.log(response);
				line.positions.push(response.position);
			});
		}
		
		controller.addPaytablesManual = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/paytables/paytablesmanual.html',
				controller: 'GoGameMathModelsAddPaytablesManualController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					paytables: function() {
						return controller.model.edit.paytables;
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/paytables/paytablesmanual.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				var paytables = null;
				try {
					paytables = JSON.parse(response.paytables);
					controller.model.edit.paytables = paytables;
				} catch (error) {
					notify.error("Could not parse paytables to JSON. Please ensure proper JSON formatting.");
					return;
				}
			});
		}
		
		controller.addPaytable = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/paytables/paytables.html',
				controller: 'GoGameMathModelsAddPaytablesController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/paytables/paytables.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
//				console.log(response);
				controller.model.edit.paytables.push({ symbol: response.symbol, payoutCents: [] });
			});
		}
		
		controller.removePaytable = function(paytable, $index) {
			for (var i = 0; i < controller.model.edit.paytables.length; i++) {
				if (i === $index) {
					controller.model.edit.paytables.splice(i, 1);
					break;
				}
			}
		}
		
		controller.addPaytablePayoutCents = function(paytable) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/paytables/payoutcents/payoutcents.html',
				controller: 'GoGameMathModelsAddPaytablesPayoutCentsController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/paytables/payoutcents/payoutcents.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
//				console.log(response);
				paytable.payoutCents.push(response.payoutCents);
			});
		}
		
		controller.addFeatureConfig = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/featureconfigs/featureconfigs.html',
				controller: 'GoGameMathModelsAddFeatureConfigsController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					features: function() {
						if (controller.model.engine !== undefined && controller.model.engine !== null &&
								controller.model.engine.id !== undefined && controller.model.engine.id !== null) {
							return gogameGamesRest.findEngineFeatures(controller.model.engine.id);
						} else {
							return null;
						}
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/featureconfigs/featureconfigs.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
//				console.log(response);
				
				switch (response.type) {
					case 'freespin':
						controller.model.edit.featureConfigs.freespins = response.object;
						break;
					case 'retrigger':
						controller.model.edit.featureConfigs.retrigger = response.object;
						break;
					case 'freespinWheel':
						controller.model.edit.featureConfigs.freespinWheel = response.object;
						break;
					case 'multiplierWild':
						controller.model.edit.featureConfigs.multiplierWild = response.object;
						break;
					case 'anySymbol':
						controller.model.edit.featureConfigs.anySymbol = response.object;
						break;
					case 'megaLinkBonus':
						controller.model.edit.featureConfigs.megaLinkBonus = response.object;
						break;
				}
			});
		}
		
		controller.featureConfigAddOption = function(object, json) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/featureconfigsaddoption/featureconfigsaddoption.html',
				controller: 'GoGameMathModelsFeatureConfigsAddOptionController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					option: function() { return null },
					showTextArea: function() {return false},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/featureconfigsaddoption/featureconfigsaddoption.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				var obj = null;
				if (json) {
					try {
						obj = JSON.parse(response.option);
					} catch (error) {
						notify.error("Could not parse to JSON. Please ensure proper JSON formatting.");
						return;
					}
				} else {
					obj = response.option;
				}
				object.push(obj);
			});
		}
		
		controller.featureConfigChangeOption = function(object, property, textArea = false) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/featureconfigsaddoption/featureconfigsaddoption.html',
				controller: 'GoGameMathModelsFeatureConfigsAddOptionController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					option: function() {
						if (!textArea) {
							return (object[property] != null) ? object[property] : 1;
						} else {
							return (object[property] != null) ? JSON.stringify(object[property]) : 1;
						}
					},
					showTextArea: function() {
						return textArea;
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/featureconfigsaddoption/featureconfigsaddoption.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				if (!textArea) {
					object[property] = response.option;
				} else {
					var obj = null;
					try {
						obj = JSON.parse(response.option);
						object[property] = obj;
					} catch (error) {
						notify.error("Could not parse JSON.");
						return;
					}
				}
			});
		}
		
		controller.removeFeatureConfig = function(type) {
			switch (type) {
				case 'freespin':
					controller.model.edit.featureConfigs.freespins = undefined;
					break;
				case 'retrigger':
					controller.model.edit.featureConfigs.retrigger = undefined;
					break;
				case 'freespinWheel':
					controller.model.edit.featureConfigs.freespinWheel = undefined;
					break;
				case 'multiplierWild':
					controller.model.edit.featureConfigs.multiplierWild = undefined;
					break;
				case 'anySymbol':
					controller.model.edit.featureConfigs.anySymbol = undefined;
					break;
				case 'megaLinkBonus':
					controller.model.edit.featureConfigs.megaLinkBonus = undefined;
					break;
			}
		}
		
		controller.addReelsManual = function(adding, type, $index) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reelsmanual.html',
				controller: 'GoGameMathModelsAddReelsManualController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					reelSet: function() {
						if (!adding) {
							switch (type) {
								case 'bs': return JSON.stringify(controller.model.edit.reels[$index]);
								case 'fs': return JSON.stringify(controller.model.edit.fsReels[$index]);
								case 'rt': return JSON.stringify(controller.model.edit.rtReels[$index]);
								case 'ml': return JSON.stringify(controller.model.edit.mlReels[$index]);
							}
						} else {
							return null;
						}
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reelsmanual.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				var reelSet = null;
				try {
					reelSet = JSON.parse(response.reelSet);
				} catch (error) {
					notify.error("Could not parse reel set to JSON. Please ensure proper JSON formatting.");
					return;
				}
				switch (type) {
					case 'bs':
						if (adding) {
							controller.model.edit.reels.push(reelSet);
						} else {
							controller.model.edit.reels[$index] = reelSet;
						}
						controller.setupReelsBaseGame();
						break;
					case 'fs':
						if (adding) {
							controller.model.edit.fsReels.push(reelSet);
						} else {
							controller.model.edit.fsReels[$index] = reelSet;
						}
						controller.setupReelsFreespin();
						break;
					case 'rt':
						if (adding) {
							controller.model.edit.rtReels.push(reelSet);
						} else {
							controller.model.edit.rtReels[$index] = reelSet;
						}
						controller.setupReelsRetrigger();
						break;
					case 'ml':
						if (adding) {
							controller.model.edit.mlReels.push(reelSet);
						} else {
							controller.model.edit.mlReels[$index] = reelSet;
						}
						controller.setupReelsMegalink();
						break;
				}
			});
		}
		
		controller.addReelBS = function(adding, $index) {
			if (controller.reelSets !== undefined && controller.reelSets !== null && controller.reelSets.length > 0) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.html',
					controller: 'GoGameMathModelsAddReelsController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						reelSets: function() { return controller.reelSets; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.js']
							})
						}
					}
				});
				
				modalInstance.result.then(function(response) {
					if (response.reelsetId) {
						for (var i = 0; i < controller.reelSets.length; i++) {
							if (controller.reelSets[i].id === response.reelsetId) {
								if (adding) {
									controller.model.edit.reels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.edit.reels[$index] = JSON.parse(controller.reelSets[i].json)[0];
								}
								controller.setupReelsBaseGame();
								break;
							}
						}
					}
				});
			} else {
				notify.warning("No reel sets available");
				return;
			}
		}
		
		controller.removeReelBS = function($index) {
			controller.model.edit.reels.splice($index);
		}
		
		controller.addReelFS = function(adding, $index) {
			if (controller.reelSets !== undefined && controller.reelSets !== null && controller.reelSets.length > 0) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.html',
					controller: 'GoGameMathModelsAddReelsController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						reelSets: function() { return controller.reelSets; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.js']
							})
						}
					}
				});
				
				modalInstance.result.then(function(response) {
					if (response.reelsetId) {
						for (var i = 0; i < controller.reelSets.length; i++) {
							if (controller.reelSets[i].id === response.reelsetId) {
								if (adding) {
									controller.model.edit.fsReels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.edit.fsReels[$index] = JSON.parse(controller.reelSets[i].json)[0];
								}
								controller.setupReelsFreespin();
								break;
							}
						}
					}
				});
			} else {
				notify.warning("No reel sets available");
				return;
			}
		}
		
		controller.removeReelFS = function($index) {
			controller.model.edit.fsReels.splice($index);
		}
		
		controller.addReelRT = function(adding, $index) {
			if (controller.reelSets !== undefined && controller.reelSets !== null && controller.reelSets.length > 0) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.html',
					controller: 'GoGameMathModelsAddReelsController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						reelSets: function() { return controller.reelSets; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.js']
							})
						}
					}
				});
				
				modalInstance.result.then(function(response) {
					if (response.reelsetId) {
						for (var i = 0; i < controller.reelSets.length; i++) {
							if (controller.reelSets[i].id === response.reelsetId) {
								if (adding) {
									controller.model.edit.rtReels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.edit.rtReels[$index] = JSON.parse(controller.reelSets[i].json)[0];
								}
								controller.setupReelsRetrigger();
								break;
							}
						}
					}
				});
			} else {
				notify.warning("No reel sets available");
				return;
			}
		}
		
		controller.removeReelRT = function($index) {
			controller.model.edit.rtReels.splice($index);
		}

		controller.addReelML = function(adding, $index) {
			if (controller.reelSets !== undefined && controller.reelSets !== null && controller.reelSets.length > 0) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.html',
					controller: 'GoGameMathModelsAddReelsController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						reelSets: function() { return controller.reelSets; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: ['scripts/controllers/dashboard/gogamegames/mathmodels/components/reels/reels.js']
							})
						}
					}
				});

				modalInstance.result.then(function(response) {
					if (response.reelsetId) {
						for (var i = 0; i < controller.reelSets.length; i++) {
							if (controller.reelSets[i].id === response.reelsetId) {
								if (adding) {
									controller.model.edit.mlReels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.edit.mlReels[$index] = JSON.parse(controller.reelSets[i].json)[0];
								}
								controller.setupReelsMegalink();
								break;
							}
						}
					}
				});
			} else {
				notify.warning("No reel sets available");
				return;
			}
		}

		controller.removeReelML = function($index) {
			controller.model.edit.mlReels.splice($index);
		}
		
		controller.clearArray = function(array) {
			array.splice(0);
		}
		
		controller.hasAtleastOneProp = function(obj) {
			for (var prop in obj) {
				if (obj.hasOwnProperty(prop)) {
					return true;
				}
			}
		}
		
		controller.onContinue = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			var obj = {
				name: controller.model.edit.name,
				engineId: controller.model.engine.id,
				totalPlay: controller.model.totalPlay,
				stops: (controller.model.edit.stops.length > 0)? JSON.stringify(controller.model.edit.stops): null,
				lines: (controller.model.edit.lines.length > 0)? JSON.stringify(controller.model.edit.lines): null,
				paytables: (controller.model.edit.paytables.length > 0)? JSON.stringify(controller.model.edit.paytables): null,
				featureConfigs: (controller.hasAtleastOneProp(controller.model.edit.featureConfigs))? JSON.stringify(controller.model.edit.featureConfigs): null,
				reels: (controller.model.edit.reels.length > 0)? JSON.stringify(controller.model.edit.reels): null,
				reelsProbabilities: (controller.model.edit.reelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.reelsProbabilities): null,
				fsReels: (controller.model.edit.fsReels.length > 0)? JSON.stringify(controller.model.edit.fsReels): null,
				fsReelsProbabilities: (controller.model.edit.fsReelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.fsReelsProbabilities): null,
				rtReels: (controller.model.edit.rtReels.length > 0)? JSON.stringify(controller.model.edit.rtReels): null,
				rtReelsProbabilities: (controller.model.edit.rtReelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.rtReelsProbabilities): null,
				mlReels: (controller.model.edit.mlReels.length > 0)? JSON.stringify(controller.model.edit.mlReels): null,
				mlReelsProbabilities: (controller.model.edit.mlReelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.mlReelsProbabilities): null
			};
			
//			console.log("Obj", obj);
			
			gogameGamesRest.modifyMathModelPost(mathModel.id, obj).then(function(response) {
				if (response._status === 0) {
					notify.success('Math model saved successfully');
					$state.go("dashboard.gogamegames.mathmodels.mathmodel.view", { id:response.id, mathModelRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}
				
			}).catch(function(error) {
				notify.error('Math model could not be saved');
				errors.catch('', false)(error)
			});
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			var obj = {
				name: controller.model.edit.name,
				engineId: controller.model.engine.id,
				totalPlay: controller.model.totalPlay,
				stops: (controller.model.edit.stops.length > 0)? JSON.stringify(controller.model.edit.stops): "[]",
				lines: (controller.model.edit.lines.length > 0)? JSON.stringify(controller.model.edit.lines): "[]",
				paytables: (controller.model.edit.paytables.length > 0)? JSON.stringify(controller.model.edit.paytables): "[]",
				featureConfigs: (controller.hasAtleastOneProp(controller.model.edit.featureConfigs))? JSON.stringify(controller.model.edit.featureConfigs): null,
				reels: (controller.model.edit.reels.length > 0)? JSON.stringify(controller.model.edit.reels): "[]",
				reelsProbabilities: (controller.model.edit.reelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.reelsProbabilities): "[]",
				fsReels: (controller.model.edit.fsReels.length > 0)? JSON.stringify(controller.model.edit.fsReels): null,
				fsReelsProbabilities: (controller.model.edit.fsReelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.fsReelsProbabilities): null,
				rtReels: (controller.model.edit.rtReels.length > 0)? JSON.stringify(controller.model.edit.rtReels): null,
				rtReelsProbabilities: (controller.model.edit.rtReelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.rtReelsProbabilities): null,
				mlReels: (controller.model.edit.mlReels.length > 0)? JSON.stringify(controller.model.edit.mlReels): null,
				mlReelsProbabilities: (controller.model.edit.mlReelsProbabilities.length > 0)? JSON.stringify(controller.model.edit.mlReelsProbabilities): null
			};
			
			angular.extend(obj, controller.engineFiveData);
			
//			console.log("Obj", obj);
			
			gogameGamesRest.modifyAndSaveCurrentMathModel(mathModel.id, obj).then(function(response) {
				if (response._status === 0) {
					notify.success('Math model modified successfully');
					$state.go("dashboard.gogamegames.mathmodels.mathmodel.view", { id:response.id, mathModelRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}
				
			}).catch(function(error) {
				notify.error('Math model could not be modified');
				errors.catch('', false)(error)
			});
		}
	}
]);