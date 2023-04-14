'use strict';

angular.module('lithium')
.controller('ConfirmNoteDeleteModal',
['$uibModalInstance', 'domainName', 'entityId', 'isDelete', 'restService', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, domainName, entityId, isDelete, restService, notify, errors, bsLoadingOverlayService) {
	var controller = this;
	controller.isDelete = isDelete;
	
	controller.referenceId = 'addcomment-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		restService.setDeleted(entityId, controller.isDelete).then(function(response) {
			$uibModalInstance.close(response);
		}).catch(
			errors.catch('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.DELETED.CHANGE.ERROR', false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
