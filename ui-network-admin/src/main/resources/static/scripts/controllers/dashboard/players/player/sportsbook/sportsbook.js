'use strict';

angular.module('lithium')
.controller('PlayerSportsbookController', ["user", "$scope", "$state", "rest-provider", 'notify',
    function(user, $scope, $state, restProvider, notify) {
        var controller = this;
        const betHistoryLink = 'dashboard.players.player.sportsbook.bethistory'
        controller.tabs = [
            { name: betHistoryLink, title: 'Sportsbook History', roles: "sportsbook_*" }
        ];

        controller.provider = function (tab) {
            if (tab.name === betHistoryLink) {
                restProvider.listForDomain(user.domain.name).then(function (response) {
                    let providerConfigured = false;
                    for (let i = 0; i < response.length; i++) {
                        if (response[i].url === 'service-casino-provider-sportsbook') {
                            providerConfigured = true;
                            if (response[i].enabled) {
                                $state.go(tab.name, {
                                    playerOffset: response[i].properties.find(property => property.name === 'playerOffset').value,
                                });
                            } else {
                                notify.error('UI_NETWORK_ADMIN.DOMAIN.PROVIDERS.ERRORS.NOT_ENABLED');
                            }
                        }
                    }
                    if (!providerConfigured) {
                        notify.error('UI_NETWORK_ADMIN.DOMAIN.PROVIDERS.ERRORS.NOT_CONFIGURED');
                    }
                });
            } else {
                $state.go(tab.name);
            }
        }

        controller.setTab = function(tab) {
            if (tab.tclass !== 'disabled') {
                controller.tab = tab;
                controller.provider(controller.tab);
            }
        }

        angular.forEach(controller.tabs, function(tab) {
            if ($state.includes(tab.name)) {
                controller.tab = tab;
                controller.provider(controller.tab);
            }
        });
    }
]);
