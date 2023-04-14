'use strict'

angular.module('lithium').controller('PlayerMissionsController', ["$q", "$timeout", "$state","$scope", '$translate', "$rootScope", 'user',
	function($q, $timeout, $state, $scope, $translate, $rootScope, user) {
		$rootScope.provide.data = {
			user
		}
	window.VuePluginRegistry.loadByPage("Promotions-Reward-Player-History");
	}
]);
