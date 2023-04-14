'use strict';

angular.module('lithium')
	.controller('ProductAdd', ["$userService", "notify", "errors", "$state", "ProductRest", "NotificationRest",
	function($userService, notify, errors, $state, productRest, notificationRest) {
		var controller = this;
		
		controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		
		controller.model = {};
		controller.model.domain = {};
		controller.options = {};
		
		controller.fields = [{
			"className":"col-xs-12",
			"type":"input",
			"key":"guid",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.GUID.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.GUID.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.GUID.DESC" | translate'
			}
		},{
			"className":"col-xs-12",
			"type":"input",
			"key":"name",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.NAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.NAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.NAME.DESC" | translate'
			}
		},{
			"className":"col-xs-12",
			"type":"input",
			"key":"description",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":false,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.DESCRIPTION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.DESCRIPTION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.DESCRIPTION.DESC" | translate'
			}
		},{
			key: "domain.name", 
			className: "col-xs-12",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'name',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.DOMAINNAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.DOMAINNAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.DOMAINNAME.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.to.options = controller.availableDomains;
			}]
		},{
			key: "notification", 
			className: "col-xs-12",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'name',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.NOTIFICATION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.NOTIFICATION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.NOTIFICATION.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.$watch('model.domain.name', function (newValue, oldValue, theScope) {
					if (newValue !== oldValue) {
						if ($scope.model[$scope.options.key] && oldValue) {
							$scope.model[$scope.options.key] = '';
						} 
//						$scope.to.loading = 
						notificationRest.findByDomainName(controller.model.domain.name).then(function(notifications) {
							$scope.to.options = notifications.plain();
						}).catch(function(error) {
							notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
							errors.catch("", false)(error)
						}).finally(function() {
						});
					}
				});
			}]
//		},{
//			className: "col-xs-12",
//			template: "<div><strong><hr/></strong></div>"
//		},{
//			"className": "row v-reset-row ",
//			"fieldGroup": [{
//				"className":"col-xs-11",
//				"type":"ui-select-single",
//				"key":"catalog.name",
//				"templateOptions":{
//					"label":"",
//					"placeholder":"",
//					"description":"",
//					"required":true,
//					"optionsAttr": "bs-options",
//					"valueProp": "value",
//					"labelProp": "label",
//					"options":[]
//				},
//				"expressionProperties": {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.CATALOG.TITLE" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.CATALOG.PLACE" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.CATALOG.DESC" | translate'
//				},
//				controller: ['$scope', function($scope) {
//					$scope.options.templateOptions.options = [{label:"catalog A", value:"more"},{label:"catalog B", value:"less"}];
//				}]
//			},{
//				"className": "col-xs-1",
//				"type": "buttoncustom",
//				"key": "category-button-add",
//				templateOptions : {
//					label: '',
//					text : '',
//					bclass: "primary",
//					icon: "plus",
//				},
//				"expressionProperties": {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.CATALOG.ADD" | translate'
//				},
//				controller: ['$scope', function($scope) {
//					$scope.onClick = function($event, model) {
//						console.log($event, model)
//					}
//				}]
//			}]
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			console.log(controller.model);
			productRest.add(controller.model).then(function(p) {
				console.log(p);
				if (p._successful) {
					notify.success("UI_NETWORK_ADMIN.CATALOG.ADD.SUCCESS");
					$state.go("dashboard.product.view", {id: p.id});
				} else {
					notify.error("UI_NETWORK_ADMIN.CATALOG.ADD.ERROR");
				}
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			console.log('cancel');
			$state.go("dashboard.product.list");
		};
	}
]);