'use strict';

angular.module('lithium')
	.controller('DomainClosureReasonsViewController', ["closurereason", "domain", "$scope", "$translate", "$state", "notify", "ClosureReasonsRest",
	"$uibModal", function(closurereason, domain, $scope, $translate, $state , notify, closureReasonsRest, $uibModal) {
		var controller = this;
		console.log(closurereason.plain());
		controller.model = closurereason.plain();
		controller.modelOriginal = angular.copy(closurereason.plain());
		controller.options = { formState: { readOnly: true } };

		controller.fields = [
		{
			className: "col-xs-12",
			key: "description",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "description", description: "", placeholder: "",
				required: false, minlength: 2, maxlength: 255, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},
		{
			className: "col-xs-12",
			key: "text",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "Password", description: "", placeholder: "",
				required: true, minlength: 1, maxlength: 255, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.TEXT.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.TEXT.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.TEXT.DESCRIPTION" | translate'
			}
		}];

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

		controller.changelogs = {
			domainName: domain.name,
			entityId: closurereason.id,
			restService: closureReasonsRest,
			reload: 0
		}

		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			closureReasonsRest.save(domain.name, controller.model).then(function(response) {
				console.log(response.plain());
				notify.success("UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.MSG1");
				controller.model = response.plain();
				controller.modelOriginal = angular.copy(response.plain());
				controller.options.formState.readOnly = true;
				controller.changelogs.reload += 1;
			});
		}

		controller.deleteClosureReason = function() {
			let modalInstance  = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/closurereasons/delete/confirmdeletestatusreason.html',
				controller: 'ConfirmDeleteStatusReason',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					controllerModel : function() {
						return controller.model;
					},
					closureReasonsRest : function () {
						return closureReasonsRest;
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name: 'lithium',
							files: ['scripts/controllers/dashboard/domains/domain/closurereasons/delete/confirmdeletestatusreason.js']
						});
					}
				}
			});
			modalInstance.result.then(function(response) {
				notify.success('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.DELETE.SUCCESS');
				$state.go("dashboard.domains.domain.closurereasons");
			});
		}
	}
]);
