'use strict';

angular.module('lithium')
	.factory('KycVerifyRest', ['$log', 'Restangular',
		function($log, Restangular) {
			try {
				var service = {};

				var rest = function() {
					return Restangular.withConfig(function(RestangularConfigurer) {
						RestangularConfigurer.setBaseUrl("services/service-kyc/backoffice");
					});
				}

				service.verify = function(verifyRequest) {
					return rest().all("kyc").all("verify").post(verifyRequest);
				}

				service.banks = function(userGuid,  provider) {
					return rest().all("kyc").all("banks").getList({ userGuid: userGuid, provider: provider});
				}

				return service;
			} catch (err) {
				$log.error(err);
				throw err;
			}
		}
	]);
