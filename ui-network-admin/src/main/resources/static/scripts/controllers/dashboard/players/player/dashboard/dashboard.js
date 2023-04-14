'use strict'

angular.module('lithium').controller('PlayerDashboardController', ["$translate", "$log", "$scope", "$timeout", "user", "rest-domain",
	function($translate, $log, $scope, $timeout, user, domainRest) {
		$scope.selectedDomains = [ user.domain.name ];
		var localScope = $scope;

		localScope.disableGranularitySwitch = true;
		
		localScope.setGranularity = function (g) {
			localScope.ranges = {
				1: { dateStart : moment().subtract(20, 'years'), dateEnd: moment().add(1, "years") },
				2: { dateStart : moment().subtract(48, 'months'), dateEnd: moment().add(1, "months") },
				3: { dateStart : moment().subtract(365, 'days'), dateEnd: moment().add(1, "days") },
				4: { dateStart : moment().subtract(104, 'weeks'), dateEnd: moment().add(1, "weeks").startOf("isoWeeks") }
			};
			localScope.granularity = g;
			localScope.disableGranularitySwitch = true;
			$timeout(function () {
				localScope.disableGranularitySwitch = false;
			}, 1000);
		}
		
		domainRest.findByName(user.domain.name).then(function(domain) {
			var currency = { code: domain.currency, symbol: domain.currencySymbol, format: domain.currencySymbol+' '+"##.00" };

			localScope.dashboardData = [
				{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE_CASINO_BONUS_PENDING",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PENDING_BONUS_BALANCE",
					type: "balancesummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE_CASINO_BONUS",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUS_BALANCE",
					type: "balancesummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PLAYER_BALANCE",
					type: "balancesummary"
				},{
					type: "row v-reset-row "
				},{
					user: user,
					domains: [user.domain.name],
					currency: currency,
					accountCode: "PLAYER_BALANCE_PENDING_WITHDRAWAL",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PLAYER_BALANCE_WITHDRAWAL_PENDING",
					type: "balancesummary"
				},{
					type: "row v-reset-row "
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE,PLAYER_BALANCE_PENDING_WITHDRAWAL",
					inverse: true,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.DEPOSITS", tran: "CASHIER_DEPOSIT" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.WITHDRAWALS", tran: "CASHIER_PAYOUT" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.NETCASH" },
					type: "netsummary"
				},{
					type: "row v-reset-row "
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					color: "blue",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.VIRTUALBETS",
					transactionType: 'VIRTUAL_BET',
					tran: "ALL",
					link: 'dashboard.incentivegames.bets.list',
					type: "trantypesummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.VIRTUALWINS",
					transactionType: 'VIRTUAL_WIN',
					tran: "WIN",
					link: 'dashboard.incentivegames.bets.list',
					type: "trantypesummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "green",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.VIRTUALBETSVOIDED",
					transactionType: 'VIRTUAL_BET_VOIDED',
					type: "trantypesummary"
				},{
					type: "row v-reset-row "
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.SPORT_STAKE", trans: ["SPORTS_BET"]},
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.SPORT_RETURN", tran: "SPORTS_WIN" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.SPORT_MARGIN" },
					type: "netsummary"
				},{
					type: "row v-reset-row "
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE_PENDING_WITHDRAWAL",
					inverse: true,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL", trans: ["TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL"]},
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL", tran: "TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PLAYER_BALANCE_PENDING_WITHDRAWAL_NET" },
					type: "netsummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETS", trans: ["CASINO_BET", "CASINO_BET_ROLLBACK", "CASINO_NEGATIVE_BET"] },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINS", trans: ["CASINO_WIN", "CASINO_WIN_ROLLBACK"] },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONET" },
					type: "netsummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERTOBONUS", trans: ["TRANSFER_TO_CASINO_BONUS", "TRANSFER_FROM_CASINO_BONUS_PENDING", "CASINO_BONUS_PENDING"], ascontra: [false, true, true], accountCodes: ["PLAYER_BALANCE", "PLAYER_BALANCE_CASINO_BONUS", "CASINO_BONUS_PENDING"]},
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERFROMBONUS", tran: "TRANSFER_FROM_CASINO_BONUS" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSNET" },
					type: "netsummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE_CASINO_BONUS",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETSBONUS", trans: ["CASINO_BET", "CASINO_BET_ROLLBACK", "CASINO_NEGATIVE_BET"] },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINSBONUS", trans: ["CASINO_WIN", "CASINO_WIN_ROLLBACK"] },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONETBONUS" },
					type: "netsummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE_CASINO_BONUS",
					inverse: false,
					debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETSBONUS_FREESPIN", tran: "", ignore: "true" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINSBONUS_FREESPIN", tran: "CASINO_WIN_FREESPIN" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONETBONUS_FREESPIN" },
					type: "netsummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					nocalc: true, //means the credit and debit values will not be subtracted from oneanother, so it will be the raw credit/debit being displayed (note the tran_types are the same)
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BALANCE_ADJUST_DEBIT", tran: "BALANCE_ADJUST" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BALANCE_ADJUST_CREDIT", tran: "BALANCE_ADJUST" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BALANCE_ADJUST_NET" },
					type: "netsummary"
				},{
					user: user,
					domains: [ user.domain.name ],
					currency: currency,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					nocalc: true, //means the credit and debit values will not be subtracted from oneanother, so it will be the raw credit/debit being displayed (note the tran_types are the same)
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BONUS_VIRTUAL_DEBIT", tran: "MANUAL_BONUS_VIRTUAL" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BONUS_VIRTUAL_CREDIT", tran: "MANUAL_BONUS_VIRTUAL" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BONUS_VIRTUAL_NET" },
					type: "netsummary"
				}
			];

			// localScope.cashGraph = {
			// 	user: user,
			// 	id: "cashgraph",
			// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.DEPOSITS_WITHDRAWALS_HISTORY",
			// 	domains: [ user.domain.name ],
			// 	currency: currency,
			// 	accountCode: "PLAYER_BALANCE,PLAYER_BALANCE_PENDING_WITHDRAWAL",
			// 	inverse: true,
			// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.DEPOSITS", tran: "CASHIER_DEPOSIT" },
			// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.WITHDRAWALS", tran: "CASHIER_PAYOUT" },
			// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.NETCASH" }
			// };
			//
			// localScope.casinoGraph = {
			// 	user: user,
			// 	id: "casinograph",
			// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINO_HISTORY",
			// 	domains: [ user.domain.name ],
			// 	currency: currency,
			// 	accountCode: "PLAYER_BALANCE",
			// 	inverse: false,
			// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETS", tran: "CASINO_BET" },
			// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINS", tran: "CASINO_WIN" },
			// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONET" }
			// };
			//
			// localScope.bonusGraph = {
			// 	user: user,
			// 	id: "bonusgraph",
			// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSHISTORY",
			// 	domains: [ user.domain.name ],
			// 	currency: currency,
			// 	accountCode: "PLAYER_BALANCE_CASINO_BONUS",
			// 	inverse: true,
			// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERTOBONUS", tran: "TRANSFER_TO_CASINO_BONUS" },
			// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERFROMBONUS", tran: "TRANSFER_FROM_CASINO_BONUS" },
			// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSNET" }
			// };
			//
			// localScope.casinoBonusGraph = {
			// 	user: user,
			// 	id: "casinobonusgraph",
			// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINO_BONUS_HISTORY",
			// 	domains: [ user.domain.name ],
			// 	currency: currency,
			// 	accountCode: "PLAYER_BALANCE_CASINO_BONUS",
			// 	inverse: false,
			// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETSBONUS", tran: "CASINO_BET" },
			// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINSBONUS", tran: "CASINO_WIN" },
			// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONETBONUS" }
			// };
		
			localScope.setGranularity(2);
		
		});
	}
]);