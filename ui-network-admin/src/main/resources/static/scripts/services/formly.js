'use strict';


angular.module('lithium')

.factory('formlyValidators', ['$http', '$log', '$q',
	function($http, $log, $q) {
		var service = {};
		
		service.pattern = function(pattern, message) {
			return {
				expression: function($viewValue, $modelValue, scope) {
					if (!$viewValue || $viewValue.length == 0) return true;
					return pattern.test($viewValue);
				},
				message: '"'+message+'"'
			}
		};
		
		service.firstName = function(message) {
			return service.pattern(/^[^0-9\[\]\{\}\'\"\;\:\/\?\.\>\,\<\=\+\_\§\±\!\@\#\$\%\^\&\*\(\)\`\~\|\\]*$/, message);
		};

		service.lastNamePrefix = function(message) {
			return service.pattern(/(^[a-zA-Z ]*$)/, message);
		};

		service.lastName = function(message) {
			return service.pattern(/^[^0-9\[\]\{\}\'\"\;\:\/\?\.\>\,\<\=\+\_\§\±\!\@\#\$\%\^\&\*\(\)\`\~\|\\]*$/, message);
		};
		
		service.email = function() {
			var r = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
			return service.pattern(r, "UI_NETWORK_ADMIN.VALIDATION.EMAIL_PATTERN");
		}
		
		service.telephone = function() {
			return service.pattern(/^[0-9 +]*$/, "GLOBAL.VALIDATION.TELEPHONE");
		}

		service.password = function() {
			return {
				expression: function($viewValue, $modelValue, scope) {
					if (!$viewValue|| $viewValue.length == 0) return true;
					return (
						// /[A-Z]/.test($viewValue) &&
						// /[a-z]/.test($viewValue) &&
						// /[0-9]/.test($viewValue));
						/[a-zA-Z0-9]/.test($viewValue));
				},
				message: '"GLOBAL.VALIDATION.PASSWORD"'
			}
		}

		return service;
	}])
	
.factory('formlyFieldTemplates', ['formlyValidators', '$log', '$q',
	function(formlyValidators, $log, $q) {
		var service = {};
		
		return service;
	}])

;
