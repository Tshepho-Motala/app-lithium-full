'use strict'

angular.module('lithium').controller('GameSuppliersViewController', ['gameSupplier', 'notify', 'errors', 'GameSuppliersRest',
    function(gameSupplier, notify, errors, rest) {
        var controller = this;
        controller.gameSupplier = gameSupplier;

        controller.changelogs = {
            domainName: gameSupplier.domain.name,
            entityId: gameSupplier.id,
            restService: rest,
            reload: 0
        }
    }
]);