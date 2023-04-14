'use strict';

angular.module('lithium')
	.controller('ReportGamesCreate', ["$scope", "notify", "$state", "$rootScope", "$dt", "$translate", "bsLoadingOverlayService", "ReportGamesRest", "$userService", "$uibModal",
	function($scope, notify, $state, $rootScope, $dt, $translate, bsLoadingOverlayService, ReportGamesRest, $userService, $uibModal) {
		let controller = this;
		
		controller.model = { enabled: true, allFiltersApplicable: true, filters: [], actions: [], dayOfWeek: null, dayOfMonth: null, cron: null, prettyCron: null };
		
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
		
		controller.generateCron = function() {
			if (angular.isDefined(controller.model.period) && controller.model.period != null && controller.model.period !== '') {
				let minute = (angular.isDefined(controller.model.minute) && controller.model.minute != null &&
								controller.model.minute !== '')? controller.model.minute: "0";
				let hour = (angular.isDefined(controller.model.hour) && controller.model.hour != null &&
								controller.model.hour !== '')? controller.model.hour: "0";
				let dayOfMonth = (angular.isDefined(controller.model.dayOfMonth) && controller.model.dayOfMonth != null &&
								controller.model.dayOfMonth !== '')? controller.model.dayOfMonth: "*";
				let dayOfWeek = (angular.isDefined(controller.model.dayOfWeek) && controller.model.dayOfWeek != null &&
								controller.model.dayOfWeek !== '')? controller.model.dayOfWeek: "*";
				controller.model.cron = "0 " + minute + " " + hour + " " + dayOfMonth + " * " + dayOfWeek;
				controller.model.prettyCron = prettyCron.toString(controller.model.cron.substring(2));
			} else {
				controller.model.cron = null;
				controller.model.prettyCron = null;
			}
		}
		
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
						label : "",
						required : true,
						description : "",
						valueProp : 'name',
						labelProp : 'name',
						optionsAttr: 'ui-options', "ngOptions": 'ui-options',
						placeholder : '',
						options : []
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DOMAIN.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DOMAIN.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DOMAIN.DESCRIPTION" | translate'
					},
					controller: ['$scope', function($scope) {
						$scope.to.options = $userService.domainsWithRole("REPORT_GAMES");
					}]
				},
				{
					className: "row v-reset-row ",
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
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.NAME.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.NAME.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.NAME.DESCRIPTION" | translate'
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
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DESCRIPTION.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
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
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.ALLFILTERSAPPLICABLE.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.ALLFILTERSAPPLICABLE.DESCRIPTION" | translate'
					}
				},
				{
					className: "section-label",
					template: "<div><strong>{{ 'UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.SECTION_LABELS.PLAYER_DATA_PERIOD' | translate }}</strong></div>"
				},
				{
					className: "row v-reset-row ",
					fieldGroup:
					[
						{
							className: "col-xs-6",
							key : "gameDataPeriod",
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
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.GAMEDATAPERIOD.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.GAMEDATAPERIOD.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.GAMEDATAPERIOD.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-6",
							key: "gameDataPeriodOffset",
							type: "input",
							templateOptions: {
								label: "", description: "", placeholder: ""
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.GAMEDATAPERIODOFFSET.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.GAMEDATAPERIODOFFSET.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.GAMEDATAPERIODOFFSET.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								return (angular.isDefined(scope.model.gameDataPeriod) && scope.model.gameDataPeriod === 5)
							},
							modelOptions: {
								updateOn: 'default change blur', debounce: 0
							},
							validators: {
								pattern: {
									expression: function($viewValue, $modelValue, scope) {
										return /^[0-9]+$/.test($viewValue);
									},
									message: '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.GAMEDATAPERIODOFFSET.PATTERN" | translate'
								}
							}
						},
						{
							className: "col-xs-6",
							key: "compareXperiods",
							type: "input",
							templateOptions: {
								label: "Compare x Periods", description: "", placeholder: ""
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.COMPAREXPERIODS.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.COMPAREXPERIODS.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.COMPAREXPERIODS.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								return (angular.isDefined(scope.model.gameDataPeriod) && scope.model.gameDataPeriod === 5)
							},
							modelOptions: {
								updateOn: 'default change blur', debounce: 0
							},
							validators: {
								pattern: {
									expression: function($viewValue, $modelValue, scope) {
										return /^[0-9]+$/.test($viewValue);
									},
									message: '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.COMPAREXPERIODS.PATTERN" | translate'
								}
							}
						}
					]
				},
				{
					className: "row v-reset-row ",
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
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.ENABLED.NAME" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.ENABLED.DESCRIPTION" | translate'
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
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.PERIOD.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.PERIOD.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.PERIOD.DESCRIPTION" | translate'
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
								placeholder : '',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFWEEK.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFWEEK.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFWEEK.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (!angular.isDefined(scope.model.period) || scope.model.period !== 'Weekly') hide = true;
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
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFMONTH.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFMONTH.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFMONTH.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								let hide = false;
								if (!angular.isDefined(scope.model.period) || scope.model.period !== 'Monthly') hide = true;
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
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.HOUR.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.HOUR.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.HOUR.DESCRIPTION" | translate'
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
								placeholder : '',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.MINUTE.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.MINUTE.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.MINUTE.DESCRIPTION" | translate'
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
				templateUrl: 'scripts/controllers/dashboard/reports/games/report/report-filter.html',
				controller: 'ReportFilterGamesAddModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/games/report/report-filter.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (filter) {
				let exists = false;
				for (let i = 0; i < controller.model.filters.length; i++) {
					console.log(controller.model.filters[i]);
					if (controller.model.filters[i] === filter) {
						exists = true;
					}
				}
				if (!exists) controller.model.filters.push(filter);
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
		
		controller.eligableActions = function() {
			let actions = [{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ELIGIBLE_ACTIONS.SEND_FULL_VIA_EMAIL'), value: "sendFullReportViaEmail" },
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
				templateUrl: 'scripts/controllers/dashboard/reports/games/report/report-action.html',
				controller: 'ReportActionGamesAddModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					actions: function() { return controller.eligableActions() },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/reports/games/report/report-action.js' ]
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
				templateUrl: 'scripts/controllers/dashboard/reports/games/report/report-recipient.html',
				controller: 'ReportRecipientGamesAddModal',
				controllerAs: 'controller',
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
							files: [ 'scripts/controllers/dashboard/reports/games/report/report-recipient.js' ]
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
				templateUrl: 'scripts/controllers/dashboard/reports/games/report/report-email-template.html',
				controller: 'ReportActionGamesEmailTemplateModal',
				controllerAs: 'controller',
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
							files: [ 'scripts/controllers/dashboard/reports/games/report/report-email-template.js' ]
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
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			ReportGamesRest.create(controller.model).then(function(response) {
				if (response._successful) {
					notify.success("UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.CREATION_SUCCESS");
					$state.go("^.report", { reportId:response.id });
				} else {
					notify.error("UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.NOTIFICATION.CREATION_FAIL");
				}
			});
		}
	}
]);
