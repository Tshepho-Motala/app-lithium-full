'use strict';

angular.module('lithium')
	.controller('languages', ["$translate", "$log", "$dt", "$scope", "$state", "$http", function($translate, $log, $dt, $scope, $state, $http) {
			
		var controller = this;
		var baseUrl = "services/service-translate/apiv1/languages/list?1=1";
		
		controller.rowClickHandler = function (info) {
			$http.get('services/service-translate/apiv1/language/' + info.id + '/toggle').then(function(response) {
				$scope.tableDisabled.instance.reloadData(function(){}, false);
				$scope.tableEnabled.instance.reloadData(function(){}, false);
			});
		} 
		 
		$scope.tableDisabled = $dt.builder()
			.column($dt.column('description').withTitle("Name"))
			.column($dt.column('locale2').withTitle("ISO (2 letters)"))
			.column($dt.column('locale3').withTitle("ISO (3 letters)"))
			.options(baseUrl + "&enabled=false", controller.rowClickHandler)
			.build();
		
		$scope.tableEnabled = $dt.builder()
			.column($dt.column('description').withTitle("Name"))
			.column($dt.column('locale2').withTitle("ISO (2 letters)"))
			.column($dt.column('locale3').withTitle("ISO (3 letters)"))
			.options(baseUrl + "&enabled=true", controller.rowClickHandler)
			.build();

}]);