'use strict';

angular.module('lithium')
	.controller('CashierDomainProfileUsersController', ["profile", "rest-cashier", "$dt", "$translate", "$uibModal", "$stateParams", "notify", "errors", "$scope", 
	function(profile, cashierRest, $dt, $translate, $uibModal, $stateParams, notify, errors, $scope) {
		var controller = this;
		$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.HEADER.PROFILEUSERS");
		
		var baseUrl = "services/service-cashier/cashier/user/profile/"+profile.id+"/table?1=1";
		controller.usersTable = $dt.builder()
		.column($dt.column('guid').withTitle($translate("UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.USER.LABEL")))
		.column($dt.linkscolumn("", [{ permission: "CASHIER_PROFILE_EDIT", permissionType:"any", permissionDomain:$stateParams.domainName, title: "<span class=\"fa fa-times\"></span>", css: "btn btn-danger", click: function(data) { controller.removeUser(data); } }]))
		.options(baseUrl, null)
		.build();
		
		controller.removeUser = function(data) {
			data.profile.id = -1;
			cashierRest.userProfileUpdate(data, data.profile).then(function(dmpu) {
				controller.usersTable.instance.reloadData();
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROFILES.REMOVEUSER.SUCCESS");
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		
		controller.addUser = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/cashier/config/profiles/profile/user.html',
				controller: 'user',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					profile: function() {
						return profile;
					},
					displayOnly: function() {
						return false;
					},
					domainName: function() {
						return $stateParams.domainName;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/cashier/config/profiles/profile/user.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(result) {
				controller.usersTable.instance.reloadData();
			});
		}
	}
]);
