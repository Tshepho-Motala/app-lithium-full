'use strict';

angular.module('lithium')
	.controller('CashierTranCancelController',
		['$uibModalInstance', '$stateParams', 'rest-cashier', 'notify', 'bsLoadingOverlayService', 'errors', 'transaction',
		function ($uibModalInstance, $stateParams, cashierRest, notify, bsLoadingOverlayService, errors, transaction) {
			var cancelController = this;
			cancelController.submitCalled = false;
			cancelController.options = {removeChromeAutoComplete:true};
			cancelController.transaction = transaction;
			cancelController.fields = [{
				className: "col-xs-12",
				key: "transaction",
				type: "ui-select-single",
				templateOptions : {
					required: false,
					dataAllowClear: false,
					label: "Transaction Cancel",
					description: "",
					placeholder: "Select reason of Cancel",
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				}
			}];

			cancelController.submit = function() {
				var e = document.getElementById("cancelReason");
				var comment = e.options[e.selectedIndex].text;
				$uibModalInstance.close(comment);
			};

			cancelController.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};
		}]);
