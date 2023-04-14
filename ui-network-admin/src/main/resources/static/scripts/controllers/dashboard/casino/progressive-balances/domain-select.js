'use strict';

angular.module('lithium')
.controller('ProgressiveBalancesDomainSelectController', ['$translate', '$scope', '$userService', '$stateParams', '$state', "$rootScope",
    function($translate, $scope, $userService, $stateParams, $state, $rootScope) {
        var controller = this;
        controller.domains = $userService.playerDomainsWithAnyRole(['ADMIN', 'JACKPOT_BALANCE_VIEW']);
        controller.tabs = [
            { name: "dashboard.casino.progressive-balances.list", title: "UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.NAVIGATION_TABS.PROGRESSIVE_FEEDS", roles: "JACKPOT_BALANCE_VIEW" },
            { name: "dashboard.casino.progressive-balances.configuration", title: "UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.NAVIGATION_TABS.CONFIGURATION", roles: "JACKPOT_BALANCE_VIEW" }
        ];

        controller.setTab = function(tab) {
            controller.tab = tab;
            $state.go(tab.name, {
                domainName: controller.selectedDomain
            });
        }

        controller.domainSelect = function(item) {
            controller.selectedDomain = item.name;
            if (angular.isUndefined(controller.tab)) {
                controller.setTab(controller.tabs[0]);
            } else {
                controller.setTab(controller.tab);
            }
        }

        controller.clearSelectedDomain = function() {
            $scope.description = "";
            controller.selectedDomain = null;
        }

        if ($stateParams.domainName != null) controller.selectedDomain = $stateParams.domainName;

        angular.forEach(controller.tabs, function(tab) {
            if ($state.includes(tab.name)) controller.tab = tab;
        });

        $rootScope.provide.pageHeaderProvider['getDomains'] = () => {
            return $userService.playerDomainsWithAnyRole(["ADMIN", "JACKPOT_BALANCE_VIEW"])
        }

        window.VuePluginRegistry.loadByPage("dashboard/progressive-balances")
    }
]);
