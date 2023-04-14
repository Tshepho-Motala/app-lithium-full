'use strict';

angular.module('lithium')
	.controller('PlayerAccountingAdjustmentsController', ["domain", "user", "userFields", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify", "rest-accounting", "$filter",
	function(domain, user, userFields, $uibModal, $translate, $log, $dt, $state, $rootScope, notify, acctRest, $filter) {
		var controller = this;
		
		controller.user = user;
		controller.domain = domain;
		
		controller.getbalance = function() {
			acctRest.balance('USD', 'ffp', 'ffp/riaans').then(function(response) {
				console.log(response);
				controller.balance = response;
				controller.newbalance = response;
				controller.newbalancedisplay = $filter('cents')(controller.newbalance);
				controller.adjustment = $filter('cents')(controller.balance - controller.newbalance);
			});
		}
		controller.getbalance();
		
		controller.decrease = function(adjustment) {
			controller.newbalance = controller.newbalance - (Math.round(adjustment*100));
			controller.newbalancedisplay = $filter('cents')(controller.newbalance);
			controller.adjustment = $filter('cents')(controller.newbalance - controller.balance);
		}
		controller.increase = function(adjustment) {
			controller.newbalance = controller.newbalance + (Math.round(adjustment*100));
			controller.newbalancedisplay = $filter('cents')(controller.newbalance);
			controller.adjustment = $filter('cents')(controller.newbalance - controller.balance);
		}
		controller.adjust = function() {
			acctRest.balanceadjust(
				(controller.newbalance - controller.balance),
				new Date(),
				'BALANCE_ADJUST',
				'BALANCE_ADJUST',
				'PLAYER_BALANCE',
				controller.comment,
				'USD',
				domain.name,
				'ffp/riaans',
				'default/admin'
			).then(function(response) {
				notify.success("Balance Adjusted!!!");
				console.log('Balance Adjusted', response);
				controller.getbalance();
			});
		}
}]);