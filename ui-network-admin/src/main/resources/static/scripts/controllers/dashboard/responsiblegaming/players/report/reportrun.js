'use strict';

angular.module('lithium')
    .controller('ReportRunPlayers', ["$scope", "notify", "$state", "$rootScope", "$dt", "$translate", "bsLoadingOverlayService", "ReportPlayersRest", "$http",
        function ($scope, notify, $state, $rootScope, $dt, $translate, bsLoadingOverlayService, ReportPlayersRest, $http) {
            let controller = this;

            const reportId = $state.params.reportId;
            const reportRunId = $state.params.reportRunId;

            $scope.reportId = reportId;
            $scope.reportRunId = reportRunId;

            $scope.reportRun = {};

            bsLoadingOverlayService.start({referenceId: 'player-reportrun'});

            ReportPlayersRest.run(reportId, reportRunId).then(function success(result) {
                console.log(result);
                $scope.reportRun = result;
                bsLoadingOverlayService.stop({referenceId: 'player-reportrun'});
            }, function fail(result) {
                bsLoadingOverlayService.stop({referenceId: 'player-reportrun'});
            });

            const baseUrl = "services/service-report-players/report/players/" + reportId + "/runs/" + reportRunId + "/results/table";

            controller.table = $dt.builder()
                .column($dt.column('username.value').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.USERINFO.USERNAME')))
                .column($dt.column('email.value').withTitle($translate('UI_NETWORK_ADMIN.USER.PROMOOPTOUT.EMAIL')))
                .column($dt.column('firstName.value').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.BVN.FIRSTNAME')))
                .column($dt.column('lastName.value').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.BVN.LASTNAME')))
                .column($dt.column('enabled').withTitle($translate('UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.ENABLED.NAME')))
                .column($dt.column('status.value').withTitle($translate('GLOBAL.FIELDS.STATUS')))
                .column($dt.column('emailValidated').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.EMAILVALIDATED')))
                .column($dt.column('residentialAddressLine1.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.LINE1')))
                .column($dt.column('residentialAddressLine2.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.LINE2')))
                .column($dt.column('residentialAddressLine3.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.LINE3')))
                .column($dt.column('residentialAddressCity.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.CITY')))
                .column($dt.column('residentialAddressAdminLevel1.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.REGION')))
                .column($dt.column('residentialAddressCountry.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.COUNTRY')))
                .column($dt.column('residentialAddressPostalCode.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.POSTALCODE')))
                .column($dt.column('postalAddressLine1.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.LINE1')))
                .column($dt.column('postalAddressLine2.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.LINE2')))
                .column($dt.column('postalAddressLine3.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.LINE3')))
                .column($dt.column('postalAddressCity.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.CITY')))
                .column($dt.column('postalAddressAdminLevel1.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.REGION')))
                .column($dt.column('postalAddressCountry.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.COUNTRY')))
                .column($dt.column('postalAddressPostalCode.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.RESIDENTIAL.POSTALCODE')))
                .column($dt.column('telephoneNumber.value').withTitle($translate('GLOBAL.FIELDS.TELEPHONENUMBER')))
                .column($dt.column('cellphoneNumber.value').withTitle($translate('GLOBAL.FIELDS.CELLPHONENUMBER')))
                .column($dt.columnformatdatetime('createdDate').withTitle($translate('GLOBAL.BONUS.TYPE.0')))
                .column($dt.column('signupBonusCode.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.SIGNUPBONUS')))
                .column($dt.columnformatdate('dateOfBirth').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.DOB')))
                .column($dt.column('dateOfBirthDay').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.DOBDAY')))
                .column($dt.column('dateOfBirthMonth').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.DOBMONTH')))
                .column($dt.column('dateOfBirthYear').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.DOBYEAR')))
                .column($dt.column('affiliateGuid.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.AFFILIATE')))
                .column($dt.column('bannerGuid.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.BANNER')))
                .column($dt.column('campaignGuid.value').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CAMPAIGN')))
                .column($dt.columncurrency('currentBalanceCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.CURRENTBALANCE')))
                .column($dt.columncurrency('periodOpeningBalanceCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.OPENINGBALANCE')))
                .column($dt.columncurrency('periodClosingBalanceCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.CLOSINGBALANCE')))
                .column($dt.columncurrency('currentBalanceCasinoBonusCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.CURRENTBALANCECASINOBONUS')))
                .column($dt.columncurrency('periodOpeningBalanceCasinoBonusCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.OPENINGBALANCECASINOBONUS')))
                .column($dt.columncurrency('periodClosingBalanceCasinoBonusCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.CLOSINGBALANCECASINOBONUS')))
                .column($dt.columncurrency('currentBalanceCasinoBonusPendingCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.CURRENTBALANCECASINOBONUSPENDING')))
                .column($dt.columncurrency('periodOpeningBalanceCasinoBonusPendingCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.OPENINGBALANCECASINOBONUSPENDING')))
                .column($dt.columncurrency('periodClosingBalanceCasinoBonusPendingCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.EMAILTEMPLATE.PLACEHOLDER.PLAYER.CLOSINGBALANCECASINOBONUSPENDING')))
                .column($dt.columncurrency('currentBalancePendingWithdrawalCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.PENDINGWITHDRAWAL.CURRENTBALANCE')))
                .column($dt.columncurrency('periodOpeningBalancePendingWithdrawalCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.PENDINGWITHDRAWAL.OPENINGBALANCE')))
                .column($dt.columncurrency('periodClosingBalancePendingWithdrawalCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.PENDINGWITHDRAWAL.CLOSINGBALANCE')))
                .column($dt.columncurrency('transferToPlayerBalancePendingWithdrawalAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL')))
                .column($dt.columncurrency('transferFromPlayerBalancePendingWithdrawalAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL')))

                .column($dt.columncurrency('depositAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.DEPOSITS')))
                .column($dt.column('depositCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.DEPOSITS')))
                .column($dt.columncurrency('depositFeeCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.DEPOSITFEES')))
                .column($dt.columncurrency('payoutAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.PAYOUTS')))
                .column($dt.column('payoutCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.PAYOUTS')))
                .column($dt.columncurrency('balanceAdjustAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.TAB.ADJUSTMENTS')))
                .column($dt.column('balanceAdjustCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.ADJUSTMENTS')))

                .column($dt.columncurrency('casinoBetAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.CASINOBETS')))
                .column($dt.column('casinoBetCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.VIRTUALBETS')))
                .column($dt.columncurrency('casinoWinAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.CASINOWINS')))
                .column($dt.column('casinoWinCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOWINS')))
                .column($dt.columncurrency('casinoNetAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.CASINONET')))

                .column($dt.columncurrency('virtualBetAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.VIRTUALBETS')))
                .column($dt.column('virtualBetCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOBETS')))
                .column($dt.column('virtualLossCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.VIRTUALLOSS')))
                .column($dt.columncurrency('virtualWinAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.VIRTUALWINS')))
                .column($dt.column('virtualWinCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.VIRTUALWINS')))
                .column($dt.columncurrency('virtualBetVoidAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.DASHBOARD.VIRTUALBETSVOIDED')))
                .column($dt.column('virtualBetVoidCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.VIRTUALBETSVOIDED')))

                .column($dt.columncurrency('casinoBonusBetAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.BETS')))
                .column($dt.column('casinoBonusBetCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOBONUSBETS')))
                .column($dt.columncurrency('casinoBonusWinAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.WINS')))
                .column($dt.column('casinoBonusWinCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOBONUSWINS')))
                .column($dt.columncurrency('casinoBonusNetAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.NET')))

                .column($dt.columncurrency('casinoBonusActivateAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.ACTIVATED')))
                .column($dt.columncurrency('casinoBonusTransferToBonusAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.TRANSFERTO')))
                .column($dt.columncurrency('casinoBonusTransferFromBonusAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.TRANSFERFROM')))
                .column($dt.columncurrency('casinoBonusCancelAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.CANCEL')))
                .column($dt.columncurrency('casinoBonusExpireAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.EXPIRE')))
                .column($dt.columncurrency('casinoBonusMaxPayoutExcessAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOMAXPAYOUTEXCESS')))
                .column($dt.column('casinoBonusPendingCount').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.COUNT.CASINOBONUSPENDING')))
                .column($dt.columncurrency('casinoBonusPendingAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.PENDING')))
                .column($dt.columncurrency('casinoBonusTransferToBonusPendingAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.TRANSFERTOPENDING')))
                .column($dt.columncurrency('casinoBonusTransferFromBonusPendingAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.TRANSFERFROMPENDING')))
                .column($dt.columncurrency('casinoBonusPendingCancelAmountCents', '$', 2).withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.CASINOBONUS.PENDINGCANCEL')))
                .column($dt.column('emailOptOut').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.OPTOUT.EMAIL')))
                .column($dt.column('smsOptOut').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.OPTOUT.SMS')))
                .column($dt.column('callOptOut').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.OPTOUT.CALL')))
                .column($dt.column('referralCode.value').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.REFERRALCODE")))
                .column($dt.column('gamstopStatus').withTitle($translate('UI_NETWORK_ADMIN.REPORTS.PLAYERS.REPORTRUN.GAMSTOPSTATUS')))
                .post(true)
                .options(baseUrl)
                .build();

            controller.back = function () {
                "Referral Code"
                $state.go("^.report", {reportId: reportId});
            }

            //This should possibly go live in a module for easy reuse and just take params
            controller.downloadCsv = function (data) {

                const baseUrl = 'services/service-report-players/report/players/' + reportId + '/runs/' + reportRunId + '/csv';
                const req = {
                    method: 'POST',
                    url: baseUrl,
                    headers: {'Authorization': 'Bearer ' + $rootScope.token},
                    params: {
                        accessKey: $scope.reportRun.accessKey
                    },
                    responseType: 'arraybuffer'
                }

                $http(req).success(function (data, status, headers) {
                    headers = headers();
                    const filename = headers['x-filename'];
                    const contentType = headers['content-type'];
                    const linkElement = document.createElement('a');
                    try {
                        const blob = new Blob([data], {type: contentType});
                        const url = window.URL.createObjectURL(blob);
                        linkElement.setAttribute('href', url);
                        linkElement.setAttribute("download", filename);

                        const clickEvent = new MouseEvent('click', {
                            'view': window,
                            'bubbles': true,
                            'cancelable': false
                        });
                        linkElement.dispatchEvent(clickEvent);
                    } catch (ex) {
                        console.log(ex);
                    }
                }).error(function (data) {
                    console.log(data);
                });
            }
            controller.refresh = function () {
                $state.reload();
            }

        }
    ]);
