'use strict';

angular.module('lithium').factory('SMSTemplateRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			
			var service = {};
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-sms');
			});
			
			service.view = function(id) {
				return config.all("smstemplate").one(id).get();
			}
			
			service.add = function(o, domainName) {
				return config.all(domainName).all("smstemplates").post(o);
			}

			service.testSMSTemplate = function(id, recipientMobile) {
				return config.all("smstemplate").all(id).all("test").all(encodeURI(recipientMobile)).post();
			}
			
			service.findByNameAndLangAndDomainName = function(name, lang, domainName) {
				return config.all(domainName).all("smstemplates").one("findByNameAndLangAndDomainName").get({name: name, lang: lang});
			}
			
			service.findByDomainNameAndLang = function(domainName, lang) {
				return config.all(domainName).all("smstemplates").all("findByDomainNameAndLang").getList({lang: lang});
			}
			
			service.edit = function(id) {
				return config.all("smstemplate").all(id).get("edit");
			}
			
			service.save = function(o) {
				return config.all("smstemplate").all(o.id).post(o);
			}
			
			service.continueLater = function(o) {
				return config.all("smstemplate").all(o.id).all("continueLater").post(o);
			}
			
			service.cancelEdit = function(o) {
				return config.all("smstemplate").all(o.id).all("cancelEdit").post(o);
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return config.all("smstemplate").all(entityId).one("changelogs").get({ p: page });
			}
			
			service.viewDefaultSMSTemplate = function(id) {
				return config.all("defaultsmstemplates").one(id).get();
			}
			
			return service;
			
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);