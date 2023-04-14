'use strict';

angular.module('lithium')
.controller('ConfirmNoteDeleteModal',
['$uibModalInstance', 'entityId', 'rest-casino', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, entityId, casinoRest, notify, errors, bsLoadingOverlayService) {
	var controller = this;
	
	controller.referenceId = 'addcomment-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		casinoRest.removeBonus(entityId).then(function(response) {
			notify.success('UI_NETWORK_ADMIN.BONUS.DELETE.NOTIFY.SUCCESS');
			$uibModalInstance.close(response);
		}).catch(
			errors.catch('UI_NETWORK_ADMIN.BONUS.DELETE.NOTIFY.ERROR', false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
