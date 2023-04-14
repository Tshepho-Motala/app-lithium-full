'use strict';

angular.module('lithium')
	.controller('PlayersTagViewPlayers', ["UserRest", "$translate", "$uibModal", "notify", "tag", "$dt", "$state", "$rootScope", "DTOptionsBuilder",
		function(userRest, $translate, $uibModal, notify, tag, $dt, $state, $rootScope, DTOptionsBuilder) {
		var controller = this;
		controller.tag = tag;
		
		var baseUrl = "services/service-user/backoffice/players/tag/view/"+tag.id+"/players?1=1";
		controller.playersTable = $dt.builder()
		.column($dt.column('username').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME")))
		.column($dt.linkscolumn("", [{ permission: "player_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.players.player.summary", {id:data.id, domainName:data.domain.name}) } }]))
		.column($dt.columnWithClass('status.name', 'label label-lg label-default').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.NAME")))
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.DOMAIN.NAME")))
		.column($dt.column('firstName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.NAME")))
		.column($dt.column('lastName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.NAME")))
		.column($dt.linkscolumn("", [{ permission: "player_edit", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.REMOVE_LC", click: function(data) { controller.removePlayer(data) } }]))
		.options(baseUrl)
		.order([0, 'desc'])
		.build();
		
		controller.removePlayer = function(data) {
			console.log(data);
			userRest.tagRemovePlayer(controller.tag.id, data.username).then(function(user) {
				notify.success("UI_NETWORK_ADMIN.PLAYERS.TAGS.REMOVEPLAYERSUCCESS");
				controller.tableLoad();
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.PLAYERS.TAGS.REMOVEPLAYERERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.playersTable.instance)) {
				baseUrl = "services/service-user/backoffice/players/tag/view/"+tag.id+"/players?1=1";
				controller.playersTable.instance._renderer.options.ajax = baseUrl;
				controller.playersTable.instance.rerender();
			}
		}
		
		controller.addPlayer = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/players/tags/addplayer.html',
				controller: 'TagPlayerAdd',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					tag: function() {
						return tag;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/players/tags/addplayer.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(result) {
				controller.tableLoad();
			});
		}
	}
]);