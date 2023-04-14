'use strict';

angular.module('lithium')
	.controller('user', ["domainName", "profile", "$stateParams", "$uibModalInstance", "rest-cashier", "UserRest", "notify", "errors",
	function(domainName, profile, $stateParams, $uibModalInstance, cashierRest, userRest, notify, errors) {
		var controller = this;
		
		controller.profile = profile;
		controller.submitDisabled = true;
		
		controller.searchUsers = function(userGuid) {
			return userRest.search($stateParams.domainName, userGuid).then(function(searchResult) {
				return searchResult.plain();
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		};
		
		controller.selectUser = function(user) {
			controller.selectedUser = user;
			controller.submitDisabled = true;
			controller.showAnotherProfileWarning = false;
			controller.inThisProfile = false
			controller.otherProfile = null;
			cashierRest.user(user.guid).then(function(u) {
				controller.selectedUser.guid = u.guid;
				if (u.profile === null) {
					//("no linked profile, good to go");
					controller.submitDisabled = false;
				} else if (u.profile.id === profile.id) {
					//("Already in this profile");
					controller.inThisProfile = true;
				} else if (u.profile.id !== profile.id) {
					//("Already belongs to another profile");
					controller.submitDisabled = false;
					controller.showAnotherProfileWarning = true;
					controller.otherProfile = u.profile;
				}
			}).catch(function(error) {
				errors.catch("", false)(error)
			}).then(function() {
			});
		}
		
		controller.submit = function() {
			cashierRest.userProfileUpdate(controller.selectedUser, profile).then(function(dmpu) {
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROFILES.ADDUSER.SUCCESS");
				$uibModalInstance.close("success");
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);