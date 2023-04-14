'use strict';

angular.module('lithium')
.factory('TemplatesRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			
			var service = {};
			
			service.baseUrl = 'services/service-domain';
			
			service.imageUploadUrl = function (id) {
				service.baseUrl + "template/" + id + "/upload";
			}
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-domain');
			});
			
			service.view = function(id, domainName) {
				return config.all(domainName).all("template").one(id).get();
			}
			
			service.add = function(o, domainName) {
				return config.all(domainName).all("templates").post(o);
			}

			service.delete = function(domainName, id) {
				return config.all(domainName).all("template").all(id).remove();
			}


			service.findByNameAndLangAndDomainName = function(name, lang, domainName) {
				return config.all(domainName).all("templates").one("findByNameAndLangAndDomainName").get({name: name, lang: lang});
			}
			
			service.findByDomainNameAndLang = function(domainName, lang) {
				return config.all(domainName).all("templates").all("findByDomainNameAndLang").getList({lang: lang});
			}

			service.edit = function(domainName, id) {
				return config.all(domainName).all("template").all(id).get("edit");
			}

			service.save = function(domainName, o) {
				return config.all(domainName).all("template").all(o.id).post(o);
			}
			
			service.continueLater = function(domainName, o) {
				return config.all(domainName).all("template").all(o.id).all("continueLater").post(o);
			}
			
			service.cancelEdit = function(domainName, o) {
				return config.all(domainName).all("template").all(o.id).all("cancelEdit").post(o);
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return config.all(domainName).all("template").all(entityId).one("changelogs").get({ p: page });
			}

			return service;
			
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
