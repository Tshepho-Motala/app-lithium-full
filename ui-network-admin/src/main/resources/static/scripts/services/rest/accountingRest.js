'use strict';

angular.module('lithium-rest-accounting', ['restangular'])
.factory('rest-accounting', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var accountingService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-accounting');
			});

			var accountingServiceThroughSvcUser = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user');
			});
			
			service.playerbalance = function(currencyCode, domainName, ownerGuid) {
				//BalanceController
				var balancePromise = accountingService.all('balance').all('get')
					.all(domainName)
					.all('PLAYER_BALANCE')
					.all('PLAYER_BALANCE')
					.one(currencyCode+'')
					.one(ownerGuid+'')
					.get();
				return balancePromise;
			}
			
			service.balance = function(accountCode, accountType, currencyCode, domainName, ownerGuid) {
				//BalanceController
				var balancePromise = accountingService.all('balance').all('get')
					.all(domainName)
					.all(accountCode)
					.all(accountType)
					.one(currencyCode)
					.one(ownerGuid)
					.get();
				return balancePromise;
			}
			service.getByAccountType = function(domainName, accountType, currencyCode, ownerGuid) {
				//BalanceController
				var balancePromise = accountingService.all('balance').all('getByAccountType')
					.all(domainName)
					.all(accountType)
					.one(currencyCode)
					.one(ownerGuid)
					.get();
				return balancePromise;
			}

			service.getAllByOwnerGuid = function(domainName, ownerGuid) {
				//BalanceController
				var balancePromise = accountingService.all('balance').all(domainName).one('getAllByOwnerGuid')
					.get({'ownerGuid': ownerGuid});
				return balancePromise;
			}

			service.balanceadjust = function(amountCents, date, accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode, comment, currencyCode, domainName, ownerGuid, authorGuid) {
				var adjustMultiRequest = {
					amountCents:amountCents,
					date:date,
					accountCode:accountCode,
					accountTypeCode:accountTypeCode,
					transactionTypeCode:transactionTypeCode,
					contraAccountCode:contraAccountCode,
					contraAccountTypeCode:contraAccountTypeCode,
					labels:['comment=' + comment],
					domainName:domainName,
					currencyCode:currencyCode,
					ownerGuid:ownerGuid,
					authorGuid:authorGuid
				};
				var balanceadjustPromise = accountingServiceThroughSvcUser.all('backoffice').all(domainName).all('balance-adjust').post(adjustMultiRequest);
				return balanceadjustPromise;
			}

			service.tranTypeSummaryByOwnerGuid = function(domainName, ownerGuid, granularity, accountCode,
														  transactionType, currencyCode) {
				return accountingService.all('summary').all('trantype').one(domainName, 'findByOwnerGuid').get(
					{
						ownerGuid: ownerGuid,
						granularity: granularity,
						accountCode: accountCode,
						transactionType: transactionType,
						currency: currencyCode
					}
				);
			}

			service.summaryAccountByOwnerGuid = function(domainName, ownerGuid, granularity, accountCode,
														 currencyCode) {
				return accountingService.all('summary').all('account').one(domainName, 'findByOwnerGuid').get(
					{
						ownerGuid: ownerGuid,
						granularity: granularity,
						accountCode: accountCode,
						currency: currencyCode
					}
				);
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
