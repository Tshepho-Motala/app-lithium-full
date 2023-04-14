'use strict';

angular.module('lithium')
.directive('playTimeLimits', function () {
	return {
		templateUrl: 'scripts/directives/player/play-time-limits/play-time-limits.html',
		scope: {
			domain: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		controllerAs: 'controller',
		controller: ['$uibModal', '$translate', '$scope', 'notify', 'errors',
			'bsLoadingOverlayService', 'UserRest',
			function ($uibModal, $translate, $scope, notify, errors,
					bsLoadingOverlayService,
					UserRest) {
				let controller = this;
				let endOfMonth = 31;
				$scope.referenceId = 'playTimeLimitsV2-overlay';
				$scope.model = {};
				$scope.model.ptlUserData = {};
				$scope.model.ptlUserData.currentConfigRevision = {};
				$scope.model.ptlUserData.currentConfigRevision.granularity = {
					name: $translate.instant(
							'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.DAILY')
				};
				$scope.model.ptlUserData.secondsAccumulated = 5;
				controller.arePlayTimeLimitSettingsActive = function () {
					if ($scope.domain.playtimeLimit !== undefined) {
						return $scope.domain.playtimeLimit;
					} else {
						return false;
					}
				}

				controller.updateAndGetPlayerEntry = function () {

					UserRest.updateAndGetPlayerEntryHttp($scope.user.guid).then(
							function (result) {
								if (result.status === 200 && result.data != null) {
									$scope.model.ptlUserData.secondsAccumulated = result.data.secondsAccumulated;
								}
							});
				}

				controller.init = function () {

					UserRest.getPlayerPlayTimeLimitConfigHttp($scope.user.guid, $scope.user.domain.name).then(
							function (response) {
								if (response !== undefined && response.status === 200) {
									if(response.data !== ""){
										$scope.model.ptlUserData = response.data;
										controller.updateAndGetPlayerEntry();
									}
								} else {
									errors.catch(
											'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.ERRORS.RETRIEVE_LIMIT',
											false);
								}
							});

				};
				controller.init();
				controller.refresh = function () {
					controller.init();
				}

				$scope.translateGranularity = function (type) {
					let translation;
					switch (type) {
						case 'GRANULARITY_DAY':
							translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.DAILY';
							break;
						case 'GRANULARITY_WEEK':
							translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.WEEKLY';
							break;
						case 'GRANULARITY_MONTH':
							translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.MONTHLY';
							break;
						default:
							translation = 'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.TABLE.NOT_SET';
							break;
					}
					const translated = $translate.instant(translation);

					$scope.model.ptlUserData.currentConfigRevision.granularity.name = translated;

					return translated;
				}
				//
				controller.applyFullDateTimeFormat = function (dateToModify) {
					return moment.unix(dateToModify).utc().format('MMM D, YYYY HH:mm:ss A');
				}

				controller.editPlayTimeLimits = function () {
					controller.refresh();
					const modalInstance = $uibModal.open({
						animation: true,
						ariaLabelledBy: 'modal-title',
						ariaDescribedBy: 'modal-body',
						templateUrl: 'scripts/directives/player/play-time-limits/edit-play-time-limits.html',
						controller: 'EditPlayTimeLimitsModal',
						controllerAs: 'controller',
						backdrop: 'static',
						size: 'md',
						resolve: {
							playTimeLimitUserData: function () {
								return $scope.model.ptlUserData ?? undefined;
							},
							user: function () {
								return $scope.user;
							},
							loadMyFiles: function ($ocLazyLoad) {
								return $ocLazyLoad.load({
									name: 'lithium',
									files: ['scripts/directives/player/play-time-limits/edit-play-time-limits.js']
								})
							}
						}
					});

					modalInstance.result.then(function (result) {
						if (result) {
							notify.success(
									"GLOBAL.RESPONSE.SUCCESS_FORMS.ADDED_NEW_PLAY_TIME_LIMIT");
							controller.init();
						}
					});
				};

				$scope.secondsToHumanTime = function (seconds) {
					if (seconds < 0) {
						seconds = 0;
					}
					let diff = new moment.duration(seconds, 'seconds');

					if(diff.months() > 0){
						return endOfMonth + 'd ' + diff.hours() + 'h ' + diff.minutes() + 'm ' + diff.seconds() + 's ';
					}
					return diff.days() + 'd ' + diff.hours() + 'h '
							+ diff.minutes() + 'm ' + diff.seconds() + 's ';
				}

				controller.removePendingConfigPlayTimeLimit = function (id) {
					UserRest.removePendingConfigPlayTimeLimit(id).then(
							function () {
								notify.success('GLOBAL.RESPONSE.SUCCESS_FORMS.PENDING_PLAY_TIME_LIMIT_REMOVED');
								controller.refresh();
							});
				}

			}]
	}
});
