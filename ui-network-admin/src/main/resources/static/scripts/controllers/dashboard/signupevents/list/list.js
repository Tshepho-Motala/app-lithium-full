'use strict';

angular.module('lithium').controller('SignupEventsList', ["$filter", "$scope", "$translate", "$log", "$dt", "$state", "$rootScope", "DTOptionsBuilder", "$userService", "UserRest",
	function($filter, $scope, $translate, $log, $dt, $state, $rootScope, DTOptionsBuilder, $userService, userRest) {
		var controller = this;

		controller.model = {
			successful: 0,
			signupDateRangeStart: null,
			signupDateRangeEnd: null
		}

		controller.legendCollapsed = true;

		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "SIGNUPEVENTS_VIEW"]);

		controller.fields = [
			{
				className: 'col-md-3 col-xs-12',
				key: 'successful',
				type: 'ui-select-single',
				templateOptions : {
					label: "Successful",
					valueProp: 'value',
					labelProp: 'label',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: [
						{value: 0, label: 'All'},
						{value: 1, label: 'Successful'},
						{value: 2, label: 'Not Successful'}
					]
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.SIGNUPEVENTS.FILTERS.SUCCESSFUL.LABEL" | translate'
				}
			},
			{
				className: 'col-md-3 col-xs-12',
				key: 'signupDateRangeStart',
				type: 'datepicker',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Signup Date: Range Start',
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					},
					onChange: function() { controller.fields[2].templateOptions.datepickerOptions.minDate = controller.model.signupDateRangeStart; }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.SIGNUPEVENTS.FILTERS.SIGNUP_DATE_RANGE_START.LABEL" | translate'
				}
			},
			{
				className: 'col-md-3 col-xs-12',
				key: 'signupDateRangeEnd',
				type: 'datepicker',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Signup Date: Range End',
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					},
					onChange: function() { controller.fields[1].templateOptions.datepickerOptions.maxDate = controller.model.signupDateRangeEnd; }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.SIGNUPEVENTS.FILTERS.SIGNUP_DATE_RANGE_END.LABEL" | translate'
				}
			}
		]

		controller.toggleLegendCollapse = function() {
			controller.legendCollapsed = !controller.legendCollapsed;
		}

		controller.resetFilter = function(collapse) {
			if (collapse) {
				controller.toggleLegendCollapse();
			}
			controller.model.successful = 0;
			controller.selectedUser = undefined;
			controller.selectedEmail = undefined;
			controller.model.signupDateRangeStart = null;
			controller.model.signupDateRangeEnd = null;
			controller.applyFilter(true);
		}

		controller.applyFilter = function(toggle) {
			if (toggle === true) {
				controller.toggleLegendCollapse();
			}
			controller.refreshTable();
		}

		controller.resetUserSearch = function() {
			controller.selectedUser = undefined;
		}

		controller.resetEmailSearch = () => {
			controller.selectedEmail = null;
		}

		controller.searchUsers = function(search) {
			return userRest.searchAllPlayers(search).then(function(searchResult) {
				return searchResult.plain();
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		};

		controller.formatDate = function(date) {
			return $filter('date')(date, 'dd/MM/yyyy');
		}

		controller.isSuccessful = function(successful) {
			if (successful === undefined || successful === null) return 0;
			switch (successful) {
				case 0:
					return null;
				case 1:
					return true;
				case 2:
					return false;
			}
		}

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
		
		var baseUrl = "services/service-user/signupevents/table";
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('bFilter', false).withOption('order', [0, 'desc']);
		controller.table = $dt.builder()
			.column($dt.columnformatdatetime('date').withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.DATE')))
			.column($dt.linkscolumn("", [
					{ 
						permission: "signupevents_view",
						permissionType:"any",
						permissionDomain: function(data) {
							return data.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.signupevents.view", {id:data.id}) 
						} 
					}
				]
			))
			.column($dt.column('ipAddress').withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.IPADDRESS')))
			.column($dt.column('successful').withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.SUCCESSFUL')))
			.column($dt.column('comment').withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.COMMENT')).notSortable())
			.column($dt.column('user.username').withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.USER.USERNAME')))
			.column($dt.column('user.firstName').withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.USER.FIRSTNAME')))
			.column($dt.column(function(data) {
				if (data.user != null)
					if (data.user.lastNamePrefix != undefined && data.user.lastNamePrefix != null)
						return data.user.lastNamePrefix + ' ' + data.user.lastName
					else
						return data.user.lastName;
			}).withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.USER.LASTNAME')))
			.column($dt.column('user.email').withTitle($translate('UI_NETWORK_ADMIN.SIGNUPEVENTS.USER.EMAIL')))
			.options(
				{
					url: baseUrl,
					type: 'GET',
					data: function(d) {
						d.domainNamesCommaSeperated = controller.selectedDomainsCommaSeperated,
						d.successful = controller.isSuccessful(controller.model.successful),
						d.userId = controller.selectedUser ? controller.selectedUser.id: controller.selectedEmail ? controller.selectedEmail.id: null,
						d.signupDateRangeStart = (controller.model.signupDateRangeStart != null)? controller.formatDate(controller.model.signupDateRangeStart): null,
						d.signupDateRangeEnd = (controller.model.signupDateRangeEnd != null)? controller.formatDate(controller.model.signupDateRangeEnd): null
					}
				},
				null,
				dtOptions,
				null
			)
			.build();
		
		controller.refreshTable = function() {
			controller.table.instance.rerender(true)
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
			controller.refreshTable()
		}

		window.VuePluginRegistry.loadByPage("DomainSelect")
	}
]);
