'use strict'

angular.module('lithium').controller('MailQueueController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$rootScope', '$userService', 'rest-domain', "EmailTemplateRest",
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $rootScope, $userService,  restDomain, emailRest) {
		var controller = this;
		
		controller.domains = $userService.playerDomainsWithAnyRole(["MAIL_QUEUE_VIEW", "PLAYER_MAIL_HISTORY_VIEW"]);

		controller.getMailTemplates = function() {
			const templates = []

			controller.selectedDomains.forEach((domain) => {
				emailRest.getEmailTemplates(domain)
					.then((res) => {
						if(res) {
							templates.push(...res.plain())
						}
					})
			})

			return templates
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
		
		controller.model = {
			showSent: false,
			dateRangeStart: null,
			dateRangeEnd: null,
			mailTemplate: null,
			selectedDomains: [] // only for watcher checking changes
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
					label: 'Show Sent Mail'
				}
			},
			{
				className: 'col-xs-12 pull-left',
				type: 'checkbox',
				key: 'showFailed',
				templateOptions: {
					label: 'Show Failed Mail'
				}
			},
			{
				className: 'col-md-3 col-xs-12',
				key: 'mailTemplate',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Mail template',
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MAILQUEUE.MAIL.MAIL_TEMPLATE" | translate'
				},
				watcher: {
					expression: 'model.selectedDomains',
					listener: async function(field, newValue, oldValue, scope, stopWatching) {
						field.templateOptions.options = await controller.getMailTemplates()
					}
				}
			}
		];
		
		controller.formatDate = function(date) {
			return $filter('date')(date, 'dd/MM/yyyy');
		}
		
		var baseUrl = 'services/service-mail/mail/findByDomain/table';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.mailQueueTable = $dt.builder()
		.column($dt.column('id').notVisible())
		.column($dt.columnformatdatetime('createdDate').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.CREATEDDATE')))
		.column($dt.linkscolumn("", [{ permission: "mail_queue_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.mail.queue.view", {id:data.id}) } }]))
		.column($dt.column(function (data) {
				if (data.author != null) {
					return data.author.guid;
				} else return 'not specified'
			}).withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.AUTHOR_GUID')).notSortable())
		.column($dt.column(function (data) {
				if (data.author != null) {
					if (data.author.lastName == null || data.author.firstName == null) {
						return 'not specified'
					} else {
						return data.author.firstName + ' ' + data.author.lastName;
					}
				} else return 'not specified';
			}).withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.AUTHOR_FULL_NAME')).notSortable())
		.column($dt.column('user.guid').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.USERINFO.USER')).withOption('defaultContent', ''))
		.column($dt.columnformatdatetime('sentDate').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.SENTDATE')))
		.column($dt.column('from').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.FROM')).notSortable())
		.column($dt.column('to').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.TO')).notSortable())
		.column($dt.column('bcc').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.BCC')).notSortable())
		.column($dt.column('subject').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.SUBJECT')).notSortable())
		.column($dt.column(function (data) {
				return (data.template == null ? "" : data.template.name);
			}).withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.TEMPLATE')).notSortable())
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domainNamesCommaSeparated = controller.selectedDomainsCommaSeperated, d.showSent = controller.model.showSent, d.showFailed = controller.model.showFailed, d.createdDateStart = controller.formatDate(controller.model.dateRangeStart), d.createdDateEnd = controller.formatDate(controller.model.dateRangeEnd), d.mailTemplate = controller.model.mailTemplate } }, null, dtOptions, null)
		.nosearch()
		.build();
		
		controller.refreshMailQueueTable = function() {
			controller.mailQueueTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.model }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.mailQueueTable.instance.reloadData(function(){}, false);
			}
		}, true);


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
			controller.model.selectedDomains = domainNames
			controller.selectedDomainsCommaSeperated = domainNames.join(',')
			controller.mailQueueTable.instance.rerender(true)
		}

		window.VuePluginRegistry.loadByPage("DomainSelect")
	}
]);
