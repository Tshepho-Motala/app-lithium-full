'use strict';

angular.module('lithium')
	.controller('DomainCataboomListController', ['$log', 'domain','$state', '$dt', '$filter', '$uibModal','$translate',
	function($log, domain, $state, $dt, $filter, $uibModal,$translate) {
		var controller = this;
		var url = 'services/service-casino-provider-cataboom/cataboomcampaigns/'+domain.name+'/table';
		//var url = 'services/service-casino-provider-cataboom/cataboomcampaigns/table';

		controller.domainCataboomTable = $dt.builder()
		.column($dt.column('campaignName').withTitle("Campaign Name"))
		.column($dt.column('token').withTitle("Token"))
		.column($dt.column('campaignUsername').withTitle("Campaign Username"))
		.column($dt.column('campaignPassword').withTitle("Campaign Password"))
		.column(
			$dt.labelcolumn(
				"",
				[{lclass: function(data) {
					if (data.enabled) {
						return 'success';
					} else {
						return 'primary'
					}
				},
				text: function(data) {
					if (data.enabled) {
						return 'ENABLED';
					} else {
						return 'DISABLED';
					}
				},
				uppercase:true
				}]
			)
		)
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.domains.domain.cataboomcampaigns.cataboomcampaign", { id:data.id });
						}
					}
				]
			)
		)
		.options(url)
		.order([0, 'asc'])
		.build();
		
		controller.add = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/cataboomcampaigns/add/add.html',
				controller: 'DomainCataboomAddModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domain: function () {
						return domain;
					}
				}
			});
			
			modalInstance.result.then(function(result) {
				controller.domainCataboomTable.instance.reloadData(function(){}, false);
			});
		}

	}
]);
