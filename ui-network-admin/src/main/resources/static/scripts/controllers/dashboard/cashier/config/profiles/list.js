'use strict';

angular.module('lithium')
.controller('CashierProfilesListController', ["rest-cashier", "$scope", "$stateParams", "$uibModal", "profiles",
	function(cashierRest, $scope, $stateParams, $uibModal, profiles) {
		var controller = this;
		controller.profiles = profiles;
		$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.HEADER.PROFILELIST");
		
		controller.editProfile = function(profile) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/cashier/config/profiles/edit.html',
				controller: 'editProfile',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					domainName: function () {
						return $stateParams.domainName;
					},
					profile: function () {
						return profile;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/cashier/config/profiles/edit.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(result) {
				controller.reload();
			});
		}
		
		controller.addProfile = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/cashier/config/profiles/add.html',
				controller: 'ProfileAdd',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					domainName: function () {
						return $stateParams.domainName;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/cashier/config/profiles/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(result) {
				controller.reload();
			});
		}
		
		controller.reload = function() {
			cashierRest.profiles($stateParams.domainName).then(function(profiles) {
				controller.profiles = profiles.plain();
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		
		controller.deleteById = function(profileId) {
			cashierRest.deleteProfileById($stateParams.domainName, profileId).then(function(profiles) {
				controller.profiles = profiles.plain();
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
	}
]);
