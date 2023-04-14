'use strict';

angular.module('lithium')
	.controller('PlayerAbandonedList', ["$translate", "$userService", "$dt", "$state", "$rootScope", "bsLoadingOverlayService", "notify", "errors", "UserRest",
	function($translate, $userService, $dt, $state, $rootScope, bsLoadingOverlayService, notify, errors, userRest) {
		var controller = this;
		controller.referenceId = "PlayerAbandonedList_"+(Math.random()*1000);
		console.log("Abandoned list table");
		controller.stage = "initial-details";
		controller.stageLabel = "Initial Details";

		function domainArray() {
			var str = "";
			angular.forEach(controller.selectedDomains, function(d) {
				str += d.name+",";
			});
			return str;
		}
		controller.noStageFilter=function(){
			controller.stage="";
			controller.stageLabel="All Stages";
			
		}
		controller.selectSignUpStage=function(stage,label){
			controller.stage = stage
			controller.stageLabel=label;
		}
		function catArray() {
			var cats = "";
			angular.forEach(controller.selectedTypes, function(d) {
				cats += d.id+",";
			});
			return cats;
		}
		
		controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) 
					controller.selectedDomains.push(controller.domains[d]);
			}
			if (controller.selectedDomains.length === controller.domains.length) {
				controller.selectedDomainsDisplay = $translate.instant('UI_NETWORK_ADMIN.PLAYERS.LINKS.OPTIONS.DOMAINS_SELECTED');
			} else {
				controller.selectedDomainsDisplay =  controller.selectedDomains.length+" Domain(s) Selected"
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
		
		var baseUrl = "services/service-user/incompleteusers/table?1=1";
		baseUrl += "&domainNames="+domainArray();
		baseUrl += "&categories="+catArray();
		baseUrl +="&loadUnfinished=true";
		if(controller.stage!=""){
			baseUrl += "&stage="+controller.stage;
		}
		controller.playersTable = $dt.builder()
		.column($dt.column('username').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME")))
		.column($dt.linkscolumn("", [{ permission: "player_view", permissionType:"any", title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.players.incomplete.view", {id:data.id, domainName:data.domain.name}) } }]).notSortable())
		// .column($dt.columnWithClass('status.name', 'label label-lg label-default').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.NAME")).notSortable())
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.DOMAIN.NAME")))
		.column($dt.column('firstName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.NAME")))
		.column($dt.column('lastName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.NAME")))
		// .column($dt.column('userApiToken.shortGuid').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.REFERRALCODE")))
		.column($dt.columnformatdate('createdDate').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.CREATEDDATE.NAME")))
		.column($dt.column('gender').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.GENDER.NAME")))
		.options(baseUrl)
		.order([5, 'desc'])
		.build();
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.playersTable.instance)) {
				console.log("tableLoad2");
				baseUrl = "services/service-user/incompleteusers/table?1=1";
				baseUrl += "&domainNames="+domainArray();
				baseUrl += "&categories="+catArray();
				baseUrl += "&loadUnfinished=true";
				if(controller.stage!=""){
					baseUrl += "&stage="+controller.stage;
				}
				controller.playersTable.instance._renderer.options.ajax = baseUrl;
				controller.playersTable.instance.rerender();
			}
		}
		controller.unfinished = function() {
			controller.loadUnfinishedOnly =  !controller.loadUnfinishedOnly;
			controller.tableLoad();
		}
}]);
