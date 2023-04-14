'use strict';

angular.module('lithium')
.directive('biometricsStatus', function() {
	return {
		templateUrl:'scripts/directives/player/biometrics-status/biometrics-status.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'UserRest', 'notify',
		function($q, $uibModal, $scope, UserRest, notify) {
			$scope.data.showHistory = ($scope.data.showHistory) || false;
			$scope.changeBiometricsStatus = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl: 'scripts/directives/player/biometrics-status/change-biometrics-status.html',
					controller: 'ChangeBiometricsStatusModal',
					controllerAs: 'vm',
					size: 'md',
					resolve: {
						user: function() {return angular.copy($scope.user);},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/biometrics-status/change-biometrics-status.js' ]
							})
						}
					}
				});
				modalInstance.result.then(function (user) {
					$scope.user.biometricsStatus = user.biometricsStatus;
					notify.success("Status updated successfully");
				});
			};
		}]
	}
});
