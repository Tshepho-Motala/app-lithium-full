'use strict';

angular.module('lithium')
.factory('rest-paymentmethods', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-cashier');
			});
			
			service.getDomainMethods = function(domainName) {
				//DomainMethodController
				return config.all("cashier/direct-withdrawal").all("domain-methods").getList({
					domainName: domainName
				});
			}

			service.getPlayerBalance = function(domainName, userGuid) {
				return config.all("cashier/direct-withdrawal").all("get-player-balance").get('', {
					domainName: domainName,
					userGuid: userGuid
				});
			}

			service.getEscrowWalletPlayerBalance = function(domainName, userGuid) {
				return config.all("cashier/direct-withdrawal").all("get-escrow-wallet-player-balance").get('', {
					domainName: domainName,
					userGuid: userGuid
				});
			}

			service.getLtDepositsAttempts = function(domainName, userGuid) {
				return config.all("/player-transaction-statistics").all("/deposits/count").get('', {
					domainName: domainName,
					userGuid: userGuid
				});
			}

			service.getLtDepositsSuccess = function(domainName, userGuid) {
				return config.all("/player-transaction-statistics").all("/deposits/count").get('', {
					domainName: domainName,
					userGuid: userGuid,
					status: "SUCCESS"
				});
			}

			service.getLtPlacedTo = function(domainName, userGuid) {
				return config.all("/player-transaction-statistics").all("summary-transaction-types/bets").get('', {
					domainName: domainName,
					userGuid: userGuid
				});
			}

			service.getDirectWithdrawProcessorAccount = function(methodCode, userGuid) {
				//ProcessorUserCardController
				return config.all("cashier").all("pmc").all("withdraw-processor-accounts-per-user-method").getList({
					methodCode: methodCode,
					userGuid: userGuid
				});
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
