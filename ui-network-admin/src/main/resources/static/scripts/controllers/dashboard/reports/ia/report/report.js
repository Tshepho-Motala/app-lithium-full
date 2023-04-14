'use strict';

angular.module('lithium')
	.controller('ReportIa', ["$scope", "notify", "$state", "$rootScope", "$dt", "$translate", "$uibModal", "bsLoadingOverlayService", "ReportIaRest",
	function($scope, notify, $state, $rootScope, $dt, $translate, $uibModal, bsLoadingOverlayService, ReportIaRest) {
		let controller = this;
		
		let reportId = $state.params.reportId;
		$scope.report = {};
		$scope.reportFilters = [];
		$scope.reportActions = [];
		
		bsLoadingOverlayService.start({ referenceId: 'ia-report' });
		ReportIaRest.view(reportId).then(function(result) {
			$scope.report = result;
			$scope.report.prettyCron = prettyCron.toString($scope.report.current.cron.substring(2));
			bsLoadingOverlayService.stop({ referenceId: 'ia-report' });
		});
		
		bsLoadingOverlayService.start({ referenceId: 'ia-report-filters' });
		ReportIaRest.getFilters(reportId, false).then(function(result) {
			$scope.reportFilters = result;
			bsLoadingOverlayService.stop({ referenceId: 'ia-report-filters' });
		});
		
		bsLoadingOverlayService.start({ referenceId: 'ia-report-actions' });
		ReportIaRest.getActions(reportId, false).then(function(result) {
			$scope.reportActions = result;
			bsLoadingOverlayService.stop({ referenceId: 'ia-report-actions' });
		});

		let baseUrl = "services/service-affiliate-provider-ia/report/ia/" + reportId + "/runs/table?1=1";

		controller.table = $dt.builder()
		.column($dt.columnformatdatetime('startedOn').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.REPORT.STARTEDON')))
		.column($dt.linkscolumn("", [{
				permission: "report_ia",
				permissionType:"any", 
				permissionDomain: function(data) { return data.domainName; },
				title: "GLOBAL.ACTION.OPEN", 
				href: function(data) { return $state.href("^.run", { reportId:reportId, reportRunId:data.id })} 
			}]))
		.column($dt.column("totalRecords").withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.REPORT.TOTALRECORDS')))
		.column($dt.column("processedRecords").withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.REPORT.PROCESSEDRECORDS')))
		.column($dt.column("filteredRecords").withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.FILTEREDRECORDS')))
		.column($dt.column("actionsPerformed").withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.ACTIONSPERFORMED')))
		.column($dt.columnformatdatetime('completedOn').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.REPORT.COMPLETEDON')))
		.column($dt.columnformatdatetime('completed').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.BONUS.COMPLETED')))
		.column($dt.column('failed').withTitle($translate('UI_NETWORK_ADMIN.PUSHMSG.TBL.FAILED')))
		.column($dt.column('runGranularityString').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.ACTIONS.TABLE.RUN_GRANULARITY')))
		.column($dt.column('runGranularityOffset').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.ACTIONS.TABLE.RUN_GRANULARITY_OFFSET')))
		.column($dt.column('periodString').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PERIOD.NAME')))
		.post(true)
		.options(baseUrl)
		.nosearch()
		.order( [[ 0, "desc" ]] )
		.build();

		controller.refresh = function() {
			$state.reload();
		}
		
		controller.getReportActionLabelValuesByLabelCommaSepString = function(action, label) {
			let commaSepString = '';
			for (let i = 0; i < action.labelValueList.length; i++) {
				if (action.labelValueList[i].labelValue.label.name !== label) continue;
				let value = action.labelValueList[i].labelValue.value;
				if (commaSepString.length > 0) {
					commaSepString = commaSepString + ", ";
				}
				commaSepString = commaSepString + value;
			}
			return commaSepString;
		}
	}
]);
