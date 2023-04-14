'use strict';

angular.module('lithium')
.factory('IncompleteUserRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			var rest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/" + domainName +"/");
				}).service("incompleteusers");
			}

			service.findById = function(domainName, id) {
				return rest(domainName).one(id).get();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);