'use strict';

angular.module('lithium')
	.controller('ResponsibleGamingPlayersList', ["notify", "$state", "$rootScope", "$dt", "$translate",
	function(notify, $state, $rootScope, $dt, $translate) {
		let controller = this;
		
		const baseUrl = "services/service-report-players/report/players/list/table?1=1&action=checkGamstopStatus";
		controller.table = $dt.builder()
		.column($dt.column('current.name').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORT.FIELDS.NAME.NAME")))
		.column($dt.linkscolumn("", [{ 
				permission: "GAMSTOP_VIEW",
				permissionType:"any",
				permissionDomain: function(data) { return data.domainName; }, 
				title: "GLOBAL.ACTION.OPEN", 
				href: function(data) { 
					return $state.href("^.report", {
							reportId:data.id
					})
				} 
			}]))			

		.column($dt.column('current.description').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORT.FIELDS.DESCRIPTION.NAME")))
		.column($dt.column('domainName').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORT.FIELDS.DOMAIN.NAME")))
		.column($dt.columnformatdatetime('scheduledDate').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.NEXT_RUN")))
		.column($dt.columnformatdatetime('running.startedOn').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RUNNING_SINCE")))
		.column($dt.columnformatdatetime('lastCompleted.completedOn').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.LAST_COMPLETED")))
		.column($dt.columnformatdatetime('lastFailed.completedOn').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.LAST_FAILED")))

		.options(baseUrl)
		.build();
		
		controller.refreshTable = function() {
			controller.table.instance.reloadData(function(){}, false);
		}
	}
]);
