'use strict';

angular.module('lithium')
	.directive('balance', function() {
		return {
			templateUrl:'scripts/directives/player/accounting/balance.html',
			scope: {
				data: "=",
				balance: '=',
				onUpdate: "&"
			},
			restrict: 'E',
			replace: true,
			controller: ['$q', '$uibModal', '$scope', 'rest-accounting', 'errors', 'bsLoadingOverlayService', 'notify', 'rest-domain',
			function($q, $uibModal, $scope, acctRest, errors, bsLoadingOverlayService, notify, domainRest) {
				var me = this;
				
				if (!$scope.data.color) {
					$scope.data.color = "gray";
				}
				if (!$scope.data.currency) {
					$scope.data.currency = 'USD';
				}
				if ($scope.data.symbol) {
					$scope.symbol = $scope.data.symbol;
				}
				if ($scope.data.franctionSize) {
					$scope.franctionSize = $scope.data.franctionSize;
				}
				if (!$scope.data.accountCode) {
					$scope.data.accountCode = 'PLAYER_BALANCE';
				}
				if (!$scope.data.accountType) {
					$scope.data.accountType = 'PLAYER_BALANCE';
				}
				if (!$scope.data.displayOnly) {
					$scope.data.displayOnly = false;
				}
				$scope.referenceId = 'balance-overlay-'+$scope.data.accountCode+'-'+$scope.data.accountType;
				$scope.refresh = function() {
					bsLoadingOverlayService.start({referenceId:$scope.referenceId});
					domainRest.findByName($scope.data.domainName).then(function(domain) {
//						$scope.data.currency = $scope.data.currency;
//						$scope.data.symbol = domain.currencySymbol+' ';
						if (!$scope.symbol) {
							$scope.symbol = domain.currencySymbol+' ';
						}
//						$scope.symbol = domain.currencySymbol+' ';

						acctRest.balance($scope.data.accountCode, $scope.data.accountType, $scope.data.currency, $scope.data.domainName, $scope.data.ownerGuid).then(function(response) {
							if (angular.isDefined(response)) {
								$scope.balance = response.data;
							} else {
								$scope.balance = 0;
							}
							$scope.onUpdate({id: $scope.data.id, balance:$scope.balance});
						});
					}).catch(
						errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BALANCE", false)
					).finally(function () {
						bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
					});
				};

				if (!$scope.balance) {
					$scope.refresh();
				}
				
				$scope.adjust = function () {
					var balanceModalInstance = $uibModal.open({
						animation: true,
						ariaLabelledBy: 'modal-title',
						ariaDescribedBy: 'modal-body',
						templateUrl: 'scripts/controllers/dashboard/players/player/accounting/adjustments.html',
						controller: 'BalanceModal',
						controllerAs: 'controller',
						size: 'lg',
						resolve: {
							domainName: function(){return $scope.data.domainName},
							balance: function(){return $scope.balance},
							currency: function(){return $scope.data.currency},
							symbol: function(){return $scope.data.symbol},
							ownerGuid: function(){return $scope.data.ownerGuid},
							authorGuid: function(){return $scope.data.authorGuid},
							accountType: function(){return $scope.data.accountType},
							accountCode: function(){return $scope.data.accountCode}
						}
					});
					balanceModalInstance.result.then(function (response) {
						notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
						$scope.refresh();
						$scope.data.changelogs.reload += 1;
					});
				};
				
				$scope.writeoff = function (balanceAmount) {
					console.log("writeoff for value:", balanceAmount);
					acctRest.balanceadjust(
							balanceAmount * -1,
							new Date(),
							$scope.data.accountCode, 
							$scope.data.accountType,
							'BALANCE_ADJUST',
							'PLAYER_BALANCE_WRITEOFF',
							'MANUAL_BALANCE_ADJUST',
							'write-off player balance',
							$scope.data.currency,
							$scope.data.domainName,
							$scope.data.ownerGuid,
							$scope.data.authorGuid
						).then(function(response) {
							notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
							$scope.refresh();
							$scope.data.changelogs.reload += 1;
						}).catch(
							errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BALANCEUPDATE", false)
						).finally(function () {
							bsLoadingOverlayService.stop({referenceId:controller.referenceId});
						});
				};
				
				
				$scope.setGranularity = function (g) {
					$scope.ranges = {
							1: { dateStart : moment().subtract(20, 'years'), dateEnd: moment().add(1, 'years') },
							2: { dateStart : moment().subtract(48, 'months'), dateEnd: moment().add(1, 'month') },
							3: { dateStart : moment().subtract(365, 'days'), dateEnd: moment().add(1, 'days') },
							4: { dateStart : moment().subtract(104, 'weeks'), dateEnd: moment().add(1, 'weeks') }
						};	
					$scope.granularity = g;
				}
				
				$scope.balanceAdjustGraph = { 
					id: "cashgraph", 
					titleKey: "UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.HISTORY",
					domains: $scope.data.domainName,
					currency: { code: "USD", format: "$##.00" },
					accountCode: "PLAYER_BALANCE",
					inverse: true,
					debit: { titleKey: "UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.HISTORY.DEPOSITS", tran: "MANUAL_BALANCE_ADJUST" },
					credit: { titleKey: "UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.HISTORY.WITHDRAWALS", tran: "MANUAL_BALANCE_ADJUST" },
					net: { titleKey: "UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.HISTORY.NETCASH" }
				};
				
				//$scope.refresh();
			}]
		}
	}).controller('BalanceModal', ['$uibModalInstance', '$translate', '$userService', '$scope', 'notify', 'rest-accounting', 'rest-tranta', '$filter', 'balance', 'domainName', 'accountType', 'accountCode', 'currency', 'symbol', 'ownerGuid', 'authorGuid', 'errors', 'bsLoadingOverlayService',
		function ($uibModalInstance, $translate, $userService, $scope, notify, acctRest, restTranta, $filter, balance, domainName, accountType, accountCode, currency, symbol, ownerGuid, authorGuid, errors, bsLoadingOverlayService) {
			var controller = this;
			//console.log("controller 2", balance, domainName, currency, ownerGuid, authorGuid);
			controller.typeLists = []
			controller.typeListsLoad = function () {
				restTranta.all(domainName).then(function (response) {
					controller.typeLists = response.filter(el => el.accountTypeCode !== "PLAYER_BALANCE_WRITEOFF");
				});
			}
			controller.typeListsLoad()

			controller.options = {};
			controller.model = {
				balance: $filter('cents')(balance, symbol),
				newbalance: balance,
				newbalancedisplay: $filter('cents')(balance, symbol),
				adjustment: 0,
				description: ''
			};
			
			controller.fields = [{
				className : "col-xs-12",
				key: 'adjustment',
				type: "ui-money-mask",
				templateOptions: {
					label: "",
					description: "",
					required: true,
					symbol: symbol
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.ADJUST.LABEL" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.ADJUST.DESC" | translate'
				}
			},{
				className : "row v-reset-row ",
				fieldGroup : [{
					className : "col-xs-2",
					type : "button",
					key : "radio-1477465160762",
					templateOptions : {
						text : '- 100',
						btnType: "default btn-block btn-sm",
						onClick : function($event) {
							controller.decrease(100);
						}
					}
				},{
					className : "col-xs-2",
					type : "button",
					key : "radio-1477465160762",
					templateOptions : {
						text : '- 10',
						btnType: "default btn-block btn-sm",
						onClick : function($event) {
							controller.decrease(10);
						}
					}
				},{
					className : "col-xs-2",
					type : "button",
					key : "radio-1477465160762",
					templateOptions : {
						text : '+ 10',
						btnType: "default btn-block btn-sm",
						onClick : function($event) {
							controller.increase(10);
						}
					}
				},{
					className : "col-xs-2",
					type : "button",
					key : "radio-1477465160762",
					templateOptions : {
						text : '+ 100',
						btnType: "default btn-block btn-sm",
						onClick : function($event) {
							controller.increase(100);
						}
					}
				}]
			},{
				className : "col-xs-12",
				type : "input",
				key : "balance",
				templateOptions : {
					label : "current",
					disabled : true,
					placeholder : "",
					description : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.CURRENT.LABEL" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.CURRENT.DESC" | translate'
				}
			},{
				className : "col-xs-12",
				type : "input",
				key : "newbalancedisplay",
				templateOptions : {
					label : "new",
					disabled : true,
					placeholder : "",
					description : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.NEW.LABEL" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.NEW.DESC" | translate'
				}
			},{
				key: "trantypeacct", 
				className: "col-xs-12 form-group",
				type: "ui-select-single",
				templateOptions : {
					label: "",
					description: "",
					placeholder: "",
					required : true,
					valueProp: 'accountTypeCode',
					labelProp: 'accountTypeCode',
					optionsAttr: 'ui-options', // 'bs-options' || 'ui-options'
					ngOptions: 'ui-options',
					options: [],
					onClick: function ($event, response) {
						if ($event !== undefined) {
							const option =  response.templateOptions.options.find(el => el.accountTypeCode === $event)
							if(option && option.accountTypeCode) {
								controller.model.adjustmentDescription = $translate.instant('UI_NETWORK_ADMIN.USER.ACCOUNT_TYPE_CODE.DESCRIPTION.' + option.accountTypeCode)
							}
						}
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.DESC" | translate'
				},
			},
				{
					className: "col-xs-12",
					type: "warning-text-with-label",
					key: 'adjustmentDescription',
					templateOptions: {
						label: "Transaction type description:",
					},
				},
				{
				className : "col-xs-12",
				type : "textarea",
				key : "comment",
				templateOptions : {
					label : "",
					required : true,
					placeholder : "",
					description : "",
					maxlength: 65535
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.COMMENT.LABEL" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.COMMENT.DESC" | translate'
				}
			}];
			
			///////////////////////
			
			controller.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};

			$scope.$watch('controller.model.adjustment', function(newValue, oldValue) {
				controller.model.trantypeacct = ''
				controller.model.adjustmentDescription = ''
				const index = controller.fields.findIndex(el => el.key === 'trantypeacct')
				if(newValue > 0 && index){
					controller.fields[index].templateOptions.options = controller.typeLists.filter(el => el.credit === true)
				} else if(newValue < 0 && index) {
					controller.fields[index].templateOptions.options = controller.typeLists.filter(el => el.debit === true)
				} else {
					controller.fields[index].templateOptions.options = []
				}
				if (angular.isUndefined(controller.model.adjustment)) {
					controller.model.adjustment = 0;
					controller.form.$invalid = true;
				}
				controller.model.newbalance = (Big(controller.model.adjustment).times(100).plus(balance)).toString(); //balance + (controller.model.adjustment*100);
				controller.model.newbalancedisplay = $filter('cents')(controller.model.newbalance, symbol);
			});

			controller.decrease = function(adjustment) {
				controller.model.adjustment = Big(controller.model.adjustment).minus(adjustment).toString();
			}

			controller.increase = function(adjustment) {
				controller.model.adjustment = Big(controller.model.adjustment).plus(adjustment).toString();
			}

			controller.referenceId = 'balanceadjust-overlay';
		
			$scope.disableSubmitButton = false;
			
			controller.doAdjust = function() {
				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				var amount = (new Big(controller.model.adjustment).times(100)).toString();
				if (new Big(controller.model.newbalance).lt(0) < 0) {
					controller.form.$invalid = true;
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
					notify.error("UI_NETWORK_ADMIN.NEGATIVE_BALANCE.ERROR");
					return false;
				}

				if (amount === '0') {
					controller.form.$invalid = true;
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
					notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
					return false;
				}
				if (controller.form.$invalid) {
					angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
					notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
					return false;
				}

				// There are no async tasks in the above validation, so it's safe to set the button state
				// here and save ourselves from having to reset the value on every failed check.
				$scope.disableSubmitButton = true;

				// Make sure this promise resets the button state when resolving.
				// While the 'finally' method is all that's needed for resetting the style,
				// experience shows that there are instances which may cause it to not get hit,
				// so redundancy in the `then` and `catch` is a safety net
				acctRest.balanceadjust(
					amount,
					new Date(),
					accountCode, 
					accountType,
					(controller.model.trantypeacct === 'MANUAL_BONUS_VIRTUAL') ? 'MANUAL_BONUS_VIRTUAL' : 'BALANCE_ADJUST',
					controller.model.trantypeacct,
					(controller.model.trantypeacct === 'MANUAL_BONUS_VIRTUAL') ? 'MANUAL_BONUS_VIRTUAL_ADJUST' : 'MANUAL_BALANCE_ADJUST',
					controller.model.comment,
					currency,
					domainName,
					ownerGuid,
					authorGuid
				).then(function(response) {
					$uibModalInstance.close(response);
					$scope.disableSubmitButton = false;
				}).catch(function () {
					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BALANCEUPDATE", false);
					$scope.disableSubmitButton = false;
				}).finally(function () {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
					$scope.disableSubmitButton = false;
				});
			}
	}]).controller('DirectWithdrawalModal', ['$uibModalInstance', '$translate', '$userService', '$state', '$scope', '$stateParams', 'notify', 'rest-cashier', 'rest-accounting', 'rest-tranta', 'rest-paymentmethods', '$filter', 'balance', 'domain', 'domainName', 'accountType', 'accountCode', 'currency', 'symbol', 'ownerGuid', 'authorGuid', 'errors', 'bsLoadingOverlayService',
		function ($uibModalInstance, $translate, $userService, $state, $scope, $stateParams, notify, cashierRest, acctRest, restTranta, restPaymentMethods, $filter, balance, domain, domainName, accountType, accountCode, currency, symbol, ownerGuid, authorGuid, errors, bsLoadingOverlayService) {
			var controller = this;

			controller.options = {};

			controller.model = {
				isBalanceLimitEscrow: false,
				currentBalanceAmount: balance,
				currentBalanceDisplay: $filter('cents')(balance, symbol),
				newBalanceAmount: balance,
				newBalanceDisplay: $filter('cents')(balance, symbol),
				oldAdjustmentAmount: 0,
				adjustmentAmount: 0,
				selectedDomainMethod: undefined,
				selectedPaymentMethod: undefined
			};
			controller.model.redirectToTransaction = true;

			controller.defineCurrentAndNewBalance = function (response, controller, $filter, symbol) {
				if (response !== undefined) {
					controller.model.currentBalanceAmount = response;
					controller.model.currentBalanceDisplay = $filter('cents')(response, symbol);
					controller.model.newBalanceAmount = response;
					controller.model.newBalanceDisplay = $filter('cents')(response, symbol);
				} else {
					controller.model.currentBalanceAmount = 0;
					controller.model.currentBalanceDisplay = $filter('cents')(0, symbol);
					controller.model.newBalanceAmount = 0;
					controller.model.newBalanceDisplay = $filter('cents')(0, symbol);
				}
			}

			controller.init = function () {
				restPaymentMethods.getPlayerBalance(domainName, ownerGuid).then(function (response) {
					controller.defineCurrentAndNewBalance(response, controller, $filter, symbol)
				});
			};

			controller.init();

			controller.fields = [];

			if (domain.playerBalanceLimit) {
				controller.fields = [
					{
						className: 'col-xs-12',
						key: 'player-wallet-headline',
						template:
							'<label htmlFor="controller.form_ui-select-single_domainmethods_2" className="control-label"' +
							'ng-if="to.label" style="">' +
							$translate.instant("UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.PLAYER_WALLET.TITLE") +
							" *" +
							'</label>',
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.PLAYER_WALLET.TITLE" | translate',
							'templateOptions.placeholder': '',
							'templateOptions.description': ''
						}
					},
					{
						className: 'col-xs-12',
						key: 'player-wallet-block',
						type: 'btn-radio',
						templateOptions: {
							btnclass: 'default',
							optionsAttr: 'bs-options',
							description: $translate.instant("UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.PLAYER_WALLET.DESC"),
							valueProp: 'value',
							labelProp: 'name',
							label: $translate.instant("UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.PLAYER_WALLET.TITLE"),
							optionsAttr: 'ui-options', "ngOptions": 'ui-options',
							placeholder: '',
							options: [
								{
									name: $translate.instant("UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.PLAYER_WALLET.PLAYER_BALANCE"),
									value: 0
								},
								{
									name: $translate.instant("UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.PLAYER_WALLET.BALANCE_LIMIT_ESCROW"),
									value: 1
								}
							],
							addFormControlClass: true,
							onClick: function ($event) {
								if ($event === 0) {
									restPaymentMethods.getPlayerBalance(domainName, ownerGuid).then(function (response) {
										controller.defineCurrentAndNewBalance(response, controller, $filter, symbol);
									});
									controller.model.isBalanceLimitEscrow = false;
								} else if ($event === 1) {
									restPaymentMethods.getEscrowWalletPlayerBalance(domainName, ownerGuid).then(function (response) {
										controller.defineCurrentAndNewBalance(response, controller, $filter, symbol);
									});
									controller.model.isBalanceLimitEscrow = true;
								}
								controller.model.adjustmentAmount = 0;
							}
						}
					}
				];
			}

			controller.fields = controller.fields.concat(
				[
					{
						key: "domainmethods",
						className: "col-xs-12 form-group",
						type: "ui-select-single",
						templateOptions: {
							label: "",
							description: "",
							placeholder: "",
							required: true,
							valueProp: 'name',
							labelProp: 'name',
							optionsAttr: 'ui-options', // 'bs-options' || 'ui-options'
							ngOptions: 'ui-options',
							options: [],
							onClick: function ($event, response) {
								if ($event !== undefined) {
									for (var i = 0; i < response.templateOptions.options.length; i++) {
										if (response.templateOptions.options[i].name === $event) {
											controller.model.selectedDomainMethod = response.templateOptions.options[i];
										}
									}
								}
							}
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.DOMAIN_METHOD.LABEL" | translate',
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.DOMAIN_METHOD.PLACEHOLDER" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.DOMAIN_METHOD.DESC" | translate'
						},
						controller: ['$scope', function ($scope) {
							restPaymentMethods.getDomainMethods(domainName).then(function (response) {
								$scope.to.options = response;
							});
						}]
					},
					{
						key: "paymentmethods",
						className: "col-xs-12 form-group",
						type: "ui-select-single",
						templateOptions: {
							label: "",
							description: "",
							placeholder: "",
							required: true,
							valueProp: 'descriptor',
							labelProp: 'descriptor',
							optionsAttr: 'ui-options', // 'bs-options' || 'ui-options'
							ngOptions: 'ui-options',
							options: [],
							onClick: function ($event, response) {
								if ($event !== undefined) {
									for (var i = 0; i < response.templateOptions.options.length; i++) {
										if (response.templateOptions.options[i].descriptor === $event) {
											controller.model.selectedPaymentMethod = response.templateOptions.options[i];
										}
									}
								}
							}
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.PAYMENT_METHOD.LABEL" | translate',
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.PAYMENT_METHOD.PLACEHOLDER" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.PAYMENT_METHOD.DESC" | translate'
						},
						controller: ['$scope', function ($scope) {
							if (controller.model.selectedDomainMethod !== undefined) {
								restPaymentMethods.getDirectWithdrawProcessorAccount(controller.model.selectedDomainMethod.method.code, ownerGuid).then(function (response) {
									$scope.controller.fields[3].templateOptions.options = response;
								});
							}
						}]
					},
					{
						className: "col-xs-12",
						key: 'adjustmentAmount',
						type: "ui-money-mask",
						templateOptions: {
							label: "",
							description: "",
							required: true,
							symbol: symbol
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.AMOUNT.LABEL" | translate',
							'templateOptions.placeholder': '"" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.AMOUNT.DESC" | translate'
						}
					},
					{
						className: "row v-reset-row",
						fieldGroup: [{
							className: "col-xs-2",
							type: "button",
							key: "radio-1477465160762",
							templateOptions: {
								text: '+ 5.00',
								btnType: "default btn-block btn-sm",
								onClick: function ($event) {
									controller.increase(5);
								}
							}
						}, {
							className: "col-xs-2",
							type: "button",
							key: "radio-1477465160762",
							templateOptions: {
								text: '+ 10.00',
								btnType: "default btn-block btn-sm",
								onClick: function ($event) {
									controller.increase(10);
								}
							}
						}, {
							className: "col-xs-2",
							type: "button",
							key: "radio-1477465160762",
							templateOptions: {
								text: '+ 50.00',
								btnType: "default btn-block btn-sm",
								onClick: function ($event) {
									controller.increase(50);
								}
							}
						}, {
							className: "col-xs-2",
							type: "button",
							key: "radio-1477465160762",
							templateOptions: {
								text: 'Current Balance',
								btnType: "default btn-block btn-sm",
								onClick: function ($event) {
									controller.setCurrentBalance();
								}
							}
						}]
					},
					{
						className: "col-xs-12",
						type: "input",
						key: "currentBalanceDisplay",
						templateOptions: {
							label: "current",
							disabled: true,
							placeholder: "",
							description: ""
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.CURRENT_BALANCE.LABEL" | translate',
							'templateOptions.placeholder': '"" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.CURRENT_BALANCE.DESC" | translate'
						}
					},
					{
						className: "col-xs-12",
						type: "input",
						key: "newBalanceDisplay",
						templateOptions: {
							label: "new",
							disabled: true,
							placeholder: "",
							description: ""
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.NEW_BALANCE.LABEL" | translate',
							'templateOptions.placeholder': '"" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.NEW_BALANCE.DESC" | translate'
						}
					},
					{
						className: "col-xs-12",
						type: "input",
						key: "comment",
						templateOptions: {
							label: "comment",
							required: true,
							placeholder: "",
							description: ""
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.DIRECT_WITHDRAWAL.FIELDS.COMMENT.LABEL" | translate',
							'templateOptions.placeholder': '"" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.FIELDS.COMMENT.DESC" | translate'
						}
					},
					{
						className: "col-xs-12",
						type: "ui-direct-withdrawal-warning-block-template",
						key: "warning_block",
						templateOptions: {
							label: "",
							placeholder: "",
							description: ""
						},
						expressionProperties: {
							'templateOptions.label': '',
							'templateOptions.placeholder': '',
							'templateOptions.description': ''
						}
					},
					{
						className: "col-xs-12",
						type: 'checkbox',
						key: 'redirectToTransaction',
						templateOptions: {
							label: 'Redirect to Transaction Page after Submit',
							placeholder: "",
							description: ""
						},
						expressionProperties: {
							'templateOptions.label': 'Redirect to Transaction Page after Submit',
							'templateOptions.placeholder': '',
							'templateOptions.description': ''
						}
					}
				]
			);

			///////////////////////

			$scope.$watch('controller.model.selectedDomainMethod', function () {
				if (controller.model.selectedDomainMethod !== undefined) {
					if (domain.playerBalanceLimit) {
						$scope.controller.fields[3].templateOptions.options = undefined;
					} else {
						$scope.controller.fields[1].templateOptions.options = undefined;
					}
					controller.model.selectedPaymentMethod = undefined;
					controller.model.paymentmethods = undefined;

					restPaymentMethods.getDirectWithdrawProcessorAccount(controller.model.selectedDomainMethod.method.code, ownerGuid).then(function (response) {
						if (domain.playerBalanceLimit) {
							$scope.controller.fields[3].templateOptions.options = response;
						} else {
							$scope.controller.fields[1].templateOptions.options = response;
						}
					});
				}
			});

			$scope.$watch('controller.model.adjustmentAmount', function() {
				if (controller.model.adjustmentAmount < 0) {
					return errorMessage(controller, bsLoadingOverlayService, notify, "UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.RESPONSE.ERRORS.NEGATIVE_NON_ZERO_VALUE");
				}
				if (Math.round(controller.model.adjustmentAmount * 100) > controller.model.currentBalanceAmount) {
					return errorMessage(controller, bsLoadingOverlayService, notify, "UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.RESPONSE.ERRORS.EXAGGERATED_ADJUSTMENT");
				}
				if (angular.isUndefined(controller.model.adjustmentAmount)) {
					controller.model.adjustmentAmount = 0;
					controller.form.$invalid = true;
				}
				controller.model.oldAdjustmentAmount = controller.model.adjustmentAmount;
				controller.model.newBalanceAmount = (Big(controller.model.currentBalanceAmount).minus(Math.round(controller.model.adjustmentAmount * 100))).toString();
				controller.model.newBalanceDisplay = $filter('cents')(controller.model.newBalanceAmount, symbol);
			});

			controller.increase = function(adjustmentAmount) {
				if ((Number(controller.model.adjustmentAmount) + adjustmentAmount) * 100 > controller.model.currentBalanceAmount) {
					console.log("minus values: ", (Big(controller.model.currentBalanceAmount).minus(Math.round(controller.model.adjustmentAmount * 100))).toString())
					controller.form.$invalid = true;
					bsLoadingOverlayService.stop({referenceId: controller.referenceId});
					notify.warning("UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.RESPONSE.ERRORS.EXAGGERATED_ADJUSTMENT");
					return false;
				}
				controller.model.adjustmentAmount = (Big(controller.model.adjustmentAmount).plus(adjustmentAmount)).toString();
			}

			controller.setCurrentBalance = function () {
				controller.model.adjustmentAmount = (Big(controller.model.currentBalanceAmount / 100)).toString();
			};

			controller.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};

			controller.referenceId = 'direct-withdrawal-overlay';

			controller.findValue = function(field) {
				var fieldValue = null;
				angular.forEach(controller.model, function(value, key) {
					if (key === field) {
						fieldValue = value;
					}
				});
				return fieldValue;
			}

			controller.doDirectWithdrawal = function () {
				bsLoadingOverlayService.start({referenceId: controller.referenceId});
				var amount = (new Big(controller.model.adjustmentAmount).times(100)).toString();
				if (amount === '0') {
					controller.form.$invalid = true;
					bsLoadingOverlayService.stop({referenceId: controller.referenceId});
					notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
					return false;
				}
				if (controller.form.$invalid) {
					angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
					notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
					bsLoadingOverlayService.stop({referenceId: controller.referenceId});
					return false;
				}

				var directWithdrawalTransaction = {};
				directWithdrawalTransaction.domainMethod = controller.model.selectedDomainMethod;
				directWithdrawalTransaction.userGuid = ownerGuid;
				directWithdrawalTransaction.processorAccountId = controller.model.selectedPaymentMethod.id;
				directWithdrawalTransaction.comment = controller.model.comment;
				directWithdrawalTransaction.amount = controller.model.adjustmentAmount;
				directWithdrawalTransaction.balanceLimitEscrow = controller.model.isBalanceLimitEscrow;

				cashierRest.executeDirectWithdrawal(directWithdrawalTransaction).then(function(response) {
					if (typeof(response) === 'number') {
						notify.success("The transaction was added successfully.");
						if (controller.model.redirectToTransaction) {
							$state.go("dashboard.cashier.transaction", {
								tranId: response,
								domainName: $stateParams.domainName
							});
						}
					}
					if (!response._successful) {
						if (response._status === 500) {
							$uibModalInstance.close(response);
							notify.error(response._data2);
						}
					}
					$uibModalInstance.close(response);
				}).catch(function(error) {
					errors.catch("", false)(error)
				}).finally(function () {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			}
		}]);

function errorMessage(controller, bsLoadingOverlayService, notify, errorMessage) {
	controller.form.$invalid = true;
	bsLoadingOverlayService.stop({referenceId: controller.referenceId});
	notify.warning(errorMessage);
	controller.model.adjustmentAmount = controller.model.oldAdjustmentAmount;
	return false;
}
