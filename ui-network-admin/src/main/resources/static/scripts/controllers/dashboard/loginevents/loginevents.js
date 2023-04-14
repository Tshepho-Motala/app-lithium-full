'use strict'

angular.module('lithium').controller('LoginEventsController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$scope', '$state', '$rootScope', '$userService', 'rest-domain',
	function($log,  $translate, $dt, DTOptionsBuilder, $filter, $scope, $state, $rootScope, $userService, restDomain) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "LOGINEVENTS_VIEW"]);
		
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			controller.selectedDomainsCommaSeperated = '';
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) {
					controller.selectedDomains.push(controller.domains[d].name);
					if (controller.selectedDomainsCommaSeperated.length > 0) {
						controller.selectedDomainsCommaSeperated += ",";
					}
					controller.selectedDomainsCommaSeperated += controller.domains[d].name;
				}
			}
			if (controller.selectedDomains.length == controller.domains.length) {
				controller.selectedDomainsDisplay = "All Domains";
			} else {
				controller.selectedDomainsDisplay = "Selected (" + controller.selectedDomains.length + ")";
			}
		};
		
		controller.domainSelectAll = function() {
			for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
			controller.domainSelect();
		};
		
		controller.domainSelectAll();
		
		var baseUrl = 'services/service-user/loginevents/table';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']).withOption('bFilter', false);
		controller.loginEventsTable = $dt.builder()
		
//		.column($dt.column('id').withTitle('#'))
		.column($dt.columnformatdatetime('date').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.DATE')))
		.column($dt.columnformatcountryflag('countryCode').withClass("text-center").withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.COUNTRY')))
		.column($dt.column('ipAddress').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.IPADDRESS')))
		.column($dt.column('successful').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.SUCCESSFUL')))
		.column($dt.column('errorCode').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.ERROR_CODE')))
		.column($dt.column('internal').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.INTERNAL')))
//		.column($dt.column('playerEvent').withTitle('Player'))
		.column($dt.column('user.username').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.USERNAME')))
		.column($dt.column('domain.name').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.DOMAIN')))
		.column($dt.column('providerName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.PROVIDER')))
		.column($dt.column('providerAuthClient').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.AUTHCLIENT')))
		.column($dt.linkscolumn("", [{
			permission: function(data) {
				if (data.playerEvent) {
					return "player_view";
				} else {
					return "domain_*,user_*";
				}
			},
			permissionType:"all",
			permissionDomain: function(data) {
				if (data.user !== null) {
					if ((data.user) && (data.user.domain)) return data.user.domain.name;
				}
				if (!data.domain) return "";
				return data.domain.name;
			},
			condition: function(data) {
				if ((data.user) && (data.user.id)) return true;
				return false;
			},
			title: "GLOBAL.ACTION.OPEN",
			target: "_blank",
			href: function(data) {
				if (data.user !== null) {
					if ((data.user) && (data.user.id)) {
						if (data.playerEvent) {
							return $state.href('dashboard.players.guidredirect', {domainName:data.user.guid.split('/')[0], usernameOrId: data.user.guid.split('/')[1]});
						} else {
							return $state.href('dashboard.domains.domain.users.user', {id:data.user.id, domainName:data.user.domain.name});
						}
					}
				}
				return $state.href(".");
			}
		}]))
		.column($dt.column('user.firstName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.FIRSTNAME')))
		.column($dt.column('user.lastName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.LASTNAME')))
		.column($dt.column('comment').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.COMMENT')))
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domainNamesCommaSeperated = controller.selectedDomainsCommaSeperated } }, null, dtOptions, null)
		.build();
		
		controller.refreshLoginEventsTable = function() {
			controller.loginEventsTable.instance.rerender(true)
		};


		// Domain select
		$rootScope.provide.dropDownMenuProvider['domainList']  = () => {
			return controller.domains
		}
		$rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
			const domainNames = []
			data.forEach(el=> {
				domainNames.push(el.name)
			})
			controller.selectedDomains = domainNames
			controller.selectedDomainsCommaSeperated = domainNames.join(',')
			controller.refreshLoginEventsTable()
		}

		window.VuePluginRegistry.loadByPage("DomainSelect")
	}
]);
