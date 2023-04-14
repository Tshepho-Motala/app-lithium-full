'use strict';

angular.module('lithium')
.controller('UserAddGroupModal', ['$uibModalInstance', 'user', "rest-group", 'userFields', 'notify', function ($uibModalInstance, user, groupRest, userFields, notify) {

	var controller = this;
	
	if (!user.groups) user.groups = [];
	
	groupRest.list(user.domain.name).then(function(result) {
		
		controller.groups = result;
		
		angular.forEach(user.groups, function(group) {
			controller.groups = controller.groups.filter( function( el ) {
				return el.id != group.id;
			});
		});
		
	});

	controller.selectGroup = function(group) {
		user.customPOST(group.id, "addgroup").then(function(response) {
			notify.success("Group added successfully");
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again");
		});
		
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
	
}]);

