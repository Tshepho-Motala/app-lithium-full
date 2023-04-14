angular.module('lithium')
	.controller('gameDemoController', ["$url","$state", "$stateParams", "$scope",
	function($url, $state, $stateParams, $scope) {
		var controller = this;
		
		$scope.url = $stateParams.url;
		
		controller.test = $url;
	}]
	);