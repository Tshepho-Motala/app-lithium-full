'use strict';

angular.module('lithium')
	.controller('LeaderboardAdd', ["$userService", "RRule", "notify", "errors", "$state", "$translate", "LeaderboardRest", "NotificationRest",
	function($userService, RRule, notify, errors, $state, $translate, lbRest, notificationRest) {
		var controller = this;
		
		controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		
		controller.model = {};
		controller.options = {};
		controller.scheduler = {
			box: "default box-solid",
			title: "GLOBAL.SCHEDULER.TITLE"
		}
		
		controller.fields = [{
			"className":"col-xs-6",
			"type":"input",
			"key":"name",
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
			"className":"col-xs-6",
			"type":"ui-select-single",
			"key":"domainName",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required":true,
				"optionsAttr": "bs-options",
				"valueProp": "name",
				"labelProp": "name",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DOMAINNAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DOMAINNAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.DOMAINNAME.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		},{
			"className":"col-xs-12",
			"type":"input",
			"key":"description",
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
			"className": "col-xs-6",
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
			"className": "col-xs-12",
			key: "recurrencePattern",
			type: "scheduler",
			optionsTypes: ['editable'],
			templateOptions: {
				data: controller.scheduler
			},
			controller: ['$scope', function($scope) {
				console.log($scope);
			}]
		},{
			className: "col-xs-12",
			type: "hr"
		},{
			"className":"col-xs-4",
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
			}
		},{
			"className":"col-xs-4",
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
			}
		},{
			className: "col-xs-4",
			key: "xpLevelExample",
			type: "examplewell",
			templateOptions: {
				label: "",
				explain: ""
			},
			expressionProperties: {
				'templateOptions.label': '',
				'templateOptions.explain': function(viewValue, modelValue, $scope) {
					if (!angular.isUndefined($scope.model.xpLevelMin) && !angular.isUndefined($scope.model.xpLevelMax)) {
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
			template: "<div><strong><hr/></strong></div>"
		},{
			"className": "row v-reset-row ",
			"fieldGroup": [{
				"className": "col-xs-2",
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
				}
			},{
				"className": "col-xs-2",
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
				}
			},{
				"className": "col-xs-2",
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
				}
			},{
				"className":"col-xs-6",
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
				}
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
			template: "<div><strong><hr/></strong></div>"
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
			}
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
				min: '1',
				max: '999999'
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.SCORETOPOINTS.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.SCORETOPOINTS.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.SCORETOPOINTS.DESC" | translate'
			}
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
				if (controller.model.domainName) {
					notificationRest.findByDomainName(controller.model.domainName).then(function(notifications) {
						$scope.to.options = notifications.plain();
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATIONS.LIST.ERROR");
//						errors.catch("", false)(error)
						$scope.to.options = [];
					}).finally(function() {
					});
				}
				$scope.$watch('model.domainName', function (newValue, oldValue, theScope) {
					if (newValue !== oldValue) {
						if ($scope.model[$scope.options.key] && oldValue) {
							$scope.model[$scope.options.key] = '';
						} 
//						$scope.to.loading = 
						notificationRest.findByDomainName(controller.model.domainName).then(function(notifications) {
							$scope.to.options = notifications.plain();
						}).catch(function(error) {
							notify.error("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATIONS.LIST.ERROR");
//							errors.catch("", false)(error)
							$scope.to.options = [];
						}).finally(function() {
						});
					}
				});
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
				if (controller.model.domainName) {
					notificationRest.findByDomainName(controller.model.domainName).then(function(notifications) {
						$scope.to.options = notifications.plain();
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATIONS.LIST.ERROR");
//						errors.catch("", false)(error)
						$scope.to.options = [];
					}).finally(function() {
					});
				}
				$scope.$watch('model.domainName', function (newValue, oldValue, theScope) {
					if (newValue !== oldValue) {
						if ($scope.model[$scope.options.key] && oldValue) {
							$scope.model[$scope.options.key] = '';
						} 
//						$scope.to.loading = 
						notificationRest.findByDomainName(controller.model.domainName).then(function(notifications) {
							$scope.to.options = notifications.plain();
						}).catch(function(error) {
							notify.error("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATIONS.LIST.ERROR");
//							errors.catch("", false)(error)
							$scope.to.options = [];
						}).finally(function() {
						});
					}
				});
			}]
		},{
			className: "col-xs-12",
			template: "<div><strong>&nbsp;</strong></div>"
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			var recurr = controller.model.recurrencePattern.split('\n');
			var startDate = luxon.DateTime.fromISO(recurr[0].split(':')[1]);
			console.log(recurr, startDate);
			
			controller.model.startDate = startDate;
			
			lbRest.add(controller.model).then(function(category) {
				if (category._successful) {
					notify.success("UI_NETWORK_ADMIN.LEADERBOARD.ADD.SUCCESS");
					$state.go("dashboard.leaderboard.list");
				} else {
					notify.error("UI_NETWORK_ADMIN.LEADERBOARD.ADD.ERROR");
				}
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			console.log('cancel');
			$state.go("dashboard.leaderboard.list");
		};
	}
]);