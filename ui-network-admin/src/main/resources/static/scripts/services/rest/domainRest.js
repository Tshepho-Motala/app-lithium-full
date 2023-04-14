'use strict';

angular.module('lithium-rest-domain', ['restangular'])
.factory('rest-domain', ['$rootScope', 'Restangular', '$security',
	function($rootScope, Restangular, security) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-domain');
			});
			
			service.authProviders = function(domainName) {
				return config.all("domain").all(domainName).all("providers").all("auth").getList();
			}

			service.view = async function (domainName) {
				//DomainController
				return config.all("domain").one(domainName).get();
			}
			service.save = function(domainName, domain) {
				//DomainController
				return config.all("domain").all(domainName).post(domain);
			}
			service.findByName = function(domainName) {
				//DomainsController
				return config.all("domains").customGET("findByName", {name:domainName});
			}
			service.findAllDomains = function() {
				return config.all("domains").all("findAllDomains").getList();
			}
			service.findAllPlayerDomains = function() {
				return config.all("domains").all("findAllPlayerDomains").getList();
			}
			service.children = function(domainName) {
				//DomainController
				var children = config.all("domain").all(domainName).all("children").getList();
				return children;
			}
			service.add = function(domain) {
				//DomainsController
				return config.all("domains").post(domain);
			}

			service.defaultroles = function(domainName) {
				//DomainRolesController
				var roles = config.all("domain").all(domainName).all("roles").getList();
				return roles;
			}
			service.adddefaultroles = function(domainName, roles) {
				//DomainRolesController
				var add = config.one("domain", domainName).all("roles").all("add").all("all").post(roles);
				return add;
			}
			service.roleenable = function(domainName, domainRoleId, enabled) {
				//DomainRoleController
				var domainRole = config.all("domain").all(domainName).all(domainRoleId).all("enabled").all(enabled).post();
//				domainRole.then(function(dr) {
//					$rootScope.$broadcast('lit-security:roles:enable', dr);
//				});
				return domainRole;
			}
			service.roledelete = function(domainName, domainRoleId) {
				//DomainRoleController
				return config.all("domain").all(domainName).all(domainRoleId).all("delete").post();
			}
			
			service.findDomainRevision = function(domainName, domainRevisionId) {
				return config.all("domain").all("settings").one(domainName, domainRevisionId).get();
			}
			
			service.addDomainSettings = function(domainName, settings) {
				return config.all("domain").all("settings").all(domainName).all("add").post({domainName: domainName, labelValues: settings});
			}
			
			service.findCurrentDomainSettings = function(domainName) {
				return config.all("domain").all("settings").all(domainName).all("findCurrentSettings").getList();
			}

			service.findCurrentDomainSetting = function(domainName, settingName) {
				return config.all("domain").all("settings").all(domainName).one("findCurrentSetting").get({settingName: settingName});
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return config.all("domain").all(domainName).all(entityId).one("changelogs").get({ p: page });
			}

			service.toggleBettingEnabled = function(domainName) {
				return config.all("domain").all(domainName).all("bettingenabled").all("toggle").post();
			}

			service.findAllProviderAuthClients = function(domains) {
				return config.all("domain").all("providerauthclient").all('list').getList({domainNames:domains});
			}
			return service;
		} catch (err) {
			console.error(err);
			throw err;
		}
	}
]);
