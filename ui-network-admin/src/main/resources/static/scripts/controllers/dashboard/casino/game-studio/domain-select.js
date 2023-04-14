'use strict';

angular.module('lithium')
.controller('GameStudioDomainSelectController', ['$translate', '$scope', '$userService', '$stateParams', '$state',
    function($translate, $scope, $userService, $stateParams, $state) {
        var controller = this;
        controller.domains = $userService.playerDomainsWithAnyRole(['ADMIN', 'GAME_STUDIO_*']);

        controller.domainSelect = function(item) {
            controller.selectedDomain = item.name;
            $state.go('dashboard.casino.game-studio.list', {
                domainName: controller.selectedDomain
            });
        }
        controller.clearSelectedDomain = function() {
            controller.selectedDomain = null;
            $scope.setDescription("");
            $state.go('dashboard.casino.game-studio');
        }

        if ((!angular.isUndefined($stateParams.domainName)) && (angular.isUndefined($stateParams.lobbyId))) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.casino.game-studio.list', {
                domainName: controller.selectedDomain
            });
        } else if ((!angular.isUndefined($stateParams.domainName)) &&
                   (!angular.isUndefined($stateParams.id))) {
            controller.selectedDomain = $stateParams.domainName;
            $state.go('dashboard.casino.game-studio.view', {
                domainName: controller.selectedDomain,
                id: $stateParams.id
            });
        }
    }
]);
