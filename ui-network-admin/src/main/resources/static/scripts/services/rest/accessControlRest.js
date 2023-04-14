'use strict';

angular.module('lithium-rest-accesscontrol', ['restangular']).factory('accessControlRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var accessControlService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-access');
			});
			
			service.findAllListTypes = function() {
				return accessControlService.all('listTypes').all('find').all('all').getList();
			}
			
			service.addList = function(list) {
				return accessControlService.all('lists').all('create').post(list);
			}
			
			service.findByDomainNameAndListTypeName = function(domainName, listTypeName, enabled) {
				return accessControlService.all('lists').all('find').all(domainName).all(listTypeName).all(enabled).getList();
			}
			
			service.findListById = function(id) {
				return accessControlService.one('list', id).get();
			}
			
			service.addListValue = function(id, data) {
				return accessControlService.one('list', id).all('addvalue').post(data);
			}
			
			service.removeListValue = function(id, valueId) {
				return accessControlService.one('list', id).all('removevalue').post(valueId);
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return accessControlService.one('list', entityId).one('changelogs').get({ p: page });
			}
			
			service.toggleEnable = function(id) {
				return accessControlService.one('list', id).all('toggleEnable').post();
			}

			service.browsers = function() {
				return accessControlService.all('frontend').all('helper').all('browsers').getList();
			}
			
			service.operatingSystems = function() {
				return accessControlService.all('frontend').all('helper').all('operatingSystems').getList();
			}

			service.duplicateTypes = function() {
				return accessControlService.all('frontend').all('helper').all('duplicate-types').getList();
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);
