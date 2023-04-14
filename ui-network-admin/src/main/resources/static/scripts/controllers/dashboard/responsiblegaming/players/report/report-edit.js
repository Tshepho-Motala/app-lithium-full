'use strict'

angular.module('lithium').controller('ReportPlayersEdit', ['$scope', '$state', '$userService', 'bsLoadingOverlayService', '$uibModal', 'notify', 'report', 'ReportPlayersRest','$translate',
	function($scope, $state, $userService, bsLoadingOverlayService, $uibModal, notify, report, ReportPlayersRest, $translate) {
		let controller = this;
		
		controller.report = report;
		
		controller.model = { actions: [], filters: [] };
		
		controller.hoursOfDay = [];
		controller.minutesOfHour = [];
		controller.daysOfMonth = [];
		
		controller.setTimeIntervalValues = function() {
			controller.hoursOfDay = [];
			controller.minutesOfHour = [];
			controller.daysOfMonth = [];
			for (let i = 0; i <= 59; i++) {
				if (i <= 23) {
					controller.hoursOfDay.push({ name: i, value: i });
				}
				controller.minutesOfHour.push({ name: i, value: i });
				if (i <= 27) {
					controller.daysOfMonth.push({ name: i+1, value: i+1 });
				}
			}
		}
		
		controller.setTimeIntervalValues();
		
		controller.init = function() {
			controller.model.domainName = report.domainName;
			controller.model.name = report.edit.name;
			controller.model.description = report.edit.description;
			controller.model.allFiltersApplicable = report.edit.allFiltersApplicable;
			controller.model.playerDataPeriod = report.edit.granularity;
			controller.model.playerDataPeriodOffset = report.edit.granularityOffset;
			controller.model.enabled = report.enabled;
			if (angular.isDefined(report.edit.cron) && report.edit.cron != null) {
				let cronBreakDown = report.edit.cron.split(" ");
				let period = null;
				if (cronBreakDown[3] === '*' && cronBreakDown[5] === '*') {
					period = 'Daily';
				} else if (cronBreakDown[3] !== '*' && cronBreakDown[5] === '*') {
					period = 'Monthly';
				} else if (cronBreakDown[3] === '*' && cronBreakDown[5] !== '*') {
					period = 'Weekly';
				}
				controller.model.period = period;
				controller.model.dayOfMonth = (cronBreakDown[3] !== '*')? parseInt(cronBreakDown[3]): null;
				controller.model.dayOfWeek = (cronBreakDown[5] !== '*')? parseInt(cronBreakDown[5]): null;
				controller.model.hour = parseInt(cronBreakDown[2]);
				controller.model.minute = parseInt(cronBreakDown[1]);
				controller.model.cron = report.edit.cron;
				controller.model.prettyCron = prettyCron.toString(report.edit.cron.substring(2));
				controller.model.scheduleType = 3;
			}
			if (angular.isDefined(report.edit.chosenDateString) && report.edit.chosenDateString != null) {
				controller.model.chosenDate = report.edit.chosenDateString;
				controller.model.scheduleType = 2;
			}
			if (angular.isDefined(report.edit.chosenTimeString) && report.edit.chosenTimeString != null) {
				controller.model.chosenTime = report.edit.chosenTimeString;
				controller.model.scheduleType = 2;
			}
			if (!angular.isDefined(controller.model.scheduleType)) controller.model.scheduleType = 1;
			bsLoadingOverlayService.start({ referenceId: 'player-report-filters' });
			ReportPlayersRest.getFilters(report.id, true).then(function(result) {
				controller.model.filters = result;
				bsLoadingOverlayService.stop({ referenceId: 'player-report-filters' });
			});
			bsLoadingOverlayService.start({ referenceId: 'player-report-actions' });
			ReportPlayersRest.getActions(report.id, true).then(function(result) {
				for (let i = 0; i < result.length; i++) {
					let action = result[i];
					if (action.actionType === 'sendFullReportViaEmail') {
						action.recipients = controller.getReportActionLabelValuesByLabel(action, 'reportFullRecipientEmail');
						action.emailTemplate = controller.getReportActionLabelValuesByLabel(action, 'reportFullEmailTemplate')[0];
					} else if (action.actionType === 'sendReportStatsViaEmail') {
						action.recipients = controller.getReportActionLabelValuesByLabel(action, 'reportStatsRecipientEmail');
						action.emailTemplate = controller.getReportActionLabelValuesByLabel(action, 'reportStatsEmailTemplate')[0];
					} else if (action.actionType === 'sendEmailToPlayer') {
						action.recipients = [];
						action.emailTemplate = controller.getReportActionLabelValuesByLabel(action, 'reportPlayerEmailTemplate')[0];
					} else if (action.actionType === 'sendSMSToPlayer') {
						action.recipients = [];
						action.smsTemplate = controller.getReportActionLabelValuesByLabel(action, 'reportPlayerSMSTemplate')[0];
					} else if (action.actionType === 'sendNotificationToPlayer') {
						action.recipients = [];
						action.notificationName = controller.getReportActionLabelValuesByLabel(action, 'reportPlayerNotificationName')[0];
					}
					controller.model.actions.push(action);
				}
				controller.model.actions = result;
				bsLoadingOverlayService.stop({ referenceId: 'player-report-actions' });
			});
		}
		
		controller.getReportActionLabelValuesByLabel = function(action, label) {
			let reportActionLabelValues = [];
			for (let i = 0; i < action.labelValueList.length; i++) {
				if (action.labelValueList[i].labelValue.label.name !== label) continue;
				reportActionLabelValues.push(action.labelValueList[i].labelValue.value);
			}
			return reportActionLabelValues;
		}
		
		controller.init();
		
		controller.generateCron = function() {
			if (angular.isDefined(controller.model.period) && controller.model.period != null && controller.model.period !== '') {
				const minute = (angular.isDefined(controller.model.minute) && controller.model.minute != null &&
								controller.model.minute !== '')? controller.model.minute: "0";
				const hour = (angular.isDefined(controller.model.hour) && controller.model.hour != null &&
								controller.model.hour !== '')? controller.model.hour: "0";
				const dayOfMonth = (angular.isDefined(controller.model.dayOfMonth) && controller.model.dayOfMonth != null &&
								controller.model.dayOfMonth !== '')? controller.model.dayOfMonth: "*";
				const dayOfWeek = (angular.isDefined(controller.model.dayOfWeek) && controller.model.dayOfWeek != null &&
								controller.model.dayOfWeek !== '')? controller.model.dayOfWeek: "*";
				controller.model.cron = "0 " + minute + " " + hour + " " + dayOfMonth + " * " + dayOfWeek;
				controller.model.prettyCron = prettyCron.toString(controller.model.cron.substring(2));
			} else {
				controller.model.cron = null;
				controller.model.prettyCron = null;
			}
		}
		
		$scope.$watch('controller.model.scheduleType', function (newValue, oldValue) {
			if (newValue !== oldValue) {
				if (newValue !== 3) {
					if (angular.isDefined(controller.model.cron)) controller.model.cron = null;
				} else {
					if (angular.isDefined(controller.model.chosenDate)) controller.model.chosenDate = null;
					if (angular.isDefined(controller.model.chosenTime)) controller.model.chosenTime = null;
				}
			}
		}, true);
		
		$scope.$watch('[controller.model.minute, controller.model.hour, controller.model.dayOfMonth, controller.model.dayOfWeek, controller.model.period]', function (newValue, oldValue) {
			if (newValue !== oldValue) {
				controller.generateCron();
			}
		}, true);
		
		controller.fields = 
			[
				{
					className : 'col-xs-12',
					key : "domainName",
					type : "ui-select-single",
					templateOptions : {
						disabled: true,
						label : "",
						required : true,
						description : "",
						valueProp : 'name',
						labelProp : 'name',
						optionsAttr: 'ui-options', "ngOptions": 'ui-options',
						placeholder : 'Select Player Report Domain',
						options : []
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DOMAIN.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DOMAIN.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DOMAIN.DESCRIPTION" | translate'
					},
					controller: ['$scope', function($scope) {
						$scope.to.options = $userService.domainsWithRole("REPORT_PLAYERS");
					}]
				},
				{
					className: "row",
					fieldGroup: [
						{
							className : 'col-xs-12',
							key: "name",
							type: "input",
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: true
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NAME.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NAME.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NAME.DESCRIPTION" | translate'
							}
						},
						{
							className : 'col-xs-12',
							key: "description",
							type: "input",
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: true
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DESCRIPTION.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
							}
						}
					]
				},
				{
					type: 'checkbox',
					key: 'allFiltersApplicable',
					templateOptions: {
						label: '', description: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ALLFILTERSAPPLICABLE.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ALLFILTERSAPPLICABLE.DESCRIPTION" | translate'
					}
				},
				{
					className: "section-label",
					template: "<div><strong>Period of Player Data</strong></div>"
				},
				{
					className: "row",
					fieldGroup:
					[
						{
							className: "col-xs-6",
							key : "playerDataPeriod",
							type : "ui-select-single",
							templateOptions : {
								label : "",
								required : true,
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : '',
								options : [
									{ name: $translate.instant('GLOBAL.GRANULARITIES.3'), value: 3  },
									{ name: $translate.instant('GLOBAL.GRANULARITIES.2'), value: 2 },
									{ name: $translate.instant('GLOBAL.GRANULARITIES.4'), value: 4},
									{ name: $translate.instant('GLOBAL.GRANULARITIES.1'), value: 1 },
									{ name: $translate.instant('GLOBAL.GRANULARITIES.5'), value: 5 }
								]
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYERDATAPERIOD.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYERDATAPERIOD.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYERDATAPERIOD.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-6",
							key: "playerDataPeriodOffset",
							type: "input",
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: true
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYERDATAPERIODOFFSET.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYERDATAPERIODOFFSET.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYERDATAPERIODOFFSET.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								return (angular.isDefined(scope.model.playerDataPeriod) && scope.model.playerDataPeriod === 5)
							},
							modelOptions: {
								updateOn: 'default change blur', debounce: 0
							},
							validators: {
								pattern: {
									expression: function($viewValue, $modelValue, scope) {
										return /^[0-9]+$/.test($viewValue);
									},
									message: '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYERDATAPERIODOFFSET.PATTERN" | translate'
								}
							}
						}
					]
				},
				{
					className: "row",
					fieldGroup: 
					[
						{
							className: "section-label",
							template: "<div><strong>{{ 'GLOBAL.SCHEDULER.TITLE' | translate }}</strong></div>"
						},
						{
							type: 'checkbox',
							key: 'enabled',
							templateOptions: {
								label: '', description: ''
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ENABLED.NAME" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ENABLED.DESCRIPTION" | translate'
							}
						},
						{
							key: "scheduleType",
							type: "uib-btn-radio",
							templateOptions : {
								label : "",
								required : true,
								btnclass: 'default',
								showicons: true,
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : '',
								options: [
									{
										name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.IMMEDIATE'),
										value: 1
									},
									{
										name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.DATE_AND_TIME'),
										value: 2
									},
									{
										name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.RECURRING'),
										value: 3
									}
								]
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.SCHEDULE_TYPE" | translate',
							}
						},
						{
							className: "col-xs-12",
							key : "chosenDate",
							type : "datepicker",
							templateOptions : {
								label : "",
								required : true,
								description : "",
								placeholder : '',
								datepickerOptions: {
									format: 'dd/MM/yyyy Z'
								}
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.CHOOSE_DATE" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.CHOOSE_DATE" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (angular.isDefined(scope.model.scheduleType) && scope.model.scheduleType !== 2) hide = true;
								if (hide === true) scope.model.chosenDate = null;
								return hide;
							}
						},
						{
							className: "col-xs-12",
							key : "chosenTime",
							type : "timepicker",
							templateOptions : {
								label : "",
								required : true,
								description : "",
								placeholder : '',
								datepickerOptions: {
									showMeridian: false
								}
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.CHOOSE_TIME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.CHOOSE_TIME" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (angular.isDefined(scope.model.scheduleType) && scope.model.scheduleType !== 2) hide = true;
								if (hide === true) scope.model.chosenDate = null;
								return hide;
							}
						},
						{
							className: "col-xs-12",
							key : "period",
							type : "ui-select-single",
							templateOptions : {
								label : "",
								required : true,
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : '',
								options : [
									{ name: $translate.instant('GLOBAL.GRANULARITY.DAILY'), value: "Daily" },
									{ name: $translate.instant('GLOBAL.GRANULARITY.WEEKLY'), value: "Weekly" },
									{ name: $translate.instant('GLOBAL.GRANULARITY.MONTHLY'), value: "Monthly"}
								]
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PERIOD.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PERIOD.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PERIOD.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (angular.isDefined(scope.model.scheduleType) && scope.model.scheduleType !== 3) hide = true;
								if (hide === true) scope.model.period = null;
								return hide;
							}
						},
						{
							className : 'col-xs-12',
							key : "dayOfWeek",
							type : "ui-select-single",
							templateOptions : {
								label : "",
								required : true,
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select Day of Week',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DAYOFWEEK.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DAYOFWEEK.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DAYOFWEEK.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (!angular.isDefined(scope.model.period) || scope.model.period !== 'Weekly') hide = true;
								if (angular.isDefined(scope.model.scheduleType) && scope.model.scheduleType !== 3) hide = true;
								if (hide) scope.model.dayOfWeek = null;
								return hide;
							},
							controller: ['$scope', function($scope) {
								$scope.to.options = [
									{name: $translate.instant('GLOBAL.DAYS.1'), value: 1},
									{name: $translate.instant('GLOBAL.DAYS.2'), value: 2},
									{name: $translate.instant('GLOBAL.DAYS.3'), value: 3},
									{name: $translate.instant('GLOBAL.DAYS.4'), value: 4},
									{name: $translate.instant('GLOBAL.DAYS.5'), value: 5},
									{name: $translate.instant('GLOBAL.DAYS.6'), value: 6},
									{name: $translate.instant('GLOBAL.DAYS.7'), value: 7}
								];
							}]
						},
						{
							className : 'col-xs-12',
							key : "dayOfMonth",
							type : "ui-select-single",
							templateOptions : {
								label : "",
								required : true,
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : '',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DAYOFMONTH.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DAYOFMONTH.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.DAYOFMONTH.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (!angular.isDefined(scope.model.period) || scope.model.period !== 'Monthly') hide = true;
								if (angular.isDefined(scope.model.scheduleType) && scope.model.scheduleType !== 3) hide = true;
								if (hide) scope.model.dayOfMonth = null;
								return hide;
							},
							controller: ['$scope', function($scope) {
								$scope.to.options = controller.daysOfMonth;
							}]
						},
						{
							className: "col-xs-6",
							key: "hour",
							type: "ui-select-single",
							templateOptions: {
								label : "",
								required : true,
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : '',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.HOUR.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.HOUR.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.HOUR.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (angular.isDefined(scope.model.scheduleType) && scope.model.scheduleType !== 3) hide = true;
								if (hide) scope.model.hour = null;
								return hide;
							},
							controller: ['$scope', function($scope) {
								$scope.to.options = controller.hoursOfDay;
							}]
						},
						{
							className: "col-xs-6",
							key: "minute",
							type: "ui-select-single",
							templateOptions: {
								label : "",
								required : true,
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select the Minute',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.MINUTE.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.MINUTE.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.MINUTE.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (angular.isDefined(scope.model.scheduleType) && scope.model.scheduleType !== 3) hide = true;
								if (hide) scope.model.minute = null;
								return hide;
							},
							controller: ['$scope', function($scope) {
								$scope.to.options = controller.minutesOfHour;
							}]
						}
					]
				},
			];
		
		controller.addFilterModal = function() {
			let modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/players/report/report-filter.html',
				controller: 'ReportFilterAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/players/report/report-filter.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (filter) {
				controller.model.filters.push(filter);
			});
		}
		
		controller.removeFilter = function(filter) {
			for (let i = 0; i < controller.model.filters.length; i++) {
				if (controller.model.filters[i] === filter) {
					controller.model.filters.splice(i, 1);
					break;
				}
			}
		}

		controller.eligibleActions = function() {
			let actions = [{ name:$translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ELIGIBLE_ACTIONS.SEND_FULL_VIA_EMAIL'), value: "sendFullReportViaEmail" },
				{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ELIGIBLE_ACTIONS.SEND_STATS_VIA_EMAIL'), value: "sendReportStatsViaEmail" },
				{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ELIGIBLE_ACTIONS.SEND_EMAIL_TO_PLAYER'), value: "sendEmailToPlayer"},
				{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ELIGIBLE_ACTIONS.SEND_SMS_TO_PLAYER'), value: "sendSMSToPlayer"},
				{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ELIGIBLE_ACTIONS.SEND_NOTIFICATION_TO_PLAYER'), value: "sendNotificationToPlayer"},
				{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ELIGIBLE_ACTIONS.CHECK_STATUS'), value: "checkGamstopStatus"}];
			for (let i = 0; i < controller.model.actions.length; i++) {
				for (let k = 0; k < actions.length; k++) {
					if (controller.model.actions[i].actionType === actions[k].value) {
						actions.splice(k, 1);
					}
				}
			}
			return actions;
		}
		
		controller.addActionModal = function() {
			let modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/players/report/report-action.html',
				controller: 'ReportActionAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					actions: function() { return controller.eligibleActions() },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/players/report/report-action.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (action) {
				action.recipients = [];
				action.emailTemplate = "";
				controller.model.actions.push(action);
			});
		}
		
		controller.removeAction = function(action) {
			for (let i = 0; i < controller.model.actions.length; i++) {
				if (controller.model.actions[i] === action) {
					controller.model.actions.splice(i, 1);
					break;
				}
			}
		}
		
		controller.reportRecipientAddModal = function(actionType) {
			let modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/players/report/report-recipient.html',
				controller: 'ReportRecipientAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					recipients: function() {
						for (let i = 0; i < controller.model.actions.length; i++) {
							if (controller.model.actions[i].actionType === actionType) {
								return controller.model.actions[i].recipients;
							}
						}
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/players/report/report-recipient.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (recipients) {
				for (let i = 0; i < controller.model.actions.length; i++) {
					if (controller.model.actions[i].actionType === actionType) {
						controller.model.actions[i].recipients = recipients;
						break;
					}
				}
			});
		}
		
		controller.reportActionEmailTemplateModal = function(actionType) {
			let modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/players/report/report-email-template.html',
				controller: 'ReportActionEmailTemplateModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					emailTemplate: function() {
						for (let i = 0; i < controller.model.actions.length; i++) {
							if (controller.model.actions[i].actionType === actionType) {
								return controller.model.actions[i].emailTemplate;
							}
						}
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/players/report/report-email-template.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (emailTemplate) {
				for (let i = 0; i < controller.model.actions.length; i++) {
					if (controller.model.actions[i].actionType === actionType) {
						controller.model.actions[i].emailTemplate = emailTemplate;
						break;
					}
				}
			});
		}
		
		controller.reportActionSMSTemplateModal = function(actionType) {
			let modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/players/report/report-sms-template.html',
				controller: 'ReportActionSMSTemplateModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					smsTemplate: function() {
						for (let i = 0; i < controller.model.actions.length; i++) {
							if (controller.model.actions[i].actionType === actionType) {
								return controller.model.actions[i].smsTemplate;
							}
						}
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/players/report/report-sms-template.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (smsTemplate) {
				for (let i = 0; i < controller.model.actions.length; i++) {
					if (controller.model.actions[i].actionType === actionType) {
						controller.model.actions[i].smsTemplate = smsTemplate;
						break;
					}
				}
			});
		}
		
		controller.reportActionNotificationNameModal = function(actionType) {
			let modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/players/report/report-notification-name.html',
				controller: 'ReportActionNotificationNameModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					notificationName: function() {
						for (let i = 0; i < controller.model.actions.length; i++) {
							if (controller.model.actions[i].actionType === actionType) {
								return controller.model.actions[i].notificationName;
							}
						}
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/players/report/report-notification-name.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (notificationName) {
				for (let i = 0; i < controller.model.actions.length; i++) {
					if (controller.model.actions[i].actionType === actionType) {
						controller.model.actions[i].notificationName = notificationName;
						break;
					}
				}
			});
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			ReportPlayersRest.editPost(report.id, 'commit', controller.model).then(function(response) {
				if (response._successful) {
					notify.success('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.EDIT_SUCCESS');
					$state.go("^.report", { reportId:response.id });
				} else {
					notify.error("UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.EDIT_FAIL");
				}
			});
		}
		
		controller.onContinue = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			ReportPlayersRest.editPost(report.id, 'continue', controller.model).then(function(response) {
				if (response._successful) {
					notify.success('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.EDIT_SUCCESS');
					$state.go("^.report", { reportId:response.id });
				} else {
					notify.error("UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.EDIT_CONTINUE_FAIL");
				}
			});
		}
		
		controller.onCancel = function() {
			ReportPlayersRest.editPost(report.id, 'cancel', controller.model).then(function(response) {
				if (response._successful) {
					notify.success('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.EDIT_CANCEL_SUCCESS');
					$state.go("^.report", { reportId:response.id });
				} else {
					notify.error("UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.EDIT_CANCEL_FAIL");
				}
			});
		}
	}
]);
