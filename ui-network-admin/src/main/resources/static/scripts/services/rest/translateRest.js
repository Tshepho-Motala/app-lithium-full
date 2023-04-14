'use strict';

angular.module('lithium-rest-translate', ['restangular'])
.factory('rest-translate', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var translateService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-translate');
			});

			service.getTranslationsById = function(id, domainName) {
				return translateService.all("apiv2").all("translations").all(domainName).all("get").all(id).getList();
			}

			service.getKeyIdByCode = function(code, domainName) {
				return translateService.all("apiv2").all("translations").all(domainName).all("get").one("key").get({code: code});
			}

			service.getAllLanguages = function() {
				return translateService.all("apiv1").all("languages").all("all").getList();
			}

			service.addTranslation = function(keyId, domainName, locale2, value) {
				return translateService.all("apiv2").all("translations").all(domainName).all("add").post({keyId: keyId, domainName: domainName, language: locale2, value: value})
			}

			service.addTranslationKey = function (model, domainName, subModule){
				return translateService.all("apiv2").all("translations").all(domainName).all(subModule).all("key").all("create").post(model)
			}

			service.editTranslation = function (domainName, valueId, value) {
				return translateService.all("apiv2").all("translations").all(domainName).all("edit").post({valueId: valueId, value:value});
			}

			service.deleteTranslation = function (domainName, valueId) {
				return translateService.all("apiv2").all("translations").all(domainName).all("delete").post({valueId: valueId});
			}

			service.deleteTranslationKeyAndValues = function (domainName, key) {
				return translateService.all("apiv2").all("translations").all(domainName).all("remove").all("user-defined").remove({key: key});
			}

			service.changelogs = function(domainName, entityId, page) {
				return translateService.one('apiv2').all("translations").all(domainName).one('changelogs').get({ p: page });
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);