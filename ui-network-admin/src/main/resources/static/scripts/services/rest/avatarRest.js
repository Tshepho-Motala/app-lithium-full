'use strict';

angular.module('lithium')
.factory('AvatarRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-avatar';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.add = function(domainName, avatar) {
				return config.all('admin').all('avatar').all(domainName).all('add').post(avatar);
			}
			
			service.view = function(domainName, avatarId) {
				return config.all('admin').all('avatar').all(domainName).one('view', avatarId).get();
			}
			
			service.deleteById = function(domainName, avatarId) {
				return config.all('admin').all('avatar').all(domainName).one('delete', avatarId).remove();
			}
			
			service.toggleEnable = function(domainName, avatarId) {
				return config.all('admin').all('avatar').all(domainName).one('toggleEnable', avatarId).post();
			}
			
			service.setAsDefault = function(domainName, avatarId) {
				return config.all('admin').all('avatar').all(domainName).one('setAsDefault', avatarId).post();
			}
			
			service.getUserAvatar = function(domainName, userName) {
				return config.all('useravatar').all(domainName).one(userName).get();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);