'use strict';

angular.module('lithium')
	.controller('DomainClientsViewController', ["client", "domain", "$scope", "$translate", "$state", "notify", "rest-provider-auth-client",
	function(client, domain, $scope, $translate, $state , notify, restProviderAuthClient) {
		var controller = this;
		console.log(client.plain());
		controller.model = client.plain();
		controller.modelOriginal = angular.copy(client.plain());
		controller.options = { formState: { readOnly: true } };
		
		controller.fields = [{
			className: "col-xs-12",
			key: "guid",
			type: "input",
			optionsTypes: [],
			templateOptions: {
				label: "Guid", description: "", placeholder: "",
				required: false, minlength: 2, maxlength: 255, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.GUID.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.GUID.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.GUID.DESCRIPTION" | translate'
			}
		},{
			className: "col-xs-12",
			key: "code",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "Code", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 205, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.CODE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.CODE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.CODE.DESCRIPTION" | translate'
			}
		},
		{
			className: "col-xs-12",
			key: "description",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "description", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 255, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},
		{
			className: "col-xs-12",
			key: "password",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "Password", description: "", placeholder: "",
				required: true, minlength: 1, maxlength: 255, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.PASSWORD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.PASSWORD.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.PASSWORD.DESCRIPTION" | translate'
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
			entityId: client.id,
			restService: restProviderAuthClient,
			reload: 0
		}

		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			restProviderAuthClient.save(domain.name, controller.model).then(function(response) {
				console.log(response.plain());
				notify.success("UI_NETWORK_ADMIN.DOMAIN.CLIENTS.MSG2");
				controller.model = response.plain();
				controller.modelOriginal = angular.copy(response.plain());
				controller.options.formState.readOnly = true;
				controller.changelogs.reload += 1;
			});
		}

		controller.deleteProviderAuthClient = function() {
			$translate('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.DELETE.CONFIRM').then(function(response) {
				if (window.confirm(response)) {
					restProviderAuthClient.delete(domain.name, controller.model.id).then(function(response) {
						if (response._status !== 0) {
							notify.error('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.DELETE.ERROR');
						} else {
							notify.success('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.DELETE.SUCCESS');
							$state.go("dashboard.domains.domain.clients")
						}
					}).catch(function() {
						errors.catch('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.DELETE.ERROR', false);
					}).finally(function() {
					});
				}
			}).catch(function() {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR');
			});
		}
	}
]);