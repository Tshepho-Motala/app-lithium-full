'use strict'

angular.module('lithium').controller('GoGameResultSimulationsAddController', ['$translate', '$uibModalInstance', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function($translate, $uibModalInstance, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.fields = [
			{
				key: 'mathModelId',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Math Model',
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
				key: 'quantity',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Quantity',
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
					max: '',
					required: true
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
			
			gogameGamesRest.addResultSimulation(controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success('Result simulation added successfully');
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error('Result simulation could not be added');
				errors.catch('', false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);