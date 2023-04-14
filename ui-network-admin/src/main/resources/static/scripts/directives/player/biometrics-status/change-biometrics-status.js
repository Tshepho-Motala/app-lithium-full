'use strict';

angular.module('lithium')
.controller('ChangeBiometricsStatusModal', ['$uibModalInstance', '$scope', 'user', "UserRest", 'userFields', 'notify',  '$translate',
function ($uibModalInstance, $scope,  user, userRest, userFields, notify, $translate) {
	var vm = this;
	vm.submitCalled = false;
	vm.options = {removeChromeAutoComplete:true};
	vm.model = user;

	vm.fields = [
		userFields.biometricsStatus,
		userFields.comment("biometricsStatusComment",
			$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME"),
			$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER"),
			$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION"),
			true)
	];


	vm.submit = function() {
		vm.submitCalled = true;
		if (vm.form.$invalid) {
			angular.element("[name='" + vm.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}

		var statusUpdate = {
			userId: user.id,
			statusName: vm.model.biometricsStatus,
			comment: vm.model.biometricsStatusComment
		}
		userRest.saveBiometricsStatus(user.domain.name, statusUpdate).then(function(response) {
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
	};

	vm.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);