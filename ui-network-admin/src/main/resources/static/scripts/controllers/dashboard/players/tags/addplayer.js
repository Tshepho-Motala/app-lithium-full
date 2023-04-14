'use strict';

angular.module('lithium')
	.controller('TagPlayerAdd', ["$uibModalInstance", "$userService", "notify", "errors", "UserRest", "tag", "bsLoadingOverlayService",
	function($uibModalInstance, $userService, notify, errors, userRest, tag, bsLoadingOverlayService) {
		var controller = this;
		controller.tag = tag;
		controller.referenceId = tag.id+"_"+(Math.random()*1000);
		
		controller.searchPlayer = function(searchValue) {
			return userRest.search(controller.tag.domain.name, searchValue).then(function(response) {
				return response.plain();
			});
		}
		
		controller.registerPlayer = function() {
			bsLoadingOverlayService.start({referenceId:controller.referenceId});
			
			userRest.tagAddPlayer(controller.tag.id, controller.playerSearch).then(function(response) {
				notify.success('UI_NETWORK_ADMIN.PLAYERS.TAGS.ADDPLAYERSUCCESS');
				$uibModalInstance.close(response);
			}).catch(function() {
				errors.catch('UI_NETWORK_ADMIN.PLAYERS.TAGS.ADDPLAYERERROR', false);
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			}).finally(function() {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);