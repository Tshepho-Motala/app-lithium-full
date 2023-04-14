'use strict';

angular.module('lithium')
.directive('status', function() {
	return {
		templateUrl:'scripts/directives/player/status/status.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService', 'StatusRest',
		function($q, $uibModal, $scope, UserRest, notify, errors, bsLoadingOverlayService, statusRest) {
			$scope.data.showHistory = ($scope.data.showHistory) || false;
			$scope.changeStatus = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl: 'scripts/directives/player/status/changestatus.html',
					controller: 'ChangeStatusModal',
					controllerAs: 'vm',
					size: 'md',
					resolve: {
						statuses: function() {
							return statusRest.findAll().then(function(statuses) {
								return statuses.plain();
							});
						},
						user: function() {return angular.copy($scope.user);},
						excludeStatusReasons: ['$stateParams', function($stateParams) {
							let excludeStatusReasons = [];

							var cruksSelfExclEnabled = ($scope.data.domainSettings['cruksId'] == "show" ? true : false);
							if (!cruksSelfExclEnabled) {
								excludeStatusReasons.push("CRUKS_SELF_EXCLUSION")
							}
							excludeStatusReasons.push("GAMSTOP_SELF_EXCLUSION")
							return excludeStatusReasons;
						}],
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/status/changestatus.js' ]
							})
						}
					}
				});
				modalInstance.result.then(function (user) {
					$scope.user.status = user.status;
					$scope.user.statusReason = user.statusReason;
					if (!user.status.userEnabled) {
					    $scope.user.loggedOutDate = user.loggedOutDate;
                        $scope.user.sessionDuration = user.sessionDuration;
                    }
					notify.success("Status updated successfully");
				});
			};
		}]
	}
});
