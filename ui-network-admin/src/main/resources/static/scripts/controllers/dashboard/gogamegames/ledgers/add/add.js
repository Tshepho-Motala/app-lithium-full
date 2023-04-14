'use strict'

angular.module('lithium').controller('GoGameLedgersAddController', ['ledger', '$translate', '$uibModal', '$filter', '$uibModalInstance', '$userService', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', 'rest-accounting-internal',
	function(ledger, $translate, $uibModal, $filter, $uibModalInstance, $userService, $state, $scope, errors, notify, $q, gogameGamesRest, accountingInternalRest) {
		var controller = this;
		
		if (ledger !== null) {
			controller.model = ledger;
			controller.adding = false;
		} else {
			controller.model = { domain: { name: undefined } };
			controller.adding = true;
		}
		
		controller.fields = [
			{
				key : "domain.name",
				type : "ui-select-single",
				templateOptions : {
					label : "Domain",
					required : true,
					optionsAttr: 'bs-options',
					description : "The domain that you are creating the ledger for",
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
					$scope.to.options = $userService.domainsWithRole("GOGAMEGAMES_LEDGERS_*");
					if (ledger !== null) $scope.to.disabled = true;
				}]
			}, {
				key: "name",
				type: "input",
				templateOptions: {
					label: 'Name',
					description: "",
					placeholder: "",
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
//				}
				controller: ['$scope', function($scope) {
					if (ledger !== null) $scope.to.disabled = true;
				}]
			}, {
				key: "description",
				type: "textarea",
				templateOptions: {
					label: 'Description',
					description: "",
					placeholder: ""
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
//				}
			}, {
				key: 'currencyCode', 
				type: 'ui-select-single',
				templateOptions : {
					label: "Currency",
					description: "",
					optionsAttr: 'bs-options',
					valueProp: 'value',
					labelProp: 'label',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.DESCRIPTION" | translate'
//				}
				controller: ['$scope', function($scope) {
					if (controller.model.domain !== undefined && controller.model.domain.name !== undefined) {
						accountingInternalRest.findDomainCurrencies(controller.model.domain.name).then(function(response) {
							console.log("Got currencies", response.plain());
							var c = [];
							for (var i = 0; i < response.plain().length; i++) {
								c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
							}
							$scope.to.options = c;
						});
					}
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
		]
		
		controller.setCurrencies = function() {
			if (controller.model.domain !== undefined && controller.model.domain.name !== undefined) {
				controller.model.currencyCode = null;
				accountingInternalRest.findDomainCurrencies(controller.model.domain.name).then(function(response) {
					console.log("Got currencies", response.plain());
					var c = [];
					for (var i = 0; i < response.plain().length; i++) {
						c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
					}
					controller.fields[3].templateOptions.options = c;
				});
			}
		}
		
		$scope.$watch(function() { return controller.model.domain.name; }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.setCurrencies();
			}
		});
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			var ledgerBasic = {
				name: controller.model.name,
				description: controller.model.description,
				currencyCode: controller.model.currencyCode,
				totalPlay: controller.model.totalPlay,
				domainName: controller.model.domain.name,
				engineId: controller.model.engine.id
			};
			
			if (ledger !== null) {
				gogameGamesRest.updateLedger(ledger.id, ledgerBasic).then(function(response) {
					if (response._status === 0) {
						notify.success('Ledger updated successfully');
						$uibModalInstance.close(response);
					} else {
						notify.warning(response._message);
					}
				}).catch(function(error) {
					notify.error('Ledger could not be updated');
					errors.catch('', false)(error)
				});
			} else {
				gogameGamesRest.addLedger(ledgerBasic).then(function(response) {
					if (response._status === 0) {
						notify.success('Ledger added successfully');
						$uibModalInstance.close(response);
					} else {
						notify.warning(response._message);
					}
				}).catch(function(error) {
					notify.error('Ledger could not be updated');
					errors.catch('', false)(error)
				});
			}
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);