'use strict'

angular.module('lithium').controller('NotificationsEditController', ['notification', 'availableChannels', '$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'NotificationRest', 'notificationTypes',
	function(notification, availableChannels, $translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, notificationRest, noticationTypes) {
		var controller = this;
		
		controller.model = notification;
		
		controller.setAvailableChannels = function() {
			controller.availableChannels = angular.copy(availableChannels);
			for (var i = 0; i < controller.model.channels.length; i++) {
				for (var k = 0; k < controller.availableChannels.length; k++) {
					if (controller.model.channels[i].channel.name === controller.availableChannels[k].name) {
						controller.availableChannels.splice(k, 1);
					}
				}
			}
		}
		
		controller.setAvailableChannels();
		
		controller.fields = [
			{
				className : 'col-xs-12',
				key : "domain.name",
				type : "ui-select-single",
				templateOptions : {
					label : "Domain",
					required : true,
					description : "Choose the domain that you are creating the notification for",
					valueProp : 'name',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : [],
					disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DOMAIN.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.to.options = $userService.domainsWithRole("NOTIFICATION_ADD");
				}]
			},
			{
				className : 'col-xs-12',
				key: "name",
				type: "input",
				templateOptions: {
					label: "Name", description: "A unique name for the notification", placeholder: "",
					required: true, disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NAME.DESCRIPTION" | translate'
				}
			},
			{
				className : 'col-xs-12',
				key: "displayName",
				type: "input",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true
				},
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DISPLAYNAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DISPLAYNAME.DESCRIPTION" | translate'
				}
			},
			{
				className : 'col-xs-12',
				key: "description",
				type: "textarea",
				templateOptions: {
					label: "Description", description: "A short description to remember the purpose of the notification", placeholder: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
				}
			},
			{
				className : 'col-xs-12',
				key : "notificationType",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					description : "",
					valueProp : 'value',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : noticationTypes,
					disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NOTIFICATION_TYPE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NOTIFICATION_TYPE.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.to.options = noticationTypes.map(type => {
						return { name: type.name, value: type }
					});
				}]
			}
		];
		
		controller.messageField = [
			{
				className: "col-xs-12",
				key: "message",
				type: "ckeditor",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: false, maxlength: 1000000,
					ckoptions: {
						fullPage: true,
						language: 'en',
						enterMode: CKEDITOR.ENTER_P,
						shiftEnterMode: CKEDITOR.ENTER_BR,
						allowedContent: true,
						entities: false,
						filebrowserBrowseUrl: notificationRest.baseUrl + 'browser/browse.php',
						filebrowserUploadUrl: notificationRest.baseUrl + 'uploader/upload.php'
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.MESSAGE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.MESSAGE.DESCRIPTION" | translate'
				}
			}
		];
		
		controller.addChannel = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/notifications/notifications/addchannel/addchannel.html',
				controller: 'NotificationsAddChannelModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domainName: function() { 
						return (controller.model.domain !== undefined && controller.model.domain !== null && controller.model.domain.name != undefined && controller.model.domain.name !== null)
								? controller.model.domain.name: ""; },
					channels: function() { return controller.availableChannels },
					notification: function() { return controller.model; },
					channel: function() { return null; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/notifications/notifications/addchannel/addchannel.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				if (response) {
					notification = response.plain();
					controller.model = response.plain();
				}
			});
		}
		
		controller.modifyChannel = function(channel) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/notifications/notifications/addchannel/addchannel.html',
				controller: 'NotificationsAddChannelModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domainName: function() { 
						return (controller.model.domain !== undefined && controller.model.domain !== null && controller.model.domain.name != undefined && controller.model.domain.name !== null)
								? controller.model.domain.name: ""; },
					channels: function() { return availableChannels; },
					notification: function() { return controller.model; },
					channel: function() { return angular.copy(channel); },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/notifications/notifications/addchannel/addchannel.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				if (response) {
					notification = response.plain();
					controller.model = response.plain();
					controller.setAvailableChannels();
				}
			});
		}
		
		controller.removeChannel = function(channel) {

			angular.confirmDialog({
				title: 'UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.DELETE_CONFIRMATION.TITLE',
				message: 'UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.DELETE_CONFIRMATION.MESSAGE',
				onConfirm:() => {
					notificationRest.removeChannel(controller.model.id, channel.id).then(function(response) {
						if (response._status === 0) {
							controller.model = response.plain();
							notify.success("UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.REMOVE.SUCCESS");
						} else {
							notify.warning(response._message);
						}
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.REMOVE.ERROR");
						errors.catch("", false)(error)
					});
				},
				confirmButtonText: 'UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.DELETE_CONFIRMATION.CONFIRM_BUTTON',
				confirmButtonIcon: 'trash',
				cancelButtonText: 'UI_NETWORK_ADMIN.NOTIFICATIONS.CHANNEL.DELETE_CONFIRMATION.CANCEL_BUTTON',
				confirmType: 'danger'
			});
			
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			let ckEditorInSrcMode = false;

		    for (var i in CKEDITOR.instances) {     
		      if (CKEDITOR.instances[i].mode === 'source') {            
		          ckEditorInSrcMode = true;
		          break;
		      }
		    }

		    if (ckEditorInSrcMode) {
		      notify.warning("The message content editor is still in source mode. Changes will not persist. Please switch the mode of the template content editor.");
		      return;
		    }
			
			notificationRest.edit(controller.model.id, controller.model).then(function(response) {
				if (response._status === 0) {
					controller.model = response.plain();
					notify.success("UI_NETWORK_ADMIN.NOTIFICATIONS.EDIT.SUCCESS");
					$state.go("^.view", { id:response.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.NOTIFICATIONS.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);