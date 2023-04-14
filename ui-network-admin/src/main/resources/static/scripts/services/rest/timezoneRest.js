'use strict';

angular.module('lithium')
.factory('timezoneRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-user';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.timezoneList= function(){
				return config.all('timezones').getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);