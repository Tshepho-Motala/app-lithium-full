'use strict';

angular.module('lithium')
.provider('unlockgameusersearch', function() {
	this.$get = function() {
		var publicMethods = {
			unlockGame:function() {
				console.log("provider called");
				return this;
			}
		}
		return publicMethods;
	};
})
.directive('unlockgameusersearch', function(unlockgameusersearch) {
	return {
//		template: '',
		templateUrl:'scripts/directives/player/unlockgame/unlockgameusersearch.html',
		scope: {
			game: "="
		},
		link: function(scope) {
			unlockgameusersearch.unlockGame = scope.unlockGame;
		},
		restrict: 'AE',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'rest-casino', 'userEventRest', 'UserRest', 'errors', 'bsLoadingOverlayService', 'notify', '$timeout',
		function($q, $uibModal, $scope, casinoRest, userEventRest, userRest, errors, bsLoadingOverlayService, notify, $timeout) {
			var me = this;
			var modalInstance;
			
			$scope.unlockGame = function() {
//				console.log("in directive");
				modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl:'scripts/directives/player/unlockgame/unlockgameusersearchmodal.html',
					controller: 'UnlockGameUserSearcModalController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						game: function() {
							return $scope.game;
						}
					}
				});
				
				return modalInstance.result;
//				return modalInstance.result.then(function(response) {
//					if (response != null) console.log(response);
//					return response;
//				});
			};
		}]
	}
}).controller('UnlockGameUserSearcModalController',
['$uibModalInstance', 'game', '$userService', 'bsLoadingOverlayService', 'errors', 'notify', 'UserRest', 'rest-games', 
function ($uibModalInstance, game, $userService, bsLoadingOverlayService, errors, notify, userRest, gamesRest) {
	var controller = this;
	
	controller.game = game;
	controller.selectedDomain = game.domainName;
	controller.referenceId = game.id+"_"+(Math.random()*1000);
//	controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "GAME_LIST"]);
	
	console.log(controller.game, controller.referenceId);
	
	controller.cancel = function() {
		$uibModalInstance.dismiss();
	}
	
	controller.domainSelect = function(item) {
		controller.selectedDomain = item.name;
	}
	controller.clearSelectedDomain = function() {
		controller.selectedDomain = null;
	}
	
	controller.searchPlayer = function(searchValue) {
		return userRest.search(controller.selectedDomain, searchValue).then(function(response) {
			return response.plain();
		});
	}
	
	controller.registerPlayer = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		
		gamesRest.toggleLocked(controller.game.id, controller.selectedDomain+"/"+controller.playerSearch).then(function(response) {
			console.log(response.plain());
			notify.success('UI_NETWORK_ADMIN.GAME.UNLOCK.USERREGSUCCESS');
			$uibModalInstance.close(response);
		}).catch(function() {
			errors.catch('UI_NETWORK_ADMIN.GAME.UNLOCK.USERREGERROR', false);
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		}).finally(function() {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}
}]);