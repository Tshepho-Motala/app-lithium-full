'use strict';

angular.module('lithium')
.factory('rest-provider', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-domain'); //yes, it should be service-domain
			});

			service.list = function() {
				var providerListPromise = config.all("/providers").getList();
				return providerListPromise;
			}
			
			service.listForDomain = function(domainName) {
				var providerListPromise = config.all("domain/"+domainName+"/providers").getList();
				return providerListPromise;
			}
			
			service.listForDomainLink = function(domainName) {
				var providerListLinkPromise = config.all("domain/"+domainName+"/providers/linksList").getList();
				return providerListLinkPromise;
			}
			
			service.availableProviderLinksList = function(domainName,providerUrl) {
				var providerListLinkPromise = config.all("domain/"+domainName+"/providers/availableProviderLinksList?providerUrl="+providerUrl).getList();
				return providerListLinkPromise;
			}
			
			service.listForProviderLink = function(domainName, providerId) {
				var providerListLinkPromise = config.all("domain/"+domainName+"/provider/"+providerId+"/linksListByProviderId").getList();
				return providerListLinkPromise;
			}
			
			service.view = function(domainName, providerId) {
				var providerPromise = config.all("domain/"+domainName+"/provider/"+providerId).one("view").get();
				return providerPromise;
			}
			
			service.save = function(domainName, providerId, provider) {
				var providerPromise = config.all("domain/"+domainName+"/provider/"+providerId+"/edit").post(provider);
				return providerPromise;
			}
			
			service.configProps = function(providerUrl) {
				var providerConfigPropsPromise = 
					Restangular.all("/services/"+providerUrl.toLowerCase()+"/modules/providers").getList();
				return providerConfigPropsPromise;
			}
			
			service.add = function(domainName, provider) {
				var providerAddPromise =
					config.all("/domain/"+domainName+"/providers/add").post(provider);
				return providerAddPromise;
			}
			
			service.addLink = function(domainName, linkId) { //TODO: can possibly just move this to add and check the body content serverside
				var providerAddPromise =
					config.all("/domain/"+domainName+"/providers/addLink").post({},{linkId: linkId});
				return providerAddPromise;
			}
			
			service.viewLink = function(domainName, linkId) {
				var providerLinkPromise = config.all("domain/"+domainName+"/providers").customGET("viewLink",{linkId: linkId});
				return providerLinkPromise;
			}
			
			service.editLink = function(domainName, domainProviderLink) {
				var providerLinkPromise = config.all("domain/"+domainName+"/providers/editLink").post(domainProviderLink);
				return providerLinkPromise;
			}
			
			service.findOwnerLink = function(domainName, providerId) {
				var providerLinkPromise = config.all("domain/"+domainName+"/providers").customGET("findOwnerLink",{providerId: providerId});
				return providerLinkPromise;
			}
			
			service.listByDomainAndType = function(domainName, type) {
				return config.all("domain/"+domainName+"/providers/listbydomainandtype").getList({type: type});
			}

			service.listByType = function(domainName, type) {
				return config.all("domain/"+domainName+"/providers/listbytype").getList({type: type});
			}

			// Keeping entityId signature since it is being passed in from the changelog directive. However it is unnecessary here.
			// Domain id is used to tie together provider changelogs
			service.changelogs = function(domainName, entityId, page) {
				return config.all("domain").all(domainName).all("providers").one("changelogs").get({ p: page });
			}
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
