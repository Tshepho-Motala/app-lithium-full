'use strict';

angular.module('lithium').controller('MassPlayerUpdateController', ["$scope", "$stateParams", "$state", "$userService", "$rootScope",
    function($scope, $stateParams, $state, $userService, $rootScope) {

        var controller = this;

        controller.selectedDomain = null;
        controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.MASS_PLAYER_UPDATE.TITLE'
        controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.MASS_PLAYER_UPDATE.DESCRIPTION'

        $rootScope.provide.pageHeaderProvider.getDomainsList = () => {
            return $userService.playerDomainsWithAnyRole(["ADMIN", "MASS_PLAYER_UPDATE_VIEW"]);
        }
        $rootScope.provide.pageHeaderProvider.domainSelect = ( item ) =>  {
            if ( item === null) {
                $rootScope.provide.pageHeaderProvider.clearSelectedDomain()
                return
            }

            controller.selectedDomain = item.name;

            $state.go('dashboard.players.massplayerupdate.tool', {
                domainName: item.name
            });
        }

        $rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
            controller.selectedDomain = null;
            $state.go('dashboard.players.massplayerupdate');
        }

        $rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
            return controller.textTitle ? controller.textTitle : ''
        }

        $rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
            return controller.textDescr ? controller.textDescr : ''
        }

        window.VuePluginRegistry.loadByPage("page-header")
    }]);
