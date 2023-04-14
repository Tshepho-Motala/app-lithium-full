'use strict';

angular.module('lithium').directive('sidebar',['$location',function() {
	return {
		templateUrl:'scripts/directives/sidebar/sidebar.html',
		restrict: 'E',
		replace: true,
		scope: {
		},
		controller: ['$state', '$menu', '$scope', function($state, $menu, $scope){
			$scope.menuItems = $menu.menuItems;
			
//			$scope.click = function(item) {
//				if (item.route) $state.go(item.route);
//			}
		}]
	}
}]);
