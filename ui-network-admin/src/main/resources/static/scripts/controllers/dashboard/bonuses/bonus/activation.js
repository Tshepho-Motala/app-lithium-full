'use strict';

angular.module('lithium')
	.controller('BonusActivationController', ["bonus", "bonusRevision", "$scope", "$translate", "$dt", "DTOptionsBuilder", "$filter", "registerbonususersearch",
	function(bonus, bonusRevision, $scope, $translate, $dt, DTOptionsBuilder, $filter, registerbonususersearch) {
		var controller = this;
		
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;
		
		// console.log(controller.bonus);
		// console.log(controller.bonusRevision);
		
		var statusKeys = [
			"UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.COMPLETED",
			"UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.CANCELLED",
			"UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.EXPIRED",
			"UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.ACTIVE"
		];
		$translate(statusKeys).then(function success(response) {
			controller.statuses = [];
			for (var s in response) {
				controller.statuses.push({"id":$filter('lowercase')(response[s]),"name":response[s],"selected":true});
			}
			controller.statusSelectAll();
			controller.statusSelect();
		});
		
		controller.statusSelect = function() {
			controller.selectedStatuses = [];
			for (var d = 0; d < controller.statuses.length; d++) {
				if (controller.statuses[d].selected) 
					controller.selectedStatuses.push(controller.statuses[d].id);
			}
			if (controller.selectedStatuses.length == controller.statuses.length) {
				controller.selectedStatusesDisplay = "Status";
			} else {
				controller.selectedStatusesDisplay = "Selected (" + controller.selectedStatuses.length + ")";
			}
		};
		
		controller.statusSelectAll = function() {
			for (var d = 0; d < controller.statuses.length; d++) controller.statuses[d].selected = true;
			controller.statusSelect();
		};
		
		controller.bonusTableLoad = function() {
			if (!angular.isUndefined(controller.bonusActivationTable.instance)) {
				baseBonusActivationUrl = "services/service-casino/casino/bonus/table/activation?1=1";
				baseBonusActivationUrl += "&bonusRevisionId="+bonusRevision.id;
				baseBonusActivationUrl += "&status="+controller.selectedStatuses;
				controller.bonusActivationTable.instance._renderer.options.ajax = baseBonusActivationUrl;
				controller.bonusActivationTable.instance.rerender();
			}
		}
		
		controller.openRegisterBonus = function() {
			registerbonususersearch.registerBonus().then(function(response) {
//				if (response != null) console.log(response);
				controller.refresh();
			});
		}
		
		controller.refresh = function() {
			controller.bonusTableLoad();
		}
		
		function statusHtml(data, type, full, meta) {
			var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
			if (full.completed === true) {
				$translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.COMPLETED").then(function success(response) {
					parentDom.html('<span style="display:list-item;line-height:inherit;" class="label label-success">'+$filter('uppercase')(response)+'</span>');
				});
			} else if (full.cancelled === true) {
				$translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.CANCELLED").then(function success(response) {
					parentDom.html('<span style="display:list-item;line-height:inherit;" class="label label-warning">'+$filter('uppercase')(response)+'</span>');
				});
			} else if (full.expired === true) {
				$translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.EXPIRED").then(function success(response) {
					parentDom.html('<span style="display:list-item;line-height:inherit;" class="label label-danger">'+$filter('uppercase')(response)+'</span>');
				});
			} else {
				$translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION.STATUS.ACTIVE").then(function success(response) {
					parentDom.html('<span style="display:list-item;line-height:inherit;" class="label label-info">'+$filter('uppercase')(response)+'</span>');
				});
			}
			return "";
		}

		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [1, 'desc']);
		var baseBonusActivationUrl = "services/service-casino/casino/bonus/table/activation?bonusRevisionId="+bonusRevision.id;
		
		controller.bonusActivationTable = $dt.builder()
		.column($dt.emptycolumnrenderwith($translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION.TITLE.STATUS"), statusHtml).notSortable())
		.column($dt.columnformatdatetime('startedDate').withTitle($translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION.TITLE.STARTED")))
		.column($dt.columnformatdatetime('completed').withTitle($translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION_HISTORY.COMPLETED")))
		.column($dt.column('playerBonus.playerGuid').withTitle($translate("UI_NETWORK_ADMIN.BONUS.ACTIVATION.TITLE.PLAYER")))
		.options(baseBonusActivationUrl, null, dtOptions, null)
		.build();
	}
]);
