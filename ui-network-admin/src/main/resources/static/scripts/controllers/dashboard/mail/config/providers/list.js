'use strict'

angular.module('lithium')
.controller('MailProvidersListController',
["mailRest", "domainProviders", "$scope", "$stateParams", "$uibModal", "notify", "errors", "$filter",
function(mailRest, domainProviders, $scope, $stateParams, $uibModal, notify, errors, $filter) {
	
	var controller = this;
	
	controller.domainProviders = domainProviders;
	
	controller.addDomainProvider = function() {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/mail/config/providers/addprovider.html',
			controller: 'ProviderAdd',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			backdrop: 'static',
			resolve: {
				domainName: function () {
					return $stateParams.domainName;
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/mail/config/providers/addprovider.js'
						]
					})
				}
			}
		});
		
		modalInstance.result.then(function(domainProvider) {
			controller.editProperties(domainProvider);
			controller.reload();
		});
	}
	
	controller.saveDomainProviderOrder = function() {
		angular.forEach(controller.domainProviders, function(domainProvider, $index) {
			domainProvider.priority = $index;
		});
		mailRest.domainProviderUpdateMultiple(controller.domainProviders).then(function(dps) {
			controller.domainProviders = dps.plain();
			angular.extend(controller.domainProviders, dps.plain());
			notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.EDIT.ORDERCHANGEDSUCCESS");
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
		controller.domainProviderOrderChanged = false;
	}
	
	controller.editDomainProvider = function(domainProvider) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/mail/config/providers/editprovider.html',
			controller: 'EditProviderController',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			backdrop: 'static',
			resolve: {
				domainProvider: function() {
					return angular.copy(domainProvider);
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/mail/config/providers/editprovider.js'
						]
					})
				}
			}
		});
		
		modalInstance.result.then(function(dp) {
			angular.extend(domainProvider, dp);
		});
	}
	
	controller.toggleDomainProvider = function(domainProvider) {
		var enabled;
		angular.forEach(controller.domainProviders, function(dp, $index) {
			if (domainProvider.id === dp.id) {
				dp.enabled = !dp.enabled;
				enabled = dp.enabled;
			}
		});
		mailRest.domainProviderUpdateMultiple(controller.domainProviders).then(function(dps) {
			controller.domainProviders = dps.plain();
			if (enabled) {
				notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.EDIT.ENABLED");
			} else {
				notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.EDIT.DISABLED");
			}
		}).catch(function(error) {
			errors.catch("", false)(error)
		}).then(function() {
			controller.domainProviderGroups = $filter('orderBy')(controller.domainProviderGroups, '+priority');
		});
	}
	
	controller.deleteDomainProvider = function(domainProvider) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/mail/config/providers/deleteprovider.html',
			controller: 'DeleteProviderController',
			controllerAs: 'controller',
			size: 'md cascading-modal card-danger-shadow',
			backdrop: 'static',
			resolve: {
				domainProvider: function() {
					return angular.copy(domainProvider);
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/mail/config/providers/deleteprovider.js'
						]
					})
				}
			}
		});
		
		modalInstance.result.then(function(dp) {
			controller.reload();
		});
	}
	
	controller.editProperties = function(domainProvider) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/mail/config/providers/properties.html',
			controller: 'EditPropertiesController',
			controllerAs: 'controller',
			size: 'lg cascading-modal',
			backdrop: 'static',
			resolve: {
				domainProvider: function() {
					return domainProvider;
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/mail/config/providers/properties.js'
						]
					})
				}
			}
		});
		
		modalInstance.result.then(function(result) {
		});
	}
	
	controller.reload = function() {
		mailRest.domainProviders($stateParams.domainName).then(function(domainProviders) {
			controller.domainProviders = $filter('orderBy')(domainProviders.plain(), 'provider.code');
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}
	
	controller.treeOptions = {
		accept: function(sourceNodeScope, destNodesScope, destIndex) {
			if (sourceNodeScope.$treeScope != destNodesScope.$treeScope) {
				controller.domainProviderOrderChanged = false;
				return false;
			} else {
				controller.domainProviderOrderChanged = true;
				notify.warning("UI_NETWORK_ADMIN.MAIL.PROVIDERS.EDIT.ORDERCHANGED", {ttl: 30000});
				return true;
			}
		}
	};
	
}]);