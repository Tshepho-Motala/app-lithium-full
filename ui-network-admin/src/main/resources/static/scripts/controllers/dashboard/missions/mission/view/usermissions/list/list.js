'use strict'

angular.module('lithium').controller('MissionRevisionUserMissionsListController', ['missionRevision', '$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService',
	function(missionRevision, $log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService) {
		var controller = this;
		
		var baseUrl = 'services/service-promo/backoffice/user-promotions/'+missionRevision.id+"/table";
		
		var dtOptions = null; // DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[3, 'desc'], [4, 'desc'], [5, 'asc']]);
		controller.userMissionsTable = $dt.builder()
		.column($dt.column('missionRevision.domain.name').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME")))
		.column($dt.column('user.guid').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.USER.NAME")))
		.column($dt.column('missionRevision.name').withTitle("Name"))
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						permission: "usermissions",
						permissionType: "any",
						permissionDomain: function(data) {
							return data.missionRevision.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.usermissions.view", { id:data.id });
						}
					}
				]
			)
		)
		.column($dt.column('missionRevision.description').withTitle($translate("UI_NETWORK_ADMIN.MISSIONS.FIELDS.DESCRIPTION.NAME")))
		.column($dt.column('active').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.ACTIVE.NAME")))
		.column($dt.column('expired').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.EXPIRED.NAME")))
		.column($dt.columnformatdatetime('startedDisplay').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.STARTED.NAME")))
		.column($dt.columnformatdatetime('completedDisplay').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.COMPLETED.NAME")))
		.column($dt.column('timezone').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.TIMEZONE.NAME")))
		.column($dt.column('percentage').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.PERCENTAGE.NAME")))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
