
'use strict';

angular.module('lithium')
.factory('ProfileRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			var rest = function() {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/backoffice");
				}).service("profile");
			}

			let backofficeProfileRest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/backoffice/"+domainName+"/");
				}).service("profile");
			}
			
			service.get = function() {
				//BackofficeUserProfileController
				return rest().one().get();
			}
			
			service.save = function(user) {
				//BackofficeUserProfileController
				return rest().post(user);
			}
			
			service.saveAddress = function(address) {
				//BackofficeUserProfileController
				return rest().one().post("save-address", address);
			}
			
			service.savePassword = function(userId, passwd) {
				//BackofficeUserProfileController
				return rest().one().post("change-password", passwd);
			}

			service.resetPassword = function(domainName, playerGuid, type, token, tokenLength) {
				return backofficeProfileRest(domainName).one().all('password-reset').post('', {playerGuid:playerGuid, type:type, token:token, tokenLength:tokenLength});
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);