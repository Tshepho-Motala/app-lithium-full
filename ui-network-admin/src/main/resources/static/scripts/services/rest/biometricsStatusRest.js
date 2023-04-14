'use strict';

angular.module('lithium')
.factory('BiometricsStatusRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			var rest = function() {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/backoffice/biometrics-statuses");
				});
			}
			
			service.findAll = function() {
				return rest().all("").getList();
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);