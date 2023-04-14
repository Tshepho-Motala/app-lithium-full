'use strict';

angular.module('lithium-rest-cashier-dmp', ['restangular'])
.factory('rest-cashier-dmp', ['$rootScope', 'Restangular', '$security',
	function($rootScope, Restangular, security) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-cashier');
			});
			
			service.changelogs = function(domainName, entityId, page) {
				return config.all('cashier').one('dmp', entityId).one('changelogs').get({ p: page });
			}
			
			return service;
		} catch (err) {
			console.error(err);
			throw err;
		}
	}
]);