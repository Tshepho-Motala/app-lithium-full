'use strict';

angular.module('lithium')
	.controller('ReportPlayersList', ["notify", "$state", "$rootScope", "$dt", "$translate", "$uibModal",
	function(notify, $state, $rootScope, $dt, $translate, $uibModal) {
		let controller = this;

		let baseUrl = "services/service-report-players/report/players/list/table?1=1";
		
		controller.table = $dt.builder()
		.column($dt.column('current.name').withTitle($translate("UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORT.FIELDS.NAME.NAME")))
		.column($dt.linkscolumn("", [{ 
				permission: "report_players", 
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
		
//		controller.addModal = function() {
//			var modalInstance = $uibModal.open({
//				animation: true,
//				ariaLabelledBy: 'modal-title',
//				ariaDescribedBy: 'modal-body',
//				templateUrl: 'scripts/controllers/dashboard/reports/players/report/report-create.html',
//				controller: 'ReportPlayersAddModal',
//				controllerAs: 'controller',
//				size: 'md',
//				resolve: {
//					domains: function($userService) {
//						return $userService.domainsWithRole("REPORT_PLAYERS");
//					},
//					loadMyFiles: function($ocLazyLoad) {
//						return $ocLazyLoad.load({
//							name:'lithium',
//							files: [ 'scripts/controllers/dashboard/reports/players/report/report-create.js' ]
//						})
//					}
//				}
//			});
//			
//			modalInstance.result.then(function (data) {
//				$state.go("^.report", { reportId:data.id });
//			});
//		}
	}
]);
