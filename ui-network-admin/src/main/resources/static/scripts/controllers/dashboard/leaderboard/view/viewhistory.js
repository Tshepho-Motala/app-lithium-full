'use strict';

angular.module('lithium')
	.controller('LeaderboardHistoryView', ["leaderboardHistory", "$userService", "notify", "errors", "$state", "LeaderboardRest", "$dt", "$translate", "$filter", 
	function(leaderboardHistory, $userService, notify, errors, $state, lbRest, $dt, $translate, $filter) {
		var controller = this;
		
		controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		
		controller.model = {};
		controller.model = leaderboardHistory;
		controller.modelOriginal = angular.copy(leaderboardHistory);
		controller.options = { formState: { readOnly: true } };
		
		var baseUrl = "services/service-leaderboard/leaderboard/admin/history/"+controller.model.id+"/places/table?1=1";
		controller.leaderboardTable = $dt.builder()
		.column($dt.column('rank').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.HISTORY.RANK")))
		.column($dt.column('points').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.HISTORY.POINTS")))
		.column($dt.column('score').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.HISTORY.SCORE")))
		.column($dt.column('user.guid').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.HISTORY.PLAYER")))
//		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewHistory(data) } }]))
		.options(baseUrl)
		.order([0, 'asc'])
		.build();
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.leaderboardTable.instance)) {
				baseUrl = "services/service-leaderboard/leaderboard/admin/history/"+controller.model.id+"/places/table?1=1";
				controller.leaderboardTable.instance._renderer.options.ajax = baseUrl;
				controller.leaderboardTable.instance.rerender();
			}
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			lbRest.add(controller.model).then(function(category) {
				notify.success("UI_NETWORK_ADMIN.LEADERBOARD.ADD.SUCCESS");
				$state.go("dashboard.leaderboard.list");
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.viewHistory = function(data) {
			console.log(data);
		}
		
		controller.list = function() {
			$state.go("dashboard.leaderboard.view", {id:controller.model.leaderboard.id});
		};
	}
]);