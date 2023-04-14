'use strict';

angular.module('lithium')
.controller('ChangeAddressModal',
['$uibModalInstance', 'user', "UserRest", "ProfileRest", 'userFields', 'notify', 'type', '$filter', 'profile', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, user, UserRest, ProfileRest, userFields, notify, type, $filter, profile, errors, bsLoadingOverlayService) {
	var controller = this;
	controller.submitCalled = false;
	
	controller.options = {removeChromeAutoComplete:true};
	controller.model = user;
	controller.type = type;
	if (controller.model[type] !== null) {
		var tmp = {
			userId: user.id,
			addressType: type
		};
		angular.extend(controller.model[type], tmp);
	} else {
		controller.model[type] = {
			userId: user.id,
			addressType: type
		};
	}
	
	controller.fields = [
		userFields.address(type, 'UI_NETWORK_ADMIN.PLAYER.ADDRESS.'+($filter('uppercase')(type))+'.DESC', false)
	];
	
	controller.submit = function() {
		controller.referenceId = 'address-save-overlay';
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		controller.submitCalled = true;
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		if (!profile) {
			UserRest.saveAddress(user.domain.name, controller.model[type]).then(function(response) {
				$uibModalInstance.close(response);
			}).catch(
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.ADDRESSSAVE", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		} else {
			ProfileRest.saveAddress(controller.model[type]).then(function(response) {
				$uibModalInstance.close(response);
			}).catch(
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.ADDRESSSAVE", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		}
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);