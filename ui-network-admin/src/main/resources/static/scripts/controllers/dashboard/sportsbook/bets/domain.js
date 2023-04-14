'use strict';

angular.module('lithium')
.controller('SportsbookDomainController', ['$translate', '$scope', '$userService', '$stateParams', '$state', 'rest-provider', 'notify','$rootScope',
	function($translate, $scope, $userService, $stateParams, $state, restProvider, notify, $rootScope) {
		let controller = this;
		controller.viewable = false;
		controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.SPORTBOOK_BETS.TITLE'
		controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.SPORTBOOK_BETS.DESCRIPTION'
		controller.selectedDomain = null

		$rootScope.provide.pageHeaderProvider.getDomainsList = () => {
			return $userService.playerDomainsWithAnyRole(["ADMIN","ROLE_SPORTS_BET_HISTORY","ROLE_PLAYER_SPORTS_BET_HISTORY"]);
		}

		$rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
			if ( item === null) {
				$rootScope.provide.pageHeaderProvider.clearSelectedDomain()
				return
			}
			controller.selectedDomain = item.name;
			controller.viewable = false

			restProvider.listForDomain(item.name).then(function (response) {
				let providerConfigured = false;
				for (let i = 0; i < response.length; i++) {
					if (response[i].url === 'service-casino-provider-sportsbook') {
						providerConfigured = true;
						if(response[i].enabled){
							controller.viewable = true;
							controller.data = {
								domainName: item.name,
								playerOffset: response[i].properties.find(property => property.name === 'playerOffset').value
							}
						} else {
							notify.error('UI_NETWORK_ADMIN.DOMAIN.PROVIDERS.ERRORS.NOT_ENABLED');
						}
					}
				}
				if (!providerConfigured) {
					notify.error('UI_NETWORK_ADMIN.DOMAIN.PROVIDERS.ERRORS.NOT_CONFIGURED');
				}
			});
		}


		$rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
			controller.selectedDomain = null;
			$scope.description = '';
			controller.viewable = false;
		}

		$rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
			return controller.textTitle ? controller.textTitle : ''
		}

		$rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
			return controller.textDescr ? controller.textDescr : ''
		}

		window.VuePluginRegistry.loadByPage("page-header")
	}
]);
