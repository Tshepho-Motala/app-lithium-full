'use strict';

angular.module('lithium')
.controller('PlayerFinancialTransactionsController', ["user", "$scope", "$state",
    function(user, $scope, $state) {
        var controller = this;

        controller.tabs = [
            { name: "dashboard.players.player.financialtransactions.overview", title: "Overview" },
            { name: "dashboard.players.player.financialtransactions.cashiertransactions", title: "Cashier Transactions" },
            { name: "dashboard.players.player.financialtransactions.balancemovement", title: "Balance Movement" }

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
