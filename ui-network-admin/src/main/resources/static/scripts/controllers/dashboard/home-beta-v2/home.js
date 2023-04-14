'use strict';

angular.module('lithium')
	.controller('home-beta-v2', ["$userService", "$http", "$translate", "$log", "$scope", "$timeout", "rest-domain", "$rootScope", "$interval",
	function($userService, $http, $translate, $log, $scope, $timeout, domainRest, $rootScope, $interval) {
		var controller = this;

		controller.disableGranularitySwitch = true;
		
		var updateSeconds = null;
		if ($rootScope.settings !== undefined && $rootScope.settings !== null
				&& $rootScope.settings.dashboardUpdateSeconds !== undefined && $rootScope.settings.dashboardUpdateSeconds !== null) {
			updateSeconds = $rootScope.settings.dashboardUpdateSeconds;
		}
		
		controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "DASHBOARD"]);
		controller.isTestUser = false;

		controller.accountTypes = [
			{ "id": 1,  "name": "All Accounts",   "testUsers": null },
			{ "id": 2,  "name": "Only Test Accounts",  "testUsers": true },
			{ "id": 3,  "name": "Only Real Accounts",    "testUsers": false }
		];

		$scope.theAccountType = {};
		$scope.theAccountType.selected  = { "id": 3,  "name": "Only Real Accounts",    "testUsers": false };

		 // controller.currency = { code: "USD", symbol: '$', format: "$ ##.00" };
		//TODO: need to decide how to handle the currency display for multiple domains later
		
		var defaultDomain = false;
		angular.forEach(controller.domains, function(d) {
			if (d.name === 'default') defaultDomain = true;
		});

		controller.clearSelectedDomain = function() {
			controller.selectedDomains = [];
			controller.selectedDomain = null;
		}

		controller.domainSelect = function(item) {
			controller.selectedDomains = [item.name];
			controller.selectedDomain = item.name;

			if (angular.isDefined(controller.selectedDomains[0])) {
				domainRest.findByName(controller.selectedDomains[0]).then(function(domain) {
					controller.currency = {};
					controller.currency.code = domain.currency;
					controller.currency.symbol = domain.currencySymbol;
					controller.currency.format = domain.currencySymbol+' '+"##.00";

					// We need to wait until we have a currency.
					controller.setScope();
				});
			}
		};

		//implement the filter logic by isTestUser
		 controller.isTestUserSelect = function (item){
			 controller.isTestUser = item.testUsers;
			 controller.setScope();
		 }

		if (controller.domains.length === 1) {
			controller.domainSelect(controller.domains[0]);
		}

		$scope.setGranularity = function (g) {
			$scope.ranges = {
				1: { dateStart : moment().subtract(20, 'years'), dateEnd: moment().add(1, "years") },
				2: { dateStart : moment().subtract(48, 'months'), dateEnd: moment().add(1, "months") },
				3: { dateStart : moment().subtract(365, 'days'), dateEnd: moment().add(1, "days") },
				4: { dateStart : moment().subtract(104, 'weeks'), dateEnd: moment().add(1, "weeks").startOf("isoWeeks") }
			};
			$scope.granularity = g;
			controller.disableGranularitySwitch = true;
			$timeout(function () {
				controller.disableGranularitySwitch = false;
			}, 1000);
		}

		controller.setScope = function() {
			$scope.dashboardData = [
				{
					domains: controller.selectedDomains,
					statType: "user",
					event: "registration-success",
					icon: "ion ion-ios-people-outline",
					color: "aqua",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.REGISTRATIONS",
					updateSeconds: updateSeconds,
					type: "stats"
				},
				{
					type: "row v-reset-row "
				},
				{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE_CASINO_BONUS_PENDING",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PENDING_BONUS_BALANCE",
					updateSeconds: updateSeconds,
					type: "balancesummary"
				},
				{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE_CASINO_BONUS",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUS_BALANCE",
					updateSeconds: updateSeconds,
					type: "balancesummary"
				},
				{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PLAYER_BALANCE",
					updateSeconds: updateSeconds,
					type: "balancesummary"
				},
				{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE_PENDING_WITHDRAWAL",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PLAYER_BALANCE_WITHDRAWAL_PENDING",
					updateSeconds: updateSeconds,
					type: "balancesummary"
				},
				{
					type: "row v-reset-row "
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "blue",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.FIRST_TIME_DEPOSITS",
					updateSeconds: updateSeconds,
					transactionType: 'CASHIER_DEPOSIT',
					labelName: 'first_deposit',
					labelValue: 'YES',
					type: "trantypesummary"
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.FIRST_TIME_DEPOSITS_REG_SAME_DAY",
					updateSeconds: updateSeconds,
					transactionType: 'CASHIER_DEPOSIT',
					labelName: 'first_deposit_reg_same_day',
					labelValue: 'YES',
					type: "trantypesummary"
				},{
					type: "row v-reset-row "
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE,PLAYER_BALANCE_PENDING_WITHDRAWAL",
					inverse: true,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.DEPOSITS", tran: "CASHIER_DEPOSIT" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.WITHDRAWALS", tran: "CASHIER_PAYOUT" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.NETCASH" },
					updateSeconds: updateSeconds,
					link: 'dashboard.cashier.transactions.list',
					type: 'netsummary',
					status: 'SUCCESS'
				},
				{
					type: 'row'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					color: "blue",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.VIRTUALBETS",
					updateSeconds: updateSeconds,
					transactionType: 'VIRTUAL_BET',
					tran: "ALL",
					link: 'dashboard.incentivegames.bets.list',
					type: 'trantypesummary'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "yellow",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.VIRTUALWINS",
					updateSeconds: updateSeconds,
					transactionType: 'VIRTUAL_WIN',
					tran: "WIN",
					link: 'dashboard.incentivegames.bets.list',
					type: 'trantypesummary'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					color: "green",
					titleKey: "UI_NETWORK_ADMIN.DASHBOARD.VIRTUALBETSVOIDED",
					updateSeconds: updateSeconds,
					transactionType: 'VIRTUAL_BET_VOIDED',
					tran: "VOID",
					link: 'dashboard.incentivegames.bets.list',
					type: 'trantypesummary'
				},{
					type: 'row'
				},
				{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.SPORT_STAKE", trans: ["SPORTS_BET"]},
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.SPORT_RETURN", tran: "SPORTS_WIN" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.SPORT_MARGIN" },
					updateSeconds: updateSeconds,
					type: 'netsummary'
				},
				{
					type: 'row'
				},
				{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE_PENDING_WITHDRAWAL",
					inverse: true,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL", trans: ["TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL"]},
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL", tran: "TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.PLAYER_BALANCE_PENDING_WITHDRAWAL_NET" },
					updateSeconds: updateSeconds,
					type: 'netsummary'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETS", trans: ["CASINO_BET", "CASINO_BET_ROLLBACK", "CASINO_NEGATIVE_BET"] },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINS", trans: ["CASINO_WIN", "CASINO_WIN_ROLLBACK"] },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONET" },
					updateSeconds: updateSeconds,
					type: 'netsummary'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERTOBONUS", trans: ["TRANSFER_TO_CASINO_BONUS", "TRANSFER_FROM_CASINO_BONUS_PENDING", "CASINO_BONUS_PENDING"], ascontra: [false, true, true], accountCodes: ["PLAYER_BALANCE", "PLAYER_BALANCE_CASINO_BONUS", "CASINO_BONUS_PENDING"]},
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERFROMBONUS", tran: "TRANSFER_FROM_CASINO_BONUS" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSNET" },
					updateSeconds: updateSeconds,
					type: 'netsummary'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE_CASINO_BONUS",
					inverse: false,
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETSBONUS", trans: ["CASINO_BET", "CASINO_BET_ROLLBACK", "CASINO_NEGATIVE_BET"] },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINSBONUS", trans: ["CASINO_WIN", "CASINO_WIN_ROLLBACK"] },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONETBONUS" },
					updateSeconds: updateSeconds,
					type: 'netsummary'
				},
// 				FIXME: We can not account for free money as we don't separate cancelled bonusses into their original amounts of bonus and free amounts :(
//				{
//					domains: controller.selectedDomains,
//					currency: controller.currency,
//					accountCode: "CASINO_BONUS_ACTIVATE_FREEMONEY",
//					inverse: false,
//					nocalc : true,
//					debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.FREEMONEYPENDING", tran: "", ignore: "true"},
//					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBONUSFREEMONEY", trans: ["CASINO_BONUS_ACTIVATE"], ascontra: [true]},
//					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSFREEMONEYTOTAL" }
//				},
				{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE,PLAYER_BALANCE_CASINO_BONUS",
					inverse: false,
					debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETSBONUS_FREESPIN", tran: "", ignore: "true" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINSBONUS_FREESPIN", tran: "CASINO_WIN_FREESPIN" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONETBONUS_FREESPIN" } ,
					updateSeconds: updateSeconds,
					type: 'netsummary'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					nocalc: true, //means the credit and debit values will not be subtracted from oneanother, so it will be the raw credit/debit being displayed (note the tran_types are the same)
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BALANCE_ADJUST_DEBIT", tran: "BALANCE_ADJUST" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BALANCE_ADJUST_CREDIT", tran: "BALANCE_ADJUST" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BALANCE_ADJUST_NET" },
					updateSeconds: updateSeconds,
					type: 'netsummary'
				},{
					betav2: true,
					domains: controller.selectedDomains,
					currency: controller.currency,
					isTestUser:controller.isTestUser,
					accountCode: "PLAYER_BALANCE",
					inverse: false,
					nocalc: true, //means the credit and debit values will not be subtracted from oneanother, so it will be the raw credit/debit being displayed (note the tran_types are the same)
					debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BONUS_VIRTUAL_DEBIT", tran: "MANUAL_BONUS_VIRTUAL" },
					credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BONUS_VIRTUAL_CREDIT", tran: "MANUAL_BONUS_VIRTUAL" },
					net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.MANUAL_BONUS_VIRTUAL_NET" },
					updateSeconds: updateSeconds,
					type: 'netsummary'
				}
			];

		// $scope.cashGraph = {
		// 	id: "cashgraph",
		// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.DEPOSITS_WITHDRAWALS_HISTORY",
		// 	domains: controller.selectedDomains,
		// 	currency: controller.currency,
		// 	accountCode: "PLAYER_BALANCE,PLAYER_BALANCE_PENDING_WITHDRAWAL",
		// 	inverse: true,
		// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.DEPOSITS", tran: "CASHIER_DEPOSIT" },
		// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.WITHDRAWALS", tran: "CASHIER_PAYOUT" },
		// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.NETCASH" },
		// 	updateSeconds: updateSeconds
		// };
		//
		// $scope.casinoGraph = {
		// 	id: "casinograph",
		// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINO_HISTORY",
		// 	domains: controller.selectedDomains,
		// 	currency: controller.currency,
		// 	accountCode: "PLAYER_BALANCE",
		// 	inverse: false,
		// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETS", tran: "CASINO_BET" },
		// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINS", tran: "CASINO_WIN" },
		// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONET" },
		// 	updateSeconds: updateSeconds
		// };
		//
		// $scope.bonusGraph = {
		// 	id: "bonusgraph",
		// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSHISTORY",
		// 	domains: controller.selectedDomains,
		// 	currency: controller.currency,
		// 	accountCode: "PLAYER_BALANCE_CASINO_BONUS",
		// 	inverse: true,
		// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERTOBONUS", tran: "TRANSFER_TO_CASINO_BONUS" },
		// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERFROMBONUS", tran: "TRANSFER_FROM_CASINO_BONUS" },
		// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSNET" },
		// 	updateSeconds: updateSeconds
		// };
		//
		// $scope.casinoBonusGraph = {
		// 	id: "casinobonusgraph",
		// 	titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINO_BONUS_HISTORY",
		// 	domains: controller.selectedDomains,
		// 	currency: controller.currency,
		// 	accountCode: "PLAYER_BALANCE_CASINO_BONUS",
		// 	inverse: false,
		// 	debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOBETSBONUS", tran: "CASINO_BET" },
		// 	credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINOWINSBONUS", tran: "CASINO_WIN" },
		// 	net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.CASINONETBONUS" },
		// 	updateSeconds: updateSeconds
		// };

		$scope.setGranularity(2);
	}

}]);
