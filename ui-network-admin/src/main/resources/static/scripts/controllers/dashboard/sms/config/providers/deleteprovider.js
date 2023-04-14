'use strict';

angular.module('lithium')
	.controller('DeleteProviderController', ["domainProvider", "rest-sms", "notify", "errors", "$uibModalInstance",
	function(domainProvider, smsRest, notify, errors, $uibModalInstance) {
		var controller = this;
		
		controller.cancel = function() {
			notify.success("UI_NETWORK_ADMIN.SMS.PROVIDERS.DELETE.CANCEL");
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.doDelete = function() {
			controller.deleteConfirm = null;
			smsRest.domainProviderDeleteFull(domainProvider).then(function(domainProvider2) {
				notify.success("UI_NETWORK_ADMIN.SMS.PROVIDERS.DELETE.SUCCESS");
				$uibModalInstance.close(domainProvider2);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.SMS.PROVIDERS.DELETE.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);