'use strict'

angular.module('lithium').directive('sms', function() {
	return {
		restrict: 'E',
		templateUrl: '/scripts/directives/sms/sms.html',
		replace: true,
		transclude: true,
		scope: {
			data: '=',
			state: '='
		},
		controller: ['$scope', 'rest-domain', function($scope, restDomain) {
			$scope.sms = $scope.data;
			$scope.currentState = $scope.state;
			
			$scope.setlinkToProfile = function() {
				if ($scope.sms.fullUser != null) {
					restDomain.findByName($scope.sms.fullUser.domain.name).then(function(data) {
						if (data.players) {
							$scope.linkToProfile = '/#/dashboard/players/'+$scope.sms.fullUser.domain.name+'/'+$scope.sms.fullUser.id+'/summary';
						} else {
							$scope.linkToProfile = '/#/dashboard/domain/'+$scope.sms.fullUser.domain.name+'/users/'+$scope.sms.fullUser.id+'/view';
						}
					});
				}
			};
			
			$scope.setlinkToProfile();
		}]
	}
});