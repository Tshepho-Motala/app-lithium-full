'use strict';

angular.module('lithium')
	.controller('DeleteProcessorController', ["dmp", "rest-cashier", "notify", "errors", "$uibModalInstance",
	function(dmp, cashierRest, notify, errors, $uibModalInstance) {
		var controller = this;
		
		controller.cancel = function() {
			notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.DELETE.CANCEL");
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.doDelete = function() {
			controller.deleteConfirm = null;
			cashierRest.domainMethodProcessorDeleteFull(dmp).then(function(dmp2) {
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.DELETE.SUCCESS");
				$uibModalInstance.close(dmp2);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.DELETE.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);