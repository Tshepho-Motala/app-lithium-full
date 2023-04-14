'use strict'

angular.module('lithium').controller('UserMissionsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService', 'UserRest',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService, userRest) {
		var controller = this;
		
		controller.model = {
			startedDateRangeStart: null,
			startedDateRangeEnd: null
		}
		
		controller.types = [{name: "SEQUENTIAL", value: 1 }, { name: "DATE DRIVEN", value: 2 }];
		controller.legendCollapsed = true;
		controller.selectedType = null;
		controller.active = null;
		controller.current = 1;
		controller.selectedUser = null;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "USERMISSIONS_*"]);
		
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
		
		controller.toggleLegendCollapse = function() {
			controller.legendCollapsed = !controller.legendCollapsed;
		}
		
		controller.resetUserSearch = function() {
			controller.selectedUser = null;
		}
		controller.searchUsers = function(userGuid) {
			var domainAndPlayer = userGuid.split("/");
			if (domainAndPlayer.length !== 2) return null;
			var proceed = false;
			for (var i = 0; i < controller.domains.length; i++) {
				if (domainAndPlayer[0] === controller.domains[i].name) proceed = true;
			}
			if (proceed) {
				return userRest.search(domainAndPlayer[0], domainAndPlayer[1]).then(function(searchResult) {
					return searchResult.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}
			return null;
		};
		
		controller.resetFilter = function(collapse) {
			if (collapse) {
				controller.toggleLegendCollapse();
			}
			controller.selectedType = null;
			controller.active = null;
			controller.current = 1
			controller.selectedUser = null;
			controller.model.startedDateRangeStart = null;
			controller.model.startedDateRangeEnd = null;
			controller.refreshUserMissionsTable();
		}
		
		controller.applyFilter = function(toggle) {
			if (toggle === true) {
				controller.toggleLegendCollapse();
			}
			controller.refreshUserMissionsTable();
		}
		
		controller.fields = [
			{
				className: 'col-md-4 col-xs-12',
				key: 'startedDateRangeStart',
				type: 'datepicker',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Started: Range Start',
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					},
					onChange: function() { controller.fields[1].templateOptions.datepickerOptions.minDate = controller.model.startedDateRangeStart; }
				}
			},
			{
				className: 'col-md-4 col-xs-12',
				key: 'startedDateRangeEnd',
				type: 'datepicker',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Started: Range End',
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					},
					onChange: function() { controller.fields[0].templateOptions.datepickerOptions.maxDate = controller.model.startedDateRangeEnd; }
				}
			}
		];
		
		controller.formatDate = function(date) {
			return $filter('date')(date, 'dd/MM/yyyy');
		}
		
		var baseUrl = 'services/service-promo/backoffice/user-promotions/table';
		
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
						permission: "usermissions_*",
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
		
		.column(
			$dt.labelcolumn(
				'',
				[{lclass: function(data) {
					if(!controller.current && data.isOnCurrentPromotion) {
						return "success"
					}

					return ''
					
				},
				text: function(data) {
					if(!controller.current && data.isOnCurrentPromotion) {
						return $translate.instant('UI_NETWORK_ADMIN.USERMISSIONS.LIST.LABELS.CURRENT')
					}
					return ''
					
				},
				uppercase:true
				}]
			)
		)
		.column($dt.column('active').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.ACTIVE.NAME")))
		.column($dt.column('expired').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.EXPIRED.NAME")))
		.column($dt.columnformatdatetime('startedDisplay').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.STARTED.NAME")))
		.column($dt.columnformatdatetime('completedDisplay').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.COMPLETED.NAME")))
		.column($dt.column('timezone').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.TIMEZONE.NAME")))
		.column($dt.column('percentage').withTitle($translate("UI_NETWORK_ADMIN.USERMISSIONS.FIELDS.PERCENTAGE.NAME")))
		.options(
			{ 
				url: baseUrl,
				type: 'GET',
				data: function(d) { 
					d.domains = controller.commaSeparatedSelectedDomains(),
					d.type = (controller.selectedType != null)? controller.selectedType.value: null,
					d.active = controller.active,
					d.userGuid = (controller.selectedUser != null)? controller.selectedUser.guid: null;
					d.startedDateRangeStart = (controller.model.startedDateRangeStart != null)? controller.formatDate(controller.model.startedDateRangeStart): null,
					d.startedDateRangeEnd = (controller.model.startedDateRangeEnd != null)? controller.formatDate(controller.model.startedDateRangeEnd): null,
					d.current = controller.current;
				} 
			},
			null, 
			dtOptions,
			null
		)
		.build();
		
		controller.refreshUserMissionsTable= function() {
			controller.userMissionsTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.refreshUserMissionsTable();
			}
		});
	}
]);
