'use strict'

angular.module('lithium').controller('MissionRevisionsListController', ['mission', '$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope',
	function(mission, $log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-promo/backoffice/promotion/'+mission.id+'/revisions';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[4, 'desc'],[5, 'asc'],[6, 'asc']]);
		controller.revisionsTable = $dt.builder()
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME")))
	
		.column(
			$dt.labelcolumn(
				"",
				[{lclass: function(data) {
					if (data.id === mission.current.id) return "success";
					if (mission.edit && data.id === mission.edit.id) return "primary";
					return "";
				},
				text: function(data) {
					if (data.id === mission.current.id) return "CURRENT"
					if (mission.edit && data.id === mission.edit.id) return "EDIT";
					return "";
				},
				uppercase:true
				}]
			)
		)
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						permission: "missions",
						permissionType: "any",
						permissionDomain: function(data) {
							return data.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.missions.mission.view", { id:mission.id, missionRevisionId:data.id });
						}
					}
				]
			)
		)
		.column($dt.columnformatdate('startDate').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.STARTDATE.NAME")))
		.column($dt.columnformatdate('endDate').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.ENDDATE.NAME")))
		.column($dt.column('sequenceNumber').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.SEQUENCENUMBER.NAME")))
		.column($dt.column('xpLevel').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME")))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
