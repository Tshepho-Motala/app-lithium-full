'use strict';

angular.module('lithium')
	.controller('providerView', ["providerOrLink","$uibModalInstance", "$translate", "$log", "$scope", "$state", "$stateParams", "$http", "Restangular", "rest-provider",
	function(providerOrLink, $uibModalInstance, $translate, $log, $scope, $state, $stateParams, $http, Restangular, restProvider) {
		var controller = this;
		
		$scope.roleDisplayShow = false;
		console.log(providerOrLink);
		if(angular.isDefined(providerOrLink.provider)) {
			//It is a link with provider as child
			$scope.domainName = providerOrLink.provider.domain.name;
			$scope.providerId = providerOrLink.provider.id;
			$scope.linkId = providerOrLink.id;
			$scope.provider = providerOrLink.provider;
		} else {
			$scope.domainName = providerOrLink.domain.name;
			$scope.providerId = providerOrLink.id;
			$scope.provider = providerOrLink;
		}
		
//		$scope.provider = restProvider.view($scope.domainName,$scope.providerId).$object;
		restProvider.listForProviderLink($scope.domainName,$scope.providerId).then(function(providerLinkList) {
			$scope.linkList = providerLinkList;
		});
//		$scope.back = function() {
//			$state.go("dashboard.providers");
//		}
		
		controller.ok = function() {
			$uibModalInstance.close(controller.asyncSelected);
		};
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);
