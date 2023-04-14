'use strict';

angular.module('lithium')
.controller('CasinoLobbiesDomainSelectController', ['$translate', '$scope', '$userService', '$stateParams', '$state', '$rootScope',
    function($translate, $scope, $userService, $stateParams, $state, $rootScope) {
        var controller = this;

        if ((!angular.isUndefined($stateParams.domainName)) && (angular.isUndefined($stateParams.lobbyId))) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.casino.lobbies.list', {
                domainName: $stateParams.domainName
            });
        } else if ((!angular.isUndefined($stateParams.domainName)) &&
                   (!angular.isUndefined($stateParams.lobbyId)) &&
                   (!angular.isUndefined($stateParams.lobbyRevisionId))) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.casino.lobbies.lobby', {
                domainName: $stateParams.domainName,
                lobbyId: $stateParams.lobbyId,
                lobbyRevisionId: $stateParams.lobbyRevisionId
            });
        }

        controller.selectedDomain = null
        controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.CASINO_LOBBIES.TITLE'
        controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.CASINO_LOBBIES.DESCRIPTION'

        $rootScope.provide.pageHeaderProvider.getDomainsList = () => {
            return $userService.playerDomainsWithAnyRole(['ADMIN', 'CASINO_LOBBIES_VIEW']);
        }

        $rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
            if ( item === null) {
                $rootScope.provide.pageHeaderProvider.clearSelectedDomain()
                return
            }

            controller.selectedDomain = item.name
            $state.go('dashboard.casino.lobbies.list', {
                domainName: item.name
            });
        }

        $rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
            controller.selectedDomain = null
            $scope.setDescription("");
            $state.go('dashboard.casino.lobbies');
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
