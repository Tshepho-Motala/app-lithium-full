'use strict';

angular.module('lithium')
.controller('ChangePasswordModal',
['$uibModalInstance', 'user', "UserRest", "ProfileRest", 'userFields', 'notify', 'profile', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, user, UserRest, ProfileRest, userFields, notify, profile, errors, bsLoadingOverlayService) {
	var controller = this;
	
	controller.options = {};
	controller.model = { };

	controller.fields = [
		userFields.newPassword,
		userFields.confirmPassword,
	];
	
	controller.referenceId = 'changepassword-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}
		if (!profile) {
			UserRest.savePassword(user.domain.name, user.id, controller.model.newPassword).then(function(response) {
				$uibModalInstance.close(response);
			}).catch(
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PASSWORDSAVE", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		} else {
			ProfileRest.savePassword(user.id, controller.model.newPassword).then(function(response) {
				$uibModalInstance.close(response);
			}).catch(
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PASSWORDSAVE", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		}
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);