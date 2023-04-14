'use strict';

angular.module('lithium-rest-sms', ['restangular'])
.factory('rest-sms', ['$rootScope', 'Restangular', '$security',
	function($rootScope, Restangular, security) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-sms');
			});
			
			service.copy = function(copy) {
				return Restangular.copy(copy);
			}

			service.saveForPlayerWithText = function(playerGuid, text) {
				return config.all("sms").all('saveForPlayerWithText').post({playerGuid:playerGuid, text:text});
			}
			
			service.findById = function(id) {
				return config.all("sms").one('findOne', id).get();
			}
			
			service.providers = function() {
				return config.all("sms").all("provider").getList();
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