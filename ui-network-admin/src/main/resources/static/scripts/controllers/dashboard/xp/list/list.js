'use strict'

angular.module('lithium').controller('XPSchemesListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "XP_SCHEMES_*"]);
		
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) 
					controller.selectedDomains.push(controller.domains[d].name);
			}
			if (controller.selectedDomains.length == controller.domains.length) {
				controller.selectedDomainsDisplay = "Domain";
			} else {
				controller.selectedDomainsDisplay = "Selected (" + controller.selectedDomains.length + ")";
			}
		};
		
		controller.domainSelectAll = function() {
			for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
			controller.domainSelect();
		};
		
		controller.domainSelectAll();
		
		controller.commaSeparatedSelectedDomains = function() {
			var s = '';
			for (var i = 0; i < controller.selectedDomains.length; i++) {
				if (s.length > 0) s += ',';
				s += controller.selectedDomains[i];
			}
			return s;
		}
		
		var baseUrl = 'services/service-xp/admin/scheme/table';
		
		var dtOptions = null; //DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [2, 'desc']);
		controller.xpSchemesTable = $dt.builder()
		.column($dt.column('name').withTitle($translate('UI_NETWORK_ADMIN.XP.FIELDS.NAME.NAME')))
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						permission: "xp_schemes_*",
						permissionType: "any",
						permissionDomain: function(data) {
							return data.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.xp.schemes.view", { id:data.id });
						}
					}
				]
			)
		)
		.column($dt.column('domain.name').withTitle($translate('UI_NETWORK_ADMIN.XP.FIELDS.DOMAIN.NAME')))
		.column($dt.column('description').withTitle($translate('UI_NETWORK_ADMIN.XP.FIELDS.DESCRIPTION.NAME')))
		.column(
			$dt.labelcolumn(
				$translate('UI_NETWORK_ADMIN.XP.FIELDS.STATUS.NAME'),
				[{lclass: function(data) {
					switch (data.status.name) {
						case 'ACTIVE':
							return "success";
						case 'INACTIVE':
							return "warning";
						case 'ARCHIVE': 
							return "primary";
						default:
							return "default";
					}
				},
				text: function(data) {
					return data.status.name;
				},
				uppercase:true
				}]
			)
		)
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = controller.commaSeparatedSelectedDomains(); } }, null, dtOptions, null)
		.build();
		
		controller.refreshXpSchemesTable= function() {
			controller.xpSchemesTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.xpSchemesTable.instance.reloadData(function(){}, false);
			}
		});
	}
]);
