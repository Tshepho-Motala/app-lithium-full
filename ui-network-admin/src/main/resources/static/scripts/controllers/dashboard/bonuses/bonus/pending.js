'use strict';

angular.module('lithium')
	.controller('BonusPendingController', ["bonus", "bonusRevision", "$scope", "$translate", "$dt", "DTOptionsBuilder", "$filter", "registerbonususersearch",
	function(bonus, bonusRevision, $scope, $translate, $dt, DTOptionsBuilder, $filter, registerbonususersearch) {
		var controller = this;
		
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;
		
		// console.log(controller.bonus);
		// console.log(controller.bonusRevision);
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.pendingBonusTable = $dt.builder()
		.column($dt.columnformatdate('createdDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.CREATEDDATE")))
		.column($dt.column('playerGuid').withTitle('Player'))
		.column($dt.columncurrency('triggerAmount').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.DEPOSITAMOUNT")))
		.column($dt.columncurrency('bonusAmount').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.BONUSAMOUNT")))
		.column($dt.columncurrency('customFreeMoneyAmountCents').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.BONUSCUSTOMFREEAMOUNT")))
		.options({ url:"services/service-casino/casino/bonus/table/pending/"+bonusRevision.id, type:"POST" }, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.pendingBonusTable.instance.reloadData(function(){}, false);
		}
	}
]);
