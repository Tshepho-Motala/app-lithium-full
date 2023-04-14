'use strict';

angular.module('lithium').controller('PlayerGuidRedirectController', ['user', '$state',
    function(user, $state) {
        var controller = this;
        controller.user = user;
        $state.go("dashboard.players.player", { domainName: user.domain.name, id: user.id });
    }
]);