'use strict';

angular.module('lithium')
	.factory('AffiliateRest', ['$log', 'Restangular',
		function($log, Restangular) {
			try {
				var service = {};
				var config = function(domainName) {
					return Restangular.withConfig(function(RestangularConfigurer) {
						RestangularConfigurer.setBaseUrl("services/service-user/backoffice/"+domainName+"/affiliates");
					});
				}

				service.findAffiliateByPlayerId = function(domainName, id) {
					var response = config(domainName).all(id).get("references");

					return response;
				}

				return service;
			} catch (err) {
				$log.error(err);
				throw err;
			}
		}
	]);
