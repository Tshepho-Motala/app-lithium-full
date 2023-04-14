'use strict';

angular.module('lithium')
.controller('PlayerBonusesController', ["user", "$scope", "$state", 'providerSportsBook', 'notify','$translate',
    function(user, $scope, $state, providerSportsBook, notify, $translate) {
        var controller = this;

        controller.tabs = [
            { name: "dashboard.players.player.bonuses.cashbonuses", title: "Cash Bonuses", roles: "PLAYER_BONUSES_VIEW" },
            { name: "dashboard.players.player.bonuses.freebets", title: "Sports Free Bets", roles: "PLAYER_BONUSES_VIEW", tclass : providerSportsBook[0] != undefined && providerSportsBook[0].enabled ? 'enabled' : 'disabled'},
            { name: "dashboard.players.player.bonuses.freespins", title: "Free Spins", roles: "PLAYER_BONUSES_VIEW" },
        ];

        controller.setTab = function(tab) {
            if (tab.tclass !== 'disabled') {
                controller.tab = tab;
                $state.go(tab.name);
            } else {
                notify.error('UI_NETWORK_ADMIN.PLAYER.SPORTS_FREE_BETS.ERRORS.PROVIDER_ERROR');
            }
        }

        angular.forEach(controller.tabs, function(tab) {
            if ($state.includes(tab.name)) controller.tab = tab;
        });
    }
]);
