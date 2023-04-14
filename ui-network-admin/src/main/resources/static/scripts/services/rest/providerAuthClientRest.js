'use strict';

angular.module('lithium-rest-provider-auth-client', ['restangular'])
.factory('rest-provider-auth-client', ['$rootScope', 'Restangular', '$security',
	function($rootScope, Restangular) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-domain');
			});

			service.add = function(domainName, providerAuthClient) {
				//ProviderAuthClientController
				return config.all("domain").all("providerauthclient").all(domainName).all("add").post(providerAuthClient);
			}
			service.save = function(domainName, providerAuthClient) {
				//ProviderAuthClientController
				return config.all("domain").all("providerauthclient").all(domainName).all("save").post(providerAuthClient);
			}
			service.delete = function(domainName, id) {
				return config.all("domain").all("providerauthclient").all(domainName).all("delete").all(id).remove();
			}
			service.find = function(domainName, providerAuthClientId) {
				//ProviderAuthClientController
				return config.all("domain").all("providerauthclient").all(domainName).customGET("findById", {id: providerAuthClientId});
			}
			service.changelogs = function(domainName, entityId, page) {
				return config.all("domain").all("providerauthclient").all(domainName).all(entityId).one("changelogs").get({ p: page });
			}

			return service;
		} catch (err) {
			console.error(err);
			throw err;
		}
	}
]);