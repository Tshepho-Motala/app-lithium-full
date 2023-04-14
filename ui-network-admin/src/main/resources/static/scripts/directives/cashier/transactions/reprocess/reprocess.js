'use strict';

angular.module('lithium')
.controller('ReprocessModal',
['$uibModalInstance', 'ownerGuid', 'rest-cashier', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, ownerGuid, cashierRest, notify, errors, bsLoadingOverlayService) {
	var controller = this;
	
	controller.options = {};
	controller.model = {};

	controller.fields = [
		{
			key : "comment",
			type : "textarea",
			className : "col-xs-12",
			templateOptions : {
				label : "",
				required : true,
				minlength: 5, maxlength: 65535
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME" | translate'
			}
		}
	];
	
	controller.referenceId = 'addcomment-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}
		cashierRest.withdrawalReprocess(ownerGuid, controller.model.comment).then(function(response) {
			if (response != undefined && response >=0 ) {
				notify.success("Reprocess command was sent successfully for "+ response+" transactions");
			} else {
				errors.catch("UI_NETWORK_ADMIN.PLAYER.WITHDRAWAL_REPROCESS.ERROR", false)
			}
			$uibModalInstance.close(response);
		}).catch(
			errors.catch("UI_NETWORK_ADMIN.PLAYER.WITHDRAWAL_REPROCESS.ERROR", false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);