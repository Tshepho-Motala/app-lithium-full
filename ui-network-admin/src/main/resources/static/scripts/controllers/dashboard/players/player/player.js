'use strict';

angular.module('lithium')
	.controller('PlayerController', ["user", "userData", "tabs", "domainInfo", "playerBalance", "limits", "depositLimits", "balanceLimits", "coolOff", "realityCheck", "selfExclusion", "userRestrictions", "ltDeposits", "ltWithdrawals", "pendingWithdrawals", "unlockedGameUserStatuses", "UserRest", "$scope", "$state", "$uibModal", "rest-domain", "$security", "$filter", 'rest-paymentmethods', "domainSettings",
	function(user, userData, tabs, domainInfo, playerBalance, limits, depositLimits, balanceLimits, coolOff, realityCheck, selfExclusion, userRestrictions, ltDeposits, ltWithdrawals, pendingWithdrawals, unlockedGameUserStatuses, UserRest, $scope, $state, $uibModal, restDomain, $security, $filter, restPaymentMethods, domainSettings) {
		var controller = this;
		angular.forEach(tabs, function(tab) {
			if ($state.current.name.startsWith(tab.name)) {
				if (!$security.hasRoleForDomain(user.domain.name, $filter("uppercase")(tab.roles.trim()))) {
					$state.transitionTo('dashboard.403', $state.params, {location: false, inherit: false});
				}
			}
		});
		controller.domainSettings = domainSettings;
		controller.user = user;
		controller.userData = userData;
		controller.userData.limits = limits;
		controller.userData.coolOff = coolOff;
		controller.userData.realityCheck = realityCheck;
		controller.userData.domain = domainInfo;
		controller.userData.playerBalance = [];
		controller.userData.selfExclusion = selfExclusion;
		controller.userData.userRestrictions = userRestrictions;
		controller.userData.depositLimits = depositLimits;
		controller.userData.balanceLimits = balanceLimits;

		controller.ltDeposits = ltDeposits;
		controller.ltWithdrawals = ltWithdrawals;
		controller.pendingWithdrawals = pendingWithdrawals;
		controller.userData.freeGamesLocked = unlockedGameUserStatuses == undefined || unlockedGameUserStatuses.length === 0;
		controller.escrowBalance = 0
		controller.ltDepositsAttempts = 0
		controller.ltDepositsSuccess = 0
		controller.ltPlacedTo = 0

		restPaymentMethods.getEscrowWalletPlayerBalance(user.domain.name, user.guid).then(function(response) {
			let resp = response
			let total = 0;
			if (resp) {
				total += resp
			}
			 controller.escrowBalance = total
		}).catch(function(error) {
			errors.catch('', false)(error);
		});

		restPaymentMethods.getLtDepositsAttempts(user.domain.name, user.guid).then(function(response) {
			let resp = response
			let total = 0;
			if (resp) {
				total += resp.data
			}
			controller.ltDepositsAttempts = total
		}).catch(function(error) {
			errors.catch('', false)(error);
		});

		restPaymentMethods.getLtDepositsSuccess(user.domain.name, user.guid).then(function(response) {
			let resp = response
			let total = 0;
			if (resp) {
				total += resp.data
			}
			controller.ltDepositsSuccess = total
		}).catch(function(error) {
			errors.catch('', false)(error);
		});

		restPaymentMethods.getLtPlacedTo(user.domain.name, user.guid).then(function(response) {
			let resp = response
			let total = 0;
			if (resp) {
				total += resp.data
			}
			controller.ltPlacedTo = total
		}).catch(function(error) {
			errors.catch('', false)(error);
		});

		//Set the page title
		document.title = controller.user.firstName + " " + controller.user.lastName + " (" + controller.user.guid + ") | LBO";

        controller.timeConvert = function (timeInMilliSec) {

            let timeInSec = Math.round(timeInMilliSec / 1000);  // 10803


            var hours = Math.floor(timeInSec / 3600);
            var minutes = Math.floor(timeInSec /60 - hours*60);
            var seconds = Math.floor(timeInSec - hours*3600 - minutes*60);

            if (hours.toString().length<2) {
                hours = "0" + hours;
            }
            if (minutes.toString().length<2) {
                minutes = "0" + minutes;
            }
            if (seconds.toString().length<2) {
                seconds = "0" + seconds;
            }
            return hours + " : " + minutes + " : "+ seconds;
        }

        controller.futureDateToTimeRemaining = function (futureDate) {
			var future = new Date(futureDate);
        	var now = new Date();

			var seconds = Math.floor((future - (now)) / 1000);
			var minutes = Math.floor(seconds / 60);
			var hours = Math.floor(minutes / 60);
			var days = Math.floor(hours / 24);

			hours = hours - (days * 24);
			minutes = minutes - (days * 24 * 60) - (hours * 60);
			seconds = seconds - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);

			return days + 'd ' + hours + 'h ' + minutes + 'm ' + seconds + 's';
		}

        controller.calculateDuration = function() {
            var dateDiff = new Date() - controller.user.lastLoggedInDate;
            return controller.timeConvert(dateDiff);
        }

        controller.checkSessionData = function() {
            if (controller.user.duration  > 0 ) {
                controller.sessionDuration = controller.timeConvert(controller.user.duration);
            } else {
                controller.sessionDuration = controller.calculateDuration();
            }
        }
        controller.checkSessionData();


		controller.changelogs = {
			domainName: user.domain.name,
			entityId: user.id,
			restService: UserRest,
			reload: 0
		}

		controller.userData.changelogs = controller.changelogs;

		angular.forEach(playerBalance, function(balance, index) {
			var objBal = {
				id: index,
				balance: balance.balances['PLAYER_BALANCE'],
				currency:{
					code: balance.domainCurrency.currency.code,
					isDefault: balance.domainCurrency.isDefault,
					symbol: balance.domainCurrency.symbol + ' '
				},
				accountType: 'PLAYER_BALANCE'
			};

			if (balance.domainCurrency.isDefault) {
				controller.userData.defaultBalance = objBal;
			}

			controller.userData.playerBalance.push(objBal);
		});

		$scope.domain = user.domain;
		controller.tabs = tabs;
		
		controller.setTab = function(tab) {
			if (tab.tclass !== 'disabled') {
				controller.tab = tab;
				$state.go(tab.name);
			}
		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});


		// FIXME: Perhaps resolve the needed domain settings before reaching this page, in the state provider...
		controller.findCurrentDomainSetting = function() {
			restDomain.findCurrentDomainSetting(controller.user.domain.name, 'failed_passwd_reset_threshold').then(function (response) {
				controller.domainFailedResetCountSetting = 20;
				controller.failedResetWarn = false;
				controller.failedResetInfo = false;
				controller.failedResetZero = false;
				var settings = response.plain();
				if (settings && settings.labelValue) {
					controller.domainFailedResetCountSetting = settings.labelValue.value;
				}
				if (controller.user.failedResetCount > (controller.domainFailedResetCountSetting * .75)) {
					controller.failedResetWarn = true;
				} else if (controller.user.failedResetCount > 0) {
					controller.failedResetInfo = true;
				} else {
					controller.failedResetZero = true;
				}
			});
		}
		controller.findCurrentDomainSetting();

		controller.clearFailedResetCount = function() {
			UserRest.clearFailedResetCount(controller.user.guid).then(function(response) {
				controller.user.failedResetCount = 0;
				controller.findCurrentDomainSetting();
				controller.changelogs.reload += 1;
			});
		}
	}
]);
