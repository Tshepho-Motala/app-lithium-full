'use strict';

angular.module('lithium')
.controller('PlayerIncentiveGamesController', ["user", "$scope", "$state",
    function(user, $scope, $state) {
        var controller = this;

        controller.tabs = [
            { name: "dashboard.players.player.incentivegames.bethistory", title: "Bet History", roles: "incentivegames_bets_*" }
        ];

        controller.setTab = function(tab) {
            if (tab.tclass !== 'disabled') {
                controller.tab = tab;
                $state.go(tab.name);
            }
        }

        angular.forEach(controller.tabs, function(tab) {
            if ($state.includes(tab.name)) controller.tab = tab;
        });
    }
]);