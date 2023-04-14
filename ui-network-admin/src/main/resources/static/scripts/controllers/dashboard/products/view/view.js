'use strict';

angular.module('lithium')
	.controller('ProductView', ["product", "$userService", "notify", "errors", "$state", "ProductRest", "$dt", "$translate", "NotificationRest", "$uibModal", "utilityFields", "file-upload", "bsLoadingOverlayService", 
	function(product, $userService, notify, errors, $state, productRest, $dt, $translate, notificationRest, $uibModal, utilityFields, fileUpload, bsLoadingOverlayService) {
		var controller = this;
		
		controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		
		controller.model = {};
		controller.model = product;
		controller.model.forceImageReload = new Date().getTime();
		controller.imageUrl = "/services/service-product/product/graphic/view/"+product.domain.name+"?guid="+product.guid;
		controller.uploadFileUri= "services/service-product/product/admin/graphic/edit";
		
		controller.modelOriginal = angular.copy(product);
		controller.options = { formState: { readOnly: true } };
		
		controller.referenceId = "fileupload_"+(Math.random()*1000);
		
		controller.uploadFile = function(name) {
			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_"+name});
			var file = controller["my"+name+"File"];
			if (!file) {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_"+name});
				return;
			}
			var extraKeyVal = [];
			extraKeyVal.push({"key": "productId", "value": product.id});
			extraKeyVal.push({"key": "graphicFunctionName", "value": name});
			extraKeyVal.push({"key": "deleted", "value": false});
			extraKeyVal.push({"key": "enabled", "value": true});
			extraKeyVal.push({"key": "domainName", "value": product.domain.name});
			
			fileUpload.uploadFileToUrl(file, controller.uploadFileUri, extraKeyVal).then(function() {
				notify.success("UI_NETWORK_ADMIN.CATALOG.IMAGE.GRAPHICUPLOADED");
				controller.model.forceImageReload = new Date().getTime();
			}).catch(function(error) {
				errors.catch("Could not upload image.", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_"+name});
			});
		}
		
		controller.removeFile = function(name) {
			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_"+name});
			productRest.removeGraphic(product.id, name).then(function() {
				notify.success("UI_NETWORK_ADMIN.CATALOG.IMAGE.GRAPHIC_REMOVED_SUCCESS");
				controller.model.forceImageReload = new Date().getTime();
			}).catch(function(error) {
				errors.catch("Could not remove image.", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_"+name});
			});
		}
		
		controller.onEdit = function() {
			controller.options.formState.readOnly = false;
		}
		
		controller.onCancel = function() {
			controller.onReset();
			controller.options.formState.readOnly = true;
		}
		controller.onReset = function() {
			controller.model = angular.copy(controller.modelOriginal);
		}
		
		controller.currencyfields = [
			utilityFields.currencyTypeAhead("col-xs-12", true, false),
			{
				"className": "col-xs-12",
				"key":"currencyAmount",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 2,
					hidesep: true,
					neg: false
				},
				"expressionProperties": {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.BASECURRENCY.TITLE" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.BASECURRENCY.PLACE" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.CATALOG.FIELDS.BASECURRENCY.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.options.templateOptions.options = controller.availableDomains;
				}]
			}
		];
		
		controller.fields = [{
			"className":"col-xs-12",
			"type":"input",
			"key":"guid",
			"optionsTypes": ['editable'],
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
			"optionsTypes": ['editable'],
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
			"optionsTypes": ['editable'],
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
			"optionsTypes": ['editable'],
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				dataAllowClear: false,
				disabled: true,
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
			"optionsTypes": ['editable'],
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
				if (controller.model.domain.name) {
					notificationRest.findByDomainName(controller.model.domain.name).then(function(notifications) {
						$scope.to.options = notifications.plain();
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
						errors.catch("", false)(error)
					}).finally(function() {
					});
				}
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
		}];
		
		var baseUrl = "services/service-product/product/admin/localcurrency/"+controller.model.id+"/table?1=1";
		controller.localcurrencyTable = $dt.builder()
		.column($dt.column('countryCode').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.COUNTRYCODE")))
		.column($dt.columncurrencysymbol('currencyAmount', 'currencyCode', 2).withTitle($translate("UI_NETWORK_ADMIN.CATALOG.LOCALCURRENCY.CURRENCY")))
		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewLocalCurrency(data) } }]))
		.options(baseUrl)
		.order([0, 'desc'])
		.build();
		
		var baseUrl = "services/service-product/product/admin/payouts/"+controller.model.id+"/table?1=1";
		controller.payoutsTable = $dt.builder()
		.column($dt.column('bonusCode').withTitle($translate("UI_NETWORK_ADMIN.CATALOG.PAYOUT.BONUS")))
		.column($dt.columncurrencysymbol('currencyAmount', 'currencyCode', 2).withTitle($translate("UI_NETWORK_ADMIN.CATALOG.PAYOUT.CURRENCY")))
		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewPayout(data) } }]))
		.options(baseUrl)
		.order([0, 'desc'])
		.build();
		
		controller.tableLoad = function() {
			if (!angular.isUndefined(controller.localcurrencyTable.instance)) {
				baseUrl = "services/service-product/product/admin/localcurrency/"+controller.model.id+"/table?1=1";
				controller.localcurrencyTable.instance._renderer.options.ajax = baseUrl;
				controller.localcurrencyTable.instance.rerender();
			}
			if (!angular.isUndefined(controller.payoutsTable.instance)) {
				baseUrl = "services/service-product/product/admin/payouts/"+controller.model.id+"/table?1=1";
				controller.payoutsTable.instance._renderer.options.ajax = baseUrl;
				controller.payoutsTable.instance.rerender();
			}
		}
		
		controller.enable = function() {
			productRest.enable(controller.model.id).then(function(response) {
				controller.model = response.plain();
				notify.success("UI_NETWORK_ADMIN.CATALOG.ENABLED.SUCCESS");
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.ENABLED.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.addLocalCurrency = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/products/view/addlocalcurrency.html',
				controller: 'LocalCurrencyAdd',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					product: function() { return product; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/products/view/addlocalcurrency.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(category) {
				console.log(category);
				controller.tableLoad();
			});
		}
		
		controller.addPayout = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/products/view/addpayout.html',
				controller: 'PayoutAdd',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					product: function() { return product; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/products/view/addpayout.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(category) {
				console.log(category);
				controller.tableLoad();
			});
		}
		
		controller.viewPayout = function(payout) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/products/view/viewpayout.html',
				controller: 'PayoutView',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					payout: function() { return payout; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/products/view/viewpayout.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(category) {
				console.log(category);
				controller.tableLoad();
			});
		}
		
		controller.viewLocalCurrency = function(localCurrency) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/products/view/viewlocalcurrency.html',
				controller: 'LocalCurrencyView',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					localCurrency: function() { return localCurrency; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/products/view/viewlocalcurrency.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(category) {
				console.log(category);
				controller.tableLoad();
			});
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			console.log(controller.model);
			productRest.edit(controller.model).then(function(category) {
				notify.success("UI_NETWORK_ADMIN.CATALOG.EDIT.SUCCESS");
				controller.options.formState.readOnly = true;
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.CATALOG.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.list = function() {
			$state.go("dashboard.product.list");
		};
	}
]);