'use strict';

angular.module('lithium')
.controller('AddCommentModal',
['$uibModalInstance', 'domainName', 'entityId', 'restService', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, domainName, entityId, restService, notify, errors, bsLoadingOverlayService) {
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
		restService.comment(domainName, entityId, controller.model.comment).then(function(response) {
			$uibModalInstance.close(response);
		}).catch(
			errors.catch("UI_NETWORK_ADMIN.COMMENT.ERRORS.SAVE", false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);