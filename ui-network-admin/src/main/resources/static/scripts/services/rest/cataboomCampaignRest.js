'use strict';

angular.module('lithium-rest-cataboom-campaigns', ['restangular']).factory('rest-cataboom-campaign', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
		
			
			var rest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-casino-provider-cataboom/cataboomcampaigns/" + domainName +"/");
				});
			}
			
			service.save = function(domainName,model) {
				return rest(domainName).all('createCampaign').post(model);
			}
			
			service.viewCampaign = function(domainName,id) {
				return rest(domainName).one(id).get();
			}
			
			service.deleteCampaign = function(domainName,id) {
				return rest(domainName).all('delete').all(id).remove();
			}
			
			service.toggleEnable = function(domainName,id) {
				return rest(domainName).one('toggleEnable',id).post();
			}
			
			service.checkCatConfigured = function(domainName) {
				return rest(domainName).one('checkCatConfigured').get();
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);