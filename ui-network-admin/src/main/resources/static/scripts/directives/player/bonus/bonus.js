'use strict';

angular.module('lithium')
.directive('bonus', function() {
	return {
		templateUrl:'scripts/directives/player/bonus/bonus.html',
		scope: {
			data: "="
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'rest-casino', 'rest-accounting', 'errors', 'bsLoadingOverlayService', 'notify', '$timeout',
		function($q, $uibModal, $scope, casinoRest, acctRest, errors, bsLoadingOverlayService, notify, $timeout) {
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
				$scope.data.accountCode = 'PLAYER_BALANCE_CASINO_BONUS';
			}
			if (!$scope.data.accountType) {
				$scope.data.accountType = 'PLAYER_BALANCE';
			}
			if (!$scope.data.displayOnly) {
				$scope.data.displayOnly = true;
			}

			var dayInMs = 86400000;
			$scope.referenceId = 'bonus-overlay';
			$scope.refresh = function() {
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				casinoRest.activeBonus($scope.data.ownerGuid).then(function(response) {
					$scope.playerBonus = response;
					if (response.playerBonusProjection != null) {
						$scope.endDate = (response.playerBonusProjection.current.startedDate+(response.playerBonusProjection.current.bonus.validDays*dayInMs));
						$scope.refreshBalance();
						$scope.data.displayOnly = false;
					}
				}).catch(
					//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};
			$scope.refreshBalance = function() {
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				acctRest.balance($scope.data.accountCode, $scope.data.accountType, $scope.data.currency, $scope.data.domainName, $scope.data.ownerGuid).then(function(response) {
					if (angular.isDefined(response)) {
						$scope.balance = response;
					} else {
						$scope.balance = 0;
					}
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BALANCE", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};

			$scope.cancel = function() {
				console.log("Cancelling bonus");
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				casinoRest.cancelActiveBonus($scope.data.ownerGuid).then(function(response) {
					$scope.playerBonus = null;
					$scope.data.displayOnly = true;
				}).catch(
					//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};

			$scope.playerCasinoBonusBalance = {
				box: "info",
				currency: "USD",
				domainName: $scope.data.domainName,
				accountCode: "PLAYER_BALANCE_CASINO_BONUS",
				accountType: "PLAYER_BALANCE",
				ownerGuid: $scope.data.ownerGuid,
				authorGuid: $scope.data.ownerGuid,
				title: "Player Casino Bonus Balance"
			};

			$scope.adjust = function () {
				var balanceModalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
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
					$scope.refreshBalance();
				});
			};

			$scope.refresh();

			$scope.registerBonus = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl: 'scripts/directives/player/bonus/registerbonus.html',
					controller: 'RegisterBonusModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						user: function() {
							return $scope.data.user;
						},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/bonus/registerbonus.js' ]
							})
						}
					}
				});

				modalInstance.result.then(function(response) {
					if (response != null) console.log(response);
				});
			};
		}]
	}
}).controller('BalanceModal', ['$uibModalInstance', '$translate', '$userService', '$scope', 'notify', 'rest-accounting', 'rest-tranta', '$filter', 'balance', 'domainName', 'accountType', 'accountCode', 'currency', 'symbol','ownerGuid', 'authorGuid', 'errors', 'bsLoadingOverlayService',
	function ($uibModalInstance, $translate, $userService, $scope, notify, acctRest, restTranta, $filter, balance, domainName, accountType, accountCode, currency, symbol, ownerGuid, authorGuid, errors, bsLoadingOverlayService) {
	var controller = this;
	//console.log("controller 2", balance, domainName, currency, ownerGuid, authorGuid);

	controller.options = {};
	controller.model = {
		balance: $filter('cents')(balance, symbol),
		newbalance: balance,
		newbalancedisplay: $filter('cents')(balance, symbol),
		adjustment: 0
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
			// optionsAttr: 'bs-options',
			valueProp: 'accountTypeCode',
			labelProp: 'accountTypeCode',
			optionsAttr: 'ui-options',
			ngOptions: 'ui-options',
			options: []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.DESC" | translate'
		},
		controller: ['$scope', function($scope) {
			restTranta.all(domainName).then(function(response) {
				$scope.to.options = response;
			});
		}]
	},{
		className : "col-xs-12",
		type : "input",
		key : "comment",
		templateOptions : {
			label : "comment",
			required : true,
			placeholder : "",
			description : ""
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

	$scope.$watch('controller.model.adjustment', function() {
		if (angular.isUndefined(controller.model.adjustment)) {
			controller.model.adjustment = 0;
			controller.form.$invalid = true;
		}
		controller.model.newbalance = (Big(controller.model.adjustment).times(100).plus(balance)).toString(); //balance + (controller.model.adjustment*100);
		controller.model.newbalancedisplay = $filter('cents')(controller.model.newbalance, symbol );
	});

	controller.decrease = function(adjustment) {
		controller.model.adjustment = Big(controller.model.adjustment).minus(adjustment).toString();
	}
	controller.increase = function(adjustment) {
		controller.model.adjustment = Big(controller.model.adjustment).plus(adjustment).toString();
	}
	controller.referenceId = 'balanceadjust-overlay';
	controller.doAdjust = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		var amount = (new Big(controller.model.adjustment).times(100)).toString();
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
		acctRest.balanceadjust(
			amount,
			new Date(),
			accountCode,
			accountType,
			'BALANCE_ADJUST',
			controller.model.trantypeacct,
			'MANUAL_BALANCE_ADJUST',
			controller.model.comment,
			currency,
			domainName,
			ownerGuid,
			authorGuid
		).then(function(response) {
			$uibModalInstance.close(response);
		}).catch(
			errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BALANCEUPDATE", false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}
}]);;
