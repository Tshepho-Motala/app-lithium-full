'use strict'

angular.module('lithium').controller('MissionsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "MISSIONS_*"]);
		
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
		
		var baseUrl = 'services/service-promo/backoffice/promotions/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false);
		// .withOption('order', [[5, 'desc'],[6, 'desc'],[7, 'asc'],[8, 'asc']]);
		controller.missionsTable = $dt.builder()
		.column($dt.column('current.domain.name').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME")))
		.column($dt.column('current.name').withTitle("Name"))
		.column($dt.column('current.description').withTitle("Description"))
		// .column(
		// 	$dt.labelcolumn(
		// 		$translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.TYPE.NAME"),
		// 		[{lclass: function(data) {
		// 			switch (data.current.type) {
		// 				case 1: return "success";
		// 				case 2: return "primary";
		// 				default: return "";
		// 			}
		// 		},
		// 		text: function(data) {
		// 			switch (data.current.type) {
		// 				case 1: return "SEQUENTIAL";
		// 				case 2: return "DATE DRIVEN";
		// 				default: return "";
		// 			}
		// 		},
		// 		uppercase:true
		// 		}]
		// 	)
		// )
		.column($dt.columnformatdate('current.startDate').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.STARTDATE.NAME")))
		.column($dt.columnformatdate('current.endDate').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.ENDDATE.NAME")))
		.column($dt.column('current.sequenceNumber').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.SEQUENCENUMBER.NAME")))
		.column($dt.column('current.xpLevel').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME")))
		.column(
			$dt.linkscolumn(
				"",
				[
					{
						permission: "missions_*",
						permissionType: "any",
						permissionDomain: function(data) {
							return data.current.domain.name;
						},
						title: "GLOBAL.ACTION.VIEW",
						href: function(data) {
							return $state.href("dashboard.missions.mission.view", { id:data.id, missionRevisionId: data.current.id });
						}
					}
				]
			)
		)
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = controller.commaSeparatedSelectedDomains() } }, null, dtOptions, null)
		.build();
		
		controller.refreshMissionsTable= function() {
			controller.missionsTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.refreshMissionsTable();
			}
		});
	}
]);
