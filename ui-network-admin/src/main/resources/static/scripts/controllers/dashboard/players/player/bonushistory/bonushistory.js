'use strict'

angular.module('lithium').controller('PlayerBonusHistoryController', ['$scope', 'bsLoadingOverlayService', 'rest-casino', 'user','$dt', '$translate', 'currencySymbol',
	function($scope, bsLoadingOverlayService, casinoRest, user, $dt, $translate, currencySymbol) {
		var controller = this;
		$scope.referenceId = 'bonus-history-overlay';
		$scope.page = 0;
		$scope.user = user;

		controller.bonusTypeKeyString = function() {
			$translate('UI_NETWORK_ADMIN.PLAYER.BONUS.TYPE.SIGNUP').then(function success (data) {
				controller.signup = data
			})
			$translate('UI_NETWORK_ADMIN.PLAYER.BONUS.TYPE.DEPOSIT').then(function success (data) {
				controller.deposit = data
			})
			$translate('UI_NETWORK_ADMIN.PLAYER.BONUS.TYPE.TRIGGER').then(function success (data) {
				controller.trigger = data
			})
			$translate('UI_NETWORK_ADMIN.PLAYER.BONUS.TYPE.BONUSTOKEN').then(function success (data) {
				controller.bonustoken = data
			})
		}
		controller.bonusTypeKeyString();

		$scope.cancelPending = function(data) {
			console.log(data);
			bsLoadingOverlayService.start({referenceId:$scope.referenceId});
			casinoRest.cancelPendingBonus(data.playerGuid, data.id).then(function(){
				//$state.reload();
				controller.pendingBonusTable.instance.reloadData();
			}).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
			});
		}

		var baseBonusHistoryUrl = "services/service-casino/casino/bonus/table/history?playerGuid=";

		controller.bonusHistoryTable = $dt.builder()
			.column($dt.column(function(data) {
				if(data.bonus.bonusType === 0) {
					return controller.signup;
				} else if(data.bonus.bonusType === 1) {
					return controller.deposit;
				} else if(data.bonus.bonusType === 2) {
					return controller.trigger;
				} else {
					return controller.bonustoken;
				}
			}).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.TYPE.TITLE")))
			.column($dt.column('bonus.bonusCode').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.CODE")))
			.column($dt.column('bonus.id').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.REVISION_CODE")))
			.column($dt.columncurrency('triggerAmount', currencySymbol).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.TRIGGERAMOUNT")))
			.column($dt.columncurrency('bonusAmount', currencySymbol).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.BONUSAMOUNT")))
			.column($dt.column(function(data) {
				return data.bonusPercentage + ' %';
			}).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.BONUSPERCENTAGE")))
			// .column($dt.columncurrency(function(data) {
			// 	if(data.bonus.bonusTokens != null && data.bonus.bonusTokens.length > 0){
			// 		if(data.customBonusTokenAmountCents <= 0){
			// 			return data.bonus.bonusTokens[0].amount;
			// 		} else {
			// 			return data.customBonusTokenAmountCents;
			// 		}
			// 	} else {
			// 		return data.customFreeMoneyAmountCents;
			// 	}
			// }, currencySymbol).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.BONUSAMOUNTALLOCATED")))
			// .column($dt.columncurrency(function(data) {
			// 	if(data.bonus.bonusTokens != null && data.bonus.bonusTokens.length > 0){
			// 		return data.bonus.bonusTokens[0].minimumOdds;
			// 	} else {
			// 		return 0;
			// 	}
			// }, currencySymbol).withTitle($translate("UI_NETWORK_ADMIN.BONUS.BONUSTOKEN.MINIMUMODDS.NAME")))
			.column($dt.columncurrency(function(data) {
				if(data.bonus.freeMoneyAmount != null){
					if(data.customBonusTokenAmountCents <= 0){
						return data.bonus.freeMoneyAmount;
					} else {
						return data.customFreeMoneyAmountCents;
					}
				} else {
					return 0;
				}
			}, currencySymbol).withTitle($translate("UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.AMOUNT.NAME")))
			.column($dt.column(function(data) {
				if(data.playerBonusFreespinHistoryProjection != null){
					return data.playerBonusFreespinHistoryProjection.bonusRulesFreespins.freespins;
				} else {
					return 0;
				}
			}).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.FREESPINS.AWARDED")))
			.column($dt.column(function(data) {
				if(data.playerBonusFreespinHistoryProjection != null){
					return data.playerBonusFreespinHistoryProjection.freespinsRemaining;
				} else {
					return 0;
				}
			}).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.FREESPINS.REMAINING")))
			// .column($dt.column(function(data) {
			// 	if(data.playerBonusFreespinHistoryProjection != null){
			// 		return data.playerBonusFreespinHistoryProjection.bonusRulesFreespins.wagerRequirements;
			// 	} else {
			// 		return 0;
			// 	}
			// }).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.FREESPINS.WAGERREQUIREMENTS")))
			// .column($dt.column(function(data) {
			// 	 return (data.playThroughCents / 100) + '/' + (data.playThroughRequiredCents / 100);
			// }).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.PLAYTHROUGH")))
			.column($dt.columnformatdatetime(function(data) {
				return data.startedDate;
			}).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.STARTEDDATE")))
			.column($dt.columnformatdatetime(function(data) {
				return (data.startedDate + (data.bonus.validDays * 86400000));
			}).withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.ENDDATE")))
			.column($dt.column('completed').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.COMPLETED")))
			.column($dt.column('cancelled').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.CANCELLED")))
			.column($dt.column('expired').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.EXPIRED")))
			.column($dt.column('description').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUS.DESC")))
			.options( {url: baseBonusHistoryUrl+user.guid, type: 'POST'} )
			.build();

		var basePendingBonusUrl = "services/service-casino/casino/bonus/table/pending?playerGuid=";
		
		controller.pendingBonusTable = $dt.builder()
		.column($dt.column('bonusRevision.bonusName').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.BONUSNAME")))
		.column($dt.column('bonusRevision.bonusCode').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.BONUSCODE")))
		.column($dt.column('bonusRevision.id').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.BONUSREVISIONCODE")))
		.column($dt.columnformatdate('createdDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.CREATEDDATE")))
		.column($dt.columncurrency('triggerAmount').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.DEPOSITAMOUNT")))
		.column($dt.columncurrency('bonusAmount').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.BONUSAMOUNT")))
		.column($dt.columncurrency('customFreeMoneyAmountCents').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.BONUSCUSTOMFREEAMOUNT")))
		.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.ID")))
		.column($dt.linkscolumn("", [{ permission: "bonus_view", permissionType:"any", permissionDomain: function(data) { return data.bonusRevision.domain.name;}, title: "GLOBAL.ACTION.CANCEL", click: function(data){ $scope.cancelPending(data) } }]))
		.options( {url: basePendingBonusUrl+user.guid, type: 'POST'} )
		.build();

		var baseBonusTokenUrl = "services/service-casino/casino/bonus/find/bonus-token/table/player?playerGuid=";

		controller.bonusTokenBonusTable = $dt.builder()
			.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSTOKEN.ID")))
			.column($dt.columncurrency('bonusToken.amount').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSTOKEN.AMOUNT")))
			.column($dt.columncurrency('customTokenAmountCents').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSTOKEN.CUSTOM_AMOUNT")))
			.column($dt.column('bonusToken.minimumOdds').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSTOKEN.MINIMUM_ODDS")))
			.column($dt.columnformatdate('createdDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSTOKEN.CREATION_DATE")))
			.column($dt.columnformatdate('expiryDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSTOKEN.EXPIRATION_DATE")))
			.column($dt.linkscolumn("", [{ permission: "bonus_view", permissionType:"any", permissionDomain: function(data) { return $scope.user.guid.split[0] }, title: "GLOBAL.ACTION.CANCEL", click: function(data){ $scope.cancelPlayerBonusToken(data) } }]))
			.options( {url: baseBonusTokenUrl+user.guid, type: 'POST'} )
			.build();

		$scope.cancelPlayerBonusToken = function(data) {
			console.log(data);
			bsLoadingOverlayService.start({referenceId:$scope.referenceId});
			casinoRest.cancelActivePlayerBonusToken($scope.user.guid, data.id).then(function() {
				controller.bonusTokenBonusTable.instance.reloadData();
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
			});
		}
	}
]);
