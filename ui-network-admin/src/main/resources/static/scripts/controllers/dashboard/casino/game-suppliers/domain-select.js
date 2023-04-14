'use strict';

angular.module('lithium')
.controller('GameSuppliersDomainSelectController', ['$translate', '$scope', '$userService', '$stateParams', '$state', "$rootScope",
    function($translate, $scope, $userService, $stateParams, $state, $rootScope) {
        var controller = this;

        if ((!angular.isUndefined($stateParams.domainName)) && (angular.isUndefined($stateParams.lobbyId))) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.casino.game-suppliers.list', {
                domainName: $stateParams.domainName
            });
        } else if ((!angular.isUndefined($stateParams.domainName)) &&
                   (!angular.isUndefined($stateParams.id))) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.casino.game-suppliers.view', {
                domainName: $stateParams.domainName,
                id: $stateParams.id
            });
        }

        controller.selectedDomain = null
        controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.GAMES_SUPPLIERS.TITLE'
        controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.GAMES_SUPPLIERS.DESCRIPTION'

        $rootScope.provide.pageHeaderProvider.getDomainsList = () => {
            return $userService.playerDomainsWithAnyRole(['ADMIN', 'GAME_SUPPLIER_*']);
        }

        $rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
            if ( item === null) {
                $rootScope.provide.pageHeaderProvider.clearSelectedDomain()
                return
            }

            controller.selectedDomain = item.name;
            $state.go('dashboard.casino.game-suppliers.list', {
                domainName: controller.selectedDomain
            });
        }

        $rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
            controller.selectedDomain = null;
            $scope.setDescription("");
            $state.go('dashboard.casino.game-suppliers');
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
