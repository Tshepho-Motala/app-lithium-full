'use strict';


angular.module('lithium')

.factory('notify', ['growl', '$log',
	function(growl, $log) {
		var service = {};
		
		service.success = function (message) {
			var config = {};
			growl.success(message, config);
		}
		service.success = function (message, config) {
			growl.success(message, config);
		}
		
		service.info = function (message) {
			var config = {};
			growl.info(message, config);
		}
		service.info = function (message, config) {
			growl.info(message, config);
		}
		
		service.warning = function (message) {
			var config = {};
			growl.warning(message, config);
		}
		service.warning = function (message, config) {
			growl.warning(message, config);
		}
		
		service.error = function (message) {
			var config = {};
			growl.error(message, config);
		}
		service.error = function (message, config) {
			growl.error(message, config);
		}
		
		return service;
	}])
;