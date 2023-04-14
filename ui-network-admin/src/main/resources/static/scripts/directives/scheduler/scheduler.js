'use strict';

angular.module('lithium')
	.directive('scheduler', function() {
		return {
			templateUrl:'scripts/directives/scheduler/scheduler.html',
			scope: {
				data: "=",
				scheduler: "=ngModel"
			},
			restrict: 'E',
			replace: true,
			controller: ['$q', '$uibModal', '$scope', 'rest-accounting', 'errors', 'bsLoadingOverlayService', 'notify', 'rest-domain', 'RRule', '$filter',
			function($q, $uibModal, $scope, acctRest, errors, bsLoadingOverlayService, notify, domainRest, RRule, $filter) {
				var me = this;
				
				if (!$scope.data.color) {
					$scope.data.color = "gray";
				}
				if (!$scope.data.recurrencePattern) {
					$scope.data.recurrencePattern = "";
				} else {
					$scope.rrule = $scope.data.recurrencePattern;
					var rr = {};
//					if ($scope.data.recurrencePattern.indexOf("UNTIL") > -1) {
//						rr = RRule.RRule.fromString($scope.data.recurrencePattern);
//						//rr = RRule.RRule.fromString($scope.data.recurrencePattern.slice(0, $scope.data.recurrencePattern.indexOf("UNTIL")-1));
//						$scope.rruleText = rr.toText();
////						var until = new Date(parseInt($scope.data.recurrencePattern.slice($scope.data.recurrencePattern.indexOf("UNTIL")+6)));
////						var date = $filter('date')(until, 'yyyy-MM-dd', 'GMT);
////						$scope.rruleText += " until "+date;
//					} else {
						rr = RRule.RRule.fromString($scope.data.recurrencePattern);
						$scope.rruleText = rr.toText();
//					}
					
					$scope.rruleStartDate = rr.options.dtstart;
				}
				$scope.referenceId = 'scheduler-overlay-'+(Math.random()*1000);
				
				$scope.adjust = function () {
					var scheduleModalInstance = $uibModal.open({
						animation: true,
						ariaLabelledBy: 'modal-title',
						ariaDescribedBy: 'modal-body',
						templateUrl: 'scripts/directives/scheduler/addschedule.html',
						controller: 'ScheduleModal',
						controllerAs: 'controller',
						backdrop: 'static',
						size: 'lg',
						resolve: {
							recurrencePattern: function(){return $scope.data.recurrencePattern},
							freq: function($translate) {
								var freqTranslated = [];
								var freqTranslations = [
									'GLOBAL.ICAL.FREQ.DAILY',
									'GLOBAL.ICAL.FREQ.WEEKLY',
									'GLOBAL.ICAL.FREQ.MONTHLY_BY_DAY',
									'GLOBAL.ICAL.FREQ.MONTHLY_BY_DATE',
									'GLOBAL.ICAL.FREQ.YEARLY_BY_DAY',
									'GLOBAL.ICAL.FREQ.YEARLY_BY_DATE'
								];
								return $translate(freqTranslations).then(function(translations) {
									angular.forEach(translations, function(v,k) {
										this.push({id:k.slice(k.lastIndexOf(".")+1), name:v});
									}, freqTranslated);
									return freqTranslated;
								});
							},
							byday: function($translate) {
								var daysTranslated = [];
								var byDayTranslations = [
									'GLOBAL.SCHEDULER.BYDAY.0',
									'GLOBAL.SCHEDULER.BYDAY.1',
									'GLOBAL.SCHEDULER.BYDAY.2',
									'GLOBAL.SCHEDULER.BYDAY.3',
									'GLOBAL.SCHEDULER.BYDAY.4',
									'GLOBAL.SCHEDULER.BYDAY.5',
									'GLOBAL.SCHEDULER.BYDAY.6'
								];
								return $translate(byDayTranslations).then(function(translations) {
									angular.forEach(translations, function(v,k) {
										this.push({id:parseInt(k.slice(k.lastIndexOf(".")+1)), name:v});
									}, daysTranslated);
									return daysTranslated;
								});
							},
							bymonth: function($translate) {
								var byMonthTranslations = [];
								var monthsTranslated = [];
								for (var k=1; k<13; k++) {
									byMonthTranslations.push("GLOBAL.ICAL.INTERVAL.BYMONTH."+k);
								}
								return $translate(byMonthTranslations).then(function(translations) {
									angular.forEach(translations, function(v,k) {
										this.push({id:parseInt(k.split("GLOBAL.ICAL.INTERVAL.BYMONTH.")[1]), name:v});
									}, monthsTranslated);
									return monthsTranslated;
								});
							},
							bymonthday: function($translate) {
								var byMonthDayTranslated = [];
								var byMonthDayTranslations = [];
								for (var k=0; k<42; k++) {
									byMonthDayTranslations.push("GLOBAL.ICAL.INTERVAL.MONTHLYDAY."+k);
								}
								return $translate(byMonthDayTranslations).then(function(translations) {
									angular.forEach(translations, function(v,k) {
										this.push({id:parseInt(k.split("GLOBAL.ICAL.INTERVAL.MONTHLYDAY.")[1]), name:v});
									}, byMonthDayTranslated);
									return byMonthDayTranslated;
								});
							},
							bymonthdate: function($translate) {
								var byMonthDayTranslated = [];
								var byMonthDayTranslations = [];
								for (var k=1; k<32; k++) {
									byMonthDayTranslations.push("GLOBAL.ICAL.INTERVAL.MONTHDAY."+k);
								}
								return $translate(byMonthDayTranslations).then(function(translations) {
									angular.forEach(translations, function(v,k) {
										this.push({id:parseInt(k.split("GLOBAL.ICAL.INTERVAL.MONTHDAY.")[1]), name:v});
									}, byMonthDayTranslated);
									return byMonthDayTranslated;
								});
							},
							occurrences: function($translate) {
								var occurrences = [];
								var repeatTranslations = [
									"GLOBAL.ICAL.REPEAT.0",
									"GLOBAL.ICAL.REPEAT.1",
									"GLOBAL.ICAL.REPEAT.2"
								];
								return $translate(repeatTranslations).then(function(translations) {
									angular.forEach(translations, function(v,k) {
										this.push({id:parseInt(k.slice(k.lastIndexOf(".")+1)), name:v});
									}, occurrences);
									return occurrences;
								});
							}
						}
					});
					scheduleModalInstance.result.then(function(response) {
						$scope.scheduler = response.recurrencePattern;
						$scope.rrule = response.recurrencePattern;
						$scope.rruleText = response.recurrenceText;
						$scope.rruleStartDate = response.recurrenceStartDate;
					});
				};
			}]
		}
	}).controller('ScheduleModal', ['$uibModalInstance', '$translate', '$userService', '$scope', 'notify', 'RRule', '$filter', 'recurrencePattern', 'errors', 'bsLoadingOverlayService', 'bymonth', 'freq', 'byday', 'bymonthday', 'bymonthdate', 'occurrences',
		function ($uibModalInstance, $translate, $userService, $scope, notify, RRule, $filter, recurrencePattern, errors, bsLoadingOverlayService, bymonth, freq, byday, bymonthday, bymonthdate, occurrences) {
			var controller = this;
			controller.options = {};
			controller.model = {};
//			controller.modelOriginal = angular.copy(group);
			controller.model.recurrencePattern = recurrencePattern;
			controller.model.recurrenceText = "";
//			controller.model.interval = -1;
			controller.model.byMonth = [];
			controller.model.byDay = [];
			
			controller.fields = [{
				"className":"col-xs-12",
				"type":"input",
				"key":"recurrencePattern",
				"templateOptions":{
					"type":"",
					"label":"",
					"required":true,
					"placeholder":"",
					"description":"",
					"options":[]
				},
				"expressionProperties": {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.RECURRENCEPATTERN.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.RECURRENCEPATTERN.PLACE" | translate'
					
				}
			},{
				className: "col-xs-12",
				key: "recurrenceText",
				noFormControl: true,
				template: "<input type=\"text\" style=\"margin-bottom: 10px;\" ng-model=\"model[options.key]\" class=\"form-control\" readonly=\"readonly\"/>",
				templateOptions: {
					label: "",
					explain: ""
				},
				expressionProperties: {
					'templateOptions.label': ''
				}
			},{
				className: "col-xs-12",
				template: "<div><p class='subtitle fancy'><span>or</span></p></div>"
			},{
				"className":"col-xs-6",
				"type":"ui-select-single",
				"key":"freq",
				"templateOptions":{
					"label":"",
					"placeholder":"",
					"description":"",
					"required":true,
					"optionsAttr": "bs-options",
					"valueProp": "id",
					"labelProp": "name",
					"options":[],
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				"expressionProperties": {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.FREQ.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.FREQ.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.FREQ.DESC" | translate',
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = freq;
				}]
				/**
				 * 0: Daily
				 * 1: Weekly
				 * 2: Monthly by day
				 * 3: Monthly by date
				 * 4: Yearly by day
				 * 5: Yearly by date
				 */
			},{
				"className":"col-xs-6",
				"type":"ui-select-single",
				"key":"interval",
				"templateOptions":{
					"label":"",
					"placeholder":"",
					"description":"",
					"required":true,
					"optionsAttr": "bs-options",
					"valueProp": "id",
					"labelProp": "name",
					"options":[],
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				"expressionProperties": {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.INTERVAL.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.INTERVAL.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.INTERVAL.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = controller.interval;
					$scope.$watch('model.freq', function(newValue, oldValue, $scope) {
						if (newValue !== oldValue) {
							if ($scope.model[$scope.options.key] && oldValue) {
								$scope.model[$scope.options.key] = '';
								controller.reset();
							}
							controller.buildRRule();
							$scope.options.templateOptions.options = controller.interval;
						} 
					});
				}]
			},{
				"className":"col-xs-6",
				"type":"ui-select-multiple",
				"key":"byDay",
				"templateOptions":{
					"label":"",
					"placeholder":"",
					"description":"",
					"required":false,
					"optionsAttr": "bs-options",
					"valueProp": "id",
					"labelProp": "name",
					"options":[],
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				"expressionProperties": {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = byday;
				}],
				hideExpression: function($viewValue, $modelValue, scope) {
					if (controller.model.freq === "WEEKLY") return false;
					return true;
				}
			},{
				"className":"col-xs-6",
				"type":"ui-select-multiple",
				"key":"byMonth",
				"templateOptions":{
					"label":"",
					"placeholder":"",
					"description":"",
					"required":true,
					"optionsAttr": "bs-options",
					"valueProp": "id",
					"labelProp": "name",
					"options":bymonth,
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				"expressionProperties": {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.BYMONTH.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.BYMONTH.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.BYMONTH.DESC" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if ((controller.model.freq === "YEARLY_BY_DATE") || (controller.model.freq === "YEARLY_BY_DAY")) return false;
					return true;
				}
			},{
				"className":"col-xs-6",
				"type":"ui-select-multiple",
				"key":"byMonthDay",
				"templateOptions":{
					"label":"",
					"placeholder":"",
					"description":"",
					"required":true,
					"optionsAttr": "bs-options",
					"valueProp": "id",
					"labelProp": "name",
					"options": bymonthday,
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					},
					onRemove: function($item, $model) {
						console.log($item, $model);
					}
				},
				"expressionProperties": {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.DESC" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if ((controller.model.freq === "MONTHLY_BY_DAY") || (controller.model.freq === "YEARLY_BY_DAY")) return false;
					return true;
				}
			},{
				"className":"col-xs-6",
				"type":"ui-select-multiple",
				"key":"byMonthDate",
				"templateOptions":{
					"label":"",
					"placeholder":"",
					"description":"",
					"required":true,
					"optionsAttr": "bs-options",
					"valueProp": "id",
					"labelProp": "name",
					"options": bymonthdate,
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				"expressionProperties": {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.BYDAY.DESC" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if ((controller.model.freq === "MONTHLY_BY_DATE") || (controller.model.freq === "YEARLY_BY_DATE")) return false;
					return true;
				}
			},{
				className: "col-xs-12",
				type: "hr"
			},{
				className: "col-xs-4",
				key : "startDate",
				type : "datepicker",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					placeholder : '',
					datepickerOptions: {
						format: 'dd/MM/yyyy',
						initDate: new Date()
					},
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.STARTDATE.TITLE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.STARTDATE.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.initDate = new Date();
				}]
			},{
				"className": "col-xs-4",
				"type": "ui-select-single",
				"key": "occurrence",
				"templateOptions": {
					"label":"",
					"placeholder":"",
					"description":"",
					"required":true,
					"optionsAttr": "bs-options",
					"valueProp": "id",
					"labelProp": "name",
					"options": occurrences,
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.DESC" | translate'
				}
			},{
				className: "col-xs-4",
				key : "occurrenceValue",
				type : "datepicker",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					placeholder : '',
					datepickerOptions: {
						format: 'dd/MM/yyyy'
					},
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.UNTIL.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.UNTIL.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.UNTIL.DESC" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (controller.model.occurrence === 1) return false;
					return true;
				}
			},{
				"className": "col-xs-4",
				"key": "occurrenceValue",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
					max: '999999',
					onChange: function($viewValue, $modelValue, $scope) {
						controller.buildRRule();
					}
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.NR.TITLE" | translate',
					'templateOptions.placeholder': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.NR.PLACE" | translate',
					'templateOptions.description': '"GLOBAL.SCHEDULER.FIELDS.OCCURRENCE.NR.DESC" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (controller.model.occurrence === 2) return false;
					return true;
				}
			}];
			
			///////////////////////
			
			controller.reset = function() {
//				controller.model = {}; //angular.copy(controller.modelOriginal);
				if (controller.model.freq === '') {
					controller.model = {};
				} else {
					var freq = controller.model.freq;
					controller.model = {};
					controller.model.freq = freq;
				}
			}
			
			controller.firstBuild = function() {
				if (recurrencePattern) {
					var rr = {};
//					if (recurrencePattern.indexOf("UNTIL") > -1) {
//						rr = RRule.RRule.fromString($scope.data.recurrencePattern);
////						rr = RRule.RRule.fromString(recurrencePattern.slice(0, recurrencePattern.indexOf("UNTIL")-1));
//						controller.model.recurrenceText = rr.toText();
//						var until = new Date(parseInt(recurrencePattern.slice(recurrencePattern.indexOf("UNTIL")+6)));
//						var date = $filter('date')(until, 'yyyy-MM-dd', 'GMT);
//						controller.model.recurrenceText += " until "+date;
//					} else {
						rr = RRule.RRule.fromString(recurrencePattern);
						controller.model.recurrenceText = rr.toText();
						controller.model.recurrenceStartDate = rr.options.dtstart;
//					}
//					rr.options.wkst = RRule.RRule.MO;
					
					switch (rr.options.freq) {
						case RRule.RRule.DAILY:
							controller.model.freq = "DAILY";
							break;
						case RRule.RRule.WEEKLY:
							controller.model.freq = "WEEKLY";
							break;
						case RRule.RRule.MONTHLY:
							if (rr.options.byweekday) controller.model.freq = "MONTHLY_BY_DAY";
							if (rr.options.bymonthday) controller.model.freq = "MONTHLY_BY_DATE";
							break;
						case RRule.RRule.YEARLY: 
							console.log(rr.options.byweekday, rr.options.bymonthday);
							if (rr.options.byweekday || rr.options.bynweekday) {
								controller.model.freq = "YEARLY_BY_DAY";
							} else if (rr.options.bymonthday) {
								controller.model.freq = "YEARLY_BY_DATE";
							}
							break;
					}
					
					controller.model.interval = rr.options.interval;
					controller.selectMonths(rr.options.bymonth);
					controller.selectDays(rr.options.byweekday);
					controller.selectStartDate(rr.options.dtstart);
					controller.selectOccurence(rr.options);
//					console.log(recurrencePattern, rr, controller.model);
				}
			}
			
			controller.selectOccurence = function(options) {
				if (options.count != null) {
					controller.model.occurrence = 2;
					controller.model.occurrenceValue = options.count;
				} else if (options.until != null) {
					controller.model.occurrence = 1;
					controller.model.occurrenceValue = options.until;
				} else {
					controller.model.occurrence = 0;
					controller.model.occurrenceValue = null;
				}
			}
			controller.selectStartDate = function(start) {
				controller.model.startDate = start;
			}
			controller.selectDays = function(days) {
				angular.forEach(days, function(v,k) {
					controller.model.byDay.push($filter('filter')(byday, {'id':v})[0]);
				});
			}
			controller.selectMonths = function(months) {
				angular.forEach(months, function(v,k) {
					controller.model.byMonth.push($filter('filter')(bymonth, {'id':v})[0]);
				});
			}
			
			controller.determineInterval = function() {
				var intervalTranslations = [];
				controller.interval = [];
				switch (controller.model.freq) {
					case "DAILY":
						for (var k=1; k<31; k++) {
							intervalTranslations.push("GLOBAL.ICAL.INTERVAL.DAY."+k);
						}
						$translate(intervalTranslations).then(function(translations) {
							angular.forEach(translations, function(v,k) {
								this.push({id:parseInt(k.split("GLOBAL.ICAL.INTERVAL.DAY.")[1]), name:v});
							}, controller.interval);
						});
						break;
					case "WEEKLY":
						for (var k=1; k<27; k++) {
							intervalTranslations.push("GLOBAL.ICAL.INTERVAL.WEEKLY."+k);
						}
						$translate(intervalTranslations).then(function(translations) {
							angular.forEach(translations, function(v,k) {
								this.push({id:parseInt(k.split("GLOBAL.ICAL.INTERVAL.WEEKLY.")[1]), name:v});
							}, controller.interval);
						});
						break;
					case "MONTHLY_BY_DATE":
					case "MONTHLY_BY_DAY":
						for (var k=1; k<17; k++) {
							intervalTranslations.push("GLOBAL.ICAL.INTERVAL.MONTHLY."+k);
						}
						$translate(intervalTranslations).then(function(translations) {
							angular.forEach(translations, function(v,k) {
								this.push({id:parseInt(k.split("GLOBAL.ICAL.INTERVAL.MONTHLY.")[1]), name:v});
							}, controller.interval);
						});
						break;
					case "YEARLY_BY_DATE":
					case "YEARLY_BY_DAY":
						for (var k=1; k<11; k++) {
							intervalTranslations.push("GLOBAL.ICAL.INTERVAL.YEARLY."+k);
						}
						$translate(intervalTranslations).then(function(translations) {
							angular.forEach(translations, function(v,k) {
								this.push({id:parseInt(k.split("GLOBAL.ICAL.INTERVAL.YEARLY.")[1]), name:v});
							}, controller.interval);
						});
						break;
				}
			}
			
			controller.buildRRule = function() {
				var freq;
				var byDay = [];
				var byMonth = [];
				var intervalTranslations = [];
				controller.interval = [];
				
				switch (controller.model.freq) {
					case "DAILY":
						controller.model.byMonth = null;
						controller.model.byDay = null;
						byMonth = null;
						freq = RRule.RRule.DAILY;
						break;
					case "WEEKLY":
						controller.model.byMonth = null;
						byMonth = null;
						freq = RRule.RRule.WEEKLY;
						break;
					case "MONTHLY_BY_DATE":
					case "MONTHLY_BY_DAY":
						controller.model.byMonth = null;
						controller.model.byDay = null;
						byMonth = null;
						freq = RRule.RRule.MONTHLY;
						break;
					case "YEARLY_BY_DATE":
					case "YEARLY_BY_DAY":
						controller.model.byDay = null;
						freq = RRule.RRule.YEARLY;
						break;
				}
				angular.forEach(controller.model.byMonth, function(v,k) {
					byMonth.push(v['id']);
				});
				angular.forEach(controller.model.byDay, function(v,k) {
					byDay.push(v['id']);
				});
				angular.forEach(controller.model.byMonthDay, function(day) {
					if (day['id'] == "0") byDay.push(RRule.RRule.SU.nth(1)); // "1SU,";
					if (day['id'] == "1") byDay.push(RRule.RRule.MO.nth(1)); //  "1MO,";
					if (day['id'] == "2") byDay.push(RRule.RRule.TU.nth(1)); //  "1TU,";
					if (day['id'] == "3") byDay.push(RRule.RRule.WE.nth(1)); //  "1WE,";
					if (day['id'] == "4") byDay.push(RRule.RRule.TH.nth(1)); //  "1TH,";
					if (day['id'] == "5") byDay.push(RRule.RRule.FR.nth(1)); //  "1FR,";
					if (day['id'] == "6") byDay.push(RRule.RRule.SA.nth(1)); //  "1SA,";
					if (day['id'] == "7") byDay.push(RRule.RRule.SU.nth(2)); //  "2SU,";
					if (day['id'] == "8") byDay.push(RRule.RRule.MO.nth(2)); //  "2MO,";
					if (day['id'] == "9") byDay.push(RRule.RRule.TU.nth(2)); //  "2TU,";
					if (day['id'] == "10") byDay.push(RRule.RRule.WE.nth(2)); //  "2WE,";
					if (day['id'] == "11") byDay.push(RRule.RRule.TH.nth(2)); //  "2TH,";
					if (day['id'] == "12") byDay.push(RRule.RRule.FR.nth(2)); //  "2FR,";
					if (day['id'] == "13") byDay.push(RRule.RRule.SA.nth(2)); //  "2SA,";
					if (day['id'] == "14") byDay.push(RRule.RRule.SU.nth(3)); //  "3SU,";
					if (day['id'] == "15") byDay.push(RRule.RRule.MO.nth(3)); //  "3MO,";
					if (day['id'] == "16") byDay.push(RRule.RRule.TU.nth(3)); //  "3TU,";
					if (day['id'] == "17") byDay.push(RRule.RRule.WE.nth(3)); //  "3WE,";
					if (day['id'] == "18") byDay.push(RRule.RRule.TH.nth(3)); //  "3TH,";
					if (day['id'] == "19") byDay.push(RRule.RRule.FR.nth(3)); //  "3FR,";
					if (day['id'] == "20") byDay.push(RRule.RRule.SA.nth(3)); //  "3SA,";
					if (day['id'] == "21") byDay.push(RRule.RRule.SU.nth(4)); //  "4SU,";
					if (day['id'] == "22") byDay.push(RRule.RRule.MO.nth(4)); //  "4MO,";
					if (day['id'] == "23") byDay.push(RRule.RRule.TU.nth(4)); //  "4TU,";
					if (day['id'] == "24") byDay.push(RRule.RRule.WE.nth(4)); //  "4WE,";
					if (day['id'] == "25") byDay.push(RRule.RRule.TH.nth(4)); //  "4TH,";
					if (day['id'] == "26") byDay.push(RRule.RRule.FR.nth(4)); //  "4FR,";
					if (day['id'] == "27") byDay.push(RRule.RRule.SA.nth(4)); //  "4SA,";
					if (day['id'] == "28") byDay.push(RRule.RRule.SU.nth(5)); //  "5SU,";
					if (day['id'] == "29") byDay.push(RRule.RRule.MO.nth(5)); //  "5MO,";
					if (day['id'] == "30") byDay.push(RRule.RRule.TU.nth(5)); //  "5TU,";
					if (day['id'] == "31") byDay.push(RRule.RRule.WE.nth(5)); //  "5WE,";
					if (day['id'] == "32") byDay.push(RRule.RRule.TH.nth(5)); //  "5TH,";
					if (day['id'] == "33") byDay.push(RRule.RRule.FR.nth(5)); //  "5FR,";
					if (day['id'] == "34") byDay.push(RRule.RRule.SA.nth(5)); //  "5SA,";
					if (day['id'] == "35") byDay.push(RRule.RRule.SU.nth(-1)); //  "-1SU,";
					if (day['id'] == "36") byDay.push(RRule.RRule.MO.nth(-1)); //  "-1MO,";
					if (day['id'] == "37") byDay.push(RRule.RRule.TU.nth(-1)); //  "-1TU,";
					if (day['id'] == "38") byDay.push(RRule.RRule.WE.nth(-1)); //  "-1WE,";
					if (day['id'] == "39") byDay.push(RRule.RRule.TH.nth(-1)); //  "-1TH,";
					if (day['id'] == "40") byDay.push(RRule.RRule.FR.nth(-1)); //  "-1FR,";
					if (day['id'] == "41") byDay.push(RRule.RRule.SA.nth(-1)); //  "-1SA,";
				});
				
				var byMonthDate = [];
				angular.forEach(controller.model.byMonthDate, function(date) {
					this.push(date['id']);
				}, byMonthDate);
				
				var count = null;
				var until = null;
				var start = null;
				if (controller.model.occurrence == 1) {
					if (controller.model.occurrenceValue) {
						var end = new Date($filter('date')(controller.model.occurrenceValue));
						until = new Date(Date.UTC(end.getFullYear(), end.getMonth(), end.getDate()))
					}
				} else if (controller.model.occurrence == 2) {
					if (controller.model.occurrenceValue) {
						count = controller.model.occurrenceValue;
					}
				}
				
				if (!controller.model.startDate) {
					start = new Date($filter('date')(Date.now()));
				} else {
					start = new Date(controller.model.startDate);
				}
				
				controller.determineInterval();
				var rule = new RRule.RRule({
					freq: freq,
					interval: controller.model.interval,
					byhour: null,
					byminute: null,
					bysecond: null,
					bymonth: byMonth,
					byweekday: byDay,
					bymonthday: byMonthDate,
					until: until,
					count: count,
					dtstart: new Date(Date.UTC(start.getFullYear(), start.getMonth(), start.getDate()))
				});
//				tzid: luxon.DateTime.local().zoneName
				controller.model.recurrencePattern = rule.toString();
				controller.model.recurrenceText = rule.toText();
				controller.model.recurrenceStartDate = rule.options.dtstart;
			};
			
			controller.firstBuild();
			controller.determineInterval();
			
			controller.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};
			
			controller.onSubmit = function() {
				$uibModalInstance.close({
					recurrencePattern: controller.model.recurrencePattern,
					recurrenceText: controller.model.recurrenceText,
					recurrenceStartDate: controller.model.recurrenceStartDate
				});
			}
		}
	]);
