'use strict';

angular.module('lithium')
	.controller('GameListController', ["$translate", "$dt", "DTOptionsBuilder", "$filter", "$q", "$state", "$scope", "$rootScope", "$stateParams", "$userService", "rest-games", "GameSuppliersRest", "rest-provider", "notify", "errors", "$compile", "$uibModal", "bsLoadingOverlayService",
		function($translate, $dt, DTOptionsBuilder, $filter, $q, $state, $scope, $rootScope, $stateParams, $userService, restGames, gameSuppliersRest, providerRest, notify, errors, $compile, $uibModal, bsLoadingOverlayService) {
			var controller = this;
			let domainName = $state.params.domainName;

			controller.referenceId = "GamesList_" + (Math.random() * 1000);

			controller.legendCollapsed = true;
			controller.model = {};
			controller.enabled = false;
			controller.freeGame = false;
			controller.localJackpotPool = false;
			controller.progressiveJackpot = false;
			controller.visible = false;
			controller.liveCasino = false;
			controller.instantReward = false;
			controller.recentlyPlayed = false;
			controller.model.domain = domainName;
			controller.model.gameSupplier = null;
			controller.model.providerGuid = null;
			controller.selectedGameProvidersDisplay = undefined;
			controller.selectedGameSuppliersDisplay = undefined;

			controller.model.isEnabled = 0;
			controller.model.isVisible = 0;
			controller.model.isFree = 0;
			controller.model.liveCasino = 0;
			controller.model.progressiveJackpotEnabled = 0;
			controller.model.instantRewardEnabled = 0;
			controller.model.recentlyPlayed = 0;

			controller.gameTest = {};

			controller.filterFields = [
				{
					className: 'col-md-3 col-xs-12',
					key: 'isEnabled',
					type: 'ui-select-single',
					templateOptions : {
						label: 'Show Enabled',
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [
							{value: 0, label: 'Both'},
							{value: 1, label: 'Yes'},
							{value: 2, label: 'No'},
						]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.ENABLED" | translate'
					}
				} ,
				{
					className: 'col-md-3 col-xs-12',
					key: 'isVisible',
					type: 'ui-select-single',
					templateOptions : {
						label: 'Show Enabled & Visible',
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [
							{value: 0, label: 'Both'},
							{value: 1, label: 'Yes'},
							{value: 2, label: 'No'},
						]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.VISIBLE" | translate'
					}
				}, {
					className: 'col-md-3 col-xs-12',
					key: 'isFree',
					type: 'ui-select-single',
					templateOptions : {
						label: 'Free Games',
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [
							{value: 0, label: 'Both'},
							{value: 1, label: 'Yes'},
							{value: 2, label: 'No'},
						]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.FREE_GAME" | translate'
					}
				}, {
					className: 'col-md-3 col-xs-12',
					key: 'liveCasino',
					type: 'ui-select-single',
					templateOptions : {
						label: 'Live Casino',
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [
							{value: 0, label: 'Both'},
							{value: 1, label: 'Yes'},
							{value: 2, label: 'No'},
						]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.LIVE_CASINO" | translate'
					}
				},{
					className: 'col-md-3 col-xs-12',
					key: 'progressiveJackpotEnabled',
					type: 'ui-select-single',
					templateOptions : {
						label: 'Progressive Jackpot Enabled',
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [
							{value: 0, label: 'Both'},
							{value: 1, label: 'Yes'},
							{value: 2, label: 'No'},
						]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.PROGRESSIVE_JACKPOT" | translate'
					}
				},{
					className: 'col-md-3 col-xs-12',
					key: 'instantRewardEnabled',
					type: 'ui-select-single',
					templateOptions : {
						label: 'Instant Reward Enabled',
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [
							{value: 0, label: 'Both'},
							{value: 1, label: 'Yes'},
							{value: 2, label: 'No'},
						]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.INSTANT_REWARD_ENABLED" | translate'
					}
				},{
					className: 'col-md-3 col-xs-12',
					key: 'recentlyPlayed',
					type: 'ui-select-single',
					templateOptions : {
						label: 'Do Not Show In Recently Played',
						valueProp: 'value',
						labelProp: 'label',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [
							{value: 0, label: 'Both'},
							{value: 1, label: 'Yes'},
							{value: 2, label: 'No'},
						]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.EXCLUDE_RECENTLY_PLAYED" | translate'
					}
				}
			];

			controller.gameSuppliers = [];
			controller.gameProviders = [];
			controller.gameConfigs = [
				{ "id" : 1, "name" : "Show Enabled", "selected" : false },
				{ "id" : 2, "name" : "Show Enabled & Visible", "selected" : false },
				{ "id" : 3, "name" : "Free Game", "selected" : false },
				{ "id" : 4, "name" : "Live Casino", "selected" : false },
				{ "id" : 5, "name" : "Progressive Jackpot Enabled", "selected" : false },
				{ "id" : 6, "name" : "Instant Reward Enabled", "selected" : false },
				{ "id" : 7, "name" : "Do Not Show In Recently Played", "selected" : false },
			];

			controller.refreshProviderGameList = function() {
				restGames.updateGamesList(domainName).then(function() {
					notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
					controller.tableLoad();
				}, function(response) {
					notify.error("Failed to get provider list, please wait a while and refresh the page manually");
				})
			};

			controller.retrieveAllGameSuppliersFromRest = function () {
				gameSuppliersRest.findByDomain(domainName).then(function (gameSuppliers) {
					angular.forEach(gameSuppliers.plain(), function (c) {
						controller.gameSuppliers.push({id: c.id, name: c.name, selected: true});
					});
				}).catch(function (error) {
					errors.catch("", false)(error)
				}).finally(function () {
					bsLoadingOverlayService.stop({referenceId: controller.referenceId});
				});
			}

			if (!angular.isUndefined(domainName)) {
				controller.retrieveAllGameSuppliersFromRest();
			}

			controller.findAllGameSuppliers = function () {
				controller.GameSupplierSelectNone();
			}

			controller.GameSupplierSelect = function () {
				controller.selectedGameSuppliers = [];
				for (var d = 0; d < controller.gameSuppliers.length; d++) {
					if (controller.gameSuppliers[d].selected)
						controller.selectedGameSuppliers.push(controller.gameSuppliers[d]);
				}

				if (controller.selectedGameSuppliers.length == controller.gameSuppliers.length) {
					controller.selectedGameSuppliersDisplay = "All Suppliers Selected";
				} else {
					controller.selectedGameSuppliersDisplay = "" + controller.selectedGameSuppliers.length + " Suppliers Selected";
				}
			};

			controller.GameSupplierSelectAll = function () {
				for (var d = 0; d < controller.gameSuppliers.length; d++) controller.gameSuppliers[d].selected = true;
				controller.GameSupplierSelect();
			};

			controller.GameSupplierSelectNone = function () {
				for (var d = 0; d < controller.gameSuppliers.length; d++) controller.gameSuppliers[d].selected = false;
				controller.GameSupplierSelect();
			};


			controller.retrieveAllGameProvidersFromRest = function () {
				providerRest.listByDomainAndType(domainName, 'casino').then(function (gameProviders) {
					angular.forEach(gameProviders.plain(), function (c) {
						controller.gameProviders.push({id: c.id, name: c.name, selected: true});
					});
				}).catch(function (error) {
					errors.catch('', false)(error)
				}).finally(function () {
					bsLoadingOverlayService.stop({referenceId: controller.referenceId});
				});
			}

			if (!angular.isUndefined(domainName)) {
				controller.retrieveAllGameProvidersFromRest();
			}

			controller.findAllGameProviders = function () {
				controller.GameProvidersSelectNone();
			}

			controller.GameProvidersSelect = function () {
				controller.selectedGameProviders = [];
				for (var d = 0; d < controller.gameProviders.length; d++) {
					if (controller.gameProviders[d].selected)
						controller.selectedGameProviders.push(controller.gameProviders[d]);
				}

				if (controller.selectedGameProviders.length == controller.gameProviders.length) {
					controller.selectedGameProvidersDisplay = "All Game Providers Selected";
				} else {
					controller.selectedGameProvidersDisplay = "" + controller.selectedGameProviders.length + " Game Providers Selected";
				}
			};


			controller.GameProvidersSelectAll = function () {
				for (var d = 0; d < controller.gameProviders.length; d++) controller.gameProviders[d].selected = true;
				controller.GameProvidersSelect();
			};

			controller.GameProvidersSelectNone = function () {
				for (var d = 0; d < controller.gameProviders.length; d++) controller.gameProviders[d].selected = false;
				controller.GameProvidersSelect();
			};

			controller.findAllGameConfigs = function () {
				controller.GameConfigsSelectNone();
			}

			controller.GameConfigsSelect = function () {
				controller.selectedGameConfigs = [];
				for (var d = 0; d < controller.gameConfigs.length; d++) {
					if (controller.gameConfigs[d].selected)
						controller.selectedGameConfigs.push(controller.gameConfigs[d]);
				}
				if (controller.selectedGameConfigs.length == controller.gameConfigs.length) {
					controller.selectedGameConfigsDisplay = "All Game Configs Selected";
				} else {
					controller.selectedGameConfigsDisplay = "" + controller.selectedGameConfigs.length + " Game Configs Selected";
				}
			};

			controller.GameConfigsSelectAll = function () {
				for (var d = 0; d < controller.gameConfigs.length; d++) controller.gameConfigs[d].selected = true;
				controller.GameConfigsSelect();
			};

			controller.GameConfigsSelectNone = function () {
				for (var d = 0; d < controller.gameConfigs.length; d++) controller.gameConfigs[d].selected = false;
				controller.GameConfigsSelect();
			};

			function selectedGameSuppliersArray() {
				var selectedSuppliers = "";
				angular.forEach(controller.selectedGameSuppliers, function (supplier) {
					selectedSuppliers += supplier.id + ",";
				});
				return selectedSuppliers;
			}

			function selectedGameProvidersArray() {
				var selectedGameProviders = "";
				angular.forEach(controller.selectedGameProviders, function (gameProviders) {
					selectedGameProviders += "service-casino-provider-" + gameProviders.name.replace(/\s/g, '') + ",";
				});
				return selectedGameProviders;
			}

			function resetGameConfigs() {
				controller.enabled = false;
				controller.freeGame = false;
				controller.localJackpotPool = false;
				controller.progressiveJackpot = false;
				controller.visible = false;
				controller.liveCasino = false;
				controller.instantReward = false;
				controller.recentlyPlayed = false;
			}

			controller.resolveChoice = function(choice) {
				if (choice === undefined || choice === null) return null; // Both
				switch (choice) {
					case 0:
						return null; // Both
					case 1:
						return true; // Auto approved only
					case 2:
						return false; // Not auto approved only
				}
			}

			function selectedGameConfigsCheck() {
				resetGameConfigs();
				angular.forEach(controller.selectedGameConfigs, function (gameConfig) {
					if (gameConfig.name.trim() ===  "Enabled") { controller.enabled = true; }
					if (gameConfig.name.trim() ===  "Enabled & Visible") {
						controller.visible = true;
						controller.enabled = true;
					}
					if (gameConfig.name.trim() ===  "Free Game") { controller.freeGame = true; }
					if (gameConfig.name.trim() ===  "Live Casino") { controller.liveCasino = true; }
					if (gameConfig.name.trim() ===  "Progressive Jackpot Enabled") { controller.progressiveJackpot = true; }
					if (gameConfig.name.trim() ===  "Instant Reward Enabled") { controller.instantReward = true; }
					if (gameConfig.name.trim() ===  "Do Not Show In Recently Played") { controller.recentlyPlayed = true; }
				});
			}

			controller.updateEnableStatus = function (id, currentValue) {
				controller.updateGame(id, currentValue, "enabled");
			}

			controller.updateVisibleStatus = function (id, currentValue) {
				controller.updateGame(id, currentValue, "visible");
			}

			controller.updateGame = function (id, currentValue, flagType) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/games/changeflag/confirmchangeflag.html',
					controller: 'ConfirmChangeFlagModal',
					controllerAs: 'controller',
					backdrop: 'static',
					size: 'md',
					resolve: {
						entityId: function () {
							return id;
						},
						flagType: function () {
							return flagType;
						},
						flagValue: function () {
							return currentValue;
						},
						restService: function () {
							return restGames;
						},
						loadMyFiles: function ($ocLazyLoad) {
							return $ocLazyLoad.load({
								name: 'lithium',
								files: ['scripts/controllers/dashboard/games/changeflag/confirmchangeflag.js']
							})
						}
					}
				});

				modalInstance.result.then(function(response) {
					controller.tableLoad();
				});
			}

			var baseSearchUrl = 'services/service-games/backoffice/games/' + domainName + '/table?1=1';
			var dtSearchOptions = DTOptionsBuilder.newOptions().withOption('createdRow', function(row, data, dataIndex) {
				$compile(angular.element(row).contents())($scope);}).withOption('stateSave', false).withOption('order', [[0, 'desc']]);
			controller.gameSearchTable = $dt.builder()
				.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.ID")))
				.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.NAME")))
				.column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {
					return '<button class="btn btn-primary" lit-if-domains-permission="GAME_EDIT" ng-click="controller.editGame(' + data.id + ')"><i class="fa fa-edit"></i></button>';
				}))
				.column($dt.column(function (data) {return data.providerGuid.replace("service-casino-provider-", "");}).withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.PROVIDER_NAME")))
				.column($dt.column('gameSupplier.name').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.GAME_SUPPLIER")))
				.column($dt.column(function (data) {
					if (data.enabled) {
						return '<label class="switch"><input type="checkbox" checked="checked" lit-if-domains-permission="GAME_EDIT" ng-click="controller.updateEnableStatus(' + data.id + ', ' + data.enabled + ')"><span class="slider"></span></label>';
					} else {
						return '<label class="switch"><input type="checkbox" lit-if-domains-permission="GAME_EDIT" ng-click="controller.updateEnableStatus(' + data.id + ', ' + data.enabled + ')"><span class="slider"></span></label>';
					}

				}).withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.ENABLED")))
				.column($dt.column(function (data) {
					if (data.visible) {
						return '<label class="switch"><input type="checkbox" checked="checked" lit-if-domains-permission="GAME_EDIT" ng-click="controller.updateVisibleStatus(' + data.id + ', ' + data.visible + ')"><span class="slider"></span></label>';
					} else if (!data.visible && !data.enabled) {
						return '<label class="switch"><input type="checkbox" disabled><span class="slider"></span></label>';
					} else {
						return '<label class="switch"><input type="checkbox" lit-if-domains-permission="GAME_EDIT" ng-click="controller.updateVisibleStatus(' + data.id + ', ' + data.visible + ')"><span class="slider"></span></label>';
					}
				}).withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.VISIBLE")))
				.column($dt.column('freeGame').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.FREE_GAME")))
				.column($dt.column('progressiveJackpot').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.PROGRESSIVE_JACKPOT")))
				.column($dt.column('instantRewardEnabled').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.INSTANT_REWARD_ENABLED")))
				.column($dt.column('liveCasino').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.LIVE_CASINO")))
				.column($dt.column('excludeRecentlyPlayed').withTitle($translate("UI_NETWORK_ADMIN.GAMES.LIST.FIELDS.EXCLUDE_RECENTLY_PLAYED")))
				.options(
					{
						url: baseSearchUrl,
						type: 'POST',
						data: function (d) {
							selectedGameConfigsCheck();
							d.requestData = {};
							d.requestData.gameProviders = selectedGameProvidersArray();
							d.requestData.gameSuppliers = selectedGameSuppliersArray();
							d.requestData.name = controller.model.name;
							d.requestData.providerGuid = controller.model.providerGuid;
							d.requestData.enabled = controller.resolveChoice(controller.model.isEnabled);
							d.requestData.visible = controller.resolveChoice(controller.model.isVisible);
							d.requestData.freeGame = controller.resolveChoice(controller.model.isFree);
							d.requestData.progressiveJackpot = controller.resolveChoice(controller.model.progressiveJackpotEnabled);
							d.requestData.instantRewardEnabled = controller.resolveChoice(controller.model.instantRewardEnabled);
							d.requestData.liveCasinoEnabled = controller.resolveChoice(controller.model.liveCasino);
							d.requestData.recentlyPlayed = controller.resolveChoice(controller.model.recentlyPlayed);
						}
					},
					null,
					dtSearchOptions,
					null
				)
				.build();

			controller.add = function() {
				$state.go("dashboard.gameAdd", {domainName:domainName});
			}

			controller.editGame = function(gameId) {
				$state.go("dashboard.gameEdit", {gameId:gameId, domainName: domainName});
			}

			controller.tableLoad = function () {
				controller.gameSearchTable.instance.rerender(true);
			}

			controller.toggleLegendCollapse = function () {
				controller.legendCollapsed = !controller.legendCollapsed;
			}

			controller.resetFilter = function (collapse) {
				if (collapse) {
					controller.toggleLegendCollapse();
				}

				controller.model.isEnabled = 0;
				controller.model.isVisible = 0;
				controller.model.isFree = 0;
				controller.model.liveCasino = 0;
				controller.model.progressiveJackpotEnabled = 0;
				controller.model.instantRewardEnabled = 0;
				controller.model.recentlyPlayed = 0;
				controller.model.id = null;
				controller.applyFilter(true);
			}

			controller.applyFilter = function (toggle) {
				if (toggle === true) {
					controller.toggleLegendCollapse();
				}
				controller.tableLoad();
			}

			$rootScope.provide.dropDownMenuProvider['gameSuppliersList'] = () => {
				return controller.gameSuppliers
			}

			$rootScope.provide.dropDownMenuProvider['gameSuppliersChange'] = (data) => {
				controller.selectedGameSuppliers = [...data]
				controller.tableLoad()
			}

			$rootScope.provide.dropDownMenuProvider['gameProvidersList'] = () => {
				return controller.gameProviders
			}

			$rootScope.provide.dropDownMenuProvider['gameProvidersChange'] = (data) => {
				controller.selectedGameProviders = [...data]
				controller.tableLoad()
			}

			$rootScope.provide.dropDownMenuProvider['gameConfigsList'] = () => {
				return controller.gameConfigs
			}

			$rootScope.provide.dropDownMenuProvider['gameConfigsChange'] = (data) => {
				controller.selectedGameConfigs = [...data]
				controller.tableLoad()
			}

			window.VuePluginRegistry.loadByPage("GameSearchTopBar")
		}]);
