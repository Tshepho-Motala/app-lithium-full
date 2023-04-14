'use strict';

angular.module('lithium')
.controller('CashierConfigController', ["$translate", "$scope", "methods", "$userService", "$stateParams", "$state", "$filter", "$rootScope","rest-cashier", "UserRest","rest-cashier-dmp", "rest-cashier-dm", 'rest-domain','$changelogService', 'accessRulesRest',
	function($translate, $scope, methods, $userService, $stateParams, $state, $filter, $rootScope, cashierRest, userRest,cashierDmpRest, cashierDmRest, domainRest, changelogService, accessRulesRest ) {
		var controller = this;
		controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "CASHIER_CONFIG", "CASHIER_CONFIG_VIEW"]);
		
		$scope.methods = methods;
		
		controller.tabs = [
			{ name: "dashboard.cashier.config.methods", title: "UI_NETWORK_ADMIN.CASHIER.TAB.METHODS.TITLE", roles: "CASHIER_CONFIG,CASHIER_CONFIG_VIEW" },
			{ name: "dashboard.cashier.config.profiles", title: "UI_NETWORK_ADMIN.CASHIER.TAB.PROFILES", roles: "CASHIER_CONFIG,CASHIER_CONFIG_VIEW" }
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			$state.go(tab.name, {
				domainName: controller.selectedDomain
			});
		}
		
//		angular.forEach(controller.tabs, function(tab) {
//			if ($state.includes(tab.name)) controller.tab = tab;
//		});
		
		controller.domainSelect = function(item) {
			controller.selectedDomain = item.name;
			if (angular.isUndefined(controller.tab)) {
				controller.setTab(controller.tabs[0]);
			} else {
				controller.setTab(controller.tab);
			}
		}
		controller.clearSelectedDomain = function() {
			$scope.description = "";
			controller.selectedDomain = null;
//			$state.go('dashboard.cashier.config');
		}
//		controller.selectedDomain = controller.domains[5].name;
//		controller.setTab(controller.tabs[1]);
		
		if ($stateParams.domainName != null) controller.selectedDomain = $stateParams.domainName;
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});

		$rootScope.provide.pageHeaderProvider['getDomains'] = () => {
			return $userService.playerDomainsWithAnyRole(["ADMIN", "CASHIER_CONFIG", "CASHIER_CONFIG_VIEW"])
		}

		$rootScope.provide.cashierConfigProvider.deleteProcessor = (processor) => {
			return cashierRest.domainMethodProcessorDeleteFull(processor)
		}

		$rootScope.provide.cashierConfigProvider.findProperties = (domainId) => {
			return cashierRest.domainMethodProcessorPropertiesNoDefaults(domainId)
		}

		$rootScope.provide.cashierConfigProvider.saveProperties = (domainId, properties) => {
			return cashierRest.domainMethodProcessorPropertiesSave(domainId,properties)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorUserCreateOrUpdate = (dmpu, type) => {
			return cashierRest.domainMethodProcessorUserCreateOrUpdate(dmpu,type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorUserSave = (dmpu, type) => {
			return cashierRest.domainMethodProcessorUserSave(dmpu,type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileCreateOrUpdate = (dmpp) => {
			return cashierRest.domainMethodProcessorProfileCreateOrUpdate(dmpp)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileSave = (dmpp, type) => {
			return cashierRest.domainMethodProcessorProfileSave(dmpp,type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorSave = (processor, type) => {
			return cashierRest.domainMethodProcessorSave(processor, type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorSaveDL = (processor) => {
			return cashierRest.domainMethodProcessorSaveDL(processor)
		}

		$rootScope.provide.cashierConfigProvider.domainMethods = (domainName, methodsType) => {
			return cashierRest.domainMethods(domainName, methodsType)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessors = (domainId) => {
			return cashierRest.domainMethodProcessors(domainId)
		}

		$rootScope.provide.cashierConfigProvider.changelogs = (domainName,  entityId, page) => {
			return cashierDmpRest.changelogs(domainName,  entityId, page)
		}

		$rootScope.provide.cashierConfigProvider.methodChangelogs = (domainName,  deposit, page) => {
			return cashierDmRest.changelogs(domainName,  deposit, page)
		}

		$rootScope.provide.cashierConfigProvider.mapAuthorNameToChangeLogs = (domainName,  list) => {
			return changelogService.mapAuthorNameToChangeLogs(domainName, list)
		}

		$rootScope.provide.cashierConfigProvider.cashierDmpRest = () => {
			return cashierDmpRest
		}

		$rootScope.provide.cashierConfigProvider.getAccessRules = (domainName) => {
			return accessRulesRest.findByDomainName(domainName)
		}

		$rootScope.provide.cashierConfigProvider.findProfiles = function(domainName) {
			return cashierRest.profiles(domainName)
		}

		$rootScope.provide.cashierConfigProvider.searchUsers = function(domainName, userGuid) {
			return userRest.search(domainName, userGuid)
		};

		$rootScope.provide.cashierConfigProvider.getCurrencyMethod = (domainName) => {
			return domainRest.findByName(domainName)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodUpdateMultiple = (domainMethods) => {
			return cashierRest.domainMethodUpdateMultiple(domainMethods)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorAccounting = (id) => {
			return cashierRest.domainMethodProcessorAccounting(id)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodUser = (id, guid) => {
			return cashierRest.domainMethodUser(id, guid)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorsByUserNoImage = (guid) => {
			return cashierRest.domainMethodProcessorsByUserNoImage(guid)
		}

		$rootScope.provide.cashierConfigProvider.frontendMethods = (type, guid) => {
			return cashierRest.frontendMethods(type, guid, '', '')
		}

		$rootScope.provide.cashierConfigProvider.frontendProcessors = (domainMethodId, guid) => {
			return cashierRest.frontendProcessors(domainMethodId, guid, '', '')
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProfile = (methodId, profileId) => {
			return cashierRest.domainMethodProfile(methodId, profileId)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorsByProfileNoImage = (profile) => {
			return cashierRest.domainMethodProcessorsByProfileNoImage(profile)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorUpdate = (processor) => {
			return cashierRest.domainMethodProcessorUpdate(processor)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodAccounting = (processor) => {
			return cashierRest.domainMethodAccounting(processor)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorAccounting = (processorId) => {
			return cashierRest.domainMethodProcessorAccounting(processorId)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodAccountingUser = (methodId, username) => {
			return cashierRest.domainMethodAccountingUser(methodId, username)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorAccountingUser = (processor) => {
			return cashierRest.domainMethodProcessorAccountingUser(processor)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorUserDelete = (dmpu, type) => {
			return cashierRest.domainMethodProcessorUserDelete(dmpu, type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileDelete = (dmpp, type) => {
			return cashierRest.domainMethodProcessorProfileDelete(dmpp, type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorDelete = (processor, type) => {
			return cashierRest.domainMethodProcessorDelete(processor, type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodAdd = (domain, newMethod, type) => {
			return cashierRest.domainMethodAdd(domain, newMethod, type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodUpdate = (processor) => {
			return cashierRest.domainMethodUpdate(processor)
		}

		$rootScope.provide.cashierConfigProvider.methodCopy = (model) => {
			return cashierRest.copy(model)
		}

		$rootScope.provide.cashierConfigProvider.cashierMethods = () => {
			return cashierRest.methods()
		}

		$rootScope.provide.cashierConfigProvider.domainMethodDeleteFull = (method) => {
			return cashierRest.domainMethodDeleteFull(method)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProfileUpdate = (profile) => {
			return cashierRest.domainMethodProfileUpdate(profile)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodUserUpdate = (user) => {
			return cashierRest.domainMethodUserUpdate(user)
		}

		$rootScope.provide.cashierConfigProvider.processors = (id, type) => {
			return cashierRest.processors(id, type)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorAdd = (newProcessor) => {
			return cashierRest.domainMethodProcessorAdd(newProcessor)
		}

		$rootScope.provide.cashierConfigProvider.user = (guid) => {
			return cashierRest.user(guid)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodUserUpdateMultiple = (methods) => {
			return cashierRest.domainMethodUserUpdateMultiple(methods)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProfileUpdateMultiple = (methods) => {
			return cashierRest.domainMethodProfileUpdateMultiple(methods)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorUserUpdateMultiple = (processors) => {
			return cashierRest.domainMethodProcessorUserUpdateMultiple(processors)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorProfileUpdateMultiple = (processors) => {
			return cashierRest.domainMethodProcessorProfileUpdateMultiple(processors)
		}

		$rootScope.provide.cashierConfigProvider.domainMethodProcessorUpdateMultiple = (processors) => {
			return cashierRest.domainMethodProcessorUpdateMultiple(processors)
		}

		controller.experimentalFeatures =  $userService.isExperimentalFeatures()

		window.VuePluginRegistry.loadByPage("dashboard/cashier/config")
	}
]);
