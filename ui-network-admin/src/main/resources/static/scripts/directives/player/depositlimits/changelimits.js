'use strict';

angular.module('lithium')
.controller('ChangeDepositLimitsModal',
['$uibModalInstance', 'user', 'limit', 'limitUsed', 'granularity', '$scope', 'notify', 'errors', '$uibModal','bsLoadingOverlayService', 'userLimitsRest', 'domainLimitsRest', '$translate', 'rest-domain',
function ($uibModalInstance, user, limit, limitUsed, granularity, $scope, notify, errors, $uibModal ,bsLoadingOverlayService, userLimitsRest, domainLimitsRest, $translate, restDomain) {
	var controller = this;

	controller.user = user;

	controller.referenceId = 'changeuserdepositlimits-overlay';
	// $locale.NUMBER_FORMATS.CURRENCY_SYM

	controller.options = {};
	controller.model = {};
	controller.fields = [];

	controller.model.limit = limit;
	controller.model.limitUsed = limitUsed;

	controller.fields = [{
		className: 'col-xs-12',
		key: 'limit',
		type: 'ui-money-mask',
		templateOptions: {
			label: '',
			description: '',
			placeholder: '',
			required: true,
			addFormControlClass: true
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.LIMIT.'+granularity+'.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.LIMIT.'+granularity+'.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.LIMIT.'+granularity+'.DESCRIPTION" | translate'
		}
	},{
		className: 'col-xs-12',
		key: 'limitUsed',
		type: 'ui-money-mask',
		templateOptions: {
			label: '',
			description: '',
			placeholder: '',
			required: false,
			readOnly: true,
			disabled: true,
			addFormControlClass: true
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.USED.'+granularity+'.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.USED.'+granularity+'.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.USED.'+granularity+'.DESCRIPTION" | translate'
		}
	}, {
		className: 'col-xs-12',
		key: "explanation",
		type: "examplewell",
		templateOptions: {
			label: "",
			explain: ""
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DEPOSITLIMITS.EXPLANATION" | translate',
			'templateOptions.explain': function(viewValue, modelValue, $scope) {
				if (controller.model.limit <= limit) {
					$translate("UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.EXPLAIN.SMALLER").then(function success(translate) {
						$scope.options.templateOptions.explain = translate;
					});
				} else {
					restDomain.findCurrentDomainSetting(controller.user.guid.split('/')[0], 'default-deposit-limit-pending-periods-in-hr').then(function (resp) {
						var pendingPeriod = resp.id === undefined ? 24 : resp.labelValue.value;
						$scope.options.templateOptions.explain = $translate.instant("UI_NETWORK_ADMIN.DEPOSITLIMITS.CHANGE.EXPLAIN.LARGER_WITH_TIME", {time: pendingPeriod} );
					});
				}
			}
		}
	}];

	controller.confirm = function() {
		if(controller.model.limit < limit) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/directives/player/depositlimits/confirmchangelimit.html',
				controller: 'ConfirmDepositUpdateModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					modifyuiModalInstance: function() {
						return $uibModalInstance;
					},
					granularity: function() {
						return granularity;
					},
					userLimitsRest: function() {
						return userLimitsRest;
					},
					userData: function() {
						return user;
					},
					limit: function() {
						return controller.model.limit;
					},
					limitUsed: function() {
						return limit;
					},
					loadMyFiles: function ($ocLazyLoad) {
						return $ocLazyLoad.load({
						  name: 'lithium',
						  files: ['scripts/directives/player/depositlimits/confirmchangelimit.js']
					  })
					}
				}
			});
		} else {

				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				if (controller.form.$invalid) {
					angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
					notify.warning("UI_NETWORK_ADMIN.DEPOSITLIMITS.SAVE.SUCCESS");
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
					return false;
				}
		
				userLimitsRest.depositLimitSave(user.guid, granularity, controller.model.limit).then(function(response) {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
					if (response._successful) {
						notify.success('UI_NETWORK_ADMIN.DEPOSITLIMITS.SAVE.SUCCESS');
						$uibModalInstance.close(response);
					} else {
						notify.error(response._message);
						notify.error('UI_NETWORK_ADMIN.DEPOSITLIMITS.SAVE.ERROR');
						$uibModalInstance.close();
					}
				}).catch(function(error) {
					console.log(error);
					errors.catch('', false);
				});
			};
		};
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}]);

