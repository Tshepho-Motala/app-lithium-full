'use strict'

angular.module('lithium').controller('PlayerNotificationsController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', 'user',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, user) {
		var controller = this;
		
		var baseUrl = 'services/service-notifications/admin/inbox/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [4, 'desc']);
		controller.inboxTable = $dt.builder()
		.column($dt.column('domain.name').withTitle("Domain"))
		.column($dt.column('notification.name').withTitle("Notification"))
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						permission: "notification_*",
						permissionType: "any",
						permissionDomain: function(data) {
							return data.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("^.view", { inboxId:data.id });
						}
					}
				]
			)
		)
		.column($dt.column('user.guid').withTitle("User"))
		.column($dt.columnformatdatetime('createdDate').withTitle("Created Date"))
		.column($dt.columnformatdatetime('sentDate').withTitle("Sent Date"))
		.column(
			$dt.labelcolumn(
				'Read',
				[{lclass: function(data) {
					if (data.read) return "success";
					else return "danger";
				},
				text: function(data) {
					if (data.read) return "READ";
					else return "UNREAD";
				},
				uppercase:true
				}]
			)
		)
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = user.domain.name, d.showRead = true, d.showUnread = true, d.userGuid = user.guid; } }, null, dtOptions, null)
		.build();
	}
]);
