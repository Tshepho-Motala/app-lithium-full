'use strict';

angular.module('lithium')
.factory('XPRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-xp/admin';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.findAllStatuses = function() {
				return config.all('status').all('all').getList();
			}
			
			service.getActiveScheme = function(domainName) {
				return config.all('scheme').one(domainName, 'getActiveScheme').get();
			}
			
			service.createScheme = function(scheme) {
				return config.all('scheme').all('create').post(scheme);
			}
			
			service.viewScheme = function(id) {
				return config.all('scheme').one(id, 'get').get();
			}
			
			service.editScheme = function(id, scheme) {
				return config.all('scheme').all(id).all('edit').post(scheme);
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);