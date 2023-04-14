'use strict';

angular.module('lithium')
	.controller('providerEdit', ["providerOrLink","$uibModalInstance", "$log", "$scope", "$state", "$stateParams", "$http", "Restangular", "rest-provider","notify", "bsLoadingOverlayService",
		function(providerOrLink, $uibModalInstance, $log, $scope, $state, $stateParams, $http, Restangular, restProvider, notify, bsLoadingOverlayService) {
		var controller = this;
		console.log(providerOrLink.plain());
		if(angular.isDefined(providerOrLink.provider)) {
			//It is a link with provider as child
			$scope.domainName = providerOrLink.provider.domain.name;
			$scope.providerId = providerOrLink.provider.id;
			$scope.linkId = providerOrLink.id;
			$scope.provider = providerOrLink.provider;
		} else {
			$scope.domainName = providerOrLink.domain.name;
			$scope.providerId = providerOrLink.id;
			$scope.provider = providerOrLink;
		}
		
		//Controlling provider edit
		if(angular.isUndefined($scope.linkId)) {
		
			restProvider.view($scope.domainName,$scope.providerId).then(function(provider) {
				controller.model = provider;
				controller.model.propertiesHash = {};
				for(var i = 0; i < provider.properties.length; ++i) {
					controller.model.propertiesHash[provider.properties[i].name] = provider.properties[i];
				}
				
				restProvider.configProps(provider.url).then(function(providers) {
					controller.pc = providers[0];
					$log.info(controller.pc.properties);
					var fields = [];
					for(var i = 0; i < controller.pc.properties.length; i++) {
						var property = controller.pc.properties[i];
						if(!controller.model.propertiesHash[property.name]) {
							controller.model.propertiesHash[property.name] = property;
						}
						controller.model.propertiesHash[property.name].tooltip = property.tooltip;
						controller.model.propertiesHash[property.name].disabled = property.disabled;
						if (property.dataType === "java.lang.Boolean") {
							controller.model.propertiesHash[property.name].type = "checkbox2";
							controller.model.propertiesHash[property.name].value = (controller.model.propertiesHash[property.name].value === "true")
						} else {
							controller.model.propertiesHash[property.name].type = "input";
						}

					}
					var propertyNameForPassUpdate = 'Password last update Date';
					if (controller.model.propertiesHash.hasOwnProperty(propertyNameForPassUpdate)) {
						$scope.$watch('controller.model.propertiesHash.Password.value', function (newValue, oldValue) {
							let currentPassword = $scope.provider.properties.find(o => o.name === 'Password');
							if (newValue.toString() !== currentPassword.value.toString()) {
								controller.model.propertiesHash[propertyNameForPassUpdate].value = new Date().toISOString();
							}
						});
					}
					for(var propertyName in controller.model.propertiesHash) {
						var property = controller.model.propertiesHash[propertyName];
						fields.push(
							{
								className: "col-xs-12",
								key: "propertiesHash['"+property.name+"'].value",
								type: property.type,
								templateOptions: {
									label: property.name, 
									description: property.tooltip, 
									placeholder: property.name,
                                    disabled: property.disabled
								}
							}
						);
					};
					
					controller.fields.push(
						{
							className: "row v-reset-row ",
							fieldGroup: fields
						}
					);
					
					controller.original = angular.copy(controller.model);
				}, function(response) {
					notify.error("UI_NETWORK_ADMIN.PROVIDER.EDIT.FAILED_TO_GET_PROPS");
					$log.error(response);
				});
			});
	
			controller.model = {properties: {}};
			
			controller.options = {};
	
			controller.fields = 
				[
					{
						className: "row v-reset-row ",
						fieldGroup: 
						[
							{
								className: "col-xs-12",
								key: "name",
								type: "input",
								"templateOptions" : {
									"label" : "Name",
									"required" : true
								}
							},
							{
								className: "col-xs-12",
								key: "enabled",
								type: "checkbox",
								"templateOptions" : {
									"label" : "Enabled"
								}
							}
						]
					}
	
	
				];
			
			controller.onSubmit = function() {
				controller.model.properties = [];
				for (var propertyName in controller.model.propertiesHash) {
					controller.model.propertiesHash[propertyName].provider = {id: $scope.providerId};
					controller.model.properties.push(controller.model.propertiesHash[propertyName]);
				}

				if (controller.model.propertiesHash.hasOwnProperty('betSearchBrand')) {
					if (controller.model.propertiesHash.betSearchBrand.value == "") {
						angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
						notify.warning("UI_NETWORK_ADMIN.PROVIDER.EDIT.ERROR");
						bsLoadingOverlayService.stop({referenceId:controller.referenceId});
						return false;
					}
				}

				var propertyNameForPassUpdate = 'Password last update Date';
				if (controller.model.propertiesHash.hasOwnProperty(propertyNameForPassUpdate)) {
					let currentPassword = $scope.provider.properties.find(o => o.name === 'Password');
					let newPassword = controller.model.propertiesHash['Password'].value;
					let currentLastUpdateDate = $scope.provider.properties.find(o => o.name === propertyNameForPassUpdate);
					if (currentPassword.value.toString() === newPassword.toString()) {
						controller.model.propertiesHash[propertyNameForPassUpdate].value = currentLastUpdateDate.value;
					}
				}

				restProvider.save($scope.domainName,$scope.providerId, controller.model).then(function(provider) {
					for(var i = 0; i < provider.properties.length; ++i) {
						controller.model.propertiesHash[provider.properties[i].name] = provider.properties[i];
					}
					notify.success("UI_NETWORK_ADMIN.PROVIDER.EDIT.SUCCESS");
					//$state.transitionTo("dashboard.providerView",{'domainName': provider.domain.name, 'providerId': provider.id});
					$uibModalInstance.close(provider);
				}, function(response) {
					$log.error(response);
					notify.error("Unable to save, please try again");
				});
			}
			//link edit
		} else {
			//console.log($scope.linkId);
			restProvider.viewLink($scope.domainName,$scope.linkId).then(function(providerLink) {
				controller.model = providerLink;
				
				controller.options = {};
		
				controller.fields = 
					[
					 {
						 className: "row v-reset-row ",
						 fieldGroup: 
							 [
							  {
								  "className" : "col-xs-12",
								  "type" : "ui-select-single",
								  "key" : "link",
								  "templateOptions" : {
									  "label" : '',
									  "required" : true,
									  "description" : '',
									  "valueProp" : 'id',
									  "labelProp" : 'linkDisplayName',
									  "placeholder" : '',
									  "optionsAttr": 'bs-options ui-options', "ngOptions": 'ui-options',
									  "options" : []
								  },
								  controller: ['$scope', function($scope) {
									  //Assign owner as value of link
									  restProvider.findOwnerLink($stateParams.domainName, providerLink.provider.id).then(
												function(ownerLink) {
													$scope.model.link = ownerLink.id;
													//console.log($scope);
												}
										);
									  //Fill options of select with possible provider configs to link to
										restProvider.availableProviderLinksList($stateParams.domainName, providerLink.provider.url)
											.then(function(providerLinks) {
												var ownerProviderLinks = [];
												var i=0;
												for(i=0; i < providerLinks.length; ++i) {
													if(providerLinks[i].ownerLink) {
														providerLinks[i].linkDisplayName =
															providerLinks[i].domain.name+" - "+providerLinks[i].provider.name;
														ownerProviderLinks.push(angular.copy(providerLinks[i]));
													}
												  }
												$scope.to.options = ownerProviderLinks;
											  });
								  	}]
								  ,
								  expressionProperties: {
									  'templateOptions.label': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.LINK.NAME" | translate',
									  'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.LINK.PLACEHOLDER" | translate',
									  'templateOptions.description': '"UI_NETWORK_ADMIN.PROVIDER.FIELDS.LINK.DESCRIPTION" | translate'
								  }
							  },
								{
									className: "col-xs-12",
									key: "enabled",
									type: "checkbox",
									"templateOptions" : {
										"label" : "Enabled"
									}
								}
							 ]
					 }
					 ];
				
				controller.original = angular.copy(controller.model);
			});
			
			controller.onSubmit = function() {
				var data = {
						"linkId" : controller.model.id,
						"ownerLinkId" : controller.model.link,
						"enabled" : controller.model.enabled,
						"deleted" : false
				}
				//console.log(data);
				restProvider.editLink($scope.domainName, data).then(function(providerLink) {
					notify.success("UI_NETWORK_ADMIN.PROVIDER.EDIT.SUCCESS");
					//$state.transitionTo("dashboard.providerView",{'domainName': $scope.domainName, 'providerId': providerLink.provider.id, 'linkId':providerLink.id});
					$uibModalInstance.close(providerLink);
				});
			}
			
			controller.onRemove = function() {
				var data = {
						"linkId" : controller.model.id,
						"ownerLinkId" : controller.model.link,
						"enabled" : controller.model.enabled,
						"deleted" : true
				}
				controller.model.deleted = true;
				restProvider.editLink($scope.domainName, data).then(function(providerLink) {
					notify.success("UI_NETWORK_ADMIN.PROVIDER.EDIT.SUCCESS");
					//$state.transitionTo("dashboard.domains.domain.providers");
					$uibModalInstance.close(providerLink);
				});
			}
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.resetModel = function() {
			controller.model = angular.copy(controller.original);
		}
	}
]);
