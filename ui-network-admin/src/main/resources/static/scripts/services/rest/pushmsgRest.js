'use strict';

angular.module('lithium-rest-pushmsg', ['restangular'])
.factory('rest-pushmsg', ['$rootScope', 'Restangular', '$security',
	function($rootScope, Restangular, security) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-pushmsg');
			});
			
			service.copy = function(copy) {
				return Restangular.copy(copy);
			}
			
			service.test = function(domainName, templateId, userGuids) {
				return config.all("pushmsg").all('send').all(domainName).post(
					{templateId:templateId, userGuids:userGuids}
				);
			}
			
			service.userDetails = function(domainName, guid) {
				return config.all(domainName).all("pushmsgusers").all("details").getList({guid: guid});
			}
			
			service.findById = function(id) {
				return config.all("pushmsg").one('findOne', id).get();
			}
			
			service.providers = function() {
				return config.all("pushmsg").all("provider").getList();
			}
			
			service.domainProviders = function(domainName) {
				return config.all("domainProvider").one("domain", domainName).getList();
			}
			
			service.domainProviderAdd = function(domainName, description, providerId) {
				return config.all("domainProvider").all("addDomainProvider").post(
						{	
							domainName: domainName,
							description: description,
							providerId: providerId
						}
				);
			}
			
			service.domainProviderUpdate = function(domainProvider) {
				return config.all("domainProvider").all("update").post(domainProvider);
			}
			
			service.domainProviderUpdateMultiple = function(domainProviders) {
				return config.all("domainProvider").all("update").all("multiple").customPUT(domainProviders);
			}
			
			service.domainProviderDeleteFull = function(domainProvider) {
				return config.one("domainProvider", domainProvider.id).remove();
			}
			
			service.domainProviderProperties = function(domainProviderId) {
				return config.one("domainProvider", domainProviderId).all("props").getList();
			}
			
			service.domainProviderPropertiesNoDefaults = function(domainProviderId) {
				return config.one("domainProvider", domainProviderId).all("props").all("nodef").getList();
			}
			
			service.domainProviderPropertyDelete = function(domainProviderId, domainProviderPropertyId) {
				return config.one("domainProvider", domainProviderId).all("prop").one(domainProviderPropertyId+'').remove();
			}
			
			service.domainProviderPropertiesSave = function(domainProviderId, properties) {
				return config.one("domainProvider", domainProviderId).customPUT(
					properties,
					'props'
				);
			}
			
			return service;
		} catch (err) {
			console.error(err);
			throw err;
		}
	}
]);