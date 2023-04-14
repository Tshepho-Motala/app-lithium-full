'use strict';

angular.module('lithium')
.directive('externalBonus', function() {
	return {
		templateUrl:'scripts/directives/player/bonus/external-bonus.html',
		scope: {
			data: "="
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'rest-casino', 'errors', 'bsLoadingOverlayService', 'notify', '$timeout',
		function($q, $uibModal, $scope, casinoRest, errors, bsLoadingOverlayService, notify, $timeout) {
			var me = this;
			
			if (!$scope.data.color) {
				$scope.data.color = "gray";
			}
			
			var dayInMs = 86400000;
			$scope.referenceId = 'external-bonus-overlay';
			$scope.refresh = function() {
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				casinoRest.externalBonusInfo($scope.data.ownerGuid, $scope.data.provider, $scope.data.domainName, '').then(function(response) {
//					console.log(response);
					$scope.bonus = response.bonus;
				}).catch(
					//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};
			
			$scope.cancel = function(extBonusId) {
				console.log("Cancelling bonus : "+extBonusId);
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				casinoRest.externalBonusCancel(extBonusId, $scope.data.provider, $scope.data.domainName, '', '').then(function(response) {
					console.log(response);
					$scope.refresh();
				}).catch(
					//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};
			
			$scope.refresh();
		}]
	}
});