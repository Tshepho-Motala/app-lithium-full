'use strict';

angular.module('lithium')
.directive('playerlinks', function() {
	return {
		templateUrl:'scripts/directives/player/player-links/player-links.html',
		scope: {
			data: "=",
			user: "=ngModel",
		},
		restrict: 'E',
		replace: true,
		controller: ['$uibModal', '$scope', '$compile','notify', "errors", "$translate", "$dt", "$state", "DTOptionsBuilder", "UserRest",
		function($uibModal, $scope, $compile, notify, errors, $translate, $dt, $state, DTOptionsBuilder, rest) {
			var controller = this;

			$scope.updatePlayerLinkData = {};

			$scope.addPlayerLink = function(isModified) {
				$scope.playerlink = null;
				$scope.referenceId = 'playerlink-overlay';
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/player/player-links/adduserlink.html',
					controller: 'adduserLinkModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						type: function() { return $scope.data.type;},
						user: function() {return angular.copy($scope.user);},
						profile: function() {return $scope.data.profile;},
						data: function() {return $scope.data;},
						isModify: function() { return isModified;},
						updatePlayerLinkData: function() {return $scope.updatePlayerLinkData;},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/player-links/adduserlink.js' ]
							})
						}
					}
				});
				
				modalInstance.result.then(function (result) {
					controller.refresh();
				});
			};

			controller.deleted = function(confirmDelete) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/player/player-links/confirmdelete.html',
					controller: 'ConfirmNoteDeleteModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						isDelete: function () {
							return confirmDelete;
						},
						updatePlayerLinkData: function() { 
							return $scope.updatePlayerLinkData;
						},
						restService: function () {
							return rest;
						},
						loadMyFiles: function ($ocLazyLoad) {
							return $ocLazyLoad.load({
								name: 'lithium',
								files: ['scripts/directives/player/player-links/confirmdelete.js']
							})
						}
					}
				});

				modalInstance.result.then(function(response) {
					controller.refresh();
					notify.success("UI_NETWORK_ADMIN.ECOSYSTEMS.NOTIFY.DELETE");
				});
			};
			
			$scope.updatePlayerLink = function(selectedPlayer, deleteOrModify) {
				$scope.updatePlayerLinkData = selectedPlayer;
				if (deleteOrModify) {
					controller.deleted(deleteOrModify);
				} else {
					$scope.addPlayerLink(deleteOrModify);
				}
			}

			$scope.init = function() {
				$scope.listByUserData = {}
				rest.playerLinks($scope.user.guid).then(function(response){
					if (response !== undefined ) {
						$scope.listByUserData = response.plain();	
					}
				});
			}
			$scope.init();
		
			controller.refresh = function () {
				$scope.init();
			}

		}]

	}
});
