'use strict';

angular.module('lithium')
	.controller('ReportRunIncompletePlayers', ["$scope", "notify", "$state", "$rootScope", "$dt", "$translate", "bsLoadingOverlayService", "ReportIncompletePlayersRest",
	function($scope, notify, $state, $rootScope, $dt, $translate, bsLoadingOverlayService, ReportIncompletePlayersRest) {
		var controller = this;

		var reportId = $state.params.reportId;
		var reportRunId = $state.params.reportRunId;
		
		$scope.reportId = reportId;
		$scope.reportRunId = reportRunId;

		$scope.reportRun = {};
		
		bsLoadingOverlayService.start({ referenceId: 'player-reportrun' });

		ReportIncompletePlayersRest.run(reportId, reportRunId).then(function success(result) {
			console.log(result);
			$scope.reportRun = result;
			bsLoadingOverlayService.stop({ referenceId: 'player-reportrun' });
		}, function fail(result) {
			bsLoadingOverlayService.stop({ referenceId: 'player-reportrun' });
		});

		var baseUrl = "services/service-report-incomplete-players/report/players/" + reportId + "/runs/" + reportRunId + "/results/table";

		//TODO translation

		controller.table = $dt.builder()
		.column($dt.column('username.value').withTitle("Username"))
		.column($dt.column('email.value').withTitle("Email"))
		.column($dt.column('firstName.value').withTitle("First Name"))
		.column($dt.column('lastName.value').withTitle("Last Name"))
		.column($dt.column('cellphoneNumber.value').withTitle("Cellphone"))
		.column($dt.columnformatdatetime('createdDate').withTitle("Signup Date"))
		.column($dt.column('gender.value').withTitle("Gender"))
		.column($dt.column('stage.value').withTitle("Stage"))
		
		.post(true)
		.options(baseUrl)
		.build();
		
		controller.back = function() {
			$state.go("^.report", { reportId: reportId });
		}
		
		controller.downloadxls = function() {
			console.log("Downloading");
			window.location = 'services/service-report-incomplete-players/report/players/'+reportId+'/runs/'+reportRunId+'/xls?accessKey='
				+ $scope.reportRun.accessKey;
		}
		
		controller.refresh = function() {
			$state.reload();
		}

	}
]);