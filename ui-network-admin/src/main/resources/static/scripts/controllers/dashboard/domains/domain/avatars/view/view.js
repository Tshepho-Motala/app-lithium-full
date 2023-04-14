'use strict';

angular.module('lithium')
	.controller('DomainAvatarViewController', ["avatar", "$log", "$scope", "$state", "notify", "AvatarRest",
	function(avatar, $log, $scope, $state, notify, avatarRest) {
		var controller = this;
		
		controller.model = avatar;
		
		controller.avatarImageUrl = 'services/service-avatar/avatar/'+avatar.domain.name+'/getImage/'+avatar.id
		
		controller.toggleEnable = function() {
			avatarRest.toggleEnable(avatar.domain.name, controller.model.id).then(function(response) {
				if (response._status === 0) {
					if (controller.model.enabled) {
						notify.success('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DISABLE.SUCCESS');
					} else {
						notify.success('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ENABLE.SUCCESS');
					}
					controller.model = response.plain();
				} else {
					if (controller.model.enabled) {
						notify.error('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DISABLE.ERROR');
					} else {
						notify.error('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ENABLE.ERROR');
					}
				}
			}).catch(function(error) {
				if (controller.model.enabled) {
					errors.catch('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DISABLE.ERROR');
				} else {
					errors.catch('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ENABLE.ERROR');
				}
			});
		}
		
		controller.setAsDefault = function() {
			avatarRest.setAsDefault(avatar.domain.name, controller.model.id).then(function(response) {
				if (response._status === 0) {
					notify.success('UI_NETWORK_ADMIN.DOMAIN.AVATARS.SETASDEFAULT.SUCCESS');
					controller.model = response.plain();
				} else {
					notify.error('UI_NETWORK_ADMIN.DOMAIN.AVATARS.SETASDEFAULT.ERROR');
				}
			}).catch(function(error) {
				errors.catch('UI_NETWORK_ADMIN.DOMAIN.AVATARS.SETASDEFAULT.ERROR');
			});
		}
		
		controller.deleteAvatar = function() {
			avatarRest.deleteById(avatar.domain.name, controller.model.id).then(function(response) {
				if (response === true) {
					notify.success('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DELETE.SUCCESS');
					$state.go("dashboard.domains.domain.avatars");
				} else {
					notify.error('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DELETE.ERROR');
				}
			}).catch(function(error) {
				errors.catch('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DELETE.ERROR');
			});
		}
	}
]);