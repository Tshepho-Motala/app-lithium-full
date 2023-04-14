'use strict';

angular.module('lithium-rest-userevents', ['restangular']).factory('userEventRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var userEventService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user');
			});
			
			service.registerUserEvent = function(domainName, userName, type, message, data) {
				return userEventService.all("userevent").all('system').one(domainName).one(userName).all('register').post({type: type, message: message, data: data});
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);