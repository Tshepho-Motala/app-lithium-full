'use strict';

angular.module('lithium')
	.controller('DeleteMethodController', ["dm", "rest-cashier", "notify", "errors", "$uibModalInstance",
	function(dm, cashierRest, notify, errors, $uibModalInstance) {
		var controller = this;
		
		controller.cancel = function() {
			notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.DELETE.CANCEL");
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.doDelete = function() {
			controller.deleteConfirm = null;
			cashierRest.domainMethodDeleteFull(dm).then(function(dm2) {
				notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.DELETE.SUCCESS");
				$uibModalInstance.close(dm2);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.CASHIER.METHODS.DELETE.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);