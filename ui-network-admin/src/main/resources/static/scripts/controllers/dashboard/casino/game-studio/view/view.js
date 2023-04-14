'use strict'

angular.module('lithium').controller('GameStudioViewController', ['gameStudio', 'notify', 'errors', 'GameStudioRest',
    function(gameStudio, notify, errors, rest) {
        var controller = this;
        controller.gameStudio = gameStudio;

        controller.changelogs = {
            domainName: gameStudio.domain.name,
            entityId: gameStudio.id,
            restService: rest,
            reload: 0
        }
    }
]);