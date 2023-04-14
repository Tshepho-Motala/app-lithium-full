'use strict';

angular.module('lithium')
.directive('referral', function() {
	return {
		templateUrl:'scripts/directives/player/referral/referral.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'RAFRest',
		function($scope, notify, errors, bsLoadingOverlayService, rafRest) {
			$scope.referral = null;
			$scope.referenceId = 'referral-overlay';
			bsLoadingOverlayService.start({referenceId:$scope.referenceId});
			rafRest.findReferralByPlayerGuid($scope.user.domain.name, $scope.user.username).then(function(response) {
				console.debug(response.plain());
				$scope.referral = response.plain();
			}).catch(
//				errors.catch("", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
			});
		}]
	}
});