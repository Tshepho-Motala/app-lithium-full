'use strict';

angular.module('lithium-rest-domain-limits', ['restangular']).factory('domainLimitsRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};

			service.GRANULARITY_WEEK = 4;
			service.GRANULARITY_DAY = 3;
			service.GRANULARITY_MONTH = 2;
			service.LIMIT_TYPE_WIN = 1;
			service.LIMIT_TYPE_LOSS = 2;
			service.TYPE_DEPOSIT_LIMIT = 3;

			var rest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-limit/backoffice/player-limit/v1/" + domainName +"/");
				});
			}
			var domainRest = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-domain');
			});

			service.findDomainLimit = function(domainName, granularity, type) {
				return rest(domainName).one('find-domain-limit').get({granularity: granularity, type: type});
			}

			service.setDomainLimit = function(domainName, granularity, amount, type) {
				return rest(domainName).one('set-domain-limit').get({granularity: granularity, amount: amount, type: type});
			}

			service.removeDomainLimit = function(domainName, granularity, type) {
				return rest(domainName).one('remove-domain-limit').remove({granularity: granularity, type: type});
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return domainRest.all('domain').all(domainName).all(entityId).one('changelogs').one('limits').get({ p: page });
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);
