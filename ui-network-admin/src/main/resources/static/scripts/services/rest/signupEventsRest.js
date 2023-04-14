'use strict';

angular.module('lithium-rest-signupevents', ['restangular']).factory('signupEventsRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var signupEventsService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user');
			});
			
			service.findById = function(id) {
				return signupEventsService.one('signupevent', id).get();
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);