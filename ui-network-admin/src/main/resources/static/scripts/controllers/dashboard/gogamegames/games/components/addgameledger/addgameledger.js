'use strict';

angular.module('lithium').controller('GoGameGamesGameLedgersController', ["game", "errors", "$scope", "notify", "$uibModalInstance", "GoGameGamesRest",
function (game, errors, $scope, notify, $uibModalInstance, gogameGamesRest) {
	var controller = this;
	
	controller.model = { depositAmountCents: null, depositedDaysAgo: null };
	
	controller.eligableLedgers = null;
	
	controller.fields = [
		{
			key: 'ledgerId',
			type: 'ui-select-single',
			templateOptions: {
				label: 'Ledger',
				required: true,
				optionsAttr: 'bs-options',
				description: "",
				valueProp: 'id',
				labelProp: 'name',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder: '',
				options: []
			},
//			expressionProperties: {
//				'templateOptions.labgitel': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//			},
			controller: ['$scope', function($scope) {
				gogameGamesRest.findEligableLedgersForGame(game.gameId.gameId, game.gameId.domain.name).then(function(response) {
					controller.eligableLedgers = response.plain();
					$scope.to.options = controller.eligableLedgers;
				});
			}]
		}, {
			key: "depositAmountCents",
			type: "ui-money-mask",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "Deposit Amount",
				description: "The amount of money the player needs to deposit in order to gain access into this ledger. Leave empty if there is no requirement.",
				addFormControlClass: true,
				required: false
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.CURRENCY.MINAMOUNT.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.CURRENCY.MINAMOUNT.DESCRIPTION" | translate'
//			}
			hideExpression: function($viewValue, $modelValue, scope) {
				if (!angular.isDefined(scope.model.ledgerId)) {
					return true;
				} else {
					var selectedLedger = null;
					for (var i = 0; i < controller.eligableLedgers.length; i++) {
						if (controller.eligableLedgers[i].id === controller.model.ledgerId) {
							selectedLedger = controller.eligableLedgers[i];
							break;
						}
					}
					return !(selectedLedger.engine.id === 5);
				}
			}
		}, {
			key: 'depositedDaysAgo',
			type: 'ui-number-mask',
			optionsTypes: ['editable'],
			templateOptions: {
				label: 'Deposited Days Ago',
				description: "Deposits made within x days ago. These deposits are accumulated to check against the sum above. Leave empty if there is no requirement, or if deposit amount is entered, but no specific Deposited Days Ago requirement (i.e should check All Time).",
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '1',
				max: '',
				required: false
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//			}
			hideExpression: function($viewValue, $modelValue, scope) {
				if (!angular.isDefined(scope.model.ledgerId)) {
					return true;
				} else {
					var selectedLedger = null;
					for (var i = 0; i < controller.eligableLedgers.length; i++) {
						if (controller.eligableLedgers[i].id === controller.model.ledgerId) {
							selectedLedger = controller.eligableLedgers[i];
							break;
						}
					}
					return !(selectedLedger.engine.id === 5);
				}
			}
		}
	];
	
	controller.onSubmit = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		var depositAmountCents = null;
		if (controller.model.depositAmountCents !== undefined && controller.model.depositAmountCents !== null) {
			depositAmountCents = controller.model.depositAmountCents * 100;
			depositAmountCents = depositAmountCents + '';
			depositAmountCents = depositAmountCents.replace(',', '');
			depositAmountCents = depositAmountCents.replace('.', '');
		}
		
		gogameGamesRest.addGameLedger(game.gameId.gameId, game.gameId.domain.name, controller.model.ledgerId, depositAmountCents, controller.model.depositedDaysAgo).then(function(response) {
			if (response._status === 0) {
				notify.success('Game ledger added successfully');
				$uibModalInstance.close(response);
			} else {
				notify.warning(response._message);
			}
		}).catch(function(error) {
			notify.error('Game ledger could not be added');
			errors.catch('', false)(error)
		});
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);