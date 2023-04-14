'use strict';

angular.module('lithium')
	.controller('CashierDomainProfileProcessorsController', ["profile", "rest-cashier", "$dt", "$translate", "$uibModal", "$stateParams", "notify", "errors",
	function(profile, cashierRest, $dt, $translate, $uibModal, $stateParams, notify, errors) {
		var controller = this;
		
		controller.findprocessors = function() {
			cashierRest.domainMethodProcessorsByProfile(profile.id).then(function(dmps) {
				controller.processors = dmps.plain();
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		controller.findprocessors();
	}
]);