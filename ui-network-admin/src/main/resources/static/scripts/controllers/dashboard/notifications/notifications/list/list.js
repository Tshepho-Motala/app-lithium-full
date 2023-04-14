'use strict'

angular.module('lithium').controller('NotificationsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "NOTIFICATIONS_*"]);

		controller.limitText = (text, length = 30) => {
			if(angular.isUndefined(text) || typeof text !== 'string') {
				return ''
			}

			if(text.length > length) {
				return text.substring(0, length - 3) + '...';
			}

			return text;
		}
		
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
		
		var baseUrl = 'services/service-notifications/admin/notification/table';
		
		var dtOptions = null; //DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [2, 'desc']);
		controller.notificationsTable = $dt.builder()
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DOMAIN.NAME")))
		.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NAME.NAME")))
		.column($dt.column('displayName').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DISPLAYNAME.NAME")))
		.column(
            $dt.labelcolumn(
                '',
                [{lclass: function(data) {
                        if (data.systemNotification) return 'warning';
                        return '';
                    },
                    text: function(data) {
                        if (data.systemNotification) return 'SYSTEM';
                        return '';
                    },
                    uppercase: true
                }]
            )
        )
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
							return $state.href("dashboard.notifications.notifications.view", { id:data.id });
						}
					}
				]
			)
		)
		.column($dt.column('description').withTitle($translate("UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DESCRIPTION.NAME"))
				.renderWith(function (data, type, row, meta) {
                            return controller.limitText(row.description, 50);
     			})
		)
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = controller.commaSeparatedSelectedDomains(); } }, null, dtOptions, null)
		.build();
		
		controller.refreshNotificationsTable= function() {
			controller.notificationsTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.notificationsTable.instance.reloadData(function(){}, false);
			}
		});
	}
]);
