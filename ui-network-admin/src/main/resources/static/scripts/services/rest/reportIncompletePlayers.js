'use strict';

angular.module('lithium')
.factory('ReportIncompletePlayersRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-report-incomplete-players/report';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.view = function(id) {
				return config.all("players").one(id).get();
			}
			
			service.edit = function(id) {
				return config.all("players").all(id).one("edit").get();
			}
			
			service.editPost = function(id, option, report) {
				return config.all("players").all(id).all("edit").all(option).post(report);
			}
			
			service.run = function(reportId, id) {
				return config.all("players").one(reportId).all("runs").one(id).get();
			}
			
			service.create = function(report) {
				return config.all("players").post(report);
			}
			
			service.getFilters = function(reportId, edit) {
				return config.all("players").all(reportId).all("filters").all(edit).getList();
			}
			
			service.getActions = function(reportId, edit) {
				return config.all("players").all(reportId).all("actions").all(edit).getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);