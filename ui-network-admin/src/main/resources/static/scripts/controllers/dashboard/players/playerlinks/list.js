'use strict';

angular.module('lithium')
	.controller('PlayerLinksList', ["$translate", "$userService", "$dt", "$scope", "DTOptionsBuilder", "$state", "$rootScope", "bsLoadingOverlayService", "notify", "errors", "UserRest", "EcosysRest",
	function($translate, $userService, $dt, $scope, DTOptionsBuilder, $state, $rootScope, bsLoadingOverlayService, notify, errors, userRest, ecosysRest) {
		var controller = this;
		controller.referenceId = "PlayerLinksList_"+(Math.random()*1000);
        controller.selectedEcosystem =  [];
    
        function domainArray() {
			var str = "";
			angular.forEach(controller.selectedDomains, function(d) {
				str += d.name+",";
			});
			return str;
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

         ecosysRest.ecosystems().then(function (response) {
            controller.ecosystems = response.plain();
        });

        controller.ecosystemSelect = function (ecosystem) {
		    controller.selectedEcosystem = ecosystem.name;
        };

        var baseUrl = "services/service-user/backoffice/user-link/table?";
        baseUrl += "&domainNames="+domainArray();

        var dtOptions = null;
        DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('createdRow', function(row, data, dataIndex) {
            // Recompiling so we can bind Angular directive to the DT
            $compile(angular.element(row).contents())($scope);
        }).withOption('order', [0, 'desc']);

        controller.playerLinksTable = $dt.builder()
        .column($dt.column('primaryUser.username').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.LINKS.LIST.TITLE.PRIMARY")))
        .column($dt.column('secondaryUser.username').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.LINKS.LIST.TITLE.SECONDARY")))
        .column($dt.column('userLinkType.code').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.LINKS.LIST.TITLE.TYPE")))
        .column($dt.column('linkNote').withTitle($translate("UI_NETWORK_ADMIN.PLAYERS.LINKS.LIST.TITLE.COMMENT")))
        .column($dt.column('deleted').withTitle('').renderWith(function (data, type, row, meta) {
            $scope.updateData = { 
                playerLinkId: row.id,
                linkNote: row.linkNote,
                deleted: row.deleted
            }
            var html = '<button-delete ng-click="controller.deleted(' + row.id + ')"></button-delete>'
            // html += '&nbsp;<a  style="cursor: pointer;" ng-click="updatePlayerLink(updateData, true)"><span><i class="fa fa-trash"></i>Delete</span></a>'
            return html;
            // return '<button-delete ng-click="controller.deleted(' + row.id + ')"></button-delete>'

        }))
        .options(baseUrl, dtOptions)
        .build();

        console.log('Selected Ecosystem: ', controller.selectedEcosystem);
        controller.tableLoad = function() {
			if (!angular.isUndefined(controller.playerLinksTable.instance)) {
				baseUrl = "services/service-user/backoffice/user-link/table?";
                baseUrl += "&domainNames="+domainArray();
				if(controller.selectedEcosystem.length){
					baseUrl += "&ecosystemName="+controller.selectedEcosystem;
				}

				
				controller.playerLinksTable.instance._renderer.options.ajax = baseUrl;
				controller.playerLinksTable.instance.rerender();
			}
		}
		controller.unfinished = function() {
			controller.loadUnfinishedOnly =  !controller.loadUnfinishedOnly;
			controller.tableLoad();
		}
		$rootScope.provide.dropDownMenuProvider['domainList']  = () => {
			return controller.domains
		}

		$rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
			controller.selectedDomains = [...data]
			controller.tableLoad();
		}

		$rootScope.provide.dropDownMenuProvider['ecosystemChange'] = (data) => {
			const dataNames = []
			data.forEach(el=> {
				dataNames.push(el.name)
			})
			controller.selectedEcosystem = [...dataNames]
			controller.tableLoad()
		}

		$rootScope.provide.dropDownMenuProvider['ecosystemList'] = () => {
			const ecosystems = controller.ecosystems
			ecosystems.forEach(el=> {
				el.selected = false
			})
			return  ecosystems
		}
		window.VuePluginRegistry.loadByPage("PlayerLinksSearchTopBar")
}]);
