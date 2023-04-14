'use strict';

angular.module('lithium')
.factory('RAFRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-raf/admin';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.findReferralByPlayerGuid = function(domainName, userName) {
				return config.all('referral').all('findByPlayerGuid').one(domainName).get({userName: userName});
			}
			
			service.getConfiguration = function(domainName) {
				return config.all(domainName).one('configuration').get();
			}
			
			service.modifyConfiguration = function(domainName, c) {
				return config.all(domainName).all("configuration").post(c);
			}

			service.enableAutoConvertPlayer=function(domainName,request){
				return config.all(domainName).all("configuration").all("player-auto-convert").post(request);
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
