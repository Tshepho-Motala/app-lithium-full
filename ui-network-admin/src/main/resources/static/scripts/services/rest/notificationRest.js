'use strict';

angular.module('lithium')
.factory('NotificationRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-notifications/admin';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});

			var backofficeConfig = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-notifications/backoffice');
			});
			
			service.findByDomainName = function(domainName) {
				return config.all('notification').all('findByDomainName').getList({domainName:domainName});
			}
			
			service.findByDomainNameAndName = function(domainName, name) {
				return config.all('notification').one('findByDomainNameAndName').get({domainName:domainName,name:name});
			}
			
			service.view = function(id) {
				return config.all('notification').one(id).get();
			}
			
			service.create = function(notification) {
				return config.all('notification').all('create').post(notification);
			}
			
			service.send = function(userGuid, notificationName) {
				return config.all('notification').all('send').post('', {userGuid:userGuid, notificationName:notificationName});
			}
			
			service.edit = function(notificationId, notification) {
				return config.all('notification').all(notificationId).all('modify').post(notification);
			}
			
			service.addChannel = function(notificationId, notificationChannel) {
				return config.all('notification').all(notificationId).all('addChannel').post(notificationChannel);
			}
			
			service.removeChannel = function(notificationId, notificationChannelId) {
				return config.all('notification').all(notificationId).all("removeChannel").all(notificationChannelId).remove();
			}
			
			service.modifyChannel = function(notificationId, notificationChannelId, notificationChannel) {
				return config.all('notification').all(notificationId).all("modifyChannel").all(notificationChannelId).post(notificationChannel);
			}
			
			service.allChannels = function() {
				return config.all('channel').all('all').getList();
			}
			
			service.viewInbox = function(id) {
				return config.all('inbox').one(id).get();
			}

			service.getNotificationTypes = () => {
				return backofficeConfig.all('notification-types').getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);