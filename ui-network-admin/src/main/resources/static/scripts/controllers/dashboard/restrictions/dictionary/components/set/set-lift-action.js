'use strict';

angular.module('lithium').controller('RestrictionSetActionLiftModal', ["set", 'outcomeActions', "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "RestrictionsRest",
	function (set, outcomeActions, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest) {
		var controller = this;
		let wrapper = []
		set.liftActions.forEach(el => {
			wrapper.push({code: el.code})
		})
		controller.model = {actions: wrapper};
		controller.liftActionsList = outcomeActions.plain();

		controller.fields = [
			{
				className: 'col-xs-12 col-md-12',
				key: "actions",
				type: "ui-select-multiple",
				templateOptions: {
					label: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.DESCRIPTION" | translate',
					description: '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.DESCRIPTION" | translate',
					required: false,
					valueProp: 'code',
					labelProp: 'code',
					optionsAttr: 'ui-options',
					ngOptions: 'ui-options',
					placeholder: '',
					options: [],
				},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.LIFT_OUTCOME_ACTIONS.DESCRIPTION" | translate',
		},
				controller: ['$scope', function($scope) {
					$scope.to.options = controller.liftActionsList;
				}]
			}
		];

		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			bsLoadingOverlayService.start({referenceId: "loading"});

			let data = new Array();
			controller.model.actions.forEach(el => {
				data.push(el.code)
			})

			rest.domainRestrictionSetChangeLiftActions(set.id, data)
				.then(function (response) {
					if (response._status !== 0) {
						notify.error(response._message);
					} else {
						notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.CHANGE_ACTIONS.SUCCESS");
						$uibModalInstance.close(response);
					}
				}).catch(
				errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.CHANGE_ACTIONS.ERROR", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId: "loading"});
			});

			bsLoadingOverlayService.stop({referenceId: "loading"});
		}

		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		}
	}
]);