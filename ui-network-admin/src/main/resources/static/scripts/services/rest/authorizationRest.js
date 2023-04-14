'use strict';

angular.module('lithium-rest-authorization', ['restangular']).factory('authorizationRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var authorizationService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-access');
			});
			
			service.checkAuthorization = function(domainName, accessRuleName, authorizationRequest) {
				return authorizationService.all('authorization').all(domainName).all(accessRuleName).all('checkAuthorization').post(authorizationRequest, {test:true});
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);