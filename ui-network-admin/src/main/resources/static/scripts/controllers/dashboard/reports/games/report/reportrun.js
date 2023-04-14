'use strict';

angular.module('lithium')
	.controller('ReportRunGames', ["$scope", "notify", "$state", "$rootScope", "$dt", "$translate", "bsLoadingOverlayService", "ReportGamesRest", 
	function($scope, notify, $state, $rootScope, $dt, $translate, bsLoadingOverlayService, ReportGamesRest) {
		let controller = this;

		let reportId = $state.params.reportId;
		let reportRunId = $state.params.reportRunId;
		
		$scope.reportId = reportId;
		$scope.reportRunId = reportRunId;

		$scope.reportRun = {};
		
		bsLoadingOverlayService.start({ referenceId: 'game-reportrun' });
		
		ReportGamesRest.run(reportId, reportRunId).then(function success(result) {
			console.log(result);
			$scope.reportRun = result;
			bsLoadingOverlayService.stop({ referenceId: 'game-reportrun' });
		}, function fail(result) {
			bsLoadingOverlayService.stop({ referenceId: 'game-reportrun' });
		});

		let baseUrl = "services/service-report-games/report/games/" + reportId + "/runs/" + reportRunId + "/results/table";

		controller.table = $dt.builder()
		.column($dt.column('name.value').withTitle($translate.instant('UI_NETWORK_ADMIN.ACCESSCONTROL.RULES.ADD.BASIC.NAME.LABEL')))
		.column($dt.column('internalId').withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.ACTIONS.TABLE.INTERNAL_ID')))
		.column($dt.column('providerId.value').withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.ACTIONS.TABLE.PROVIDER_ID')))
		.column($dt.column('providerName.value').withTitle($translate.instant('UI_NETWORK_ADMIN.PROVIDER.FIELDS.NAME.NAME')))
		.column($dt.column('enabled.value').withTitle($translate.instant('UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.ENABLED.NAME')))

		.column($dt.columncurrency('casinoBetAmountCents', '$', 2).withTitle($translate.instant('UI_NETWORK_ADMIN.DASHBOARD.CASINOBETS')))
		.column($dt.column('casinoBetCount').withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.VIRTUALBETS')))
		.column($dt.columncurrency('casinoWinAmountCents', '$', 2).withTitle($translate.instant('UI_NETWORK_ADMIN.DASHBOARD.CASINOWINS')))
		.column($dt.column('casinoWinCount').withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOWINS')))
		.column($dt.columncurrency('casinoNetAmountCents', '$', 2).withTitle($translate.instant('UI_NETWORK_ADMIN.DASHBOARD.CASINONET')))
		.column($dt.column('casinoBonusBetCount').withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOBONUSBETS')))
		.column($dt.columncurrency('casinoBonusWinAmountCents', '$', 2).withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.WINS')))
		.column($dt.column('casinoBonusWinCount').withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOBONUSWINS')))
		.column($dt.columncurrency('casinoBonusNetAmountCents', '$', 2).withTitle($translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.NET')))
		
		.post(true)
		.options(baseUrl)
		.build();
		
		controller.back = function() {
			$state.go("^.report", { reportId: reportId });
		}
		
		controller.downloadxls = function() {
			window.location = 'services/service-report-games/report/games/'+reportId+'/runs/'+reportRunId+'/xls?accessKey='
				+ $scope.reportRun.accessKey;
		}
		
		controller.refresh = function() {
			$state.reload();
		}

	}
]);
