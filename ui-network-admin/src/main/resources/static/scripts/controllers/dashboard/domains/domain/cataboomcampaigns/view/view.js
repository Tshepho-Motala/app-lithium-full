'use strict';

angular.module('lithium')
	.controller('DomainCataboomViewController', ["campaign", "domain", "$log", "$scope", "$translate", "$state", "notify",'rest-cataboom-campaign',
	function( campaign, domain, $log, $scope, $translate, $state , notify, cataboomCampaignRest) {
		var controller = this;
		controller.model = campaign;
		controller.model.id = campaign.id;
		controller.modelOriginal = angular.copy(campaign);
		controller.options = { formState: { readOnly: true } };
		
		controller.fields = [
			
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
			
		
			
		];
		
		controller.onEdit = function() {
			controller.options.formState.readOnly = false;
		}
		
		controller.onCancel = function() {
			controller.onReset();
			controller.options.formState.readOnly = true;
		}
		controller.onReset = function() {
			controller.model = angular.copy(controller.modelOriginal);
			controller.model.code = campaign.id;
		}
		
		controller.toggleEnable = function() {
			cataboomCampaignRest.toggleEnable(domain.name, controller.model.id).then(function(response) {
				if (response._status === 0) {
					if (controller.model.enabled) {
						notify.success('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DISABLE.SUCCESS');
					} else {
						notify.success('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ENABLE.SUCCESS');
					}
					controller.model = response.plain();
				} else {
					if (controller.model.enabled) {
						notify.error('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DISABLE.ERROR');
					} else {
						notify.error('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ENABLE.ERROR');
					}
				}
			}).catch(function(error) {
				if (controller.model.enabled) {
					errors.catch('UI_NETWORK_ADMIN.DOMAIN.AVATARS.DISABLE.ERROR');
				} else {
					errors.catch('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ENABLE.ERROR');
				}
			});
		}
		
		controller.onSubmit = function() {
			console.log("save");
			cataboomCampaignRest.save(domain.name,controller.model).then(function(response) {
				console.log(response.plain());
				notify.success("Saved Succesfully.");
				controller.model = response.plain();
				//controller.model.code = response.campaign.id;
				controller.modelOriginal = angular.copy(response.plain());
				controller.options.formState.readOnly = true;
			});
		}
		
		controller.deleteCampaign = function() {//put campaign inside
//			$translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.CONFIRM').then(function(response) {
//				if (window.confirm(response)) {
			cataboomCampaignRest.deleteCampaign(domain.name, controller.model.id).then(function(response) {
						if (response._status !== 0) {
							notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR');
						} else {
							notify.success('Successfully deleted Campaign');
							$state.go("dashboard.domains.domain.cataboomcampaigns")
						}
					}).catch(function() {
						errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR', false);
					}).finally(function() {
					});
//				}
//			}).catch(function() {
//				notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR');
//			});
		}

		

	}
]);