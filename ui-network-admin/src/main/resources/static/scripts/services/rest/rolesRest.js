'use strict';

angular.module('lithium-rest-roles', ['restangular'])
.factory('rest-roles', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user');
			});
			
			service.listall = function() {
				//RolesController
				return config.all("roles").all("all").getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);