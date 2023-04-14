'use strict'

angular.module('lithium').directive('mail', function() {
	return {
		restrict: 'E',
		templateUrl: '/scripts/directives/mail/mail.html',
		replace: true,
		transclude: true,
		scope: {
			data: '=',
			state: '='
		},
		controller: ['$scope', 'rest-domain', function($scope, restDomain) {
			$scope.mail = $scope.data;
			$scope.currentState = $scope.state;
			
			$scope.setlinkToProfile = function() {
				if ($scope.mail.fullUser != null) {
					restDomain.findByName($scope.mail.fullUser.domain.name).then(function(data) {
						if (data.players) {
							$scope.linkToProfile = '/#/dashboard/players/'+$scope.mail.fullUser.domain.name+'/'+$scope.mail.fullUser.id+'/summary';
						} else {
							$scope.linkToProfile = '/#/dashboard/domains/domain/'+$scope.mail.fullUser.domain.name+'/users/'+$scope.mail.fullUser.id+'/view';
						}
					});
				}
			};
			
			$scope.setlinkToProfile();
		}]
	}
});