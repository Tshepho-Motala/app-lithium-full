'use strict'

angular.module('lithium').controller('NotificationsAddController', ['availableChannels', '$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'NotificationRest', 'notificationTypes',
	function(availableChannels, $translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, notificationRest, noticationTypes) {
		var controller = this;
		
		controller.model = { channels: [], domain: {name: ""} };

		console.log(noticationTypes)
		
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

		controller.getEditor = () => {
			let type = 'ckeditor'

			const pull = controller.model.channels.find(c => c.channel.name === 'PULL')

			if(!angular.isUndefined(pull)) {
				type = 'textarea';
				controller.model.message = ""
			}

		
			return [
				{
					className: "col-xs-12",
					key: "message",
					type: type,
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
			]
		}
		
		controller.fields = [
			{
				className : 'col-xs-12',
				key : "domain.name",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : []
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
					label: "", description: "", placeholder: "",
					required: true
				},
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NAME.DESCRIPTION" | translate'
				},
				asyncValidators: {
					nameUnique: {
						expression: function($viewValue, $modelValue, scope) {
							var success = false;
							return notificationRest.findByDomainNameAndName(controller.model.domain.name, encodeURIComponent($viewValue)).then(function(notification) {
								if (angular.isUndefined(notification) || (notification._status == 404) || (notification.length === 0)) {
									success = true;
								}
							}).catch(function() {
								scope.options.validation.show = true;
								errors.catch("UI_NETWORK_ADMIN.NOTIFICATIONS.FIELDS.NAME.UNIQUE", false);
							}).finally(function () {
								scope.options.templateOptions.loading = false;
								if (success) {
									return $q.resolve("No such notification");
								} else {
									return $q.reject("The notification already exists");
								}
							});
						},
						message: '"UI_NETWORK_ADMIN.NOTIFICATION.FIELDS.NAME.UNIQUE" | translate'
					}
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
					label: "", description: "", placeholder: ""
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
					required : true,
					description : "",
					valueProp : 'value',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : noticationTypes
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
					notification: function() { return null; },
					channel: function() { return null; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/notifications/notifications/addchannel/addchannel.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(channel) {
				channel.uid = controller.model.channels.length + 1;
				controller.model.channels.push(channel);
				controller.setAvailableChannels();
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
					notification: function() { return null; },
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
				for (var i = 0; i < controller.model.channels.length; i++) {
					if (controller.model.channels[i].uid === channel.uid) {
						controller.model.channels[i] = response;
						break;
					}
				}
				controller.setAvailableChannels();
			});
		}
		
		controller.removeChannel = function(channel) {
			for (var i = 0; i < controller.model.channels.length; i++) {
				if (controller.model.channels[i] === channel) {
					controller.model.channels.splice(i, 1);
				}
			}
			controller.setAvailableChannels();
		}
		
		$scope.$watch(function() { return controller.model.domain.name }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				if (oldValue !== undefined && oldValue !== null) {
					if (controller.model.channels.length > 0) {
						notify.warning("Domain name change detected. Communication channels have been removed as the respective templates were set for the previous domain");
						controller.model.channels = [];
						controller.setAvailableChannels();
					}
				}
			}
		});

		$scope.$watch(function() { return controller.model.channels }, function(newValue, oldValue) {
			controller.messageField = controller.getEditor()
		}, true);
		
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
			
			notificationRest.create(controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.NOTIFICATIONS.ADD.SUCCESS");
					$state.go("^.view", { id:response.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.NOTIFICATIONS.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);