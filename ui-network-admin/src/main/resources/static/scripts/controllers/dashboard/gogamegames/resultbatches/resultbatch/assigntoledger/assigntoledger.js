'use strict'

angular.module('lithium').controller('GoGameResultBatchAssignToLedgerModal', ['resultBatch', '$translate', '$uibModalInstance', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(resultBatch, $translate, $uibModalInstance, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.fields = [{
			key: 'ledger',
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
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//			},
			controller: ['$scope', function($scope) {
				gogameGamesRest.findEligableLedgersForResultBatch(resultBatch.id).then(function(response) {
					var r = response.plain();
					console.log(r);
					var options = [];
					for (var i = 0; i < r.length; i++) {
						options.push({id: r[i].id, name: r[i].domain.name + ' - ' + r[i].name});
					}
					$scope.to.options = options;
				});
			}]
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			gogameGamesRest.assignToLedger(resultBatch.id, controller.model.ledger).then(function(response) {
				if (response._status === 0) {
					notify.success('Result batch will be assigned to ledger');
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error('Result batch could not be assigned to ledger');
				errors.catch('', false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);