'use strict'

angular.module('lithium').controller('InboxListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "NOTIFICATIONS_*"]);
		
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
		
		controller.model = {
			showRead: true,
			showUnread: true
		}
		
		controller.fields = [
			{
				fieldGroup:
				[
					{
						className: 'pull-left',
						type: 'checkbox',
						key: 'showRead',
						templateOptions: {
							label: 'Show Read'
						}
					}, {
						className: 'pull-left',
						type: 'checkbox',
						key: 'showUnread',
						templateOptions: {
							label: 'Show Unread'
						}
					}
				]
			}
		]
		
		var baseUrl = 'services/service-notifications/admin/inbox/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [4, 'desc']);
		controller.inboxTable = $dt.builder()
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.INBOX.FIELDS.DOMAIN.NAME")))
		.column($dt.column('notification.name').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.INBOX.FIELDS.NOTIFICATION.NAME")))
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						permission: "notifications_*",
						permissionType: "any",
						permissionDomain: function(data) {
							return data.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.notifications.inbox.view", { id:data.id });
						}
					}
				]
			)
		)
		.column($dt.column('user.guid').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.INBOX.FIELDS.USER.NAME")))
		.column($dt.columnformatdatetime('createdDate').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.INBOX.FIELDS.CREATEDDATE.NAME")))
		.column($dt.columnformatdatetime('sentDate').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.INBOX.FIELDS.SENTDATE.NAME")))
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.NOTIFICATIONS.INBOX.FIELDS.READ.NAME"),
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
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = controller.commaSeparatedSelectedDomains(), d.showRead = controller.model.showRead, d.showUnread = controller.model.showUnread; } }, null, dtOptions, null)
		.build();
		
		controller.refreshInboxTable= function() {
			controller.inboxTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.model }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.refreshInboxTable();
			}
		}, true);
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.refreshInboxTable();
			}
		});
	}
]);
