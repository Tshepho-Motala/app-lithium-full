'use strict';

angular.module('lithium')
.controller('PlayerIncentiveGamesBetHistoryController', ["user", "$scope", "$state",
    function(user, $scope, $state) {
        var controller = this;
        controller.data = {
            user: user,
            allowUserSearch: false
        };
    }
]);