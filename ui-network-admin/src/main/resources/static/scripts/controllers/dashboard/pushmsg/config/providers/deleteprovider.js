'use strict';

angular.module('lithium')
	.controller('DeleteProviderController', ["domainProvider", "rest-pushmsg", "notify", "errors", "$uibModalInstance",
	function(domainProvider, pushmsgRest, notify, errors, $uibModalInstance) {
		var controller = this;
		
		controller.cancel = function() {
			notify.success("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.DELETE.CANCEL");
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.doDelete = function() {
			controller.deleteConfirm = null;
			pushmsgRest.domainProviderDeleteFull(domainProvider).then(function(domainProvider2) {
				notify.success("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.DELETE.SUCCESS");
				$uibModalInstance.close(domainProvider2);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.DELETE.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);