'use strict';

angular.module('lithium').directive('inbox', function() {
	return {
		templateUrl:'scripts/directives/notifications/inbox/inbox.html',
		scope: {
			data: "="
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'AvatarRest',
		function($q, $uibModal, $scope, notify, errors, avatarRest) {
			if ($scope.data.user) {
				var domainAndPlayer = $scope.data.user.guid.split('/');
				if (domainAndPlayer.length === 2) {
					avatarRest.getUserAvatar(domainAndPlayer[0], domainAndPlayer[1]).then(function(response) {
						var r = response.plain();
						$scope.avatarImageUrl = 'services/service-avatar/avatar/'+domainAndPlayer[0]+'/getImage/'+r.avatar.id
					});
				}
			}
		}]
	}
});