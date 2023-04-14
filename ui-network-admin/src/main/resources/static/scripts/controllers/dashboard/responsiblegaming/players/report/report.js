'use strict';

angular.module('lithium')
	.controller('ReportGamstop', ["$scope", "notify", "$state", "$rootScope", "$dt", "$translate", "$uibModal", "bsLoadingOverlayService", "ReportPlayersRest",
	function($scope, notify, $state, $rootScope, $dt, $translate, $uibModal, bsLoadingOverlayService, ReportPlayersRest) {
		let controller = this;

		let reportId = $state.params.reportId;
		$scope.report = {};
		$scope.reportFilters = [
			{field: "statusReason", operator: "equalTo", value: "GAMSTOP_SELF_EXCLUSION"}
		];
		$scope.reportActions = [];
		$scope.domainNames = []

		bsLoadingOverlayService.start({ referenceId: 'player-report' });
		ReportPlayersRest.view(reportId).then(function(result) {
			$scope.domainNames.push(result.domainName);
			$scope.report = result;
			if (angular.isDefined($scope.report.current.cron) &&
				$scope.report.current.cron !== null &&
				$scope.report.current.cron !== '') {
				$scope.report.prettyCron = prettyCron.toString($scope.report.current.cron.substring(2));
			}
			bsLoadingOverlayService.stop({ referenceId: 'player-report' });
		});
		
		bsLoadingOverlayService.start({ referenceId: 'player-report-filters' });
		ReportPlayersRest.getFilters(reportId, false).then(function(result) {
			$scope.reportFilters = result;
			bsLoadingOverlayService.stop({ referenceId: 'player-report-filters' });
		});
		
		bsLoadingOverlayService.start({ referenceId: 'player-report-actions' });
		ReportPlayersRest.getActions(reportId, false).then(function(result) {
			$scope.reportActions = result;
			bsLoadingOverlayService.stop({ referenceId: 'player-report-actions' });
		});

		let baseUrl = "services/service-report-players/report/players/" + reportId + "/runs/table?1=1";

		controller.table = $dt.builder()
		.column($dt.columnformatdatetime('startedOn').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.REPORT.STARTEDON')))
		.column($dt.linkscolumn("", [{
				permission: "GAMSTOP_VIEW",
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
		
		controller.getFilterOperator = function(filter) {
			if (filter.field === 'playerBirthday' ||
				filter.field === 'playerLastLoginDate' ||
				filter.field === 'playerCreatedDate') {
				switch (filter.operator) {
					case 'equalTo': return 'is (x days ago)';
					case 'lessThan': return 'before (x days ago)';
					case 'greaterThan': return 'after (x days ago)';
					default: return filter.operator;
				}
			} else {
				return filter.operator;
			}
		}
	}
]);
