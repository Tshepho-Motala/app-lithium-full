'use strict';

angular.module('lithium-rest-tranta', ['restangular'])
.factory('rest-tranta', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user');
			});
			
			service.all = function(domainName) {
				//TransactionTypeAccountController
				return config.all(domainName).all("tranta").all("all").getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);