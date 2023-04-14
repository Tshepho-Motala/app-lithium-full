'use strict'

angular.module('lithium').controller('RestrictionsAddController', ['restrictionTypes', 'outcomeActions', '$state', '$userService', '$uibModal', 'bsLoadingOverlayService', 'errors', 'notify', "$scope", 'RestrictionsRest', 'EmailTemplateRest',
	function (restrictionTypes, outcomeActions, $state, $userService, $uibModal, bsLoadingOverlayService, errors, notify, $scope, rest, emailTemplateRest) {
		var controller = this;

		controller.restrictionTypes = restrictionTypes.plain();
		controller.liftActions = outcomeActions.plain();
		controller.placeActions = outcomeActions.plain();
		controller.model = {restrictions: []};
		controller.model.domain = {};
		controller.fields = [
			{
				className: 'col-xs-12 col-md-6',
				key: "domain.name",
				type: "ui-select-single",
				templateOptions: {
					label: "Domain",
					description: "Choose the domain which you are creating the restriction for",
					required: true,
					optionsAttr: 'bs-options',
					valueProp: 'name',
					labelProp: 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.DOMAIN.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
				},
				controller: ['$scope', function ($scope) {
					$scope.to.options = $userService.domainsWithRole("RESTRICTIONS_ADD");
				}]
			}, {
				className: 'col-xs-12 col-md-6',
				key: "name",
				type: "input",
				templateOptions: {
					label: "Name",
					description: "Add a unique name for the restriction",
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.NAME.DESCRIPTION" | translate'
				}
			}, {
				className: 'col-xs-12 col-md-6',
				type: 'checkbox2',
				key: 'enabled',
				templateOptions: {
					label: 'Enabled',
					fontWeight: 'bold',
					description: 'Should the restriction be enabled?',
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.ENABLED.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.ENABLED.DESCRIPTION" | translate'
				}
			}, {
				className: 'col-xs-12 col-md-6',
				type: 'checkbox2',
				key: 'dwhVisible',
				templateOptions: {
					label: 'DWH Visible',
					fontWeight: 'bold',
					description: 'Should the restriction be accessible by DWH?',
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.DWH_VISIBLE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.DWH_VISIBLE.DESCRIPTION" | translate'
				}
			}, {
				className: 'top-space-10 col-xs-12 col-md-6 col-md-offset-6',
				type: 'checkbox2',
				key: 'communicateToPlayer',
				templateOptions: {
					label: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.COMMUNICATE_TO_PLAYER.NAME" | translate',
					fontWeight: 'bold',
					description: 'Should this be communicated to a player',
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.COMMUNICATE_TO_PLAYER.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.COMMUNICATE_TO_PLAYER.DESCRIPTION" | translate'
				}
			}, {
				className: 'col-xs-12 col-md-6',
				key: "placeMailTemplate",
				type: "ui-select-single",
				templateOptions: {
					label: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_MAIL_TEMPLATE.NAME" | translate',
					description: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_MAIL_TEMPLATE.DESCRIPTION" | translate',
					required: false,
					valueProp: 'name',
					labelProp: 'name',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					placeholder: '',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_MAIL_TEMPLATE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_MAIL_TEMPLATE.DESCRIPTION" | translate'
				},
				controller: ['$scope', function ($scope) {
					$scope.$watch('model.domain.name', function (newValue, oldValue, theScope) {
						if (newValue !== oldValue) {
							if ($scope.model[$scope.options.key] && oldValue) {
								$scope.model[$scope.options.key] = '';
							}
							emailTemplateRest.findByDomainName(newValue).then(function (response) {
								$scope.to.options = response;
							}).catch(function (error) {
								notify.error("UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_MAIL_TEMPLATE.ERROR");
								errors.catch("", false)(error)
							}).finally(function () {
							});
						}
					});
				}]
			}, {
				className: 'col-xs-12 col-md-6',
				key: "liftMailTemplate",
				type: "ui-select-single",
				templateOptions: {
					label: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_MAIL_TEMPLATE.NAME" | translate',
					description: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_MAIL_TEMPLATE.DESCRIPTION" | translate',
					required: false,
					valueProp: 'name',
					labelProp: 'name',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					placeholder: '',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_MAIL_TEMPLATE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_MAIL_TEMPLATE.DESCRIPTION" | translate'
				},
				controller: ['$scope', function ($scope) {
					$scope.$watch('model.domain.name', function (newValue, oldValue, theScope) {
						if (newValue !== oldValue) {
							if ($scope.model[$scope.options.key] && oldValue) {
								$scope.model[$scope.options.key] = '';
							}
							emailTemplateRest.findByDomainName(newValue).then(function (response) {
								$scope.to.options = response;
							}).catch(function (error) {
								notify.error("UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_MAIL_TEMPLATE.ERROR");
								errors.catch("", false)(error)
							}).finally(function () {
							});
						}
					});
				}]
			}, {
				className: 'col-xs-12 col-md-6',
				key: "placeActions",
				type: "ui-select-multiple",
				templateOptions: {
					label: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_OUTCOME_ACTIONS.NAME" | translate',
					description: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_OUTCOME_ACTIONS.DESCRIPTION" | translate',
					required: false,
					valueProp: 'code',
					labelProp: 'code',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					placeholder: '',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_OUTCOME_ACTIONS.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_OUTCOME_ACTIONS.DESCRIPTION" | translate'
				},
				controller: ['$scope', function ($scope) {
					$scope.to.options = controller.placeActions;
				}]
			}, {
				className: 'col-xs-12 col-md-6',
				key: "liftActions",
				type: "ui-select-multiple",
				templateOptions: {
					label: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.NAME" | translate',
					description: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.DESCRIPTION" | translate',
					required: false,
					valueProp: 'code',
					labelProp: 'code',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					placeholder: '',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.DESCRIPTION" | translate',
				},
				controller: ['$scope', function ($scope) {
					$scope.to.options = controller.liftActions;
				}]
			}
		];

		// Removing restriction types that are already added
		controller.getEligibleRestrictionTypes = function () {
			var restrictionTypes = angular.copy(controller.restrictionTypes);
			for (var i = 0; i < controller.model.restrictions.length; i++) {
				for (var k = 0; k < restrictionTypes.length; k++) {
					if (controller.model.restrictions[i].restriction.code == restrictionTypes[k].code) {
						restrictionTypes.splice(k, 1);
					}
				}
			}
			return restrictionTypes;
		}

		controller.getRestrictionTypeName = function (code) {
			for (var i = 0; i < controller.restrictionTypes.length; i++) {
				if (controller.restrictionTypes[i].code === code) return controller.restrictionTypes[i].name;
			}
			return code;
		}

		controller.addRestriction = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.html',
				controller: 'RestrictionsRestrictionModal',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					set: function () {
						return null
					},
					restriction: function () {
						return null;
					},
					restrictionTypes: function () {
						return controller.getEligibleRestrictionTypes();
					},
					loadMyFiles: function ($ocLazyLoad) {
						return $ocLazyLoad.load({
							name: 'lithium',
							files: [
								'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.js'
							]
						})
					}

				}
			});

			modalInstance.result.then(function (response) {
				controller.model.restrictions.push(response);
			});
		}

		controller.modifyRestriction = function ($index) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.html',
				controller: 'RestrictionsRestrictionModal',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					set: function () {
						return null
					},
					restriction: function () {
						return angular.copy(controller.model.restrictions[$index]);
					},
					restrictionTypes: function () {
						return controller.restrictionTypes;
					},
					loadMyFiles: function ($ocLazyLoad) {
						return $ocLazyLoad.load({
							name: 'lithium',
							files: [
								'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.js'
							]
						})
					}

				}
			});

			modalInstance.result.then(function (response) {
				controller.model.restrictions[$index] = response;
			});
		}

		controller.removeRestriction = function ($index) {
			controller.model.restrictions.splice($index, 1);
		}

		$scope.$watch(function () {
			return controller.model.domain
		}, function (newValue, oldValue) {
			if (oldValue !== newValue) {
				console.log(newValue)
			}
		});

		controller.onSubmit = function () {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			bsLoadingOverlayService.start({referenceId: "loading"});
			rest.domainRestrictionSetCreate(controller.model)
				.then(function (response) {
					if (response._status !== 0) {
						notify.error(response._message);
					} else {
						var data = response.plain();
						notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.ADD.SUCCESS");
						$state.go("^.view", {id: data.id});
					}
				}).catch(
				errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.ADD.ERROR", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId: "loading"});
			});
		}
	}
]);