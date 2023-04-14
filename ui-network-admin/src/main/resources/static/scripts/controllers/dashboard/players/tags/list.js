'use strict';

angular.module('lithium')
	.controller('PlayersTagsList', ["$translate", "$userService", "$dt", "$state", "notify", "$uibModal", "$rootScope", "$scope", "$compile", "DTOptionsBuilder", "UserRest",
	function($translate, $userService, $dt, $state, notify, $uibModal, $rootScope, $scope, $compile, DTOptionsBuilder, userRest) {
		var controller = this;
		
		controller.referenceId = "PlayersTagsList_"+(Math.random()*1000);
		
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
		
		var baseUrl = "services/service-user/backoffice/players/tag/table?1=1&domainNames="+domainArray()+"";
		var dtOptions =  DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('createdRow', function(row, data, dataIndex) {
		    // Recompiling so we can bind Angular directive to the DT
		    $compile(angular.element(row).contents())($scope);
		}).withOption('order', [0, 'desc']);
		controller.playersTable = $dt.builder()
		.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.NAME.TITLE")))
		.column($dt.column('description').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DESCRIPTION.TITLE")))
		.column($dt.column('dwhVisible').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DWHVISIBLE.TITLE")))
		.column($dt.linkscolumn("", [{ permission: "player_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.editTag(data) } }]))
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DOMAINNAME.TITLE")))
		.options(baseUrl, null, dtOptions, null)
		.order([0, 'desc'])
		.build();
		
		controller.addTag = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/players/tags/add.html',
				controller: 'TagAdd',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/players/tags/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function() {
				controller.tableLoad();
			});
		}
		
		controller.editTag = function(data) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/players/tags/edit.html',
				controller: 'TagEdit',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					tag: function() {
						return data;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/players/tags/edit.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function() {
				controller.tableLoad();
			});
		}

		controller.displayList = function() {
			controller.tableLoad();
			console.log(controller.selectedDomains);
		}
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.playersTable.instance)) {
				baseUrl = "services/service-user/backoffice/players/tag/table?1=1";
				baseUrl += "&domainNames="+domainArray();
				controller.playersTable.instance._renderer.options.ajax = baseUrl;
//				controller.playersTable.instance.reloadData();
				controller.playersTable.instance.rerender();
			}
		}

		$rootScope.provide.dropDownMenuProvider['domainList']  = () => {
			return controller.domains
		}
		$rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
			const domainNames = []
			data.forEach(el=> {
				domainNames.push(el.name)
			})
			controller.selectedDomains = [...domainNames]
			controller.tableLoad()
		}
		window.VuePluginRegistry.loadByPage("DomainSelect")
}]);
