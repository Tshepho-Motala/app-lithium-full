'use strict';

angular.module('lithium')
	.controller('LeaderboardList', ["$translate", "$userService", "$dt", "$state", "$uibModal", "$rootScope",
	function($translate, $userService, $dt, $state, $uibModal, $rootScope) {
		var controller = this;
		
		controller.referenceId = "LeaderboardList_"+(Math.random()*1000);
		
		controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) 
					controller.selectedDomains.push(controller.domains[d].name);
			}
			if (controller.selectedDomains.length === controller.domains.length) {
				controller.selectedDomainsDisplay = $translate.instant('UI_NETWORK_ADMIN.PLAYERS.LINKS.OPTIONS.DOMAINS_SELECTED');
			} else {
				controller.selectedDomainsDisplay = ""+controller.selectedDomains.length+" Domain(s) Selected";
			}
		};
		
		controller.domainSelectAll = function() {
			for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
			controller.domainSelect();
		};
		controller.domainSelectNone = function() {
			for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = false;
			controller.domainSelect();
		};
		controller.domainSelectAll();
		
		function domainArray() {
			var str = "";
			angular.forEach(controller.selectedDomains, function(d) {
				str += d+",";
			});
			return str;
		}
		
		var baseUrl = "services/service-leaderboard/leaderboard/admin/table?1=1&domains="+domainArray()+"";
		controller.leaderboardTable = $dt.builder()
		.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NAME.TITLE")))
//		.column($dt.columnlength('description', 20, '...').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DESCRIPTION.TITLE")))
		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewLeaderboard(data) } }]))
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.VISIBLE.TITLE"),
				[{lclass: function(data) {
					if (data.visible) {
						return "success";
					}
					return "danger";
				},
				text: function(data) {
					return data.visible+"";
				},
				uppercase:true
				}]
			)
		)
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.ENABLED.TITLE"),
				[{lclass: function(data) {
					if (data.enabled) {
						return "success";
					}
					return "danger";
				},
				text: function(data) {
					return data.enabled+"";
				},
				uppercase:true
				}]
			)
		)
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DOMAINNAME.TITLE")))
		.column($dt.columncombine('xpLevelMin', 'xpLevelMax').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.REQUIREMENTS.XPLEVEL")))
		.column($dt.columnformatdate('startDate').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.STARTDATE.TITLE")))
		.column($dt.columncombine('xpPointsMin', 'xpPointsMax').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.REQUIREMENTS.XPPOINTS")))
//		.column($dt.column('xpPointsRequired').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.TYPE.TITLE")))
//		.column($dt.columnperiod('xpPointsGranularity').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.GRANULARITY.TITLE")))
		.column($dt.columncombineperiod('xpPointsPeriod', 'xpPointsGranularity').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.REQUIREMENTS.XPHISTORY")))
		.options(baseUrl)
		.order([0, 'desc'])
		.build();
		
		controller.viewLeaderboard = function(data) {
			console.log(data);
			$state.go("dashboard.leaderboard.view", {id: data.id});
		}
		
		controller.addLeaderboard = function() {
			$state.go("dashboard.leaderboard.add");
		}
		
		controller.displayList = function() {
			controller.tableLoad();
		}
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.leaderboardTable.instance)) {
				baseUrl = "services/service-leaderboard/leaderboard/admin/table?1=1";
				baseUrl += "&domains="+domainArray();
				controller.leaderboardTable.instance._renderer.options.ajax = baseUrl;
//				controller.leaderboardTable.instance.reloadData();
				controller.leaderboardTable.instance.rerender();
			}
		}
}]);
