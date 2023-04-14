'use strict'

angular.module('lithium').controller('GoGameMathModelsAddController', ['$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function($translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.model = { engine: { id: null }, current: { stops: [], lines: [], paytables: [], featureConfigs: { freespins: undefined, retrigger: undefined, multiplierWild: undefined, freespinWheel: undefined, megaLinkBonus: undefined }, reels: [], reelsProbabilities: [], fsReels: [], fsReelsProbabilities: [], rtReels: [], rtReelsProbabilities: [], mlReels: [], mlReelsProbabilities: [] }};
		controller.engineFiveData = { totalPicks: 3, totalShuffles: 1, totalHints: 5, totalFuturePicks: 1, totalFields: 48 };
		controller.reelSets = [];
		
		controller.fieldsGeneral = [
			{
				key: "name",
				type: "input",
				templateOptions: {
					label: "Name",
					description: "The name of the math model",
					placeholder: "",
					required: true
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
				controller.model.current.stops.push(response.stop);
			});
		}
		
		controller.removeStop = function(stop, $index) {
			for (var i = 0; i < controller.model.current.stops.length; i++) {
				if (i === $index) {
					controller.model.current.stops.splice(i, 1);
					break;
				}
			}
		}
		
		controller.addLine = function() {
			controller.model.current.lines[controller.model.current.lines.length] = { positions: []};
		}
		
		controller.removeLine = function(line, $index) {
			for (var i = 0; i < controller.model.current.lines.length; i++) {
				if (i === $index) {
					controller.model.current.lines.splice(i, 1);
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
						return controller.model.current.paytables;
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
					controller.model.current.paytables = paytables;
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
				controller.model.current.paytables.push({ symbol: response.symbol, payoutCents: [] });
			});
		}
		
		controller.removePaytable = function(paytable, $index) {
			for (var i = 0; i < controller.model.current.paytables.length; i++) {
				if (i === $index) {
					controller.model.current.paytables.splice(i, 1);
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
						controller.model.current.featureConfigs.freespins = response.object;
						break;
					case 'retrigger':
						controller.model.current.featureConfigs.retrigger = response.object;
						break;
					case 'freespinWheel':
						controller.model.current.featureConfigs.freespinWheel = response.object;
						break;
					case 'multiplierWild':
						controller.model.current.featureConfigs.multiplierWild = response.object;
						break;
					case 'anySymbol':
						controller.model.current.featureConfigs.anySymbol = response.object;
						break;
					case 'megaLinkBonus':
						controller.model.current.featureConfigs.megaLinkBonus = response.object;
						break;
				}
				
				console.log(controller.model.current.featureConfigs);
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
					showTextArea: function() {return textArea},
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
					controller.model.current.featureConfigs.freespins = undefined;
					break;
				case 'retrigger':
					controller.model.current.featureConfigs.retrigger = undefined;
					break;
				case 'freespinWheel':
					controller.model.current.featureConfigs.freespinWheel = undefined;
					break;
				case 'multiplierWild':
					controller.model.current.featureConfigs.multiplierWild = undefined;
					break;
				case 'anySymbol':
					controller.model.current.featureConfigs.anySymbol = undefined;
					break;
				case 'megaLinkBonus':
					controller.model.current.featureConfigs.megaLinkBonus = undefined;
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
								case 'bs': return JSON.stringify(controller.model.current.reels[$index]);
								case 'fs': return JSON.stringify(controller.model.current.fsReels[$index]);
								case 'rt': return JSON.stringify(controller.model.current.rtReels[$index]);
								case 'ml': return JSON.stringify(controller.model.current.mlReels[$index]);
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
							controller.model.current.reels.push(reelSet);
						} else {
							controller.model.current.reels[$index] = reelSet;
						}
						controller.setupReelsBaseGame();
						break;
					case 'fs':
						if (adding) {
							controller.model.current.fsReels.push(reelSet);
						} else {
							controller.model.current.fsReels[$index] = reelSet;
						}
						controller.setupReelsFreespin();
						break;
					case 'rt':
						if (adding) {
							controller.model.current.rtReels.push(reelSet);
						} else {
							controller.model.current.rtReels[$index] = reelSet;
						}
						controller.setupReelsRetrigger();
						break;
					case 'ml':
						if (adding) {
							console.log(reelSet);
							controller.model.current.mlReels.push(reelSet);
						} else {
							controller.model.current.mlReels[$index] = reelSet;
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
									controller.model.current.reels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.current.reels[$index] = JSON.parse(controller.reelSets[i].json)[0];
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
			controller.model.current.reels.splice($index);
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
									controller.model.current.fsReels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.current.fsReels[$index] = JSON.parse(controller.reelSets[i].json)[0];
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
			controller.model.current.fsReels.splice($index);
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
									controller.model.current.rtReels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.current.rtReels[$index] = JSON.parse(controller.reelSets[i].json)[0];
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
			controller.model.current.rtReels.splice($index);
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
									controller.model.current.mlReels.push(JSON.parse(controller.reelSets[i].json)[0]);
								} else {
									controller.model.current.mlReels[$index] = JSON.parse(controller.reelSets[i].json)[0];
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
			controller.model.current.mlReels.splice($index);
		}
		
		controller.setupReelsBaseGame = function() {
			controller.reelPositionsBG = [];
			controller.maxReelLenBG = [];
			
			for (var k = 0; k < controller.model.current.reels.length; k++) {
				controller.maxReelLenBG.push(0);
				var reelSet = controller.model.current.reels[k];
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
			
			for (var k = 0; k < controller.model.current.fsReels.length; k++) {
				controller.maxReelLenFS.push(0);
				var reelSet = controller.model.current.fsReels[k];
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
			
			for (var k = 0; k < controller.model.current.rtReels.length; k++) {
				controller.maxReelLenRT.push(0);
				var reelSet = controller.model.current.rtReels[k];
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

			for (var k = 0; k < controller.model.current.mlReels.length; k++) {
				controller.maxReelLenML.push(0);
				var reelSet = controller.model.current.mlReels[k];
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
		
		controller.clearArray = function(array) {
			array.splice(0);
		}
		
		$scope.$watch(function() { return controller.model.engine.id }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				console.log("Engine changed.")
				
				controller.model.current.reels = [];
				
				if (newValue !== undefined && newValue !== null) {
					if ((oldValue !== 5 && newValue === 5) || (newValue !== 5 && oldValue === 5)) {
						controller.model.current.paytables = [];
					}
					if (newValue !== 5) {
						gogameGamesRest.findReelSetsByEngine(controller.model.engine.id).then(function(response) {
							controller.reelSets = response;
						}).catch(function(error) {
							notify.error('Unable to load reel sets');
							errors.catch('', false)(error)
						});
					}
				}
			}
		});
		
		controller.hasAtleastOneProp = function(obj) {
			for (var prop in obj) {
				if (obj.hasOwnProperty(prop)) {
					return true;
				}
			}
		}
		
		controller.engineFivePaytables = function() {
			console.log('engineFivePaytables');
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			var obj = {
				name: controller.model.name,
				engineId: controller.model.engine.id,
				totalPlay: controller.model.totalPlay,
				stops: (controller.model.current.stops.length > 0)? JSON.stringify(controller.model.current.stops): "[]",
				lines: (controller.model.current.lines.length > 0)? JSON.stringify(controller.model.current.lines): "[]",
				paytables: (controller.model.current.paytables.length > 0)? JSON.stringify(controller.model.current.paytables): null,
						featureConfigs: (controller.hasAtleastOneProp(controller.model.current.featureConfigs))? JSON.stringify(controller.model.current.featureConfigs): null,
				reels: (controller.model.current.reels.length > 0)? JSON.stringify(controller.model.current.reels): "[]",
				reelsProbabilities: (controller.model.current.reelsProbabilities.length > 0)? JSON.stringify(controller.model.current.reelsProbabilities): "[]",
				fsReels: (controller.model.current.fsReels.length > 0)? JSON.stringify(controller.model.current.fsReels): null,
				fsReelsProbabilities: (controller.model.current.fsReelsProbabilities.length > 0)? JSON.stringify(controller.model.current.fsReelsProbabilities): null,
				rtReels: (controller.model.current.rtReels.length > 0)? JSON.stringify(controller.model.current.rtReels): null,
				rtReelsProbabilities: (controller.model.current.rtReelsProbabilities.length > 0)? JSON.stringify(controller.model.current.rtReelsProbabilities): null,
				mlReels: (controller.model.current.mlReels.length > 0)? JSON.stringify(controller.model.current.mlReels): null,
				mlReelsProbabilities: (controller.model.current.mlReelsProbabilities.length > 0)? JSON.stringify(controller.model.current.mlReelsProbabilities): null
			};
			
			angular.extend(obj, controller.engineFiveData);
			
//			console.log("Obj", obj);
			
			gogameGamesRest.createMathModel(obj).then(function(response) {
				if (response._status === 0) {
					notify.success('Math model added successfully');
					$state.go("dashboard.gogamegames.mathmodels.mathmodel.view", { id:response.id, mathModelRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}
				
			}).catch(function(error) {
				notify.error('Math model could not be added');
				errors.catch('', false)(error)
			});
		}
	}
]);