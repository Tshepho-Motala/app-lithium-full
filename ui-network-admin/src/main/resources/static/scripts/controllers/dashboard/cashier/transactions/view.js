'use strict';

angular.module('lithium')
.controller('CashierTranViewController',
		["$rootScope", "$scope", "$uibModal", "$stateParams", "$state", "$userService", "rest-cashier", "notify", "errors", "Lightbox", "$filter", "rest-accounting", "UserRest", "$location", "$anchorScroll", 'rest-domain', 'rest-paymentmethods', "UserRestrictionsRest",
			function($rootScope, $scope, $uibModal, $stateParams, $state, $userService, cashierRest, notify, errors, Lightbox, $filter, acctRest, userRest, $location, $anchorScroll, restDomain, restPaymentMethods, UserRestrictionsRest) {
				var controller = this;
				$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.TAB.TRAN");
				controller.commentCollapsed = false;
				controller.workflowCollapse = false;
				controller.approveDisabled = true;
				controller.approveMessage = undefined;
				controller.showRertyButtonInApprovedState = false;
				controller.processor = "";
				controller.data = {tranId: $stateParams.tranId};
				controller.truncatedMessage = undefined;
				controller.escrowBalance = 0;
				controller.ltDepositsAttempts = 0;
				controller.ltDepositsSuccess = 0;
				controller.ltPlacedTo = 0;
				controller.rfiEnabled = false

				controller.model = {isWithdrawalAndApprovedInWorkflow: false};

				controller.fields = [
					{
						key: "tranAmount",
						type: "ui-money-mask",
						optionsTypes: ["editable"],
						templateOptions : {
							label: "Transaction Amount",
							description: "Alter the transaction amount or leave unchanged",
							required: false,
							addFormControlClass: true
						},
						expressionProperties: {
							'templateOptions.disabled' : function(viewValue, modelValue, scope) {
								return !(controller.model.isWithdrawalAndApprovedInWorkflow);
							}
						}
					}
				];

				controller.experimentalFeatures =  $userService.isExperimentalFeatures()

				// IF NOT VUE PAGE
				if(!controller.experimentalFeatures) {


					$rootScope.provide.cashierProvider['loadLastXCashierTransactions'] = (count) => {
						return cashierRest.getLastXTransactionForUser($stateParams.tranId, count)
					}

					$rootScope.provide.cashierProvider['openUserTransactions'] = (domain, userId) => {
						$state.go('dashboard.players.player.financialtransactions.cashiertransactions', {
							domainName: domain,
							id: userId
						}, {reload: true});
					}

					$rootScope.provide.cashierProvider['openUserBalanceMovementTransactionTransactions'] = (domain, userId) => {
						$state.go('dashboard.players.player.financialtransactions.balancemovement', {
							domainName: domain,
							id: userId
						}, {reload: true});
					}

					function checkWithdrawalApprovable() {
						cashierRest.withdrawApprovable($stateParams.domainName, controller.tran.id, controller.tran.user.guid, controller.tran.currencyCode, controller.isWithdrawalFundsReserved()).then(function (response) {
							controller.approveDisabled = !response.enoughBalance;
							if (controller.approveDisabled) {
								controller.approveMessage = response.message;
							} else {
								controller.approveMessage = undefined;
							}
							return controller.approveDisabled;
						});
					}

					controller.refreshTransaction = function () {
						cashierRest.transaction($stateParams.tranId).then(function (transaction) {
							controller.tran = transaction.plain();
							$rootScope.provide.cashierProvider['transaction'] = controller.tran
							if (controller.tran.domainMethod.method.code === 'cc') {
								var len = controller.tran.accountInfo.length;
								if (len > 0) controller.tran.accountInfo = controller.tran.accountInfo.substring(0, 6) + '******' + controller.tran.accountInfo.substring(len - 4, len);
							}
							if(controller.tran?.tags.length){
								controller.rfiEnabled =  controller.tran.tags.some(el => el === 'RFI_RECEIVED')
							} else {
								controller.rfiEnabled = false;
							}
						}).then(function () {

							if (controller.tran.current.status.code === "APPROVED") {
								var checkTime = new Date();
								checkTime = checkTime.setMinutes(checkTime.getMinutes() - 10);
								var checkTimeStamp = new Date(checkTime).getTime();
								controller.showRertyButtonInApprovedState = controller.tran.current.timestamp < checkTimeStamp;
							}

							controller.getUser($stateParams.domainName, controller.tran.user.guid, errors).then(function (user) {
								controller.model.user = user;
							})
							controller.getRestrictions($stateParams.domainName, controller.tran.user.guid, errors).then(function (restrictions) {
								controller.model.restrictions = restrictions;
							})
							controller.findLtDeposits($stateParams.domainName, controller.tran.user.guid, controller.tran.currencyCode, acctRest, errors).then(function (ltDeposits) {
								controller.model.ltDeposits = ltDeposits;
							});
							controller.findLtWithdrawals($stateParams.domainName, controller.tran.user.guid, controller.tran.currencyCode, acctRest, errors).then(function (ltWithdrawals) {
								controller.ltWithdrawals = ltWithdrawals;
							});
							controller.findPendingWithdrawals($stateParams.domainName, controller.tran.user.guid, controller.tran.currencyCode, acctRest, errors).then(function (pendingWithdrawals) {
								controller.pendingWithdrawals = pendingWithdrawals;
							});

							controller.findEscrowBalance($stateParams.domainName, controller.tran.user.guid).then(function (escrowBalance) {
								controller.escrowBalance = escrowBalance;
							});

							controller.findLtDepositsAttempts($stateParams.domainName, controller.tran.user.guid).then(function (ltDepositsAttempts) {
								controller.ltDepositsAttempts = ltDepositsAttempts;
							});

							controller.findLtDepositsSuccess($stateParams.domainName, controller.tran.user.guid).then(function (ltDepositsSuccess) {
								controller.ltDepositsSuccess = ltDepositsSuccess;
							});

							controller.findLtPlacedTo($stateParams.domainName, controller.tran.user.guid).then(function (ltPlacedTo) {
								controller.ltPlacedTo = ltPlacedTo;
							});

							//			controller.setUserInfo(controller.tran.user.guid);

							cashierRest.getTransactionRemarks($stateParams.tranId).then(function (remarks) {
								controller.remarks = remarks.plain();
							});

							cashierRest.linkedTransaction($stateParams.tranId).then(function (transaction) {
								controller.tran.linkedTransaction = transaction;
							});

							checkWithdrawalApprovable();

							// Let's first get the PLAYER_BALANCE_PENDING_WITHDRAWAL, it is used subsequently.
							acctRest.balance('PLAYER_BALANCE_PENDING_WITHDRAWAL', 'PLAYER_BALANCE', controller.tran.currencyCode, $stateParams.domainName, controller.tran.user.guid).then(function (res1) {
								controller.playerBalancePendingWithdrawal = res1;

								acctRest.balance('PLAYER_BALANCE', 'PLAYER_BALANCE', controller.tran.currencyCode, $stateParams.domainName, controller.tran.user.guid).then(function (res2) {
									controller.playerBalance = res2;
								}).then(function () {
									cashierRest.domainMethodImage(controller.tran.domainMethod.id).then(function (image) {
										controller.image = image.plain();
									}).catch(function (error) {
										errors.catch("", false)(error)
									});
									cashierRest.transactionWorkflow(controller.tran.id, 0, 51, true).then(function (workflow) {
										controller.tab = [];
										controller.workflow = $filter('orderBy')(workflow.plain(), '-id');
										if (controller.workflow.length > 50) {
											controller.truncatedMessage = "\tTruncated: shown first 25 attempts and last 25 attempts"
										}
										if (controller.workflow[0].processor) controller.processor = controller.workflow[0].processor.description;
										angular.forEach(controller.workflow, function (wf) {
											if ((controller.tran.transactionType === 'WITHDRAWAL') && (wf.status.code === 'APPROVED' || wf.status.code === 'AUTO_APPROVED')) {
												controller.model.isWithdrawalAndApprovedInWorkflow = true;
											}
											controller.tab[wf.id] = 'info';
											//						controller.tran.accountingReference = wf.accountingReference;
											cashierRest.transactionAttempt(controller.tran.id, wf.id).then(function (attempt) {
												wf.attempt = attempt.plain();
												if (!$userService.hasAdminRole()) {
													wf.attempt.processorRawRequest = '';
													wf.attempt.processorRawResponse = '';
												}
												if (wf.attempt.processorRawRequest === '') wf.attempt.processorRawRequest = 'N/A';
												if (wf.attempt.processorRawResponse === '') wf.attempt.processorRawResponse = 'N/A';

												cashierRest.transactionDataPerStage(controller.tran.id, wf.stage).then(function (data) {
													wf.attempt.iodata = $filter('orderBy')(data.plain(), 'output');
													angular.forEach(wf.attempt.iodata, function (io) {
														if ((io.field === 'cvv') || (io.field === 'account_info')) {
															var index = wf.attempt.iodata.indexOf(io);
															wf.attempt.iodata.splice(index, 1);
														} else if (io.field == 'ccnumber') {
															var len = io.value.length;
															io.value = io.value.substring(0, 6) + '******' + io.value.substring(len - 4, len);
														}
													});
												}).catch(function (error) {
													errors.catch("", false)(error)
												});
											}).catch(function (error) {
												errors.catch("", false)(error)
											});
										});
									}).catch(function (error) {
										errors.catch("", false)(error)
									});

									controller.fees = {};
									controller.fees.flat = 0;
									controller.fees.minimum = 0;
									controller.fees.percentage = 0;
									controller.fees.percentageFee = 0;
									controller.fees.playerAmount = 0;
									controller.fees.playerAmountCents = 0;
									controller.model.tranAmount = 0;
									controller.fees.depositAmount = 0;
									controller.fees.depositAmountCents = 0;

									cashierRest.transactionData(controller.tran.id).then(function (data) {
										controller.iodata = data.plain();
										angular.forEach(data.plain(), function (io) {
											if (io.field === 'amount') {
												if ((!angular.isUndefined(io.value)) && (Number(io.value) == io.value) && (io.value !== "")) {
													controller.fees.depositAmount = new Big(io.value).toString();
													controller.fees.depositAmountCents = new Big(io.value).times(100).toString();
													controller.model.tranAmount = new Big(io.value).toString();
													controller.fees.playerAmount = controller.fees.depositAmount;
													controller.fees.playerAmountCents = controller.fees.depositAmountCents;
												}
											}
										});
									}).catch(function (error) {
										errors.catch("", false)(error)
									});

									cashierRest.transactionLabels(controller.tran.id).then(function (data) {
										angular.forEach(data.plain(), function (label) {
											if (label.label.name === 'fees_flat') if (!angular.isUndefined(label.value) && Number(label.value) == label.value) controller.fees.flat = new Big(label.value).toString();
											if (label.label.name === 'fees_minimum') if (!angular.isUndefined(label.value) && Number(label.value) == label.value) controller.fees.minimum = new Big(label.value).toString();
											if (label.label.name === 'fees_percentage') if (!angular.isUndefined(label.value) && Number(label.value) == label.value) controller.fees.percentage = new Big(label.value).toString();
											if (label.label.name === 'fees_percentage_fee') if (!angular.isUndefined(label.value) && Number(label.value) == label.value) controller.fees.percentageFee = new Big(label.value).toString();
											if (label.label.name === 'fees_player_amount') {
												if (!angular.isUndefined(label.value) && Number(label.value) == label.value) {
													controller.fees.playerAmount = new Big(label.value).toString();
													controller.fees.playerAmountCents = new Big(label.value).times(100).toString();
												}
											}
										});
										if (Big(controller.fees.flat).plus(Big(controller.fees.percentageFee)) < Big(controller.fees.minimum)) {
											controller.fees.minimumUsed = true;
											controller.fees.feeAmount = Big(controller.fees.minimum).toString();
										} else {
											controller.fees.minimumUsed = false;
											controller.fees.feeAmount = Big(controller.fees.flat).plus(Big(controller.fees.percentageFee)).toString();
										}

										// FOR BULK ACTION
										$rootScope.provide['bulkTransactionProvider'] = {}
										const paramsBulk = {
											domain: $stateParams.domainName,
											guid: controller.tran.user.guid,
											dmp: -1,
											dm: -1
										}
										$rootScope.provide.bulkTransactionProvider.getParams =  () => { return paramsBulk }
										if (controller.model.user !== undefined && controller.model.user !== null &&
											controller.model.user.guid !== undefined && controller.model.user.guid !== null) {
											$rootScope.provide.bulkTransactionProvider.selectedUser =  controller.model.user
										} else {
											$rootScope.provide.bulkTransactionProvider.selectedUser =  null
										}

									}).catch(function (error) {
										errors.catch("", false)(error)
									});
								}).catch(function (error) {
									errors.catch("", false)(error)
								});
							}).catch(function (error) {
								errors.catch("", false)(error)
							});
						});
					}

					controller.isWithdrawalFundsReserved = function () {
						if ((controller.tran.accRefToWithdrawalPending !== undefined && controller.tran.accRefToWithdrawalPending !== null) &&
							(controller.tran.accRefFromWithdrawalPending === undefined || controller.tran.accRefFromWithdrawalPending === null)) {
							return true;
						}

						return false;
					}

					controller.openLightBox = function (src) {
						var image = [{
							'url': src
						}]
						Lightbox.openModal(image, 0);
					}

					controller.markSuccess = function () {
						if (controller.isWithdrawalFundsReserved()) {
							acctRest.balance('PLAYER_BALANCE_PENDING_WITHDRAWAL', 'PLAYER_BALANCE', controller.tran.currencyCode, $stateParams.domainName, controller.tran.user.guid).then(function (res3) {
								if (angular.isDefined(res3)) {
									controller.playerBalancePendingWithdrawal = res3;
								} else {
									controller.playerBalancePendingWithdrawal = 0;
								}

								if ((controller.playerBalancePendingWithdrawal - controller.fees.playerAmountCents) >= 0) {
									cashierRest.changeStatus($stateParams.domainName, controller.tran.id, 'success', controller.model.tranAmount, controller.model.comment).then(function (data) {
										controller.refreshTransaction();
									}).catch(function (error) {
										errors.catch("", false)(error)
									});
								} else {
									notify.error("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.FAILED.EXPLANATION");
									controller.approveDisabled = true;
									controller.approveMessage = "UI_NETWORK_ADMIN.CASHIER.TRANSACTION.FAILED.EXPLANATION";
								}
							});
						} else {
							acctRest.balance('PLAYER_BALANCE', 'PLAYER_BALANCE', controller.tran.currencyCode, $stateParams.domainName, controller.tran.user.guid).then(function (res4) {
								if (angular.isDefined(res4)) {
									controller.playerBalance = res4;
								} else {
									controller.playerBalance = 0;
								}

								cashierRest.changeStatus($stateParams.domainName, controller.tran.id, 'success', controller.model.tranAmount, controller.model.comment).then(function (data) {
									controller.refreshTransaction();
								}).catch(function (error) {
									errors.catch("", false)(error)
								});
							});
						}
					}

					controller.markApproved = function () {
						cashierRest.changeStatus($stateParams.domainName, controller.tran.id, 'approve', controller.model.tranAmount, controller.model.comment).then(function (data) {
							controller.refreshTransaction();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.retryTransaction = function () {
						cashierRest.transactionRetry($stateParams.domainName, controller.tran.id).then(function (data) {
							controller.refreshTransaction();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.setRFIFlag = function () {
						cashierRest.transactionSetRFIFlag(controller.tran.id).then(function (data) {
							controller.refreshTransaction();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.removeRFIFlag = function () {
						cashierRest.transactionRemoveRFIFlag(controller.tran.id).then(function (data) {
							controller.refreshTransaction();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.transactionClearProvider = function () {
						cashierRest.transactionClearProvider($stateParams.domainName, controller.tran.id, controller.model.comment).then(function (data) {
							controller.refreshTransaction();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.addRemark = function () {
						cashierRest.addTransactionRemark(controller.tran.id, controller.model.remark).then(function (remark) {
							controller.model.remark = undefined;
							controller.refreshTransaction();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.transactionCancel = function () {
						cashierRest.transaction(controller.data.tranId).then(function (transaction) {
							controller.tran = transaction.plain();
							controller.runTransactionCancel();
						});
					}

					controller.runTransactionCancel = function () {
						var modalInstance = $uibModal.open({
							animation: true,
							ariaLabelledBy: 'modal-title',
							ariaDescribedBy: 'modal-body',
							templateUrl: 'scripts/controllers/dashboard/cashier/transactions/transactionCanсel.html',
							controller: 'CashierTranCancelController',
							controllerAs: 'controller',
							size: 'md cascading-modal',
							resolve: {
								transaction: function () {
									return angular.copy(controller.tran);
								},
								loadMyFiles: function ($ocLazyLoad) {
									return $ocLazyLoad.load({
										name: 'lithium',
										files: ['scripts/controllers/dashboard/cashier/transactions/transactionCanсel.js']
									})
								}
							}
						});

						modalInstance.result.then(function (result) {
							if (result) {
								cashierRest.transactionCancel($stateParams.domainName, controller.tran.id, result).then(function (data) {
									controller.refreshTransaction();
								}).catch(function (error) {
									errors.catch("", false)(error)
								});
							}
						});
					}

					controller.setOnHold = function () {
						var modalInstance = $uibModal.open({
							animation: true,
							ariaLabelledBy: 'modal-title',
							ariaDescribedBy: 'modal-body',
							templateUrl: 'scripts/controllers/dashboard/cashier/transactions/on-hold.html',
							controller: 'CashierTransactionOnHoldModal',
							controllerAs: 'controller',
							size: 'md cascading-modal',
							resolve: {
								transactionId: function () {
									return controller.tran.id;
								},
								domainName: function () {
									return $stateParams.domainName;
								},
								loadMyFiles: function ($ocLazyLoad) {
									return $ocLazyLoad.load({
										name: 'lithium',
										files: ['scripts/controllers/dashboard/cashier/transactions/on-hold.js']
									})
								}
							}
						});

						modalInstance.result.then(function (result) {
							if (result) {
								controller.refreshTransaction();
							}
						});
					}

					controller.findLtDeposits = function (domainName, guid, currency, rest, errors) {

						return rest.tranTypeSummaryByOwnerGuid(domainName, guid, 5,
							'PLAYER_BALANCE', 'CASHIER_DEPOSIT', currency).then(function (response) {
							var response = response.plain();
							var total = 0;
							if (response[0]) {
								total += (response[0].debitCents - response[0].creditCents) * -1;
							}
							return total;
						}).catch(function (error) {
							errors.catch('', false)(error);
						});
					}


					controller.findLtWithdrawals = function (domainName, guid, currency, rest, errors) {
						return rest.tranTypeSummaryByOwnerGuid(domainName, guid, 5,
							'PLAYER_BALANCE_PENDING_WITHDRAWAL', 'CASHIER_PAYOUT', currency).then(function (response) {
							var response = response.plain();
							var total = 0;
							if (response[0]) {
								total += (response[0].debitCents - response[0].creditCents) * 1;
							}
							return total;
						}).catch(function (error) {
							errors.catch('', false)(error);
						});
					}

					controller.findPendingWithdrawals = function (domainName, guid, currency, rest, errors) {
						return rest.summaryAccountByOwnerGuid(domainName, guid, 5,
							'PLAYER_BALANCE_PENDING_WITHDRAWAL', currency).then(function (response) {
							var response = response.plain();
							var total = 0;
							if (response[0]) {
								total += (response[0].debitCents - response[0].creditCents) * -1;
							}
							return total;
						}).catch(function (error) {
							errors.catch('', false)(error);
						});
					}

					controller.findEscrowBalance = function (domainName, guid) {
						return restPaymentMethods.getEscrowWalletPlayerBalance(domainName, guid).then(function (response) {
							let resp = response;
							var total = 0;
							if (resp) {
								total += resp;
							}
							return total;
						}).catch(function (error) {
							errors.catch('', false)(error);
						});
					}

					controller.findLtDepositsAttempts= function (domainName, guid) {
						return restPaymentMethods.getLtDepositsAttempts(domainName, guid).then(function(response) {
							let resp = response;
							let total = 0;
							if (resp) {
								total += resp;
							}
							return total;
						}).catch(function(error) {
							errors.catch('', false)(error);
						});
					}

					controller.findLtDepositsSuccess= function (domainName, guid) {
						return restPaymentMethods.getLtDepositsSuccess(domainName, guid).then(function(response) {
							let resp = response;
							let total = 0;
							if (resp) {
								total += resp;
							}
							return total;
						}).catch(function(error) {
							errors.catch('', false)(error);
						});
					}

					controller.findLtPlacedTo = function (domainName, guid) {
						return restPaymentMethods.getLtPlacedTo(domainName, guid).then(function(response) {
							let resp = response;
							let total = 0;
							if (resp) {
								total += resp;
							}
							return total;
						}).catch(function(error) {
							errors.catch('', false)(error);
						});
					}

					controller.getDomainInfo = function (domainName, errors, restDomain) {
						return restDomain.findByName(encodeURIComponent(domainName)).then(function (domain) {
							return domain.plain();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.getUser = function (domain, userGuid, errors) {
						return userRest.findFromGuid(domain, userGuid).then(function (user) {
							return user.plain();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.getRestrictions = function (domain, userGuid, errors) {
						return UserRestrictionsRest.get(domain, userGuid).then(function (restrictions) {
							return restrictions.plain();
						}).catch(function (error) {
							errors.catch("", false)(error)
						});
					}

					controller.ngBankAccountSettingsActive = false;
					controller.isWithdrawal = false;
					controller.domainData = undefined;
					controller.transactionData = undefined;

					controller.areNGBankAccountLookupSettingsActive = async function () {
						await restDomain.findByName($stateParams.domainName).then(function (response) {
							controller.domainData = response;
						});
						for (var i = 0; i < controller.domainData.current.labelValueList.length; i++) {
							if (controller.domainData.current.labelValueList[i].label.name === "bank_account_lookup"
								&& controller.domainData.current.labelValueList[i].labelValue.value === "true") {
								controller.ngBankAccountSettingsActive = true;
								break;
							}
						}
					}
					controller.areNGBankAccountLookupSettingsActive();

					controller.isWithdrawalTransactionType = async function () {
						await cashierRest.transaction(controller.data.tranId).then(function (transaction) {
							controller.transactionData = transaction.plain();
						});
						if (!controller.transactionData.domainMethod.deposit) controller.isWithdrawal = true;
					}
					controller.isWithdrawalTransactionType();

					controller.refreshTransaction();
					$rootScope.provide.cashierProvider['refreshTransaction'] = ( ) =>  {
						return controller.refreshTransaction()
					}
					$rootScope.provide.cashierProvider['transactionID'] = $stateParams.tranId
					// BULK ACTION



					window.VuePluginRegistry.loadByPage("WithdrawalBulk")
					window.VuePluginRegistry.loadByPage("BalanceAdjustmentsTransaction")
					window.VuePluginRegistry.loadByPage("dashboard/cashier/transactions/view")

				}
				// VUE Transaction Detail page
				if(controller.experimentalFeatures) {
					window.VuePluginRegistry.loadByPage("TransactionsDetailPage")
				}



			}]);
