'use strict';

angular.module('lithium')
.controller('PlayerSportsbookBetHistoryController', ["user", "$scope", "$state", "playerOffset",
    function(user, $scope, $state, playerOffset) {
        let controller = this;

        controller.data = {
            user: user,
            allowUserSearch: false,
            playerOffset: playerOffset
        };
    }
]);
