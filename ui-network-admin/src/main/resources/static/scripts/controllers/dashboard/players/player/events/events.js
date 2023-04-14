'use strict';

angular.module('lithium')
	.controller('PlayerEventsController', ["domain", "user", "userFields", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify",
	function(domain, user, userFields, $uibModal, $translate, $log, $dt, $state, $rootScope, notify) {
		var controller = this;
		
		controller.user = user;
		controller.domain = domain;
		
}]);