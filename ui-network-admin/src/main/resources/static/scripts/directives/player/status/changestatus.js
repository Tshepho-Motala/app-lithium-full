'use strict';

angular.module('lithium')
.controller('ChangeStatusModal', ['$uibModalInstance', '$scope', 'statuses', 'user', "UserRest", 'userFields', 'notify', 'StatusRest', '$translate', 'excludeStatusReasons',
function ($uibModalInstance, $scope, statuses, user, UserRest, userFields, notify, statusRest, $translate, excludeStatusReasons) {
	var vm = this;
	vm.submitCalled = false;
	vm.options = {removeChromeAutoComplete:true};
	vm.model = user;

	vm.fields = [
		userFields.status,
		userFields.statusReason(vm.model.status.id, user.domain.name, statuses, 'id', excludeStatusReasons),
		userFields.comment("comment",
			$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME"),
			$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER"),
			$translate.instant("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION"))
	];

	vm.fields[0].templateOptions.onChange =  function() {
		vm.model.statusReason = null;
		vm.fields[1] = userFields.statusReason(vm.model.status, user.domain.name, statuses, 'id', excludeStatusReasons);
	}

	vm.submit = function() {
		vm.submitCalled = true;
		if (vm.form.$invalid) {
			angular.element("[name='" + vm.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}

		var statusId = (vm.model.status instanceof Object) ? vm.model.status.id : vm.model.status;
		var statusReasonId = (vm.model.statusReason instanceof Object) ? vm.model.statusReason.id : vm.model.statusReason;
		var statusUpdate = {
			userId: user.id,
			statusId: statusId,
			statusReasonId: statusReasonId,
			comment: vm.model.comment
		}
		UserRest.saveStatus(user.domain.name, statusUpdate).then(function(response) {
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
	};

	vm.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);