'use strict'

angular.module('lithium').controller('XPSchemesAddController', ['$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', 'XPRest',
	function($translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, xpRest) {
		var controller = this;
		
		controller.model = { levels: [], domain: { name: null } };
		
		controller.getActiveScheme = function() {
			if (controller.model.domain.name !== undefined && controller.model.domain.name !== null && controller.model.domain.name !== '') {
				xpRest.getActiveScheme(controller.model.domain.name).then(function(response) {
					controller.activeScheme = response.plain();
				});
			}
		}
		
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
					options : []
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
					required: true
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
			}
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
			
			modalInstance.result.then(function(level) {
				controller.model.levels.push(level);
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
					schemeId: function() { return null; },
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
		
		controller.removeLevel = function($index) {
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
			
			xpRest.createScheme(controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.XP.SUCCESS.CREATE");
					$state.go("^.view", { id:response.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.XP.ERRORS.CREATE");
				errors.catch("", false)(error)
			});
		}
		
		$scope.$watch(function() { return controller.model.domain.name }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				if (newValue !== undefined && newValue !== null) {
					controller.getActiveScheme();
				} else {
					controller.activeScheme = null;
				}
				if (oldValue !== undefined && oldValue !== null) {
					if (controller.model.levels.length > 0) {
						var bonusCodeRemoved = false;
						var notificationsRemoved = false;
						for (var i = 0; i < controller.model.levels.length; i++) {
							if (controller.model.levels[i].bonus !== undefined && controller.model.levels[i].bonus.bonusCode !== undefined &&
								controller.model.levels[i].bonus.bonusCode !== null &&
								controller.model.levels[i].bonus.bonusCode !== '') {
									bonusCodeRemoved = true;
									controller.model.levels[i].bonus.bonusCode = '';
							}
							if (controller.model.levels[i].notifications.length > 0) {
								notificationsRemoved = true;
								controller.model.levels[i].notifications = [];
							}
						}
						if (bonusCodeRemoved) {
							notify.warning("UI_NETWORK_ADMIN.XP.LEVELS.ADD.DOMAINCHANGED");
						}
						if (notificationsRemoved) {
							notify.warning("Your domain selection has changed, and your levels configuration contained domain specific notification information. The notifications has been removed. Remember to reconfigure the level notifications.");
						}
					}
				}
			}
		});
	}
]);
