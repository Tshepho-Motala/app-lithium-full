'use strict';

angular.module('lithium')
	.controller('domainListController', ["domains", "$scope",
	function(domains, $scope) {
		var controller = this;
		controller.domains = domains;
	}
]);