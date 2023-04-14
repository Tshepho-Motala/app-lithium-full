'use strict';

angular.module('lithium')
	.controller('ProductTransactionList', ["$translate", "$userService", "$dt", "$state", "$uibModal", "$rootScope",
	function($translate, $userService, $dt, $state, $uibModal, $rootScope) {
		var controller = this;
		
		controller.referenceId = "ProductTransactionList_"+(Math.random()*1000);
		
		controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "PRODUCTS"]);
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
		
		var baseUrl = "services/service-product/product/admin/transaction/table?1=1&domains="+domainArray()+"";
		controller.catalogTable = $dt.builder()
		.column($dt.columnformatdatetime('createdOn').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.TRANSACTIONS.CREATEDON")))
		.column($dt.column('product.guid').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.TRANSACTIONS.PRODUCTGUID")))
		.column($dt.column('product.name').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.TRANSACTIONS.PRODUCTNAME")))
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.TRANSACTIONS.DOMAINNAME")))
		.column($dt.column('user.guid').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.TRANSACTIONS.PLAYER")))
		.column($dt.column('domainMethodName').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.TRANSACTIONS.METHOD")))
		.column($dt.column('cashierTransactionId').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.TRANSACTIONS.CASHIER")))
		.column($dt.linkscolumn("", [{ permission: "products", permissionType: "any", title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewProduct(data) } }]))
		.options(baseUrl)
		.order([0, 'desc'])
		.build();
		
		controller.viewProduct = function(data) {
//			$state.go("dashboard.product.view", {id: data.id});
			console.log(data);
		}
		
		controller.addProduct = function() {
			$state.go("dashboard.product.add");
		}
		
		controller.displayList = function() {
			controller.tableLoad();
		}
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.catalogTable.instance)) {
				baseUrl = "services/service-product/product/admin/transaction/table?1=1";
				baseUrl += "&domains="+domainArray();
				controller.catalogTable.instance._renderer.options.ajax = baseUrl;
				controller.catalogTable.instance.rerender();
			}
		}
}]);
