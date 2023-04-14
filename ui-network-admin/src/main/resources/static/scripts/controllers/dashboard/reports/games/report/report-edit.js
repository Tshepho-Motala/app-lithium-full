'use strict'

angular.module('lithium').controller('ReportGamesEdit', ['$scope', '$state', '$userService', 'bsLoadingOverlayService', '$uibModal', 'notify', 'report', 'ReportGamesRest',
	function($scope, $state, $userService, bsLoadingOverlayService, $uibModal, notify, report, ReportGamesRest) {
		var controller = this;
		
		controller.report = report;
		
		controller.model = { actions: [], filters: [] };
		
		controller.hoursOfDay = [];
		controller.minutesOfHour = [];
		controller.daysOfMonth = [];
		
		controller.setTimeIntervalValues = function() {
			controller.hoursOfDay = [];
			controller.minutesOfHour = [];
			controller.daysOfMonth = [];
			for (var i = 0; i <= 59; i++) {
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
			controller.model.gameDataPeriod = report.edit.granularity;
			controller.model.gameDataPeriodOffset = report.edit.granularityOffset;
			controller.model.compareXperiods = report.edit.compareXperiods;
			controller.model.enabled = report.enabled;
			var cronBreakDown = report.edit.cron.split(" ");
			var period = null;
			if (cronBreakDown[3] === '*' && cronBreakDown[5] === '*') {
				period = 'Daily';
			} else if (cronBreakDown[3] != '*' && cronBreakDown[5] === '*') {
				period = 'Monthly';
			} else if (cronBreakDown[3] == '*' && cronBreakDown[5] != '*') {
				period = 'Weekly';
			}
			controller.model.period = period;
			controller.model.dayOfMonth = (cronBreakDown[3] != '*')? parseInt(cronBreakDown[3]): null;
			controller.model.dayOfWeek = (cronBreakDown[5] != '*')? parseInt(cronBreakDown[5]): null;
			controller.model.hour = parseInt(cronBreakDown[2]);
			controller.model.minute = parseInt(cronBreakDown[1]);
			controller.model.cron = report.edit.cron;
			controller.model.prettyCron = prettyCron.toString(report.edit.cron.substring(2));
			bsLoadingOverlayService.start({ referenceId: 'game-report-filters' });
			ReportGamesRest.getFilters(report.id, true).then(function(result) {
				controller.model.filters = result;
				bsLoadingOverlayService.stop({ referenceId: 'game-report-filters' });
			});
			bsLoadingOverlayService.start({ referenceId: 'game-report-actions' });
			ReportGamesRest.getActions(report.id, true).then(function(result) {
				for (var i = 0; i < result.length; i++) {
					var action = result[i];
					if (action.actionType === 'sendFullReportViaEmail') {
						action.recipients = controller.getReportActionLabelValuesByLabel(action, 'reportFullRecipientEmail');
						action.emailTemplate = controller.getReportActionLabelValuesByLabel(action, 'reportFullEmailTemplate')[0];
					} else if (action.actionType === 'sendReportStatsViaEmail') {
						action.recipients = controller.getReportActionLabelValuesByLabel(action, 'reportStatsRecipientEmail');
						action.emailTemplate = controller.getReportActionLabelValuesByLabel(action, 'reportStatsEmailTemplate')[0];
					}
					controller.model.actions.push(action);
				}
				controller.model.actions = result;
				bsLoadingOverlayService.stop({ referenceId: 'game-report-actions' });
			});
		}
		
		controller.getReportActionLabelValuesByLabel = function(action, label) {
			var reportActionLabelValues = [];
			for (var i = 0; i < action.labelValueList.length; i++) {
				if (action.labelValueList[i].labelValue.label.name != label) continue;
				reportActionLabelValues.push(action.labelValueList[i].labelValue.value);
			}
			return reportActionLabelValues;
		}
		
		controller.init();
		
		controller.generateCron = function() {
			if (angular.isDefined(controller.model.period) && controller.model.period != null && controller.model.period != '') {
				var minute = (angular.isDefined(controller.model.minute) && controller.model.minute != null &&
								controller.model.minute != '')? controller.model.minute: "0";
				var hour = (angular.isDefined(controller.model.hour) && controller.model.hour != null &&
								controller.model.hour != '')? controller.model.hour: "0";
				var dayOfMonth = (angular.isDefined(controller.model.dayOfMonth) && controller.model.dayOfMonth != null &&
								controller.model.dayOfMonth != '')? controller.model.dayOfMonth: "*";
				var dayOfWeek = (angular.isDefined(controller.model.dayOfWeek) && controller.model.dayOfWeek != null &&
								controller.model.dayOfWeek != '')? controller.model.dayOfWeek: "*";
				controller.model.cron = "0 " + minute + " " + hour + " " + dayOfMonth + " * " + dayOfWeek;
				controller.model.prettyCron = prettyCron.toString(controller.model.cron.substring(2));
			} else {
				controller.model.cron = null;
				controller.model.prettyCron = null;
			}
		}
		
		$scope.$watch('[controller.model.minute, controller.model.hour, controller.model.dayOfMonth, controller.model.dayOfWeek, controller.model.period]', function (newValue, oldValue) {
			if (newValue != oldValue) {
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
						optionsAttr: 'bs-options',
						description : "",
						valueProp : 'name',
						labelProp : 'name',
						optionsAttr: 'ui-options', "ngOptions": 'ui-options',
						placeholder : 'Select Game Report Domain',
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
					template: "<div><strong>Period of Game Data</strong></div>"
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
								optionsAttr: 'bs-options',
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select the Period',
								options : [{ name: "Day", value: 3  }, { name: "Month", value: 2 }, { name: "Week", value: 4}, { name: "Year", value: 1 }, { name: "Total", value: 5 }]
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
							template: "<div><strong>Schedule</strong></div>"
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
								optionsAttr: 'bs-options',
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select the Period',
								options : [{ name: "Daily", value: "Daily" }, { name: "Weekly", value: "Weekly" }, { name: "Monthly", value: "Monthly"}]
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
								optionsAttr: 'bs-options',
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select Day of Week',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFWEEK.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFWEEK.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFWEEK.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								var hide = false;
								if (!angular.isDefined(scope.model.period) || scope.model.period != 'Weekly') hide = true;
								if (hide) scope.model.dayOfWeek = null;
								return hide;
							},
							controller: ['$scope', function($scope) {
								var daysOfWeek = [
									{ name: 'Monday', value: 1 }, { name: 'Tuesday', value: 2 }, { name: 'Wednesday', value: 3 },
									{ name: 'Thursday', value: 4 }, { name: 'Friday', value: 5 }, { name: 'Saturday', value: 6 },
									{ name: 'Sunday', value: 7 }
								];
								$scope.to.options = daysOfWeek;
							}]
						},
						{
							className : 'col-xs-12',
							key : "dayOfMonth",
							type : "ui-select-single",
							templateOptions : {
								label : "",
								required : true,
								optionsAttr: 'bs-options',
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select Day of Month',
								options : []
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFMONTH.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFMONTH.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.DAYOFMONTH.DESCRIPTION" | translate'
							},
							hideExpression: function($viewValue, $modelValue, scope) {
								var hide = false;
								if (!angular.isDefined(scope.model.period) || scope.model.period != 'Monthly') hide = true;
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
								optionsAttr: 'bs-options',
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select the Hour',
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
								optionsAttr: 'bs-options',
								description : "",
								valueProp : 'value',
								labelProp : 'name',
								optionsAttr: 'ui-options', "ngOptions": 'ui-options',
								placeholder : 'Select the Minute',
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
			var modalInstance = $uibModal.open({
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
				var exists = false;
				for (var i = 0; i < controller.model.filters.length; i++) {
					console.log(controller.model.filters[i]);
					if (controller.model.filters[i] == filter) {
						exists = true;
					}
				}
				if (!exists) controller.model.filters.push(filter);
			});
		}
		
		controller.removeFilter = function(filter) {
			for (var i = 0; i < controller.model.filters.length; i++) {
				if (controller.model.filters[i] === filter) {
					controller.model.filters.splice(i, 1);
					break;
				}
			}
		}
		
		controller.eligableActions = function() {
			var actions = [{ name: "Send Full Report Via Email", value: "sendFullReportViaEmail" },
							{ name: "Send Report Stats Via Email", value: "sendReportStatsViaEmail" }];
			for (var i = 0; i < controller.model.actions.length; i++) {
				for (var k = 0; k < actions.length; k++) {
					if (controller.model.actions[i].actionType == actions[k].value) {
						actions.splice(k, 1);
					}
				}
			}
			return actions;
		}
		
		controller.addActionModal = function() {
			var modalInstance = $uibModal.open({
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
			for (var i = 0; i < controller.model.actions.length; i++) {
				if (controller.model.actions[i] === action) {
					controller.model.actions.splice(i, 1);
					break;
				}
			}
		}
		
		controller.reportRecipientAddModal = function(actionType) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/games/report/report-recipient.html',
				controller: 'ReportRecipientGamesAddModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					recipients: function() {
						for (var i = 0; i < controller.model.actions.length; i++) {
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
				for (var i = 0; i < controller.model.actions.length; i++) {
					if (controller.model.actions[i].actionType === actionType) {
						controller.model.actions[i].recipients = recipients;
						break;
					}
				}
			});
		}
		
		controller.reportActionEmailTemplateModal = function(actionType) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/reports/games/report/report-email-template.html',
				controller: 'ReportActionGamesEmailTemplateModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					emailTemplate: function() {
						for (var i = 0; i < controller.model.actions.length; i++) {
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
				for (var i = 0; i < controller.model.actions.length; i++) {
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
			ReportGamesRest.editPost(report.id, 'commit', controller.model).then(function(response) {
				if (response._successful) {
					notify.success("Report edited successfully");
					$state.go("^.report", { reportId:response.id });
				} else {
					notify.error("Unable to save edited report");
				}
			});
		}
		
		controller.onContinue = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			ReportGamesRest.editPost(report.id, 'continue', controller.model).then(function(response) {
				if (response._successful) {
					notify.success("Report edit saved successfully");
					$state.go("^.report", { reportId:response.id });
				} else {
					notify.error("Unable to save report for continuing later");
				}
			});
		}
		
		controller.onCancel = function() {
			ReportGamesRest.editPost(report.id, 'cancel', controller.model).then(function(response) {
				if (response._successful) {
					notify.success("Report edit cancelled successfully");
					$state.go("^.report", { reportId:response.id });
				} else {
					notify.error("Unable to cancel edit report");
				}
			});
		}
	}
]);