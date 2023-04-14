'use strict';

angular.module('lithium')
	.controller('DomainSettingsHistoryController', ['domain', 'notify', '$scope', 'errors', '$dt', '$uibModal', '$translate', '$state', 'DTOptionsBuilder',
	function(domain, notify, $scope, errors, $dt, $uibModal, $translate, $state, DTOptionsBuilder) {
		var controller = this;
		var url = 'services/service-domain/domain/settings/'+domain.name+'/history/table?1=1';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
		controller.domainSettingsHistoryTable = $dt.builder()
		.column($dt.columnformatdatetime('creationDate').withTitle("Creation Date"))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "domain_settings_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.domains.domain.settings.view", { domainRevisionId:data.id });
					}
				}
			]
		))
		.options(url)
		.options({url: url, type: 'GET'}, null, dtOptions, null)
		.build();
	}
]);
