'use strict';

function attachDecreaseExplanation(controller, $translate) {
	controller.fields.push(
			{
				className: 'col-xs-12',
				key: "explanation",
				type: "examplewell",
				templateOptions: {
					label: "",
					explain: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.EXPLANATION" | translate',
					'templateOptions.explain': function (viewValue, modelValue, $scope) {
						for (let i = 0; i < controller.fields.length; i++) {
							if (controller.fields[i].key === "explanation") {
								$translate("UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DECREASE_LIMIT_EXPLANATION_1").then(
										function success(translate) {
											$scope.options.templateOptions.explain = $translate.instant(
															"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DECREASE_LIMIT_EXPLANATION_1")
													+ " " + '<b>' + $translate.instant("UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DECREASE_LIMIT_EXPLANATION_2")
													+ '</b>' + " " + $translate.instant(
															"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DECREASE_LIMIT_EXPLANATION_3");
										});
							}
						}
					}
				}
			}
	);
}

function attachIncreaseExplanation(controller, $translate) {
	controller.fields.push(
			{
				className: 'col-xs-12',
				key: "explanation",
				type: "examplewell",
				templateOptions: {
					label: "",
					explain: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.EXPLANATION" | translate',
					'templateOptions.explain': function (viewValue, modelValue, $scope) {
						for (let i = 0; i < controller.fields.length; i++) {
							if (controller.fields[i].key === "explanation") {
								$translate("UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.INCREASE_LIMIT_EXPLANATION_1").then(
										function success(translate) {
											$scope.options.templateOptions.explain = $translate.instant(
															"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.INCREASE_LIMIT_EXPLANATION_1")
													+ " " + '<b>' + $translate.instant("UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.INCREASE_LIMIT_EXPLANATION_2")
													+ '</b>' + " " + $translate.instant(
															"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.INCREASE_LIMIT_EXPLANATION_4") + controller.getPendingLimitDelay()
													+$translate.instant("UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.INCREASE_LIMIT_EXPLANATION_5");
										});
							}
						}
					}
				}
			}
	);
}

function removeExplanation(controller) {
	for (let i = 0; i < controller.fields.length; i++) {
		if (controller.fields[i].key === "explanation") {
			controller.fields.splice(i, 1);
			i--;
		}
	}
}

function handleExplanation(controller, $scope, $translate) {
	removeExplanation(controller);
	$scope.disableSubmitButton = true;
	if (controller.model.currentConfigRevision.secondsAllocated === undefined) {
		if (controller.model.newPtlTimeInSeconds > 0) {
			$scope.disableSubmitButton = false;
			attachDecreaseExplanation(controller, $translate);
		}
	} else {
		if (controller.model.newGranularity !== undefined && controller.model.newPtlTimeInSeconds !== undefined
				&& controller.model.newPtlTimeInSeconds !== controller.model.currentConfigRevision.secondsAllocated) {
			if (controller.model.newPtlTimeInSeconds > controller.model.currentConfigRevision.secondsAllocated) {
				$scope.disableSubmitButton = false;
				attachIncreaseExplanation(controller, $translate);
			} else {
				$scope.disableSubmitButton = false;
				attachDecreaseExplanation(controller, $translate);
			}
		}
	}
}

function returnTranslationForGranularity(granularity) {
	switch (granularity) {
		case 'GRANULARITY_WEEK':
			return 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.WEEKLY';
		case 'GRANULARITY_MONTH':
			return 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.MONTHLY';
		default:
			return 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DAILY';
	}
}

function confirmEditPlayTimeLimitChanges(bsLoadingOverlayService, controller, user, $uibModal, $uibModalInstance, notify) {
	bsLoadingOverlayService.start({referenceId: controller.referenceId});
	let modalInstance = $uibModal.open({
		animation: true,
		ariaLabelledBy: 'modal-title',
		ariaDescribedBy: 'modal-body',
		templateUrl: 'scripts/directives/player/play-time-limits/confirm-change-play-time-limit.html',
		controller: 'ConfirmChangePlayTimeLimitModal',
		controllerAs: 'controller',
		size: 'md',
		resolve: {
			modifyUiModalInstance: function () {
				return $uibModalInstance;
			},
			fullPlayTimeLimitUserData: function () {
				return controller.model;
			},
			user: function () {
				return user;
			},
			loadMyFiles: function ($ocLazyLoad) {
				return $ocLazyLoad.load({
					name: 'lithium',
					files: ['scripts/directives/player/play-time-limits/confirm-change-play-time-limit.js']
				})
			}
		}
	});
	bsLoadingOverlayService.stop({referenceId: controller.referenceId});
	modalInstance.result.then(function (result) {
		if (result) {
			notify.success("GLOBAL.RESPONSE.SUCCESS_FORMS.ADDED_NEW_PLAY_TIME_LIMIT");
		}
	});
}

angular.module('lithium')
.controller('EditPlayTimeLimitsModal',
		['$uibModalInstance', 'playTimeLimitUserData', 'user', '$scope', 'notify', 'errors', '$uibModal', 'bsLoadingOverlayService',
			'userLimitsRest', 'UserRest', 'domainLimitsRest', '$translate',
			function ($uibModalInstance, playTimeLimitUserData, user, $scope, notify, errors, $uibModal, bsLoadingOverlayService, userLimitsRest,
					UserRest, domainLimitsRest, $translate) {
				let controller = this;
				controller.model = playTimeLimitUserData;
				controller.referenceId = 'edit-play-time-limits-v2-overlay';
				controller.options = {};
				controller.model.allGranularities = [];
				controller.fields = [];
				$scope.disableSubmitButton = true;
				let endOfMonth = 31;
				controller.init = function () {

					UserRest.getActivePlayerTimeLimitGranularities().then(function (response) {
						controller.granularity = response;
					});

					if (controller.model.currentConfigRevision !== undefined) {
						controller.model.newGranularity = controller.model.currentConfigRevision.granularity;

						let diff = new moment.duration(controller.model.currentConfigRevision.secondsAllocated, 'seconds');
						controller.model.newPtlDays = diff.days();
						controller.model.newPtlHours = diff.hours();
						controller.model.newPtlMinutes = diff.minutes();
						controller.model.currentPtlDays = diff.months() > 0 ? endOfMonth + diff.days() : diff.days();
						controller.model.currentPtlHours = diff.hours();
						controller.model.currentPtlMinutes = diff.minutes();
					}
				};
				controller.init();

				controller.findGranularity = function (id) {
					return controller.model.allGranularities.find(g => g.id === id);
				}


				controller.getPendingLimitDelayHours = function () {
					let domainSettings = $scope.settings;
					if (domainSettings === undefined) {
						return 168;
					}

					return domainSettings['pending_playtime_limit_update_delay_in_hr'];
				}

				controller.getPendingLimitDelay = function () {
					let hour = controller.getPendingLimitDelayHours();
					let duration = new moment.duration(parseInt(hour), 'hours');

					let moreThanOneHour = duration.hours() > 1 ? 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.HOURS' : 'UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.HOUR.NAME';
					let moreThanOneDay = duration.days() !== 1 ? 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DAYS' : 'GLOBAL.GRANULARITY.DAY';

					return duration.days > 0 ? duration.days() + ' ' + $translate.instant(moreThanOneDay) + ' ' : ''
							+ duration.hours() + ' ' + $translate.instant(moreThanOneHour);
				}

				controller.fields = [
					{
						className: 'col-xs-12',
						key: 'new-ptl-label',
						type: 'h4',
						templateOptions: {
							label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.NEW_PLAY_TIME_LIMIT" | translate',
							description: '',
							placeholder: '',
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.NEW_PLAY_TIME_LIMIT" | translate',
							'templateOptions.placeholder': '',
							'templateOptions.description': ''
						}
					},
					{
						className: 'col-xs-12',
						key: 'newGranularity',
						type: 'ui-select-single',
						templateOptions: {
							valueProp: 'id',
							labelProp: 'name',
							optionsAttr: 'ui-options', "ngOptions": 'ui-options',
							placeholder: '',
							options: [],
							addFormControlClass: true,
							onChange: function($viewValue, $modelValue, $scope) {
								console.log(controller.fields[2].templateOptions);
								controller.model.newGranularity = controller.findGranularity($viewValue);
							}
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CATEGORY.LABEL" | translate',
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.CATEGORY.PLACEHOLDER" | translate'
						},
						controller: ['$scope', function ($scope) {
							UserRest.getActivePlayerTimeLimitGranularities().then(function (response) {
								$scope.to.options = response;
								controller.model.allGranularities = response.plain();
							});
						}]
					},
					{
						className: "row v-reset-row ",
						fieldGroup: [
							{
								className: 'col-xs-4',
								key: 'newPtlDays',
								type: 'input',
								templateOptions: {
									label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DAYS" | translate',
									description: '',
									placeholder: '',
									type: 'number',
									pattern: '([0-9]*)',
									min: 0,
									max: endOfMonth,
									onClick: function ($event) {
										controller.model.newPtlDays = $event;
									}
								},
								defaultValue: controller.model.currentConfigRevision !== undefined ? controller.model.currentPtlDays : 0,
								expressionProperties: {
									'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DAYS" | translate',
									'templateOptions.placeholder': '',
									'templateOptions.description': ''
								}
							},
							{
								className: 'col-xs-4',
								key: 'newPtlHours',
								type: 'input',
								templateOptions: {
									label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.HOURS" | translate',
									description: '',
									placeholder: '',
									type: 'number',
									pattern: '([0-9]*)',
									min: 0,
									max: 23,
									onClick: function ($event) {
										controller.model.newPtlHours = $event;
									}
								},
								defaultValue: controller.model.currentConfigRevision !== undefined ? controller.model.currentPtlHours : 0,
								expressionProperties: {
									'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.HOURS" | translate',
									'templateOptions.placeholder': '',
									'templateOptions.description': ''
								}
							},
							{
								className: 'col-xs-4',
								key: 'newPtlMinutes',
								type: 'input',
								templateOptions: {
									label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.MINUTES" | translate',
									description: '',
									placeholder: '',
									type: 'number',
									pattern: '([0-9]*)',
									min: 0,
									max: 59,
									onClick: function ($event) {
										controller.model.newPtlMinutes = $event;
									}
								},
								defaultValue: controller.model.currentConfigRevision !== undefined ? controller.model.currentPtlMinutes : 0,
								expressionProperties: {
									'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.MINUTES" | translate',
									'templateOptions.placeholder': '',
									'templateOptions.description': ''
								}
							},
							{
								className: 'col-xs-12',
								key: 'new-ptl-description',
								template: '<p class="help-block ng-binding ng-scope">' + $translate.instant(
										"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.TIME_LIMIT_SETTINGS_DESCRIPTION") + '</p>',
							}
						]
					},
				]

				if (controller.model.currentConfigRevision.secondsAccumulated !== undefined) {
					controller.fields.push(
							{
								className: 'col-xs-12',
								key: 'current-ptl-label',
								type: 'h4',
								templateOptions: {
									label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.CURRENT_PLAY_TIME_LIMIT" | translate',
									description: '',
									placeholder: ''
								},
								expressionProperties: {
									'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.CURRENT_PLAY_TIME_LIMIT" | translate',
									'templateOptions.placeholder': '',
									'templateOptions.description': ''
								}
							}
					);
					controller.fields.push(
							{
								className: 'col-xs-12',
								key: 'current-radio-button-daily',
								type: 'ptl-btn-radio-2',
								templateOptions: {
									btnclass: 'default',
									description: "",
									valueProp: 'value',
									labelProp: 'name',
									optionsAttr: 'ui-options', "ngOptions": 'ui-options',
									placeholder: '',
									options: [
										{
											name: $translate.instant(returnTranslationForGranularity(controller.model.currentConfigRevision.granularity.type)),
											value: 0
										}
									],
									readOnly: true,
									addFormControlClass: true
								},
								expressionProperties: {}
							}
					)

					controller.fields.push(
							{
								className: "row v-reset-row ",
								fieldGroup: [
									{
										className: 'col-xs-4',
										key: 'currentPtlDays',
										type: 'input',
										templateOptions: {
											label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DAYS" | translate',
											description: '',
											placeholder: '',
											type: 'number',
											max: (endOfMonth + 1),
											disabled: true
										},
										defaultValue: controller.model.currentPtlDays,
										expressionProperties: {
											'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.DAYS" | translate',
											'templateOptions.placeholder': '',
											'templateOptions.description': ''
										}
									},
									{
										className: 'col-xs-4',
										key: 'currentPtlHours',
										type: 'input',
										templateOptions: {
											label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.HOURS" | translate',
											description: '',
											placeholder: '',
											type: 'number',
											max: 24,
											disabled: true
										},
										defaultValue: controller.model.currentPtlHours,
										expressionProperties: {
											'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.HOURS" | translate',
											'templateOptions.placeholder': '',
											'templateOptions.description': ''
										}
									},
									{
										className: 'col-xs-4',
										key: 'currentPtlMinutes',
										type: 'input',
										templateOptions: {
											label: '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.MINUTES" | translate',
											description: '',
											placeholder: '',
											type: 'number',
											max: 60,
											disabled: true
										},
										defaultValue: controller.model.currentPtlMinutes,
										expressionProperties: {
											'templateOptions.label': '"UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.MINUTES" | translate',
											'templateOptions.placeholder': '',
											'templateOptions.description': ''
										}
									},
									{
										className: 'col-xs-12',
										key: 'new-ptl-description',
										template: '<p class="help-block ng-binding ng-scope">' + $translate.instant(
												'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.POP_UP_DIALOG.CURRENT_TIME_LIMIT_SETTINGS_DESCRIPTION') + '</p>',
									}
								]
							}
					)
				}

				$scope.$watch('controller.model.newGranularity', function () {
					if (controller.model.firstInitExecuted) {
						controller.model.newPtlDays = 0;
						controller.model.newPtlHours = 0;
						controller.model.newPtlMinutes = 0;
					}
					controller.model.firstInitExecuted = true;
				});
				function pushToHandler() {
					controller.model.newPtlTimeInSeconds = moment.duration(controller.model.newPtlDays, 'days').asSeconds()
							+ moment.duration(controller.model.newPtlHours, 'hours').asSeconds()
							+ moment.duration(controller.model.newPtlMinutes, 'minutes').asSeconds();

					handleExplanation(controller, $scope, $translate);
				}
				$scope.$watch('controller.model.newPtlDays', function (newValue, oldValue) {
					let limit = 1;
					if (controller.model.newGranularity.type === 'GRANULARITY_DAY') {
						limit = 1;
					} else if (controller.model.newGranularity.type === 'GRANULARITY_WEEK') {
						limit = 7;
					} else if (controller.model.newGranularity.type === 'GRANULARITY_MONTH') {
						limit = endOfMonth;
					}
					if (newValue === undefined && newValue > limit) {
						controller.model.newPtlDays = 0;
						if (controller.model.newGranularity.type === 'GRANULARITY_DAY') {
							notify.error("GLOBAL.RESPONSE.WARNING_FORMS.OUT_OF_DAILY_LIMIT_WARNING");
						} else if (controller.model.newGranularity.type === 'GRANULARITY_WEEK') {
							notify.error("GLOBAL.RESPONSE.WARNING_FORMS.OUT_OF_WEEKLY_LIMIT_WARNING");
						} else if (controller.model.newGranularity.type === 'GRANULARITY_MONTH') {
							notify.error("GLOBAL.RESPONSE.WARNING_FORMS.OUT_OF_MONTHLY_LIMIT_WARNING");
						}
						return;
					}
					pushToHandler();
				});

				$scope.$watch('controller.model.newPtlHours', function (newValue, oldValue) {
					if (newValue === undefined) {
						controller.model.newPtlHours = 0;
						notify.error("GLOBAL.RESPONSE.WARNING_FORMS.OUT_OF_HOURS_LIMIT_WARNING");
						return;
					}
					pushToHandler();
				});

				$scope.$watch('controller.model.newPtlMinutes', function (newValue, oldValue) {
					if (newValue === undefined) {
						controller.model.newPtlMinutes = 0;
						notify.error("GLOBAL.RESPONSE.WARNING_FORMS.OUT_OF_MINUTES_LIMIT_WARNING");
						return;
					}
					pushToHandler();
				});

				controller.confirm = function () {
					const dayInSeconds = 86400;
					const weekInSeconds = 604800;
					const monthInSeconds = 2678400;

					if (controller.model.currentConfigRevision.secondsAccumulated !== undefined
							&& controller.model.newGranularity.type === controller.model.currentConfigRevision.granularity.type
							&& controller.model.newPtlTimeInSeconds === controller.model.currentConfigRevision.secondsAllocated) {
						notify.error("GLOBAL.RESPONSE.ERROR_FORMS.EQUAL_NEW_PTL_AND_CURRENT_PTL_ERROR");
						return;
					}

					if (controller.model.currentConfigRevision.secondsAccumulated === undefined) {
						if ((controller.model.newGranularity.type === 'GRANULARITY_DAY' && controller.model.newPtlTimeInSeconds <= dayInSeconds)
								|| (controller.model.newGranularity.type === 'GRANULARITY_WEEK' && controller.model.newPtlTimeInSeconds <= weekInSeconds)
								|| (controller.model.newGranularity.type === 'GRANULARITY_MONTH' && controller.model.newPtlTimeInSeconds <= monthInSeconds)) {
							confirmEditPlayTimeLimitChanges(bsLoadingOverlayService, controller, user, $uibModal, $uibModalInstance);
							return;
						}
					} else {
						if (controller.model.newPtlTimeInSeconds !== controller.model.currentConfigRevision.secondsAllocated
								|| controller.model.newGranularity !== controller.model.currentConfigRevision.granularity) {
							if ((controller.model.newGranularity.type === 'GRANULARITY_DAY' && controller.model.newPtlTimeInSeconds <= dayInSeconds)
									|| (controller.model.newGranularity.type === 'GRANULARITY_WEEK' && controller.model.newPtlTimeInSeconds <= weekInSeconds)
									|| (controller.model.newGranularity.type === 'GRANULARITY_MONTH' && controller.model.newPtlTimeInSeconds <= monthInSeconds)) {
								confirmEditPlayTimeLimitChanges(bsLoadingOverlayService, controller, user, $uibModal, $uibModalInstance);
								return;
							}
						}
					}

					if (controller.model.newGranularity.type === 'GRANULARITY_DAY') {
						if ((controller.model.newPtlDays === 1 && (controller.model.newPtlHours > 0 || controller.model.newPtlMinutes > 0))) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.DAILY_DECREASE_HOURS_AND_MINUTES_ERROR");
						} else if (controller.model.newPtlHours === 24 && (controller.model.newPtlDays > 0 || controller.model.newPtlMinutes > 0)) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.DAILY_DECREASE_DAYS_AND_MINUTES_ERROR");
						} else if (controller.model.newPtlHours === 23 && controller.model.newPtlMinutes === 60 && controller.model.newPtlDays > 0) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.DAILY_DECREASE_DAYS_ERROR");
						}
					} else if (controller.model.newGranularity.type === 'GRANULARITY_WEEK') {
						if ((controller.model.newPtlDays === 7 && (controller.model.newPtlHours > 0 || controller.model.newPtlMinutes > 0))) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.WEEKLY_DECREASE_HOURS_AND_MINUTES_ERROR");
						} else if (controller.model.newPtlHours === 24 && (controller.model.newPtlDays > 6 || controller.model.newPtlMinutes > 0)) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.WEEKLY_DECREASE_DAYS_AND_MINUTES_ERROR");
						} else if (controller.model.newPtlHours === 23 && controller.model.newPtlMinutes === 60 && controller.model.newPtlDays > 6) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.WEEKLY_DECREASE_DAYS_ERROR");
						}
					} else if (controller.model.newGranularity.type === 'GRANULARITY_MONTH') {
						if ((controller.model.newPtlDays === endOfMonth && (controller.model.newPtlHours > 0 || controller.model.newPtlMinutes > 0))) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.MONTHLY_DECREASE_HOURS_AND_MINUTES_ERROR");
						} else if (controller.model.newPtlHours === 24 && (controller.model.newPtlDays > (endOfMonth - 1) || controller.model.newPtlMinutes > 0)) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.MONTHLY_DECREASE_DAYS_AND_MINUTES_ERROR");
						} else if (controller.model.newPtlHours === 23 && controller.model.newPtlMinutes === 60 && controller.model.newPtlDays > (endOfMonth - 1)) {
							notify.error("GLOBAL.RESPONSE.ERROR_FORMS.MONTHLY_DECREASE_DAYS_ERROR");
						}
					}
				};

				controller.cancel = function () {
					$uibModalInstance.dismiss('cancel');
				};
			}
		]
);