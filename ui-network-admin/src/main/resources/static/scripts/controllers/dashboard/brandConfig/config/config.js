'use strict';


angular.module('lithium').controller('BrandConfigController', ["$userService", "$scope", "$stateParams", '$state', "$translate", 'rest-domain', '$rootScope',
    function ($userService, $scope, $stateParams, $state, $translate, restDomain, $rootScope) {
        let controller = this;

        controller.domain = null;
        controller.selectedDomain = null;
        controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.BRAND_CONFIG.TITLE'
        controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.BRAND_CONFIG.DESCRIPTION'

        controller.setTab = function (tab) {
            controller.tab = tab;
            $state.go(tab.name, {
                domainName: controller.selectedDomain,
                domain: controller.domain
            });
        }

        controller.tabs = [];
        controller.loadTabs = function() {
            let emAuth = $userService.playerDomainsWithAnyRole(["ADMIN", "ERROR_MESSAGES_VIEW"]);
            let lsAuth = $userService.playerDomainsWithAnyRole(["ADMIN", "BRAND_CONFIG_VIEW"]);
            let dtAuth = $userService.playerDomainsWithAnyRole(["ADMIN", "DOCUMENT_TYPES_VIEW"]);
            if (lsAuth.length > 0)
                controller.tabs.push({
                    name: "dashboard.brandConfig.limits",
                    title: "UI_NETWORK_ADMIN.BRANDCONFIG.TAB.LOSSLIMITS.TITLE",
                    roles: "BRAND_CONFIG_VIEW"
                })
            if (emAuth.length > 0)
                controller.tabs.push({
                    name: "dashboard.brandConfig.errormessages",
                    title: "UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TITLE",
                    roles: "ERROR_MESSAGES_VIEW"
                })
            if (dtAuth.length > 0)
                controller.tabs.push({
                    name: "dashboard.brandConfig.doctypes",
                    title: "UI_NETWORK_ADMIN.BRANDCONFIG.TAB.DOCUMENT_TYPES.TITLE",
                    roles: "DOCUMENT_TYPES_VIEW"
                })
        }
        controller.loadTabs();

        $rootScope.provide.pageHeaderProvider.getDomainsList = () => {
            return $userService.playerDomainsWithAnyRole(["ADMIN", "BRAND_CONFIG_VIEW", "ERROR_MESSAGES_VIEW", "DOCUMENT_TYPES_VIEW"]);
        }

        $rootScope.provide.pageHeaderProvider.domainSelect = ( item ) =>  {
            if ( item === null) {
                $rootScope.provide.pageHeaderProvider.clearSelectedDomain()
                return
            }

            controller.selectedDomain = item.name;

            restDomain.findByName(controller.selectedDomain).then(function (response) {
                controller.domain = response;

                if (angular.isUndefined(controller.tab)) {
                    controller.setTab(controller.tabs[0]);
                } else {
                    controller.setTab(controller.tab);
                }
            }).catch(function () {
                errors.catch('', false);
            });
        }

        $rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
            $scope.description = "";
            controller.selectedDomain = null;
        }

        $rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
            return controller.textTitle ? controller.textTitle : ''
        }

        $rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
            return controller.textDescr ? controller.textDescr : ''
        }

        window.VuePluginRegistry.loadByPage("page-header")

    }
]);
