'use strict'

angular.module('lithium').controller('GoGameGamesAddController', ['game', '$userService', '$translate', '$uibModalInstance', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', 'rest-accounting-internal',
	function(game, $userService, $translate, $uibModalInstance, $filter, $state, $scope, errors, notify, $q, gogameGamesRest, accountingInternalRest) {
		var controller = this;

		controller.currencies = [];

		if (game !== null) {
			controller.adding = false;
			controller.model = angular.copy(game);
		} else {
			controller.adding = true;
			controller.model = { domainName: undefined, engineId: undefined };
		}

		controller.fields = [{
			key : "domainName",
			type : "ui-select-single",
			templateOptions : {
				label : "Domain",
				required : true,
				description : "The domain that you are creating the game for",
				valueProp : 'name',
				labelProp : 'name',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder : '',
				options : [],
				focus: true
			},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//				},
			controller: ['$scope', function($scope) {
				$scope.to.options = $userService.domainsWithRole("GOGAMEGAMES_GAMES_*");
				if (game !== null) $scope.to.disabled = true;
			}]
		},
		{
			key: 'id',
			type: 'ui-number-mask',
			optionsTypes: ['editable'],
			templateOptions: {
				label: 'Game ID',
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '1',
				max: '',
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//			}
		}, {
			key: "name",
			type: "input",
			templateOptions: {
				label: "Name",
				description: "The name of the game",
				placeholder: "",
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
//				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
//			}
		}, {
			key: "url",
			type: "input",
			templateOptions: {
				label: "URL",
				description: "The url of the game",
				placeholder: "",
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
//				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
//			}
		}, {
			key: "description",
			type: "textarea",
			templateOptions: {
				label: 'Description',
				description: "Provide a short description of the game",
				placeholder: ""
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
//				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
//			}
		}, {
			key: 'currencyCode',
			type: 'ui-select-single',
			templateOptions : {
				label: "Currency",
				description: "",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [],
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.DESCRIPTION" | translate'
//			}
			controller: ['$scope', function($scope) {
				if (controller.model.domainName !== undefined) {
					accountingInternalRest.findDomainCurrencies(controller.model.domainName).then(function(response) {
						console.log("Got currencies", response.plain());
						controller.currencies = response.plain();
						var c = [];
						for (var i = 0; i < response.plain().length; i++) {
							c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
						}
						$scope.to.options = c;
					});
				}
			}]
		}, {
			key: 'currencyDivisor',
			type: 'ui-number-mask',
			optionsTypes: ['editable'],
			templateOptions: {
				label: 'Currency Divisor',
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '1',
				max: '',
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//			}
		}, {
			key: 'payoutCurrencyCode',
			type: 'ui-select-single',
			templateOptions : {
				label: "Payout Currency",
				description: "",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [],
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.DESCRIPTION" | translate'
//			}
			controller: ['$scope', function($scope) {
				if (controller.model.domainName !== undefined) {
					accountingInternalRest.findDomainCurrencies(controller.model.domainName).then(function(response) {
						console.log("Got currencies", response.plain());
						controller.currencies = response.plain();
						var c = [];
						for (var i = 0; i < response.plain().length; i++) {
							c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
						}
						$scope.to.options = c;
					});
				}
			}]
		}, {
			key: 'payoutCurrencyDivisor',
			type: 'ui-number-mask',
			optionsTypes: ['editable'],
			templateOptions: {
				label: 'Payout Currency Divisor',
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '1',
				max: '',
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//			}
		}, {
			key: 'real',
			type: 'ui-select-single',
			templateOptions : {
				label: "Real",
				description: "Is the game real or virtual?",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: [{value: true, label: 'Real'}, {value: false, label: 'Virtual'}],
				required: true
			}
		}, {
			key: 'engineId',
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
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//			},
			controller: ['$scope', function($scope) {
				gogameGamesRest.findAllEngines().then(function(response) {
					$scope.to.options = response.plain();
				});
				if (game !== null) $scope.to.disabled = true;
			}]
		}, {
			key: 'mathModelId',
			type: 'ui-select-single',
			templateOptions: {
				label: 'Math Model',
				description: 'Choose the math model',
				valueProp: 'id',
				labelProp: 'name',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder: '',
				options: [],
				focus: true
			},
			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate',
				'templateOptions.required': function ($viewValue, $modelValue, scope) {
					return controller.adding;
				}
			},
			controller: ['$scope', function($scope) {
				var options = [];
				if (controller.model.engineId !== undefined) {
					gogameGamesRest.findMathModelsByEngine(controller.model.engineId).then(function(response) {
						var r = response.plain();
						for (var i = 0; i < r.length; i++) {
							options.push({ id: r[i].id, name: r[i].current.name});
						}
						$scope.to.options = options;
					});
				}
			}]
		}, {
				key: 'defaultStakeCents',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Default Stake Cents',
					description: "Set this property to control the game's default selected stake when a player opens the game for the first time. Leave blank for the default (lowest stake). Nb. Make sure there is a valid game ledger assigned to the game with the exact total play, else this would be ignored.",
					decimals: 0,
					hidesep: true,
					neg: false,
					required: false
				},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//			}
			}];

		controller.setCurrencies = function() {
			if (controller.model.domainName !== undefined) {
				controller.model.currencyCode = null;
				accountingInternalRest.findDomainCurrencies(controller.model.domainName).then(function(response) {
					console.log("Got currencies", response.plain());
					controller.currencies = response.plain();
					var c = [];
					for (var i = 0; i < response.plain().length; i++) {
						c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
					}
					controller.fields[5].templateOptions.options = c;
					controller.fields[7].templateOptions.options = c;
				});
			}
		}

		controller.setMathModels = function() {
			if (controller.model.engineId !== undefined) {
				var options = [];
				gogameGamesRest.findMathModelsByEngine(controller.model.engineId).then(function(response) {
					var r = response.plain();
					for (var i = 0; i < r.length; i++) {
						options.push({ id: r[i].id, name: r[i].current.name});
					}
					controller.fields[11].templateOptions.options = options;
				});
			}
		}

		$scope.$watch(function() { return controller.model.domainName; }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.setCurrencies();
			}
		});

		$scope.$watch(function() { return controller.model.engineId; }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.model.mathModelId = undefined;
				controller.setMathModels();
			}
		});

		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			if (game !== null) {
				gogameGamesRest.update(game.id, controller.model).then(function(response) {
					if (response._status === 0) {
						notify.success('Game updated successfully');
						$uibModalInstance.close(response);
					} else {
						notify.warning(response._message);
					}
				}).catch(function(error) {
					notify.error('Game could not be updated');
					errors.catch('', false)(error)
				});
			} else {
				gogameGamesRest.add(controller.model).then(function(response) {
					if (response._status === 0) {
						notify.success('Game added successfully');
						$uibModalInstance.close(response);
					} else {
						notify.warning(response._message);
					}
				}).catch(function(error) {
					notify.error('Game could not be added');
					errors.catch('', false)(error)
				});
			}
		}

		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);