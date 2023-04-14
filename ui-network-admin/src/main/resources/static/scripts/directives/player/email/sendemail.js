'use strict';

angular.module('lithium').controller('SendEmailTemplateModal', ['user', '$rootScope', '$translate', '$uibModalInstance', '$scope', 'notify', 'errors',  'EmailTemplateRest',
			function (user, $rootScope, $translate, $uibModalInstance, $scope, notify, errors,  emailTemplateRest) {
				var controller = this;

				controller.options = {};
				controller.model = {};

				controller.fields = [
					{
						className: 'col-xs-12',
						key: "sendTemplate",
						type: "ui-select-single",
						templateOptions: {
							label: "",
							description: "",
							valueProp: 'value',
							labelProp: 'label',
							optionsAttr: 'ui-options',ngOptions: 'ui-options',
							options: [],
							placeholder: '',
							required: true
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.SEND_TEMPLATE.AVAILABLE_TEMPLATES_LABEL" | translate',
						},
						controller: ['$scope', function($scope) {
							emailTemplateRest.findByDomainName(user.domain.name).then(function(response) {
								var options = [];
								var templates = response.plain();
								for (var i = 0; i < templates.length; i++) {
									var template = templates[i];
									options.push({label: template.name, value: template});
								}
								$scope.to.options = options;
							});
						}]
					},
				];



				controller.onSubmit = function() {
					if (controller.form.$invalid) {
						angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
						notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
						return false;
					}

					emailTemplateRest.sendEmailTemplate(controller.model.sendTemplate.id, user.guid, user.id, user.email).then(function(response) {
						if (response._status !== undefined && response._status !== 0) {
							notify.error("Failed to queue email for player. " + response._message);
							$uibModalInstance.close();
						} else {
							notify.success("Successfully queued email for player.");
							$uibModalInstance.close(response);
						}
					}).catch(function(error) {
						console.error(error);
						notify.error("Failed to queue email for player.");
						errors.catch("", false)(error)
					});
				}

				controller.cancel = function() {
					$uibModalInstance.dismiss('cancel');
				};

				// Mail Vue Preview
				$rootScope.provide.quickActionProvider['user'] = user
				window.VuePluginRegistry.loadByPage("MailSendDialog")
			}
		]
	);
