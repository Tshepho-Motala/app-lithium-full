'use strict';

angular.module('lithium')
.factory('EmailTemplateRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			
			var service = {};
			
			service.baseUrl = 'services/service-mail';
			
			service.imageUploadUrl = function (id) {
				service.baseUrl + "emailtemplate/" + id + "/upload";
			}
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-mail');
			});

			service.testEmailTemplate = function(id, recipientEmail, transactionId) {
				return config.all("emailtemplate").all(id).all("test").all(encodeURI(recipientEmail)).post('',{transactionId: transactionId});
			}
			
			service.view = function(id) {
				return config.all("emailtemplate").one(id).get();
			}
			
			service.add = function(o, domainName) {
				return config.all(domainName).all("emailtemplates").post(o);
			}

			service.findByNameAndLangAndDomainName = function(name, lang, domainName) {
				return config.all(domainName).all("emailtemplates").one("findByNameAndLangAndDomainName").get({name: name, lang: lang});
			}

			service.getEmailTemplates = function(domainName) {
				return config.all(domainName).all("emailtemplates").get("list");
			}
			
			service.findByDomainNameAndLang = function(domainName, lang) {
				return config.all(domainName).all("emailtemplates").all("findByDomainNameAndLang").getList({lang: lang});
			}

			service.edit = function(id) {
				return config.all("emailtemplate").all(id).get("edit");
			}

			service.save = function(o) {
				return config.all("emailtemplate").all(o.id).post(o);
			}
			
			service.continueLater = function(o) {
				return config.all("emailtemplate").all(o.id).all("continueLater").post(o);
			}
			
			service.cancelEdit = function(o) {
				return config.all("emailtemplate").all(o.id).all("cancelEdit").post(o);
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return config.all("emailtemplate").all(entityId).one("changelogs").get({ p: page });
			}
			
			service.viewDefaultEmailTemplate = function(id) {
				return config.all("defaultemailtemplates").one(id).get();
			}

			service.sendEmailTemplate = function(templateId, recipientGuid, recipientId, recipientEmail) {
				return config.all("quick-action-email").all(templateId).all("send-template").post('',{recipientGuid:recipientGuid, recipientId: recipientId, recipientEmail: recipientEmail});
			}

			service.findByDomainName = function(domainName) {
				return config.all(domainName).all("emailtemplates").all("findByDomainName").getList();
			}

			return service;
			
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);