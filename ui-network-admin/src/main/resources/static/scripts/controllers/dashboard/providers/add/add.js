'use strict';

angular.module('lithium')
	.controller('providerAdd', ["domainName", "providerType", "$uibModalInstance","$scope", "$filter", "$translate", "$log", "$state", "$stateParams", "$http", "rest-provider","notify",
	function(domainName, providerType, $uibModalInstance, $scope, $filter, $translate, $log, $state, $stateParams, $http, providerRest, notify) {
		$scope.$parent.title = 'UI_NETWORK_ADMIN.MAIL.PROVIDERS.EDIT.TITLE';
		$scope.$parent.description = 'UI_NETWORK_ADMIN.PROVIDER.ADD.DESCRIPTION';
		$scope.domainName = domainName;
		let controller = this;
		
		controller.model = {};
		controller.options = {};
		
		console.log(providerType);
		
		controller.fields = 
			[
				{
					className: "row v-reset-row ",
					fieldGroup: 
					[
						{
							"className" : "col-xs-8",
							"type" : "ui-select-single",
							"key" : "url",
							"templateOptions" : {
								"label" : '',
								"required" : true,
								"description" : '',
								"valueProp" : 'name',
								"labelProp" : 'name',
								"placeholder" : '',
								"optionsAttr": 'ui-options', "ngOptions": 'ui-options',
								"options" : []
							},
							controller: ['$scope', function($scope) {
								providerRest.list()
									.then(function(providers) {
										console.log(providers.plain());
										providers = $filter('filter')(providers, {name: '!service-accounting-provider-internal'});
										$scope.to.options = providers;
										$scope.to.loading = false;
									});
							}],
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.URL.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.URL.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.URL.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-8",
							key: "name",
							type: "input",
							hideExpression: function($viewValue, $modelValue, scope) {
								return angular.isDefined(scope.model.link);
							},
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: true,
								minlength: 2, maxlength: 35,
								focus: true
							},
							modelOptions: {
								updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
							},
							
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.NAME.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.NAME.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.NAME.DESCRIPTION" | translate'
							}
							//,
							//validators: {
							//	pattern: {
							//		expression: function($viewValue, $modelValue, scope) {
							//			return /^[0-9a-z_\\.]+$/.test($viewValue);
							//		},
							//		message: '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.NAME.PATTERN" | translate'
							//	}
							//}
						},
						{
							"className" : "col-xs-8",
							"type" : "ui-select-single",
							"key" : "link",
							"hide" : true,
							"templateOptions" : {
								"label" : '',
								"required" : false,
								"description" : '',
								"valueProp" : 'id',
								"labelProp" : 'linkDisplayName',
								"placeholder" : '',
								"optionsAttr": 'ui-options', "ngOptions": 'ui-options',
								"options" : []
							},
							watcher: {
								expression: 'model.url',
								listener: function(field, newValue, oldValue, scope, stopWatching) {
									field.templateOptions.options = [];
									field.hide = true;
									delete scope.model[field.key];
									if(newValue) {
										providerRest.availableProviderLinksList($scope.domainName,newValue).then(function(providerLinks) {
											let ownerProviderLinks = [];
											for(let i=0; i < providerLinks.length; ++i) {
												if(providerLinks[i].ownerLink) {
													providerLinks[i].linkDisplayName =
														providerLinks[i].domain.name+" - "+providerLinks[i].provider.name;
													ownerProviderLinks.push(angular.copy(providerLinks[i]));
												}
											}
											field.templateOptions.options = ownerProviderLinks;
											field.hide = (ownerProviderLinks.length <= 0);
										});
									}
								}
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.LINK.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.LINK.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.LINK.DESCRIPTION" | translate'
							}
						},
						{
							"type": "input",
							"key": "type",
							"defaultValue": providerType, //TODO: Find a way to select the provider type (perhaps get it from provider name substring
							"templateOptions": {
								"type": "hidden",
								"label": ""
							}
						}
					]
				}
			];
		
//		controller.getProviderType = function(providerUrl) {
//			var providerSplit = providerUrl.split("-");
//			return providerSplit[1];
//		}
		
		controller.onSubmit = function() {
			console.log(controller.model);
			if(angular.isDefined(controller.model.link)) {
				providerRest.addLink($scope.domainName, controller.model.link).then(function(providerLink) {
					if(providerLink) {
						notify.success("UI_NETWORK_ADMIN.PROVIDER.ADD.SUCCESS");
						$uibModalInstance.close("success");
						//$state.go("dashboard.providerView", {domainName: $stateParams.domainName, providerId: providerLink.provider.id, linkId: providerLink.id});
					} else {
						notify.error("Unable to create provider link, there might already be a link");
					}
				});
			} else {
				//controller.model.type = controller.getProviderType(controller.model.url);
				providerRest.add($scope.domainName, controller.model).then(function(provider) {
					if(provider) {
						notify.success("UI_NETWORK_ADMIN.PROVIDER.ADD.SUCCESS");
						$uibModalInstance.close("success");
						//$state.go("dashboard.providerEdit", {domainName: $stateParams.domainName, providerId: provider.id});
					} else {
						notify.error("Unable to create provider, there might already be a provider of this type");
					}
				});
			}
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);
