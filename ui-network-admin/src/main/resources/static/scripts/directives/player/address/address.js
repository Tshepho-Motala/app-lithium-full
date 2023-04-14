'use strict';

angular.module('lithium')
.directive('address', function() {
	return {
		templateUrl:'scripts/directives/player/address/address.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$uibModal', '$scope', 'notify',
		function($uibModal, $scope, notify) {
			$scope.changeAddress = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl: 'scripts/directives/player/address/changeaddress.html',
					controller: 'ChangeAddressModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						type: function() {return $scope.data.type;},
						user: function() {return angular.copy($scope.user);},
						profile: function() {return $scope.data.profile;},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/address/changeaddress.js' ]
							})
						}
					}
				});
				
				modalInstance.result.then(function (user) {
					$scope.user[$scope.data.type] = user[$scope.data.type];
					notify.success("Address details updated successfully");
				});
			};
		}]
	}
});
