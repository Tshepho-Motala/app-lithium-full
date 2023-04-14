'use strict';

angular.module('lithium')
	.controller('LeaderboardView', ["leaderboard", "RRule", "$userService", "notify", "errors", "$state", "LeaderboardRest", "$dt", "$translate", "NotificationRest", "$uibModal", "$filter", "$scope",
	function(leaderboard, RRule, $userService, notify, errors, $state, lbRest, $dt, $translate, notificationRest, $uibModal, $filter, $scope) {
		var controller = this;
		
		controller.model = {};
		controller.model = leaderboard;
		controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		
		controller.scheduler = {
			box: "default box-solid",
			title: "GLOBAL.SCHEDULER.TITLE",
			recurrencePattern: leaderboard.recurrencePattern
		}
		
		var rr = {};
//		if (leaderboard.recurrencePattern.indexOf("UNTIL") > -1) {
//			console.log(leaderboard.recurrencePattern.slice(0, leaderboard.recurrencePattern.indexOf("UNTIL")-1));
//			rr = RRule.RRule.fromString(leaderboard.recurrencePattern.slice(0, leaderboard.recurrencePattern.indexOf("UNTIL")-1));
//			controller.rruleText = rr.toText();
//			var until = new Date(parseInt(leaderboard.recurrencePattern.slice(leaderboard.recurrencePattern.indexOf("UNTIL")+6)));
//			var date = $filter('date')(until, 'yyyy-MM-dd', 'GMT');
//			controller.rruleText += " until "+date;
//			console.log(until, date);
//		} else {
		controller.rruleText = "";
		var translations = [
			"GLOBAL.GRANULARITY.YEARS",
			"GLOBAL.GRANULARITY.MONTHS",
			"GLOBAL.GRANULARITY.DAYS",
			"GLOBAL.GRANULARITY.WEEKS"
		];
		
		controller.doRRule = function(pattern) {
			$translate(translations).then(function success(translate) {
				controller.rruleText = "Starting "+$filter('date')(leaderboard.startDate, 'MMMM dd, yyyy', 'GMT');
				controller.rruleText += " runs for "+leaderboard.durationPeriod+" ";
				switch (parseInt(leaderboard.durationGranularity)) {
					case 1: { controller.rruleText += translate["GLOBAL.GRANULARITY.YEARS"].toLowerCase(); break }
					case 2: { controller.rruleText += translate["GLOBAL.GRANULARITY.MONTHS"].toLowerCase(); break }
					case 3: { controller.rruleText += translate["GLOBAL.GRANULARITY.DAYS"].toLowerCase(); break }
					case 4: { controller.rruleText += translate["GLOBAL.GRANULARITY.WEEKS"].toLowerCase(); break }
				}
				if (!pattern) rr = RRule.RRule.fromString(leaderboard.recurrencePattern);
				if (pattern) rr = RRule.RRule.fromString(pattern);
				controller.rruleText += " "+rr.toText();
				controller.model.startDate = rr.options.dtstart;
			});
		}
		controller.doRRule();
		
		$scope.$watch('controller.rrule', function(newValue, oldValue, theScope) {
			if (newValue !== oldValue) {
				controller.model.recurrencePattern = controller.rrule;
				controller.doRRule(controller.rrule);
				controller.onSubmit();
				controller.recurrence();
			}
		});
		
		controller.recurrence = function() {
			lbRest.recurrence(leaderboard.id).then(function(response) {
				controller.upcoming = response.plain();
//				notify.success("UI_NETWORK_ADMIN.LEADERBOARD.TOGGLE.SUCCESS");
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.RECURRENCE.ERROR");
				errors.catch("", false)(error)
			});
		}
		controller.recurrence();
		
		controller.model.domainName = controller.model.domain.name;
		controller.modelOriginal = angular.copy(leaderboard);
		controller.options = { formState: { readOnly: true } };
		
		controller.onEdit = function() {
			controller.options.formState.readOnly = false;
		}
		
		controller.onCancel = function() {
			controller.onReset();
			controller.options.formState.readOnly = true;
		}
		controller.onReset = function() {
			controller.model = angular.copy(controller.modelOriginal);
		}
		
		controller.fields = [{
			"className":"col-xs-8",
			"type":"input",
			"key":"name",
			optionsTypes: ['editable'],
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NAME.DESC" | translate'
			}
		},{
			"className":"col-xs-12",
			"type":"input",
			"key":"description",
			"optionsTypes": ['editable'],
			"templateOptions":{
				"type":"",
				"label":"",
				"required":false,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DESCRIPTION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DESCRIPTION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DESCRIPTION.DESC" | translate'
			}
		},{
			className: "col-xs-12",
			type: "hr"
		},{
			"className": "col-xs-6",
			"key": "durationPeriod",
			type: "ui-number-mask",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: true,
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '0',
				max: '999999'
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DURATIONPERIOD.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DURATIONPERIOD.PLACE" | translate'
			}
		},{
			"className": "col-xs-12",
			key: "durationGranularity",
			type: "granularities",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: true,
				showicons: true,
				include: [1,2,3,4]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DURATIONGRANULARITY.TITLE" | translate'
			}
		},{
			className: "col-xs-12",
			type: "hr"
		},{
			"className":"col-xs-6",
			"key":"xpLevelMin",
			type: "ui-number-mask",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: false,
				decimals: 0,
				hidesep: true,
				neg: false
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPLEVELMIN.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPLEVELMIN.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPLEVELMIN.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		},{
			"className":"col-xs-6",
			"key":"xpLevelMax",
			type: "ui-number-mask",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: false,
				decimals: 0,
				hidesep: true,
				neg: false
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPLEVELMAX.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPLEVELMAX.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPLEVELMAX.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		},{
			className: "col-xs-12",
			key: "xpLevelExample",
			type: "examplewell",
			templateOptions: {
				label: "",
				explain: ""
			},
			expressionProperties: {
				'templateOptions.label': '',
				'templateOptions.explain': function(viewValue, modelValue, $scope) {
					if (($scope.model.xpLevelMin) && ($scope.model.xpLevelMax)) {
						$translate("UI_NETWORK_ADMIN.LEADERBOARD.XPLEVELREQUIREDEXPLAIN", {
							min: $scope.model.xpLevelMin,
							max: $scope.model.xpLevelMax
						}).then(function success(translate) {
							$scope.options.templateOptions.explain = translate;
						});
					} else {
						$translate("UI_NETWORK_ADMIN.LEADERBOARD.XPLEVELREQUIREDEXPLAIN2").then(function success(translate) {
							$scope.options.templateOptions.explain = translate;
						});
					}
				}
			}
		},{
			className: "col-xs-12",
			type: "hr"
		},{
			"className": "row v-reset-row ",
			"fieldGroup": [{
				"className": "col-xs-6",
				"key":"xpPointsMin",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 2,
					hidesep: true,
					neg: false,
					min: '0',
					max: '999999'
				},
				"expressionProperties": {
					'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSMIN.TITLE" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSMIN.PLACE" | translate'
//					'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSREQUIRED.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = controller.availableDomains;
				}]
			},{
				"className": "col-xs-6",
				"key":"xpPointsMax",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 2,
					hidesep: true,
					neg: false,
					min: '0',
					max: '999999'
				},
				"expressionProperties": {
					'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSMAX.TITLE" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSMAX.PLACE" | translate'
//					'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSREQUIRED.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = controller.availableDomains;
				}]
			},{
				"className": "col-xs-6",
				"key":"xpPointsPeriod",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: '999999'
				},
				"expressionProperties": {
					'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSPERIOD.TITLE" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSPERIOD.PLACE" | translate'
//					'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSREQUIRED.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = controller.availableDomains;
				}]
			},{
				"className":"col-xs-12",
				key: "xpPointsGranularity",
				type: "granularities",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					showicons: true,
					include: [1,2,3,4]
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSGRANULARITY.TITLE" | translate'
//					'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.XPPOINTSREQUIRED.GRANULARITY.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
//					$scope.model.xpPointsGranularity = 0;
				}]
			},{
				className: "col-xs-12",
				key: "xpExample",
				type: "examplewell",
				templateOptions: {
					label: "",
					explain: ""
				},
				expressionProperties: {
					'templateOptions.label': '',
					'templateOptions.explain': function(viewValue, modelValue, $scope) {
						var translations = [];
						translations.push("GLOBAL.GRANULARITIES.2");
						translations.push("GLOBAL.GRANULARITIES.1");
						translations.push("GLOBAL.GRANULARITIES.3");
						translations.push("GLOBAL.GRANULARITIES.4");
						$translate(translations).then(function success(response) {
							$translate("UI_NETWORK_ADMIN.LEADERBOARD.XPPOINTSREQUIREDEXPLAIN", {
								min: $scope.model.xpPointsMin,
								max: $scope.model.xpPointsMax,
								last: $scope.model.xpPointsPeriod,
								period: response["GLOBAL.GRANULARITIES."+$scope.model.xpPointsGranularity]
							}).then(function success(translate) {
								$scope.options.templateOptions.explain = translate;
							});
						});
					}
				}
			}]
		},{
			className: "col-xs-12",
			type: "hr"
		},{
			"className":"col-xs-6",
			"key":"amount",
			type: "ui-number-mask",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: true,
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '1',
				max: '1000'
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.AMOUNT.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.AMOUNT.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.AMOUNT.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		},{
			"className":"col-xs-6",
			"key":"scoreToPoints",
			type: "ui-number-mask",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: true,
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '1'
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.SCORETOPOINTS.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.SCORETOPOINTS.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.SCORETOPOINTS.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		},{
			key: "notification", 
			className: "col-xs-6",
			type: "ui-select-single",
			"optionsTypes": ['editable'],
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'name',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': function(viewValue, modelValue, $scope) {
					$translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.TITLE", {
						top: ($scope.model.amount)?$scope.model.amount:"?"
					}).then(function success(translate) {
						$scope.options.templateOptions.label = translate;
					});
				},
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				if (controller.model.domain.name) {
					notificationRest.findByDomainName(controller.model.domain.name).then(function(notifications) {
						$scope.to.options = notifications.plain();
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATIONS.LIST.ERROR");
						//errors.catch("", false)(error);
						$scope.to.options = [];
					}).finally(function() {
					});
				}
			}]
		},{
			key: "notificationNonTop", 
			className: "col-xs-6",
			type: "ui-select-single",
			"optionsTypes": ['editable'],
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'name',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': function(viewValue, modelValue, $scope) {
					$translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATIONNONTOP.TITLE", {
						top: ($scope.model.amount)?$scope.model.amount:"?"
					}).then(function success(translate) {
						$scope.options.templateOptions.label = translate;
					});
				},
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATIONNONTOP.PLACE" | translate',
				'templateOptions.description': function(viewValue, modelValue, $scope) {
					$translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATIONNONTOP.DESC", {
						top: ($scope.model.amount)?$scope.model.amount:"?"
					}).then(function success(translate) {
						$scope.options.templateOptions.description = translate;
					});
				}
			},
			controller: ['$scope', function($scope) {
				if (controller.model.domain.name) {
					notificationRest.findByDomainName(controller.model.domain.name).then(function(notifications) {
						$scope.to.options = notifications.plain();
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATIONS.LIST.ERROR");
						//errors.catch("", false)(error)
						$scope.to.options = [];
					}).finally(function() {
					});
				}
			}]
		}];
		
		var baseUrl = "services/service-leaderboard/leaderboard/admin/history/"+controller.model.id+"/table?1=1";
		controller.leaderboardTable = $dt.builder()
		.column($dt.columncombinedates('startDate', 'endDate', 'startDate', 'endDate').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.HISTORY.PERIOD")))
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.HISTORY.CLOSED"),
				[{lclass: function(data) {
					if (data.closed) {
						return "danger";
					}
					return "success";
				},
				text: function(data) {
					return data.closed+"";
				},
				uppercase:true
				}]
			)
		)
		.column($dt.columnsize('entries').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.HISTORY.ENTRIES")))
		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewHistory(data) } }]))
		.options(baseUrl)
		.order([0, 'desc'])
		.build();
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.leaderboardTable.instance)) {
				baseUrl = "services/service-leaderboard/leaderboard/admin/history/"+controller.model.id+"/table?1=1";
				controller.leaderboardTable.instance._renderer.options.ajax = baseUrl;
				controller.leaderboardTable.instance.rerender();
			}
		}
		
		var baseUrl = "services/service-leaderboard/leaderboard/admin/conversion/"+controller.model.id+"/table?1=1";
		controller.leaderboardConversionTable = $dt.builder()
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.TYPE.TITLE"),
				[{lclass: function(data) {
					return "info";
				},
				text: function(data) {
					return "UI_NETWORK_ADMIN.LEADERBOARD.TYPE."+data.type;
				},
				uppercase:true
				}]
			)
		)
		.column($dt.column('conversion').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.RATE")))
		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewConversion(data) } }]))
		.options(baseUrl)
		.order([1, 'desc'])
		.build();
		
		controller.tableConversionLoad = function() {
			if (!angular.isUndefined(controller.leaderboardConversionTable.instance)) {
				baseUrl = "services/service-leaderboard/leaderboard/admin/conversion/"+controller.model.id+"/table?1=1";
				controller.leaderboardConversionTable.instance._renderer.options.ajax = baseUrl;
				controller.leaderboardConversionTable.instance.rerender();
			}
		}
		
		controller.viewConversion = function(conversion) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/leaderboard/view/viewconversion.html',
				controller: 'ViewConversion',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					conversion: function() { return conversion; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/leaderboard/view/viewconversion.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(category) {
				console.log(category);
				controller.tableConversionLoad();
			});
		}
		
		controller.addConversion = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/leaderboard/view/addconversion.html',
				controller: 'AddConversion',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					leaderboard: function() { return leaderboard; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/leaderboard/view/addconversion.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(category) {
				console.log(category);
				controller.tableConversionLoad();
			});
		}
		controller.addNotification = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/leaderboard/view/addnotification.html',
				controller: 'AddNotification',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					leaderboard: function() { return leaderboard; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/leaderboard/view/addnotification.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(category) {
				controller.tableNotificationsLoad();
			});
		}
		controller.editNotification = function(notification) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/leaderboard/view/viewnotification.html',
				controller: 'ViewNotification',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					leaderboard: function() { return leaderboard; },
					notification: function() { return notification; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/leaderboard/view/viewnotification.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(notification) {
				controller.tableNotificationsLoad();
			});
		}
		
		var baseUrl = "services/service-leaderboard/leaderboard/admin/notification/"+controller.model.id+"/table?1=1";
		controller.leaderboardNotificationsTable = $dt.builder()
		.column($dt.column('rank').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATION.RANK")))
		.column($dt.column('bonusCode').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATION.BONUSCODE")))
		.column($dt.column('notification').withTitle($translate("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATION.NOTIFICATION")))
		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.editNotification(data) } }]))
		.options(baseUrl)
		.order([0, 'asc'])
		.build();
		
		controller.tableNotificationsLoad = function() {
			if (!angular.isUndefined(controller.leaderboardNotificationsTable.instance)) {
				baseUrl = "services/service-leaderboard/leaderboard/admin/notification/"+controller.model.id+"/table?1=1";
				controller.leaderboardNotificationsTable.instance._renderer.options.ajax = baseUrl;
				controller.leaderboardNotificationsTable.instance.rerender();
			}
		}
		
		controller.toggle = function() {
			lbRest.toggle(controller.model.id).then(function(response) {
				controller.model = response.plain();
				notify.success("UI_NETWORK_ADMIN.LEADERBOARD.TOGGLE.SUCCESS");
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.TOGGLE.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.enable = function() {
			lbRest.enable(controller.model.id).then(function(response) {
				controller.model = response.plain();
				notify.success("UI_NETWORK_ADMIN.LEADERBOARD.ENABLED.SUCCESS");
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.ENABLED.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.refresh = function() {
			lbRest.findLeaderboardById(leaderboard.id).then(function(lb) {
				controller.model = lb.plain();
				controller.tableNotificationsLoad();
				controller.tableConversionLoad();
				controller.tableLoad();
				controller.doRRule();
				controller.recurrence();
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			lbRest.edit(controller.model).then(function(response) {
				console.log(response.plain());
				controller.model = response.plain();
				notify.success("UI_NETWORK_ADMIN.LEADERBOARD.EDIT.SUCCESS");
				controller.options.formState.readOnly = true;
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.viewHistory = function(data) {
			$state.go("dashboard.leaderboard.history", {id: data.id});
		}
		
		controller.list = function() {
			$state.go("dashboard.leaderboard.list");
		};
	}
]);
