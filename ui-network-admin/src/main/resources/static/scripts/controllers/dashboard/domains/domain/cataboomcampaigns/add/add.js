'use strict';

angular.module('lithium').controller('DomainCataboomAddModal', ['$uibModalInstance', 'domain', 'notify', 'errors', 'bsLoadingOverlayService','rest-cataboom-campaign',
function ($uibModalInstance, domain, notify, errors, bsLoadingOverlayService, cataboomCampaignRest) {
	var controller = this;
	
	controller.referenceId = 'addcataboom-overlay';
	
	controller.model = {};
	
	controller.fields = [
		{
			type: 'checkbox',
			key: 'enabled',
			templateOptions: {
				label: 'Enabled', description: 'enable/disable the campaign'
			}
			
		},
		{
			className : 'col-xs-12',
			key: "token",
			type: "input",
			templateOptions: {
				label: "api key/token", description: "api key/token", placeholder: "",
				required: true
			}
		
		},
		{
			className : 'col-xs-12',
			key: "campaignUsername",
			type: "input",
			templateOptions: {
				label: "Campaign Username", description: "Cataboom campaign username", placeholder: "",
				required: true
			}
		
		},
		
		{
			className : 'col-xs-12',
			key: "campaignPassword",
			type: "input",
			templateOptions: {
				label: "Campaign Password", description: "Cataboom campaign password", placeholder: "",
				required: true
			}
		
		},
		
		{
			className : 'col-xs-12',
			key: "campaignName",
			type: "input",
			templateOptions: {
				label: "Campaign id", description: "Cataboom campaign id", placeholder: "",
				required: true
			}
		
		}
		
	
		
	]
	
	
	controller.save = function() {
		console.log(controller.model);
		cataboomCampaignRest.save(domain.name,controller.model).then(function(response) {
			if (response._status !== 0) {
				notify.error(response._message);
			} else {
				notify.success('Added Campaign Successfully');
			}
			$uibModalInstance.close(response.plain());
		}).catch(function() {
			errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.ADD.ERROR', false);
		}).finally(function() {
		});
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);

