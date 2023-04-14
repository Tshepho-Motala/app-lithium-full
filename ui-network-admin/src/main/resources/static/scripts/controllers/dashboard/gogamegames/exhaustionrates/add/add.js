'use strict'

angular.module('lithium').controller('GoGameExhaustionRatesAddController', ['$translate', '$uibModalInstance', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function($translate, $uibModalInstance, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.fields = [
			{
				key: 'pbCents',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Player Balance Cents',
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
					options: [],
					focus: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//				},
				controller: ['$scope', function($scope) {
					var options = [];
					gogameGamesRest.findAll().then(function(response) {
						$scope.to.options = response.plain();
					});
				}]
			}, {
				key: 'startLedgerEntryId',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Start Ledger Entry ID',
					description: "",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: ''
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
//				}
			}
		];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			gogameGamesRest.addExhaustionRateTest(controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success('Exhaustion rate test added. Processing will begin shortly');
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
				
			}).catch(function(error) {
				notify.error('Exhaustion rate test could not be added');
				errors.catch('', false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);