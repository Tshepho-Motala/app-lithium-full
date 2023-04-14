'use strict';

angular.module('lithium')
.factory('StatusRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			var rest = function() {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/status");
				});
			}

			var backofficeRest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/backoffice/account-status/"+domainName+"/");
				});
			}
			
			service.findAll = function() {
				return rest().all("all").getList();
			}

			service.findAllStatusReasons = function() {
				return rest().all("find-all-reasons").getList();
			}

			service.findReasonsByStatus = function(domainName, statusId) {
				return backofficeRest(domainName).all(statusId+"").all("find-reasons").getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);