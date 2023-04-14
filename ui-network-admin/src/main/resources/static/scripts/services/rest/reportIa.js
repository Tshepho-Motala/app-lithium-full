'use strict';

angular.module('lithium')
.factory('ReportIaRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-affiliate-provider-ia/report';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.view = function(id) {
				return config.all("ia").one(id).get();
			}
			
			service.edit = function(id) {
				return config.all("ia").all(id).one("edit").get();
			}
			
			service.editPost = function(id, option, report) {
				return config.all("ia").all(id).all("edit").all(option).post(report);
			}
			
			service.run = function(reportId, id) {
				return config.all("ia").one(reportId).all("runs").one(id).get();
			}
			
			service.create = function(report) {
				return config.all("ia").post(report);
			}
			
			service.getFilters = function(reportId, edit) {
				return config.all("ia").all(reportId).all("filters").all(edit).getList();
			}
			
			service.getActions = function(reportId, edit) {
				return config.all("ia").all(reportId).all("actions").all(edit).getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
