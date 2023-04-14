'use strict'

angular.module('lithium').controller('SMSQueueController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$rootScope', '$userService', 'rest-domain',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $rootScope, $userService, restDomain) {
		var controller = this;
		
		controller.domains = $userService.playerDomainsWithAnyRole(["SMS_QUEUE_VIEW", "PLAYER_SMS_HISTORY_VIEW"]);
		
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
		
		controller.model = {
			showSent: false,
			dateRangeStart: null,
			dateRangeEnd: null
		}
		
		controller.fields = [
			{
				className: 'col-xs-6',
				key: 'dateRangeStart',
				type: 'datepicker',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Created Date: Start',
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					},
					onChange: function() { controller.fields[1].templateOptions.datepickerOptions.minDate = controller.model.dateRangeStart; }
				}
			},
			{
				className: 'col-xs-6',
				key: 'dateRangeEnd',
				type: 'datepicker',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Created Date: End',
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					},
					onChange: function() { controller.fields[0].templateOptions.datepickerOptions.maxDate = controller.model.dateRangeEnd; }
				}
			},
			{
				className: 'col-xs-12 pull-left',
				type: 'checkbox',
				key: 'showSent',
				templateOptions: {
					label: 'Show Sent SMS\'s'
				}
			},
			{
				className: 'col-xs-12 pull-left',
				type: 'checkbox',
				key: 'showFailed',
				templateOptions: {
					label: 'Show Failed SMS\'s'
				}
			}
		];
		
		controller.formatDate = function(date) {
			return $filter('date')(date, 'dd/MM/yyyy');
		}
		
		var baseUrl = 'services/service-sms/sms/findByDomain/table';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.smsQueueTable = $dt.builder()
		.column($dt.columnformatdatetime('createdDate').withTitle("Created Date"))
		.column($dt.linkscolumn("", [{ permission: "sms_queue_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.sms.queue.view", {id:data.id}) } }]))
		.column($dt.column('user.guid').withTitle("User").withOption('defaultContent', ''))
		.column($dt.columnformatdatetime('sentDate').withTitle("Sent Date"))
		.column($dt.column('from').withTitle("From"))
		.column($dt.column('to').withTitle("To"))
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domainNamesCommaSeparated = controller.selectedDomainsCommaSeperated, d.showSent = controller.model.showSent, d.showFailed = controller.model.showFailed, d.createdDateStart = controller.formatDate(controller.model.dateRangeStart), d.createdDateEnd = controller.formatDate(controller.model.dateRangeEnd) } }, null, dtOptions, null)
		.build();
		
		controller.refreshSMSQueueTable = function() {
			controller.smsQueueTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.model }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.smsQueueTable.instance.reloadData(function(){}, false);
			}
		}, true);
		
		// $scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
		// 	if (newValue != oldValue) {
		// 		controller.smsQueueTable.instance.reloadData(function(){}, false);
		// 	}
		// });

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
				controller.smsQueueTable.instance.rerender(true)
		}

		window.VuePluginRegistry.loadByPage("DomainSelect")
	}
]);
