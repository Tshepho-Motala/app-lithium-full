'use strict';

angular.module('lithium').controller('RestrictionSetPlaceTemplateModal', ["set", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "RestrictionsRest", "EmailTemplateRest",
	function (set,  errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest, emailTemplateRest) {
		var controller = this;
		controller.model = {placeMailTemplate: set.placeMailTemplate};

		controller.fields = [
			{
				className: 'col-xs-12 col-md-12',
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
					options: [],
				},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_MAIL_TEMPLATE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.PLACE_MAIL_TEMPLATE.DESCRIPTION" | translate',
		},
				controller: ['$scope', function($scope) {
					emailTemplateRest.findByDomainName(set.domain.name).then(function (response) {
						$scope.to.options = response;
					})
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
			rest.domainRestrictionSetMailTemplate(set.id, controller.model.placeMailTemplate, true)
				.then(function (response) {
					if (response._status !== 0) {
						notify.error(response._message);
					} else {
						notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.CHANGE_TEMPLATE.SUCCESS");
						$uibModalInstance.close(response);
					}
				}).catch(
				errors.catch("UI_NETWORK_ADMIN.RESTRICTIONS.CHANGE_TEMPLATE.ERROR", false)
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
