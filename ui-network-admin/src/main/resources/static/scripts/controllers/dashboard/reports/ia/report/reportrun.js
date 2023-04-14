'use strict';

angular.module('lithium')
	.controller('ReportRunIa', ["$scope", "notify", "$state", "$rootScope", "$dt", "$translate", "bsLoadingOverlayService", "ReportIaRest", "$http",
	function($scope, notify, $state, $rootScope, $dt, $translate, bsLoadingOverlayService, ReportIaRest, $http) {
		var controller = this;

		var reportId = $state.params.reportId;
		var reportRunId = $state.params.reportRunId;
		
		$scope.reportId = reportId;
		$scope.reportRunId = reportRunId;

		$scope.reportRun = {};
		
		bsLoadingOverlayService.start({ referenceId: 'ia-reportrun' });
		
		ReportIaRest.run(reportId, reportRunId).then(function success(result) {
			console.log(result);
			$scope.reportRun = result;
			bsLoadingOverlayService.stop({ referenceId: 'ia-reportrun' });
		}, function fail(result) {
			bsLoadingOverlayService.stop({ referenceId: 'ia-reportrun' });
		});

		var baseUrl = "services/service-affiliate-provider-ia/report/ia/" + reportId + "/runs/" + reportRunId + "/results/table";

		//TODO translation

		controller.table = $dt.builder()
			.column($dt.column('username.value').withTitle("Username"))
			.column($dt.column('firstName.value').withTitle("First Name"))
			.column($dt.column('lastName.value').withTitle("Last Name"))
			.column($dt.column('enabled').withTitle("Enabled"))
			.column($dt.column('status.value').withTitle("Status"))
			.column($dt.columnformatdatetime('createdDate').withTitle("Signup Date"))
			.column($dt.column('affiliateGuid.value').withTitle("Affiliate"))
			.column($dt.column('bannerGuid.value').withTitle("Banner"))
			.column($dt.column('campaignGuid.value').withTitle("Campaign"))
			.column($dt.columncurrency('currentBalanceCents', '$', 2).withTitle("Current Balance"))
			.column($dt.columncurrency('periodOpeningBalanceCents', '$', 2).withTitle("Opening Balance"))
			.column($dt.columncurrency('periodClosingBalanceCents', '$', 2).withTitle("Closing Balance"))
			.column($dt.columncurrency('currentBalanceCasinoBonusCents', '$', 2).withTitle("Current Balance (Casino Bonus)"))
			.column($dt.columncurrency('periodOpeningBalanceCasinoBonusCents', '$', 2).withTitle("Opening Balance (Casino Bonus)"))
			.column($dt.columncurrency('periodClosingBalanceCasinoBonusCents', '$', 2).withTitle("Closing Balance (Casino Bonus)"))
			.column($dt.columncurrency('currentBalanceCasinoBonusPendingCents', '$', 2).withTitle("Current Balance (Casino Bonus Pending)"))
			.column($dt.columncurrency('periodOpeningBalanceCasinoBonusPendingCents', '$', 2).withTitle("Opening Balance (Casino Bonus Pending)"))
			.column($dt.columncurrency('periodClosingBalanceCasinoBonusPendingCents', '$', 2).withTitle("Closing Balance (Casino Bonus Pending)"))
			.column($dt.columncurrency('depositAmountCents', '$', 2).withTitle("Deposits"))
			.column($dt.column('depositCount').withTitle("Deposits (#)"))
			.column($dt.columncurrency('depositFeeCents', '$', 2).withTitle("Deposit Fees"))
			.column($dt.columncurrency('payoutAmountCents', '$', 2).withTitle("Payouts"))
			.column($dt.column('payoutCount').withTitle("Payouts (#)"))
			.column($dt.columncurrency('balanceAdjustAmountCents', '$', 2).withTitle("Adjustments"))
			.column($dt.column('balanceAdjustCount').withTitle("Adjustmentss (#)"))
			.column($dt.columncurrency('casinoBetAmountCents', '$', 2).withTitle("Casino Bets"))
			.column($dt.column('casinoBetCount').withTitle("Casino Bets (#)"))
			.column($dt.columncurrency('casinoWinAmountCents', '$', 2).withTitle("Casino Wins"))
			.column($dt.column('casinoWinCount').withTitle("Casino Wins (#)"))
			.column($dt.columncurrency('casinoNetAmountCents', '$', 2).withTitle("Casino Net"))
			.column($dt.columncurrency('casinoBonusBetAmountCents', '$', 2).withTitle("Casino Bonus Bets"))
			.column($dt.column('casinoBonusBetCount').withTitle("Casino Bonus Bets (#)"))
			.column($dt.columncurrency('casinoBonusWinAmountCents', '$', 2).withTitle("Casino Bonus Wins"))
			.column($dt.column('casinoBonusWinCount').withTitle("Casino Bonus Wins (#)"))
			.column($dt.columncurrency('casinoBonusNetAmountCents', '$', 2).withTitle("Casino Bonus Net"))
			.column($dt.columncurrency('casinoBonusActivateAmountCents', '$', 2).withTitle("Casino Bonus Activated"))
			.column($dt.columncurrency('casinoBonusTransferToBonusAmountCents', '$', 2).withTitle("Transfer To Casino Bonus"))
			.column($dt.columncurrency('casinoBonusTransferFromBonusAmountCents', '$', 2).withTitle("Transfer From Casino Bonus"))
			.column($dt.columncurrency('casinoBonusCancelAmountCents', '$', 2).withTitle("Casino Bonus Cancel"))
			.column($dt.columncurrency('casinoBonusExpireAmountCents', '$', 2).withTitle("Casino Bonus Expire"))
			.column($dt.columncurrency('casinoBonusMaxPayoutExcessAmountCents', '$', 2).withTitle("Casino Max Payout Excess"))
			.column($dt.column('casinoBonusPendingCount').withTitle("Casino Bonus Pending (#)"))
			.column($dt.columncurrency('casinoBonusPendingAmountCents', '$', 2).withTitle("Casino Bonus Pending"))
			.column($dt.columncurrency('casinoBonusTransferToBonusPendingAmountCents', '$', 2).withTitle("Transfer To Casino Bonus Pending"))
			.column($dt.columncurrency('casinoBonusTransferFromBonusPendingAmountCents', '$', 2).withTitle("Transfer From Casino Bonus Pending"))
			.column($dt.columncurrency('casinoBonusPendingCancelAmountCents', '$', 2).withTitle("Casino Bonus Pending Cancel"))
			.post(true)
			.options(baseUrl)
		.build();
		
		controller.back = function() {
			$state.go("^.report", { reportId: reportId });
		}
		
		controller.downloadxls = function() {
			send(reportId, reportRunId, 'xls');
		}

		controller.downloadreg = function() {
			send(reportId, reportRunId, 'csvreg');
		}

		controller.downloadsales = function() {
			send(reportId, reportRunId, 'csvsales');
		}
		
		controller.refresh = function() {
			$state.reload();
		}

		function send(reportId, reportRunId, downloadType) {
			var baseUrl = 'services/service-affiliate-provider-ia/report/ia/'+reportId+'/runs/'+reportRunId+'/'+downloadType;
			var req = {
				method: 'POST',
				url: baseUrl,
				headers: {
					'Authorization': 'Bearer '+$rootScope.token
				},
				params: {
					accessKey: $scope.reportRun.accessKey
				},
				responseType: 'arraybuffer'
			}
			$http(req).success(function (data, status, headers) {
				headers = headers();
				var filename = headers['x-filename'];
				var contentType = headers['content-type'];

				var linkElement = document.createElement('a');
				try {
					var blob = new Blob([data], { type: contentType });
					var url = window.URL.createObjectURL(blob);

					linkElement.setAttribute('href', url);
					linkElement.setAttribute("download", filename);

					var clickEvent = new MouseEvent("click", {
						"view": window,
						"bubbles": true,
						"cancelable": false
					});
					linkElement.dispatchEvent(clickEvent);
				} catch (ex) {
					console.log(ex);
				}
			}).error(function (data) {
				console.error(data);
			});
		}
	}
]);
