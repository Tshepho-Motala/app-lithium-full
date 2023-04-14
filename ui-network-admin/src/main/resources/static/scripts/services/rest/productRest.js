'use strict';

angular.module('lithium')
.factory('ProductRest', ['Restangular',
	function(Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-product/product';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.removeGraphic = function(productId, graphicFunction) {
				return config.all("admin/graphic/remove/"+productId).post({
					graphicFunction: graphicFunction
				});
			}
			
			service.add = function(model) {
				return config.all('admin').all('add').post(model);
			}
			service.edit = function(model) {
				return config.all('admin').all('edit').post(model);
			}
			service.addLocalCurrency = function(model) {
				return config.all('admin').all('add').all('localcurrency').post(model);
			}
			service.editLocalCurrency = function(model) {
				return config.all('admin').all('edit').all('localcurrency').post(model);
			}
			service.removeLocalCurrency = function(id) {
				return config.all('admin').all('remove').one("localcurrency", id).remove();
			}
			
			service.addPayout = function(model) {
				return config.all('admin').all('add').all('payout').post(model);
			}
			service.editPayout = function(model) {
				return config.all('admin').all('edit').all('payout').post(model);
			}
			service.removePayout = function(id) {
				return config.all('admin').all('remove').one("payout", id).remove();
			}
			
			service.enable = function(id) {
				return config.all('admin').all('enable').all(id+'').post();
			}
			
			service.findProductById = function(id) {
				return config.all('admin').all('find').get(id);
			}
			
			return service;
		} catch (err) {
			console.error(err);
			throw err;
		}
	}
]);