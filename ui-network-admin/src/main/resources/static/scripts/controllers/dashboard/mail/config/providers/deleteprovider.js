'use strict';

angular.module('lithium')
	.controller('DeleteProviderController', ["domainProvider", "mailRest", "notify", "errors", "$uibModalInstance",
	function(domainProvider, mailRest, notify, errors, $uibModalInstance) {
		var controller = this;
		
		controller.cancel = function() {
			notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.DELETE.CANCEL");
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.doDelete = function() {
			controller.deleteConfirm = null;
			mailRest.domainProviderDeleteFull(domainProvider).then(function(domainProvider2) {
				notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.DELETE.SUCCESS");
				$uibModalInstance.close(domainProvider2);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MAIL.PROVIDERS.DELETE.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);