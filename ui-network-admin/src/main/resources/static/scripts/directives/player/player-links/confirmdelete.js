'use strict';

angular.module('lithium')
.controller('ConfirmNoteDeleteModal',
['$uibModalInstance', 'updatePlayerLinkData', 'isDelete', 'restService', 'errors', 'bsLoadingOverlayService' ,
function ($uibModalInstance, updatePlayerLinkData, isDelete, restService, errors, bsLoadingOverlayService) {
	var controller = this;
	controller.isDelete = isDelete;
	controller.isSecondaryData = false;
	controller.secondaryUserLinkData = {}
	if(updatePlayerLinkData.secondaryUser.guid !== undefined) {
		restService.playerLinks(updatePlayerLinkData.secondaryUser.guid).then(function(response){
			if (response !== undefined ) {
				controller.responseObj = response.plain();
				for (let i = 0; i < controller.responseObj.length; i++) {
					let secondaryUserMatchesPrimaryUser = updatePlayerLinkData.secondaryUser.guid === controller.responseObj[i].primaryUser.guid
					let primaryUserMatchesSecondaryUser = updatePlayerLinkData.primaryUser.guid === controller.responseObj[i].secondaryUser.guid

					if ( secondaryUserMatchesPrimaryUser || primaryUserMatchesSecondaryUser ) {
						for (let j = 0; j < controller.responseObj.length; j++) {
							let secondaryUserLinkTypeIdMatchesSecondary = controller.responseObj[j].userLinkType.id == updatePlayerLinkData.userLinkType.id;
							let secondaryUserLinkNoteMatchesSecondary = controller.responseObj[j].userLinkType.code === updatePlayerLinkData.userLinkType.code;
							let secondaryUserLinkTypeCodeMatchesSecondary = controller.responseObj[j].userLinkType.linkNote === updatePlayerLinkData.userLinkType.linkNote;

							if ( ( secondaryUserLinkTypeIdMatchesSecondary && secondaryUserLinkTypeCodeMatchesSecondary) && secondaryUserLinkNoteMatchesSecondary ) {
								controller.isSecondaryData = true;
								controller.secondaryUserLinkData = controller.responseObj[j];
								break;
							}
						}
					}
				}
			}
		});
	}

	controller.referenceId = 'confirmdelete-overlay';
	controller.primaryaryUserLinkNoteId = updatePlayerLinkData.id;
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		restService.updateUserLink(controller.primaryaryUserLinkNoteId , updatePlayerLinkData.linkNote, isDelete).then(function(response) {
			if(controller.isSecondaryData) {
				restService.updateUserLink(controller.secondaryUserLinkData.id, updatePlayerLinkData.linkNote, isDelete).then(function(response) {
				}).catch(
					errors.catch('UI_NETWORK_ADMIN.PLAYER.LINK.CONFIRM.ERROR', false)
				)
			}
			$uibModalInstance.close(response);
		}).catch(
			errors.catch('UI_NETWORK_ADMIN.PLAYER.LINK.CONFIRM.ERROR', false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
