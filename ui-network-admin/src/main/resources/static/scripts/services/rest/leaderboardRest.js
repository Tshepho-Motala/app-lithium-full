'use strict';

angular.module('lithium')
.factory('LeaderboardRest', ['Restangular', '$filter',
	function(Restangular, $filter) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-leaderboard/leaderboard';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.add = function(model) {
				return config.all('admin').all('add').post(model);
			}
			service.addConversion = function(leaderboardId, conversion, type) {
				console.log(leaderboardId, conversion, type);
				return config.all('admin').all('add').all('conversion').post({
					leaderboardId:leaderboardId, conversion:conversion, typeId:type
				});
			}
			service.addNotification = function(leaderboardId, bonusCode, notification, rank) {
				console.log(leaderboardId, bonusCode, notification, rank);
				return config.all('admin').all('add').all(leaderboardId+'').all('notification').post({
					bonusCode:bonusCode, notification:notification, rank:rank
				});
			}
			
			service.edit = function(model) {
				return config.all('admin').all('edit').all(model.id).post(model);
			}
			service.editConversion = function(id, conversion) {
				return config.all('admin').all('edit').all('conversion').post({
					id:id, conversion:conversion
				});
			}
			service.editNotification = function(leaderboardId, id, bonusCode, notification, rank) {
				return config.all('admin').all('edit').all(leaderboardId+'').all('notification').post({
					id:id, bonusCode:bonusCode, notification:notification, rank:rank
				});
			}
			
			service.toggle = function(id) {
				return config.all('admin').all('toggle').all(id+'').post();
			}
			service.enable = function(id) {
				return config.all('admin').all('enable').all(id+'').post();
			}
			
			service.findLeaderboardById = function(id) {
				return config.all('admin').all('find').get(id);
			}
			service.findLeaderboardHistoryById = function(id) {
				return config.all('admin').all('history').all('find').get(id);
			}
			
			service.recurrence = function(leaderboardId) {
				return config.all('admin').all('recurrence').get(leaderboardId+'');
			}
			return service;
		} catch (err) {
			console.error(err);
			throw err;
		}
	}
]);