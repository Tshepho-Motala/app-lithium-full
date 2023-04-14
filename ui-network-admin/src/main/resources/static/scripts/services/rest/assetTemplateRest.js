'use strict';

angular.module('lithium')
.factory('AssetTemplatesRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			
			var service = {};
			
			service.baseUrl = 'services/service-domain/backoffice';
			
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-domain/backoffice');
			});
			
			service.view = function(id, domainName) {
				return config.all(domainName).all("asset/templates").one(id).get();
			}

			service.get = function(domainName, request) {
				return config.all(domainName).all("asset/templates/list").post(request);
			}
			
			service.add = function(o, domainName) {
				return config.all(domainName).all("asset/templates").post(o);
			}

			service.delete = function(domainName, id) {
				return config.all(domainName).all("asset/templates").all(id).remove();
			}
			
			service.save = function(domainName, o) {
				return config.all(domainName).all("asset/templates").all(o.id).post(o);
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return config.all(domainName).all("asset/templates").all(entityId).one("changelogs").get({ p: page });
			}

			service.findByNameAndDomainNameAndLang = function(name, domainName, lang) {
				return config.all(domainName).all("asset/templates").one("find-by-name-and-lang-and-domainname").get({ name: name, lang: lang})
			}

			return service;
			
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
