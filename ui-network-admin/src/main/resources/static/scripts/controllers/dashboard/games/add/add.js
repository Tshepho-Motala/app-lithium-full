'use strict';

angular.module('lithium')
	.controller('gameAdd', ["$q", "$timeout", "$translate", "$log", "$state", "$stateParams", "$http", "rest-provider","rest-games", "notify","GameSuppliersRest","GameTypesRest", "GameStudioRest", "$scope", "$filter",
	function($q, $timeout, $translate, $log, $state, $stateParams, $http, providerRest, gamesRest, notify, gameSuppliersRest, gameTypesRest, gameStudioRest, $scope, $filter) {
		let controller = this;
		controller.selectedDomain = $stateParams.domainName;

		controller.model = {};

		controller.options = {};
        $scope.currentDateTime = moment(new Date()).format("YYYY-MM-DD");
        $scope.formatDate = function (date) {
            return $filter('date')(date, "YYYY-MM-DD");
        }


		$scope.currentDateTime = moment(new Date()).format("YYYY-MM-DD HH:mm");
		$scope.formatDate = function (date) {
			return $filter('date')(date, "YYYY-MM-DD HH:mm");
		}

		controller.fields =
			[
				{
					"className": "col-xs-12",
					"type": "ui-select-single",
					"key": "url",
					"templateOptions": {
						"label": '',
						"required": true,
						"optionsAttr": 'bs-options',
						"description": "",
						"valueProp": 'url',
						"labelProp": 'name',
						"placeholder": '',
						"ngOptions": 'ui-options',
						"options": []
					},
					controller: ['$scope', function ($scope) {
						providerRest.listByDomainAndType(controller.selectedDomain, 'casino')
							.then(function (providers) {
								$scope.to.options = providers;
								$scope.to.loading = false;
							});
					}],
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.URL.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.URL.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.URL.DESCRIPTION" | translate'
					}
				},
				{
					// FIXME FIXME FIXME: Not required as opposed to spec to cater for other game providers with no
					//					  game supplier dependency
					"className": "col-xs-12",
					"type": "ui-select-single",
					"key": "gameSupplier.id",
					"templateOptions": {
						"label": '',
						"optionsAttr": 'bs-options',
						"description": "",
						"valueProp": 'id',
						"labelProp": 'name',
						"placeholder": '',
						"ngOptions": 'ui-options',
						"options": []
					},
					controller: ['$scope', function ($scope) {
						gameSuppliersRest.findByDomain(controller.selectedDomain)
							.then(function (gameSuppliers) {
								$scope.to.options = gameSuppliers;
							});
					}],
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-SUPPLIER.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-SUPPLIER.DESCRIPTION" | translate'
					}
				},
				{
					"className": "col-xs-12",
					"type": "ui-select-single",
					"key": "primaryGameType.id",
					"templateOptions": {
						"label": '',
						"optionsAttr": 'bs-options',
						"description": "",
						"valueProp": 'id',
						"labelProp": 'name',
						"placeholder": '',
						"ngOptions": 'ui-options',
						"options": []
					},
					controller: ['$scope', function ($scope) {
						gameTypesRest.findByDomainAndType(controller.selectedDomain, 'primary')
						.then(function (gameTypes) {
							$scope.to.options = gameTypes;
						});
					}],
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PRIMARY-GAME-TYPE.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.PRIMARY-GAME-TYPE.DESCRIPTION" | translate'
					}
				},
				{
					"className": "col-xs-12",
					"type": "ui-select-single",
					"key": "secondaryGameType.id",
					"templateOptions": {
						"label": '',
						"optionsAttr": 'bs-options',
						"description": "",
						"valueProp": 'id',
						"labelProp": 'name',
						"placeholder": '',
						"ngOptions": 'ui-options',
						"options": []
					},
					controller: ['$scope', function ($scope) {
						gameTypesRest.findByDomainAndType(controller.selectedDomain, 'secondary')
						.then(function (gameTypes) {
							$scope.to.options = gameTypes;
						});
					}],
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.SECONDARY-GAME-TYPE.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.SECONDARY-GAME-TYPE.DESCRIPTION" | translate'
					}
				},
				{
					"className": "col-xs-12",
					"type": "ui-select-single",
					"key": "gameStudio.id",
					"templateOptions": {
						"label": '',
						"optionsAttr": 'bs-options',
						"description": "",
						"valueProp": 'id',
						"labelProp": 'name',
						"placeholder": '',
						"ngOptions": 'ui-options',
						"options": []
					},
					controller: ['$scope', function ($scope) {
						gameStudioRest.findByDomain(controller.selectedDomain)
							.then(function (gameStudio) {
								$scope.to.options = gameStudio;
							});
					}],
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-STUDIO.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-STUDIO.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "name",
					type: "input",
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: true,
						minlength: 2, maxlength: 255,
						focus: true
					},
					modelOptions: {
						updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
					},

					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
					},
					validators: {
						pattern: {
							expression: function ($viewValue, $modelValue, scope) {
								return /^[a-zA-Z0-9äÜéöÖüßáÁÄÉíÍñÑèùòøØåÅ\s,.'_()!:&-]*$/.test($viewValue);
							},
							message: '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PATTERN" | translate'
						}
					}
				},
				{
					className: "col-xs-12",
					key: "commercialName",
					type: "input",
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: true,
						minlength: 2, maxlength: 255,
						focus: true
					},
					modelOptions: {
						updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
					},

					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.DESCRIPTION" | translate'
					},
					validators: {
						pattern: {
							expression: function ($viewValue, $modelValue, scope) {
								return /^[a-zA-Z0-9äÜéöÖüßáÁÄÉíÍñÑèùòøØåÅ\s,.'_()!:&-]*$/.test($viewValue);
							},
							message: '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.PATTERN" | translate'
						}
					}
				},
				{
					className: "col-xs-12",
					key: "providerGameId",
					type: "input",
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: true,
						minlength: 2, maxlength: 255,
						focus: true
					},
					modelOptions: {
						updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
					},

					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.DESCRIPTION" | translate'
					},
					validators: {
						pattern: {
							expression: function ($viewValue, $modelValue, scope) {
								return /^[0-9a-z_\-.]+$/.test($viewValue);
							},
							message: '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.PATTERN" | translate'
						}
					}
				},
				{
					className: "col-xs-12",
					key: "supplierGameRewardGuid",
					type: "input",
					templateOptions: {
						label: "",
						description: "",
						placeholder: "",
						focus: true
					},
					modelOptions: {
						updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
					},

					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_REWARD_GUID.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_REWARD_GUID.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_REWARD_GUID.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "supplierGameGuid",
					type: "input",
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: false,
						focus: true
					},
					modelOptions: {
						updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
					},

					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_GUID.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_GUID.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_GUID.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "moduleSupplierId",
					type: "input",
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: false,
						focus: true
					},
					modelOptions: {
						updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
					},

					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.MODULE_SUPPLIER_ID.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.MODULE_SUPPLIER_ID.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.MODULE_SUPPLIER_ID.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "description",
					type: "textarea",
					templateOptions: {
						label: '',
						required: true
					},
					modelOptions: {
						updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "rtp",
					type: "ui-percentage-mask",
					optionsTypes: ['editable'],
					templateOptions : {
						label: "",
						description: "",
						required: false,
						hidesep: true,
						decimals: 2,
						placeholder: "rtp 100%",
						max: 100
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME.DESCRIPTION" | translate'
					},
				},
				{
					className: "form-row row",
					fieldGroup: [
						{
							key: "introductionDate",
							className: "form-check form-check-inline game-time-picker-formatter",
							type: "datepicker",
							optionsTypes: ['editable'],
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: false,
								formCheck: 'inline',
								datepickerOptions: {
									format: 'yyyy/MM/dd'

								},
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INTRODUCTION.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INTRODUCTION.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INTRODUCTION.DESCRIPTION" | translate'
							}
						},
						{
							className: "form-check form-check-inline",
							key: "introductionDate",
							type: "timepicker",
							optionsTypes: ['editable'],
							templateOptions: {
								description: "", placeholder: "",
								formCheck: 'inline',
								required: false,
								datepickerOptions: {
									format: 'HH:mm'
								},
							}
						}]
				},
				{
					className: "form-row row",
					fieldGroup: [
						{
							className: "form-check form-check-inline game-time-picker-formatter",
							key: "activeDate",
							type: "datepicker",
							optionsTypes: ['editable'],
							templateOptions: {
								formCheck: 'inline',
								label: "", description: "", placeholder: "active date",
								required: false,
								datepickerOptions: {
									format: 'yyyy/MM/dd'
								},
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.DESCRIPTION" | translate'
							},
							validators: {
								pattern: {
									expression: function ($viewValue, $modelValue, scope) {
										if ($viewValue)
											return (new Date()).getTime() >= new Date($viewValue).getTime();
										return true;
									},
									message: '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.PATTERN" | translate'
								}
							}
						},
						{
							className: "form-check form-check-inline",
							key: "activeDate",
							type: "timepicker",
							optionsTypes: ['editable'],
							templateOptions: {
								description: "", placeholder: "",
								formCheck: 'inline',
								required: false,
								datepickerOptions: {
									format: 'HH:mm'
								},
							}
						}]
				},
				{
					className: "form-row row",
					fieldGroup: [
						{
							className: "form-check form-check-inline game-time-picker-formatter",
							key: "inactiveDate",
							type: "datepicker",
							optionsTypes: ['editable'],
							templateOptions: {
								label: "", description: "", placeholder: "inactive date",
								formCheck: 'inline',
								required: false,
								datepickerOptions: {
									format: 'yyyy/MM/dd'
								},
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.DESCRIPTION" | translate'
							},
							validators: {
								pattern: {
									expression: function ($viewValue, $modelValue, scope) {
										if ($viewValue)
											return (new Date()).getTime() >= new Date($viewValue).getTime();
										return true;
									},
									message: '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.PATTERN" | translate'
								}
							}
						},
						{
							className: "form-check form-check-inline",
							key: "inactiveDate",
							type: "timepicker",
							optionsTypes: ['editable'],
							templateOptions: {
								description: "", placeholder: "",
								formCheck: 'inline',
								required: false,
								datepickerOptions: {
									format: 'HH:mm'
								},
							}
						}]
				},
				{
					className: "col-xs-12",
					key: "freeSpinEnabled",
					type: "checkbox2",
					templateOptions: {
						label: '',
						description: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_ENABLED.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_ENABLED.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_ENABLED.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "casinoChipEnabled",
					type: "checkbox2",
					templateOptions: {
						label: '',
						description: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.CASINO_CHIP_ENABLED.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.CASINO_CHIP_ENABLED.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.CASINO_CHIP_ENABLED.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "instantRewardEnabled",
					type: "checkbox2",
					templateOptions: {
						label: '',
						description: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_ENABLED.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_ENABLED.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_ENABLED.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "instantRewardFreespinEnabled",
					type: "checkbox2",
					templateOptions: {
						label: '',
						description: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_FREESPIN_ENABLED.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_FREESPIN_ENABLED.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_FREESPIN_ENABLED.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "freeSpinValueRequired",
					type: "checkbox2",
					templateOptions: {
						label: '',
						description: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_VALUE_REQUIRED.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_VALUE_REQUIRED.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_VALUE_REQUIRED.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "freeSpinPlayThroughEnabled",
					type: "checkbox2",
					templateOptions: {
						label: "",
						description: 'Does the game allow a Play Through associated to Free Spin Rewards'
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_PLAY_THROUGH_ENABLED.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_PLAY_THROUGH_ENABLED.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_PLAY_THROUGH_ENABLED.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "progressiveJackpot",
					type: "checkbox2",
					optionsTypes: ['editable'],
					templateOptions: {
						label: "",
						description: 'Has a progressive jackpot'
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROGRESSIVE_JACKPOT.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROGRESSIVE_JACKPOT.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROGRESSIVE_JACKPOT.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "networkedJackpotPool",
					type: "checkbox2",
					optionsTypes: ['editable'],
					templateOptions: {
						label: "",
						description: 'Is part of a networked jackpot pool'
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NETWORKED_JACKPOT_POOL.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NETWORKED_JACKPOT_POOL.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NETWORKED_JACKPOT_POOL.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "freeGame",
					type: "checkbox2",
					templateOptions: {
						label: "",
						description: 'The game does not use money to play'
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_GAME.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_GAME.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_GAME.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "liveCasino",
					type: "checkbox2",
					templateOptions: {
						label: "",
						description: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.LIVE_CASINO.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LIVE_CASINO.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.LIVE_CASINO.DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "localJackpotPool",
					type: "checkbox2",
					optionsTypes: ['editable'],
					templateOptions: {
						label: "",
						description: 'Is part of a local jackpot pool (exclusive to LiveScore Group Brands)'
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCAL_JACKPOT_POOL.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCAL_JACKPOT_POOL.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCAL_JACKPOT_POOL.DESCRIPTION" | translate'
					}
				}
			];
		
		controller.originalFields = angular.copy(controller.fields);
		controller.originalModel = angular.copy(controller.model); //TODO: this will need to happen in then when data is returned
		
		controller.resetModel = function() {
			controller.model = angular.copy(controller.originalModel);
		}

		controller.formatDate = function (date) {
			date === null ? new Date() : date;
			return $filter('date')(date, 'yyyy-MM-dd HH:mm');
		}
		controller.onSubmit = function() {
			console.debug(controller.model);
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			var gameSupplierId = (controller.model.gameSupplier != null) ? controller.model.gameSupplier.id : null;
			var primaryGameTypeId = (controller.model.primaryGameType != null) ? controller.model.primaryGameType.id : null;
			var secondaryGameTypeId = (controller.model.secondaryGameType != null) ? controller.model.secondaryGameType.id : null;
			var gameStudioId = (controller.model.gameStudio != null) ? controller.model.gameStudio.id : null;
			gamesRest.addGame(controller.model.url, controller.model.name,controller.model.commercialName,controller.model.providerGameId,
							  controller.model.description, controller.model.supplierGameGuid, controller.model.moduleSupplierId, controller.model.rtp,controller.model.introductionDate, controller.model.activeDate, controller.model.inactiveDate, controller.selectedDomain,
				              controller.model.freeSpinEnabled,
				              controller.model.casinoChipEnabled,
				              controller.model.instantRewardEnabled,
				              controller.model.instantRewardFreespinEnabled,
				              controller.model.freeSpinValueRequired,
							  controller.model.freeSpinPlayThroughEnabled,
							  gameSupplierId,
							  primaryGameTypeId,
							  secondaryGameTypeId,
							  gameStudioId,
							  controller.model.progressiveJackpot,
							  controller.model.networkedJackpotPool,
							  controller.model.localJackpotPool ,
							  controller.model.freeGame,
							  controller.model.liveCasino, controller.model.supplierGameRewardGuid).then(function(game) {
				notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
				$state.go("dashboard.gameEdit", {gameId: game.id, domainName: game.domain.name});
			}, function(response) {
				notify.error(response);
				console.error(response);
			});
		}
	}
]);
