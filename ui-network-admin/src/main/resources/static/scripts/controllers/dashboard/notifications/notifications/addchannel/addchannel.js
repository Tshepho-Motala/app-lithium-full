'use strict';

angular.module('lithium').controller('NotificationsAddChannelModal', ["domainName", "channels", "notification", "channel", "errors", "$scope", "notify", "$uibModalInstance", "NotificationRest", "EmailTemplateRest", "SMSTemplateRest", "TemplatesRest", "PushMsgTemplateRest",
function (domainName, channels, notification, channel, errors, $scope, notify, $uibModalInstance, notificationRest, emailTemplateRest, smsTemplateRest, templatesRest, pushmsgTemplateRest,
		) {
	var controller = this;
	
	if (channel != null) controller.model = channel;
	else controller.model = { templateLang: "en"};
	
	controller.fields = [
		{
			className: 'row',
			fieldGroup: [
				{
					className : 'col-xs-12',
					key: "channel.name",  
					type: "ui-select-single",
					templateOptions : {
						label: "",
						description: "",
						placeholder: "",
						valueProp: 'name',
						labelProp: 'name',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: [],
						required: true
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.CHANNEL.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.CHANNEL.DESCRIPTION" | translate'
					},
					controller: ['$scope', function($scope) {
						$scope.to.options = channels;
					}]
				}, {
					className: "col-xs-12",
					key: "templateLang",
					type: "ui-select-single",
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: true,
						valueProp : 'locale2',
						labelProp : 'description',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: []
					},
					hideExpression: function($viewValue, $modelValue, scope) {
						return (angular.isDefined(scope.model.channel) && ['PUSH', 'PULL'].includes(scope.model.channel.name))
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.TEMPLATELANG.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.TEMPLATELANG.DESCRIPTION" | translate'
					},
					controller: ['$scope', '$http', function($scope, $http) {
						$http.get("services/service-translate/apiv1/languages/all").then(function(response) {
							$scope.to.options = response.data;
						});
					}]
				},
				{
					className : 'col-xs-12',
					key: "templateName",
					type: "ui-select-single",
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: true,
						valueProp : 'name',
						labelProp : 'name',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: []
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.TEMPLATE.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.TEMPLATE.DESCRIPTION" | translate'
					},
					hideExpression: function($viewValue, $modelValue, scope) {
						return (angular.isDefined(scope.model.channel) && ['PULL'].includes(scope.model.channel.name))
					},
				}
			]
		}, {
			type: 'checkbox',
			key: 'forced',
			templateOptions: {
				label: '', description: ''
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.FORCED.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.CHANNEL.FORCED.DESCRIPTION" | translate'
			},
			hideExpression: function($viewValue, $modelValue, scope) {
				return (angular.isDefined(scope.model.channel) && ['PULL'].includes(scope.model.channel.name))
			},
		}
	];
	
	controller.setTemplates = function() {
		if (domainName !== undefined && domainName !== null && domainName !== '') {
			if (controller.model.channel !== undefined && controller.model.channel !== null &&
					controller.model.channel.name !== undefined && controller.model.channel.name !== null) {
				switch (controller.model.channel.name) {
					case "SMS":
						smsTemplateRest.findByDomainNameAndLang(domainName, controller.model.templateLang).then(function(response) {
							var options = [];
							options = response.plain();
							if (options != undefined) {
								controller.fields[0].fieldGroup[2].templateOptions.options = options;
							}
						});
						break;
					case "EMAIL":
						emailTemplateRest.findByDomainNameAndLang(domainName, controller.model.templateLang).then(function(response) {
							var options = [];
							options = response.plain();
							if (options != undefined) {
								controller.fields[0].fieldGroup[2].templateOptions.options = options;
							}
						});
						break;
					case "PUSH":
						pushmsgTemplateRest.list(domainName).then(function(response) {
							var options = [];
							options = response.plain();
							if (options != undefined) {
								controller.fields[0].fieldGroup[2].templateOptions.options = options;
							}
						});
						break;
					default: break;
				}
			}
		}
	}
	
	controller.setTemplates();
	
	$scope.$watch("[controller.model.channel.name, controller.model.templateLang]", function(newValue, oldValue) {
		if (newValue != oldValue) {
			if (controller.model.channel.name === 'PUSH') controller.model.templateLang = "en";
			if (oldValue !== undefined && oldValue !== null && oldValue !== '') {
				controller.model.templateName = undefined;
			}

			if (controller.model.channel.name === 'PULL') {
				controller.model.templateName = ""
				controller.model.forced = true
			}

			controller.setTemplates();
		}
	}, true);
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		if (notification !== null && channel !== null) {
			notificationRest.modifyChannel(notification.id, channel.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.MODIFY.SUCCESS");
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.MODIFY.ERROR");
				errors.catch("", false)(error)
			});
		} else if (notification !== null) {
			notificationRest.addChannel(notification.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.ADD.SUCCESS");
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.ADD.ERROR");
				errors.catch("", false)(error)
			});
		} else {
			$uibModalInstance.close(controller.model);
		}
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);