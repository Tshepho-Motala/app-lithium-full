'use strict';

angular.module('lithium-rest-mail', ['restangular']).factory('mailRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var mailService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-mail');
			});
			
			service.findById = function(id) {
				return mailService.all("mail").one('findOne', id).get();
			}
			
			service.providers = function() {
				return mailService.all("mail").all("provider").getList();
			}
			
			service.domainProviders = function(domainName) {
				return mailService.all("domainProvider").one("domain", domainName).getList();
			}
			
			service.domainProviderAdd = function(domainName, description, providerId) {
				return mailService.all("domainProvider").all("addDomainProvider").post(
						{	
							domainName: domainName,
							description: description,
							providerId: providerId
						}
				);
			}
			
			service.domainProviderUpdate = function(domainProvider) {
				return mailService.all("domainProvider").all("update").post(domainProvider);
			}
			
			service.domainProviderUpdateMultiple = function(domainProviders) {
				return mailService.all("domainProvider").all("update").all("multiple").customPUT(domainProviders);
			}
			
			service.domainProviderDeleteFull = function(domainProvider) {
				return mailService.one("domainProvider", domainProvider.id).remove();
			}
			
			service.domainProviderProperties = function(domainProviderId) {
				return mailService.one("domainProvider", domainProviderId).all("props").getList();
			}
			
			service.domainProviderPropertiesNoDefaults = function(domainProviderId) {
				return mailService.one("domainProvider", domainProviderId).all("props").all("nodef").getList();
			}
			
			service.domainProviderPropertyDelete = function(domainProviderId, domainProviderPropertyId) {
				return mailService.one("domainProvider", domainProviderId).all("prop").one(domainProviderPropertyId+'').remove();
			}
			
			service.domainProviderPropertiesSave = function(domainProviderId, properties) {
				return mailService.one("domainProvider", domainProviderId).customPUT(
					properties,
					'props'
				);
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);