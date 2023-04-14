'use strict';

angular.module('lithium')
.factory('CmsAssetRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			
			var service = {};
			
			service.baseUrl = 'services/service-cdn-cms/backoffice';
			
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-cdn-cms/backoffice');
			});
			
			service.view = function(id, domainName) {
				return config.all(domainName).all("cms-assets").one(id).get();
			}

			
			service.add = function(o, domainName) {
				return config.all(domainName).all("cms-assets").post(o);
			}

			service.delete = function(domainName, id) {
				return config.all(domainName).all("cms-assets").all(id).remove();
			}
			
			service.save = function(domainName, o) {
				return config.all(domainName).all("cms-assets").all(o.id).post(o);
			}

			service.findByNameAndDomainAndType = function(name, domainName, type) {
				return config.all(domainName).all("cms-assets").all("find-by-name-and-domain-and-type").get('',{
					name, type
				});
			}

			service.findByDomainNameAndType = function(domainName, type) {
				return config.all(domainName).all("cms-assets").all("find-all-by-domain-name-and-type").all(type+'').getList();
			}

			service.changelogs = function(domainName, entityId, page) {
				return config.all(domainName).all("cms-assets").all(entityId).one("changelogs").get({ p: page });
			}

			return service;
			
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
