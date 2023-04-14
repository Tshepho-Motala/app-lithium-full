'use strict';

angular.module('lithium')
.controller('UserAddModal', ['$uibModalInstance', 'domain', "UserRest", 'userFields', 'notify', function ($uibModalInstance, domain, UserRest, userFields, notify) {

	var controller = this;
	
	controller.options = {};
	controller.model = { domainName: domain.name };

	controller.fields = 
	[
		userFields.username(domain),
		userFields.firstName, 
		userFields.lastName,
		userFields.newPassword,
		userFields.confirmPassword,
		userFields.country,
		userFields.email
	];

	
	controller.submit = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		controller.model.password = controller.model.newPassword;
		UserRest.add(domain.name, controller.model).then(function(response) {
			if (response._status === 403) {
				notify.error(response._message);
			} else {
				notify.success("The user was added successfully.");
				$uibModalInstance.close(response);
			}
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
		
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
	
}]);

