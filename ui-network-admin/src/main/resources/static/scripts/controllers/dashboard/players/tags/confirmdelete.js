'use strict';

angular.module('lithium')
.controller('ConfirmNoteDeleteModal',
['$uibModalInstance', 'entityId', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, entityId, userRest, notify, errors, bsLoadingOverlayService) {
	var controller = this;
	
	controller.referenceId = 'addcomment-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		userRest.removeTag(entityId).then(function(response) {
			$uibModalInstance.close(response);
		}).catch(
			errors.catch('UI_NETWORK_ADMIN.TAG.NOTIFY.RESPONSE.ERROR', false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
