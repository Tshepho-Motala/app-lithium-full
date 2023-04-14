'use strict';

angular.module('lithium')
	.controller('PlayerAccountingSummaryController', ["domain", "user", "userFields", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify", "rest-accounting",
	function(domain, user, userFields, $uibModal, $translate, $log, $dt, $state, $rootScope, notify, acctRest) {
		var controller = this;
		
		controller.user = user;
		controller.domain = domain;
		
//		acctRest.balance('USD', 'ffp', 'ffp/riaans').then(function(response) {
//			console.log(response);
//			controller.balance = response;
//		});
}]);