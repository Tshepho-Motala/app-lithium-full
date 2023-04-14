'use strict';

angular.module('lithium')
	.controller('UserView', ["domain", "domainSettings", "user", "userFields", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify", "UserRest", "$scope",
	function(domain, domainSettings, user, userFields, $uibModal, $translate, $log, $dt, $state, $rootScope, notify, UserRest, $scope) {
		var controller = this;
		
		controller.user = user;
		controller.domain = domain;
		
		controller.residentialAddress = {
			box: "info",
			title: "Residential Address",
			type: 'residentialAddress',
			userId: user.id,
			domainName: domain.name
		}
		controller.postalAddress = {
			box: "info",
			title: "Postal Address",
			type: 'postalAddress',
			userId: user.id,
			domainName: domain.name
		}
		controller.password = {
			box: "success",
			title: "Change Password",
			userId: user.id,
			domainName: domain.name
		}
		controller.personalinfo = {
			box: "info",
			title: "Personal Info",
			userId: user.id,
			profile: true,
			domainName: domain.name,
			domainSettings: domainSettings
		}
		
		controller.status = {
			box: "default",
			title: "Status",
			userId: user.id,
			domainName: domain.name,
			domainSettings: domainSettings
		}
		
		controller.addGroup = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/users/user/addgroup/addgroup.html',
				controller: 'UserAddGroupModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					user: function () { return user; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/users/user/addgroup/addgroup.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (group) {
				$state.reload();
			});
			
		};
		controller.removeGroup = function(group) {
			user.customPOST(group.id, "removegroup").then(function(response) {
				notify.success("User removed from the selected group");
				$state.reload();
			});
		};
		
		controller.changelogs = {
			domainName: domain.name,
			entityId: user.id,
			restService: UserRest,
			reload: 0
		}
}]);
