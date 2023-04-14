'use strict'

angular.module('lithium').controller('GameTypesViewController', ['gameType', 'rest-games', 'notify', 'errors', 'GameTypesRest', '$uibModal',
    function(gameType, gamesRest, notify, errors, rest, $uibModal) {
        var controller = this;
        controller.gameType = gameType;

        controller.changelogs = {
            domainName: gameType.domain.name,
            entityId: gameType.id,
            restService: rest,
            reload: 0
        }

        gamesRest.getDomainGamesByGameType(gameType.domain.name, gameType.id).then(games => {
            controller.games = games;
        });

        controller.viewGames = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/casino/game-types/games-list/games-list.html',
                controller: 'AssignedGamesListController',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    domainName: function() {
                        return controller.gameType.domain.name
                    },
                    gameTypeId: function() {
                        return controller.gameType.id;
                    },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/casino/game-types/games-list/games-list.js'
                            ]
                        })
                    }
                }
            });

            modalInstance.result.then(function(tag) {
                controller.tableLoad();
            });
        }

    }
]);