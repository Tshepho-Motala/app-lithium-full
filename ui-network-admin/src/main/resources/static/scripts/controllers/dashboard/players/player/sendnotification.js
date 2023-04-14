'use strict';

angular.module('lithium')
	.controller('SendPlayerNotification', ["$uibModalInstance", "$userService", "notify", "errors", "UserRest", "NotificationRest", "user",
	function($uibModalInstance, $userService, notify, errors, userRest, notificationRest, user) {
		var controller = this;
		
		controller.model = {};
		controller.options = {};
		
		controller.model.guid = user.guid;
		
		controller.fields = [{
			"className":"col-xs-12 form-group",
			"type":"ui-select-single",
			"key":"notification",
			"templateOptions":{
				"label": "",
				"placeholder": "",
				"description": "",
				"required": true,
				"optionsAttr": "bs-options",
				"valueProp": "name",
				"labelProp": "name",
				"options": []
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.NOTIFICATION.FIELDS.NOTIFICATION.LABEL" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.NOTIFICATION.FIELDS.NOTIFICATION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.NOTIFICATION.FIELDS.NOTIFICATION.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				notificationRest.findByDomainName(user.domain.name).then(function(notifications) {
					console.log(notifications.plain());
					$scope.to.options = notifications.plain();
				}).catch(function(error) {
					notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
					errors.catch("", false)(error)
				}).finally(function() {
				});
//				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			notificationRest.send(controller.model.guid, controller.model.notification).then(function(response) {
				notify.success("UI_NETWORK_ADMIN.PLAYER.NOTIFICATION.SEND.SUCCESS");
				$uibModalInstance.close(response);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.PLAYER.NOTIFICATION.SEND.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);