'use strict';

angular.module('lithium')
	.controller('providersList', ["domain", "$log", "$scope", "$state", "$stateParams", "$http", "rest-provider","$q", "$ocLazyLoad", "$uibModal", 'rest-domain', 'notify',
		function(domain, $log, $scope, $state, $stateParams, $http, providerRest, $q, $ocLazyLoad, $uibModal, domainRest, notify) {

		var controller = this;

		controller.domain = domain;

		controller.original = {};

		var linkOwnerPromises = [];
		$scope.domainName = $stateParams.domainName;
		
		controller.reload = function() {
			providerRest
			.listForDomain($stateParams.domainName)
			.then(function(providers) { 
				controller.original.providers = providers; 
				if (controller.tab) controller.setTab(controller.tab);
			});
			providerRest
			.listForDomainLink($stateParams.domainName)
			.then(function(providerLinks) {
				var i;
				for(i=0; i < providerLinks.length; ++i) {
					var providerLink = providerLinks[i];
					linkOwnerPromises.push(
						(function(providerLink){
							providerRest.findOwnerLink($stateParams.domainName, providerLink.provider.id).then(
								function(ownerLink) {
									providerLink.domain = ownerLink.domain;
								}
							)
						})(providerLink)
					);
				}
				$q.all(linkOwnerPromises).then(function() {
					controller.original.providerLinks = providerLinks;
					if (controller.tab) controller.setTab(controller.tab);
				});
			});
			if (!controller.tab) controller.tab = controller.tabs[0];
		}
		
		controller.tabs = [
			{ name: "dashboard.provider.accounting", title: "Accounting", providerType: "ACCOUNTING" , roles: "DOMAIN_VIEW" },
			{ name: "dashboard.provider.cashier", title: "Cashier", providerType: "CASHIER" , roles: "DOMAIN_VIEW" },
			{ name: "dashboard.provider.casino", title: "Casino", providerType: "CASINO", roles: "PLAYER_CASINO_HISTORY" },
			{ name: "dashboard.provider.reward", title: "Reward", providerType: "REWARD", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.register", title: "Games", providerType: "GAMES", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.auth", title: "Auth", providerType: "AUTH", roles: "GROUP_VIEW,GROUP_EDIT" },
			{ name: "dashboard.provider.user", title: "User", providerType: "USER", roles: "DOMAIN_DEFAULT_ROLES_LIST,DOMAIN_DEFAULT_ROLES_ENABLE,DOMAIN_DEFAULT_ROLES_ADD,DOMAIN_DEFAULT_ROLES_REMOVE" },
			{ name: "dashboard.provider.affiliate", title: "Affiliate", providerType: "AFFILIATE", roles: "DOMAIN_DEFAULT_ROLES_LIST,DOMAIN_DEFAULT_ROLES_ENABLE,DOMAIN_DEFAULT_ROLES_ADD,DOMAIN_DEFAULT_ROLES_REMOVE" },
			{ name: "dashboard.provider.access", title: "Access", providerType: "ACCESS", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.kyc", title: "Kyc", providerType: "KYC", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.cdn", title: "Cdn", providerType: "CDN", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.document", title: "Document", providerType: "DOCUMENT", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.pub_sub", title: "Pub Sub", providerType: "PUB-SUB", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.register", title: "Register", providerType: "REGISTER", roles: "DOMAIN_VIEW"},
			{ name: "dashboard.provider.threshold", title: "Threshold", providerType: "THRESHOLD", roles: "DOMAIN_VIEW"}
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			//$state.go(tab.name, {
			//	domainName: domain.name
			//});
			controller.providers = [];
			angular.forEach(controller.original.providers, function(provider) {
				if (provider.providerType.name == tab.providerType) this.push(provider);
			}, controller.providers);
			
			controller.providerLinks = [];
			angular.forEach(controller.original.providerLinks, function(link) {
				if (link.provider.providerType.name == tab.providerType) this.push(link);
			}, controller.providerLinks);
		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});
		
		controller.providerAddModal = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				templateUrl: 'scripts/controllers/dashboard/providers/add/add.html',
				controller: 'providerAdd',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domainName: function () {
						return $scope.domainName;
					},
					providerType: function () {
						return controller.tab.providerType;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/providers/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(providerOrLink) {
				controller.reload();
				controller.changelogs.reload += 1;
			});
		}
	
		controller.providerViewModal = function (providerOrLink) {
			$scope.providerOrLink = providerOrLink;
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				templateUrl: 'scripts/controllers/dashboard/providers/view/view.html',
				controller: 'providerView',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					providerOrLink: function() {
						return $scope.providerOrLink;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/providers/view/view.js'
							]
						})
					}
				}
			});
		}
		
		controller.providerEditModal = function (providerOrLink) {
			$scope.providerOrLink = providerOrLink;
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				templateUrl: 'scripts/controllers/dashboard/providers/edit/edit.html',
				controller: 'providerEdit',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					providerOrLink: function() {
						return $scope.providerOrLink;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/providers/edit/edit.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(providerOrLink) {
				controller.reload();
				controller.changelogs.reload += 1;
			})
		}

		controller.toggleBettingEnabled = function () {
			domainRest.toggleBettingEnabled(controller.domain.name).then(function(response) {
				if (response._status === 0) {
					var pResp = response.plain();
					controller.domain = pResp;
					var bettingEnabled = pResp.bettingEnabled;
					var type = (bettingEnabled) ? "enabled" : "disabled";
					notify.success('Successfully ' + type + " betting for " + controller.domain.displayName);

					// FIXME: Necessary because of the alert/warning added in for disabled betting. Figure out how to set the updated domain object then this can be removed.
					$state.reload();
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				errors.catch('', false)(error)
			});
		}

		controller.changelogs = {
			domainName: domain.name,
			entityId: domain.id,
			restService: providerRest,
			reload: 0
		}
	}
]);
