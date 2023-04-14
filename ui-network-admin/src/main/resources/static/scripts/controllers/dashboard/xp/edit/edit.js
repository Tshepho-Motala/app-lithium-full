'use strict'

angular.module('lithium').controller('XPSchemesEditController', ['scheme', '$translate', '$uibModal', '$filter', '$userService', '$state', 'errors', 'notify', 'XPRest',
	function(scheme, $translate, $uibModal, $filter, $userService, $state, errors, notify, xpRest) {
		var controller = this;
		
		controller.model = scheme;
		
		controller.getActiveScheme = function() {
			if (controller.model.domain.name !== undefined && controller.model.domain.name !== null && controller.model.domain.name !== '') {
				xpRest.getActiveScheme(controller.model.domain.name).then(function(response) {
					controller.activeScheme = response.plain();
				});
			}
		}
		
		controller.getActiveScheme();
		
		if (controller.model.levels === undefined) controller.model.levels = [];
		
		controller.schemeDetails = [
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
					options : [],
					disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.DOMAIN.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.DOMAIN.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.to.options = $userService.domainsWithRole("XP_SCHEME_ADD");
				}]
			},
			{
				className : 'col-xs-12',
				key: "name",
				type: "input",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true, disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.NAME.DESCRIPTION" | translate'
				}
			},
			{
				className : 'col-xs-12',
				key: "description",
				type: "textarea",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
				}
			},
			{
				className : 'col-xs-12',
				key : "status.name",
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
					'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.STATUS.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.STATUS.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					xpRest.findAllStatuses().then(function(response) {
						$scope.to.options = response.plain();
					});
				}]
			},
		]
		
		controller.wageringRequirements = [
			{
				key: "wagerPercentage",
				type: "ui-percentage-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					required: false,
					hidesep: true,
					decimals: 0,
					placeholder: "100%",
					max: 100
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.WAGERPERCENTAGE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.WAGERPERCENTAGE.DESCRIPTION" | translate'
				}
			}
		]
		
		controller.addLevel = function() {
			if (controller.model.domain === undefined || controller.model.domain === null
					|| controller.model.domain.name === undefined || controller.model.domain === null
					|| controller.model.domain.name === '') {
				notify.warning('UI_NETWORK_ADMIN.XP.LEVELS.ADD.DOMAINWARN');
				return;
			}
			
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/xp/addlevel/addlevel.html',
				controller: 'XPSchemesAddLevelModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					level: function() { return null; },
					domainName: function() { return controller.model.domain.name; },
					number: function() { return (controller.model.levels.length + 1); },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/xp/addlevel/addlevel.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				controller.model.levels.push(response);
			});
		}
		
		controller.modifyLevel = function(level, $index) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/xp/addlevel/addlevel.html',
				controller: 'XPSchemesAddLevelModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					level: function() { return angular.copy(level) },
					domainName: function() { return controller.model.domain.name; },
					number: function() { return null;},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/xp/addlevel/addlevel.js']
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				controller.model.levels[$index] = response;
			});
		}
		
		controller.removeLevel = function(level, $index) {
			controller.model.levels.splice($index, 1);
			var sortedLevels = $filter('orderBy')(controller.model.levels, 'number');
			for (var k = 0; k < sortedLevels.length; k++) {
				sortedLevels[k].number = k + 1;
			}
			controller.model.levels = sortedLevels;
		}

		controller.addNotification = function(level) {
			console.log(level);

			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/xp/addnotification/addnotification.html',
				controller: 'XPSchemesLevelAddNotificationModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					domainName: function() {
						return controller.model.domain.name;
					},
					notification: function() {
						return null;
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/xp/addnotification/addnotification.js']
						})
					}
				}
			});

			modalInstance.result.then(function(notification) {
				if (level.notifications === undefined) level.notifications = [];
				level.notifications.push(notification);
			});
		}

		controller.modifyNotification = function(level, notification, $index) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/xp/addnotification/addnotification.html',
				controller: 'XPSchemesLevelAddNotificationModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					domainName: function() {
						return controller.model.domain.name;
					},
					notification: function() {
						return angular.copy(notification);
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/xp/addnotification/addnotification.js']
						})
					}
				}
			});

			modalInstance.result.then(function(n) {
				level.notifications[$index] = n;
			});
		}

		controller.removeNotification = function(level, $index) {
			level.notifications.splice($index, 1);
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			xpRest.editScheme(controller.model.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.XP.SUCCESS.EDIT");
					$state.go("^.view", { id:response.id });
				} else {
					notify.error("UI_NETWORK_ADMIN.XP.ERRORS.EDIT");
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.XP.ERRORS.EDIT");
				errors.catch("", false)(error)
			});
		}
	}
]);
