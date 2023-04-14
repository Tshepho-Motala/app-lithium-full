'use strict';

angular.module('lithium')
    .controller('GamesController', ["$scope", "$stateParams", "$state", "$userService", "$rootScope",
    function($scope, $stateParams, $state, $userService, $rootScope) {
        let controller = this;

        if (!angular.isUndefined($stateParams.domainName)) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.games.list', {
                domainName: controller.selectedDomain
            });
        }

        controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.CASINO_GAMES.TITLE'
        controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.CASINO_GAMES.DESCRIPTION'
        controller.selectedDomain = null

        $rootScope.provide.pageHeaderProvider.getDomainsList = () => {
            return $userService.domainsWithAnyRole(["ADMIN", "GAME_LIST"])
        }

        $rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
            if ( item === null) {
                $rootScope.provide.pageHeaderProvider.clearSelectedDomain()
                return
            }

            controller.selectedDomain = item.name;
            $state.go('dashboard.games.list', {
                domainName: controller.selectedDomain
            });
        }
        $rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
            controller.selectedDomain = null;
            $state.go('dashboard.games');
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