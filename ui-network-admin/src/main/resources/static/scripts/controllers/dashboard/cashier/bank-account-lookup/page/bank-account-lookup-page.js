'use strict';

angular.module('lithium')
	.controller('CashierBankAccountLookupPageController',
		['$rootScope', '$scope', '$dt', '$translate', 'DTOptionsBuilder', '$state', '$stateParams', '$security', "rest-cashier", 'domain', 'UserRest', 'rest-paymentmethods', "notify",
			function ($rootScope, $scope, $dt, $translate, DTOptionsBuilder, $state, $stateParams, $security, cashierRest, domain, UserRest, restPaymentMethods, notify) {

				var controller = this;
				controller.data = {
					domain: domain,
					isBalanceLimitEscrow: false
				};

				$rootScope.provide.bankAccountLookupGeneration['domainMethodProcessors'] = () => {
					return new Promise((res, rej) => {
						cashierRest.findWithdrawalDomainMethodProcessors(domain.name).then(function (response) {
							res(response);
						}).catch(function (error) {
							rej(error);
						});
					})
				}

				$rootScope.provide.bankAccountLookupGeneration['banks'] = (processorProperties, processorUrl) => {
					return new Promise((res, rej) => {
						cashierRest.banks(processorProperties, processorUrl).then(function (response) {
							res(response);
						}).catch(function (error) {
							rej(error);
						});
					})
				}

				$rootScope.provide.bankAccountLookupGeneration['lookup'] = (bankAccountLookupRequest, processorUrl) => {
					return new Promise((res, rej) => {
						cashierRest.bankAccountLookupModule(bankAccountLookupRequest, processorUrl).then(function (response) {
							res(response);
						}).catch(function (error) {
							rej(error);
						});
					})
				}

				$rootScope.provide.bankAccountLookupGeneration.searchUsers = search => {
					return new Promise((res, rej) => {
						UserRest.search($stateParams.domainName, search)
							.then(response => res(response))
							.catch(function (error) {
								rej(error);
							});
					})
				}

				controller.getEscrowWalletPlayerBalance = (domain, guid) => {
					restPaymentMethods.getEscrowWalletPlayerBalance(domain, guid).then(function (response) {
						controller.data.isBalanceLimitEscrow = Boolean(response)
					});
				}

				$rootScope.provide.bankAccountLookupGeneration.getUserBalance = ( domain, guid ) => {
					controller.getEscrowWalletPlayerBalance(domain, guid)

					if(guid) {
						return new Promise((res, rej) => {
							restPaymentMethods.getPlayerBalance(domain, guid)
								.then(response => res(response))
								.catch(function (error) {
									rej(error);
								});
						})
					}
				}

				$rootScope.provide.bankAccountLookupGeneration.getCurrencySymbol = () => {
					return Promise.resolve(controller.data.domain.currencySymbol)
				}

				$rootScope.provide.bankAccountLookupGeneration['createManualWithdrawal'] = (domainMethod, userGuid, accountNumber, bankCode, amount, comment, redirectToTransaction) => {
					return new Promise((res, rej) => {
						var manualWithdrawalTransaction = {};

						manualWithdrawalTransaction.domainMethod = domainMethod;
						manualWithdrawalTransaction.userGuid = userGuid;
						manualWithdrawalTransaction.accountNumber = accountNumber;
						manualWithdrawalTransaction.bankCode = bankCode;
						manualWithdrawalTransaction.comment = comment;
						manualWithdrawalTransaction.amount = (+amount).toString();
						manualWithdrawalTransaction.balanceLimitEscrow = controller.data.isBalanceLimitEscrow;

						cashierRest.executeManualWithdrawal(manualWithdrawalTransaction).then(function(response) {
							if (typeof(response) === 'number') {
								notify.success("The transaction was added successfully.");
								if (redirectToTransaction) {
									$state.go("dashboard.cashier.transaction", {
										tranId: response,
										domainName: $stateParams.domainName
									});
								}
							}
							if (!response._successful) {
								if (response._status === 500) {
									console.log('500')
									notify.error(response._data2);
								}
							}
						}).catch(function(error) {
							console.error(error)
						})
					})
				}

				window.VuePluginRegistry.loadByPage("dashboard/cashier/bank-account-lookup/page")
			}]
	);