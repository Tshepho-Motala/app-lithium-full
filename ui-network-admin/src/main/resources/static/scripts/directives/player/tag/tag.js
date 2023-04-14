'use strict';

angular.module('lithium')
.directive('tag', function() {
	return {
		templateUrl:'scripts/directives/player/tag/tag.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService',
		function($q, $uibModal, $scope, userRest, notify, errors, bsLoadingOverlayService) {
//			$scope.userCopy = angular.copy($scope.user);
			$scope.changeStatus = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/player/tag/changetag.html',
					controller: 'ChangeTagModal',
					controllerAs: 'vm',
					size: 'md cascading-modal',
					resolve: {
						user: function() {return angular.copy($scope.user);},
						domainName: function() { return $scope.data.domainName; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/tag/changetag.js' ]
							})
						}
					}
				});
				modalInstance.result.then(function (user) {
					$scope.user.userCategories = user.userCategories;
					notify.success("Tag updated successfully");
				});
			};
		}]
	}
});
