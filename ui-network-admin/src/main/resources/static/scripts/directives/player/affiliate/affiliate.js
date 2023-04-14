'use strict';

angular.module('lithium')
.directive('affiliate', function() {
	return {
		templateUrl:'scripts/directives/player/affiliate/affiliate.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'AffiliateRest',
		function($scope, notify, errors, bsLoadingOverlayService, affiliateRest) {
			$scope.affiliate = null;
			$scope.referenceId = 'affiliate-overlay';
			bsLoadingOverlayService.start({referenceId:$scope.referenceId});
			affiliateRest.findAffiliateByPlayerId($scope.user.domain.name, $scope.user.id).then(function(response) {
				$scope.affiliate = response;
			}).catch(
				errors.catch("", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
			});
		}]
	}
});
