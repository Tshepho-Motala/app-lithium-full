'use strict';

angular.module('lithium').factory('PushMsgTemplateRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			
			var service = {};
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-pushmsg');
			});
			
			service.searchUser = function(domainName, username) {
				return config.all("pushmsg").all("user").all(domainName).all('list').getList({ search: username });
			}
			
			service.view = function(id) {
				return config.all("pushmsgtemplate").one(id).get();
			}
			
			service.add = function(o, domainName) {
				return config.all(domainName).all("pushmsgtemplates").post(o);
			}
			
			service.findByNameAndDomainName = function(name, domainName) {
				return config.all(domainName).all("pushmsgtemplates").one("find").get({name: name});
			}
			
			service.list = function(domainName, lang) {
				return config.all(domainName).all("pushmsgtemplates").all("list").getList({lang: lang});
			}
			
			service.edit = function(id) {
				return config.all("pushmsgtemplate").all(id).get("edit");
			}
			
			service.save = function(o) {
				return config.all("pushmsgtemplate").all(o.id).post(o);
			}
			
			service.continueLater = function(o) {
				return config.all("pushmsgtemplate").all(o.id).all("continueLater").post(o);
			}
			
			service.cancelEdit = function(o) {
				return config.all("pushmsgtemplate").all(o.id).all("cancelEdit").post(o);
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return config.all("pushmsgtemplate").all(entityId).one("changelogs").get({ p: page });
			}
			
			service.viewDefaultSMSTemplate = function(id) {
				return config.all("defaultpushmsgtemplates").one(id).get();
			}
			
			return service;
			
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);