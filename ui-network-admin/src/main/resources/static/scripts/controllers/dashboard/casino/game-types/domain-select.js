'use strict';

angular.module('lithium')
.controller('GameTypesDomainSelectController', ['$translate', '$scope', '$userService', '$stateParams', '$state','$rootScope',
    function($translate, $scope, $userService, $stateParams, $state, $rootScope) {
        var controller = this;
        controller.selectedDomain = null
        controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.GAMES_TYPES.TITLE'
        controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.GAMES_TYPES.DESCRIPTION'

        if ((!angular.isUndefined($stateParams.domainName)) && (angular.isUndefined($stateParams.id))) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.casino.game-types.list', {
                // domainName: controller.selectedDomain
                domainName: $stateParams.domainName
            });
        } else {
            controller.selectedDomain = $stateParams.domainName;
            controller.id = $stateParams.id;
        }

        $rootScope.provide.pageHeaderProvider.getDomainsList = () => {
            return $userService.playerDomainsWithAnyRole(['ADMIN', 'GAME_TYPE_*']);
        }

        $rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
            if ( item === null) {
                $rootScope.provide.pageHeaderProvider.clearSelectedDomain()
                return
            }

            controller.selectedDomain = item.name;
            $state.go('dashboard.casino.game-types.list', {
                domainName: controller.selectedDomain
            });
        }

        $rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
            controller.selectedDomain = null;
            $scope.setDescription("");
            $state.go('dashboard.casino.game-types');
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
