'use strict';

angular.module('lithium')
	.controller('CashierBankAccountLookupController',
		["$scope", "$stateParams", "$translate", "$state", "$userService", "rest-domain", "rest-cashier", "$rootScope",
			function ($scope, $stateParams, $translate, $state, $userService, domainRest, cashierRest, $rootScope) {
			var controller = this;

			controller.selectedDomain = null;
			controller.textTitle = 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP.HEADER.TITLE'
			controller.textDescr = 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP.HEADER.DESCRIPTION'

			$rootScope.provide.bankAccountLookupGeneration.domainMethodProcessors = () => {
				return new Promise((res, rej) => {
					domainRest.findAllPlayerDomains("CASHIER_BANK_ACCOUNT_LOOKUP", "bank_account_lookup", "true").then(function (response) {
						res(response);
					}).catch(function (error) {
						rej(error);
					});
				})
			}

			$rootScope.provide.pageHeaderProvider.getDomains = () => {
				return new Promise((res, rej) => {
					domainRest.findAllPlayerDomains("CASHIER_BANK_ACCOUNT_LOOKUP", "bank_account_lookup", "true").then(function (response) {
						res(response);
					}).catch(function (error) {
						rej(error);
					});
				})
			}

			$rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
				if ( item === null) {
					$rootScope.provide.pageHeaderProvider.clearSelectedDomain()
					return
				}
				controller.selectedDomain = item.name;
				$stateParams.domainName = item.name;

				if (isAlreadyChecked) {
					$state.go('dashboard.cashier.bank_account_lookup.page', {
						domainName: item.name
					});
				}
			}

			$rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
				controller.selectedDomain = null;
				$state.go('dashboard.cashier.bank_account_lookup');
			}

			$rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
				return controller.textTitle ? controller.textTitle : ''
			}

			$rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
				return controller.textDescr ? controller.textDescr : ''
			}

			window.VuePluginRegistry.loadByPage("page-header")
		}]
	);